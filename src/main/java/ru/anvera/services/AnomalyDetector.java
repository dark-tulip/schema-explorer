package ru.anvera.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.stereotype.Service;
import ru.anvera.config.KafkaStreamsConfig;
import ru.anvera.models.response.ColumnMetadataResponse;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Класс для обнаружения аномалий в данных, используя статистику из потока
 */
@Slf4j
@Service
public class AnomalyDetector {

    private static final String ANOMALY_DETECTED_TOPIC = "books.stats.anomalies";
    private static final String INPUT_TOPIC = "source_db.public.books";
    private static final String MEAN_TOPIC = "source_db.public.books.stats";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final Properties properties;
    private KStream<String, String> inputStream;
    private KStream<String, String> meanStatsStream;
    private double currentMean = 0.0;
    private double currentStdDev = 0.0;
    private static final double THRESHOLD_SIGMA = 2.0;
    private String columnName;
    public AnomalyDetector() {
        properties = new KafkaStreamsConfig().kafkaStreamsProperties();
    }
    public void SetColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void ProcessStream(HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> data, KStream<String, String> inputStream, KStream<String, String> meanStatsStream) {
        StreamsBuilder          streamsBuilder   = new StreamsBuilder();
        this.inputStream = inputStream;
        this.meanStatsStream = meanStatsStream;
        String type = data.get("public").get("books").stream().filter(i -> this.columnName.equalsIgnoreCase(i.getName())).findFirst().map(ColumnMetadataResponse::getType).orElse(null);
        KStream<String, String> joinedStream = this.inputStream.join(
                this.meanStatsStream,
                (inputValue, statsValue) -> {
                    try{
                        JsonNode inputRoot = OBJECT_MAPPER.readTree(inputValue);
                        JsonNode statsRoot = OBJECT_MAPPER.readTree(statsValue);
                        double currentMean = statsRoot.get("mean").asDouble();
                        double currentStdDev = statsRoot.get("stdDev").asDouble();
                        System.out.println("Updated Stats: Mean = " + currentMean + ", StdDev = " + currentStdDev);
                        double deviation = Math.abs(((type.equalsIgnoreCase("INTEGER"))
                                ? inputRoot.get("payload").get(this.columnName).asInt()
                                : inputRoot.get("payload").get(this.columnName).asDouble())
                                - currentMean);

                        double threshold = THRESHOLD_SIGMA * currentStdDev;
                        Map<String, Object> result = Map.of(
                                "value", (type.equalsIgnoreCase("INTEGER")) ? inputRoot.get("payload").get(this.columnName).asInt() :
                                        inputRoot.get("payload").get(this.columnName).asDouble(),
                                "deviation", deviation,
                                "threshold", threshold,
                                "anomaly", ((deviation > threshold) ? "true" : "false")
                        );
                        return OBJECT_MAPPER.writeValueAsString(result);
                    } catch (Exception e) {
                        log.warn("Failed to process join data", e);
                        return null;
                    }
                },
                JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(5))
                ,
                StreamJoined.with(Serdes.String(), Serdes.String(), Serdes.String())
        );
        joinedStream
                .filter((key, value) -> value != null)
                .to(ANOMALY_DETECTED_TOPIC, Produced.with(Serdes.String(), Serdes.String()));
    }
}
