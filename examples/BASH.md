## Список установленных на кластере коннекторов
```bash
curl -X GET http://localhost:8083/connector-plugins | jq .
```

```bash
curl -X POST -H "Content-Type: application/json" \
--data @mongo-source-config.json http://localhost:8083/connectors
```
