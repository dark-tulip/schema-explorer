package ru.anvera.models.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.anvera.models.enums.DbType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableMappingAllGetResponse {

  private List<TableMappingInfoShort> tableMappingInfoShorts;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TableMappingInfoShort {
    private Long   tableMappingId;
    private DbType sourceDbType;
    private DbType sinkDbType;
    private String sourceTableName;
    private String sinkTableName;
  }
}
