package ru.anvera.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.anvera.factory.DynamicJdbcTemplateFactory;
import ru.anvera.models.entity.DatasourceConnection;
import ru.anvera.models.request.ValidateConnectionAndGetInfoRequest;
import ru.anvera.models.response.ColumnMetadataResponse;
import ru.anvera.models.response.DatasourceMetadataInfoResponse;
import ru.anvera.repos.DatasourceConnectionRepository;
import ru.anvera.repos.PostgresDatabaseMetadataRepository;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class DatasourceMetadataService {

  private final DynamicJdbcTemplateFactory     dynamicJdbcTemplateFactory;
  private final DatasourceConnectionRepository datasourceConnectionRepository;

  public DatasourceMetadataInfoResponse validateConnectionAndGetInfo(ValidateConnectionAndGetInfoRequest request) {
    // create new template
    JdbcTemplate jdbcTemplate = dynamicJdbcTemplateFactory
        .createJdbcTemplate(
            request.getDbType(),
            request.getUrl(),
            request.getUsername(),
            request.getPassword()
        );

    // create new repo for this data source
    PostgresDatabaseMetadataRepository postgresDatabaseMetadataRepository = new PostgresDatabaseMetadataRepository(jdbcTemplate);

    HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> schemaNameAndTables = new HashMap<>();

    for (String schemaName : postgresDatabaseMetadataRepository.getSchemas()) {

      if (skipInternalSchema(schemaName)) {
        continue;
      }

      HashMap<String, List<ColumnMetadataResponse>> tables = new HashMap<>();

      for (String tableName : postgresDatabaseMetadataRepository.getTableNames(schemaName)) {
        List<ColumnMetadataResponse> columnsResponse = postgresDatabaseMetadataRepository
            .getColumnMetadataBySchemaNameAndTableName(schemaName, tableName)
            .stream()
            .map(column -> new ColumnMetadataResponse(
                (String) column.get("column_name"),
                (String) column.get("data_type"),
                (String) column.get("is_nullable")
            ))
            .collect(Collectors.toList());

        tables.put(tableName, columnsResponse);
      }

      schemaNameAndTables.put(schemaName, tables);
    }

    log.info("63GAN469 :: connection validated for datasource: {}", new DatasourceMetadataInfoResponse(schemaNameAndTables));

    return new DatasourceMetadataInfoResponse(schemaNameAndTables);
  }


  public DatasourceMetadataInfoResponse getInfo(Long connectionId, Long projectId) {
    DatasourceConnection connection = datasourceConnectionRepository.getById(connectionId, projectId);

    return validateConnectionAndGetInfo(new ValidateConnectionAndGetInfoRequest(
        connection.getDbType(),
        connection.getUrl(),
        connection.getUsername(),
        connection.getPassword()
    ));
  }

  private boolean skipInternalSchema(String schemaName) {
    return schemaName.equals("information_schema")
        || schemaName.equals("pg_toast")
        || schemaName.equals("INFORMATION_SCHEMA")
        || schemaName.equals("default")
        || schemaName.equals("system")
        || schemaName.equals("pg_catalog");
  }
}
