package ru.anvera.utils;

import java.net.URI;

public class ConnectorUtils {

  public static String generateTopicName(String dbName, String schemaName, String tableName) {
    return dbName + "." + schemaName + "." + tableName;
  }

  public static String extractDbNameFromUrl(String url) {
    return url.substring(url.lastIndexOf("/") + 1);
  }

  public static String extractHostname(String url) {
    return url.split("//")[1].split(":")[0];
  }

  public static String extractPort(String url) {
    try {
      URI uri = new URI(url.substring(5)); // Удаляем "jdbc:" для корректной обработки
      return String.valueOf(uri.getPort());
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid URL format: " + url, e);
    }
  }

}
