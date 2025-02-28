package ru.anvera.utils;

public class ConnectorUtils {

  public static String generateTopicName(String dbName, String schemaName, String tableName) {
    return dbName + "." + schemaName + "." + tableName;
  }

  public static String extractDbNameFromUrl(String url) {
    return url.substring(url.lastIndexOf("/") + 1);
  }

}
