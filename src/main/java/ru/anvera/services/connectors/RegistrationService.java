package ru.anvera.services.connectors;

import ru.anvera.models.enums.DataSourceType;

interface RegistrationService {
  void register(Long tableMappingId, DataSourceType dataSourceType);

}
