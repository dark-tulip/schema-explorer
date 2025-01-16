package ru.anvera.services;

import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;


@Service
public class RestCallerService {

  public static void main(String[] args) {
    String targetUrl = "http://localhost:8083/connectors/";
    String filePath  = "postgresql-source-connector-config.json"; // Путь к файлу конфигурации

    try {
      String jsonPayload = new String(Files.readAllBytes(Paths.get(filePath)));

      URL url = new URL(targetUrl);

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Accept", "application/json");
      connection.setRequestProperty("Content-Type", "application/json");

      // Отправляем запрос
      try (OutputStream os = connection.getOutputStream()) {
        os.write(jsonPayload.getBytes());
        os.flush();
      }

      // Получаем ответ
      int responseCode = connection.getResponseCode();
      System.out.println("Response Code: " + responseCode);
      System.out.println("Response Message: " + connection.getResponseMessage());

      // Читаем тело ответа (если необходимо)
      if (responseCode == 200 || responseCode == 201) {
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()))) {
          String line;
          while ((line = reader.readLine()) != null) {
            System.out.println(line);
          }
        }
      } else {
        System.out.println("Failed to call REST service. Response Code: " + responseCode);
      }

      connection.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
