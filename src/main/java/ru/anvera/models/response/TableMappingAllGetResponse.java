package ru.anvera.models.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long tableMappingId;
    private String sourceTableName;
    private String sinkTableName;
  }
}
