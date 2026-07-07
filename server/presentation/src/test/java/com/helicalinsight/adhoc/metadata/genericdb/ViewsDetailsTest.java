package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonParser;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ViewsDetailsTest {

	@Test
	public void ut_a1_test_getAllViewNames() throws SQLException {
		ViewsDetails details = new ViewsDetails();
		Connection connection = mock(Connection.class);
		Statement statement = mock(Statement.class);
		DatabaseMetaData metaData = mock(DatabaseMetaData.class);
		ResultSet resultSet = mock(ResultSet.class);
		
		when(connection.createStatement()).thenReturn(statement);
		when(connection.getMetaData()).thenReturn(metaData);
		when(metaData.getTables(null, null, null, new String[]{"VIEW"})).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		when(resultSet.getString(anyString())).thenReturn("Table");
		String allViewNames = ViewsDetails.getAllViewNames(connection);
		assertTrue(JsonParser.parseString(allViewNames).getAsJsonObject().has("views"));
		
	}
	
	@Test(expected = MetadataRetrievalException.class)
	public void ut_a2_test_getAllViewNames() throws SQLException {
		Connection connection = mock(Connection.class);
		Statement statement = mock(Statement.class);
		DatabaseMetaData metaData = mock(DatabaseMetaData.class);
		ResultSet resultSet = mock(ResultSet.class);
		
		when(connection.createStatement()).thenReturn(statement);
		when(connection.getMetaData()).thenReturn(metaData);
		when(metaData.getTables(null, null, null, new String[]{"VIEW"})).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		when(resultSet.getString(anyString())).thenThrow(new SQLException());
		ViewsDetails.getAllViewNames(connection);
		
	}
	
}
