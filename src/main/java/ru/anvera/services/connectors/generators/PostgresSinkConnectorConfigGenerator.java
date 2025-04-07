package ru.anvera.services.connectors.generators;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.anvera.configs.SecurityContextUtils;
import ru.anvera.models.entity.DatasourceConnection;
import ru.anvera.models.entity.TableMapping;
import ru.anvera.models.enums.DbType;
import ru.anvera.repos.DatasourceConnectionRepository;
import ru.anvera.repos.TableMappingRepository;

import static ru.anvera.utils.ConnectorUtils.extractDbNameFromUrl;
import static ru.anvera.utils.ConnectorUtils.generateTopicName;

/**
 * Генерирует файл с пропертями для таблицы ПОЛУЧАТЕЛЯ (то, куда записываем данные из Kafka).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostgresSinkConnectorConfigGenerator {

  private final DatasourceConnectionRepository datasourceConnectionRepository;
  private final TableMappingRepository         tableMappingRepository;
  private final SecurityContextUtils           securityContextUtils;

  public JsonObject generateSinkConnectorConfig(Long tableMappingId) {
    Long projectId = securityContextUtils.getPrincipal().getProjectId();

    TableMapping tableMapping = tableMappingRepository.getById(tableMappingId);
    String       tableName    = tableMapping.getSinkTable();
    String       schemaName   = tableMapping.getSinkSchemaName();

    DatasourceConnection sinkDb = datasourceConnectionRepository.getById(
        tableMapping.getSinkDbConnectionId(),
        projectId
    );

    DatasourceConnection sourceDb = datasourceConnectionRepository.getById(
        tableMapping.getSourceDbConnectionId(),
        projectId
    );

    String dbType    = sinkDb.getDbType();
    String url       = sinkDb.getUrl();
    String username  = sinkDb.getUsername();
    String password  = sinkDb.getPassword();
    String topicName = generateTopicName(extractDbNameFromUrl(sourceDb.getUrl()), schemaName, tableName);

    if (sourceDb.getDbType().equalsIgnoreCase(DbType.MONGODB.getName())) {
      topicName = sourceDb.getDbType() + "." + schemaName + "." + tableName;

      return generateConnectorConfigV2FromSourceMongoToSinkPostgres(tableMapping.getSinkDbConnectionId(), dbType, url, username, password, tableName, topicName);
    }

    return generateConnectorConfig(tableMapping.getSinkDbConnectionId(), dbType, url, username, password, tableName, topicName);
  }

  private static JsonObject generateConnectorConfig(
      long id, String dbType, String url, String username, String password,
      String tableName, String topicName) {

    log.warn("81C5XOYS :: created sink connector to topic: " + topicName);

    JsonObject config = new JsonObject();
    config.addProperty("name", dbType + "-" + topicName + "-sink-connector-" + id);

    JsonObject configDetails = new JsonObject();
    configDetails.addProperty("connector.class", "io.debezium.connector.jdbc.JdbcSinkConnector");
    configDetails.addProperty("tasks.max", "1");
    configDetails.addProperty("topics", topicName);
    configDetails.addProperty("connection.url", url.replace("localhost", "local_postgres"));
    configDetails.addProperty("connection.username", username);
    configDetails.addProperty("connection.password", password);
    configDetails.addProperty("auto.create", "true");
    configDetails.addProperty("auto.evolve", "true");
    configDetails.addProperty("insert.mode", "insert");
    configDetails.addProperty("table.name.format", tableName);
    configDetails.addProperty("key.converter", "org.apache.kafka.connect.json.JsonConverter");
    configDetails.addProperty("value.converter", "org.apache.kafka.connect.json.JsonConverter");
    configDetails.addProperty("key.converter.schemas.enable", "true");
    configDetails.addProperty("value.converter.schemas.enable", "true");
    config.add("config", configDetails);

    return config;
  }

  private static JsonObject generateConnectorConfigV2FromSourceMongoToSinkPostgres(
      Long id, String dbType, String url, String username, String password,
      String tableName, String topicName) {

    JsonObject config = new JsonObject();
    config.addProperty("name", dbType + "-" + topicName + "-sink-connector-" + id);

    JsonObject configDetails = new JsonObject();
    configDetails.addProperty("connector.class", "io.debezium.connector.jdbc.JdbcSinkConnector");
    configDetails.addProperty("tasks.max", "1");
    configDetails.addProperty("topics", topicName);

    configDetails.addProperty("connection.url", url.replace("localhost", "local_postgres"));
    configDetails.addProperty("connection.username", username);
    configDetails.addProperty("connection.password", password);

    configDetails.addProperty("table.name.format", tableName);
    configDetails.addProperty("auto.create", "true");
    configDetails.addProperty("auto.evolve", "true");

    // Use upsert mode
    configDetails.addProperty("insert.mode", "upsert");
    configDetails.addProperty("pk.mode", "record_key");
    configDetails.addProperty("pk.fields", "id");

    configDetails.addProperty("key.converter", "org.apache.kafka.connect.json.JsonConverter");
    configDetails.addProperty("value.converter", "org.apache.kafka.connect.json.JsonConverter");
    configDetails.addProperty("key.converter.schemas.enable", "false");
    configDetails.addProperty("value.converter.schemas.enable", "false");

    // Add transformations
    configDetails.addProperty("transforms", "unwrap,renameFields,extractIdKey");

    // Unwrap fullDocument from MongoDB CDC envelope
    configDetails.addProperty("transforms.unwrap.type", "io.debezium.transforms.ExtractNewDocumentState");
    configDetails.addProperty("transforms.unwrap.drop.tombstones", "true");
    configDetails.addProperty("transforms.unwrap.delete.handling.mode", "drop");

    // Rename fields (_id -> id, name -> title)
    configDetails.addProperty("transforms.renameFields.type", "org.apache.kafka.connect.transforms.ReplaceField$Value");
    configDetails.addProperty("transforms.renameFields.renames", "_id:id,name:title");

    // Extract _id from documentKey to key (so it can be used as primary key)
    configDetails.addProperty("transforms.extractIdKey.type", "org.apache.kafka.connect.transforms.ExtractField$Key");
    configDetails.addProperty("transforms.extractIdKey.field", "_id");

    config.add("config", configDetails);

    return config;
  }


}
