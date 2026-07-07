package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.DatabaseMetadataInformationProvider;
import com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.efw.exceptions.EfwdServiceException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseMetadataInformationProviderTest {

	@Test(expected = EfwdServiceException.class)
	public void ut_a1_test_executeComponent() {
		DatabaseMetadataInformationProvider provider = new DatabaseMetadataInformationProvider();
		DriverConnection databaseConnection = mock(DriverConnection.class);

		JsonObject formJson = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("provideSchemas", "true");
		formJson.addProperty("provideCatalogs", "true");

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(JsonObject.class), anyString()))
					.thenReturn(databaseConnection);
			provider.executeComponent(formJson.toString());
		}
	}

	@Test(expected = EfwdServiceException.class)
	public void ut_a2_test_executeComponent() throws SQLException {
		DatabaseMetadataInformationProvider provider = new DatabaseMetadataInformationProvider();
		DriverConnection databaseConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);

		JsonObject formJson = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("provideSchemas", "true");
		formJson.addProperty("provideCatalogs", "true");

		when(databaseConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenThrow(new SQLException());
		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(JsonObject.class), anyString()))
					.thenReturn(databaseConnection);
			provider.executeComponent(formJson.toString());
		}
	}

	@Test
	public void ut_a3_test_executeComponent() throws SQLException {
		DatabaseMetadataInformationProvider provider = new DatabaseMetadataInformationProvider();
		DriverConnection databaseConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);

		JsonObject formJson = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("provideSchemas", "true");
		formJson.addProperty("provideCatalogs", "true");

		JsonObject schema = new JsonObject();
		JsonArray schemas = new JsonArray();
		schema.add("schemas", schemas);

		JsonObject catalog = new JsonObject();
		JsonArray catalogs = new JsonArray();
		catalog.add("catalogs", catalogs);

		when(databaseConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);
		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				mockedStatic2.when(() -> DatabaseDetails.newRetrieveSchemas(databaseMetaData)).thenReturn(schema);
				mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(databaseMetaData)).thenReturn(catalog);

				mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(JsonObject.class), anyString()))
						.thenReturn(databaseConnection);
				String executeComponent = provider.executeComponent(formJson.toString());
				assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().get("schemas").getAsJsonArray()
						.isEmpty());
			}
		}
	}

	@Test
	public void ut_a4_test_executeComponent() throws SQLException {
		DatabaseMetadataInformationProvider provider = new DatabaseMetadataInformationProvider();
		DriverConnection databaseConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);

		JsonObject formJson = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("provideSchemas", "false");
		formJson.addProperty("provideCatalogs", "false");

		when(databaseConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);
		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(JsonObject.class), anyString()))
					.thenReturn(databaseConnection);
			String executeComponent = provider.executeComponent(formJson.toString());
			assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().entrySet().isEmpty());

		}
	}

	@Test
	public void ut_a5_test_executeComponent() throws SQLException {
		DatabaseMetadataInformationProvider provider = new DatabaseMetadataInformationProvider();
		DriverConnection databaseConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);

		JsonObject formJson = new JsonObject();
		formJson.addProperty("type", "type");

		when(databaseConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);
		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {

			mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(JsonObject.class), anyString()))
					.thenReturn(databaseConnection);
			String executeComponent = provider.executeComponent(formJson.toString());
			assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().entrySet().isEmpty());

		}
	}
	
	@Test
	public void ut_a6_test_isThredSafeToCache() {
		DatabaseMetadataInformationProvider provider = new DatabaseMetadataInformationProvider();
		boolean threadSafeToCache = provider.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}

}
