package ru.anvera.models.enums;

import lombok.Getter;

public enum DbType {
  POSTGRESQL("postgresql", "org.postgresql.Driver"),
  CLICKHOUSE("clickhouse", "com.clickhouse.jdbc.ClickHouseDriver"),
  MONGODB("mongodb", "");

  @Getter
  final String name;

  @Getter
  final String driverClassName;

  public static DbType valueOfToUpperCase(String value) {
    return DbType.valueOf(value.toUpperCase());
  }

  DbType(String name, String driverClassName) {
    this.name            = name;
    this.driverClassName = driverClassName;
  }
}
