package ru.anvera.models.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateConnectionAndGetInfoRequest {

  @NotNull(message = "dbType cannot be null")
  private String dbType;

  @NotNull(message = "jdbc connection url cannot be null")
  private String url;

  @NotNull(message = "username cannot be null")
  private String username;

  @NotNull(message = "password cannot be null")
  private String password;
}
