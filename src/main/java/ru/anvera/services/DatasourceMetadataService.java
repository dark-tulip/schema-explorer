package ru.anvera.services;

import com.mongodb.client.MongoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.anvera.factory.DynamicJdbcTemplateFactory;
import ru.anvera.factory.DynamicMongoClientFactory;
import ru.anvera.models.entity.DatasourceConnection;
import ru.anvera.models.enums.DbType;
import ru.anvera.models.request.ValidateConnectionAndGetInfoRequest;
import ru.anvera.models.response.ColumnMetadataResponse;
import ru.anvera.models.response.DatasourceMetadataInfoResponse;
import ru.anvera.repos.DatasourceConnectionRepository;
import ru.anvera.repos.MongoDatabaseMetadataRepository;
import ru.anvera.repos.PostgresDatabaseMetadataRepository;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class DatasourceMetadataService {

  private final DynamicJdbcTemplateFactory     dynamicJdbcTemplateFactory;
  private final DynamicMongoClientFactory      dynamicMongoClientFactory;
  private final DatasourceConnectionRepository datasourceConnectionRepository;

  public DatasourceMetadataInfoResponse validateConnectionAndGetInfo(ValidateConnectionAndGetInfoRequest request) {
    if (request.getDbType().equals(DbType.MONGODB.name())) {
      // Create a new MongoClient dynamically
      try (MongoClient mongoClient = dynamicMongoClientFactory.createMongoClient(
              request.getUrl(),
              request.getUsername(),
              request.getPassword())
      ) {

        // Create new repo instance with the client
        MongoDatabaseMetadataRepository mongoDatabaseMetadataRepository = new MongoDatabaseMetadataRepository(mongoClient);
        HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> databaseAndCollections = new HashMap<>();
        for (String databaseName : mongoDatabaseMetadataRepository.getDatabases()) {
          if (skipInternalDatabases(databaseName)) {
            continue;
          }

          HashMap<String, List<ColumnMetadataResponse>> collections = new HashMap<>();

          for (String collectionName : mongoDatabaseMetadataRepository.getCollectionNames(databaseName)) {
            List<ColumnMetadataResponse> columnsResponse = mongoDatabaseMetadataRepository
                .getCollectionMetadata(databaseName, collectionName)
                .stream()
                .map(column -> new ColumnMetadataResponse(
                    column.get("column_name").toString(),
                    column.get("data_type") != null ? column.get("data_type").toString() : "unknown",
                    column.get("is_nullable") != null ? column.get("is_nullable").toString() : "unknown"
                ))
                .collect(Collectors.toList());

            collections.put(collectionName, columnsResponse);
          }

          databaseAndCollections.put(databaseName, collections);
        }

        log.info("MongoDB connection validated for datasource: {}", new DatasourceMetadataInfoResponse(databaseAndCollections));

        return new DatasourceMetadataInfoResponse(databaseAndCollections);
      }
    }

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

  private boolean skipInternalDatabases(String databaseName) {
    return databaseName.equals("admin")
        || databaseName.equals("local")
        || databaseName.equals("config");
  }

}

