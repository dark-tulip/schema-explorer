package ru.anvera.models.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableMapping {
  private Long   id;
  private Long   sourceDbConnectionId;
  private Long   sinkDbConnectionId;
  private String sourceSchemaName;
  private String sinkSchemaName;
  private String sourceTable;
  private String sinkTable;
  private Long   projectId;

  /**
   * * кол-во столбцов для маппинга в source and sink должны быть равны
   * {
   * "source_columns": ["col1", "col2", "col3"],
   * "sink_columns": ["colA", "colB", "colC"],
   * }
   * <p>
   * {
   * "col1": "colA",
   * "col2": "colB",
   * "col3": "colC"
   * }
   */
  private HashMap<String, String> sourceToSinkColumnNameMapping;

  /**
   * type -> jsonb
   * <p>
   * "transformations": {
   * "col1": "UPPER(col1)",
   * "col2": null,
   * "col3": "col3::DATE"
   * }
   * }
   */
  private HashMap<String, String> transformations;
}
