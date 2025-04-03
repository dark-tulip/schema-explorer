package ru.anvera.controllers;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.anvera.services.KafkaConnectorService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/connectors")
public class KafkaConnectorController {

  private final KafkaConnectorService kafkaConnectorService;

  @PostMapping("/update")
  public ResponseEntity<String> updateConnector(@RequestParam String connectorName,
                                                @RequestBody JsonObject newConfig) {
    return kafkaConnectorService.update(connectorName, newConfig);
  }

  @PostMapping("/delete")
  public ResponseEntity<String> deleteConnector(@RequestParam String connectorName) {
    return kafkaConnectorService.delete(connectorName);
  }

}
