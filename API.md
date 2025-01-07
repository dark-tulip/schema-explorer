# 1. Quick start

1. локально поднимите сервисы

```bash
docker compose up
```

2. После запустите приложение из класса Main (зеленая стрелка запуска в IDEA)
3. После можно вызывать АПИ которые есть в сервисе. Контракты описаны ниже

--- 

# 2. Schema explorer REST API

## Получить схему данных по источнику данных
```bash
curl --location 'http://localhost:8081/info' \
--header 'Content-Type: application/json' \
--data '{
    "dbType": "postgresql",
    "url": "jdbc:postgresql://localhost:5432/your_database",
    "username": "your_username",
    "password": "your_password"
}'
```

## Зарегистрировать маппинг полей из source в sink
- описывает каким образом мы хотим сопоставить поля из источника в целевую БД
```bash
curl --location 'http://localhost:8081/datasource/connection/validate/schema-mapping' \
--header 'Content-Type: application/json' \
--data '{
  "sinkDbConnectionId": 7,
  "sourceDbConnectionId": 6,
  "sourceSchemaName": "public",
  "sinkSchemaName": "public",
  "sourceTableName": "sourcetable1",
  "sinkTableName": "sinktable1",
  "sourceColumnsList": ["columna", "columnb", "columnc"],
  "sinkColumnsList": ["column1", "column2", "column3"],
  "transformations": {
    "column1": "UPPER(column1)",
    "column3": "TO_DATE(column3, '\''YYYY-MM-DD'\'')"
  }
}
'
```

Данные будут сохраняться в таблице `table_mapping`

## Генератор пропертей для регистрации нового коннектора

- еще не доработанное АПИ, созает `source-postgres-connector.json` файл, который нужен для регистрации коннектора

```bash
curl --location 'http://localhost:8081/connector/configs/generate/source?tableMappingId=1'
```

# 3. Вспомогательные curl 

## Настроить source connector (PG -> Kafka)
```bash
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @postgresql-source-connector-config.json
```
