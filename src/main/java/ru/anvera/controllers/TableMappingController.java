package ru.anvera.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.anvera.models.response.TableMappingAllGetResponse;
import ru.anvera.models.response.TableMappingInfoGetResponse;
import ru.anvera.services.TableMappingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/table-mapping")
public class TableMappingController {

  private final TableMappingService tableMappingService;

  @GetMapping("/all")
  public TableMappingAllGetResponse getAll() {
    return tableMappingService.getAll();
  }

  @GetMapping("/info")
  public TableMappingInfoGetResponse getInfo(Long tableMappingId) {
    return tableMappingService.getInfo(tableMappingId);
  }

}
