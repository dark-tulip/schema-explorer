package ru.anvera.models.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static ru.anvera.utils.ConnectorUtils.extractDbNameFromUrl;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatasourceConnection {
  private Long    id;
  private String  dbType;
  private String  url;
  private String  username;
  private String  password;
  private Boolean isActive;
  private String  datasourceType;
  private Long  projectId;

  public String getDbNameFromUrl() {
    return extractDbNameFromUrl(url);
  }
}
