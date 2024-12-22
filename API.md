## Получить схему данных
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

