package ru.anvera.models.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatasourceConnectionAddResponse {
  private String  dbType;
  private String  url;
  private String  username;
  private String  password;
  private Boolean isActive;
  private String  datasourceType;
}
