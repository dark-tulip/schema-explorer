package ru.anvera.models.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableResponse {
  private String               table;
  private List<ColumnResponse> columns;
}
