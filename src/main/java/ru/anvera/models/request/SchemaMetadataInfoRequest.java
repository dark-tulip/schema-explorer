package ru.anvera.models.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchemaMetadataInfoRequest {
  private String dbType;
  private String url;
  private String username;
  private String password;
}
