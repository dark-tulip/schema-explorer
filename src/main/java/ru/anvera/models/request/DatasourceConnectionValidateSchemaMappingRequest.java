package ru.anvera.models.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  private HashMap<String, String> transformations;

}
