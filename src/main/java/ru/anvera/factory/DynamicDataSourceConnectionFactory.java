package ru.anvera.factory;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.anvera.models.enums.DbType;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
public class DynamicDataSourceConnectionFactory {

  public DataSource createDataSource(String dbTypeString, String url, String username, String password) {
    HikariDataSource dataSource = new HikariDataSource();

    DbType dbType;

    try {
      dbType = DbType.valueOf(dbTypeString.trim().toLowerCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("data source type is not supported: " + dbTypeString);
    }

    switch (dbType) {
      case POSTGRES:
        dataSource.setDriverClassName(dbType.getDriverClassName());
        dataSource.addDataSourceProperty("currentSchema", "public");
        break;

      case CLICKHOUSE:
        dataSource.setDriverClassName(dbType.getDriverClassName());
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
