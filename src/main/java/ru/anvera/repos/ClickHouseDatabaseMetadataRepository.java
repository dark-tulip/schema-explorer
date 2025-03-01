package ru.anvera.repos;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ClickHouseDatabaseMetadataRepository {

  private final JdbcTemplate jdbcTemplate;

  public List<String> getSchemas() {
    String query = "SELECT name FROM system.databases;";
    return jdbcTemplate.query(query, (rs, rowNum) -> rs.getString("name"));
  }

  public List<String> getTableNames(String schema) {
    String query = "SELECT name FROM system.tables WHERE database = ?;";
    return jdbcTemplate.query(query, new Object[]{schema}, (rs, rowNum) -> rs.getString("name"));
  }

  public List<Map<String, Object>> getColumnMetadataBySchemaNameAndTableName(String schema, String table) {
    String query = "SELECT name AS column_name, " +
                   "type AS data_type, " +
                   "NULL AS is_nullable " +
                   "FROM system.columns " +
                   "WHERE database = ? " +
                   "AND table = ?;";
    return jdbcTemplate.queryForList(query, schema, table);
  }
}
