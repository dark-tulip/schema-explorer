package ru.anvera.models.request;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableMappingInfoGetRequest {
  private Long tableMappingId;
}
