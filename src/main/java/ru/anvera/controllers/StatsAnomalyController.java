package ru.anvera.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.anvera.config.KafkaStreamsConfig;
import ru.anvera.models.response.ColumnMetadataResponse;
import ru.anvera.services.AnomalyDetector;
import ru.anvera.services.ComputeMean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mean")
public class StatsAnomalyController {
    private static final String INPUT_TOPIC = "source_db.public.books";
    private static final String MEAN_TOPIC = "source_db.public.books.stats";
    StreamsBuilder streamsBuilder   = new StreamsBuilder();


    private final ComputeMean computeMean;
    private final AnomalyDetector anomalyDetector;
    public StatsAnomalyController(ComputeMean computeMean, AnomalyDetector anomalyDetector) {
        this.anomalyDetector = anomalyDetector;
        this.computeMean = computeMean;
    }

    @PostMapping("/set-col-name")
    public ResponseEntity<Map<String, String>> setColumnName(@RequestBody String columnName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("Column name received = " + columnName);
        HashMap<String, String> parsedData =
                mapper.readValue(columnName, new TypeReference<>() {
                });
        anomalyDetector.SetColumnName(parsedData.get("columnName"));
        computeMean.SetColumnName(parsedData.get("columnName"));
        Map<String, String> response = Map.of("message", "Данные успешно отправлены!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resource")
    public ResponseEntity<Map<String, String>> createResource(@RequestBody String data) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> parsedData =
                mapper.readValue(data, new TypeReference<>() {
                });
        StreamsBuilder          streamsBuilder   = new StreamsBuilder();
        KStream<String, String> changeJsonStream1 = streamsBuilder.stream(INPUT_TOPIC);
        KStream<String, String> changeJsonStream2 = streamsBuilder.stream(MEAN_TOPIC);
        computeMean.ProcessStream(parsedData, changeJsonStream1);
        anomalyDetector.ProcessStream(parsedData, changeJsonStream1, changeJsonStream2);
        final Topology appTopology = streamsBuilder.build();

        KafkaStreams streams = new KafkaStreams(appTopology, new KafkaStreamsConfig().kafkaStreamsProperties());
        streams.start();
        Map<String, String> response = Map.of("message", "Обработка успешно запущена!");
        return ResponseEntity.ok(response);
    }
}
