package ru.anvera.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.anvera.models.entity.TableMapping;
import ru.anvera.models.response.TableMappingAllGetResponse;
import ru.anvera.models.response.TableMappingInfoGetResponse;
import ru.anvera.repos.TableMappingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableMappingService {

  private final TableMappingRepository tableMappingRepository;

  public TableMappingInfoGetResponse getInfo(Long tableMappingId) {
    TableMapping tableMapping = tableMappingRepository.getById(tableMappingId);

    return new TableMappingInfoGetResponse(
        tableMappingId,
        new TableMappingInfoGetResponse.DatasourceConnectionInfo(
            tableMapping.getSourceDbConnectionId(),
            tableMapping.getSourceSchemaName(),
            tableMapping.getSourceTable(),
            new ArrayList<>(tableMapping.getSourceToSinkColumnNameMapping().keySet())
        ),
        new TableMappingInfoGetResponse.DatasourceConnectionInfo(
            tableMapping.getSinkDbConnectionId(),
            tableMapping.getSinkSchemaName(),
            tableMapping.getSinkTable(),
            new ArrayList<>(tableMapping.getSourceToSinkColumnNameMapping().values())
        )
    );
  }

  public TableMappingAllGetResponse getAll() {
    List<TableMapping> tableMappingList = tableMappingRepository.getAll();

    List<TableMappingAllGetResponse.TableMappingInfoShort> tableMappingInfoShorts = tableMappingList
        .stream()
        .map(x -> new TableMappingAllGetResponse.TableMappingInfoShort(
            x.getId(),
            x.getSourceSchemaName() + "." + x.getSourceTable(),
            x.getSinkSchemaName() + "." + x.getSinkTable()
        )).collect(Collectors.toList());

    return new TableMappingAllGetResponse(tableMappingInfoShorts);
  }
}
