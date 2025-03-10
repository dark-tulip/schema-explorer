package ru.anvera.models.enums;

import lombok.Getter;

public enum DbType {
  POSTGRESQL("postgresql", "org.postgresql.Driver"),
  CLICKHOUSE("clickhouse", "com.clickhouse.jdbc.ClickHouseDriver"),
  MONGODB("mongodb", "");

  final String name;

  @Getter
  final String driverClassName;

  DbType(String name, String driverClassName) {
    this.name            = name;
    this.driverClassName = driverClassName;
  }
}
