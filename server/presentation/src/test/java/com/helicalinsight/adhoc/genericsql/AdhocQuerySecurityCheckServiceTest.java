package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdhocQuerySecurityCheckServiceTest {

	@Test(expected = Exception.class)
	public void ut_a1_test_check_for_custom_columns() throws Exception {
		AdhocQuerySecurityCheckService checkService = new AdhocQuerySecurityCheckService();
		JsonObject formDataJson = new JsonObject();
		Metadata metadataJson = mock(Metadata.class);
		checkService.check_for_custom_columns(formDataJson, metadataJson);
		
	}

	@Test(expected = Exception.class)
	public void ut_a2_test_check_for_custom_columns() throws Exception {
		AdhocQuerySecurityCheckService checkService = new AdhocQuerySecurityCheckService();
		JsonObject formDataJson = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		jsonArray.add("sample");

		Metadata metadataJson = mock(Metadata.class);
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);

		when(jsonDocumentContext.read("$.filters[*].values[*]")).thenReturn(jsonArray);
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
					.thenReturn(jsonDocumentContext);
			checkService.check_for_custom_columns(formDataJson, metadataJson);
			

		}
	}

	@Test(expected = Exception.class)
	public void ut_a3_test_check_for_custom_columns() throws Exception {
		AdhocQuerySecurityCheckService checkService = new AdhocQuerySecurityCheckService();
		JsonObject formDataJson = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		jsonArray.add("sample");

		Metadata metadataJson = mock(Metadata.class);
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		Statement stmt = mock(Statement.class);
		when(jsonDocumentContext.read("$.filters[*].values[*]")).thenReturn(jsonArray);
		
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedStatic<CCJSqlParserUtil> mockedStatic1 = mockStatic(CCJSqlParserUtil.class)) {
				mockedStatic1.when(() -> CCJSqlParserUtil.parse(anyString())).thenReturn(stmt);

				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				checkService.check_for_custom_columns(formDataJson, metadataJson);
				
			}
		}
	}
	
	@Test(expected = Exception.class)
	public void ut_a4_test_check_for_custom_columns() throws Exception {
		AdhocQuerySecurityCheckService checkService = new AdhocQuerySecurityCheckService();
		JsonObject formDataJson = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("column", ".");
		JsonObject json2 = new JsonObject();
		json2.addProperty("column", "dimdate.travelledby");
		jsonArray.add(json2);
		jsonArray.add(json);

		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		Metadata metadataJson = mock(Metadata.class);
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.read("$.filters[*].values[*]")).thenReturn(jsonArray);
		when(jsonDocumentContext.read("$.fil")).thenReturn(jsonArray);
		when(metadataJson.getDatabase()).thenReturn(database);
		when(database.getTables()).thenReturn(tables);
		List<Table> tablesList = new ArrayList<>();
		tablesList.add(table);
		when(tables.getTableList()).thenReturn(tablesList);
		when(table.getColumns()).thenReturn(columns);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		when(column.getName()).thenReturn("travelledby");
		when(columns.getColumn()).thenReturn(columnList);
		
		Map<String, String> propsMap = new HashMap<>();
		propsMap.put("$.filters[*].values[*]", "strict-metadata");
		propsMap.put("$.fil", "strict-metadata");
		Field field = AdhocQuerySecurityCheckService.class.getDeclaredField("propsMap");
		field.setAccessible(true);
		field.set(checkService, propsMap);
		
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedStatic<CCJSqlParserUtil> mockedStatic1 = mockStatic(CCJSqlParserUtil.class)) {
				mockedStatic1.when(() -> CCJSqlParserUtil.parse(anyString())).thenReturn(null);

			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
					.thenReturn(jsonDocumentContext);
			checkService.check_for_custom_columns(formDataJson, metadataJson);
			
			}
		}
	}
	
	@Test(expected = Exception.class)
	public void ut_a5_test_check_for_custom_columns() throws Exception {
		AdhocQuerySecurityCheckService checkService = new AdhocQuerySecurityCheckService();
		JsonObject formDataJson = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("column", ".");
		JsonObject json2 = new JsonObject();
		json2.addProperty("column", "dimdate.travelledby");
		jsonArray.add(json2);
		jsonArray.add(json);

		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		Metadata metadataJson = mock(Metadata.class);
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.read("$.filters[*].values[*]")).thenReturn(jsonArray);
		when(jsonDocumentContext.read("$.fil")).thenReturn(jsonArray);
		when(metadataJson.getDatabase()).thenReturn(database);
		when(database.getTables()).thenReturn(tables);
		List<Table> tablesList = new ArrayList<>();
		tablesList.add(table);
		tablesList.add(table);
		when(tables.getTableList()).thenReturn(tablesList);
		when(table.getColumns()).thenReturn(columns);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		when(column.getName()).thenReturn("travelledby");
		when(columns.getColumn()).thenReturn(columnList);
		
		Map<String, String> propsMap = new HashMap<>();
		propsMap.put("$.filters[*].values[*]", "strict-metadata");
		propsMap.put("$.fil", "strict-metadata");
		Field field = AdhocQuerySecurityCheckService.class.getDeclaredField("propsMap");
		field.setAccessible(true);
		field.set(checkService, propsMap);
		
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedStatic<CCJSqlParserUtil> mockedStatic1 = mockStatic(CCJSqlParserUtil.class)) {
				mockedStatic1.when(() -> CCJSqlParserUtil.parse(anyString())).thenReturn(null);

			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
					.thenReturn(jsonDocumentContext);
			checkService.check_for_custom_columns(formDataJson, metadataJson);
			
			}
		}
	}
	
	
	@Test
	public void ut_a6_test_check_for_custom_columns() throws Exception {
		AdhocQuerySecurityCheckService checkService = new AdhocQuerySecurityCheckService();
		JsonObject formDataJson = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("column", ".");
		JsonObject json2 = new JsonObject();
		json2.addProperty("column", "dimdate.travelledby");
		jsonArray.add(json2);
		jsonArray.add(json);

		
		Metadata metadataJson = mock(Metadata.class);
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.read("$.filters[*].values[*]")).thenReturn(jsonArray);
		when(jsonDocumentContext.read("$.fil")).thenReturn(jsonArray);
		
		Map<String, String> propsMap = new HashMap<>();
		propsMap.put("$.filters[*].values[*]", "stripped");
		Field field = AdhocQuerySecurityCheckService.class.getDeclaredField("propsMap");
		field.setAccessible(true);
		field.set(checkService, propsMap);
		
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedStatic<CCJSqlParserUtil> mockedStatic1 = mockStatic(CCJSqlParserUtil.class)) {
				mockedStatic1.when(() -> CCJSqlParserUtil.parse(anyString())).thenReturn(null);

			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
					.thenReturn(jsonDocumentContext);
			JsonObject check_for_custom_columns = checkService.check_for_custom_columns(formDataJson, metadataJson);
			assertNull(check_for_custom_columns);
			}
		}
	}
	
	@Test(expected = Exception.class)
	public void ut_a7_test_check_for_custom_columns() throws Exception {
		AdhocQuerySecurityCheckService checkService = new AdhocQuerySecurityCheckService();
		JsonObject formDataJson = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("column", ".");
		JsonObject json2 = new JsonObject();
		json2.addProperty("column", "dimdate.travelledby");
		jsonArray.add(json2);
		jsonArray.add(json);

		
		Metadata metadataJson = mock(Metadata.class);
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.read("$.filters[*].values[*]")).thenReturn(jsonArray);
		
		Map<String, String> propsMap = new HashMap<>();
		propsMap.put("$.filters[*].values[*]", "strict");
		Field field = AdhocQuerySecurityCheckService.class.getDeclaredField("propsMap");
		field.setAccessible(true);
		field.set(checkService, propsMap);
		
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedStatic<CCJSqlParserUtil> mockedStatic1 = mockStatic(CCJSqlParserUtil.class)) {
				mockedStatic1.when(() -> CCJSqlParserUtil.parse(anyString())).thenReturn(null);

			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
					.thenReturn(jsonDocumentContext);
			checkService.check_for_custom_columns(formDataJson, metadataJson);
			
			}
		}
	}

	@Test
	public void ut_a8_test_check_for_custom_columns() throws Exception {
		AdhocQuerySecurityCheckService checkService = new AdhocQuerySecurityCheckService();
		JsonObject formDataJson = new JsonObject();
		JsonObject jsonArray = new JsonObject();
		
		Metadata metadataJson = mock(Metadata.class);
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.read("$.filters[*].values[*]")).thenReturn(jsonArray);
		
		Map<String, String> propsMap = new HashMap<>();
		propsMap.put("$.filters[*].values[*]", "strict");
		Field field = AdhocQuerySecurityCheckService.class.getDeclaredField("propsMap");
		field.setAccessible(true);
		field.set(checkService, propsMap);
		
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedStatic<CCJSqlParserUtil> mockedStatic1 = mockStatic(CCJSqlParserUtil.class)) {
				mockedStatic1.when(() -> CCJSqlParserUtil.parse(anyString())).thenReturn(null);

			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
					.thenReturn(jsonDocumentContext);
			JsonObject check_for_custom_columns = checkService.check_for_custom_columns(formDataJson, metadataJson);
			assertNull(check_for_custom_columns);
			}
		}
	}

}
