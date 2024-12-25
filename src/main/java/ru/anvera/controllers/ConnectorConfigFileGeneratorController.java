package ru.anvera.controllers;


import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.anvera.services.SourceConnectorConfigGenerator;

@RequestMapping("/connector/configs/")
@RestController
@RequiredArgsConstructor
public class ConnectorConfigFileGeneratorController {
  private final SourceConnectorConfigGenerator sourceConnectorConfigGenerator;

  /**
   * Генерирует source-connector-config-properties.json файл,
   * который содержит параметры для регистрации коннектора
   *
   * @param tableMappingId идентификатор сопоставленных полей и столбцов.
   * @return сгенерированный JsonObject,
   * благодаря которому можно зарегистрировать коннектор с помощью curl вызова в кластере кафки
   */
  @GetMapping(value = "/generate/source", produces = "application/json")
  public String generateSourceConnectorConfig(@RequestParam Long tableMappingId) {
    JsonObject object = sourceConnectorConfigGenerator.generateSourceConnectorConfig(tableMappingId);
    return object.toString();
  }

  @GetMapping("/generate/sink")
  public JsonObject generate(@RequestParam Long datasourceConnectorId) {
    return sourceConnectorConfigGenerator.generateSourceConnectorConfig(datasourceConnectorId);
  }
}
