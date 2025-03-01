package ru.anvera.models.enums;

import lombok.Getter;

public enum DbType {
  POSTGRESQL("postgresql", "org.postgresql.Driver"),
  CLICKHOUSE("clickhouse", "com.clickhouse.jdbc.ClickHouseDriver");

  final String name;

  @Getter
  final String driverClassName;

  DbType(String name, String driverClassName) {
    this.name            = name;
    this.driverClassName = driverClassName;
  }
}
