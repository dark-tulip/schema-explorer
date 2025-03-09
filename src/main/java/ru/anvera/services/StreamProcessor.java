package ru.anvera.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import ru.anvera.config.KafkaStreamsConfig;
import ru.anvera.models.response.ColumnMetadataResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public abstract class StreamProcessor {
    @Getter
    private final Properties properties;
    @Getter
    private String columnName;
    @Getter
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public StreamProcessor() {
        this.properties = new KafkaStreamsConfig().kafkaStreamsProperties();
    }

    public void SetColumnName(String columnName) {
        this.columnName = columnName;
    }
    public abstract void ProcessStream(HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> data);
}
