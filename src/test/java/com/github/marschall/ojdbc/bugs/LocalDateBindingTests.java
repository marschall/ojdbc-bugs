package com.github.marschall.ojdbc.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@OracleTest
class LocalDateBindingTests {

  @Autowired
  private DataSource dataSource;

  @Test
  void bindDates() throws SQLException {
    LocalDate todayJud = LocalDate.now();
    Date todayJsd = Date.valueOf(todayJud);
    String result;
    try (Connection connection = this.dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("""
            SELECT (CASE WHEN (? = ?) THEN
                         'eq'
                    ELSE
                         'ne'
                     END) AS r
              FROM dual
            """)) {
      preparedStatement.setTimestamp(1, new Timestamp(todayJsd.getTime()));
      preparedStatement.setObject(2, todayJud);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        assertTrue(resultSet.next());
        result = resultSet.getString(1);
        assertFalse(resultSet.next());
      }
    }
    assertEquals("eq", result);
  }

}
