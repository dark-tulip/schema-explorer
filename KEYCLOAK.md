## Запустите кейклок
1. Создать realm
2. Создать username - установить пароль по умолчанию
3. Создать client (взять client_id) который нужен ля запроса
4. Можно обновить время жизни токена

```bash
curl --location 'http://localhost:7080/realms/amvera/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--header 'Cookie: JSESSIONID=BE0F64B482099F9660CF92714DF11262' \
--data-urlencode 'client_id=amvera-client' \
--data-urlencode 'client_secret=MiEOXl5yqmhy8nTHYB6ighVuyIgcbeky' \
--data-urlencode 'username=cdc-user1' \
--data-urlencode 'password=user1pass' \
--data-urlencode 'grant_type=password'
```
