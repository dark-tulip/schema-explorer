package ru.anvera.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.anvera.configs.CustomUserPrincipal;
import ru.anvera.models.entity.DatasourceConnection;
import ru.anvera.models.entity.TableMapping;
import ru.anvera.models.request.DatasourceConnectionAddRequest;
import ru.anvera.models.request.DatasourceConnectionRegisterSchemaMappingRequest;
import ru.anvera.models.response.ColumnMetadataResponse;
import ru.anvera.services.DatasourceConnectionService;
import ru.anvera.services.DatasourceMetadataService;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/datasource/connection/")
public class DatasourceConnectionController {

  private final DatasourceConnectionService datasourceConnectionService;
  private final DatasourceMetadataService   datasourceMetadataService;

  @PostMapping("/add")
  public DatasourceConnection addPost(
      @AuthenticationPrincipal CustomUserPrincipal principal,
      @RequestBody DatasourceConnectionAddRequest request) {
    return datasourceConnectionService.add(request, principal);
  }

  @PostMapping("/register/table-mapping")
  public TableMapping registerTableMapping(
      @AuthenticationPrincipal CustomUserPrincipal principal,
      @RequestBody @Validated DatasourceConnectionRegisterSchemaMappingRequest request) {
    return datasourceConnectionService.registerTableMapping(
        request,
        principal
    );
  }

  @GetMapping("/metadata/info")
  public HashMap<String, HashMap<String, List<ColumnMetadataResponse>>> getInfo(
      @AuthenticationPrincipal CustomUserPrincipal principal,
      @RequestParam Long datasourceConnectionId) {
    return datasourceMetadataService.getInfo(datasourceConnectionId, principal.getProjectId())
                                    .getSchemaNameAndTables();
  }

}
