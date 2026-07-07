package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.EfwdMetadataHandler;
import com.helicalinsight.datasource.QueryExecutor;
import com.helicalinsight.efw.utility.JsonUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EfwdMetadataHandlerTest {

	@Test
	public void ut_a1_test_handleFetchCatalog() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchSchemas", "schemas");
		parameters.addProperty("view", "");
		parameters.addProperty("fetchTables", "table");
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("fetchData", "data");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		JsonObject json = new JsonObject();
		JsonArray data = new JsonArray();
		JsonObject catlog = new JsonObject();
		catlog.addProperty("catalogs", "catalogs");
		data.add(catlog);
		json.add("data", data);
		try(MockedConstruction<QueryExecutor> mockedConstruction = mockConstruction(QueryExecutor.class,(mock,context)->{
			when(mock.getResultSet(any(Connection.class), anyString())).thenReturn(json);
		})){
			handler.handleFetchCatalog();	
		}
	}
	
	@Test
	public void ut_a2_test_handleFetchCatalog() {
		JsonObject parameters = new JsonObject();
		
		parameters.addProperty("fetchSchemas", "schemas");
		parameters.addProperty("view", "");
		parameters.addProperty("fetchTables", "table");
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("fetchData", "data");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		handler.handleFetchCatalog();	
		
	}
	
	@Test
	public void ut_a3_test_handleFetchSchema() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchSchemas", true);
		parameters.addProperty("fetchTables", "table");
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("fetchData", "data");
		parameters.addProperty("view","tree");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		JsonObject json = new JsonObject();
		JsonArray data = new JsonArray();
		JsonObject catlog = new JsonObject();
		catlog.addProperty("schemas", "schemas");
		data.add(catlog);
		json.add("data", data);
		try(MockedConstruction<QueryExecutor> mockedConstruction = mockConstruction(QueryExecutor.class,(mock,context)->{
			when(mock.getResultSet(any(Connection.class), anyString())).thenReturn(json);
		})){
			handler.handleFetchSchema();	
		}
	}
	@Test
	public void ut_a3_a1_test_handleFetchSchema() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchSchemas", true);
		parameters.addProperty("fetchTables", "table");
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("fetchData", "data");
		parameters.addProperty("view","three");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		JsonObject json = new JsonObject();
		JsonArray data = new JsonArray();
		JsonObject catlog = new JsonObject();
		catlog.addProperty("schemas", "schemas");
		data.add(catlog);
		json.add("data", data);
		try(MockedConstruction<QueryExecutor> mockedConstruction = mockConstruction(QueryExecutor.class,(mock,context)->{
			when(mock.getResultSet(any(Connection.class), anyString())).thenReturn(json);
		})){
			handler.handleFetchSchema();	
		}
	}
	@Test
	public void ut_a3_a2_test_handleFetchSchema() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchSchemas", true);
		parameters.addProperty("fetchTables", "table");
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("fetchData", "data");
		
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		JsonObject json = new JsonObject();
		JsonArray data = new JsonArray();
		JsonObject catlog = new JsonObject();
		catlog.addProperty("schemas", "schemas");
		data.add(catlog);
		json.add("data", data);
		try(MockedConstruction<QueryExecutor> mockedConstruction = mockConstruction(QueryExecutor.class,(mock,context)->{
			when(mock.getResultSet(any(Connection.class), anyString())).thenReturn(json);
		})){
			handler.handleFetchSchema();	
		}
	}
	@Test
	public void ut_a4_test_handleFetchSchema() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchSchemas", true);
		parameters.addProperty("fetchTables", "table");
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("fetchData", "data");
		parameters.addProperty("view","tree");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com/connection/json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		catalogs.add("json");
		metadata.add("catalogs", catalogs);
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		JsonObject json = new JsonObject();
		JsonArray data = new JsonArray();
		JsonObject catlog = new JsonObject();
		catlog.addProperty("schemas", "schemas");
		data.add(catlog);
		json.add("data", data);
		try(MockedConstruction<QueryExecutor> mockedConstruction = mockConstruction(QueryExecutor.class,(mock,context)->{
			when(mock.getResultSet(any(Connection.class), anyString())).thenReturn(json);
		})){
			handler.handleFetchSchema();	
		}
	}
	
	@Test
	public void ut_a5_test_handleFetchSchema() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchSchemas", true);
		parameters.addProperty("fetchTables", "table");
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("fetchData", "data");
		parameters.addProperty("view","tree");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com/connection/json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		catalogs.add("Itemjson");
		metadata.add("catalogs", catalogs);
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		JsonObject json = new JsonObject();
		JsonArray data = new JsonArray();
		JsonObject catlog = new JsonObject();
		catlog.addProperty("schemas", "schemas");
		data.add(catlog);
		json.add("data", data);
		try(MockedConstruction<QueryExecutor> mockedConstruction = mockConstruction(QueryExecutor.class,(mock,context)->{
			when(mock.getResultSet(any(Connection.class), anyString())).thenReturn(json);
		})){
			handler.handleFetchSchema();	
		}
	}
	
	@Test
	public void ut_a6_test_handleFetchSchema() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchSchemas", true);
		parameters.addProperty("fetchTables", "table");
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("fetchData", "data");
		parameters.addProperty("view","tree");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com/connection/json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		catalogs.add("json");
		catalogs.add("HI_DATABASE");
		catalogs.add("HI_CATALOG_CONTAINS");
		catalogs.add("HI_DATABASE_CONTAINS");
		metadata.add("catalogs", catalogs);
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		JsonObject json = new JsonObject();
		JsonArray data = new JsonArray();
		JsonObject catlog = new JsonObject();
		catlog.addProperty("schemas", "schemas");
		data.add(catlog);
		json.add("data", data);
		
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("HI_DATABASE", "HI_DATABASE");
		map.add("HI_DATABASE_CONTAINS", "HI_DATABASE_CONTAINS");
		map.add("HI_CATALOG_CONTAINS", "HI_CATALOG_CONTAINS");
		UriComponentsBuilder builder = mock(UriComponentsBuilder.class);
		UriComponents uriComponents = mock(UriComponents.class);
		when(builder.build()).thenReturn(uriComponents);
		when(uriComponents.getQueryParams()).thenReturn(map);
		try(MockedConstruction<QueryExecutor> mockedConstruction = mockConstruction(QueryExecutor.class,(mock,context)->{
			when(mock.getResultSet(any(Connection.class), anyString())).thenReturn(json);
		})){
			try(MockedStatic<UriComponentsBuilder> mockedStatic = mockStatic(UriComponentsBuilder.class)){
				mockedStatic.when(()-> UriComponentsBuilder.fromUriString(anyString())).thenReturn(builder);
				handler.handleFetchSchema();	
			}
			
		}
	}
	
	
	@Test
	public void ut_a7_test_handleFetchSchema() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchSchemas", "false");
		parameters.addProperty("fetchTables", "table");
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("fetchData", "data");
		parameters.addProperty("view","tree");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		handler.handleFetchSchema();	
		
	}
	@Test
	public void ut_a8_test_handleFetchSchema() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchTables", "table");
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("fetchData", "data");
		parameters.addProperty("view","tree");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		handler.handleFetchSchema();	
		
	}

	@Test
	public void ut_a9_test_getUrl() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchTables", "table");
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("fetchData", "data");
		parameters.addProperty("view","tree");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("Url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		String url = handler.getUrl();	
		assertEquals("com.connection.json", url);
	}
	@Test
	public void ut_b1_test_getUrl() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchTables", "table");
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("fetchData", "data");
		parameters.addProperty("view","tree");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("Uurl", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		String url = handler.getUrl();	
		assertEquals("", url);
	}
	
	@Test
	public void ut_b2_test_getSchemasTreeWithCatalog() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchTables", "table");
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("fetchData", "data");
		parameters.addProperty("view","tree");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("Uurl", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		JsonObject json = new JsonObject();
		JsonArray data = new JsonArray();
		JsonObject catlog = new JsonObject();
		catlog.addProperty("schemas", "schemas");
		data.add(catlog);
		json.add("data", data);
		
		Method method = EfwdMetadataHandler.class.getDeclaredMethod("getSchemasTreeWithCatalog", String.class);
		method.setAccessible(true);
		
		try(MockedConstruction<QueryExecutor> mockedConstruction = mockConstruction(QueryExecutor.class,(mock,context)->{
			when(mock.getResultSet(any(Connection.class), anyString())).thenReturn(json);
		})){
			Object invoke = method.invoke(handler, "catalogName");
			assertNotNull(invoke);
		}
	}
	
	
	@Test
	public void ut_b3_test_handleFetchTables() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchTables", false);
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("fetchData", "data");
		parameters.addProperty("view","tree");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("Uurl", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		handler.handleFetchTables();	
		
	}
	
	@Test
	public void ut_b4_test_handleFetchTables() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("fetchData", "data");
		parameters.addProperty("view","tree");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("Uurl", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		handler.handleFetchTables();	
		
	}
	
	@Test
	public void ut_b5_test_handleFetchTables() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchTables", true);
		parameters.addProperty("fetchColumns", "travels");
		JsonArray fetchData = new JsonArray();
		JsonObject fetchDataJSONObject = new JsonObject();
		
		fetchData.add(fetchDataJSONObject);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("view","tree");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		JsonObject json = new JsonObject();
		JsonArray data = new JsonArray();
		
		JsonObject table = new JsonObject();
		table.addProperty("tables", "tablesName");
		
		data.add(table);
		
		json.add("data", data);
		try(MockedConstruction<QueryExecutor> mockedConstruction = mockConstruction(QueryExecutor.class,(mock,context)->{
			when(mock.getResultSet(any(Connection.class), anyString())).thenReturn(json);
		})){
			handler.handleFetchTables();	
		}
		
	}
	
	
	@Test
	public void ut_b6_test_handleFetchTables() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchTables", true);
		parameters.addProperty("fetchColumns", "travels");
		JsonArray fetchData = new JsonArray();
		JsonObject fetchDataJSONObject = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject item = new JsonObject();
		item.addProperty("name", "schema-name");
		schemas.add(item);
		fetchDataJSONObject.add("schemas", schemas);
		fetchData.add(fetchDataJSONObject);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("view","tree");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		JsonObject json = new JsonObject();
		JsonArray data = new JsonArray();
			
		json.add("data", data);
		try(MockedConstruction<QueryExecutor> mockedConstruction = mockConstruction(QueryExecutor.class,(mock,context)->{
			when(mock.getResultSet(any(Connection.class), anyString())).thenReturn(json);
		})){
			handler.handleFetchTables();	
		}
		
	}
	
	
	@Test
	public void ut_b7_test_handleFetchTables() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchTables", true);
		parameters.addProperty("fetchColumns", "travels");
		
		
		parameters.addProperty("view","tree");
		
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		JsonObject json = new JsonObject();
		JsonArray data = new JsonArray();
		
		JsonObject table = new JsonObject();
		table.addProperty("tables", "tablesName");
		
		data.add(table);
		
		json.add("data", data);
		try(MockedConstruction<QueryExecutor> mockedConstruction = mockConstruction(QueryExecutor.class,(mock,context)->{
			when(mock.getResultSet(any(Connection.class), anyString())).thenReturn(json);
		})){
			handler.handleFetchTables();	
		}
		
	}

	
	@Test
	public void ut_b8_test_prepareParametersForDrillCSV() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchTables", true);
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("view","tree");
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		Method method = EfwdMetadataHandler.class.getDeclaredMethod("prepareParametersForDrillCSV", JsonObject.class);
		method.setAccessible(true);
		JsonObject efwdParameters = new JsonObject();
		JsonObject connection_JSON = new JsonObject();
		connection_JSON.addProperty("driverClassName", "driverClassName");
		connection_JSON.addProperty("Driver", "Driver");
		connection_JSON.addProperty("Url", "jdbc:drill");
		efwdParameters.add("connection", connection_JSON);
		JsonObject drill = new JsonObject();
		JsonObject drillConfig = new JsonObject();
		drill.add("drill", drillConfig);
		
		try(MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)){
			mockedStatic.when(()-> JsonUtils.getHiMiddleWareName()).thenReturn("Driver");
			mockedStatic.when(()-> JsonUtils.getDrillConfigPath()).thenReturn("path");
			mockedStatic.when(()-> JsonUtils.newGetXmlAsJson(anyString())).thenReturn(drill);
			mockedStatic.when(()-> JsonUtils.decryptPasswordFromDrillConfigObj(any(JsonObject.class))).thenReturn(drillConfig);
			
			method.invoke(handler, efwdParameters);
		}
		
		
	}

	@Test
	public void ut_b9_test_getFormatedDrillDatasources() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchTables", true);
		parameters.addProperty("fetchColumns", "travels");
		parameters.addProperty("view","tree");
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		
		
		JsonObject drillJSONObejct = new JsonObject();
		JsonObject enabledTypesJSON = new JsonObject();
		JsonObject value1 = new JsonObject();
		JsonObject configJson = new JsonObject();
		configJson.add("config1", new JsonObject());
		value1.add("config", configJson);
		enabledTypesJSON.add("key1", value1);
		drillJSONObejct.add("enabledTypes", enabledTypesJSON);
		
		handler.getFormatedDrillDatasources(drillJSONObejct);
		
		
	}

	@Test
	public void ut_c1_test_handleFetchColumns() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchTables", true);
		parameters.addProperty("fetchColumns", false);
		parameters.addProperty("view","tree");
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		handler.handleFetchColumns();
		
		
	}
	@Test
	public void ut_c2_test_handleFetchColumns() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchTables", true);
		parameters.addProperty("view","tree");
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		handler.handleFetchColumns();
		
		
	}
	@Test
	public void ut_c3_test_handleFetchColumns() {
		JsonObject parameters = new JsonObject();
		parameters.addProperty("fetchCatalogs", true);
		parameters.addProperty("fetchTables", true);
		parameters.addProperty("fetchColumns", true);
		parameters.addProperty("view","tree");
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
		
		
		JsonArray fetchData = new JsonArray();
		JsonObject fetchDataJSONObject = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject schemaJSONObject = new JsonObject();
		JsonArray tablesArray = new JsonArray();
		schemaJSONObject.add("tables", tablesArray);
		schemas.add(schemaJSONObject);
		fetchDataJSONObject.add("schemas", schemas);
		fetchData.add(fetchDataJSONObject);
		parameters.add("fetchData", fetchData);
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		
		
		JsonObject json = new JsonObject();
		JsonArray data = new JsonArray();
		
		JsonObject table = new JsonObject();
		table.addProperty("tables", "tablesName");
		table.addProperty("size", "12");
		table.addProperty("nullable", "not");
		table.addProperty("columns", "columns");
		table.addProperty("position", "position");
		table.addProperty("type", "type");
		table.addProperty("dataType", "dataType");
		table.addProperty("schemas", "schemas");
		table.addProperty("catalogs", "catalogs");
		data.add(table);
		
		json.add("data", data);
		try(MockedConstruction<QueryExecutor> mockedConstruction = mockConstruction(QueryExecutor.class,(mock,context)->{
			when(mock.getResultSet(any(Connection.class), anyString())).thenReturn(json);
		})){
			handler.handleFetchColumns();
		}
	}
	
	@Test
	public void ut_c4_test_prepareEfwdParameters() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		JsonObject parameters = new JsonObject();
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
	
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		Method method = EfwdMetadataHandler.class.getDeclaredMethod("prepareEfwdParameters", JsonObject.class,JsonObject.class
				,JsonArray.class);
		method.setAccessible(true);
		JsonObject fetchDataJSONObject = new JsonObject();
		fetchDataJSONObject.addProperty("catalog", "catalog");
		JsonObject catalogJSON = new JsonObject();
		JsonArray fetchDataTablesArray = new JsonArray();
		fetchDataTablesArray.add("table");
		Object invoke = method.invoke(handler, fetchDataJSONObject,catalogJSON,fetchDataTablesArray);
		assertNotNull(invoke);
	}
	
	@Test
	public void ut_c4_test_prepareSchemaJson() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		JsonObject parameters = new JsonObject();
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
	
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		Method method = EfwdMetadataHandler.class.getDeclaredMethod("prepareSchemaJson", JsonObject.class,JsonObject.class
				,JsonObject.class);
		method.setAccessible(true);
		JsonObject fetchDataJSONObject = new JsonObject();
		JsonObject efwdParameters = new JsonObject();
		JsonObject schemasJSON = new JsonObject();
		Object invoke = method.invoke(handler, fetchDataJSONObject,efwdParameters,schemasJSON);
		assertNull(invoke);
	}
	
	@Test
	public void ut_c5_test_whenCatalogIsEmpty() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		JsonObject parameters = new JsonObject();
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
	
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		Method method = EfwdMetadataHandler.class.getDeclaredMethod("whenCatalogIsEmpty", JsonObject.class,JsonObject.class);
		method.setAccessible(true);
		JsonObject fetchDataJSONObject = new JsonObject();
		fetchDataJSONObject.addProperty("catalog", "catalog");
		JsonObject efwdParameters = new JsonObject();
		Object invoke = method.invoke(handler, fetchDataJSONObject,efwdParameters);
		assertNotNull(invoke);
	}
	@Test
	public void ut_c6_test_getSchemasForTree() {
		JsonObject parameters = new JsonObject();
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "com.connection.json");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
	
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		
		JsonArray schemasForTree = handler.getSchemasForTree(new JsonObject());
		assertTrue(schemasForTree.isEmpty());
		
	}
	
	@Test
	public void ut_c7_test_getSchemasForTree() {
		JsonObject parameters = new JsonObject();
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "https://www.example.com/path/to/resource?HI_DATABASE=value1&HI_DATABASE_CONTAINS=value1&HI_SCHEMA_CONTAINS=value1");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
	
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		JsonObject jsonObject = new JsonObject();
		JsonArray data = new JsonArray();
		JsonObject object = new JsonObject();
		object.addProperty("schemas", "value1");
		data.add(object);
		jsonObject.add("data", data);
		JsonArray schemasForTree = handler.getSchemasForTree(jsonObject);
		assertNotNull(schemasForTree);
		
	}
	
	@Test
	public void ut_c8_test_getSchemasForTree() {
		JsonObject parameters = new JsonObject();
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "https://www.example.com/path/to/resource?HI_DATABASE=value1&HI_DATABASE_CONTAINS=value1&HI_SCHEMA_CONTAINS=value1");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
	
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		JsonObject jsonObject = new JsonObject();
		JsonArray data = new JsonArray();
		JsonObject object = new JsonObject();
		object.addProperty("schemas", "resource");
		data.add(object);
		jsonObject.add("data", data);
		JsonArray schemasForTree = handler.getSchemasForTree(jsonObject);
		assertNotNull(schemasForTree);
		
	}
	
	@Test
	public void ut_c9_test_getSchemasTree() {
		JsonObject parameters = new JsonObject();
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "https://www.example.com/path/to/resource?HI_DATABASE=value1&HI_DATABASE_CONTAINS=value1&HI_SCHEMA_CONTAINS=value1");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
	
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		JsonObject jsonObject = new JsonObject();
		JsonArray data = new JsonArray();
		JsonObject object = new JsonObject();
		object.addProperty("schemas", "resource");
		object.addProperty("catalog", "catalog");
		data.add(object);
		jsonObject.add("data", data);
		JsonArray schemasForTree = handler.getSchemasTree(jsonObject);
		assertNotNull(schemasForTree);
		
	}
	
	@Test
	public void ut_d1_test_getTables() {
		JsonObject parameters = new JsonObject();
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "https://www.example.com/path/to/resource?HI_DATABASE=value1&HI_DATABASE_CONTAINS=value1&HI_SCHEMA_CONTAINS=value1");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
	
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		JsonObject jsonObject = new JsonObject();
		JsonArray data = new JsonArray();
		jsonObject.add("data", data);
		JsonArray schemasForTree = handler.getTables(jsonObject);
		assertNotNull(schemasForTree);
		
	}
	
	@Test
	public void ut_d2_test_getTables() {
		JsonObject parameters = new JsonObject();
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "https://www.example.com/path/to/resource?HI_TABLE=value1&HI_TABLE_CONTAINS=value1");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
	
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		JsonObject jsonObject = new JsonObject();
		JsonArray data = new JsonArray();
		JsonObject object = new JsonObject();
		object.addProperty("tables", "value1");
		object.addProperty("schemas", "schemas");
		data.add(object);
		jsonObject.add("data", data);
		JsonArray schemasForTree = handler.getTables(jsonObject);
		assertNotNull(schemasForTree);
		
	}
	
	@Test
	public void ut_d3_test_checkForBrackets() {
		JsonObject parameters = new JsonObject();
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "https://www.example.com/path/to/resource?HI_TABLE=value1&HI_TABLE_CONTAINS=value1");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
	
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		jsonArray.add("[]");
		handler.checkForBrackets(jsonArray.toString(), jsonObject);
		
		
	}
	
	@Test
	public void ut_d4_test_checkForBrackets() {
		JsonObject parameters = new JsonObject();
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "https://www.example.com/path/to/resource?HI_TABLE=value1&HI_TABLE_CONTAINS=value1");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
	
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		handler.checkForBrackets(jsonArray.toString(), jsonObject);
		
		
	}
	@Test
	public void ut_d5_test_checkForBrackets() {
		JsonObject parameters = new JsonObject();
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "https://www.example.com/path/to/resource?HI_TABLE=value1&HI_TABLE_CONTAINS=value1");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
	
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		JsonObject jsonObject = new JsonObject();
		
		handler.checkForBrackets("", jsonObject);
		
		
	}
	@Test
	public void ut_d6_test_checkForBrackets() {
		JsonObject parameters = new JsonObject();
		String efwdFileNameWithExtension = "extenasion.efwd";
		Connection conn = mock(Connection.class);
		JsonObject connectionJson = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("url", "https://www.example.com/path/to/resource?HI_TABLE=value1&HI_TABLE_CONTAINS=value1");
		connectionJson.add("connection", connection);
		JsonObject metadata = new JsonObject();
	
		
		EfwdMetadataHandler handler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, conn, connectionJson, metadata);
		JsonObject jsonObject = new JsonObject();
		handler.checkForBrackets("helical", jsonObject);
		
		
	}
}



