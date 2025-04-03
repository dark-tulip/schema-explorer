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
 * Generates a Kafka Connect configuration file for MongoDB Source Connector.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MongoSourceConnectorConfigGenerator {

  private final DatasourceConnectionRepository datasourceConnectionRepository;
  private final TableMappingRepository tableMappingRepository;
  private final SecurityContextUtils securityContextUtils;

  private static final String DOCKER_COMPOSE_DATABASE_CONTAINER_NAME = "mongodb";

  /**
   * Generates a MongoDB Source Connector configuration in JSON format.
   *
   * @param tableMappingId The ID of the table mapping.
   * @return A JSON object representing the connector configuration.
   */
  public JsonObject  generateMongoSourceConnectorConfig(Long tableMappingId) {
    Long projectId = securityContextUtils.getPrincipal().getProjectId();

    TableMapping tableMapping = tableMappingRepository.getById(tableMappingId);
    String tableName = tableMapping.getSourceTable();
    String schemaName = tableMapping.getSourceSchemaName();

    DatasourceConnection sourceDb = datasourceConnectionRepository.getById(
        tableMapping.getSourceDbConnectionId(),
        projectId
    );

    String dbType = sourceDb.getDbType();
    String url = sourceDb.getUrl();
    String topicName = tableName;

    return generateSourceConnectorConfig(tableMapping.getSourceDbConnectionId(), dbType, url, schemaName, tableName, topicName);
  }

  /**
   * Builds the configuration JSON for the MongoDB Source Connector.
   */
  private static JsonObject generateSourceConnectorConfig(Long id, String dbType, String url,
                                                          String schemaName, String collectionName, String topicName) {

    log.info("Generating MongoDB Source Connector for topic: {}", topicName);

    JsonObject config = new JsonObject();
    config.addProperty("name", dbType + "-mongo-source-connector-" + id);

    JsonObject configDetails = new JsonObject();
    configDetails.addProperty("connector.class", "com.mongodb.kafka.connect.MongoSourceConnector");
    configDetails.addProperty("tasks.max", "1");
    configDetails.addProperty("connection.uri", url.replace("localhost", DOCKER_COMPOSE_DATABASE_CONTAINER_NAME));
    configDetails.addProperty("database", schemaName);
    configDetails.addProperty("collection", collectionName);
    configDetails.addProperty("topic.prefix", dbType);

    // Optional: Include all existing data
    configDetails.addProperty("copy.existing", "true");
    configDetails.addProperty("copy.existing.pipeline", "[{ \"$match\": {} }]");

    // Kafka Message format
    configDetails.addProperty("key.converter", "org.apache.kafka.connect.storage.StringConverter");
    configDetails.addProperty("value.converter", "org.apache.kafka.connect.storage.StringConverter");

    // Tuning
    configDetails.addProperty("poll.max.batch.size", "1000");
    configDetails.addProperty("poll.await.time.ms", "5000");

    config.add("config", configDetails);
    return config;
  }
}
