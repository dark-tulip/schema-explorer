package ru.anvera.models.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatasourceConnectionAddRequest {
  private String  dbType;
  private String  url;
  private String  username;
  private String  password;
  private Boolean isActive;
  private String  datasourceType;
}
