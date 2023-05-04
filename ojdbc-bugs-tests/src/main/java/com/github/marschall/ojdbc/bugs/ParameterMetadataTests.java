package com.github.marschall.ojdbc.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@OracleTest
public class ParameterMetadataTests {

  @Autowired
  private DataSource dataSource;

  @Test
  void getParameterMetaData() throws SQLException {
    int parameterType;
    try (Connection connection = this.dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("""
            SELECT /* t */ 1
              FROM dual
             WHERE dummy = ?
            """)) {
      ParameterMetaData metaData = preparedStatement.getParameterMetaData();
      parameterType = metaData.getParameterType(1);
    }
    assertEquals(Types.VARCHAR, parameterType);
  }

}
