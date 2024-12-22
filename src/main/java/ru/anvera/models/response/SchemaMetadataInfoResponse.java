package ru.anvera.models.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchemaMetadataInfoResponse {
  HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> schemaNameAndTables = new HashMap<>();
}
