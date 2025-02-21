package ru.anvera.repos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.anvera.models.entity.User;


@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {

  private final JdbcTemplate jdbcTemplate;

  private final RowMapper<User> rowMapper = (rs, rowNum) -> new User(
      rs.getLong("id"),
      rs.getString("username"),
      rs.getString("email"),
      rs.getString("password"),
      rs.getLong("project_id"),
      rs.getBoolean("is_active")
  );

  public User findById(Long id) {
    String sql = "SELECT * FROM users WHERE id = ?";
    try {
      return jdbcTemplate.queryForObject(sql, rowMapper, id);
    } catch (EmptyResultDataAccessException e) {
      log.warn("User not found by id: {}", id);
      return null;
    }
  }

  public User findByUsername(String username) {
    try {
      String sql = "SELECT * FROM users WHERE username = ?";
      return jdbcTemplate.queryForObject(sql, rowMapper, username);
    } catch (EmptyResultDataAccessException e) {
      throw new RuntimeException("user not found by username: " + username);
    }
  }

  public Long save(User user) {
    String sql = "INSERT INTO users (username, email, password, project_id, is_active) " +
        "VALUES (?, ?, ?, ?, ?) RETURNING id";
    return jdbcTemplate.queryForObject(
        sql,
        new Object[]{
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            user.getProjectId(),
            user.getIsActive()
        },
        Long.class
    );
  }

  public int update(User user) {
    String sql = "UPDATE users SET username = ?, email = ?, password = ?, project_id = ?, is_active = ? " +
        "WHERE id = ?";
    return jdbcTemplate.update(sql,
        user.getUsername(),
        user.getEmail(),
        user.getPassword(),
        user.getProjectId(),
        user.getIsActive(),
        user.getId()
    );
  }

  public int deleteById(Long id) {
    String sql = "DELETE FROM users WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }
}
