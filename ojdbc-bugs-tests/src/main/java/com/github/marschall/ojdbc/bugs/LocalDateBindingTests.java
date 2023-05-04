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
public class LocalDateBindingTests {

  @Autowired
  private DataSource dataSource;

  @Test
  void bindDates() throws SQLException {
    LocalDate todayJud = LocalDate.now();
    Date todayJsd = Date.valueOf(todayJud);
    String result;
    Timestamp readbackDate;
    LocalDate readbakckLocalDate;
    try (Connection connection = this.dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("""
            SELECT (CASE WHEN (? = ?) THEN
                         'eq'
                    ELSE
                         'ne'
                     END) AS r, ? AS d, ? AS ld
              FROM dual
            """)) {
      preparedStatement.setDate(1, todayJsd);
      preparedStatement.setObject(2, todayJud);

      preparedStatement.setDate(3, todayJsd);
      preparedStatement.setObject(4, todayJud);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        assertTrue(resultSet.next());
        result = resultSet.getString(1);
        readbackDate = resultSet.getTimestamp(2);
        readbakckLocalDate = resultSet.getObject(3, LocalDate.class);
        assertFalse(resultSet.next());
      }
    }
    assertEquals("eq", result, () -> "sql.date: " + readbackDate + " util.date: " + readbakckLocalDate);
  }

}
