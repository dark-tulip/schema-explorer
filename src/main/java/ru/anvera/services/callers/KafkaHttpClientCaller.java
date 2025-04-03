package ru.anvera.services.callers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static ru.anvera.configs.KafkaBrocerConfigs.KAFKA_CONNECT_URL;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaHttpClientCaller {
  private final RestTemplate restTemplate = new RestTemplate();

  public void callRegisterNewConnector(String jsonPayload) throws IOException {
    URL url = new URL(KAFKA_CONNECT_URL);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setDoOutput(true);
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Accept", "application/json");
    connection.setRequestProperty("Content-Type", "application/json");

    // call
    try (OutputStream os = connection.getOutputStream()) {
      os.write(jsonPayload.getBytes());
      os.flush();

      StringBuilder response = new StringBuilder();
      try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
      }
      log.info("V62NH6IY :: response of connector registration: " + response);
      connection.disconnect();
    }
  }

  public void delete(String connectorName) throws InterruptedException {
    log.info("Deleting existing connector: {}", connectorName);
    restTemplate.delete(KAFKA_CONNECT_URL + "/" + connectorName);
    Thread.sleep(2000); // brief wait for cleanup
  }

  public boolean connectorExists(String name) {
    try {
      restTemplate.getForEntity(KAFKA_CONNECT_URL + "/" + name, String.class);
      return true;
    } catch (HttpClientErrorException.NotFound e) {
      return false;
    }
  }
}
