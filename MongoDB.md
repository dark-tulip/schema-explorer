# Добавление MongoDB sink коннектора, из топика кафки

- В коннектор дебезиума требуется смонтировать плагин MongoSinkConnector
- локально по проекту это папка: `./docker/kafka-connectors`
- в докер композере это: `CONNECT_PLUGIN_PATH: "/kafka/connect,/usr/share/java"`

```yml
    volumes:
      - ./kafka-connectors:/usr/share/java
```

- Соединение в монгу происходит путем инициализации монго клиента, а не jdbctemplate - поэтому стандартные методы для установления соединения не сработали.
- Нужно добавить новый тип dbType=mongoDB
- Так как в документе хранится json, там нет явных метаданных о типе полей. Вся информация берется путем. Вытащить 3 документа - достать поля - определить типы.


1. Зарегистрируйте соединение с монгой

![img_3.png](img_3.png)

```
curl --location --request POST 'http://localhost:8081/connectors/register/sink?tableMappingId=2&dbTypeString=mongodb' \
--header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJpZGlWUXRuSWVPZUhTb3VJMTh1SWJPYWh5UllwUFIwNUh0N1BKUDFCSnMwIn0.eyJleHAiOjE3NDE2MzgwMzQsImlhdCI6MTc0MTYwODAzNCwianRpIjoiMTkzMTMyMDktM2JjOS00NGY1LTgyY2ItMDk1YTk2NWRjNmI5IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo3MDgwL3JlYWxtcy9hbXZlcmEiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiMTdiNzMyY2EtOTRmMy00Y2EyLWE5NTEtYzljNDJkMmU2ZGQ5IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYW12ZXJhLWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiJjNjZjNjZlNS1iNWY1LTRkODYtYjE5Ni0wNWJhNTk1Y2NkYzYiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6ODA4MSJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJkZWZhdWx0LXJvbGVzLWFtdmVyYSIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiYzY2YzY2ZTUtYjVmNS00ZDg2LWIxOTYtMDViYTU5NWNjZGM2IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJjZGMgdXNlciIsInByZWZlcnJlZF91c2VybmFtZSI6ImNkYy11c2VyMSIsImdpdmVuX25hbWUiOiJjZGMiLCJmYW1pbHlfbmFtZSI6InVzZXIiLCJlbWFpbCI6ImNkYy11c2VyMUBtYWlsLnJ1In0.Uq4GJVf89L0f6s6Yiw4NO4ey8AnjUFSbFkqb7BzZ7FzL6KrPlg0rVPyA7HeJFmWeJ14NcmJNgjA7z-p-84FUZooktDODNcgeu6oh2NUc5Te9vuV8HQQqjk7Cd4CfPJupyvR7_H4jyn_Bjz3B0h7QuiU0O3bXN6kWv_jibfkJg7DyOYCMFMHTU4Q-ogo8cHY6IUdMp61mqTTYBbho0EynkHFyTc8g-NKh0K5RrVKV5oduAPjTXqSoXtJNulT7bgmXliuSbNha9g5wtnz9zAj5n2OBJABiRMNlVdJbXMu1ekbYgbRFy-_lJLtHr093o2-oNhOAeB7BHKA7u51fbFT2Aw' \
--header 'Cookie: JSESSIONID=ACF8886F97BBE0B8C7A73E307B91A80A'
```

2. После появится коннектор в кластере кафки

![img_4.png](img_4.png)


3. Успешно зарегистрированный коннектор подхватит данные c source_db

![img_5.png](img_5.png)


## Create source connector example

```bash
curl --location 'http://localhost:8081/datasource/connection/add' \
--header 'Content-Type: application/json' \
--header 'Authorization: ••••••' \
--header 'Cookie: JSESSIONID=691F306B05045D512994A594D06B82D9' \
--data-raw '{
    "dbType": "mongodb",
    "url": "mongodb://root:example@localhost:27017",
    "username": "root",
    "password": "example",
    "isActive": "true"
}'
```

![img_6.png](img_6.png)


## Зарегистрировать маппинг из монги в постгрес

1. Создать БД и коллекцию в монге
```bash
show dbs;
use public;
db.books.insertOne({"name":"tutorials point"})
```

2. `name (in mongo) -> title (in postgres)`
```bash
curl --location 'http://localhost:8081/datasource/connection/register/table-mapping' \
--header 'Content-Type: application/json' \
--header 'Authorization: ••••••' \
--header 'Cookie: JSESSIONID=97123A0F80A535EDDD8BD5C9882602FA' \
--data '{
  "sourceDbConnectionId": 6,
  "sinkDbConnectionId": 2,
  "sourceSchemaName": "public",
  "sinkSchemaName": "public",
  "sourceTableName": "books",
  "sinkTableName": "books",
  "sourceColumnsList": ["_id", "name"],
  "sinkColumnsList": ["id", "title"],
  "transformations": null
}
'
```

![img_7.png](img_7.png)

3. Зарегистрировать source коннектор

```bash
curl --location --request POST 'http://localhost:8081/connectors/register/source?tableMappingId=2&dbTypeString=MONGODB' \
--header 'Authorization: Bearer ..' \
--header 'Cookie: JSESSIONID=17AF08FDDA235381E13A483BDA933754'
```

![img_8.png](img_8.png)


