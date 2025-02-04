package ru.anvera.models.enums;


public enum DataSource {
  SOURCE("source"),
  SINK("sink");

  final String name;

  DataSource(String name) {
    this.name = name;
  }
}
