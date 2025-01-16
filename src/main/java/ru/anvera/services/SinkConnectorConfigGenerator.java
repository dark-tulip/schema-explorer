package ru.anvera.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.anvera.models.entity.DatasourceConnection;
import ru.anvera.models.entity.TableMapping;
import ru.anvera.repos.DatasourceConnectionRepository;
import ru.anvera.repos.TableMappingRepository;

/**
 * Генерирует файл с пропертями для таблицы ПОЛУЧАТЕЛЯ (то, куда записываем данные из Kafka).
 */
@Service
@RequiredArgsConstructor
public class SinkConnectorConfigGenerator {

  private final DatasourceConnectionRepository datasourceConnectionRepository;
  private final TableMappingRepository         tableMappingRepository;

  public JsonObject generateSinkConnectorConfig(Long tableMappingId) {
    TableMapping tableMapping = tableMappingRepository.getById(tableMappingId);
    String       tableName    = tableMapping.getSinkTable();
    String       schemaName   = tableMapping.getSinkSchemaName();
    String       topicName    = generateTopicName(schemaName, tableName);

    // Данные из таблицы datasource_connections
    DatasourceConnection connection = datasourceConnectionRepository.getById(tableMapping.getSinkDbConnectionId());
    String               dbType     = connection.getDbType();
    String               url        = connection.getUrl();
    String               username   = connection.getUsername();
    String               password   = connection.getPassword();

    // Генерация конфигурации
    JsonObject config = generateConnectorConfig(tableMapping.getSinkDbConnectionId(), dbType, url, username, password, schemaName, tableName, topicName);

    // Сохранение JSON в файл
    saveToFile(config, dbType + "-sink-connector-config.json");

    return config;
  }

  private static JsonObject generateConnectorConfig(
      long id, String dbType, String url, String username, String password,
      String schemaName, String tableName, String topicName) {

    // Создание конфигурации
    JsonObject config = new JsonObject();
    config.addProperty("name", dbType + "sink-connector-" + id);

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

  private static String generateTopicName(String schemaName, String tableName) {
    return schemaName + "." + tableName;
  }

  private static String extractDbNameFromUrl(String url) {
    return url.substring(url.lastIndexOf("/") + 1);
  }

  private static void saveToFile(JsonObject json, String fileName) {
    try (java.io.FileWriter writer = new java.io.FileWriter(fileName)) {
      Gson gson = new Gson();
      gson.toJson(json, writer);
    } catch (Exception e) {
      throw new RuntimeException("Ошибка при сохранении JSON-файла", e);
    }
  }
}
