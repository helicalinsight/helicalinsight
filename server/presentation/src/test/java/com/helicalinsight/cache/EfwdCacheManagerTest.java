package com.helicalinsight.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.cache.manager.EfwdCacheManager;
import com.helicalinsight.datasource.EnhancedQueryExecutor;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.IDriver;
import com.helicalinsight.datasource.QueryExecutor;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.GroovyUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

@RunWith(MockitoJUnitRunner.class)
public class EfwdCacheManagerTest {

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@InjectMocks
	private EfwdCacheManager efwdCacheManager;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testsetRequestData() {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		JSONObject data = new JSONObject();
		data.put("dir", "System/Temp");
		JSONObject efwd = new JSONObject();
		efwd.put("file", "ead71899-7382-41ec-a0f1-992bb1ecb9fa.efwd");
		efwd.put("dir", "1463377807724/1593413909773");
		data.put("efwd", efwd);
		String info = data.toString();
		cacheManager.setRequestData(info);
	}

	@Test
	public void testgetDiretor() {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		JSONObject data = new JSONObject();
		data.put("dir", "System/Tem");
		JSONObject efwd = new JSONObject();
		efwd.put("file", "ead71899-7382-41ec-a0f1-992bb1ecb9fa.efwd");
		efwd.put("dir", "1463377807724/1593413909773");
		data.put("efwd", efwd);
		String info = data.toString();
		cacheManager.setRequestData(info);

		cacheManager.getDirectory();

	}

	@Test
	public void testgetMapId() {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		JSONObject data = new JSONObject();
		data.put("dir", "System/Temp");
		Integer id = 11;
		data.put("map_id", id);
		JSONObject efwd = new JSONObject();
		efwd.put("file", "ead71899-7382-41ec-a0f1-992bb1ecb9fa.efwd");
		efwd.put("dir", "1463377807724/1593413909773");
		data.put("efwd", efwd);
		String info = data.toString();
		cacheManager.setRequestData(info);
		Integer mapId = cacheManager.getMapId();
		Assert.assertEquals(null, mapId);

	}

	@Test
	public void testgetRequestData() {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		String requestData = cacheManager.getRequestData();
		Assert.assertNull(requestData);
	}

	@Test(expected = NullPointerException.class)
	public void testServeCachedContent_WithFileContentAndLastModified() {
		// Arrange
		JsonObject fileContent = new JsonObject();
		fileContent.addProperty("data", "Sample data");
		long lastModifiedTime = 123456789L;
		when(request.getAttribute("lastModifiedCache")).thenReturn(lastModifiedTime);

		efwdCacheManager.serveCachedContent(request, response, fileContent);
	}

	@Test
	public void testServeCachedContent_WithIOException() throws IOException {
		// Arrange
		JsonObject fileContent = new JsonObject();
		fileContent.addProperty("data", "Sample data");

		when(response.getWriter()).thenThrow(new IOException());
		efwdCacheManager.serveCachedContent(request, response, fileContent);

	}

	// ----------------------

	@Test
	public void testGetDataFromDatabase()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		IDriver driverObject = mock(IDriver.class);
		JsonObject requestParameterJson = mock(JsonObject.class);
		Field field = EfwdCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		field.set(cacheManager, requestParameterJson);
		Field field2 = EfwdCacheManager.class.getDeclaredField("driverObject");
		field2.setAccessible(true);
		field2.set(cacheManager, driverObject);
		cacheManager.getDataFromDatabase("select * from Emp");
	}

	@Test
	public void testGetQuery_a1()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		Map<String, String> settingsDataSourcesMap = mock(Map.class);
		IDriver driverObject = mock(IDriver.class);
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("@type", "type");
		connectionDetails.addProperty("Condition", "where");

		Field field = EfwdCacheManager.class.getDeclaredField("settingsDataSourcesMap");
		field.setAccessible(true);
		field.set(cacheManager, settingsDataSourcesMap);
		Field field2 = EfwdCacheManager.class.getDeclaredField("driverObject");
		field2.setAccessible(true);
		field2.set(cacheManager, driverObject);
		Field field3 = EfwdCacheManager.class.getDeclaredField("connectionDetails");
		field3.setAccessible(true);
		field3.set(cacheManager, connectionDetails);

		when(settingsDataSourcesMap.get(anyString())).thenReturn("clazz");

		try (MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)) {
			try (MockedStatic<GlobalJdbcTypeUtils> globalJdbcTypeUtils = mockStatic(GlobalJdbcTypeUtils.class)) {
				try (MockedStatic<GroovyUtils> groovyUtils = mockStatic(GroovyUtils.class)) {
					mockedStatic.when(() -> FactoryMethodWrapper.getUntypedInstance("clazz")).thenReturn(driverObject);
					globalJdbcTypeUtils.when(() -> GlobalJdbcTypeUtils.isTypeGroovy("type")).thenReturn(true);
					groovyUtils.when(() -> GroovyUtils.executeGroovy("where", "evalCondition", JsonObject.class))
							.thenReturn(new JsonObject());
					cacheManager.getQuery("type");
				}
			}
		}

	}

	@Test
	public void testGetQuery_a2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		Map<String, String> settingsDataSourcesMap = mock(Map.class);
		IDriver driverObject = mock(IDriver.class);
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("@type", "type");
		connectionDetails.addProperty("Condition", "where");

		Field field = EfwdCacheManager.class.getDeclaredField("settingsDataSourcesMap");
		field.setAccessible(true);
		field.set(cacheManager, settingsDataSourcesMap);
		Field field2 = EfwdCacheManager.class.getDeclaredField("driverObject");
		field2.setAccessible(true);
		field2.set(cacheManager, driverObject);
		Field field3 = EfwdCacheManager.class.getDeclaredField("connectionDetails");
		field3.setAccessible(true);
		field3.set(cacheManager, connectionDetails);

		when(settingsDataSourcesMap.get(anyString())).thenReturn("clazz");

		try (MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)) {
			try (MockedStatic<GlobalJdbcTypeUtils> globalJdbcTypeUtils = mockStatic(GlobalJdbcTypeUtils.class)) {
				try (MockedStatic<GroovyUtils> groovyUtils = mockStatic(GroovyUtils.class)) {
					mockedStatic.when(() -> FactoryMethodWrapper.getUntypedInstance("clazz")).thenReturn(driverObject);
					globalJdbcTypeUtils.when(() -> GlobalJdbcTypeUtils.isTypeGroovy("type")).thenReturn(false);
					groovyUtils.when(() -> GroovyUtils.executeGroovy("where", "evalCondition", JsonObject.class))
							.thenReturn(new JsonObject());
					cacheManager.getQuery("type");
				}
			}
		}

	}

	@Test
	public void testGetQuery_a3()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		Map<String, String> settingsDataSourcesMap = mock(Map.class);
		IDriver driverObject = mock(IDriver.class);
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("@type", "type");
		connectionDetails.addProperty("Condition", "where");

		Field field = EfwdCacheManager.class.getDeclaredField("settingsDataSourcesMap");
		field.setAccessible(true);
		field.set(cacheManager, settingsDataSourcesMap);
		Field field2 = EfwdCacheManager.class.getDeclaredField("driverObject");
		field2.setAccessible(true);
		field2.set(cacheManager, driverObject);

		when(settingsDataSourcesMap.get(anyString())).thenReturn(null);

		try (MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)) {
			mockedStatic.when(() -> FactoryMethodWrapper.getUntypedInstance("clazz")).thenReturn(driverObject);

			cacheManager.getQuery("type");
		}

	}

	@Test
	public void testgetConnectionType_a1()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		EfwdCacheManager cacheManager = new EfwdCacheManager();
		JsonObject efwdFileAsJson = new JsonObject();
		JsonArray dataSource = new JsonArray();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("@id", 12l);
		jsonObject.addProperty("@type", "type");
		dataSource.add(jsonObject);
		efwdFileAsJson.add("DataSources", dataSource);
		Field field = EfwdCacheManager.class.getDeclaredField("efwdFileAsJson");
		field.setAccessible(true);
		field.set(cacheManager, efwdFileAsJson);

		Long cId = 12l;
		String connectionType = cacheManager.getConnectionType(cId);
		assertEquals(connectionType, "type");

	}

	@Test
	public void testgetConnectionType_a2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		EfwdCacheManager cacheManager = new EfwdCacheManager();
		JsonObject efwdFileAsJson = new JsonObject();
		JsonArray dataSource = new JsonArray();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("@id", 12l);
		dataSource.add(jsonObject);
		efwdFileAsJson.add("DataSources", dataSource);
		Field field = EfwdCacheManager.class.getDeclaredField("efwdFileAsJson");
		field.setAccessible(true);
		field.set(cacheManager, efwdFileAsJson);

		Long cId = 13l;
		String connectionType = cacheManager.getConnectionType(cId);
		assertNull(connectionType);

	}

	@Test
	public void testgetConnectionType_a3()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		EfwdCacheManager cacheManager = new EfwdCacheManager();
		JsonObject efwdFileAsJson = new JsonObject();
		JsonArray dataSource = new JsonArray();
		efwdFileAsJson.add("DataSources", dataSource);
		Field field = EfwdCacheManager.class.getDeclaredField("efwdFileAsJson");
		field.setAccessible(true);
		field.set(cacheManager, efwdFileAsJson);

		Long cId = 13l;
		String connectionType = cacheManager.getConnectionType(cId);
		assertNull(connectionType);

	}

	@Test
	public void testgetConnectionType_a4() {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		Long cId = 0l;
		String connectionType = cacheManager.getConnectionType(cId);
		assertNull(connectionType);
	}

	@Test
	public void testGetConnectionId_a1()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("dir", "AB:");
		requestParameterJson.addProperty("map_id", 11);

		Field field = EfwdCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		field.set(cacheManager, requestParameterJson);

		Field field2 = EfwdCacheManager.class.getDeclaredField("efwdFile");
		field2.setAccessible(true);
		String efwdFile = "file.efwd";
		field2.set(cacheManager, efwdFile);

		JsonObject efwdFileAsJson = new JsonObject();
		JsonArray dataMaps = new JsonArray();
		JsonObject dataMapTag = new JsonObject();
		dataMapTag.addProperty("@id", 11);
		dataMapTag.addProperty("@connection", 11);
		dataMaps.add(dataMapTag);
		efwdFileAsJson.add("DataMaps", dataMaps);
		Field field3 = EfwdCacheManager.class.getDeclaredField("efwdFileAsJson");
		field3.setAccessible(true);
		field3.set(cacheManager, efwdFileAsJson);
		try (MockedStatic<DataSourceSecurityUtility> mockedStatic = mockStatic(DataSourceSecurityUtility.class)) {
			Long connId = 11l;
			Long connectionId = cacheManager.getConnectionId();
			assertEquals(connectionId, connId);
		}
	}

	@Test
	public void testGetConnectionId_a2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("dir", "AB:");
		requestParameterJson.addProperty("map_id", 11);

		Field field = EfwdCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		field.set(cacheManager, requestParameterJson);

		Field field2 = EfwdCacheManager.class.getDeclaredField("efwdFile");
		field2.setAccessible(true);
		String efwdFile = "file.efwd";
		field2.set(cacheManager, efwdFile);

		JsonObject efwdFileAsJson = new JsonObject();
		JsonArray dataMaps = new JsonArray();
		JsonObject dataMapTag = new JsonObject();
		dataMapTag.addProperty("@id", 12);
		dataMapTag.addProperty("@connection", 11);
		dataMaps.add(dataMapTag);
		efwdFileAsJson.add("DataMaps", dataMaps);
		Field field3 = EfwdCacheManager.class.getDeclaredField("efwdFileAsJson");
		field3.setAccessible(true);
		field3.set(cacheManager, efwdFileAsJson);

		try (MockedStatic<DataSourceSecurityUtility> mockedStatic = mockStatic(DataSourceSecurityUtility.class)) {
			Long connId = 0l;
			Long connectionId = cacheManager.getConnectionId();
			assertEquals(connectionId, connId);
		}
	}

	@Test
	public void testGetConnectionId_a3()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("dir", "AB:");
		requestParameterJson.addProperty("map_id", 11);

		Field field = EfwdCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		field.set(cacheManager, requestParameterJson);

		Field field2 = EfwdCacheManager.class.getDeclaredField("efwdFile");
		field2.setAccessible(true);
		String efwdFile = "file.efwd";
		field2.set(cacheManager, efwdFile);

		JsonObject efwdFileAsJson = new JsonObject();
		JsonArray dataMaps = new JsonArray();
		efwdFileAsJson.add("DataMaps", dataMaps);
		Field field3 = EfwdCacheManager.class.getDeclaredField("efwdFileAsJson");
		field3.setAccessible(true);
		field3.set(cacheManager, efwdFileAsJson);

		try (MockedStatic<DataSourceSecurityUtility> mockedStatic = mockStatic(DataSourceSecurityUtility.class)) {
			Long connId = 0l;
			Long connectionId = cacheManager.getConnectionId();
			assertEquals(connectionId, connId);
		}
	}

	@Test
	public void testGetConnectionId_a4()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("dir", "System/Temp");
		requestParameterJson.addProperty("map_id", 11);
		JsonObject innerEfwdJson = new JsonObject();
		innerEfwdJson.addProperty("file", "file.efwd");
		requestParameterJson.add("efwd", innerEfwdJson);

		Field field = EfwdCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		field.set(cacheManager, requestParameterJson);

		try (MockedStatic<DataSourceSecurityUtility> dataSourceSecurityUtility = mockStatic(
				DataSourceSecurityUtility.class)) {
			Long connId = 0l;
			Long connectionId = cacheManager.getConnectionId();
			assertEquals(connectionId, connId);
		}

	}

	@Test(expected = NullPointerException.class)
	public void testGetConnectionFilePath_a1()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		ApplicationProperties applicationProperties = mock(ApplicationProperties.class);

		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("dir", "System/Temp");
		JsonObject efwd = new JsonObject();
		efwd.addProperty("file", "file.efwd");
		requestParameterJson.add("efwd", efwd);

		Field field = EfwdCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		field.set(cacheManager, requestParameterJson);

		try (MockedConstruction<EnhancedQueryExecutor> executor = mockConstruction(EnhancedQueryExecutor.class)) {
			EnhancedQueryExecutor queryExecutor = new EnhancedQueryExecutor(requestParameterJson.toString(),
					applicationProperties);

			cacheManager.getConnectionFilePath();

		}

	}

	@Test(expected = NullPointerException.class)
	public void testGetConnectionFilePath_a2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		ApplicationProperties applicationProperties = mock(ApplicationProperties.class);

		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("dir", "System/Temp");
		requestParameterJson.add("efwd", null);

		Field field = EfwdCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		field.set(cacheManager, requestParameterJson);

		try (MockedConstruction<EnhancedQueryExecutor> executor = mockConstruction(EnhancedQueryExecutor.class)) {
			EnhancedQueryExecutor queryExecutor = new EnhancedQueryExecutor(requestParameterJson.toString(),
					applicationProperties);

			cacheManager.getConnectionFilePath();

		}

	}

	@Test(expected = NullPointerException.class)
	public void testGetConnectionFilePath_a3()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		ApplicationProperties applicationProperties = mock(ApplicationProperties.class);

		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("dir", "Temp");
		JsonObject efwd = new JsonObject();
		efwd.addProperty("file", "file.efwd");
		efwd.addProperty("dir", "AB:");
		requestParameterJson.add("efwd", efwd);

		JsonObject json = mock(JsonObject.class);
		json.addProperty("_efwdFileName_", "file.efwd");

		Field field = EfwdCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		field.set(cacheManager, requestParameterJson);

		try (MockedConstruction<QueryExecutor> executor = mockConstruction(QueryExecutor.class)) {
			QueryExecutor mock = mock(QueryExecutor.class);
			cacheManager.getConnectionFilePath();
		}

	}

	@Test(expected = NullPointerException.class)
	public void testGetConnectionFilePath_a4()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		ApplicationProperties applicationProperties = mock(ApplicationProperties.class);

		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("dir", "Temp");
		JsonObject efwd = new JsonObject();
		efwd.add("file", null);
		efwd.add("dir", null);
		requestParameterJson.add("efwd", efwd);

		JsonObject json = mock(JsonObject.class);
		json.addProperty("_efwdFileName_", "file.efwd");

		Field field = EfwdCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		field.set(cacheManager, requestParameterJson);

		try (MockedConstruction<QueryExecutor> executor = mockConstruction(QueryExecutor.class)) {
			QueryExecutor mock = mock(QueryExecutor.class);
			cacheManager.getConnectionFilePath();
		}

	}

	@Test
	public void testInit() {
		EfwdCacheManager cacheManager = new EfwdCacheManager();
		try (MockedStatic<ApplicationProperties> mocked = mockStatic(ApplicationProperties.class)) {
			ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
			mocked.when(() -> ApplicationProperties.getInstance()).thenReturn(applicationProperties);
			when(applicationProperties.getSettingPath()).thenReturn("path");
			when(applicationProperties.getSolutionDirectory()).thenReturn("AB:");
			try (MockedStatic<ResourceProcessorFactory> resourceProcessorFactory = mockStatic(
					ResourceProcessorFactory.class)) {
				IProcessor processor = mock(IProcessor.class);
				resourceProcessorFactory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
				JsonObject object = new JsonObject();
				JsonObject dataSources = new JsonObject();
				JsonArray dataSource = new JsonArray();
				JsonObject data = new JsonObject();
				data.addProperty("type", "type");
				data.addProperty("class", "class");
				dataSource.add(data);
				dataSources.add("DataSource", dataSource);
				object.add("DataSources", dataSources);
				when(processor.getJsonObject(anyString(), any(Boolean.class))).thenReturn(object);
				cacheManager.init();
			}

		}

	}
}
