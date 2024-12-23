package ru.anvera.services;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.anvera.factory.JdbcTemplateFactory;
import ru.anvera.models.request.SchemaMetadataInfoRequest;
import ru.anvera.models.response.ColumnMetadataResponse;
import ru.anvera.models.response.SchemaMetadataInfoResponse;
import ru.anvera.repos.PostgresDatabaseMetadataRepository;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SchemaMetadataService {

  private final JdbcTemplateFactory jdbcTemplateFactory;

  public SchemaMetadataInfoResponse getInfo(SchemaMetadataInfoRequest request) {
    // create new template
    JdbcTemplate jdbcTemplate = jdbcTemplateFactory
        .createJdbcTemplate(request.getDbType(),
            request.getUrl(),
            request.getUsername(),
            request.getPassword()
        );

    // create new repo for this data source
    PostgresDatabaseMetadataRepository postgresDatabaseMetadataRepository = new PostgresDatabaseMetadataRepository(jdbcTemplate);

    HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> schemaNameAndTables = new HashMap<>();

    for (String schemaName : postgresDatabaseMetadataRepository.getSchemas()) {
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

    return new SchemaMetadataInfoResponse(schemaNameAndTables);
  }
}
