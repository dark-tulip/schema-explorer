package ru.anvera.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.anvera.models.enums.DataSourceType;
import ru.anvera.models.enums.DbType;
import ru.anvera.services.connectors.ClickHouseRegistrationService;
import ru.anvera.services.connectors.PostgresRegistrationService;


@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectorRegistrationService {

  private final ClickHouseRegistrationService clickHouseRegistrationService;
  private final PostgresRegistrationService   postgresRegistrationService;

  public void registerConnector(Long tableMappingId, DataSourceType dataSourceType, String dbTypeString) {
    DbType dbType = DbType.valueOf(dbTypeString);

    if (DbType.POSTGRES.equals(dbType)) {
      postgresRegistrationService.register(tableMappingId, dataSourceType);
    }

    if (DbType.CLICKHOUSE.equals(dbType)) {
      clickHouseRegistrationService.register(tableMappingId, dataSourceType);
    }

  }

}
