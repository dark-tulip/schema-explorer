package ru.anvera.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.anvera.models.request.SchemaMetadataInfoRequest;
import ru.anvera.models.response.ColumnMetadataResponse;
import ru.anvera.services.DatasourceMetadataService;

import java.util.HashMap;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/datasource/metadata")
public class DatasourceMetadataController {

  private final DatasourceMetadataService datasourceMetadataService;

  @PostMapping("/info")
  public HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> getInfo(@Validated @RequestBody SchemaMetadataInfoRequest request) {
    return datasourceMetadataService.connectAndGetMetadataInfo(request).getSchemaNameAndTables();
  }

}
