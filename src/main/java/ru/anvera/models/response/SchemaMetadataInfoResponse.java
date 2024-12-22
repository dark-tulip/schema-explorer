package ru.anvera.models.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchemaMetadataInfoResponse {
  private String              schema;
  private List<TableResponse> tables;
}
