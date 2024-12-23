package ru.anvera.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.anvera.models.entity.DatasourceConnection;
import ru.anvera.models.request.DatasourceConnectionAddRequest;
import ru.anvera.models.request.DatasourceConnectionValidateSchemaMappingRequest;
import ru.anvera.models.request.SchemaMetadataInfoRequest;
import ru.anvera.models.response.ColumnMetadataResponse;
import ru.anvera.models.response.SchemaMetadataInfoResponse;
import ru.anvera.repos.DatasourceConnectionRepository;

import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatasourceConnectionService {

  private final DatasourceConnectionRepository datasourceConnectionRepository;
  private final SchemaMetadataService          schemaMetadataService;



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

  public void validateSchemaMapping(DatasourceConnectionValidateSchemaMappingRequest request) {
    // ****** source metadata ******
    DatasourceConnection source = datasourceConnectionRepository.getById(request.getSourceDatasourceConnectionId());
    SchemaMetadataInfoResponse sourceMetadataInfoResponse = schemaMetadataService.getInfo(new SchemaMetadataInfoRequest(
        source.getDbType(),
        source.getUrl(),
        source.getUsername(),
        source.getPassword()
    ));

    // схемы
    HashMap<String, List<ColumnMetadataResponse>> sourceSchemaMetadata = sourceMetadataInfoResponse.getSchemaNameAndTables().get(request.getSourceSchemaName());
    if (sourceSchemaMetadata == null) {
      throw new RuntimeException("Нет такой схемы в источнике: " + request.getSourceSchemaName());
    }

    // таблицы
    List<ColumnMetadataResponse> sourceTableColumns = sourceSchemaMetadata.get(request.getSourceTableName());
    if (sourceTableColumns == null) {
      throw new RuntimeException("Нет такой таблицы в источнике: " + request.getSourceSchemaName());
    }

    // ****** sink metadata ******
    DatasourceConnection sink = datasourceConnectionRepository.getById(request.getSinkDatasourceConnectionId());
    SchemaMetadataInfoResponse sinkMetadataInfoResponse = schemaMetadataService.getInfo(new SchemaMetadataInfoRequest(
        sink.getDbType(),
        sink.getUrl(),
        sink.getUsername(),
        sink.getPassword()
    ));

    // схемы ышил
    HashMap<String, List<ColumnMetadataResponse>> sinkSchemaMetadata = sinkMetadataInfoResponse.getSchemaNameAndTables().get(request.getSinkTableName());
    if (sinkSchemaMetadata == null) {
      throw new RuntimeException("Нет такой схемы в целевой базе данных: " + request.getSinkSchemaName());
    }

    // таблицы
    List<ColumnMetadataResponse> sinkTableColumns = sourceSchemaMetadata.get(request.getSinkTableName());
    if (sinkTableColumns == null) {
      throw new RuntimeException("Нет такой таблицы в целевой схеме: " + request.getSinkSchemaName());
    }

    // если нам нужно создать новые схемы
    if (request.isCreateNewSchema()) {
      // todo подумай
    }

    // schema1
    // table_name1
    // columns -> column_type {10}

    // schema2
    // table_name2
    // columns -> column_type {5}

//    sourceSchemaM
//    sourceTableColumns.get()
    for (ColumnMetadataResponse columnMetadata : sourceTableColumns) {
//      request.getSinkTableName()
    }


  }
}
