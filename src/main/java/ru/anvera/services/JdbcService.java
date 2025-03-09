package ru.anvera.services;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class JdbcService {

    private final String url = "jdbc:postgresql://localhost:5432/source_db";
    private final String username = "user1";
    private final String password = "user1pwd";

    public List<List<String>> getRows(int limit) {
        List<List<String>> rows = new ArrayList<>();

        String query = "SELECT * FROM books LIMIT ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, limit);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Извлекаем заголовки столбцов
                List<String> columnNames = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    columnNames.add(metaData.getColumnName(i)); // Получаем имя столбца
                }
                rows.add(columnNames); // Добавляем заголовки в первую строку

                while (resultSet.next()) {
                    List<String> row = new ArrayList<>();
                    for (int i = 1; i <= columnCount; ++i) {
                        row.add(resultSet.getString(i));
                    }
                    rows.add(row);
                }
                for (List<String> row: rows) {
                    for (String word: row) {
                        System.out.println(word + " ");
                    }
                    System.out.println();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }
}
