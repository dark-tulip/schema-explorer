package ru.anvera.services;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.stereotype.Service;
import ru.anvera.models.response.ColumnMetadataResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

enum AggregationFunction {
    MIN {
        @Override
        public Integer apply(List<String> values) {
            if (values == null || values.isEmpty()) return 0;
            return values.stream()
                    .mapToInt(Integer::parseInt)
                    .min()
                    .orElse(0);
        }
    },
    MAX {
        @Override
        public Integer apply(List<String> values) {
            if (values == null || values.isEmpty()) return 0;
            return values.stream()
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0);
        }
    },
    SUM {
        @Override
        public Integer apply(List<String> values) {
            if (values == null || values.isEmpty()) return 0;
            return values.stream()
                    .mapToInt(Integer::parseInt)
                    .sum();
        }
    },
    AVG {
        @Override
        public Double apply(List<String> values) {
            if (values == null || values.isEmpty()) return 0.0;
            return values.stream()
                    .mapToInt(Integer::parseInt)
                    .average()
                    .orElse(0.0);
        }
    };

    public abstract Number apply(List<String> values);
}

@Slf4j
@Service
public class ProcessAggregates extends StreamProcessor {
    private String aggregatingFunctionName;
    public void SetFunctionName(String aggregatingFunctionName) {
        this.aggregatingFunctionName = aggregatingFunctionName;
    }
    @Override
    public void ProcessStream(HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> data) {

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> inputStream = builder.stream("source_db.public.books");
        log.info("Kafka Streams started");
        AggregationFunction function;
        switch (this.aggregatingFunctionName) {
            case "MIN":
                function = AggregationFunction.MIN;
                break;
            case "MAX":
                function = AggregationFunction.MAX;
                break;
            case "AVG":
                function = AggregationFunction.AVG;
                break;
            case "SUM":
                function = AggregationFunction.SUM;
                break;
            default:
                log.warn("Unknown aggregation function: {}. Using MAX as default.", this.aggregatingFunctionName);
                function = AggregationFunction.MAX;
                break;
        }
        KTable<String, List<String>> aggrTable = inputStream.selectKey((key, value) -> {
            try {
                JsonNode jsonNode = this.getOBJECT_MAPPER().readTree(value);
                return jsonNode.get("payload").get(this.getColumnName()).asText();
            } catch (Exception e) {
                log.warn("Failed to extract key from value: {}", value, e);
                return "unknown";
            }
        }).groupByKey().aggregate(
                ArrayList::new,
                (key, value, aggregate) -> {
                    aggregate.add(value);
                    return aggregate;
                }, Materialized.with(Serdes.String(), new Serdes.ListSerde<>())
        );
        KTable<String, String> resultTable = aggrTable.mapValues(values -> {
            Number result = function.apply(values);
            return result.toString();
        });
        resultTable.toStream().foreach((key, value) -> {
            log.info("Key: {}, Result of {}: {}", key, function.name(), value);
        });
        KafkaStreams streams = new KafkaStreams(builder.build(), getProperties());
        streams.start();
    }

}
