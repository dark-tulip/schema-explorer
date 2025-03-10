package ru.anvera.services.connectors.generators;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.anvera.configs.SecurityContextUtils;
import ru.anvera.models.entity.DatasourceConnection;
import ru.anvera.models.entity.TableMapping;
import ru.anvera.repos.DatasourceConnectionRepository;
import ru.anvera.repos.TableMappingRepository;

import static ru.anvera.utils.ConnectorUtils.*;

/**
 * Generates a Kafka Connect configuration file for MongoDB Sink Connector.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MongoSinkConnectorConfigGenerator {

  private final DatasourceConnectionRepository datasourceConnectionRepository;
  private final TableMappingRepository         tableMappingRepository;
  private final SecurityContextUtils           securityContextUtils;

  private final static String DOCKER_COMPOSE_DATABASE_CONTAINER_NAME="mongodb";

  /**
   * Generates a MongoDB Sink Connector configuration in JSON format.
   *
   * @param tableMappingId The ID of the table mapping.
   * @return A JSON object representing the connector configuration.
   */
  public JsonObject generateMongoSinkConnectorConfig(Long tableMappingId) {
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
    String topicName = generateTopicName(extractDbNameFromUrl(sourceDb.getUrl()), schemaName, tableName);

    return generateConnectorConfig(tableMapping.getSinkDbConnectionId(), dbType, url, tableName, topicName);
  }

  /**
   * Builds the configuration JSON for the MongoDB Sink Connector.
   */
  private static JsonObject generateConnectorConfig(Long id, String dbType, String url,
      String tableName, String topicName) {

    log.info("Generating MongoDB Sink Connector for topic: " + topicName);

    JsonObject config = new JsonObject();
    config.addProperty("name", dbType + "-mongo-sink-connector-" + id);

    JsonObject configDetails = new JsonObject();
    configDetails.addProperty("connector.class", "com.mongodb.kafka.connect.MongoSinkConnector");
    configDetails.addProperty("tasks.max", "1");
    configDetails.addProperty("topics", topicName);
    // todo adaptation for docker compose
    configDetails.addProperty("database.hostname", extractHostname(url).replaceAll("localhost", DOCKER_COMPOSE_DATABASE_CONTAINER_NAME));
    configDetails.addProperty("connection.uri", url);
    configDetails.addProperty("database", "public");
    configDetails.addProperty("collection", tableName);

    // Kafka Message format settings
    configDetails.addProperty("key.converter", "org.apache.kafka.connect.storage.StringConverter");
    configDetails.addProperty("value.converter", "org.apache.kafka.connect.json.JsonConverter");

    // Setting up ID strategy to use "payload.id" as MongoDB "_id"
    configDetails.addProperty("document.id.strategy", "com.mongodb.kafka.connect.sink.processor.id.strategy.PartialValueStrategy");
    configDetails.addProperty("document.id.strategy.partial.value.projection.list", "id");
    configDetails.addProperty("document.id.strategy.partial.value.projection.type", "AllowList");

    config.add("config", configDetails);
    return config;
  }
}
