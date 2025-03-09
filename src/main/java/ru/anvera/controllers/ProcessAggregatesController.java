package ru.anvera.controllers;

import ru.anvera.models.response.ColumnMetadataResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.anvera.services.ProcessAggregates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/aggregates")
public class ProcessAggregatesController {

    private final ProcessAggregates processor;

    public ProcessAggregatesController(ProcessAggregates processor) {
        this.processor = processor;
    }

    @PostMapping("/set-col-name")
    public ResponseEntity<Map<String, String>> setColumnName(@RequestBody String columnName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("Column name received = " + columnName);
        HashMap<String, String> parsedData =
                mapper.readValue(columnName, new TypeReference<>() {
                });
        System.out.println("Column name is " + parsedData.get("columnName"));
        processor.SetColumnName(parsedData.get("columnName"));
        Map<String, String> response = Map.of("message", "Данные успешно отправлены!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/set-agg-func")
    public ResponseEntity<Map<String, String>> setAggregatingFunction(@RequestBody String funcName)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("Aggregating function name is " + funcName);
        Map<String, String> parsedData =
                objectMapper.readValue(funcName, new TypeReference<>() {
                });
        processor.SetFunctionName(parsedData.get("funcName"));
        Map<String, String> response = Map.of("message", "Данные успешно отправлены!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resource")
    public ResponseEntity<Map<String, String>> createResource(@RequestBody String data) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> parsedData =
                mapper.readValue(data, new TypeReference<>() {
                });
        processor.ProcessStream(parsedData);
        Map<String, String> response = Map.of("message", "Обработка успешно запущена!");
        return ResponseEntity.ok(response);
    }

}



