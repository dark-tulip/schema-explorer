package ru.anvera.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.anvera.models.enums.DataSourceType;
import ru.anvera.models.enums.DbType;
import ru.anvera.services.connectors.ClickHouseRegistrationService;
import ru.anvera.services.connectors.MongodbRegistrationService;
import ru.anvera.services.connectors.PostgresRegistrationService;


@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectorRegistrationService {

  private final ClickHouseRegistrationService clickHouseRegistrationService;
  private final PostgresRegistrationService   postgresRegistrationService;
  private final MongodbRegistrationService    mongodbRegistrationService;

  public void registerConnector(Long tableMappingId, DataSourceType dataSourceType, String dbTypeString) {
    DbType dbType = DbType.valueOf(dbTypeString.toUpperCase());

    if (DbType.POSTGRESQL.equals(dbType)) {
      postgresRegistrationService.register(tableMappingId, dataSourceType);
    }

    if (DbType.CLICKHOUSE.equals(dbType)) {
      clickHouseRegistrationService.register(tableMappingId, dataSourceType);
    }

    if (DbType.MONGODB.equals(dbType)) {
      mongodbRegistrationService.register(tableMappingId, dataSourceType);
    }

  }

}
