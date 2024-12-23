package ru.anvera.models.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatasourceConnectionValidateSchemaMappingRequest {
  private Long sinkDatasourceConnectionId;
  private Long sourceDatasourceConnectionId;

  // схема
  private String sourceSchemaName;
  private String sinkSchemaName;

  // таблицы
  private String sourceTableName;
  private String sinkTableName;

  // столбцы
  private List<String> sourceColumnsList;
  private List<String> sinkColumnsList;

  // todo optional field to create new schema and tables
  private boolean createNewSchema = true;
}
