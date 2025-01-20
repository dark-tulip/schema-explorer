package ru.anvera.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.anvera.models.entity.DatasourceConnection;
import ru.anvera.models.entity.TableMapping;
import ru.anvera.models.request.DatasourceConnectionAddRequest;
import ru.anvera.models.request.DatasourceConnectionRegisterSchemaMappingRequest;
import ru.anvera.models.response.ColumnMetadataResponse;
import ru.anvera.services.DatasourceConnectionService;
import ru.anvera.services.DatasourceMetadataService;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/datasource/connection/")
public class DatasourceConnectionController {

  private final DatasourceConnectionService datasourceConnectionService;
  private final DatasourceMetadataService   datasourceMetadataService;

  @PostMapping("/add")
  public DatasourceConnection addPost(@RequestBody DatasourceConnectionAddRequest request) {
    return datasourceConnectionService.add(request);
  }

  @PostMapping("/register/table-mapping")
  public TableMapping registerTableMapping(@RequestBody @Validated DatasourceConnectionRegisterSchemaMappingRequest request) {
    return datasourceConnectionService.registerTableMapping(request);
  }

  @GetMapping("/metadata/info")
  public HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> getInfo(@RequestParam Long datasourceConnectionId) {
    return datasourceMetadataService.getInfo(datasourceConnectionId).getSchemaNameAndTables();
  }

}
