package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseDetailsTest {

	@Test
	public void ut_a1_test_getDatabaseSchemaJson() throws SQLException {
		DatabaseDetails databaseDetails = new DatabaseDetails();
		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		ResultSet result = mock(ResultSet.class);
		
		when(result.next()).thenReturn(true).thenReturn(false);
		when(result.getString(4)).thenReturn("result");
		when(databaseMetaData.getColumns(any(), any(), any(), any())).thenReturn(result);
		when(connection.getMetaData()).thenReturn(databaseMetaData);
		when(connection.getCatalog()).thenReturn("catalog");
		List<String> tableNameList = Arrays.asList("table1");
		
		try(MockedStatic<TableDetails> mockedStatic = mockStatic(TableDetails.class)){
			mockedStatic.when(()-> TableDetails.getListOfTables(any(), any(), any())).thenReturn(tableNameList);
			String databaseSchemaJson = DatabaseDetails.getDatabaseSchemaJson(connection, "sName");
			String response = JsonParser.parseString(databaseSchemaJson).getAsJsonObject().get("name").getAsString();
			assertEquals("catalog", response);
		}
	}
	@Test
	public void ut_a2_test_newRetrieveSchemas() throws SQLException {
		
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		ResultSet result = mock(ResultSet.class);
		
		String url = "https://example.com/api/resource?HI_DATABASE=resource&HI_DATABASE_CONTAINS=resource&HI_SCHEMA_CONTAINS=resource";

		when(databaseMetaData.getURL()).thenReturn(url);
		when(databaseMetaData.getSchemas()).thenReturn(result);
		when(result.next()).thenReturn(true).thenReturn(false);
		when(result.getString("TABLE_SCHEM")).thenReturn("resource");
		
		JsonObject newRetrieveSchemas = DatabaseDetails.newRetrieveSchemas(databaseMetaData);
		assertTrue(newRetrieveSchemas.getAsJsonArray("schemas").contains(new JsonPrimitive("resource")));
	}
	
	@Test
	public void ut_a3_test_newRetrieveSchemas() throws SQLException {
		
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		ResultSet result = mock(ResultSet.class);
		
		String url = "https://example.com/api/resource?HI_DATABASE=value1&HI_DATABASE_CONTAINS=value2&HI_SCHEMA_CONTAINS=value3";

		when(databaseMetaData.getURL()).thenReturn(url);
		when(databaseMetaData.getSchemas()).thenReturn(result);
		when(result.next()).thenReturn(true).thenReturn(false);
		when(result.getString("TABLE_SCHEM")).thenReturn("resource1");
		
		JsonObject newRetrieveSchemas = DatabaseDetails.newRetrieveSchemas(databaseMetaData);
		assertTrue(newRetrieveSchemas.getAsJsonArray("schemas").contains(new JsonPrimitive("resource1")));
	}
	
	@Test
	public void ut_a4_test_newRetrieveSchemas() throws SQLException {
		
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		ResultSet result = mock(ResultSet.class);
		
		String url = "https://example.com/api/resource?HI_DATABASE=value1&HI_DATABASE_CONTAINS=value2&HI_SCHEMA_CONTAINS=value3";

		when(databaseMetaData.getURL()).thenReturn(url);
		when(databaseMetaData.getSchemas()).thenThrow(new SQLException());
		when(result.next()).thenReturn(true).thenReturn(false);
		when(result.getString("TABLE_SCHEM")).thenReturn("resource1");
		
		JsonObject newRetrieveSchemas = DatabaseDetails.newRetrieveSchemas(databaseMetaData);
		assertTrue(newRetrieveSchemas.getAsJsonArray("schemas").contains(new JsonPrimitive("none")));
	}
	
	@Test
	public void ut_a5_test_addNone() {
		JsonObject responseJson = new JsonObject();
		Set<JsonObject> schemaList = new HashSet<>();
		schemaList.add(responseJson);
		DatabaseDetails.addNone(schemaList, responseJson);
	}
}
