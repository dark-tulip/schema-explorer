package ru.anvera.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.stereotype.Service;
import ru.anvera.config.KafkaStreamsConfig;
import ru.anvera.models.response.ColumnMetadataResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Класс для расчета среднего значения и стандартного отклонения потоковых данных в скользящих временных окнах
 */
@Slf4j
@Service
public class ComputeMean {

    public static class AggregatedResult {
        private String originalKey;
        private double[] stats;

        public AggregatedResult() {
            this.stats = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
        }

        public AggregatedResult(String originalKey, double[] stats) {
            this.originalKey = originalKey;
            this.stats = stats;
        }

        public String getOriginalKey() {
            return originalKey;
        }

        public void setOriginalKey(String originalKey) {
            this.originalKey = originalKey;
        }

        public double[] getStats() {
            return stats;
        }

        public void setStats(double[] stats) {
            this.stats = stats;
        }
    }

    private static final String MEAN_TOPIC = "source_db.public.books.stats";
    private static final String INPUT_TOPIC = "source_db.public.books";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private String columnName;
    private final Properties properties;
    private KStream<String, String> inputStream;

    public ComputeMean() {
        this.properties = new KafkaStreamsConfig().kafkaStreamsProperties();
    }

    public void SetColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void ProcessStream(HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> data, KStream<String, String> inputStream) {
        StreamsBuilder streamsBuilder = new StreamsBuilder(); // это надо делать в контроллере, и так же для второго класса, а потом каждому передавать stream
        this.inputStream = inputStream;
        String type = data.get("public").get("books").stream().filter(i -> this.columnName.equalsIgnoreCase(i.getName())).findFirst().map(ColumnMetadataResponse::getType).orElse(null);
        this.inputStream
                .mapValues((key, value) -> Map.of("originalKey", key, "value", value))
                .selectKey((key, value) -> "key-to-group")
                .mapValues((key, value) -> {
                    try {
                        String originalValue = value.get("value");
                        Map<String, Object> root = OBJECT_MAPPER.readValue(originalValue, Map.class);
                        Map<String, Object> payload = (Map<String, Object>) root.get("payload");
                        if (payload != null && payload.containsKey(this.columnName)) {
                            return OBJECT_MAPPER.writeValueAsString(Map.of("originalKey", value.get("originalKey"), this.columnName, payload.get(this.columnName)));
                        } else {
                            log.warn(String.format("Missing or invalid %s field in payload: %s", this.columnName, originalValue));
                        }
                    } catch (Exception e) {
                        log.warn("Failed to parse message: {}", value, e);
                    }
                    return null;
                })
                .filter((key, value) -> value != null)
                .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
                .aggregate(
                        () -> new AggregatedResult(null, new double[]{0.0, 0.0, 0.0, 0.0, 0.0}),
                        (key, newValue, aggValue) -> {
                            try {
                                Map<String, Object> valueMap = OBJECT_MAPPER.readValue(newValue, Map.class);
                                String originalKey = (String) valueMap.get("originalKey");
                                double[] stats = aggValue.getStats();
                                System.out.println("Here: " + valueMap + " " + this.columnName + " type: " + type.equalsIgnoreCase("INTEGER"));
                                stats[0] += (type.equalsIgnoreCase("INTEGER")) ? (Integer) valueMap.get(this.columnName) :
                                        (Double) valueMap.get(this.columnName);
                                stats[1] += 1;
                                stats[2] += ((type.equalsIgnoreCase("INTEGER")) ? (Integer) valueMap.get(this.columnName) :
                                        (Double) valueMap.get(this.columnName)) * ((type.equalsIgnoreCase("INTEGER")) ? (Integer) valueMap.get(this.columnName) :
                                        (Double) valueMap.get(this.columnName));
                                stats[3] = stats[0] / stats[1];
                                stats[4] = Math.sqrt(stats[2] / stats[1] - stats[3] * stats[3]);
                                System.out.println(originalKey);
                                aggValue.setOriginalKey(originalKey);
                            } catch (Exception e) {
                                log.warn("Failed to parse value map during aggregation: {}", newValue, e);
                            }
                            return aggValue;
                        },
                        Materialized.with(Serdes.String(), Serdes.serdeFrom(
                                (topic, aggregatedResult) -> {
                                    try {
                                        return OBJECT_MAPPER.writeValueAsBytes(aggregatedResult);
                                    } catch (IOException e) {
                                        throw new RuntimeException("Serialization error", e);
                                    }
                                },
                                (topic, bytes) -> {
                                    try {
                                        return OBJECT_MAPPER.readValue(bytes, AggregatedResult.class);
                                    } catch (IOException e) {
                                        throw new RuntimeException("Deserialization error", e);
                                    }
                                }
                        ))
                )
                .toStream()
                .selectKey((key, value) -> value.getOriginalKey())
                .mapValues((readOnlyKey, aggValue) -> {
                    double sum = aggValue.getStats()[0];
                    double count = aggValue.getStats()[1];
                    double mean = aggValue.getStats()[3];
                    double stdDev = aggValue.getStats()[4];
                    Map<String, Object> result = Map.of(
                            "mean", mean,
                            "sum", sum,
                            "count", count,
                            "stdDev", stdDev
                    );

                    try {
                        return OBJECT_MAPPER.writeValueAsString(result);
                    } catch (IOException e) {
                        log.error("Error serializing JSON", e);
                        return null;
                    }
                })
                .filter((key, value) -> value != null)
                .to(MEAN_TOPIC, Produced.with(Serdes.String(), Serdes.String()));
    }
}
