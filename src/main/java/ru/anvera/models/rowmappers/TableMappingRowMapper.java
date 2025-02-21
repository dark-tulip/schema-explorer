package ru.anvera.models.rowmappers;

import org.springframework.jdbc.core.RowMapper;
import ru.anvera.models.entity.TableMapping;
import ru.anvera.utils.ARowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TableMappingRowMapper extends ARowMapper implements RowMapper<TableMapping> {

  @Override
  public TableMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
    String sourceToSinkJson    = rs.getString("source_to_sink_column_mapping");
    String transformationsJson = rs.getString("transformations");

    return new TableMapping(
        rs.getLong("id"),
        rs.getLong("source_db_connection_id"),
        rs.getLong("sink_db_connection_id"),
        rs.getString("source_schema_name"),
        rs.getString("sink_schema_name"),
        rs.getString("source_table"),
        rs.getString("sink_table"),
        rs.getLong("project_id"),
        parseJsonToHashMap(sourceToSinkJson),
        parseJsonToHashMap(transformationsJson));
  }
}

