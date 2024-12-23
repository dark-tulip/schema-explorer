package ru.anvera.repos;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
public class PostgresDatabaseMetadataRepository {

  private final JdbcTemplate jdbcTemplate;

  public List<String> getSchemas() {
    String query = "SELECT schema_name FROM information_schema.schemata;";
    return jdbcTemplate.query(query, (rs, rowNum) -> rs.getString("schema_name"));
  }

  public List<String> getTableNames(String schema) {
    String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?;";
    return jdbcTemplate.query(query, new Object[]{schema}, (rs, rowNum) -> rs.getString("table_name"));
  }

  public List<Map<String, Object>> getColumnMetadataBySchemaNameAndTableName(String schema, String table) {
    String query = "SELECT column_name, data_type, is_nullable " +
        "FROM information_schema.columns " +
        "WHERE table_schema = ? AND table_name = ?;";
    return jdbcTemplate.queryForList(query, schema, table);
  }
}
