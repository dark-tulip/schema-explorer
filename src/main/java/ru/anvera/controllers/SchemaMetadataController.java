package ru.anvera.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.anvera.models.request.SchemaMetadataInfoRequest;
import ru.anvera.models.response.ColumnMetadataResponse;
import ru.anvera.services.SchemaMetadataService;

import java.util.HashMap;
import java.util.List;


@RequiredArgsConstructor
@RestController("/schema/metadata")
public class SchemaMetadataController {

  private final SchemaMetadataService schemaMetadataService;

  @PostMapping("/info")
  public HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> getInfo(@Validated @RequestBody SchemaMetadataInfoRequest request) {
    return schemaMetadataService.getInfo(request).getSchemaNameAndTables();
  }

}
