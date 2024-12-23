package ru.anvera.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.anvera.models.entity.TableMapping;
import ru.anvera.models.request.DatasourceConnectionAddRequest;
import ru.anvera.models.request.DatasourceConnectionValidateSchemaMappingRequest;
import ru.anvera.services.DatasourceConnectionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/datasource/connection/")
public class DatasourceConnectionController {

  private final DatasourceConnectionService datasourceConnectionService;

  @PostMapping("/add")
  public Long addPost(@RequestBody DatasourceConnectionAddRequest request) {
    return datasourceConnectionService.add(request);
  }

  @PostMapping("/validate/schema-mapping")
  public TableMapping validateSchemaMapping(@RequestBody DatasourceConnectionValidateSchemaMappingRequest request) {
    return datasourceConnectionService.validateSchemaMapping(request);
  }


}
