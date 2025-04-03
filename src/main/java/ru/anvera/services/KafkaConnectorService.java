package ru.anvera.services;


import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.anvera.services.callers.KafkaHttpClientCaller;

import static ru.anvera.configs.KafkaBrocerConfigs.KAFKA_CONNECT_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConnectorService {

  private final KafkaHttpClientCaller kafkaHttpClientCaller;

  public ResponseEntity<String> update(String connectorName, JsonObject newConfig) {
    try {
      // Delete existing connector if exists
      if (kafkaHttpClientCaller.connectorExists(connectorName)) {
        kafkaHttpClientCaller.delete(KAFKA_CONNECT_URL + "/" + connectorName);
      }

      // Build final connector registration payload
      JsonObject payload = new JsonObject();
      payload.addProperty("name", connectorName);
      payload.add("config", newConfig);

      kafkaHttpClientCaller.callRegisterNewConnector(payload.toString());

      return ResponseEntity.ok("Connector '" + connectorName + "' created/updated successfully.");

    } catch (Exception e) {
      log.error("Failed to update connector '{}'", connectorName, e);
      return ResponseEntity.internalServerError()
                           .body("Failed to update connector '" + connectorName + "': " + e.getMessage());
    }
  }

  public ResponseEntity<String> delete(String connectorName) {
    try {
      if (kafkaHttpClientCaller.connectorExists(connectorName)) {
        kafkaHttpClientCaller.delete(KAFKA_CONNECT_URL + "/" + connectorName);
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return ResponseEntity.ok("Connector '" + connectorName + "' deleted successfully.");
  }
}
