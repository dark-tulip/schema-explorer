package ru.anvera.repos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.anvera.models.entity.DatasourceConnection;

import java.util.List;


@Slf4j
@Repository
@RequiredArgsConstructor
public class DatasourceConnectionRepository {

  private final JdbcTemplate jdbcTemplate;

  private final RowMapper<DatasourceConnection> rowMapper = (rs, rowNum) -> new DatasourceConnection(
      rs.getLong("id"),
      rs.getString("db_type"),
      rs.getString("url"),
      rs.getString("username"),
      rs.getString("password"),
      rs.getBoolean("is_active"),
      rs.getString("datasource_type"),
      rs.getLong("project_id")
  );

  public List<DatasourceConnection> findAll(Long projectId) {
    String sql = "SELECT * FROM datasource_connections " +
        " where project_id = ?";
    return jdbcTemplate.query(sql, rowMapper, projectId);
  }

  public DatasourceConnection getById(Long id, Long projectId) {
    String sql = "SELECT * FROM datasource_connections WHERE id = ? and project_id = ?";
    try {
      return jdbcTemplate.queryForObject(sql, rowMapper, id, projectId);
    } catch (EmptyResultDataAccessException e) {
      throw new RuntimeException("R4Z24WXI :: Not found by id: " + id);
    }
  }

  public Long save(DatasourceConnection connection) {
    String sql = "INSERT INTO datasource_connections " +
        "(db_type, " +
        " url, " +
        " username, " +
        " password, " +
        " project_id, " +
        " is_active, " +
        " datasource_type) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
    return jdbcTemplate.queryForObject(
        sql,
        new Object[]{
            connection.getDbType(),
            connection.getUrl(),
            connection.getUsername(),
            connection.getPassword(),
            connection.getProjectId(),
            connection.getIsActive(),
            connection.getDatasourceType()
        },
        Long.class
    );
  }

  public int update(DatasourceConnection connection) {
    String sql = "UPDATE datasource_connections " +
            " SET " +
            " db_type = ?, " +
            " url = ?, " +
            " username = ?, " +
            " password = ?, " +
            " is_active = ?, " +
            " project_id = ?, " +
            " datasource_type = ? " +
        " WHERE id = ?";
    return jdbcTemplate.update(sql,
        connection.getDbType(),
        connection.getUrl(),
        connection.getUsername(),
        connection.getPassword(),
        connection.getIsActive(),
        connection.getProjectId(),
        connection.getDatasourceType(),
        connection.getId()
    );
  }

  public int deleteById(Long id) {
    String sql = "DELETE FROM datasource_connections " +
        " WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }
}
