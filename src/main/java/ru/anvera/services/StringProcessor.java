package ru.anvera.services;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.data.relational.core.sql.In;
import ru.anvera.models.response.ColumnMetadataResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.anvera.config.KafkaStreamsConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import java.io.IOException;
import java.time.Duration;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StringProcessor extends StreamProcessor {

    private double sendToFlask(String jsonData) {
        try {
            URL url = new URL("http://127.0.0.1:5000/train_model");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            log.info("Flask Response Code: {}", responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    String responseBody = response.toString();
                    log.info("Response from Flask: {}", responseBody);

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode responseJson = objectMapper.readTree(responseBody);

                    JsonNode scoreNode = responseJson.get("score");
                    double score = scoreNode.isArray() && scoreNode.size() > 0 ? scoreNode.get(0).asDouble() : 0.0;
                    log.info("Received score: {}", score);

                    processFlaskResponse(responseJson);
                    return score;
                }
            } else {
                log.error("Flask server returned an error code: {}", responseCode);
                return 0.0;
            }
        } catch (Exception e) {
            log.error("Error sending anomaly to Flask", e);
            return 0.0;
        }
    }

    private void processFlaskResponse(JsonNode responseJson) {
        String message = responseJson.get("message").asText();
        log.info("Flask message: {}", message);
    }

    @Override
    public void ProcessStream(HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> data) {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> inputStream = builder.stream("source_db.public.books");
        inputStream.foreach((key, value) -> log.info("Received value: {}", value));

        String type = data.get("public").get("books")
                .stream()
                .filter(i -> this.getColumnName().equals(i.getName()))
                .findFirst()
                .map(ColumnMetadataResponse::getType)
                .orElse(null);

        log.info("Kafka Streams started");

        KTable<Windowed<String>, Map<String, Double>> statsTable = inputStream
                .selectKey((key, value) -> "1")
                .groupByKey()
                .windowedBy(TimeWindows.of(Duration.ofSeconds(20L)))
                .aggregate(
                        ()-> new HashMap<>(),
                        (key, value, aggregate) -> {
                            try {
                                JsonNode newValue = this.getOBJECT_MAPPER().readTree(value).get("payload").get(this.getColumnName());
                                String stringValue = newValue.asText();

                                Map<String, Object> dataToSend = Map.of(
                                        "value", stringValue,
                                        "timestamp", System.currentTimeMillis()
                                );

                                String jsonData = getOBJECT_MAPPER().writeValueAsString(dataToSend);

                                double score = sendToFlask(jsonData);
                                aggregate.put(stringValue, score);

                                return aggregate;
                            } catch (Exception e) {
                                log.warn("Failed to parse value map during aggregation: {}", value, e);
                                return aggregate;
                            }
                        },
                        Materialized.<String, Map<String, Double>, WindowStore<Bytes, byte[]>>as("stats-table-store")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new JsonSerde<>(new TypeReference<>() {}))
                );

        statsTable.toStream()
                .map((k, v) -> KeyValue.pair(k.key(), v))
                .flatMapValues((readOnlyKey, values) -> {
                    if (values.isEmpty()) return Collections.emptyList();

                    List<Map<String, Object>> anomalies = new ArrayList<>();

                    return anomalies.stream()
                            .map(anomaly -> {
                                try {
                                    return getOBJECT_MAPPER().writeValueAsString(anomaly);
                                } catch (IOException e) {
                                    log.error("Error serializing JSON", e);
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                })
                .to("anomalies-topic", Produced.with(Serdes.String(), Serdes.String()));


        KafkaStreams streams = new KafkaStreams(builder.build(), getProperties());
        streams.start();
    }

}
