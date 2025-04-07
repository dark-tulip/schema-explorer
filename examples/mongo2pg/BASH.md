```bash
curl -X POST -H "Content-Type: application/json" \
     --data @mongo-source.json \
     http://localhost:8083/connectors
```


```bash
curl -X POST -H "Content-Type: application/json" \
     --data @postgres-sink.json \
     http://localhost:8083/connectors
```


```bash
curl -X DELETE http://localhost:8083/connectors/mongo-source-connector
```


```bash
curl -X DELETE http://localhost:8083/connectors/pg-jdbc-sink
```


поиск для source монги

{
"connector.class": "io.debezium.connector.mongodb.MongoDbConnector",
"topic.prefix": "mongo",
"collection.include.list": "public.books",
"mongodb.connection.string": "mongodb://root:example@mongodb:27017/?replicaSet=rs0",
"name": "mongo-source-connector",
"capture.mode": "change_streams"
}