package ru.anvera.models.enums;


import lombok.Getter;


@Getter
public enum ConnectorDrivers {

  /**
   * Критерий:	JDBC Source VS Connector	Debezium
   * Метод работы:	Периодическое сканирование таблиц (polling).	Чтение журнала изменений (CDC).
   * Поддержка: SQL-запросов	Да, любые запросы.	Нет, только работа с целыми таблицами.
   * Реальное время:	Нет, данные поступают с задержкой.	Да, изменения обрабатываются мгновенно.
   * Нагрузка на базу данных:	Высокая при большом объеме данных.	Минимальная (только чтение журнала).
   * Сложность настройки:	Простая.	Требует настройки CDC в базе данных.
   * Мониторинг изменений:	Ограниченный (только новые данные).	Полный (INSERT, UPDATE, DELETE).
   * Типы данных:	Фильтруются на уровне SQL-запроса.	Все данные из таблицы.
   * Сценарии использования:	Одноразовый экспорт, редкие обновления.	Постоянная синхронизация данных.
   */
  DEBEZIUM_TO_POSTGRES("io.debezium.connector.postgresql.PostgresConnector", DataSourceType.SOURCE),
  KAFKA_TO_CLICKHOUSE("com.clickhouse.kafka.connect.sink.ClickHouseSinkConnector", DataSourceType.SINK);

  final String         driverName;
  final DataSourceType connectorType;

  ConnectorDrivers(String driverName, DataSourceType connectorType) {
    this.driverName    = driverName;
    this.connectorType = connectorType;
  }
}
