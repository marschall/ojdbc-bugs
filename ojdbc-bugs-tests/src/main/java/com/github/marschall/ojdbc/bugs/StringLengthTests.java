package com.github.marschall.ojdbc.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@OracleTest
public class StringLengthTests {

  @Autowired
  private DataSource dataSource;

  @Test
  void dataLength() throws SQLException {
    try (Connection connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("""
             SELECT data_length, char_col_decl_length, char_length, char_used
               FROM all_tab_cols
              WHERE data_type = 'VARCHAR2'
            """)) {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          int dataLength = resultSet.getInt("data_length");
          int charColDeclLength = resultSet.getInt("char_col_decl_length");
          int charLength = resultSet.getInt("char_length");
          String charUsed = resultSet.getString("char_used");
        }
      }
    }
  }

  @Test
  void encoding() throws SQLException {
    String output;
    try (Connection connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("""
            SELECT value
              FROM nls_database_parameters
             WHERE parameter = 'NLS_CHARACTERSET'
            """)) {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        assertTrue(resultSet.next());
        output = resultSet.getString(1);
        assertFalse(resultSet.next());
      }
    }
    assertEquals("AL32UTF8", output);
  }

  @Test
  void asciiString() throws SQLException {
    String input = "0123456789";
    assertEquals(10, input.length());
    String output;
    try (Connection connection = this.dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("""
            SELECT CAST(? AS VARCHAR2(10 char))
              FROM dual
            """)) {
      preparedStatement.setString(1, input);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        assertTrue(resultSet.next());
        output = resultSet.getString(1);
        assertFalse(resultSet.next());
      }
    }
    assertEquals(input, output);
  }

  @Test
  void ellipsis() throws SQLException {
    String input = "012345678\u2026";
    assertEquals(10, input.length());
    String output;
    try (Connection connection = this.dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("""
            SELECT CAST(? AS VARCHAR2(10 char))
              FROM dual
            """)) {
      preparedStatement.setString(1, input);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        assertTrue(resultSet.next());
        output = resultSet.getString(1);
        assertFalse(resultSet.next());
      }
    }
    assertEquals(input, output);
  }

}
