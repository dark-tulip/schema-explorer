package ru.anvera.models.enums;


public enum DataSourceType {
  SOURCE("source"),
  SINK("sink");

  final String name;

  DataSourceType(String name) {
    this.name = name;
  }
}
