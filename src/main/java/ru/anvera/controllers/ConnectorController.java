package ru.anvera.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.anvera.models.enums.DataSource;
import ru.anvera.services.ConnectorRegistrationService;

@RestController
@RequestMapping("/connectors")
@RequiredArgsConstructor
public class ConnectorController {

  private final ConnectorRegistrationService registrationService;

  @PostMapping("/register/source")
  public void registerSourceConnector(@RequestParam Long tableMappingId) throws Exception {
    registrationService.registerConnector(tableMappingId, DataSource.SOURCE.toString());
  }

  @PostMapping("/register/sink")
  public void registerSinkConnector(@RequestParam Long tableMappingId) throws Exception {
    registrationService.registerConnector(tableMappingId, DataSource.SINK.toString());
  }

}
