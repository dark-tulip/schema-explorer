package ru.anvera.services.connectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anvera.models.enums.DataSourceType;
import ru.anvera.services.callers.KafkaHttpClientCaller;
import ru.anvera.services.connectors.generators.MongoSinkConnectorConfigGenerator;
import ru.anvera.services.connectors.generators.MongoSourceConnectorConfigGenerator;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MongodbRegistrationService implements RegistrationService {

  private final KafkaHttpClientCaller             kafkaHttpClientCaller;
  private final MongoSinkConnectorConfigGenerator   mongoSinkConnectorConfigGenerator;
  private final MongoSourceConnectorConfigGenerator mongoSourceConnectorConfigGenerator;

  @Override
  @Transactional
  public void register(Long tableMappingId, DataSourceType dataSourceType) {
    String jsonPayload;
    if (dataSourceType.equals(DataSourceType.SINK)) {
      jsonPayload = mongoSinkConnectorConfigGenerator.generateMongoSinkConnectorConfig(tableMappingId).toString();
    } else {
      jsonPayload = mongoSourceConnectorConfigGenerator.generateMongoSourceConnectorConfig(tableMappingId).toString();
    }

    try {
      kafkaHttpClientCaller.callRegisterNewConnector(jsonPayload);
    } catch (IOException e) {
      throw new IllegalArgumentException("cannot register connector for postgres: " + e.getMessage());
    }
  }
}
