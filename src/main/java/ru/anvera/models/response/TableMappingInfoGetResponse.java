package ru.anvera.models.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableMappingInfoGetResponse {

  private Long                     tableMappingId;
  private DatasourceConnectionInfo source;
  private DatasourceConnectionInfo sink;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DatasourceConnectionInfo {
    private Long         datasourceConnectionId;
    private String       schemaName;
    private String       tableName;
    private List<String> columnsList;
  }
}
