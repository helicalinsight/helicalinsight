package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.efw.HIManagedThread;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WorkflowDatabaseTemplateTest {

	@Test
	public void ut_a1_test_startThread() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		WorkflowDatabaseTemplate databaseTemplate = new WorkflowDatabaseTemplate();
		JsonObject formData = new JsonObject();
		String catalog = "";
		String schema = "";
		Boolean[] handlerFlag = { true, false };
		Thread.UncaughtExceptionHandler handler = mock(Thread.UncaughtExceptionHandler.class);
		int counter = 11;
		List<Table> list = new ArrayList<>();
		Method method = WorkflowDatabaseTemplate.class.getDeclaredMethod("startThread", JsonObject.class, String.class,
				String.class, Boolean[].class, Thread.UncaughtExceptionHandler.class, int.class, List.class);
		method.setAccessible(true);
		Object invoke = method.invoke(databaseTemplate, formData, catalog, schema, handlerFlag, handler, counter, list);
		assertNull(invoke);
	}

	@Test
	public void ut_a2_test_startThread() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		WorkflowDatabaseTemplate databaseTemplate = new WorkflowDatabaseTemplate();
		JsonObject formData = new JsonObject();
		formData.addProperty("type", "type");
		String catalog = "";
		String schema = "";
		Boolean[] handlerFlag = { true, false };
		Thread.UncaughtExceptionHandler handler = mock(Thread.UncaughtExceptionHandler.class);
		int counter = 11;
		List<Table> list = new ArrayList<>();
		Method method = WorkflowDatabaseTemplate.class.getDeclaredMethod("startThread", JsonObject.class, String.class,
				String.class, Boolean[].class, Thread.UncaughtExceptionHandler.class, int.class, List.class);
		method.setAccessible(true);

		try (MockedConstruction<RelationshipsRetrievalThread> construction = mockConstruction(
				RelationshipsRetrievalThread.class, (mock, context) -> {

				})) {
			try (MockedConstruction<HIManagedThread> construction1 = mockConstruction(HIManagedThread.class,
					(mock, context) -> {

					})) {
				Object invoke = method.invoke(databaseTemplate, formData, catalog, schema, handlerFlag, handler,
						counter, list);
				assertNotNull(invoke);

			}
		}

	}

	@Test(expected = EfwServiceException.class)
	public void ut_a3_test_getDatabase() {
		WorkflowDatabaseTemplate databaseTemplate = new WorkflowDatabaseTemplate();
		Connection connection = mock(Connection.class);
		JsonObject formData = new JsonObject();
		JsonObject singleCatalog = new JsonObject();
		singleCatalog.addProperty("catalogs", "catalog");
		JsonArray schemas = new JsonArray();
		singleCatalog.add("schemas", schemas);
		;
		formData.add("singleCatalog", singleCatalog);

		databaseTemplate.getDatabase(connection, formData);

	}

	@Test
	public void ut_a4_test_getDatabase()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		WorkflowDatabaseTemplate databaseTemplate = new WorkflowDatabaseTemplate();
		Connection connection = mock(Connection.class);
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		ColumnsTemplate columnsTemplate = mock(ColumnsTemplate.class);

		JsonObject formData = new JsonObject();
		JsonObject singleCatalog = new JsonObject();
		singleCatalog.addProperty("catalogs", "catalog");
		JsonArray schemas = new JsonArray();
		JsonObject schemaJson = new JsonObject();
		schemaJson.addProperty("name", "schemaName");
		schemas.add(schemaJson);
		JsonArray tablesJson = new JsonArray();
		JsonObject object = new JsonObject();
		object.addProperty("name", "name");
		tablesJson.add(object);
		schemaJson.add("tables", tablesJson);
		singleCatalog.add("schemas", schemas);
		formData.add("singleCatalog", singleCatalog);

		Field field = WorkflowDatabaseTemplate.class.getDeclaredField("columnsTemplate");
		field.setAccessible(true);
		field.set(databaseTemplate, columnsTemplate);

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<MetadataMultiThreadingUtilities> mockedStatic1 = mockStatic(
					MetadataMultiThreadingUtilities.class)) {

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(Database.class)).thenReturn(database);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(Tables.class)).thenReturn(tables);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(Table.class)).thenReturn(table);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(Columns.class)).thenReturn(columns);

				mockedStatic1.when(() -> MetadataMultiThreadingUtilities.isMultiThreadingEnabled()).thenReturn(true);
				Database database2 = databaseTemplate.getDatabase(connection, formData);
				assertEquals(database, database2);

			}
		}

	}

	@Test
	public void ut_a5_test_getDatabase()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		WorkflowDatabaseTemplate databaseTemplate = new WorkflowDatabaseTemplate();
		Connection connection = mock(Connection.class);
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		ColumnsTemplate columnsTemplate = mock(ColumnsTemplate.class);

		JsonObject formData = new JsonObject();
		JsonObject singleCatalog = new JsonObject();
		singleCatalog.addProperty("catalogs", "catalog");
		JsonArray schemas = new JsonArray();
		JsonObject schemaJson = new JsonObject();
		schemaJson.addProperty("name", "schemaName");
		schemas.add(schemaJson);
		JsonArray tablesJson = new JsonArray();
		schemaJson.add("tables", tablesJson);
		singleCatalog.add("schemas", schemas);
		formData.add("singleCatalog", singleCatalog);

		Field field = WorkflowDatabaseTemplate.class.getDeclaredField("columnsTemplate");
		field.setAccessible(true);
		field.set(databaseTemplate, columnsTemplate);

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<MetadataMultiThreadingUtilities> mockedStatic1 = mockStatic(
					MetadataMultiThreadingUtilities.class)) {

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(Database.class)).thenReturn(database);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(Tables.class)).thenReturn(tables);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(Table.class)).thenReturn(table);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(Columns.class)).thenReturn(columns);

				mockedStatic1.when(() -> MetadataMultiThreadingUtilities.isMultiThreadingEnabled()).thenReturn(true);
				Database database2 = databaseTemplate.getDatabase(connection, formData);
				assertEquals(database, database2);

			}
		}

	}

	@Test(expected = MetadataRetrievalException.class)
	public void ut_a6_test_getDatabase() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, SQLException {
		WorkflowDatabaseTemplate databaseTemplate = new WorkflowDatabaseTemplate();
		Connection connection = mock(Connection.class);
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		ColumnsTemplate columnsTemplate = mock(ColumnsTemplate.class);

		JsonObject formData = new JsonObject();
		JsonObject singleCatalog = new JsonObject();
		singleCatalog.addProperty("catalogs", "catalog");
		JsonArray schemas = new JsonArray();
		JsonObject schemaJson = new JsonObject();
		schemaJson.addProperty("name", "schemaName");
		schemas.add(schemaJson);
		JsonArray tablesJson = new JsonArray();
		schemaJson.add("tables", tablesJson);
		singleCatalog.add("schemas", schemas);
		formData.add("singleCatalog", singleCatalog);

		Field field = WorkflowDatabaseTemplate.class.getDeclaredField("columnsTemplate");
		field.setAccessible(true);
		field.set(databaseTemplate, columnsTemplate);

		when(connection.getMetaData()).thenThrow(new SQLException());
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<MetadataMultiThreadingUtilities> mockedStatic1 = mockStatic(
					MetadataMultiThreadingUtilities.class)) {

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(Database.class)).thenReturn(database);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(Tables.class)).thenReturn(tables);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(Table.class)).thenReturn(table);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(Columns.class)).thenReturn(columns);

				mockedStatic1.when(() -> MetadataMultiThreadingUtilities.isMultiThreadingEnabled()).thenReturn(false);
				databaseTemplate.getDatabase(connection, formData);
				

			}
		}

	}
}
