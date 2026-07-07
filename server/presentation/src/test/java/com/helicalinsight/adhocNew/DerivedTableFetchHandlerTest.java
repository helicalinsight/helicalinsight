package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.DerivedTableFetchHandler;
import com.helicalinsight.adhoc.ViewLabelsRetrievalComponent;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataUtils;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.adhoc.metadata.jaxb.View;
import com.helicalinsight.adhoc.metadata.jaxb.Views;
import com.helicalinsight.adhoc.metadata.jaxb.View.Query;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DerivedTableFetchHandlerTest {

	@Test(expected = RequiredParameterIsNullException.class)
	public void ut_a1_test_executeComponent() {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();
		JsonObject formData = new JsonObject();
		fetchHandler.executeComponent(formData.toString());
	}

	@Test(expected = InvocationTargetException.class)
	public void ut_a2_test_getView() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();

		Method method = DerivedTableFetchHandler.class.getDeclaredMethod("getView", String.class, String.class,
				String.class, String.class);
		method.setAccessible(true);
		String location = null;
		String metadataFileName = null;
		String viewId = "view";
		String connectionId = "conn";
		method.invoke(fetchHandler, location, metadataFileName, viewId, connectionId);
	}

	@Test
	public void ut_a3_test_getView() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();

		Method method = DerivedTableFetchHandler.class.getDeclaredMethod("getView", String.class, String.class,
				String.class, String.class);
		method.setAccessible(true);
		String location = null;
		String metadataFileName = null;
		String viewId = "view";
		String connectionId = "conn";
		
		String path = TempDirectoryCleaner.getTempDirectory() + File.separator+"view.xml";
		File file = new File(path);
		file.createNewFile();

		View view = mock(View.class);
		try (MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)) {
			mockedStatic.when(() -> JaxbUtils.unMarshal(View.class, file)).thenReturn(view);
			Object invoke = method.invoke(fetchHandler, location, metadataFileName, viewId, connectionId);
			assertEquals(view, invoke);
		} finally {
			file.delete();
		}
	}

	@Test
	public void ut_a4_test_getView() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();

		Method method = DerivedTableFetchHandler.class.getDeclaredMethod("getView", String.class, String.class,
				String.class, String.class);
		method.setAccessible(true);
		String location = "location";
		String metadataFileName = "metadataFileName";
		String viewId = "view";
		String connectionId = "conn";

		String path = TempDirectoryCleaner.getTempDirectory() + File.separator+"view.xml";
		File file = new File(path);
		file.createNewFile();

		View view = mock(View.class);
		try (MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)) {
			mockedStatic.when(() -> JaxbUtils.unMarshal(View.class, file)).thenReturn(view);
			Object invoke = method.invoke(fetchHandler, location, metadataFileName, viewId, connectionId);
			assertEquals(view, invoke);
		} finally {
			file.delete();
		}
	}

	@Test
	public void ut_a5_test_response() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();

		Table table = mock(Table.class);
		Query query = mock(Query.class);
		View view = mock(View.class);
		JsonObject formData = new JsonObject();

		JsonObject json = new JsonObject();
		JsonArray labels = new JsonArray();
		json.add("labels", labels);

		Method method = DerivedTableFetchHandler.class.getDeclaredMethod("response", JsonObject.class, View.class);
		method.setAccessible(true);

		when(view.getTable()).thenReturn(table);
		when(table.getColumns()).thenReturn(null);
		when(view.getQuery()).thenReturn(query);
		when(query.getUnprocessedQuery()).thenReturn("query");
		when(query.getType()).thenReturn("type");
		try (MockedConstruction<ViewLabelsRetrievalComponent> construction = mockConstruction(
				ViewLabelsRetrievalComponent.class, (mock, context) -> {
					when(mock.executeComponent(anyString())).thenReturn(json.toString());
				})) {
			Object invoke = method.invoke(fetchHandler, formData, view);
			JsonObject jsonObject = JsonParser.parseString(invoke.toString()).getAsJsonObject();
			assertTrue(jsonObject.getAsJsonArray("labels").isEmpty());
		}

	}

	@Test
	public void ut_a6_test_response() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();

		Table table = mock(Table.class);
		Query query = mock(Query.class);
		View view = mock(View.class);
		JsonObject formData = new JsonObject();

		JsonObject json = new JsonObject();
		JsonArray labels = new JsonArray();
		labels.add(new JsonObject());
		json.add("labels", labels);

		Method method = DerivedTableFetchHandler.class.getDeclaredMethod("response", JsonObject.class, View.class);
		method.setAccessible(true);

		when(view.getTable()).thenReturn(table);
		when(table.getColumns()).thenReturn(null);
		when(view.getQuery()).thenReturn(query);
		when(query.getUnprocessedQuery()).thenReturn("query");
		when(query.getType()).thenReturn("type");
		try (MockedConstruction<ViewLabelsRetrievalComponent> construction = mockConstruction(
				ViewLabelsRetrievalComponent.class, (mock, context) -> {
					when(mock.executeComponent(anyString())).thenReturn(json.toString());
				})) {
			Object invoke = method.invoke(fetchHandler, formData, view);
			JsonObject jsonObject = JsonParser.parseString(invoke.toString()).getAsJsonObject();
			assertFalse(jsonObject.getAsJsonArray("labels").isEmpty());
		}

	}

	@Test
	public void ut_a7_test_populateExistingTable() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();

		Table table = mock(Table.class);
		View view = mock(View.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);

		JsonArray labels = new JsonArray();
		labels.add(new JsonObject());
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);

		when(column.getAliasName()).thenReturn("aliasName");
		when(column.getType()).thenReturn("type");
		when(view.getTable()).thenReturn(table);
		when(table.getColumns()).thenReturn(columns);
		when(columns.getColumn()).thenReturn(columnList);

		Method method = DerivedTableFetchHandler.class.getDeclaredMethod("populateExistingTable", View.class,
				JsonArray.class);
		method.setAccessible(true);
		method.invoke(fetchHandler, view, labels);

	}

	@Test(expected = InvocationTargetException.class)
	public void ut_a8_test_fetchViewFromMetadata() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();

		Method method = DerivedTableFetchHandler.class.getDeclaredMethod("fetchViewFromMetadata", String.class,
				String.class, String.class, String.class);
		method.setAccessible(true);
		String location = null;
		String metadataFileName = null;
		String viewId = "view";
		String connectionId = "conn";
		method.invoke(fetchHandler, location, metadataFileName, viewId, connectionId);

	}

	@Test(expected = InvocationTargetException.class)
	public void ut_a9_test_fetchViewFromMetadata() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();

		Method method = DerivedTableFetchHandler.class.getDeclaredMethod("fetchViewFromMetadata", String.class,
				String.class, String.class, String.class);
		method.setAccessible(true);
		String location = "location";
		String metadataFileName = "metadataFileName";
		String viewId = "view";
		String connectionId = "conn";

		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		HIMetadataResourceServiceDB metadataService = mock(HIMetadataResourceServiceDB.class);

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
					.thenReturn(serviceDB);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class))
					.thenReturn(metadataService);

			method.invoke(fetchHandler, location, metadataFileName, viewId, connectionId);

		}

	}

	@Test(expected = InvocationTargetException.class)
	public void ut_b1_test_fetchViewFromMetadata() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();

		Method method = DerivedTableFetchHandler.class.getDeclaredMethod("fetchViewFromMetadata", String.class,
				String.class, String.class, String.class);
		method.setAccessible(true);
		String location = "location";
		String metadataFileName = "metadataFileName";
		String viewId = "view";
		String connectionId = "conn";

		Database database = mock(Database.class);
		Metadata metadata = mock(Metadata.class);
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		HIMetadataResourceServiceDB metadataService = mock(HIMetadataResourceServiceDB.class);
		HIResource metadataResource = mock(HIResource.class);

		when(metadata.getDatabase()).thenReturn(database);
		when(metadataService.getHIResourceMetadataByResourceId(anyInt())).thenReturn(metadata);
		when(metadataResource.getResourceId()).thenReturn(12);
		when(serviceDB.getResourceByUrl(anyString())).thenReturn(metadataResource);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<MetadataUtils> mockedStatic2 = mockStatic(MetadataUtils.class)) {
				mockedStatic2.when(() -> MetadataUtils.getNewMetadata(anyString(), any(Metadata.class)))
						.thenReturn(metadata);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
						.thenReturn(serviceDB);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class))
						.thenReturn(metadataService);

				method.invoke(fetchHandler, location, metadataFileName, viewId, connectionId);
			}
		}

	}

	@Test(expected = InvocationTargetException.class)
	public void ut_b2_test_fetchViewFromMetadata() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();

		Method method = DerivedTableFetchHandler.class.getDeclaredMethod("fetchViewFromMetadata", String.class,
				String.class, String.class, String.class);
		method.setAccessible(true);
		String location = "location";
		String metadataFileName = "metadataFileName";
		String viewId = "view";
		String connectionId = "conn";

		Database database = mock(Database.class);
		Metadata metadata = mock(Metadata.class);
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		HIMetadataResourceServiceDB metadataService = mock(HIMetadataResourceServiceDB.class);
		HIResource metadataResource = mock(HIResource.class);
		View view = mock(View.class);

		when(metadata.getDatabase()).thenReturn(database);
		when(metadataService.getHIResourceMetadataByResourceId(anyInt())).thenReturn(metadata);
		when(metadataResource.getResourceId()).thenReturn(12);
		when(serviceDB.getResourceByUrl(anyString())).thenReturn(metadataResource);

		String path = TempDirectoryCleaner.getTempDirectory() + File.separator+"view.xml";
		File file = new File(path);
		file.createNewFile();

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<MetadataUtils> mockedStatic2 = mockStatic(MetadataUtils.class)) {

				try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
					mockedStatic3.when(() -> JaxbUtils.unMarshal(View.class, file)).thenReturn(null);

					mockedStatic2.when(() -> MetadataUtils.getNewMetadata(anyString(), any(Metadata.class)))
							.thenReturn(metadata);

					mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
							.thenReturn(serviceDB);
					mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class))
							.thenReturn(metadataService);

					method.invoke(fetchHandler, location, metadataFileName, viewId, connectionId);
				}
			}
		} finally {
			file.delete();
		}

	}

	@Test
	public void ut_b3_test_fetchViewFromMetadata() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();

		Method method = DerivedTableFetchHandler.class.getDeclaredMethod("fetchViewFromMetadata", String.class,
				String.class, String.class, String.class);
		method.setAccessible(true);
		String location = "location";
		String metadataFileName = "metadataFileName";
		String viewId = "view";
		String connectionId = "conn";

		Database database = mock(Database.class);
		Metadata metadata = mock(Metadata.class);
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		HIMetadataResourceServiceDB metadataService = mock(HIMetadataResourceServiceDB.class);
		HIResource metadataResource = mock(HIResource.class);
		View view = mock(View.class);

		when(metadata.getDatabase()).thenReturn(database);
		when(metadataService.getHIResourceMetadataByResourceId(anyInt())).thenReturn(metadata);
		when(metadataResource.getResourceId()).thenReturn(12);
		when(serviceDB.getResourceByUrl(anyString())).thenReturn(metadataResource);

		String path = TempDirectoryCleaner.getTempDirectory() + File.separator+"view.xml";
		File file = new File(path);
		file.createNewFile();

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<MetadataUtils> mockedStatic2 = mockStatic(MetadataUtils.class)) {

				try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
					mockedStatic3.when(() -> JaxbUtils.unMarshal(View.class, file)).thenReturn(view);

					mockedStatic2.when(() -> MetadataUtils.getNewMetadata(anyString(), any(Metadata.class)))
							.thenReturn(metadata);

					mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
							.thenReturn(serviceDB);
					mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class))
							.thenReturn(metadataService);

					Object invoke = method.invoke(fetchHandler, location, metadataFileName, viewId, connectionId);
					assertEquals(view, invoke);
				}
			}
		} finally {
			file.delete();
		}

	}

	
	@Test(expected = InvocationTargetException.class)
	public void ut_b4_test_fetchViewFromMetadata() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();

		Method method = DerivedTableFetchHandler.class.getDeclaredMethod("fetchViewFromMetadata", String.class,
				String.class, String.class, String.class);
		method.setAccessible(true);
		String location = "location";
		String metadataFileName = "metadataFileName";
		String viewId = "view";
		String connectionId = "conn";

		Database database = mock(Database.class);
		Metadata metadata = mock(Metadata.class);
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		HIMetadataResourceServiceDB metadataService = mock(HIMetadataResourceServiceDB.class);
		HIResource metadataResource = mock(HIResource.class);
		View view = mock(View.class);
		Views views =mock(Views.class);
		
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getViews()).thenReturn(views);
		when(views.getViewList()).thenReturn(null);
		when(metadataService.getHIResourceMetadataByResourceId(anyInt())).thenReturn(metadata);
		when(metadataResource.getResourceId()).thenReturn(12);
		when(serviceDB.getResourceByUrl(anyString())).thenReturn(metadataResource);

		String path = TempDirectoryCleaner.getTempDirectory() + File.separator+"view.xml";
		File file = new File(path);
		file.createNewFile();

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<MetadataUtils> mockedStatic2 = mockStatic(MetadataUtils.class)) {

				try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
					mockedStatic3.when(() -> JaxbUtils.unMarshal(View.class, file)).thenReturn(view);

					mockedStatic2.when(() -> MetadataUtils.getNewMetadata(anyString(), any(Metadata.class)))
							.thenReturn(metadata);

					mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
							.thenReturn(serviceDB);
					mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class))
							.thenReturn(metadataService);

					method.invoke(fetchHandler, location, metadataFileName, viewId, connectionId);
					
				}
			}
		} finally {
			file.delete();
		}

	}

	
	@Test
	public void ut_b5_test_fetchViewFromMetadata() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();

		Method method = DerivedTableFetchHandler.class.getDeclaredMethod("fetchViewFromMetadata", String.class,
				String.class, String.class, String.class);
		method.setAccessible(true);
		String location = "location";
		String metadataFileName = "metadataFileName";
		String viewId = "view";
		String connectionId = "conn";

		Table table = mock(Table.class);
		Tables tables = mock(Tables.class);
		Database database = mock(Database.class);
		Metadata metadata = mock(Metadata.class);
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		HIMetadataResourceServiceDB metadataService = mock(HIMetadataResourceServiceDB.class);
		HIResource metadataResource = mock(HIResource.class);
		View view = mock(View.class);
		Views views =mock(Views.class);
		List<View> viewList = new ArrayList<>();
		viewList.add(view);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		
		when(table.getId()).thenReturn("view");
		when(view.getId()).thenReturn("view");
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getViews()).thenReturn(views);
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(views.getViewList()).thenReturn(viewList);
		when(metadataService.getHIResourceMetadataByResourceId(anyInt())).thenReturn(metadata);
		when(metadataResource.getResourceId()).thenReturn(12);
		when(serviceDB.getResourceByUrl(anyString())).thenReturn(metadataResource);

		String path = TempDirectoryCleaner.getTempDirectory() + File.separator+"view.xml";
		File file = new File(path);
		file.createNewFile();

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<MetadataUtils> mockedStatic2 = mockStatic(MetadataUtils.class)) {

				try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
					mockedStatic3.when(() -> JaxbUtils.unMarshal(View.class, file)).thenReturn(view);

					mockedStatic2.when(() -> MetadataUtils.getNewMetadata(anyString(), any(Metadata.class)))
							.thenReturn(metadata);

					mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
							.thenReturn(serviceDB);
					mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class))
							.thenReturn(metadataService);

					Object invoke = method.invoke(fetchHandler, location, metadataFileName, viewId, connectionId);
					assertEquals(view, invoke);
				}
			}
		} finally {
			file.delete();
		}

	}

	
	@Test(expected = InvocationTargetException.class)
	public void ut_b6_test_fetchViewFromMetadata() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();

		Method method = DerivedTableFetchHandler.class.getDeclaredMethod("fetchViewFromMetadata", String.class,
				String.class, String.class, String.class);
		method.setAccessible(true);
		String location = "location";
		String metadataFileName = "metadataFileName";
		String viewId = "view";
		String connectionId = "conn";

		Table table = mock(Table.class);
		Tables tables = mock(Tables.class);
		Database database = mock(Database.class);
		Metadata metadata = mock(Metadata.class);
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		HIMetadataResourceServiceDB metadataService = mock(HIMetadataResourceServiceDB.class);
		HIResource metadataResource = mock(HIResource.class);
		View view = mock(View.class);
		Views views =mock(Views.class);
		List<View> viewList = new ArrayList<>();
		viewList.add(view);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		
		when(table.getId()).thenReturn("view");
		when(view.getId()).thenReturn("viewww");
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getViews()).thenReturn(views);
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(views.getViewList()).thenReturn(viewList);
		when(metadataService.getHIResourceMetadataByResourceId(anyInt())).thenReturn(metadata);
		when(metadataResource.getResourceId()).thenReturn(12);
		when(serviceDB.getResourceByUrl(anyString())).thenReturn(metadataResource);

		String path = TempDirectoryCleaner.getTempDirectory() + File.separator+"view.xml";
		File file = new File(path);
		file.createNewFile();

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<MetadataUtils> mockedStatic2 = mockStatic(MetadataUtils.class)) {

				try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
					mockedStatic3.when(() -> JaxbUtils.unMarshal(View.class, file)).thenReturn(null);

					mockedStatic2.when(() -> MetadataUtils.getNewMetadata(anyString(), any(Metadata.class)))
							.thenReturn(metadata);

					mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
							.thenReturn(serviceDB);
					mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class))
							.thenReturn(metadataService);

				    method.invoke(fetchHandler, location, metadataFileName, viewId, connectionId);
					
				}
			}
		} finally {
			file.delete();
		}

	}
	
	@Test
	public void ut_b8_test_isThreadSafeToCache() {
		DerivedTableFetchHandler fetchHandler = new DerivedTableFetchHandler();
		boolean threadSafeToCache = fetchHandler.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}

}
