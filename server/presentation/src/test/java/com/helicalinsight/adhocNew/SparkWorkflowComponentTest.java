package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.DatabaseMetadataProvider;
import com.helicalinsight.adhoc.SparkWorkflowComponent;
import com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.datasource.managed.JdbcQueryExecutor;
import com.helicalinsight.datasource.managed.SparkJdbcExecutor;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SparkWorkflowComponentTest {

	@Test(expected = IncompleteFormDataException.class)
	public void ut_a1_test_executeComponent() {
		SparkWorkflowComponent component = new SparkWorkflowComponent();
		JsonObject obj = new JsonObject();
		JsonObject parameters = new JsonObject();

		obj.add("parameters", parameters);
		String jsonFormDataString = obj.toString();
		component.executeComponent(jsonFormDataString);
	}

	@Test
	public void ut_a2_test_executeComponent() {
		SparkWorkflowComponent component = new SparkWorkflowComponent();
		JsonObject obj = new JsonObject();
		String jsonFormDataString = obj.toString();

		try (MockedConstruction<DatabaseMetadataProvider> mockedConstruction = mockConstruction(
				DatabaseMetadataProvider.class, (mock, context) -> {
					when(mock.executeComponent(anyString())).thenReturn("checkString");
				})) {
			String executeComponent = component.executeComponent(jsonFormDataString);
			assertEquals("checkString", executeComponent);
		}
	}

	@Test
	public void ut_a3_test_executeComponent() throws SQLException {
		SparkWorkflowComponent component = new SparkWorkflowComponent();
		JsonObject obj = new JsonObject();
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", "true");
		obj.add("parameters", parameters);
		obj.addProperty("type", "type");
		obj.addProperty("classifier", "classifier");
		String jsonFormDataString = obj.toString();

		JsonObject details = new JsonObject();
		JsonArray catalogs = new JsonArray();
		details.add("catalogs", catalogs);

		DriverConnection driverConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);

		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
						.thenReturn(details);

				mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(JsonObject.class), anyString()))
						.thenReturn(driverConnection);

				String executeComponent = component.executeComponent(jsonFormDataString);
				assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().has("metadata"));
			}
		}

	}

	@Test
	public void ut_a4_test_executeComponent() throws SQLException {
		SparkWorkflowComponent component = new SparkWorkflowComponent();
		JsonObject obj = new JsonObject();
		JsonObject parameters = new JsonObject();
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("catalog", "catalog");
		JsonArray schemas = new JsonArray();
		json.add("schemas", schemas);
		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchCatalogs", "true");
		parameters.addProperty("fetchTables", "true");
		obj.add("parameters", parameters);
		obj.addProperty("type", "type");
		obj.addProperty("classifier", "classifier");
		String jsonFormDataString = obj.toString();

		JsonObject details = new JsonObject();
		JsonArray catalogs = new JsonArray();
		details.add("catalogs", catalogs);

		DriverConnection driverConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);

		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
						.thenReturn(details);

				mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(JsonObject.class), anyString()))
						.thenReturn(driverConnection);

				String executeComponent = component.executeComponent(jsonFormDataString);
				assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().has("metadata"));
			}
		}

	}
	
	@Test(expected = EfwServiceException.class)
	public void ut_a5_test_executeComponent() throws SQLException {
		SparkWorkflowComponent component = new SparkWorkflowComponent();
		JsonObject obj = new JsonObject();
		JsonObject parameters = new JsonObject();
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("catalog", "catalog");
		json.add("schemas", null);
		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchCatalogs", "true");
		parameters.addProperty("fetchTables", "true");
		obj.add("parameters", parameters);
		obj.addProperty("type", "type");
		obj.addProperty("classifier", "classifier");
		String jsonFormDataString = obj.toString();

		JsonObject details = new JsonObject();
		JsonArray catalogs = new JsonArray();
		details.add("catalogs", catalogs);

		DriverConnection driverConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);

		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
						.thenReturn(details);

				mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(JsonObject.class), anyString()))
						.thenReturn(driverConnection);

				 component.executeComponent(jsonFormDataString);
				
			}
		}

	}

	@Test
	public void ut_a6_test_executeComponent() throws SQLException {
		SparkWorkflowComponent component = new SparkWorkflowComponent();
		JsonObject obj = new JsonObject();
		JsonObject parameters = new JsonObject();
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject aSchema = new JsonObject();
		aSchema.addProperty("name", "schemaName");
		JsonArray tables = new JsonArray();
		tables.add("tableName");
		aSchema.add("tables", tables);
		schemas.add(aSchema);
		json.add("schemas", schemas);
		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchCatalogs", "true");
		parameters.addProperty("fetchColumns", "true");
		obj.add("parameters", parameters);
		obj.addProperty("type", "type");
		obj.addProperty("classifier", "classifier");
		String jsonFormDataString = obj.toString();

		JsonObject details = new JsonObject();
		JsonArray catalogs = new JsonArray();
		details.add("catalogs", catalogs);
		JsonArray metadata = new JsonArray();
		JsonObject metadataJson = new JsonObject();
		JsonObject indexElement = new JsonObject();
		indexElement.addProperty("name", "ColumnName");
		indexElement.addProperty("type", "ColumnType");
		metadataJson.add("1", indexElement);
		metadata.add(metadataJson);
		details.add("metadata", metadata);

		DriverConnection driverConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);

		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				try(MockedConstruction<SparkJdbcExecutor> mockedConstruction = mockConstruction(SparkJdbcExecutor.class,(mock,context)->{
					when(mock.executeSql()).thenReturn(details);
				})){
					
				
				mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
						.thenReturn(details);

				mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(JsonObject.class), anyString()))
						.thenReturn(driverConnection);

				String executeComponent = component.executeComponent(jsonFormDataString);
				assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().has("metadata"));
				}
			}
		}

	}

	@Test
	public void ut_a7_test_executeComponent() throws SQLException {
		SparkWorkflowComponent component = new SparkWorkflowComponent();
		JsonObject obj = new JsonObject();
		JsonObject parameters = new JsonObject();
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject aSchema = new JsonObject();
		JsonArray tables = new JsonArray();
		tables.add("tableName");
		aSchema.add("tables", tables);
		schemas.add(aSchema);
		json.add("schemas", schemas);
		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchCatalogs", "true");
		parameters.addProperty("fetchColumns", "true");
		obj.add("parameters", parameters);
		obj.addProperty("type", "type");
		obj.addProperty("classifier", "classifier");
		String jsonFormDataString = obj.toString();

		JsonObject details = new JsonObject();
		JsonArray catalogs = new JsonArray();
		details.add("catalogs", catalogs);
		JsonArray metadata = new JsonArray();
		JsonObject metadataJson = new JsonObject();
		JsonObject indexElement = new JsonObject();
		indexElement.addProperty("name", "ColumnName");
		indexElement.addProperty("type", "ColumnType");
		metadataJson.add("1", indexElement);
		metadata.add(metadataJson);
		details.add("metadata", metadata);

		DriverConnection driverConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);

		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				try(MockedConstruction<SparkJdbcExecutor> mockedConstruction = mockConstruction(SparkJdbcExecutor.class,(mock,context)->{
					when(mock.executeSql()).thenReturn(details);
				})){
					
				
				mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
						.thenReturn(details);

				mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(JsonObject.class), anyString()))
						.thenReturn(driverConnection);

				String executeComponent = component.executeComponent(jsonFormDataString);
				assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().has("metadata"));
				}
			}
		}

	}

	@Test(expected = EfwServiceException.class)
	public void ut_a8_test_executeComponent() throws SQLException {
		SparkWorkflowComponent component = new SparkWorkflowComponent();
		JsonObject obj = new JsonObject();
		JsonObject parameters = new JsonObject();
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject aSchema = new JsonObject();
		JsonArray tables = new JsonArray();
		tables.add("tableName");
		aSchema.add("tables", tables);
		schemas.add(aSchema);
		json.add("schemas", schemas);
		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchCatalogs", "true");
		parameters.addProperty("fetchColumns", "true");
		obj.add("parameters", parameters);
		obj.addProperty("type", "type");
		obj.addProperty("classifier", "classifier");
		String jsonFormDataString = obj.toString();

		JsonObject details = new JsonObject();
		JsonArray catalogs = new JsonArray();
		details.add("catalogs", catalogs);
		JsonArray metadata = new JsonArray();
		JsonObject metadataJson = new JsonObject();
		JsonObject indexElement = new JsonObject();
		indexElement.addProperty("name", "ColumnName");
		indexElement.addProperty("type", "ColumnType");
		metadataJson.add("1", indexElement);
		metadata.add(metadataJson);
		details.add("metadata", metadata);

		DriverConnection driverConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);

		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				try(MockedConstruction<SparkJdbcExecutor> mockedConstruction = mockConstruction(SparkJdbcExecutor.class,(mock,context)->{
					when(mock.executeSql()).thenThrow(new SQLException());
				})){
					
				
				mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
						.thenReturn(details);

				mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(JsonObject.class), anyString()))
						.thenReturn(driverConnection);

				String executeComponent = component.executeComponent(jsonFormDataString);
				assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().has("metadata"));
				}
			}
		}

	}


    @Test
	public void ut_a9_test_escape() {
		String escape = SparkWorkflowComponent.escape("helical/insight");
		assertEquals("helical//insight",escape);
	}
    @Test
	public void ut_b1_test_escape() {
		String escape = SparkWorkflowComponent.escape("helical\\insight");
		assertEquals("helical\\\\insight",escape);
	}

    @Test
    public void ut_b2_test_addAllTablesSpark() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	SparkWorkflowComponent component = new SparkWorkflowComponent();
    	
    	Method method = SparkWorkflowComponent.class.getDeclaredMethod("addAllTablesSpark", Connection.class,JsonArray.class,
    					String.class,JsonArray.class,JsonObject.class,JsonArray.class);
    	method.setAccessible(true);
    	Connection connection = mock(Connection.class);
    	JsonArray allCatalogs = new JsonArray();
    	JsonArray schemas = new JsonArray();
    	JsonObject aSchema = new JsonObject();
    	aSchema.addProperty("name", "schema_name");
    	;
    	schemas.add(aSchema);
    	JsonObject singleCatalog = new JsonObject();
    	JsonArray allSchemas = new JsonArray();
    	method.invoke(component, connection,allCatalogs,"catalog",schemas,singleCatalog,allSchemas);
    }
    
    @Test
    public void ut_b3_test_addAllTablesSpark() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
    	SparkWorkflowComponent component = new SparkWorkflowComponent();
    	
    	Method method = SparkWorkflowComponent.class.getDeclaredMethod("addAllTablesSpark", Connection.class,JsonArray.class,
    					String.class,JsonArray.class,JsonObject.class,JsonArray.class);
    	method.setAccessible(true);
    	Connection connection = mock(Connection.class);
    	JsonArray allCatalogs = new JsonArray();
    	JsonArray schemas = new JsonArray();
    	JsonObject aSchema = new JsonObject();
    	schemas.add(aSchema);
    	JsonObject singleCatalog = new JsonObject();
    	JsonArray allSchemas = new JsonArray();
    	Statement stmt = mock(Statement.class);
    	ResultSet rs = mock(ResultSet.class);
    	when(connection.createStatement()).thenReturn(stmt);
    	when(stmt.executeQuery(anyString())).thenReturn(rs);
    	when(rs.next()).thenReturn(true).thenReturn(false);
    	when(rs.getString(2)).thenReturn("");
    	method.invoke(component, connection,allCatalogs,"catalog",schemas,singleCatalog,allSchemas);
    }
    
    @Test
    public void ut_b4_test_getSchemaFromQuery() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
    	SparkWorkflowComponent component = new SparkWorkflowComponent();
    	
    	Method method = SparkWorkflowComponent.class.getDeclaredMethod("getSchemaFromQuery", Connection.class,String.class);
    	method.setAccessible(true);
    	Connection connection = mock(Connection.class);
    	
    	
    	JsonObject result = new JsonObject();
    	JsonArray data = new JsonArray();
    	JsonObject json = new JsonObject();
    	json.addProperty("databaseName", "databaseName");
    	data.add(json);
    	result.add("data", data);
    	
    	
    	Statement stmt = mock(Statement.class);
    	when(connection.createStatement()).thenReturn(stmt);
    	try(MockedConstruction<JdbcQueryExecutor> mockedConstruction = mockConstruction(JdbcQueryExecutor.class,(mock,context)->{
    		when(mock.executeSql()).thenReturn(result);
    	})){
    		Object invoke = method.invoke(component, connection,"databaseTable");
    		assertNotNull(invoke);
    	}
    	
    }
    
    @Test
    public void ut_b5_test_addSchemas() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
    	SparkWorkflowComponent component = new SparkWorkflowComponent();
    	
    	Method method = SparkWorkflowComponent.class.getDeclaredMethod("addSchemas", JsonObject.class,JsonObject.class,DatabaseMetaData.class);
    	method.setAccessible(true);
    	
    	JsonObject parameters = new JsonObject();
    	parameters.addProperty("fetchSchemas", "true");
    	JsonArray schemas = new JsonArray();
    	schemas.add("hi-ee");
    	parameters.add("schemas",schemas);
    	JsonObject metadata = new JsonObject();
    	
    	
    	DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
    	
    	try(MockedStatic<DatabaseDetails> mockedStatic = mockStatic(DatabaseDetails.class)){
    		mockedStatic.when(()-> DatabaseDetails.newRetrieveSchemas(any(DatabaseMetaData.class))).thenReturn(parameters);
    		method.invoke(component, parameters,metadata,databaseMetaData);
    	}
    	
    }
    @Test(expected = InvocationTargetException.class)
    public void ut_b6_test_isColumnsRequested() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
    	SparkWorkflowComponent component = new SparkWorkflowComponent();
    	
    	Method method = SparkWorkflowComponent.class.getDeclaredMethod("isColumnsRequested", JsonObject.class);
    	method.setAccessible(true);
    	JsonObject parameters = new JsonObject();
    	method.invoke(component, parameters);
    }
	@Test
	public void ut_b7_test_isThreadSafeToCache() {
		SparkWorkflowComponent componentTest = new SparkWorkflowComponent();
		boolean threadSafeToCache = componentTest.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
}
