# Добавление sink коннектора в Кликхаус, из топика кафки

## 1. Добавить соединение с кликхаусом

```bash
curl --location 'http://localhost:8081/datasource/connection/add' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIzRmhLOWxOejRCdXJ6aW1nU0lORi1PWUlkaUtIME1RSzBseDY0T1Y0U2JNIn0.eyJleHAiOjE3NDA4NDc0MjcsImlhdCI6MTc0MDg0NzEyNywianRpIjoiZjhjMzlkM2YtZDNiOS00NjA1LWE1MGEtM2M1OGJlYTFkMDA2IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo3MDgwL3JlYWxtcy9hbXZlcmEiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiOTE0YmE1NzItMGI0MC00ZmZjLTkzNDUtYzY1YTU0NjBhYTZjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiY2RjLWNsaWVudDEiLCJzZXNzaW9uX3N0YXRlIjoiZWUwMzQyYjEtMjljMC00OTRlLTkxN2UtYWVmMzIwM2Q4MTI4IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODEiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1hbXZlcmEiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6ImVlMDM0MmIxLTI5YzAtNDk0ZS05MTdlLWFlZjMyMDNkODEyOCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiY2RjLXVzZXIxIGNkYy11c2VyMSIsInByZWZlcnJlZF91c2VybmFtZSI6ImNkYy11c2VyMSIsImdpdmVuX25hbWUiOiJjZGMtdXNlcjEiLCJmYW1pbHlfbmFtZSI6ImNkYy11c2VyMSIsImVtYWlsIjoiY2RjLXVzZXIxQGdtYWlsLmNvbSJ9.SncnJOp0Oc7bCRVj0CazDjLCyhWcIJ6lbnkkLP0VVu01cijik5zy9orMEa7cfo0BY0SG2CtnlwkXSV0dPwEIL5RimvZdnmCTJkqVq4p7YnBcK0OTFR-RSk4fJI1DaG5xHy06nWcza99rtVYrQsnaYU910pxuWRH2BsOD-t78kKuO7KBA5Bs6X2I2v_26I5_0UpNFwgx2zOlwWIAX1Ysedn2X5yZ5HUjqLx9aIkDfFD8Tf1J23XQ3DqtXRmOVhnA1nnGE8tRxxfY115c1g0vZB7xvP5w2Bg3Lo0UODp_bSZehqnet0tl5DpihobyJic9nxgAqhCkT5eEFt09EDqQnKg' \
--header 'Cookie: JSESSIONID=295B829EBEE005E3854EE5560430C885' \
--data '{
    "dbType": "clickhouse",
    "url": "jdbc:clickhouse://localhost:8123/testdb",
    "username": "clickuser",
    "password": "clickpass",
    "isActive": "true"
}'
```

<img width="942" alt="Pasted Graphic 2" src="https://github.com/user-attachments/assets/a57fbb6e-47eb-4c7d-be9f-6e0b628c9f0d" />


## 2. Запись появилась в БД и к ней можно подсоединиться

￼<img width="1440" alt="Pasted Graphic" src="https://github.com/user-attachments/assets/3a2392fa-a98d-4ba8-9eb4-280d33d408d6" />


## 3. Проверить информации о соединении можно со след АПИ

```bash
curl --location 'http://localhost:8081/datasource/connection/metadata/info?datasourceConnectionId=4' \
--header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIzRmhLOWxOejRCdXJ6aW1nU0lORi1PWUlkaUtIME1RSzBseDY0T1Y0U2JNIn0.eyJleHAiOjE3NDA4NDgwMjAsImlhdCI6MTc0MDg0NzcyMCwianRpIjoiMWEzMzhjMTQtOWEzYi00MzUxLWE3YjEtZjMxODliZWEyNWVjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo3MDgwL3JlYWxtcy9hbXZlcmEiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiOTE0YmE1NzItMGI0MC00ZmZjLTkzNDUtYzY1YTU0NjBhYTZjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiY2RjLWNsaWVudDEiLCJzZXNzaW9uX3N0YXRlIjoiOTI4MTJlZmMtYWIzMS00MWM1LTkzYTEtNjFjNmE3ZDUxNzIyIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODEiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1hbXZlcmEiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6IjkyODEyZWZjLWFiMzEtNDFjNS05M2ExLTYxYzZhN2Q1MTcyMiIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiY2RjLXVzZXIxIGNkYy11c2VyMSIsInByZWZlcnJlZF91c2VybmFtZSI6ImNkYy11c2VyMSIsImdpdmVuX25hbWUiOiJjZGMtdXNlcjEiLCJmYW1pbHlfbmFtZSI6ImNkYy11c2VyMSIsImVtYWlsIjoiY2RjLXVzZXIxQGdtYWlsLmNvbSJ9.aUdm1bOFQvIt_cDNVH1Oc-wk5BxcK6enJ3BNYhAepMWJUmPlUY3IqNGr_1YLN8MuNnWjwywcgEcyZP-mH0oY5vc7SX969FfafpOUiWKybJOF_pD2hxbmIzZcgu2hcuBPFoHhkVMOT70pjqjuxi341AL5oS7GiIkxPK21cVCSkjRfVfkFIil0lm8eR2iOxdKDxsbky6PybKuDUVxP6TLpSG3hPHIuZbLP-pp1W7azu4vYv8KZgbCAq9V80pgAe8l7VXrAjZunoC0dTcxeTEhCFamZ880X5nNav__j6PvaZK7H1wOggpkZfbZxOFxKbwlNMsY7E69WDfEPVguvAKb7ZA' \
--header 'Cookie: JSESSIONID=295B829EBEE005E3854EE5560430C885'
```
￼
<img width="940" alt="Pasted Graphic 4" src="https://github.com/user-attachments/assets/6a2012d0-9fde-4fd2-921b-e1f0f4a087f2" />


## 4. Создать маппинг (сопоставление полей)

```bash
curl --location 'http://localhost:8081/datasource/connection/register/table-mapping' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIzRmhLOWxOejRCdXJ6aW1nU0lORi1PWUlkaUtIME1RSzBseDY0T1Y0U2JNIn0.eyJleHAiOjE3NDA4NTMwNDYsImlhdCI6MTc0MDg1Mjc0NiwianRpIjoiZTMzZDQwNjQtOWQxZC00ZjZmLThmMTctNTljODEzMjU1NjRjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo3MDgwL3JlYWxtcy9hbXZlcmEiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiOTE0YmE1NzItMGI0MC00ZmZjLTkzNDUtYzY1YTU0NjBhYTZjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiY2RjLWNsaWVudDEiLCJzZXNzaW9uX3N0YXRlIjoiOTBmMzI0MmQtN2M3Zi00MTU0LTlhNzUtYjg2NzNiN2I4MGRkIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODEiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1hbXZlcmEiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6IjkwZjMyNDJkLTdjN2YtNDE1NC05YTc1LWI4NjczYjdiODBkZCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiY2RjLXVzZXIxIGNkYy11c2VyMSIsInByZWZlcnJlZF91c2VybmFtZSI6ImNkYy11c2VyMSIsImdpdmVuX25hbWUiOiJjZGMtdXNlcjEiLCJmYW1pbHlfbmFtZSI6ImNkYy11c2VyMSIsImVtYWlsIjoiY2RjLXVzZXIxQGdtYWlsLmNvbSJ9.iM75dXHOnli2U2lqMW6vhgly-UKpiOiM0199BqyjXYYs0O13upXfzh_U4IlFsuQEA5AP-M5m3gN1i9FXSCZmzyCFtraj4vbvc5-N9mj1MZvriTfWg9hWV4KEUJYeTTApbR5k02tuUEd7PQdsqDNOpCMMnxfInfK97EU1G-yA6txmny_WWnwZltfXkNbNIwV85KQ87MEp9gyKvzOXQnr-3BVLt9Z-UL6-TKOfzj6RnyTotlQHrFrw3mF1X8jrDhRk9tiTNvpIllJ3NgBvS2XdAL7oum7O3TjCH8uuubl6k1qRNH1m37XmTkNqXfdwSJRXo-twe4eexn3DkcLiYxUEag' \
--header 'Cookie: JSESSIONID=60A8500DCEA3615B21944E1084F43918' \
--data '{
  "sourceDbConnectionId": 1,
  "sinkDbConnectionId": 4,
  "sourceSchemaName": "public",
  "sinkSchemaName": "public",
  "sourceTableName": "books",
  "sinkTableName": "books",
  "sourceColumnsList": ["id", "title", "author", "published_year"],
  "sinkColumnsList": ["id", "title", "author", "published_year"],
  "transformations": null,
  "createNew": true
}
'
```

￼<img width="945" alt="Pasted Graphic 3" src="https://github.com/user-attachments/assets/7ab58b6d-819b-4641-8e44-be6844f1c0dc" />


## 5. Информация о зарегистрированных сопоставлениях

```bash
curl --location 'http://localhost:8081/table-mapping/info?tableMappingId=4' \
--header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIzRmhLOWxOejRCdXJ6aW1nU0lORi1PWUlkaUtIME1RSzBseDY0T1Y0U2JNIn0.eyJleHAiOjE3NDA4NTUzMjQsImlhdCI6MTc0MDg1NTAyNCwianRpIjoiYjBmMjUwNzUtYjc5Yy00MzIwLWE3YWYtZDRhMWQ4MWY3ZWVkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo3MDgwL3JlYWxtcy9hbXZlcmEiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiOTE0YmE1NzItMGI0MC00ZmZjLTkzNDUtYzY1YTU0NjBhYTZjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiY2RjLWNsaWVudDEiLCJzZXNzaW9uX3N0YXRlIjoiNDlmYzQ2OWItZDY3Yi00NWM4LWFkZmQtNzUwMjgxOWNhMjFhIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODEiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1hbXZlcmEiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6IjQ5ZmM0NjliLWQ2N2ItNDVjOC1hZGZkLTc1MDI4MTljYTIxYSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiY2RjLXVzZXIxIGNkYy11c2VyMSIsInByZWZlcnJlZF91c2VybmFtZSI6ImNkYy11c2VyMSIsImdpdmVuX25hbWUiOiJjZGMtdXNlcjEiLCJmYW1pbHlfbmFtZSI6ImNkYy11c2VyMSIsImVtYWlsIjoiY2RjLXVzZXIxQGdtYWlsLmNvbSJ9.cVWUGdIypTWDBduXuMABFHZyE59-Ba-B_AP3IDQmLFTYGllT_cTBbHxpcqh3C-oeuZ9Nogfjb8WXlqirSCq8CJy630kRSQsa0Id1g2NAHuJ_nnO_pX2_sPxN2OHN25esfdGfTIQH1Pxa582lU2K79h9cnMZF_1CyjWNKpTbDA1nNlNXpIMMcsZ2c1SDmluYP8kgEGHawjlFR0rZCvyc8tlfEfPdFFqZDPfRaEuNK-tH7xTSHEp-1xzkXe7WidUsL0QsShH3O3hXuKQv5ejtu41F8nW5huYzgnoswTsjiRn4A-97IMwscyh0GX5asGfPRSqiRP4gUUYX0w8Z1easarg' \
--header 'Cookie: JSESSIONID=F8457907FC43020F9DFFE88E18936B81'
```

￼<img width="937" alt="Pasted Graphic 5" src="https://github.com/user-attachments/assets/4cda48ad-5107-4e49-aeb2-759458811a38" />

## 6. Регистрация sink connector-a из Топика Кафки в Таблицу Кликхауса

```bash
curl --location --request POST 'http://localhost:8081/connectors/register/sink?tableMappingId=4&dbTypeString=clickhouse' \
--header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIzRmhLOWxOejRCdXJ6aW1nU0lORi1PWUlkaUtIME1RSzBseDY0T1Y0U2JNIn0.eyJleHAiOjE3NDA4NTQ5OTQsImlhdCI6MTc0MDg1NDY5NCwianRpIjoiMjljMGQxMDctMTU5NS00NjFkLWIwNDgtMWNmOWE1MmJjNTU1IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo3MDgwL3JlYWxtcy9hbXZlcmEiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiOTE0YmE1NzItMGI0MC00ZmZjLTkzNDUtYzY1YTU0NjBhYTZjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiY2RjLWNsaWVudDEiLCJzZXNzaW9uX3N0YXRlIjoiNTk4MDhlN2MtMDZiNy00ZjQxLWEwZmItYmVkNDYwZTQ2NGExIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODEiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1hbXZlcmEiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6IjU5ODA4ZTdjLTA2YjctNGY0MS1hMGZiLWJlZDQ2MGU0NjRhMSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiY2RjLXVzZXIxIGNkYy11c2VyMSIsInByZWZlcnJlZF91c2VybmFtZSI6ImNkYy11c2VyMSIsImdpdmVuX25hbWUiOiJjZGMtdXNlcjEiLCJmYW1pbHlfbmFtZSI6ImNkYy11c2VyMSIsImVtYWlsIjoiY2RjLXVzZXIxQGdtYWlsLmNvbSJ9.XOuBougH69znCDi63P3DpMVH9hwKYIwrBZhokSDfre-Bdp1EVOMfeIcP0bRqnZJyUNHcL6rxOcL-MqknvkowutfBUuROzZUleCRtabHoGb_3dzfOKW5TjXCzI1RdaSCWZejWgASzhscUvdxO2N-r882h5pr9NamA53E3WhtcMeUQMXVQKbAgmlRTX6OcRCjVr8KKei_-38Kp4tZLHDYTewN43q_I-cHkcPvWMM8oN-P_pKFO9fZvbRhoWOlmFqd6Cws3oRNzhZr1lAq0UwiSk7mlUlyxpW825KyGPMar7bIVL7_PyxPuAqInNcatGmwtT_XFqsXpO7ruMvupnpZxhQ' \
--header 'Cookie: JSESSIONID=F8457907FC43020F9DFFE88E18936B81'
```

￼<img width="941" alt="Pasted Graphic 6" src="https://github.com/user-attachments/assets/57f80c7a-e4da-4596-8100-384430886bf2" />

- АПИ Создает таблицы, матвьюху и временную таблицу в кликхаусе если не существует

<img width="753" alt="Pasted Graphic 8" src="https://github.com/user-attachments/assets/43abb3cb-e64a-42a2-a8f3-cc88c92d48a3" />

- После вызова этого АПИ - зарегистрируется консюмер с кликхауса, который подтянет данные с топика Кафки. Добавляется новый консюмер:

<img width="1437" alt="Pasted Graphic 7" src="https://github.com/user-attachments/assets/4936d189-52c2-442e-81e4-95eb49238f0a" />

- Консюмер сразу подтянет данные и вставит в таблицы

<img width="1102" alt="Pasted Graphic 9" src="https://github.com/user-attachments/assets/c1d0db99-c95e-4394-b301-d675950465fa" />

￼

Примечание:
- В кликхаусе своя стратегия регистрации коннектора
- Это не регистрация коннектора, а регистрация контрмера (встроенного по типу ENGINE = Kafka) - используем встроенное решение
