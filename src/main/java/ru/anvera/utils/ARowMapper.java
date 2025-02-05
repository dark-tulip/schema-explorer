package ru.anvera.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

public class ARowMapper {

  private final Gson gson = new Gson();

  protected HashMap<String, String> parseJsonToHashMap(String json) {
    return json != null
        ? gson.fromJson(json, new TypeToken<HashMap<String, String>>() {
    }.getType())
        : null;
  }

  public ARowMapper() {
  }

  public Long getLongOrNull(ResultSet rs, String column) throws SQLException {
    Long value = rs.getLong(column);
    return rs.wasNull() ? null : value;
  }

  public Double getDoubleOrNull(ResultSet rs, String column) throws SQLException {
    Double value = rs.getDouble(column);
    return rs.wasNull() ? null : value;
  }

  public Float getFloatOrNull(ResultSet rs, String column) throws SQLException {
    Float value = rs.getFloat(column);
    return rs.wasNull() ? null : value;
  }

  public Integer getIntOrNull(ResultSet rs, String column) throws SQLException {
    Integer value = rs.getInt(column);
    return rs.wasNull() ? null : value;
  }

  public LocalDateTime getLocalDateTimeOrNull(ResultSet rs, String column) throws SQLException {
    return rs.getTimestamp(column) != null ? rs.getTimestamp(column).toLocalDateTime() : null;
  }

  public LocalDate getLocalDateOrNull(ResultSet rs, String column) throws SQLException {
    return rs.getTimestamp(column) != null ? rs.getTimestamp(column).toLocalDateTime().toLocalDate() : null;
  }

  public LocalTime getLocalTimeOrNull(ResultSet rs, String column) throws SQLException {
    return rs.getTimestamp(column) != null ? rs.getTimestamp(column).toLocalDateTime().toLocalTime() : null;
  }

  public boolean isColumnExist(ResultSet rs, String column) {
    try {
      rs.findColumn(column);
      return true;
    } catch (SQLException var4) {
      return false;
    }
  }
}
