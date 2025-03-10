package ru.anvera.services.connectors;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.anvera.models.enums.DataSourceType;
import ru.anvera.services.callers.KafkaHttpClientCaller;
import ru.anvera.services.connectors.generators.PostgresSinkConnectorConfigGenerator;
import ru.anvera.services.connectors.generators.PostgresSourceConnectorConfigGenerator;

import java.io.IOException;


@RequiredArgsConstructor
@Service
public class PostgresRegistrationService implements RegistrationService {

  private final PostgresSourceConnectorConfigGenerator postgresSourceConnectorConfigGenerator;
  private final PostgresSinkConnectorConfigGenerator   postgresSinkConnectorConfigGenerator;
  private final KafkaHttpClientCaller                  kafkaHttpClientCaller;

  public void register(Long tableMappingId, DataSourceType connectorType) {
    String jsonPayload;
    if (connectorType.equals(DataSourceType.SINK)) {
      jsonPayload = postgresSinkConnectorConfigGenerator.generateSinkConnectorConfig(tableMappingId).toString();
    } else {
      jsonPayload = postgresSourceConnectorConfigGenerator.generateSourceConnectorConfig(tableMappingId).toString();
    }

    try {
      kafkaHttpClientCaller.callRegisterNewConnector(jsonPayload);
    } catch (IOException e) {
      throw new IllegalArgumentException("cannot register connector for postgres: " + e.getMessage());
    }
  }
}
