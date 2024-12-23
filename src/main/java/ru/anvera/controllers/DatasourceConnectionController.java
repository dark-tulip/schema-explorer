package ru.anvera.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.anvera.models.request.DatasourceConnectionAddRequest;
import ru.anvera.models.request.DatasourceConnectionValidateSchemaMappingRequest;
import ru.anvera.services.DatasourceConnectionService;

@RequiredArgsConstructor
@RestController("/datasource/connection/")
public class DatasourceConnectionController {

  private final DatasourceConnectionService datasourceConnectionService;

  @PostMapping("/add")
  public Long addPost(@RequestBody DatasourceConnectionAddRequest request) {
    return datasourceConnectionService.add(request);
  }

//  @PostMapping("/validate/check-connection")
//  public void validateCheckConnectionPost(@RequestBody DatasourceConnectionAddRequest request) {
//    datasourceConnectionService.validateCheckConnectionPost(request);
//  }

  @PostMapping("/validate/schema-mapping")
  public void validateSchemaMapping(@RequestBody DatasourceConnectionValidateSchemaMappingRequest request) {
    datasourceConnectionService.validateSchemaMapping(request);
  }


}
