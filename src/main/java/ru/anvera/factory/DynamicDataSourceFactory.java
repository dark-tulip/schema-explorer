package ru.anvera.factory;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
public class DynamicDataSourceFactory {

  public DataSource createDataSource(String dbType, String url, String username, String password) {
    HikariDataSource dataSource = new HikariDataSource();

    switch (dbType.toLowerCase()) {
      case "postgresql":
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.addDataSourceProperty("currentSchema", "public");
        break;

      case "mysql":
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.addDataSourceProperty("useSSL", "false");
        dataSource.addDataSourceProperty("allowPublicKeyRetrieval", "true");
        dataSource.addDataSourceProperty("serverTimezone", "UTC");
        break;

      case "oracle":
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        break;

      case "sqlserver":
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        break;

      default:
        throw new IllegalArgumentException("Unsupported database type: " + dbType);
    }

    dataSource.setJdbcUrl(url);
    dataSource.setUsername(username);
    dataSource.setPassword(password);

    // Common settings for all DataSources
    dataSource.setMaximumPoolSize(10);
    dataSource.setMinimumIdle(2);

    return dataSource;
  }
}
