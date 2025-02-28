package ru.anvera.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.anvera.models.enums.DataSourceType;
import ru.anvera.services.ConnectorRegistrationService;

@RestController
@RequestMapping("/connectors/register")
@RequiredArgsConstructor
public class ConnectorRegisterController {

  private final ConnectorRegistrationService connectorRegistrationService;

  @PostMapping("/source")
  public void registerSourceConnector(@RequestParam Long tableMappingId,
                                      @RequestParam String dbTypeString) {
    connectorRegistrationService.registerConnector(tableMappingId, DataSourceType.SOURCE, dbTypeString);
  }

  @PostMapping("/sink")
  public void registerSinkConnector(@RequestParam Long tableMappingId,
                                    @RequestParam String dbTypeString) {
    connectorRegistrationService.registerConnector(tableMappingId, DataSourceType.SINK, dbTypeString);
  }

}
