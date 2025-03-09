package ru.anvera.services;

import com.fasterxml.jackson.core.type.TypeReference;
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
public class GenericProcessor {

    private final Properties properties;
    private String columnName;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public GenericProcessor() {
        properties = new KafkaStreamsConfig().kafkaStreamsProperties();
    }

    public void SetColumnName(String columnName) {
        this.columnName = columnName;
    }

    private void sendToFlask(String jsonData) {
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

            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                // Чтение ответа
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    // Ответ от Flask в виде строки
                    String responseBody = response.toString();
                    log.info("Response from Flask: {}", responseBody);

                    // Парсим JSON-ответ
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode responseJson = objectMapper.readTree(responseBody);

                    // Извлекаем нужные данные (например, score)
                    double score = responseJson.get("score").asDouble();
                    log.info("Received score: {}", score);

                    // Здесь можно обработать строку ответа, например:
                    processFlaskResponse(responseJson);
                }
            } else {
                log.error("Flask server returned an error code: {}", responseCode);
            }
        } catch (Exception e) {
            log.error("Error sending anomaly to Flask", e);
        }
    }

    private void processFlaskResponse(JsonNode responseJson) {
        String message = responseJson.get("message").asText();
        log.info("Flask message: {}", message);
    }

    public void ProcessStream(HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> data) {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> inputStream = builder.stream("source_db.public.books");
        inputStream.foreach((key, value) -> log.info("Received value: {}", value));

        String type = data.get("public").get("books")
                .stream()
                .filter(i -> this.columnName.equals(i.getName()))
                .findFirst()
                .map(ColumnMetadataResponse::getType)
                .orElse(null);

        log.info("Kafka Streams started");

        KTable<Windowed<String>, List<Double>> statsTable = inputStream
                .selectKey((key, value) -> "1")
                .groupByKey()
                .windowedBy(TimeWindows.of(Duration.ofSeconds(20L)))
                .aggregate(
                        ArrayList::new,
                        (key, value, aggregate) -> {
                            try {
                                JsonNode newValue = OBJECT_MAPPER.readTree(value).get("payload").get(this.columnName);
                                double numericValue = (type.equals("INTEGER")) ? newValue.asInt() : newValue.asDouble();
                                aggregate.add(numericValue);

                                Map<String, Object> dataToSend = Map.of(
                                        "value", numericValue,
                                        "timestamp", System.currentTimeMillis()
                                );

                                String jsonData = OBJECT_MAPPER.writeValueAsString(dataToSend);

                                sendToFlask(jsonData);

                                return aggregate;
                            } catch (Exception e) {
                                log.warn("Failed to parse value map during aggregation: {}", value, e);
                                return aggregate;
                            }
                        },
                        Materialized.with(Serdes.String(), new JsonSerde<>(new TypeReference<List<Double>>() {
                        }))
                );

        statsTable.toStream()
                .map((k, v) -> KeyValue.pair(k.key(), v))
                .flatMapValues((readOnlyKey, values) -> {
                    if (values.isEmpty()) return Collections.emptyList();

                    List<Double> sortedValues = new ArrayList<>(values);
                    Collections.sort(sortedValues);
                    double median = sortedValues.get(sortedValues.size() / 2);
                    double threshold = 10.0; // фиксированный порог
                    double lower = median - threshold;
                    double upper = median + threshold;

                    // Найдем все аномальные значения
                    List<Map<String, Object>> anomalies = new ArrayList<>();
                    for (double value : values) {
                        boolean isAnomalous = value < lower || value > upper;
                        if (isAnomalous) {
                            Map<String, Object> result = Map.of(
                                    "median", median,
                                    "lower", lower,
                                    "upper", upper,
                                    "value", value,
                                    "isAnomalous", true
                            );
                            anomalies.add(result);
                        }
                    }

                    return anomalies.stream()
                            .map(anomaly -> {
                                try {
                                    return OBJECT_MAPPER.writeValueAsString(anomaly);
                                } catch (IOException e) {
                                    log.error("Error serializing JSON", e);
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                })
                .to("anomalies-topic", Produced.with(Serdes.String(), Serdes.String()));


        KafkaStreams streams = new KafkaStreams(builder.build(), properties);
        streams.start();
    }
}
