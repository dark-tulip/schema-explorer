package ru.anvera.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectorRegistrationService {

    private static final String KAFKA_CONNECT_URL = "http://localhost:8083/connectors/";

    private final SourceConnectorConfigGenerator sourceConnectorConfigGenerator;
    private final SinkConnectorConfigGenerator   sinkConnectorConfigGenerator;

    public void registerConnector(Long tableMappingId, String connectorType) throws Exception {
        String jsonPayload;

        if (connectorType.equals("SINK")) {
            jsonPayload = sinkConnectorConfigGenerator.generateSinkConnectorConfig(tableMappingId).toString();
        } else {
            jsonPayload = sourceConnectorConfigGenerator.generateSourceConnectorConfig(tableMappingId).toString();
        }

        log.info("L3C4WMNP :: " + jsonPayload);

        // Настройка соединения
        URL url = new URL(KAFKA_CONNECT_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");

        // Отправка данных
        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonPayload.getBytes());
            os.flush();
        }

        // Получение ответа
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        // Чтение тела ответа
        StringBuilder response = new StringBuilder();
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        connection.disconnect();
    }
}
