package ru.anvera.services.connectors;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.anvera.models.enums.DataSourceType;
import ru.anvera.services.SinkConnectorConfigGenerator;
import ru.anvera.services.SourceConnectorConfigGenerator;
import ru.anvera.services.callers.KafkaHttpClientCaller;

import java.io.IOException;


@RequiredArgsConstructor
@Service
public class PostgresRegistrationService implements RegistrationService {

  private final SourceConnectorConfigGenerator sourceConnectorConfigGenerator;
  private final SinkConnectorConfigGenerator   sinkConnectorConfigGenerator;
  private final KafkaHttpClientCaller          kafkaHttpClientCaller;

  public void register(Long tableMappingId, DataSourceType connectorType) {
    String jsonPayload;
    if (connectorType.equals(DataSourceType.SINK)) {
      jsonPayload = sinkConnectorConfigGenerator.generateSinkConnectorConfig(tableMappingId).toString();
    } else {
      jsonPayload = sourceConnectorConfigGenerator.generateSourceConnectorConfig(tableMappingId).toString();
    }

    try {
      kafkaHttpClientCaller.callRegisterNewConnector(jsonPayload);
    } catch (IOException e) {
      throw new IllegalArgumentException("cannot register connector for postgres: " + e.getMessage());
    }
  }
}
