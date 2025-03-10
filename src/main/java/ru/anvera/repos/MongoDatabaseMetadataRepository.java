package ru.anvera.repos;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class MongoDatabaseMetadataRepository {

  private final MongoClient mongoClient;

  /**
   * Get all databases (equivalent to schemas in SQL)
   */
  public List<String> getDatabases() {
    MongoIterable<String> databaseNames = mongoClient.listDatabaseNames();
    return StreamSupport.stream(databaseNames.spliterator(), false).collect(Collectors.toList());
  }

  /**
   * Get all collections in a database (equivalent to tables in SQL)
   */
  public Set<String> getCollectionNames(String databaseName) {
    MongoDatabase database = mongoClient.getDatabase(databaseName);
    MongoIterable<String> collections = database.listCollectionNames();
    return StreamSupport.stream(collections.spliterator(), false).collect(Collectors.toSet());
  }

  /**
   * Get metadata from a collection (equivalent to column metadata in SQL)
   */
  public List<Map<String, Object>> getCollectionMetadata(String databaseName, String collectionName) {
    MongoDatabase database = mongoClient.getDatabase(databaseName);
    MongoCollection<Document> collection = database.getCollection(collectionName);

    // Fetch a few sample documents to infer the schema
    return StreamSupport.stream(collection.find().limit(5).spliterator(), false)
                        .map(doc -> doc.entrySet().stream()
                                       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                        .collect(Collectors.toList());
  }
}
