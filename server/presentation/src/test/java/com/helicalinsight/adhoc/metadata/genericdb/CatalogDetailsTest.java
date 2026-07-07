package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonParser;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CatalogDetailsTest {

	@Test
	public void ut_a1_test_getAllCatalogNames() throws SQLException {
		CatalogDetails catalogDetails = new CatalogDetails();
		Connection connection = mock(Connection.class);
		ResultSet resultSet = mock(ResultSet.class);
		DatabaseMetaData data = mock(DatabaseMetaData.class);
		
		when(connection.getMetaData()).thenReturn(data);
		when(data.getCatalogs()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		when(resultSet.getString(anyString())).thenReturn("catalogs");
		
		String allCatalogNames = CatalogDetails.getAllCatalogNames(connection);
		assertTrue(JsonParser.parseString(allCatalogNames).getAsJsonObject().has("catalogs"));
	}
	
	@Test(expected = MetadataRetrievalException.class)
	public void ut_a2_test_getAllCatalogNames() throws SQLException {
		Connection connection = mock(Connection.class);
		ResultSet resultSet = mock(ResultSet.class);
		DatabaseMetaData data = mock(DatabaseMetaData.class);
		
		when(connection.getMetaData()).thenReturn(data);
		when(data.getCatalogs()).thenReturn(resultSet);
		when(resultSet.next()).thenThrow(new SQLException());
		when(resultSet.getString(anyString())).thenReturn("catalogs");
	    CatalogDetails.getAllCatalogNames(connection);
		
	}
	@Test
	public void ut_a3_test_getCatalogName() throws SQLException {
		Connection connection = mock(Connection.class);
		when(connection.getCatalog()).thenReturn("catalog");
		String catalogName = CatalogDetails.getCatalogName(connection);
		assertEquals("catalog",catalogName);
	}
	
	@Test(expected = MetadataRetrievalException.class)
	public void ut_a4_test_getCatalogName() throws SQLException {
		Connection connection = mock(Connection.class);
		when(connection.getCatalog()).thenThrow(new SQLException());
		CatalogDetails.getCatalogName(connection);
		
	}
}
