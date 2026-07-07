package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.AddRemoveTableColumnsHandler;
import com.helicalinsight.adhoc.metadata.WorkflowDatabaseMetadataProvider;
import com.helicalinsight.adhoc.metadata.genericdb.DuplicateColumnException;
import com.helicalinsight.adhoc.metadata.genericdb.TableNameExistsException;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.adhoc.metadata.jaxb.View;
import com.helicalinsight.adhoc.metadata.jaxb.Views;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddRemoveTableColumnsHandlerTest {

	@Test
	public void ut_a1_test_getMetadataToUpdate() {
		AddRemoveTableColumnsHandler handler = new AddRemoveTableColumnsHandler();
		JsonObject obj = new JsonObject();
		obj.addProperty("location", "Temp");
		obj.addProperty("metadataFileName", "test.metadata");
		String metadataToUpdate = AddRemoveTableColumnsHandler.getMetadataToUpdate(obj);
		String expectedPath = ApplicationProperties.INSTANCE.getSolutionDirectory() + File.separator+ "Temp" +File.separator+"test.metadata";
		assertEquals(expectedPath, metadataToUpdate);

	}

	@Test
	public void ut_a2_test_getMetadataToUpdate() throws IOException {
		JsonObject obj = new JsonObject();
		obj.addProperty("location", "Temp");
		obj.addProperty("metadataFileName", "test");
		String path = TempDirectoryCleaner.getTempDirectory()+File.separator+"Temp_temp_test.metadata";
		File file = new File(path);
		file.createNewFile();
		String metadataToUpdate = AddRemoveTableColumnsHandler.getMetadataToUpdate(obj);
		assertEquals(path, metadataToUpdate);
		file.delete();
	}

	@Test
	public void ut_a3_test_getMetadataToUpdate() {
		JsonObject metadata = new JsonObject();
		metadata.addProperty("uniqueId", "111");
		String metadataToUpdate = AddRemoveTableColumnsHandler.getMetadataToUpdate(metadata);
		assertEquals("111", metadataToUpdate);
	}

	@Test
	public void ut_a4_test_getMetadataToUpdate() {
		JsonObject obj = new JsonObject();
		JsonObject metadata = new JsonObject();
		metadata.addProperty("uniqueId", "111");
		obj.add("metadata", metadata);
		String metadataToUpdate = AddRemoveTableColumnsHandler.getMetadataToUpdate(obj);
		assertEquals("111", metadataToUpdate);
	}

	@Test
	public void ut_a5_test_getMetadataToUpdate() {
		JsonObject obj = new JsonObject();
		AddRemoveTableColumnsHandler.getMetadataToUpdate(obj);

	}

	@Test
	public void ut_a6_test_handleAddRemove() {
		JsonObject obj = new JsonObject();
		String handleAddRemove = AddRemoveTableColumnsHandler.handleAddRemove(obj);
		assertNull(handleAddRemove);
	}

	@Test
	public void ut_a7_test_handleAddRemove() throws IOException {
		JsonObject obj = new JsonObject();
		obj.addProperty("location", "Temp");
		obj.addProperty("metadataFileName", "sampleMeta");

		JsonObject metadata = new JsonObject();
		metadata.addProperty("uniqueId", "111");
		JsonArray catalogs = new JsonArray();
		JsonObject object = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject object2 = new JsonObject();
		JsonArray tables = new JsonArray();
		JsonObject object3 = new JsonObject();
		object3.addProperty("id", "33");
		JsonArray columns = new JsonArray();
		JsonObject object4 = new JsonObject();
		columns.add(object4);
		object3.add("columns", columns);
		tables.add(object3);
		object2.add("tables", tables);
		schemas.add(object2);
		object.add("schemas", schemas);
		catalogs.add(object);
		metadata.add("catalogs", catalogs);
		obj.add("metadata", metadata);
		JsonObject removeItem = new JsonObject();
		obj.add("removeItem", removeItem);

		String path = TempDirectoryCleaner.getTempDirectory()+File.separator+"Temp_temp_sampleMeta.metadata";
		File file = new File(path);
		file.createNewFile();
		Database data = mock(Database.class);
		Tables tablesObj = mock(Tables.class);
		Metadata metadataObj = mock(Metadata.class);
		Table table = mock(Table.class);
		List<Table> list = Arrays.asList(table);

		when(metadataObj.getDatabase()).thenReturn(data);
		when(data.getTables()).thenReturn(tablesObj);
		when(tablesObj.getTableList()).thenReturn(list);
		JsonObject response = new JsonObject();
		response.addProperty("uniqueId", "sampleMeta");
		try (MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)) {
			try (MockedStatic<WorkflowDatabaseMetadataProvider> mockedStatic1 = mockStatic(
					WorkflowDatabaseMetadataProvider.class)) {
				
				mockedStatic.when(() -> JaxbUtils.unMarshal(Metadata.class, file)).thenReturn(metadataObj);
				mockedStatic1.when(() -> WorkflowDatabaseMetadataProvider.prepareResponse(any(JsonObject.class), any(Metadata.class))).thenReturn(response);
				
				String handleAddRemove = AddRemoveTableColumnsHandler.handleAddRemove(obj);
				
				assertTrue(handleAddRemove.contains("sampleMeta"));
				file.delete();
			}
		}

	}

	@Test
	public void ut_a8_test_handleAddRemove() throws IOException {
		JsonObject obj = new JsonObject();
		obj.addProperty("location", "Temp");
		obj.addProperty("metadataFileName", "sampleMeta.metadata");

		JsonObject metadata = new JsonObject();
		metadata.addProperty("uniqueId", "111");
		JsonArray catalogs = new JsonArray();
		JsonObject object = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject object2 = new JsonObject();
		JsonArray tables = new JsonArray();
		JsonObject object3 = new JsonObject();
		object3.addProperty("id", "33");
		JsonArray columns = new JsonArray();
		JsonObject object4 = new JsonObject();
		columns.add(object4);
		object3.add("columns", columns);
		tables.add(object3);
		object2.add("tables", tables);
		schemas.add(object2);
		object.add("schemas", schemas);
		catalogs.add(object);
		metadata.add("catalogs", catalogs);
		obj.add("metadata", metadata);
		JsonObject removeItem = new JsonObject();
		obj.add("removeItem", removeItem);

		String path = TempDirectoryCleaner.getTempDirectory()+File.separator+"Temp_temp_sampleMeta.metadata";
		File file = new File(path);
		file.createNewFile();
		
		Database data = mock(Database.class);
		Tables tablesObj = mock(Tables.class);
		Metadata metadataObj = mock(Metadata.class);
		Table table = mock(Table.class);
		List<Table> list = Arrays.asList(table);

		when(metadataObj.getDatabase()).thenReturn(data);
		when(data.getTables()).thenReturn(tablesObj);
		when(tablesObj.getTableList()).thenReturn(list);
		JsonObject response = new JsonObject();
		response.addProperty("uniqueId", "sampleMeta");
		try (MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)) {
			try (MockedStatic<WorkflowDatabaseMetadataProvider> mockedStatic1 = mockStatic(
					WorkflowDatabaseMetadataProvider.class)) {
				
				mockedStatic.when(() -> JaxbUtils.unMarshal(Metadata.class, file)).thenReturn(metadataObj);
				mockedStatic1.when(() -> WorkflowDatabaseMetadataProvider.prepareResponse(any(JsonObject.class), any(Metadata.class))).thenReturn(response);
				
				String handleAddRemove = AddRemoveTableColumnsHandler.handleAddRemove(obj);
				
				assertTrue(handleAddRemove.contains("sampleMeta"));
				file.delete();
			}
		}

	}
	@Test
	public void ut_a9_test_handleAddRemove() throws IOException {
		JsonObject obj = new JsonObject();
		obj.addProperty("uniqueId", "111");
		obj.addProperty("metadataFileName", "sampleMeta");

		JsonObject metadata = new JsonObject();
		metadata.addProperty("uniqueId", "111");
		JsonArray catalogs = new JsonArray();
		JsonObject object = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject object2 = new JsonObject();
		JsonArray tables = new JsonArray();
		JsonObject object3 = new JsonObject();
		object3.addProperty("id", "33");
		JsonArray columns = new JsonArray();
		JsonObject object4 = new JsonObject();
		columns.add(object4);
		object3.add("columns", columns);
		tables.add(object3);
		object2.add("tables", tables);
		schemas.add(object2);
		object.add("schemas", schemas);
		catalogs.add(object);
		metadata.add("catalogs", catalogs);
		obj.add("metadata", metadata);
		JsonObject removeItem = new JsonObject();
		obj.add("removeItem", removeItem);

		String path = TempDirectoryCleaner.getTempDirectory()+File.separator+"111.metadata";
		File file = new File(path);
		
		Database data = mock(Database.class);
		Tables tablesObj = mock(Tables.class);
		Metadata metadataObj = mock(Metadata.class);
		Table table = mock(Table.class);
		List<Table> list = Arrays.asList(table);

		when(metadataObj.getDatabase()).thenReturn(data);
		when(data.getTables()).thenReturn(tablesObj);
		when(tablesObj.getTableList()).thenReturn(list);
		JsonObject response = new JsonObject();
		response.addProperty("uniqueId", "sampleMeta");
		try (MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)) {
			try (MockedStatic<WorkflowDatabaseMetadataProvider> mockedStatic1 = mockStatic(
					WorkflowDatabaseMetadataProvider.class)) {
				
				mockedStatic.when(() -> JaxbUtils.unMarshal(Metadata.class, file)).thenReturn(metadataObj);
				mockedStatic1.when(() -> WorkflowDatabaseMetadataProvider.prepareResponse(any(JsonObject.class), any(Metadata.class))).thenReturn(response);
				
				String handleAddRemove = AddRemoveTableColumnsHandler.handleAddRemove(obj);
				
				assertTrue(handleAddRemove.contains("sampleMeta"));
			}
		}

	}
	@Test
	public void ut_b1_test_handleAddRemove() throws IOException {
		JsonObject obj = new JsonObject();
		obj.addProperty("metadataFileName", "sampleMeta");

		JsonObject metadata = new JsonObject();
		metadata.addProperty("uniqueId", "111");
		JsonArray catalogs = new JsonArray();
		JsonObject object = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject object2 = new JsonObject();
		JsonArray tables = new JsonArray();
		JsonObject object3 = new JsonObject();
		object3.addProperty("id", "33");
		JsonArray columns = new JsonArray();
		JsonObject object4 = new JsonObject();
		columns.add(object4);
		object3.add("columns", columns);
		tables.add(object3);
		object2.add("tables", tables);
		schemas.add(object2);
		object.add("schemas", schemas);
		catalogs.add(object);
		metadata.add("catalogs", catalogs);
		obj.add("metadata", metadata);
		JsonObject removeItem = new JsonObject();
		obj.add("removeItem", removeItem);

		String path = TempDirectoryCleaner.getTempDirectory()+File.separator+"111.metadata";
		File file = new File(path);
		
		Database data = mock(Database.class);
		Tables tablesObj = mock(Tables.class);
		Metadata metadataObj = mock(Metadata.class);
		Table table = mock(Table.class);
		List<Table> list = Arrays.asList(table);

		when(metadataObj.getDatabase()).thenReturn(data);
		when(data.getTables()).thenReturn(tablesObj);
		when(tablesObj.getTableList()).thenReturn(list);
		JsonObject response = new JsonObject();
		response.addProperty("uniqueId", "sampleMeta");
		try (MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)) {
			try (MockedStatic<WorkflowDatabaseMetadataProvider> mockedStatic1 = mockStatic(
					WorkflowDatabaseMetadataProvider.class)) {
				
				mockedStatic.when(() -> JaxbUtils.unMarshal(Metadata.class, file)).thenReturn(metadataObj);
				mockedStatic1.when(() -> WorkflowDatabaseMetadataProvider.prepareResponse(any(JsonObject.class), any(Metadata.class))).thenReturn(response);
				
				String handleAddRemove = AddRemoveTableColumnsHandler.handleAddRemove(obj);
				
				assertTrue(handleAddRemove.contains("sampleMeta"));
			}
		}

	}

	
	@Test(expected = TableNameExistsException.class)
	public void ut_b2_test_handleAddRemoveTableColumns() {
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		JsonObject object = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject object2 = new JsonObject();
		JsonArray tables = new JsonArray();
		JsonObject object3 = new JsonObject();
		JsonArray columns = new JsonArray();
		JsonObject object4 = new JsonObject();
		columns.add(object4);
		object3.add("columns", columns);
		object3.addProperty("name", "TableName");
		object3.addProperty("alias", "TableAlias");
		tables.add(object3);
		object2.add("tables", tables);
		schemas.add(object2);
		object.add("schemas", schemas);
		catalogs.add(object);
		metadata.add("catalogs", catalogs);
		formData.add("metadata", metadata);
		JsonObject removeItem = new JsonObject();
		formData.add("removeItem", removeItem);

		Database data = mock(Database.class);
		Tables tablesObj = mock(Tables.class);
		Metadata metadataObj = mock(Metadata.class);
		Table table = mock(Table.class);
		List<Table> list = Arrays.asList(table);
		
		when(table.getName()).thenReturn("TableName");
		when(metadataObj.getDatabase()).thenReturn(data);
		when(data.getTables()).thenReturn(tablesObj);
		when(tablesObj.getTableList()).thenReturn(list);
		AddRemoveTableColumnsHandler.handleAddRemoveTableColumns(formData, metadataObj);

	}
	@Test
	public void ut_b3_test_handleAddRemoveTableColumns() {
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		JsonObject object = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject object2 = new JsonObject();
		JsonArray tables = new JsonArray();
		JsonObject object3 = new JsonObject();
		JsonArray columns = new JsonArray();
		JsonObject object4 = new JsonObject();
		object4.addProperty("name", "cName");
		object4.addProperty("type", "text");
		columns.add(object4);
		object3.add("columns", columns);
		object3.addProperty("name", "TableName");
		object3.addProperty("alias", "TableAlias");
		tables.add(object3);
		object2.add("tables", tables);
		schemas.add(object2);
		object.add("schemas", schemas);
		catalogs.add(object);
		metadata.add("catalogs", catalogs);
		formData.add("metadata", metadata);
		JsonObject removeItem = new JsonObject();
		formData.add("removeItem", removeItem);

		Database data = mock(Database.class);
		Tables tablesObj = mock(Tables.class);
		Metadata metadataObj = mock(Metadata.class);
		Table table = mock(Table.class);
		List<Table> list = new ArrayList<>();
		list.add(table);
		
		Column column = mock(Column.class);
		Columns tableColumns = mock(Columns.class);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		when(table.getName()).thenReturn("dummyTable");
		when(metadataObj.getDatabase()).thenReturn(data);
		when(data.getTables()).thenReturn(tablesObj);
		when(tablesObj.getTableList()).thenReturn(list);
		when(table.getColumns()).thenReturn(tableColumns);
		when(tableColumns.getColumn()).thenReturn(columnList);
		when(column.getName()).thenReturn("columnName");
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Table.class)).thenReturn(table);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Column.class)).thenReturn(column);
			
			AddRemoveTableColumnsHandler.handleAddRemoveTableColumns(formData, metadataObj);

		}
	}

	@Test(expected = DuplicateColumnException.class)
	public void ut_b4_test_handleAddRemoveTableColumns() {
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		JsonObject object = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject object2 = new JsonObject();
		JsonArray tables = new JsonArray();
		JsonObject object3 = new JsonObject();
		JsonArray columns = new JsonArray();
		JsonObject object4 = new JsonObject();
		object4.addProperty("name", "cName");
		object4.addProperty("type", "text");
		columns.add(object4);
		object3.add("columns", columns);
		object3.addProperty("name", "TableName");
		object3.addProperty("alias", "TableAlias");
		tables.add(object3);
		object2.add("tables", tables);
		schemas.add(object2);
		object.add("schemas", schemas);
		catalogs.add(object);
		metadata.add("catalogs", catalogs);
		formData.add("metadata", metadata);
		JsonObject removeItem = new JsonObject();
		formData.add("removeItem", removeItem);

		Database data = mock(Database.class);
		Tables tablesObj = mock(Tables.class);
		Metadata metadataObj = mock(Metadata.class);
		Table table = mock(Table.class);
		List<Table> list = new ArrayList<>();
		list.add(table);
		
		Column column = mock(Column.class);
		Columns tableColumns = mock(Columns.class);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		when(table.getName()).thenReturn("dummyTable");
		when(metadataObj.getDatabase()).thenReturn(data);
		when(data.getTables()).thenReturn(tablesObj);
		when(tablesObj.getTableList()).thenReturn(list);
		when(table.getColumns()).thenReturn(tableColumns);
		when(tableColumns.getColumn()).thenReturn(columnList);
		when(column.getName()).thenReturn("cName");
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Table.class)).thenReturn(table);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Column.class)).thenReturn(column);
			
			AddRemoveTableColumnsHandler.handleAddRemoveTableColumns(formData, metadataObj);

		}
	}
	@Test
	public void ut_b5_test_handleAddRemoveTableColumns() {
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		JsonObject object = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject object2 = new JsonObject();
		JsonArray tables = new JsonArray();
		JsonObject object3 = new JsonObject();
		JsonArray columns = new JsonArray();
		JsonObject object4 = new JsonObject();
		object4.addProperty("name", "cName");
		object4.addProperty("type", "text");
		columns.add(object4);
		object3.add("columns", columns);
		object3.addProperty("name", "TableName");
		object3.addProperty("alias", "TableAlias");
		tables.add(object3);
		object2.add("tables", tables);
		schemas.add(object2);
		object.add("schemas", schemas);
		catalogs.add(object);
		metadata.add("catalogs", catalogs);
		formData.add("metadata", metadata);
		JsonObject removeItem = new JsonObject();
		formData.add("removeItem", removeItem);

		Database data = mock(Database.class);
		Tables tablesObj = mock(Tables.class);
		Metadata metadataObj = mock(Metadata.class);
		Table table = mock(Table.class);
		List<Table> list = new ArrayList<>();
		list.add(table);
		
		Column column = mock(Column.class);
		Columns tableColumns = mock(Columns.class);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		when(table.getName()).thenReturn("dummyTable");
		when(metadataObj.getDatabase()).thenReturn(data);
		when(data.getTables()).thenReturn(tablesObj);
		when(tablesObj.getTableList()).thenReturn(list);
		when(table.getColumns()).thenReturn(tableColumns);
		when(tableColumns.getColumn()).thenReturn(null);
		when(column.getName()).thenReturn("cName");
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Table.class)).thenReturn(table);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Column.class)).thenReturn(column);
			
			AddRemoveTableColumnsHandler.handleAddRemoveTableColumns(formData, metadataObj);

		}
	}
	@Test
	public void ut_b6_test_handleAddRemoveTableColumns() {
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		JsonObject object = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject object2 = new JsonObject();
		JsonArray tables = new JsonArray();
		JsonObject object3 = new JsonObject();
		JsonArray columns = new JsonArray();
		JsonObject object4 = new JsonObject();
		object4.addProperty("name", "cName");
		object4.addProperty("type", "text");
		columns.add(object4);
		object3.add("columns", columns);
		object3.addProperty("name", "TableName");
		object3.addProperty("alias", "TableAlias");
		tables.add(object3);
		object2.add("tables", tables);
		schemas.add(object2);
		object.add("schemas", schemas);
		catalogs.add(object);
		metadata.add("catalogs", catalogs);
		formData.add("metadata", metadata);
		JsonObject removeItem = new JsonObject();
		formData.add("removeItem", removeItem);

		Database data = mock(Database.class);
		Tables tablesObj = mock(Tables.class);
		Metadata metadataObj = mock(Metadata.class);
		Table table = mock(Table.class);
		List<Table> list = new ArrayList<>();
		list.add(table);
		
		Properties properties = mock(Properties.class);
		Column column = mock(Column.class);
		Columns tableColumns = mock(Columns.class);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		when(table.getName()).thenReturn("dummyTable");
		when(metadataObj.getDatabase()).thenReturn(data);
		when(data.getTables()).thenReturn(tablesObj);
		when(tablesObj.getTableList()).thenReturn(list);
		when(table.getColumns()).thenReturn(null);
		when(tableColumns.getColumn()).thenReturn(columnList);
		when(column.getName()).thenReturn("columnName");
		when(properties.getProperty(anyString())).thenReturn("property");
		String path = ApplicationProperties.INSTANCE.getSystemDirectory() + File.separator+"Admin"+File.separator+"defaultFunctions.properties";
		File file = new File(path);
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			try(MockedStatic<ConfigurationFileReader> mockedStatic1 = mockStatic(ConfigurationFileReader.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Table.class)).thenReturn(table);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Column.class)).thenReturn(column);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Columns.class)).thenReturn(tableColumns);
			mockedStatic1.when(()-> ConfigurationFileReader.getPropertiesFromFile(file)).thenReturn(properties);
			
			AddRemoveTableColumnsHandler.handleAddRemoveTableColumns(formData, metadataObj);
			}
		}
	}

	@Test
	public void ut_b7_test_handleAddRemoveTableColumns() {
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		JsonObject object = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject object2 = new JsonObject();
		JsonArray tables = new JsonArray();
		JsonObject object3 = new JsonObject();
		JsonArray columns = new JsonArray();
		JsonObject object4 = new JsonObject();
		object4.addProperty("name", "cName");
		object4.addProperty("type", "text");
		columns.add(object4);
		object3.add("columns", columns);
		object3.addProperty("name", "TableName");
		object3.addProperty("alias", "TableAlias");
		object3.addProperty("id", "1");
		tables.add(object3);
		object2.add("tables", tables);
		schemas.add(object2);
		object.add("schemas", schemas);
		catalogs.add(object);
		metadata.add("catalogs", catalogs);
		formData.add("metadata", metadata);
		JsonObject removeItem = new JsonObject();
		formData.add("removeItem", removeItem);

		Database data = mock(Database.class);
		Tables tablesObj = mock(Tables.class);
		Metadata metadataObj = mock(Metadata.class);
		Table table = mock(Table.class);
		List<Table> list = new ArrayList<>();
		list.add(table);
		
		Properties properties = mock(Properties.class);
		Column column = mock(Column.class);
		Columns tableColumns = mock(Columns.class);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		when(table.getName()).thenReturn("dummyTable");
		when(metadataObj.getDatabase()).thenReturn(data);
		when(data.getTables()).thenReturn(tablesObj);
		when(tablesObj.getTableList()).thenReturn(list);
		when(table.getColumns()).thenReturn(null);
		when(tableColumns.getColumn()).thenReturn(columnList);
		when(column.getName()).thenReturn("columnName");
		when(properties.getProperty(anyString())).thenReturn("property");
		when(table.getId()).thenReturn("1");
		String path = ApplicationProperties.INSTANCE.getSystemDirectory() + File.separator+"Admin"+File.separator+"defaultFunctions.properties";
		File file = new File(path);
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			try(MockedStatic<ConfigurationFileReader> mockedStatic1 = mockStatic(ConfigurationFileReader.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Table.class)).thenReturn(table);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Column.class)).thenReturn(column);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Columns.class)).thenReturn(tableColumns);
			mockedStatic1.when(()-> ConfigurationFileReader.getPropertiesFromFile(file)).thenReturn(properties);
			
			AddRemoveTableColumnsHandler.handleAddRemoveTableColumns(formData, metadataObj);
			}
		}
	}
	
	@Test
	public void ut_b8_test_newHandleAddNewTable() {
		
		List<JsonObject> newTableArray = new ArrayList<>();
		
		Database data = mock(Database.class);
		Tables tables = mock(Tables.class);
		Metadata metadata = mock(Metadata.class);
		Table table = mock(Table.class);
		List<Table> list = new ArrayList<>();
		list.add(table);
		
		when(metadata.getDatabase()).thenReturn(data);
		when(data.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(null);
		AddRemoveTableColumnsHandler.newHandleAddNewTable(metadata, newTableArray);
	}

	@Test
	public void ut_b9_test_handlRemoveItems() {
		Metadata metadata = new Metadata();
		Database data = new Database();
		Tables tables = new Tables();
		List<Table> list = new ArrayList<>();
		Table t1 = new Table();
		t1.setId("1");
		list.add(t1);
		tables.setTableList(list);
		data.setTables(tables);
		Views view =new Views();
		List<View> listView = new ArrayList<>();
		View v = new View();
		v.setId("23");
		listView.add(v);
		view.setViewList(listView);
		data.setViews(view);
		metadata.setDatabase(data);
		JsonObject removeItem = new JsonObject();
		JsonArray arrayTable = new JsonArray();
		JsonArray arrayColumns = new JsonArray();
		JsonArray arrayViews = new JsonArray();
		JsonObject jsonObject  = new JsonObject();
		JsonArray columns = new JsonArray();
		columns.add("rCol");
		jsonObject.addProperty("id", "11");
		jsonObject.add("columns", columns);
		
		arrayViews.add(jsonObject);
		removeItem.add("colmns", arrayColumns);
		removeItem.add("tables", arrayTable);
		removeItem.add("views", arrayViews);
		
		AddRemoveTableColumnsHandler.handlRemoveItems(metadata, removeItem);
		
	}
	
	@Test
	public void ut_c1_test_handlRemoveItems() {
		Metadata metadata = new Metadata();
		Database data = new Database();
		Tables tables = new Tables();
	
		data.setTables(tables);
		Views view =new Views();
		List<View> listView = new ArrayList<>();
		View v = new View();
		v.setId("23");
		listView.add(v);
		view.setViewList(listView);
		data.setViews(view);
		metadata.setDatabase(data);
		JsonObject removeItem = new JsonObject();
		JsonArray arrayTable = new JsonArray();
		JsonArray arrayColumns = new JsonArray();
		JsonArray arrayViews = new JsonArray();
		JsonObject jsonObject  = new JsonObject();
		JsonArray columns = new JsonArray();
		columns.add("rCol");
		jsonObject.addProperty("id", "11");
		jsonObject.add("columns", columns);
		
		arrayViews.add(jsonObject);
		removeItem.add("colmns", arrayColumns);
		removeItem.add("tables", arrayTable);
		removeItem.add("views", arrayViews);
		
		AddRemoveTableColumnsHandler.handlRemoveItems(metadata, removeItem);
		
	}
	
	@Test
	public void ut_c2_test_handlRemoveItems() {
		Metadata metadata = mock(Metadata.class);
		Database data = mock(Database.class);
		Tables tables = mock(Tables.class);
		Views views = mock(Views.class);
		View view = mock(View.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		
		List<View> viewList = new ArrayList<>();
		viewList.add(view);
		
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		List<Column> columnlist = new ArrayList<>();
		columnlist.add(column);
		
		when(metadata.getDatabase()).thenReturn(data);
		when(data.getViews()).thenReturn(views);
		when(views.getViewList()).thenReturn(viewList);
		when(view.getId()).thenReturn("11");
		when(data.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(table.getId()).thenReturn("11");
		when(table.getColumns()).thenReturn(columns);
		when(columns.getColumn()).thenReturn(columnlist);
		when(column.getId()).thenReturn("1");
		
		
		JsonObject removeItem = new JsonObject();
		JsonArray arrayColumns = new JsonArray();
		arrayColumns.add("1");
		JsonArray arrayViews = new JsonArray();
		JsonObject jsonObject  = new JsonObject();
		jsonObject.addProperty("id", "11");
		arrayViews.add(jsonObject);
		removeItem.add("columns", arrayColumns);
		removeItem.add("views", arrayViews);
		
		AddRemoveTableColumnsHandler.handlRemoveItems(metadata, removeItem);
		
	}

	@Test
	public void ut_c3_test_handlRemoveItems() {
		Metadata metadata = mock(Metadata.class);
		Database data = mock(Database.class);
		Tables tables = mock(Tables.class);
		Views views = mock(Views.class);
		View view = mock(View.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		
		List<View> viewList = new ArrayList<>();
		viewList.add(view);
		
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		List<Column> columnlist = new ArrayList<>();
		columnlist.add(column);
		
		when(metadata.getDatabase()).thenReturn(data);
		when(data.getViews()).thenReturn(views);
		when(views.getViewList()).thenReturn(viewList);
		when(view.getId()).thenReturn("11");
		when(data.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(table.getId()).thenReturn("11");
		when(table.getColumns()).thenReturn(columns);
		when(columns.getColumn()).thenReturn(columnlist);
		when(column.getId()).thenReturn("1");
		
		
		JsonObject removeItem = new JsonObject();
		JsonArray arrayColumns = new JsonArray();
		arrayColumns.add("1");
		JsonArray arrayViews = new JsonArray();
		JsonObject jsonObject  = new JsonObject();
		jsonObject.addProperty("id", "11");
		arrayViews.add(jsonObject);
		removeItem.add("columns", arrayColumns);
		removeItem.add("views", arrayViews);
		JsonArray tablesArray = new JsonArray();
		tablesArray.add("1");
		removeItem.add("tables", tablesArray);
		AddRemoveTableColumnsHandler.handlRemoveItems(metadata, removeItem);
		
	}

}
