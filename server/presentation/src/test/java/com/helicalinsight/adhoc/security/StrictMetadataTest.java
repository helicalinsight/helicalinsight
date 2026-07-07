package com.helicalinsight.adhoc.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
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
public class StrictMetadataTest {

	@Test(expected = Exception.class)
	public void ut_a1_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);

		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		when(jsonDocumentContext.read(anyString())).thenReturn("ALTER");
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
					.thenReturn(jsonDocumentContext);
			JsonObject checkSqlInjection = strictMetadata.checkSqlInjection(formData, "key", metadata);
			assertEquals(formData, checkSqlInjection);
		}
	}

	@Test
	public void ut_a2_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);

		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		array.add("string");
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
					.thenReturn(jsonDocumentContext);
			JsonObject checkSqlInjection = strictMetadata.checkSqlInjection(formData, "$.filters", metadata);
			assertEquals(formData, checkSqlInjection);

		}
	}

	@Test(expected = Exception.class)
	public void ut_a3_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Statement stmt = mock(Statement.class);
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		array.add("string");
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedStatic<CCJSqlParserUtil> mockedStatic1 = mockStatic(CCJSqlParserUtil.class)) {
				mockedStatic1.when(() -> CCJSqlParserUtil.parse(anyString())).thenReturn(stmt);
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "$.filters", metadata);

			}
		}
	}

	@Test(expected = SecurityException.class)
	public void ut_a4_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);

		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new HashSet<>();
		set.add("table1");
		sqlMap.put("complexQuery", set);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn("schemaName");
		when(database.getCatalog()).thenReturn("catalog");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(table.getName()).thenReturn("table2");
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}

	public void ut_a5_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);

		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new HashSet<>();
		set.add("table1");
		sqlMap.put("complexQuery", set);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn("schemaName");
		when(database.getCatalog()).thenReturn("catalog");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(table.getName()).thenReturn("table1");
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}
	@Test(expected = SecurityException.class)
	public void ut_a6_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);

		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new HashSet<>();
		set.add("table0");
		set.add("table2");
		set.add("table1");
		sqlMap.put("complexQuery", set);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn("table1");
		when(database.getCatalog()).thenReturn("table1");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(table.getName()).thenReturn("table1").thenReturn("table0");
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}
	
	@Test(expected = SecurityException.class)
	public void ut_a7_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);
		
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new HashSet<>();
		set.add("table0");
		set.add("table2");
		set.add("table1");
		sqlMap.put("complexQuery", set);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn("table");
		when(database.getCatalog()).thenReturn("table1");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(table.getName()).thenReturn("table1").thenReturn("table0");
		
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}

	

	@Test(expected = SecurityException.class)
	public void ut_a8_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);

		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new HashSet<>();
		set.add("table0");
		sqlMap.put("complexQuery", set);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn("table1");
		when(database.getCatalog()).thenReturn("table0");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(table.getName()).thenReturn("table1").thenReturn("table0");
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}

	@Test(expected = SecurityException.class)
	public void ut_a9_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);

		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new HashSet<>();
		set.add("table0");
		set.add("table1");
		sqlMap.put("complexQuery", set);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn("table");
		when(database.getCatalog()).thenReturn("table");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(table.getName()).thenReturn("table1").thenReturn("table0");
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}
	
	
	@Test
	public void ut_b1_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new HashSet<>();
		set.add("table0");
		set.add("table1");
		sqlMap.put("complexQuery", set);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn("table");
		when(database.getCatalog()).thenReturn("table1");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(table.getName()).thenReturn("table1").thenReturn("table0");
		
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		when(table.getColumns()).thenReturn(columns);
		when(columns.getColumn()).thenReturn(columnList);
		when(column.getName()).thenReturn("table1");
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}
	
	@Test(expected = SecurityException.class)
	public void ut_b2_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		
		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new HashSet<>();
		set.add("table0");
		set.add("table1");
		sqlMap.put("complexQuery", set);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn("table");
		when(database.getCatalog()).thenReturn("table1");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(table.getName()).thenReturn("table1").thenReturn("table0");
		
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		when(table.getColumns()).thenReturn(columns);
		when(columns.getColumn()).thenReturn(columnList);
		when(column.getName()).thenReturn("column");
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}
	
	
	
	@Test(expected = SecurityException.class)
	public void ut_b3_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);

		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new HashSet<>();
		Set<String> set1 = new HashSet<>();
		set1.add("table0");
		set1.add("table1");
		sqlMap.put("catalog", set);
		sqlMap.put("schema", set1);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn("");
		when(database.getCatalog()).thenReturn("catalog");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(table.getName()).thenReturn("table1").thenReturn("table0");
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}
	
	@Test(expected = SecurityException.class)
	public void ut_b4_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);

		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new HashSet<>();
		Set<String> set1 = new HashSet<>();
		set1.add("table0");
		set1.add("table1");
		sqlMap.put("catalog", set);
		sqlMap.put("schema", set1);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn("");
		when(database.getCatalog()).thenReturn("catalog").thenReturn("");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(table.getName()).thenReturn("table1").thenReturn("table0");
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}


	@Test(expected = SecurityException.class)
	public void ut_b5_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);

		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new HashSet<>();
		set.add("table0");
		set.add("table1");
		sqlMap.put("catalog", set);
		sqlMap.put("schema", set);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn("table1");
		when(database.getCatalog()).thenReturn("table0");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList).thenReturn(tableList).thenReturn(null);
		when(table.getName()).thenReturn("table1").thenReturn("table0");
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}

	@Test(expected = SecurityException.class)
	public void ut_b6_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);

		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new HashSet<>();
		set.add("table0");
		set.add("table1");
		sqlMap.put("catalog", set);
		sqlMap.put("schema", set);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn("table");
		when(database.getCatalog()).thenReturn("table0");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList).thenReturn(tableList);
		when(table.getName()).thenReturn("table1").thenReturn("table0");
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}
	
	
	@Test(expected = SecurityException.class)
	public void ut_b7_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);

		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new HashSet<>();
		set.add("table0");
		set.add("table1");
		sqlMap.put("catalog", set);
		Set<String> set1 = new HashSet<>();
		sqlMap.put("schema", set1);
		sqlMap.put("tables", set);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn("table1");
		when(database.getCatalog()).thenReturn("table0");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList).thenReturn(tableList);
		when(table.getName()).thenReturn("table1").thenReturn("table0");
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}

	
	@Test(expected = SecurityException.class)
	public void ut_b8_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);

		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new HashSet<>();
		set.add("table0");
		set.add("table1");
		sqlMap.put("catalog", set);
		Set<String> set1 = new HashSet<>();
		sqlMap.put("schema", set1);
		Set<String> tableSet = new HashSet<>();
		tableSet.add("table1");
		sqlMap.put("tables", tableSet);
		Set<String> columnSet = new HashSet<>();
		columnSet.add("table1");
		sqlMap.put("column", columnSet);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn("table1");
		when(database.getCatalog()).thenReturn("table0");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList).thenReturn(tableList);
		when(table.getName()).thenReturn("table1").thenReturn("table0").thenReturn("table1");
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}
	
	@Test(expected = SecurityException.class)
	public void ut_b9_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);

		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new HashSet<>();
		set.add("table0");
		set.add("table1");
		sqlMap.put("catalog", set);
		Set<String> set1 = new HashSet<>();
		sqlMap.put("schema", set1);
		Set<String> tableSet = new HashSet<>();
		tableSet.add("table1");
		sqlMap.put("tables", tableSet);
		Set<String> columnSet = new HashSet<>();
		columnSet.add("table1");
		sqlMap.put("column", columnSet);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn("table1");
		when(database.getCatalog()).thenReturn("table0");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList).thenReturn(tableList);
		when(table.getName()).thenReturn("table1").thenReturn("table0");
		Columns columns = mock(Columns.class);
		when(table.getColumns()).thenReturn(columns);
		List<Column> column1 = new ArrayList<>();
		Column column = mock(Column.class);
		column1.add(column);
		when(column.getName()).thenReturn("table1");
		when(columns.getColumn()).thenReturn(column1);
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}
	
	@Test(expected = SecurityException.class)
	public void ut_c1_test_checkSqlInjection() throws Exception {
		StrictMetadata strictMetadata = new StrictMetadata();
		JsonObject formData = new JsonObject();
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);

		DocumentContext jsonDocumentContext = mock(DocumentContext.class);
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("column", "column");
		array.add(obj);
		when(jsonDocumentContext.read(anyString())).thenReturn(array);
		Map<String, Set<String>> sqlMap = new HashMap<>();
		Set<String> set = new LinkedHashSet<>();
		set.add("table0");
		set.add("table1");
		sqlMap.put("catalog", set);
		Set<String> set1 = new LinkedHashSet<>();
		sqlMap.put("schema", set1);
		Set<String> tableSet = new LinkedHashSet<>();
		tableSet.add("table1");
		sqlMap.put("tables", tableSet);
		Set<String> columnSet = new LinkedHashSet<>();
		columnSet.add("table1");
		sqlMap.put("column", columnSet);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getSchema()).thenReturn(null);
		when(database.getCatalog()).thenReturn("table0");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList).thenReturn(tableList);
		when(table.getName()).thenReturn("table1").thenReturn("table0");
		Columns columns = mock(Columns.class);
		when(table.getColumns()).thenReturn(columns);
		List<Column> column1 = new ArrayList<>();
		Column column = mock(Column.class);
		column1.add(column);
		when(column.getName()).thenReturn("table");
		when(columns.getColumn()).thenReturn(column1);
		when(table.getAliasName()).thenReturn("table1");
		try (MockedStatic<JsonPath> mockedStatic = mockStatic(JsonPath.class)) {
			try (MockedConstruction<SqlUtils> construction = mockConstruction(SqlUtils.class, (mock, context) -> {
				when(mock.getSqlColumns(anyString(), anyString())).thenReturn(sqlMap);
			})) {
				mockedStatic.when(() -> JsonPath.parse(any(JsonObject.class), any(Configuration.class)))
						.thenReturn(jsonDocumentContext);
				strictMetadata.checkSqlInjection(formData, "key", metadata);

			}

		}
	}
	
	@Test
	public void ut_c2_test_isThreadSafeToCache() {
		StrictMetadata strictMetadata = new StrictMetadata();
		boolean threadSafeToCache = strictMetadata.isThreadSafeToCache();
		assertFalse(threadSafeToCache);
	}

	@Test
	public void ut_c3_test_getColumnsFromTable() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		StrictMetadata strictMetadata = new StrictMetadata();
		Method method = StrictMetadata.class.getDeclaredMethod("getColumnsFromTable", List.class, Set.class);
		method.setAccessible(true);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		List<Table> metadataTable = new ArrayList<>();
		metadataTable.add(table);
		Set<String> tables = new HashSet<>();
		tables.add("column");
		when(table.getAliasName()).thenReturn("column");
		when(table.getColumns()).thenReturn(columns);
		Object invoke = method.invoke(strictMetadata, metadataTable,tables);
		assertEquals(invoke,columns);
		
	}
	
	@Test
	public void ut_c4_test_getColumnsFromTable() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		StrictMetadata strictMetadata = new StrictMetadata();
		Method method = StrictMetadata.class.getDeclaredMethod("getColumnsFromTable", List.class, Set.class);
		method.setAccessible(true);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		List<Table> metadataTable = new ArrayList<>();
		metadataTable.add(table);
		Set<String> tables = new HashSet<>();
		tables.add("column1");
		when(table.getAliasName()).thenReturn("column");
		when(table.getOriginalName()).thenReturn("column1");
		
		when(table.getColumns()).thenReturn(columns);
		Object invoke = method.invoke(strictMetadata, metadataTable,tables);
		assertEquals(invoke,columns);
		
	}
	
	@Test
	public void ut_c5_test_getColumnsFromTable() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		StrictMetadata strictMetadata = new StrictMetadata();
		Method method = StrictMetadata.class.getDeclaredMethod("getColumnsFromTable", List.class, Set.class);
		method.setAccessible(true);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		List<Table> metadataTable = new ArrayList<>();
		metadataTable.add(table);
		Set<String> tables = new HashSet<>();
		tables.add("column1");
		when(table.getAliasName()).thenReturn("column");
		when(table.getOriginalName()).thenReturn("");
		
		when(table.getColumns()).thenReturn(columns);
		Object invoke = method.invoke(strictMetadata, metadataTable,tables);
		assertEquals(invoke,null);
		
	}
	
	@Test
	public void ut_c6_test_getTablesFromSchema() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		StrictMetadata strictMetadata = new StrictMetadata();
		Method method = StrictMetadata.class.getDeclaredMethod("getTablesFromSchema", Database.class, String.class);
		method.setAccessible(true);
		Database db = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		when(db.getSchema()).thenReturn("schemaName");
		when(db.getTables()).thenReturn(tables);
		List<Table> list = new ArrayList<>();
		list.add(table);
		when(tables.getTableList()).thenReturn(list);
		Object invoke = method.invoke(strictMetadata, db,"schemaName");
		assertEquals(invoke,list);
		
	}
	
	@Test
	public void ut_c7_test_getTablesFromSchema() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		StrictMetadata strictMetadata = new StrictMetadata();
		Method method = StrictMetadata.class.getDeclaredMethod("getTablesFromSchema", Database.class, String.class);
		method.setAccessible(true);
		Database db = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		when(db.getSchema()).thenReturn("schemaName1");
		when(db.getTables()).thenReturn(tables);
		List<Table> list = new ArrayList<>();
		list.add(table);
		when(tables.getTableList()).thenReturn(list);
		Object invoke = method.invoke(strictMetadata, db,"schemaName");
		assertEquals(invoke,null);
		
	}
	
	@Test
	public void ut_c8_test_containsNameAlias() {
		StrictMetadata strictMetadata = new StrictMetadata();
		List<Column> list = new ArrayList<>();
		Column column = mock(Column.class);
		list.add(column);
		when(column.getAliasName()).thenReturn("aliasColumn");
		boolean containsNameAlias = strictMetadata.containsNameAlias(list, "aliasColumn");
		assertTrue(containsNameAlias);
	}
	
	@Test
	public void ut_c9_test_containsNameAlias() {
		StrictMetadata strictMetadata = new StrictMetadata();
		List<Column> list = new ArrayList<>();
		Column column = mock(Column.class);
		list.add(column);
		when(column.getAliasName()).thenReturn("");
		boolean containsNameAlias = strictMetadata.containsNameAlias(list, "aliasColumn");
		assertFalse(containsNameAlias);
	}
	
	@Test
	public void ut_d1_test_containsNameOriginal() {
		StrictMetadata strictMetadata = new StrictMetadata();
		List<Column> list = new ArrayList<>();
		Column column = mock(Column.class);
		list.add(column);
		when(column.getOriginalName()).thenReturn("orgColumn");
		when(column.getAliasName()).thenReturn("aliasCol");
		boolean containsNameAlias = strictMetadata.containsNameOriginal(list, "aliasCol");
		assertTrue(containsNameAlias);
	}
	
	@Test
	public void ut_d2_test_containsNameOriginal() {
		StrictMetadata strictMetadata = new StrictMetadata();
		List<Column> list = new ArrayList<>();
		Column column = mock(Column.class);
		list.add(column);
		when(column.getOriginalName()).thenReturn("");
		
		boolean containsNameAlias = strictMetadata.containsNameOriginal(list, "aliasCol");
		assertFalse(containsNameAlias);
	}
	@Test
	public void ut_d3_test_containsSchemaAndCatalog() {
		Set<String> elemList = new LinkedHashSet<>();
		elemList.add("schemaName");
		boolean containsSchemaAndCatalog = StrictMetadata.containsSchemaAndCatalog("schemaName", elemList);
		assertTrue(containsSchemaAndCatalog);
	}
	
	@Test
	public void ut_d4_test_containsSchemaAndCatalog() {
		Set<String> elemList = new LinkedHashSet<>();
		elemList.add("schemaName2");
		boolean containsSchemaAndCatalog = StrictMetadata.containsSchemaAndCatalog("schemaName", elemList);
		assertFalse(containsSchemaAndCatalog);
	}
}
