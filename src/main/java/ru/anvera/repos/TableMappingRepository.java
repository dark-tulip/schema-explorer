package ru.anvera.repos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.anvera.models.entity.TableMapping;
import ru.anvera.models.rowmappers.TableMappingRowMapper;

import java.util.List;

@Repository
public class TableMappingRepository {

  private final JdbcTemplate jdbcTemplate;

  public TableMappingRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public TableMapping getById(Long id) {
    String sql = "SELECT * " +
        " FROM table_mapping " +
        " WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, new TableMappingRowMapper(), id);
  }

  public List<TableMapping> getAll() {
    String sql = "SELECT * FROM table_mapping ";
    return jdbcTemplate.query(sql, new TableMappingRowMapper());
  }

  public Long insert(TableMapping mapping) {
    String sql = "INSERT INTO table_mapping (" +
        " source_db_connection_id, " +
        " sink_db_connection_id, " +
        " source_schema_name, " +
        " sink_schema_name, " +
        " source_table, " +
        " sink_table, " +
        " source_to_sink_column_mapping, " +
        " transformations) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?::jsonb, ?::jsonb) RETURNING id";

    return jdbcTemplate.queryForObject(sql, new Object[]{
        mapping.getSourceDbConnectionId(),
        mapping.getSinkDbConnectionId(),
        mapping.getSourceSchemaName(),
        mapping.getSinkSchemaName(),
        mapping.getSourceTable(),
        mapping.getSinkTable(),
        toJson(mapping.getSourceToSinkColumnNameMapping()),
        toJson(mapping.getTransformations())
    }, Long.class);
  }

  public int update(TableMapping mapping) {
    String sql = "UPDATE table_mapping SET " +
        " source_schema_name = ?, " +
        " sink_schema_name = ?, " +
        " source_table = ?, " +
        " sink_table = ?, " +
        " source_to_sink_column_mapping = ?, " +
        " transformations = ? " +
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
    String sql = "DELETE FROM table_mapping " +
        " WHERE id = ?";
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
