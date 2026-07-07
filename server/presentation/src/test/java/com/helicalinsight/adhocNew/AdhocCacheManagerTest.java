package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.sql.ResultSet;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.AdhocCacheManager;
import com.helicalinsight.adhoc.DatabaseQueryGenerator;
import com.helicalinsight.adhoc.services.QueryGeneratorService;
import com.helicalinsight.admin.service.ComponentFactory;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.efw.AppStatistics;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.serviceframework.ServiceUtils;
import com.helicalinsight.efw.utility.ResultSetToJson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdhocCacheManagerTest {
	
	@Test
	public void ut_a1_test_getRequestData() {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		String requestData = adhocCacheManager.getRequestData();
		assertNull(requestData);
	}
	
	@Test
	public void ut_a2_test_setRequestParameterJson() {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		adhocCacheManager.setRequestParameterJson(new JsonObject());
		
	}

	@Test
	public void ut_a3_test_setRequestData() {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		JsonObject formData = new JsonObject();
		formData.addProperty("requestType", "dataSource");
		formData.addProperty("serviceType", "dataType");
		formData.addProperty("service", "service");
		formData.addProperty("classifier", "classifier");
		
		JsonObject metadataFileJson = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		metadataFileJson.addProperty("isCached", true);
		connectionDetails.addProperty("connectionType", "connectionType");
		connectionDetails.addProperty("connectionId", "connectionId");
		connectionDetails.addProperty("directory", "directory");
		
		
		metadataFileJson.add("connectionDetails", connectionDetails);
		formData.add("metadataFileJson", metadataFileJson);
		
		String formdata = formData.toString();
		
		try(MockedConstruction<QueryGeneratorService> construction = mockConstruction(QueryGeneratorService.class,(mock,context)->{
			when(mock.newFindConfigurationSettings(anyString())).thenReturn(formData);
		})){
			try(MockedStatic<DataSourceSecurityUtility> mockedStatic1 = mockStatic(DataSourceSecurityUtility.class)){
				try(MockedStatic<ServiceUtils> mockedStatic2 = mockStatic(ServiceUtils.class)){
					mockedStatic2.when(()-> ServiceUtils.componentJson(anyString(), anyString(), anyString(), anyString()))
					.thenReturn("componentJson");
					adhocCacheManager.setRequestData(formdata);
				}
			}
		}
	}
	
	@Test
	public void ut_a4_test_setRequestData() {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		JsonObject formData = new JsonObject();
		formData.addProperty("requestType", "dataSource");
		formData.addProperty("serviceType", "dataType");
		formData.addProperty("service", "service");
		formData.addProperty("classifier", "classifier");
		
		JsonObject metadataFileJson = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		metadataFileJson.addProperty("isCached", true);
		connectionDetails.addProperty("connectionType", "global.jdbc");
		connectionDetails.addProperty("connectionId", "connectionId");
		connectionDetails.addProperty("directory", "directory");
		
		
		metadataFileJson.add("connectionDetails", connectionDetails);
		formData.add("metadataFileJson", metadataFileJson);
		
		String formdata = formData.toString();
		
		try(MockedConstruction<QueryGeneratorService> construction = mockConstruction(QueryGeneratorService.class,(mock,context)->{
			when(mock.newFindConfigurationSettings(anyString())).thenReturn(formData);
		})){
			try(MockedStatic<DataSourceSecurityUtility> mockedStatic1 = mockStatic(DataSourceSecurityUtility.class)){
				try(MockedStatic<ServiceUtils> mockedStatic2 = mockStatic(ServiceUtils.class)){
					try(MockedStatic<AppStatistics> mockedStatic3 = mockStatic(AppStatistics.class)){
						mockedStatic3.when(()-> AppStatistics.isSPARK_STARTED()).thenReturn(true);
						mockedStatic3.when(()-> AppStatistics.isMASTER_STARTED()).thenReturn(true);
						mockedStatic2.when(()-> ServiceUtils.componentJson(anyString(), anyString(), anyString(), anyString()))
						.thenReturn("componentJson");
						mockedStatic2.when(()-> ServiceUtils.componentClass(anyString()))
						.thenReturn("componentClass");
						adhocCacheManager.setRequestData(formdata);
					}
				}
			}
		}
	}

	@Test
	public void ut_a5_test_setRequestData() {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		JsonObject formData = new JsonObject();
		formData.addProperty("requestType", "dataSource");
		formData.addProperty("serviceType", "dataType");
		formData.addProperty("service", "service");
		formData.addProperty("classifier", "classifier");
		
		String formdata = formData.toString();
		
		try(MockedConstruction<QueryGeneratorService> construction = mockConstruction(QueryGeneratorService.class,(mock,context)->{
			when(mock.newFindConfigurationSettings(anyString())).thenReturn(null);
		})){
			try(MockedStatic<ServiceUtils> mockedStatic2 = mockStatic(ServiceUtils.class)){
				mockedStatic2.when(()-> ServiceUtils.componentClass(null))
				.thenReturn("componentClass");
				adhocCacheManager.setRequestData(formdata);
			}		
		}
	}

	@Test
	public void ut_a5_test_getConnectionFilePath() {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		JsonObject json = new JsonObject();
		json.addProperty("location", "AB");
		json.addProperty("metadataFileName", "dummy");
		adhocCacheManager.setRequestParameterJson(json);
		String connectionFilePath = adhocCacheManager.getConnectionFilePath();
		assertEquals("AB"+File.separator+"dummy", connectionFilePath);
	}
	@Test
	public void ut_a6_test_getConnectionFilePath() {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		JsonObject json = new JsonObject();
		json.addProperty("uniqueId", "meta123");
		adhocCacheManager.setRequestParameterJson(json);
		String connectionFilePath = adhocCacheManager.getConnectionFilePath();
		assertEquals(File.separator + "meta123", connectionFilePath);
	}
	@Test
	public void ut_a7_test_getDirectory() {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		JsonObject json = new JsonObject();
		json.addProperty("location", "A/dummy.metadata");
		adhocCacheManager.setRequestParameterJson(json);
		String directory = adhocCacheManager.getDirectory();
		assertEquals("A/dummy.metadata", directory);
	}
	
	@Test
	public void ut_a8_test_getQuery() {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		JsonObject json = new JsonObject();
		json.addProperty("location", "dummy");
		json.addProperty("query", "query");
		adhocCacheManager.setRequestParameterJson(json);
		
		try(MockedConstruction<DatabaseQueryGenerator> construction = mockConstruction(DatabaseQueryGenerator.class,(mock,context)->{
			when(mock.executeComponent(anyString())).thenReturn(json.toString());
		})){
			String query = adhocCacheManager.getQuery("connectionType");
			assertEquals("query",query);
		}
	}
	
	@Test
	public void ut_a9_test_getQuery() {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		
		JsonObject formData = new JsonObject();
		formData.addProperty("requestType", "dataSource");
		formData.addProperty("serviceType", "dataType");
		formData.addProperty("service", "service");
		formData.addProperty("classifier", "classifier");
		formData.addProperty("query", "query");
		JsonObject metadataFileJson = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		metadataFileJson.addProperty("isCached", true);
		connectionDetails.addProperty("connectionType", "global.jdbc");
		connectionDetails.addProperty("connectionId", "connectionId");
		connectionDetails.addProperty("directory", "directory");
		
		
		metadataFileJson.add("connectionDetails", connectionDetails);
		formData.add("metadataFileJson", metadataFileJson);
		
		String formdata = formData.toString();
		
		adhocCacheManager.setRequestParameterJson(formData);
		ObjectNode objectNode = new ObjectNode(null);
		try(MockedConstruction<DatabaseQueryGenerator> construction = mockConstruction(DatabaseQueryGenerator.class,(mock,context)->{
			when(mock.executeComponent(anyString())).thenReturn(formdata);
		})){
			try(MockedStatic<GlobalJdbcTypeUtils> mockedStatic = mockStatic(GlobalJdbcTypeUtils.class)){
				mockedStatic.when(() -> GlobalJdbcTypeUtils.isTypeGroovy(anyString())).thenReturn(true);
				mockedStatic.when(() -> GlobalJdbcTypeUtils.getSwitchedConnection(anyString(),anyString())).thenReturn(objectNode);
				String query = adhocCacheManager.getQuery("connectionType");
				assertEquals("query##__{}__##",query);
			}
			
		}
	}
	@Test
	public void ut_b1_test_getConnectionId() {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		Long connectionId = adhocCacheManager.getConnectionId();
		Long id = 0l;
		assertEquals(id,connectionId);
	}
	@Test
	public void ut_b2_test_getMapId() {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		Integer mapId = adhocCacheManager.getMapId();
		Integer id = 0;
		assertEquals(id,mapId);
	}
	@Test
	public void ut_b3_test_getDataFromDatabase() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		
		JsonObject formData = new JsonObject();
		formData.addProperty("requestType", "dataSource");
		formData.addProperty("serviceType", "dataType");
		formData.addProperty("service", "service");
		formData.addProperty("classifier", "classifier");
		
		JsonObject metadataFileJson = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		metadataFileJson.addProperty("isCached", true);
		connectionDetails.addProperty("connectionType", "global.jdbc");
		connectionDetails.addProperty("connectionId", "connectionId");
		connectionDetails.addProperty("directory", "directory");
		
		metadataFileJson.add("connectionDetails", connectionDetails);
		formData.add("metadataFileJson", metadataFileJson);
		adhocCacheManager.setRequestParameterJson(formData);
		
		Field field = AdhocCacheManager.class.getDeclaredField("classifier");
		field.setAccessible(true);
		field.set(adhocCacheManager, "classifier");
		
		IComponent iComponent = mock(IComponent.class);
		ResultSet resultSet = mock(ResultSet.class);
		when(iComponent.componentLogic(anyString())).thenReturn(resultSet);
		try(MockedStatic<ServiceUtils> mockedStatic2 = mockStatic(ServiceUtils.class)){
			try(MockedStatic<ComponentFactory> mockedStatic1 = mockStatic(ComponentFactory.class)){
				mockedStatic1.when(()-> ComponentFactory.getComponentInstance(anyString())).thenReturn(iComponent);
				mockedStatic2.when(()-> ServiceUtils.componentJson(anyString(), anyString(), anyString(), anyString()))
				.thenReturn("componentJson");
				mockedStatic2.when(()-> ServiceUtils.componentClass(anyString()))
				.thenReturn("componentClass");
				
				ResultSet dataFromDatabase = adhocCacheManager.getDataFromDatabase("query");
				assertEquals(resultSet,dataFromDatabase);
			}
			
		}	
		
	}
	
	
	@Test
	public void ut_b4_test_getDataFromDatabase() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		
		JsonObject formData = new JsonObject();
		formData.addProperty("requestType", "dataSource");
		formData.addProperty("serviceType", "dataType");
		formData.addProperty("service", "service");
		formData.addProperty("classifier", "classifier");
		
		JsonObject metadataFileJson = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		metadataFileJson.addProperty("isCached", true);
		connectionDetails.addProperty("connectionType", "global.jdbc");
		connectionDetails.addProperty("connectionId", "connectionId");
		connectionDetails.addProperty("directory", "directory");
		
		metadataFileJson.add("connectionDetails", connectionDetails);
		formData.add("metadataFileJson", metadataFileJson);
		adhocCacheManager.setRequestParameterJson(formData);
		
		Field field = AdhocCacheManager.class.getDeclaredField("classifier");
		field.setAccessible(true);
		field.set(adhocCacheManager, "classifier");
		
		IComponent iComponent = mock(IComponent.class);
		ResultSet resultSet = mock(ResultSet.class);
		when(iComponent.componentLogic(anyString())).thenReturn(resultSet);
		try(MockedStatic<ServiceUtils> mockedStatic2 = mockStatic(ServiceUtils.class)){
			try(MockedStatic<AppStatistics> mockedStatic3 = mockStatic(AppStatistics.class)){
				mockedStatic3.when(()-> AppStatistics.isSPARK_STARTED()).thenReturn(true);
				mockedStatic3.when(()-> AppStatistics.isMASTER_STARTED()).thenReturn(true);

			try(MockedStatic<ComponentFactory> mockedStatic1 = mockStatic(ComponentFactory.class)){
				mockedStatic1.when(()-> ComponentFactory.getComponentInstance(anyString())).thenReturn(iComponent);
				mockedStatic2.when(()-> ServiceUtils.componentJson(anyString(), anyString(), anyString(), anyString()))
				.thenReturn("componentJson");
				mockedStatic2.when(()-> ServiceUtils.componentClass(anyString()))
				.thenReturn("componentClass");
				
				ResultSet dataFromDatabase = adhocCacheManager.getDataFromDatabase("query##__{}__##");
				assertEquals(resultSet,dataFromDatabase);
			}
			}
			
		}	
		
	}

	@Test
	public void ut_b5_test_getDataFromDatabase() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		
		JsonObject formData = new JsonObject();
		formData.addProperty("requestType", "dataSource");
		formData.addProperty("serviceType", "dataType");
		formData.addProperty("service", "service");
		formData.addProperty("classifier", "classifier");
		formData.addProperty("query", "query");
		JsonObject metadataFileJson = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		metadataFileJson.addProperty("isCached", true);
		connectionDetails.addProperty("connectionType", "global.jdbc");
		connectionDetails.addProperty("connectionId", "connectionId");
		connectionDetails.addProperty("directory", "directory");
		
		metadataFileJson.add("connectionDetails", connectionDetails);
		formData.add("metadataFileJson", metadataFileJson);
		adhocCacheManager.setRequestParameterJson(formData);
		
		Field field = AdhocCacheManager.class.getDeclaredField("classifier");
		field.setAccessible(true);
		field.set(adhocCacheManager, "classifier");
		
		IComponent iComponent = mock(IComponent.class);
		ResultSet resultSet = mock(ResultSet.class);
		when(iComponent.componentLogic(anyString())).thenReturn(resultSet);
		try(MockedStatic<ServiceUtils> mockedStatic2 = mockStatic(ServiceUtils.class)){
			try(MockedStatic<AppStatistics> mockedStatic3 = mockStatic(AppStatistics.class)){
				mockedStatic3.when(()-> AppStatistics.isSPARK_STARTED()).thenReturn(true);
				mockedStatic3.when(()-> AppStatistics.isMASTER_STARTED()).thenReturn(false);

			try(MockedStatic<ComponentFactory> mockedStatic1 = mockStatic(ComponentFactory.class)){
				mockedStatic1.when(()-> ComponentFactory.getComponentInstance(anyString())).thenReturn(iComponent);
				mockedStatic2.when(()-> ServiceUtils.componentJson(anyString(), anyString(), anyString(), anyString()))
				.thenReturn("componentJson");
				mockedStatic2.when(()-> ServiceUtils.componentClass(anyString()))
				.thenReturn("componentClass");
				
				ResultSet dataFromDatabase = adhocCacheManager.getDataFromDatabase("query##__{}__##");
				assertEquals(resultSet,dataFromDatabase);
			}
			}
			
		}	
		
	}
	@Test
	public void ut_b6_test_serveCachedContent() throws IOException {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ResultSet resultSet = mock(ResultSet.class);
		PrintWriter out = mock(PrintWriter.class);
		ResultSetToJson objectToJson = mock(ResultSetToJson.class);
		
		JsonObject formData = new JsonObject();
		formData.addProperty("requestType", "dataSource");
		adhocCacheManager.setRequestParameterJson(formData);
		when(objectToJson.resultSetToJson(any(Boolean.class), any(JsonObject.class))).thenReturn(new JsonObject());
		when(response.getWriter()).thenReturn(out);
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResultSetToJson.class)).thenReturn(objectToJson);
			boolean serveCachedContent = adhocCacheManager.serveCachedContent(request, response, resultSet);
			assertTrue(serveCachedContent);
		}
	}
	
	@Test
	public void ut_b7_test_serveCachedContent() throws IOException {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ResultSet resultSet = mock(ResultSet.class);
		
		ResultSetToJson objectToJson = mock(ResultSetToJson.class);
		
		JsonObject formData = new JsonObject();
		formData.addProperty("requestType", "dataSource");
		adhocCacheManager.setRequestParameterJson(formData);
		when(objectToJson.resultSetToJson(any(Boolean.class), any(JsonObject.class))).thenReturn(new JsonObject());
		when(response.getWriter()).thenThrow(new IOException());
		when(request.getAttribute(anyString())).thenReturn(11l);
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResultSetToJson.class)).thenReturn(objectToJson);
			boolean serveCachedContent = adhocCacheManager.serveCachedContent(request, response, resultSet);
			assertTrue(serveCachedContent);
		}
	}
	@Test
	public void ut_b8_test_getConnectionType() {
		AdhocCacheManager adhocCacheManager = new AdhocCacheManager();
		JsonObject formData = new JsonObject();
		formData.addProperty("classifier", "classifier");
		adhocCacheManager.setRequestParameterJson(formData);
		String connectionType = adhocCacheManager.getConnectionType(11l);
		assertEquals("classifier", connectionType);
	}
}
