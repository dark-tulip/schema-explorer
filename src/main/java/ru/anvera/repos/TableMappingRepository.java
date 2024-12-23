package ru.anvera.repos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.anvera.models.entity.TableMapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

@Repository
public class TableMappingRepository {

  private final JdbcTemplate jdbcTemplate;

  public TableMappingRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private final RowMapper<TableMapping> rowMapper = (rs, rowNum) -> {
    TableMapping mapping = new TableMapping();
    mapping.setId(rs.getLong("id"));
    mapping.setSourceSchemaName(rs.getString("source_schema_name"));
    mapping.setSinkSchemaName(rs.getString("sink_schema_name"));
    mapping.setSourceTable(rs.getString("source_table"));
    mapping.setSinkTable(rs.getString("sink_table"));

    // Deserialize JSON to HashMap for column mapping
    String sourceToSinkJson    = rs.getString("source_to_sink_column_mapping");
    String transformationsJson = rs.getString("transformations");

    try {
      mapping.setSourceToSinkColumnNameMapping(
          sourceToSinkJson != null ? new ObjectMapper().readValue(sourceToSinkJson, HashMap.class) : null
      );
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    try {
      mapping.setTransformations(
          transformationsJson != null ? new ObjectMapper().readValue(transformationsJson, HashMap.class) : null
      );
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    return mapping;
  };

  public TableMapping getById(Long id) {
    String sql = "SELECT * FROM table_mapping WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, rowMapper, id);
  }

  public int insert(TableMapping mapping) {
    String sql = "INSERT INTO table_mapping (" +
        "source_schema_name, " +
        "sink_schema_name, " +
        "source_table, " +
        "sink_table, " +
        "source_to_sink_column_mapping, " +
        "transformations) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    return jdbcTemplate.update(sql,
        mapping.getSourceSchemaName(),
        mapping.getSinkSchemaName(),
        mapping.getSourceTable(),
        mapping.getSinkTable(),
        toJson(mapping.getSourceToSinkColumnNameMapping()),
        toJson(mapping.getTransformations())
    );
  }

  public int update(TableMapping mapping) {
    String sql = "UPDATE table_mapping SET " +
        "source_schema_name = ?, " +
        "sink_schema_name = ?, " +
        "source_table = ?, " +
        "sink_table = ?, " +
        "source_to_sink_column_mapping = ?, " +
        "transformations = ? " +
        "WHERE id = ?";

    return jdbcTemplate.update(sql,
        mapping.getSourceSchemaName(),
        mapping.getSinkSchemaName(),
        mapping.getSourceTable(),
        mapping.getSinkTable(),
        toJson(mapping.getSourceToSinkColumnNameMapping()),
        toJson(mapping.getTransformations()),
        mapping.getId()
    );
  }

  public int deleteById(Long id) {
    String sql = "DELETE FROM table_mapping WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }

  private String toJson(Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (Exception e) {
      throw new RuntimeException("Error converting object to JSON", e);
    }
  }
}
