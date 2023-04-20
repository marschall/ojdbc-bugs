package com.github.marschall.ojdbc.bugs;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import oracle.jdbc.OracleConnection;

@OracleTest
class ColumnMetadataTests {

  @Autowired
  private DataSource dataSource;

  @Test
  void databaseMetaData_getColumns() throws SQLException {
    try (Connection connection = this.dataSource.getConnection()) {
      connection.unwrap(OracleConnection.class).setRemarksReporting(true);
      DatabaseMetaData metaData = connection.getMetaData();
      try (ResultSet columns = metaData.getColumns(null, null, null, null)) {
        while (columns.next()) {
          String tableName = columns.getString("TABLE_NAME");
          assertNotNull(tableName);
          assertDoesNotThrow(() -> columns.getString("COLUMN_DEF"), "accessing default column value");
        }
      }
    }
  }

}
