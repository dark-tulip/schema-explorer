package ru.anvera.services;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.anvera.factory.JdbcTemplateFactory;
import ru.anvera.models.request.SchemaMetadataInfoRequest;
import ru.anvera.models.response.ColumnResponse;
import ru.anvera.models.response.SchemaMetadataInfoResponse;
import ru.anvera.models.response.TableResponse;
import ru.anvera.repos.DatabaseMetadataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SchemaMetadataService {

  private final JdbcTemplateFactory jdbcTemplateFactory;

  public List<SchemaMetadataInfoResponse> getInfo(SchemaMetadataInfoRequest request) {
    List<SchemaMetadataInfoResponse> response = new ArrayList<>();


    // create new template
    JdbcTemplate jdbcTemplate = jdbcTemplateFactory
        .createJdbcTemplate(request.getDbType(),
            request.getUrl(),
            request.getUsername(),
            request.getPassword()
        );

    // create new repo for this data source
    DatabaseMetadataRepository databaseMetadataRepository = new DatabaseMetadataRepository(jdbcTemplate);

    List<String> schemaNames = databaseMetadataRepository.getSchemas();

    for (String schemaName : schemaNames) {
      List<TableResponse> tablesResponse = new ArrayList<>();

      List<String> tableNames = databaseMetadataRepository.getTableNames(schemaName);

      for (String tableName : tableNames) {
        List<ColumnResponse> columnsResponse = databaseMetadataRepository
            .getColumnMetadataBySchemaNameAndTableName(schemaName, tableName)
            .stream()
            .map(column -> new ColumnResponse(
                (String) column.get("column_name"),
                (String) column.get("data_type"),
                (String) column.get("is_nullable")
            ))
            .collect(Collectors.toList());

        tablesResponse.add(new TableResponse(tableName, columnsResponse));
      }
      response.add(new SchemaMetadataInfoResponse(schemaName, tablesResponse));

    }
    return response;
  }
}
