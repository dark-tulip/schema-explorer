package ru.anvera.models.enums;

/**
 * Source - стро
 */
public enum DataSource {
  SOURCE("source"),
  SINK("sink");

  final String name;

  DataSource(String name) {
    this.name = name;
  }

}
