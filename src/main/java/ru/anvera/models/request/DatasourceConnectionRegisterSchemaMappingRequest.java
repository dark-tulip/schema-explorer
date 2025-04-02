package ru.anvera.models.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatasourceConnectionRegisterSchemaMappingRequest {

  @NotNull(message = "sinkDbConnectionId cannot be null")
  private Long sinkDbConnectionId;
  @NotNull(message = "sourceDbConnectionId cannot be null")
  private Long sourceDbConnectionId;

  // схема
  @NotNull
  private String sourceSchemaName;
  @NotNull
  private String sinkSchemaName;

  // таблицы
  @NotNull
  private String sourceTableName;
  @NotNull
  private String sinkTableName;

  // столбцы
  private List<String> sourceColumnsList;
  private List<String> sinkColumnsList;

  // Преобразования, применяемые к данным. При перемещении из source в sink
  private HashMap<String, String> transformations;

  @NotNull(message = "you should set createNew in your request body")
  private Boolean createNew;

}
