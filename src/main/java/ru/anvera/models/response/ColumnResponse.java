package ru.anvera.models.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnResponse {
  private String name;
  private String type;
  private String constraints;
}
