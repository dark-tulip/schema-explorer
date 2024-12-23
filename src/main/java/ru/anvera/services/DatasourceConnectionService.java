package ru.anvera.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.anvera.models.DataSource;
import ru.anvera.models.entity.DatasourceConnection;
import ru.anvera.models.request.DatasourceConnectionAddRequest;
import ru.anvera.models.request.DatasourceConnectionValidateSchemaMappingRequest;
import ru.anvera.models.request.SchemaMetadataInfoRequest;
import ru.anvera.models.response.ColumnMetadataResponse;
import ru.anvera.models.response.DatasourceMetadataInfoResponse;
import ru.anvera.repos.DatasourceConnectionRepository;
import ru.anvera.repos.TableMappingRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatasourceConnectionService {

  private final DatasourceConnectionRepository datasourceConnectionRepository;
  private final DatasourceMetadataService datasourceMetadataService;
  private final TableMappingRepository    tableMappingRepository;

  public List<DatasourceConnection> getAllConnections() {
    return datasourceConnectionRepository.findAll();
  }

  public DatasourceConnection getConnectionById(Long id) {
    return datasourceConnectionRepository.getById(id);
  }

  public Long add(DatasourceConnectionAddRequest request) {
    return datasourceConnectionRepository.save(new DatasourceConnection(
        null,
        request.getDbType(),
        request.getUrl(),
        request.getUsername(),
        request.getPassword(),
        request.getIsActive(),
        request.getDatasourceType()
    ));
  }

  public void updateConnection(DatasourceConnection connection) {
    datasourceConnectionRepository.update(connection);
  }

  public void deleteConnection(Long id) {
    datasourceConnectionRepository.deleteById(id);
  }

  /**
   * есть source datasource
   * есть еще sink datasource
   * из source мы выбираем схему и таблицу (и выборочно названия столбцов которые хотим перенести) в sink datasource,
   */
  public void validateSchemaMapping(DatasourceConnectionValidateSchemaMappingRequest request) {
    // ****** source metadata ******
    validateSchemaMetadataForExistence(
        request.getSourceDatasourceConnectionId(),
        request.getSourceSchemaName(),
        request.getSourceTableName(),
        request.getSourceColumnsList(),
        DataSource.SOURCE
    );

    // ****** sink metadata ******
    validateSchemaMetadataForExistence(
        request.getSinkDatasourceConnectionId(),
        request.getSinkSchemaName(),
        request.getSinkTableName(),
        request.getSinkColumnsList(),
        DataSource.SINK
    );

    if (request.getSinkColumnsList().size() == request.getSourceColumnsList().size()) {
      throw new RuntimeException("Колво столбцов для маппинга source и sink таблицы должны быть равны");
    }


//    tableMappingRepository.insert(
//        new TableMapping(
//
//        )
//    )


  }

  private void validateSchemaMetadataForExistence(Long datasourceConnectionId,
                                                 String schemaName,
                                                 String tableName,
                                                 List<String> requestColumnNamesForMapping,
                                                 DataSource dataSourceType) {
    DatasourceConnection dbConnection = datasourceConnectionRepository.getById(datasourceConnectionId);
    DatasourceMetadataInfoResponse dbMetadataInfoResponse = datasourceMetadataService.connectAndGetMetadataInfo(new SchemaMetadataInfoRequest(
        dbConnection.getDbType(),
        dbConnection.getUrl(),
        dbConnection.getUsername(),
        dbConnection.getPassword()
    ));

    // схемы
    HashMap<String, List<ColumnMetadataResponse>> sourceSchemaMetadata = dbMetadataInfoResponse.getSchemaNameAndTables().get(schemaName);
    if (sourceSchemaMetadata == null) {
      throw new RuntimeException("Нет такой схемы в " + schemaName + " БД " + dataSourceType);
    }

    // таблицы
    List<ColumnMetadataResponse> sourceTableColumns = sourceSchemaMetadata.get(tableName);
    if (sourceTableColumns == null) {
      throw new RuntimeException("Нет такой таблицы в " + dataSourceType + " БД и схеме " + schemaName);
    }

    Set<String> sourceTableColumnNames = sourceTableColumns.stream()
                                                           .map(ColumnMetadataResponse::getName)
                                                           .collect(Collectors.toSet());

    // все запрашиваемые столбцы должны быть в схеме данных
    for (String columnName : requestColumnNamesForMapping) {
      if (sourceTableColumnNames.contains(columnName)) {
        throw new RuntimeException("Нет такого столбца в " + dataSourceType + " БД и таблице: " + tableName);
      }
    }
  }
}
