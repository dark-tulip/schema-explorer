package ru.anvera.services.connectors;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anvera.configs.SecurityContextUtils;
import ru.anvera.factory.DynamicJdbcTemplateFactory;
import ru.anvera.models.entity.DatasourceConnection;
import ru.anvera.models.entity.TableMapping;
import ru.anvera.models.enums.DataSourceType;
import ru.anvera.repos.DatasourceConnectionRepository;
import ru.anvera.repos.TableMappingRepository;

import java.util.Map;

import static ru.anvera.configs.KafkaBrocerConfigs.KAFKA_BROKER_NAME_LIST;
import static ru.anvera.utils.ConnectorUtils.generateTopicName;

@Service
@RequiredArgsConstructor
public class ClickHouseRegistrationService implements RegistrationService {

  private final DynamicJdbcTemplateFactory     jdbcTemplateFactory;
  private final DatasourceConnectionRepository datasourceConnectionRepository;
  private final SecurityContextUtils           securityContextUtils;
  private final TableMappingRepository         tableMappingRepository;

  @Override
  @Transactional
  public void register(Long tableMappingId, DataSourceType dataSourceType) {
    if (dataSourceType.equals(DataSourceType.SINK)) {
      // тут не нужен коннектор, в кликхаусе есть встроенный консюмер из БД
      registerSinkConsumer(tableMappingId);
    } else {
      throw new RuntimeException("method is not implemented");
    }

  }

  private void registerSinkConsumer(Long tableMappingId) {
    TableMapping tableMapping = tableMappingRepository.getById(tableMappingId);
    Long         projectId    = securityContextUtils.getPrincipal().getProjectId();

    DatasourceConnection sinkDb   = datasourceConnectionRepository.getById(tableMapping.getSinkDbConnectionId(), projectId);
    DatasourceConnection sourceDb = datasourceConnectionRepository.getById(tableMapping.getSourceDbConnectionId(), projectId);

    JdbcTemplate sinkJdbcTemplate = jdbcTemplateFactory.createJdbcTemplate(
        sinkDb.getDbType(),
        sinkDb.getUrl(),
        sinkDb.getUsername(),
        sinkDb.getPassword()
    );

    String rawKafkaSinkTableName          = "kafka_" + tableMapping.getSinkTable() + "_raw";
    String persistentStorageSinkTableName = tableMapping.getSinkTable();
    String materializedViewProcessorName  = "kafka_to_" + tableMapping.getSinkTable();

    String readFromTopic = generateTopicName(
        sourceDb.getDbNameFromUrl(),
        tableMapping.getSourceSchemaName(),
        tableMapping.getSourceTable()
    );

    /*
     * -- Шаг 1: Создать ClickHouse-таблицу для Kafka (temporary)
     * -- kafka_books_raw не хранит данные, а только читает новые сообщения из Kafka в реальном времени.
     * -- Данная таблица работает как очередь, которая позволяет ClickHouse читать данные из Kafka без внешних консюмеров (Python, Java).
     * -- ClickHouse по умолчанию ожидает "плоский" JSON, поэтому нам нужно извлекать только payload
     * -- Мы используем JSONAsString, чтобы сначала загрузить полные сообщения, а потом парсить их
     */
    String createRawKafkaTable = """
        CREATE TABLE IF NOT EXISTS %s (
            raw_message String
        ) ENGINE = Kafka
        SETTINGS kafka_broker_list = '%s',
                 kafka_topic_list = '%s',
                 kafka_group_name = 'clickhouse-consumer',
                 kafka_format = 'JSONAsString',
                 kafka_max_block_size = 65536;
        """.formatted(rawKafkaSinkTableName, KAFKA_BROKER_NAME_LIST, readFromTopic);

    /*
     * -- Шаг 2: Создать таблицу хранения (persistent)
     * -- По умолчанию, сообщения читаются только один раз → Если ClickHouse уже прочитал сообщение, то при следующем SELECT в kafka_books_raw оно исчезнет.
     * -- Она работает как "стрим" → В отличие от MergeTree, Kafka-таблица просто отображает поток данных, а не хранит их.
     */
    StringBuilder createStorageKafkaTable = new StringBuilder("CREATE TABLE IF NOT EXISTS " + persistentStorageSinkTableName + " (\n");
    tableMapping.getSourceToSinkColumnNameMapping().forEach(
        (sourceCol, sinkCol) -> createStorageKafkaTable.append(sinkCol).append(" String,\n")
    );

    createStorageKafkaTable.append(") ENGINE = MergeTree() ORDER BY ")
                           .append(tableMapping.getSourceToSinkColumnNameMapping().values().iterator().next())
                           .append(";");

    /*
     * -- Шаг 3: (автоконвертация и перекладывание) Создадим материализованное представление,
     * -- которое будет разбирать JSON и сохранять данные в постоянном хранилище
     * -- ClickHouse автоматически передаёт их в MergeTree через MATERIALIZED VIEW.
     * -- Когда Kafka отправляет новое сообщение: ClickHouse-консьюмер читает сообщение в kafka_books_raw
     *  и через MATERIALIZED VIEW сохраняет их в persistent storage
     */
    StringBuilder createMaterializedViewQuery = new StringBuilder("CREATE MATERIALIZED VIEW IF NOT EXISTS " + materializedViewProcessorName + " TO " + persistentStorageSinkTableName + " AS SELECT\n");
    for (Map.Entry<String, String> entry : tableMapping.getSourceToSinkColumnNameMapping().entrySet()) {
      String sourceCol      = entry.getKey();
      String sinkCol        = entry.getValue();
      String transformation = tableMapping.getTransformations().get(sourceCol);
      createMaterializedViewQuery.append(transformation != null
                                     ? transformation + " AS " + sinkCol
                                     : "JSONExtractString(raw_message, 'payload', '" + sourceCol + "') AS " + sinkCol)
                                 .append(",\n");
    }

    createMaterializedViewQuery.delete(createMaterializedViewQuery.length() - 2, createMaterializedViewQuery.length()); // убрать лишнюю точку с запятой на конце
    createMaterializedViewQuery.append(" FROM ").append(rawKafkaSinkTableName).append(";");

    sinkJdbcTemplate.execute(createRawKafkaTable);
    sinkJdbcTemplate.execute(createStorageKafkaTable.toString());
    sinkJdbcTemplate.execute(createMaterializedViewQuery.toString());
  }


}
