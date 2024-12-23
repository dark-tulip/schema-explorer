package ru.anvera.repos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  private final RowMapper<DatasourceConnection> rowMapper = (rs, rowNum) -> {
    DatasourceConnection connection = new DatasourceConnection();
    connection.setId(rs.getLong("id"));
    connection.setDbType(rs.getString("db_type"));
    connection.setUrl(rs.getString("url"));
    connection.setUsername(rs.getString("username"));
    connection.setPassword(rs.getString("password"));
    connection.setIsActive(rs.getBoolean("is_active"));
    connection.setDatasourceType(rs.getString("datasource_type"));
    return connection;
  };

  public List<DatasourceConnection> findAll() {
    String sql = "SELECT * FROM datasource_connections";
    return jdbcTemplate.query(sql, rowMapper);
  }

  public DatasourceConnection getById(Long id) {
    String sql = "SELECT * FROM datasource_connections WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, rowMapper, id);
  }

  public Long save(DatasourceConnection connection) {
    String sql = "INSERT INTO datasource_connections " +
        "(db_type, url, username, password, is_active, datasource_type) " +
        "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
    return jdbcTemplate.queryForObject(
        sql,
        new Object[]{
            connection.getDbType(),
            connection.getUrl(),
            connection.getUsername(),
            connection.getPassword(),
            connection.getIsActive(),
            connection.getDatasourceType()
        },
        Long.class
    );
  }

  public int update(DatasourceConnection connection) {
    String sql = "UPDATE datasource_connections SET db_type = ?, url = ?, username = ?, password = ?, is_active = ?, datasource_type = ? " +
        "WHERE id = ?";
    return jdbcTemplate.update(sql, connection.getDbType(), connection.getUrl(), connection.getUsername(),
        connection.getPassword(), connection.getIsActive(), connection.getDatasourceType(), connection.getId());
  }

  public int deleteById(Long id) {
    String sql = "DELETE FROM datasource_connections WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }
}
