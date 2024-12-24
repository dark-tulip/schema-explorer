package ru.anvera.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.anvera.models.entity.DatasourceConnection;
import ru.anvera.models.entity.TableMapping;
import ru.anvera.models.enums.ConnectorDrivers;
import ru.anvera.repos.DatasourceConnectionRepository;
import ru.anvera.repos.TableMappingRepository;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Генерирует файл с пропертями для таблицы ИСТОЧНИКА (то откуда берем данные и кладем в кафка топик)
 */
@Service
@RequiredArgsConstructor
public class SourceConnectorConfigGenerator {

  private final DatasourceConnectionRepository datasourceConnectionRepository;
  private final TableMappingRepository         tableMappingRepository;

  public JsonObject generateSourceConnectorConfig(Long tableMappingId) {
    TableMapping tableMapping = tableMappingRepository.getById(tableMappingId);
    String       tableName    = tableMapping.getSourceTable();
    String       schemaName   = tableMapping.getSourceSchemaName();
    String       topicPrefix  = generateTopicPrefix(schemaName, tableName);
    Set<String>  tableColumns = tableMapping.getSourceToSinkColumnNameMapping().keySet();

    // Данные из таблицы datasource_connections
    DatasourceConnection connection = datasourceConnectionRepository.getById(tableMapping.getSourceDbConnectionId());
    String               dbType     = connection.getDbType();
    String               url        = connection.getUrl();
    String               username   = connection.getUsername();
    String               password   = connection.getPassword();

    // Генерация конфигурации
    JsonObject config = generateConnectorConfig(tableMapping.getSourceDbConnectionId(), dbType, url, username, password, schemaName, tableName, tableColumns, topicPrefix);

    // Сохранение JSON в файл
    saveToFile(config, dbType + "-source-connector-config.json");

    return config;
  }

  private static JsonObject generateConnectorConfig(
      long id, String dbType, String url, String username, String password,
      String schemaName, String tableName, Set<String> tableColumns, String topicPrefix) {

    // wrap columns
    String formattedColumnNames = tableColumns.stream()
                                              .map(columnName -> schemaName + "." + tableName + "." + columnName)
                                              .collect(Collectors.joining(","));

    String dbName = extractDbNameFromUrl(url);

    // Создание конфигурации
    JsonObject config = new JsonObject();
    config.addProperty("name", dbType + "-connector-" + id);

    JsonObject configDetails = new JsonObject();
    configDetails.addProperty("connector.class", ConnectorDrivers.DEBEZIUM_TO_POSTGRES.getDriverName());
    configDetails.addProperty("tasks.max", "1");
    configDetails.addProperty("database.hostname", extractHostname(url));
    configDetails.addProperty("database.port", extractPort(url));
    configDetails.addProperty("database.user", username);
    configDetails.addProperty("database.password", password);
    configDetails.addProperty("database.dbname", dbName);
    configDetails.addProperty("topic.prefix", topicPrefix);
    configDetails.addProperty("schema.include.list", schemaName);
    configDetails.addProperty("table.include.list", schemaName + "." + tableName);
    configDetails.addProperty("column.include.list", formattedColumnNames);
    configDetails.addProperty("plugin.name", "pgoutput");
    configDetails.addProperty("transforms", "unwrap");
    configDetails.addProperty("transforms.unwrap.type", "io.debezium.transforms.ExtractNewRecordState");
    configDetails.addProperty("key.converter", "org.apache.kafka.connect.json.JsonConverter");
    configDetails.addProperty("value.converter", "org.apache.kafka.connect.json.JsonConverter");
    configDetails.addProperty("key.converter.schemas.enable", "true");
    configDetails.addProperty("value.converter.schemas.enable", "true");

    config.add("config", configDetails);

    return config;
  }

  private static String generateTopicPrefix(String schemaName, String tableName) {
    return schemaName + "." + tableName;
  }

  private static String extractDbNameFromUrl(String url) {
    return url.substring(url.lastIndexOf("/") + 1);
  }

  private static String extractHostname(String url) {
    return url.split("//")[1].split(":")[0];
  }

  private static String extractPort(String url) {
    return url.split(":")[2].split("/")[0];
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
