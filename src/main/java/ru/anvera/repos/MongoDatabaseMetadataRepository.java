package ru.anvera.repos;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.*;
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

    List<Document> sampleDocs = collection.find().limit(2).into(new ArrayList<>());

    if (sampleDocs.isEmpty()) {
      return Collections.emptyList(); // Return empty if no documents exist
    }

    Set<String> fieldNames = new HashSet<>();
    sampleDocs.forEach(doc -> fieldNames.addAll(doc.keySet()));

    List<Map<String, Object>> schemaMetadata = new ArrayList<>();
    for (String fieldName : fieldNames) {
      Map<String, Object> fieldMetadata = new HashMap<>();
      fieldMetadata.put("column_name", fieldName);
      fieldMetadata.put("data_type", inferFieldType(sampleDocs, fieldName));
      fieldMetadata.put("is_nullable", isFieldNullable(sampleDocs, fieldName) ? "YES" : "NO");

      schemaMetadata.add(fieldMetadata);
    }

    return schemaMetadata;
  }

  private String inferFieldType(List<Document> documents, String fieldName) {
    for (Document doc : documents) {
      Object value = doc.get(fieldName);
      if (value != null) {
        return mapJavaTypeToDBType(value.getClass());
      }
    }
    return "unknown"; // If all values are null
  }

  private boolean isFieldNullable(List<Document> documents, String fieldName) {
    for (Document doc : documents) {
      if (doc.containsKey(fieldName) && doc.get(fieldName) != null) {
        return false; // If at least one document has a non-null value, it's not nullable
      }
    }
    return true;
  }

  private String mapJavaTypeToDBType(Class<?> clazz) {
    if (clazz == String.class) return "string";
    if (clazz == Integer.class || clazz == Long.class || clazz == Short.class) return "bigint";
    if (clazz == Double.class || clazz == Float.class) return "decimal";
    if (clazz == Boolean.class) return "boolean";
    if (clazz == Date.class) return "timestamp";
    return "object"; // Default case for nested objects or unknown types
  }
}
