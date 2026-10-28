package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.dto.GlobalDatasourceLookupDTO;
import com.helicalinsight.admin.model.HIMetadataConnectionGlobal;
import com.helicalinsight.admin.model.HIMetadataConnections;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.datasource.model.DSTypeHikari;
import com.helicalinsight.datasource.model.DSTypeJndi;
import com.helicalinsight.datasource.model.DSTypeNoSQL;
import com.helicalinsight.datasource.model.DSTypeTomcat;
import com.helicalinsight.datasource.model.GlobalConnectionSecurity;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.export.dto.Conflict;
import com.helicalinsight.export.dto.DataSourceWrapper;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.exception.ResourceImportException;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.ResourceDataWriter;
import com.helicalinsight.export.service.DatasourceHandler;
import com.helicalinsight.export.service.DatasourceShareHandler;
import com.helicalinsight.export.service.GlobalDSHandler;
import com.helicalinsight.export.service.RecycleBinHandler;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.export.utils.JsonMapperUtils;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.export.utils.ResourceShareUtils;
import com.helicalinsight.resourcedb.HIResourceDTO;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GlobalDSHandlerTest {

	private static void injectRecycleBinHandler(GlobalDSHandler dsHandler)
			throws NoSuchFieldException, IllegalAccessException {
		Field recycleBinHandlerField = ResourceIOHandler.class.getDeclaredField("recycleBinHandler");
		recycleBinHandlerField.setAccessible(true);
		recycleBinHandlerField.set(dsHandler, mock(RecycleBinHandler.class));
	}

	@Test(expected = ResourceImportException.class)
	public void ut_a1_testImportResource() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GlobalDSHandler dsHandler = new GlobalDSHandler();
		HIResource resource = mock(HIResource.class);
		ImportManagerContext context = mock(ImportManagerContext.class);
		Field field2 = ResourceIOHandler.class.getDeclaredField("context");
		field2.setAccessible(true);
		field2.set(dsHandler, context);
		dsHandler.importResource(resource,  "dsFileName", "onConflict");
		
	}
	@Test
	public void ut_a2_testImportResource() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GlobalDSHandler dsHandler = new GlobalDSHandler();
		HIResource resource = mock(HIResource.class);
		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
		DataSourceWrapper wrapper = mock(DataSourceWrapper.class);
		DSTypeHikari dsTypeHikari = mock(DSTypeHikari.class);
		GlobalConnections gConnection = mock(GlobalConnections.class);
		GlobalConnectionService connectionService = mock(GlobalConnectionService.class);
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest importRequest = mock(ImportRequest.class);
		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		
		Field field = ResourceIOHandler.class.getDeclaredField("mapperUtils");
		field.setAccessible(true);
		field.set(dsHandler, mapperUtils);
		
		Field field1 = GlobalDSHandler.class.getDeclaredField("connectionService");
		field1.setAccessible(true);
		field1.set(dsHandler, connectionService);
		
		Field field2 = ResourceIOHandler.class.getDeclaredField("context");
		field2.setAccessible(true);
		field2.set(dsHandler, context);
		
		Field field3 = DatasourceHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(dsHandler, shareHandler);
		
		Field shareUtilsField = ResourceIOHandler.class.getDeclaredField("shareUtils");
		shareUtilsField.setAccessible(true);
		shareUtilsField.set(dsHandler, shareUtils);
		
		Field dtoMapperField = ResourceIOHandler.class.getDeclaredField("dtoMapper");
		dtoMapperField.setAccessible(true);
		dtoMapperField.set(dsHandler, dtoMapper);
		injectRecycleBinHandler(dsHandler);
		
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		ArrayNode dsList = jsonNodeFactory.arrayNode();
		JsonNode textNode = jsonNodeFactory.textNode("Hello, World!");
		dsList.add(textNode);
		
		when(context.getRequest()).thenReturn(importRequest);
		when(importRequest.getOnConflict()).thenReturn("update");
		
		when(gConnection.getId()).thenReturn(1);
		when(gConnection.getGlobalId()).thenReturn(1);
		when(gConnection.isDeleted()).thenReturn(false);
		when(connectionService.getGlobalConnectionBy(any(GlobalDatasourceLookupDTO.class))).thenReturn(gConnection);
		when(connectionService.getExtraOptions(anyInt())).thenReturn(Collections.emptyList());
		when(dsTypeHikari.getGlobalConnections()).thenReturn(gConnection);
		when(mapperUtils.mapToArray(any(FileInputStream.class))).thenReturn(dsList);
		when(mapperUtils.mapToDTO(textNode.toString(),DataSourceWrapper.class)).thenReturn(wrapper);
		when(wrapper.getHikari()).thenReturn(dsTypeHikari);
		try(MockedConstruction<FileInputStream> construction = mockConstruction(FileInputStream.class)){
			try(MockedConstruction<GlobalDatasourceLookupDTO> lookup = mockConstruction(GlobalDatasourceLookupDTO.class,(mock,context1)->{
				
			})){
			dsHandler.importResource(resource,  "dsFileName", "onConflict");
			}
		}
	}
	
	@Test
	public void ut_a3_testImportResource() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GlobalDSHandler dsHandler = new GlobalDSHandler();
		HIResource resource = mock(HIResource.class);
		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
		DataSourceWrapper wrapper = mock(DataSourceWrapper.class);
		DSTypeHikari dsTypeHikari = mock(DSTypeHikari.class);
		GlobalConnections gConnection = mock(GlobalConnections.class);
		GlobalConnectionService connectionService = mock(GlobalConnectionService.class);
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest importRequest = mock(ImportRequest.class);
		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		
		Field field = ResourceIOHandler.class.getDeclaredField("mapperUtils");
		field.setAccessible(true);
		field.set(dsHandler, mapperUtils);
		
		Field field1 = GlobalDSHandler.class.getDeclaredField("connectionService");
		field1.setAccessible(true);
		field1.set(dsHandler, connectionService);
		
		Field field2 = ResourceIOHandler.class.getDeclaredField("context");
		field2.setAccessible(true);
		field2.set(dsHandler, context);
		
		Field field3 = DatasourceHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(dsHandler, shareHandler);
		
		Field shareUtilsField = ResourceIOHandler.class.getDeclaredField("shareUtils");
		shareUtilsField.setAccessible(true);
		shareUtilsField.set(dsHandler, shareUtils);
		
		Field dtoMapperField = ResourceIOHandler.class.getDeclaredField("dtoMapper");
		dtoMapperField.setAccessible(true);
		dtoMapperField.set(dsHandler, dtoMapper);
		injectRecycleBinHandler(dsHandler);
		
		
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		ArrayNode dsList = jsonNodeFactory.arrayNode();
		JsonNode textNode = jsonNodeFactory.textNode("Hello, World!");
		dsList.add(textNode);
		
		when(context.getRequest()).thenReturn(importRequest);
		when(importRequest.getOnConflict()).thenReturn("update");
		when(connectionService.getHikariConnectionById(anyInt())).thenReturn(dsTypeHikari);
		when(gConnection.getId()).thenReturn(1);
		when(gConnection.getGlobalId()).thenReturn(1);
		when(gConnection.isDeleted()).thenReturn(false);
		when(connectionService.getGlobalConnectionBy(any(GlobalDatasourceLookupDTO.class))).thenReturn(gConnection);
		when(connectionService.getExtraOptions(anyInt())).thenReturn(Collections.emptyList());
		when(dsTypeHikari.getGlobalConnections()).thenReturn(gConnection);
		when(dsTypeHikari.getId()).thenReturn(1);
		when(mapperUtils.mapToArray(any(FileInputStream.class))).thenReturn(dsList);
		when(mapperUtils.mapToDTO(textNode.toString(),DataSourceWrapper.class)).thenReturn(wrapper);
		when(wrapper.getHikari()).thenReturn(dsTypeHikari);
		Map<Integer, List<GlobalConnectionSecurity>> map = new HashMap<>();
		List<GlobalConnectionSecurity> list = Arrays.asList(new GlobalConnectionSecurity());
		map.put(1, list);
		when(wrapper.getSecurities()).thenReturn(map);
		try(MockedConstruction<FileInputStream> construction = mockConstruction(FileInputStream.class)){
			try(MockedConstruction<GlobalDatasourceLookupDTO> lookup = mockConstruction(GlobalDatasourceLookupDTO.class,(mock,context1)->{
				
			})){
			dsHandler.importResource(resource,  "dsFileName", "onConflict");
			}
		}
	}
	
	@Test
	public void ut_a4_testImportResource() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GlobalDSHandler dsHandler = new GlobalDSHandler();
		HIResource resource = mock(HIResource.class);
		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
		DataSourceWrapper wrapper = mock(DataSourceWrapper.class);
		GlobalConnections gConnection = mock(GlobalConnections.class);
		GlobalConnectionService connectionService = mock(GlobalConnectionService.class);
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest importRequest = mock(ImportRequest.class);
		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
		DSTypeJndi dsTypeJndi = mock(DSTypeJndi.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		
		Field field = ResourceIOHandler.class.getDeclaredField("mapperUtils");
		field.setAccessible(true);
		field.set(dsHandler, mapperUtils);
		
		Field field1 = GlobalDSHandler.class.getDeclaredField("connectionService");
		field1.setAccessible(true);
		field1.set(dsHandler, connectionService);
		
		Field field2 = ResourceIOHandler.class.getDeclaredField("context");
		field2.setAccessible(true);
		field2.set(dsHandler, context);
		
		Field field3 = DatasourceHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(dsHandler, shareHandler);
		
		Field shareUtilsField = ResourceIOHandler.class.getDeclaredField("shareUtils");
		shareUtilsField.setAccessible(true);
		shareUtilsField.set(dsHandler, shareUtils);
		
		Field dtoMapperField = ResourceIOHandler.class.getDeclaredField("dtoMapper");
		dtoMapperField.setAccessible(true);
		dtoMapperField.set(dsHandler, dtoMapper);
		injectRecycleBinHandler(dsHandler);
		
		
		
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		ArrayNode dsList = jsonNodeFactory.arrayNode();
		JsonNode textNode = jsonNodeFactory.textNode("Hello, World!");
		dsList.add(textNode);
		
		when(context.getRequest()).thenReturn(importRequest);
		when(importRequest.getOnConflict()).thenReturn("update");
		when(connectionService.getJndiConnectionById(anyInt())).thenReturn(dsTypeJndi);
		when(gConnection.getId()).thenReturn(1);
		when(gConnection.getGlobalId()).thenReturn(1);
		when(gConnection.isDeleted()).thenReturn(false);
		when(connectionService.getGlobalConnectionBy(any(GlobalDatasourceLookupDTO.class))).thenReturn(gConnection);
		when(connectionService.getExtraOptions(anyInt())).thenReturn(Collections.emptyList());
		when(dsTypeJndi.getGlobalConnections()).thenReturn(gConnection);
		when(mapperUtils.mapToArray(any(FileInputStream.class))).thenReturn(dsList);
		when(mapperUtils.mapToDTO(textNode.toString(),DataSourceWrapper.class)).thenReturn(wrapper);
		when(wrapper.getJndi()).thenReturn(dsTypeJndi);
		try(MockedConstruction<FileInputStream> construction = mockConstruction(FileInputStream.class)){
			try(MockedConstruction<GlobalDatasourceLookupDTO> lookup = mockConstruction(GlobalDatasourceLookupDTO.class,(mock,context1)->{
				
			})){
			dsHandler.importResource(resource,  "dsFileName", "onConflict");
			}
		}
	}

	@Test
	public void ut_a5_testImportResource() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GlobalDSHandler dsHandler = new GlobalDSHandler();
		HIResource resource = mock(HIResource.class);
		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
		DataSourceWrapper wrapper = mock(DataSourceWrapper.class);
		GlobalConnections gConnection = mock(GlobalConnections.class);
		GlobalConnectionService connectionService = mock(GlobalConnectionService.class);
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest importRequest = mock(ImportRequest.class);
		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
		DSTypeJndi dsTypeJndi = mock(DSTypeJndi.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);

		
		Field field = ResourceIOHandler.class.getDeclaredField("mapperUtils");
		field.setAccessible(true);
		field.set(dsHandler, mapperUtils);
		
		Field field1 = GlobalDSHandler.class.getDeclaredField("connectionService");
		field1.setAccessible(true);
		field1.set(dsHandler, connectionService);
		
		Field field2 = ResourceIOHandler.class.getDeclaredField("context");
		field2.setAccessible(true);
		field2.set(dsHandler, context);
		
		Field field3 = DatasourceHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(dsHandler, shareHandler);
		
		Field shareUtilsField = ResourceIOHandler.class.getDeclaredField("shareUtils");
		shareUtilsField.setAccessible(true);
		shareUtilsField.set(dsHandler, shareUtils);
		
		Field dtoMapperField = ResourceIOHandler.class.getDeclaredField("dtoMapper");
		dtoMapperField.setAccessible(true);
		dtoMapperField.set(dsHandler, dtoMapper);
		injectRecycleBinHandler(dsHandler);
		
		
		
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		ArrayNode dsList = jsonNodeFactory.arrayNode();
		JsonNode textNode = jsonNodeFactory.textNode("Hello, World!");
		dsList.add(textNode);
		
		when(context.getRequest()).thenReturn(importRequest);
		when(importRequest.getOnConflict()).thenReturn("update");
		when(connectionService.getJndiConnectionById(anyInt())).thenReturn(null);
		when(gConnection.getId()).thenReturn(1);
		when(gConnection.getGlobalId()).thenReturn(1);
		when(gConnection.isDeleted()).thenReturn(false);
		when(connectionService.getGlobalConnectionBy(any(GlobalDatasourceLookupDTO.class))).thenReturn(gConnection);
		when(connectionService.getExtraOptions(anyInt())).thenReturn(Collections.emptyList());
		when(dsTypeJndi.getGlobalConnections()).thenReturn(gConnection);
		when(mapperUtils.mapToArray(any(FileInputStream.class))).thenReturn(dsList);
		when(mapperUtils.mapToDTO(textNode.toString(),DataSourceWrapper.class)).thenReturn(wrapper);
		when(wrapper.getJndi()).thenReturn(dsTypeJndi);
		try(MockedConstruction<FileInputStream> construction = mockConstruction(FileInputStream.class)){
			try(MockedConstruction<GlobalDatasourceLookupDTO> lookup = mockConstruction(GlobalDatasourceLookupDTO.class,(mock,context1)->{
				
			})){
			dsHandler.importResource(resource,  "dsFileName", "onConflict");
			}
		}
	}

	@Test
	public void ut_a6_testImportResource() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GlobalDSHandler dsHandler = new GlobalDSHandler();
		HIResource resource = mock(HIResource.class);
		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
		DataSourceWrapper wrapper = mock(DataSourceWrapper.class);
		
		GlobalConnections gConnection = mock(GlobalConnections.class);
		GlobalConnectionService connectionService = mock(GlobalConnectionService.class);
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest importRequest = mock(ImportRequest.class);
		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
		DSTypeNoSQL dsTypeNoSQL = mock(DSTypeNoSQL.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);

		
		Field field = ResourceIOHandler.class.getDeclaredField("mapperUtils");
		field.setAccessible(true);
		field.set(dsHandler, mapperUtils);
		
		Field field1 = GlobalDSHandler.class.getDeclaredField("connectionService");
		field1.setAccessible(true);
		field1.set(dsHandler, connectionService);
		
		Field field2 = ResourceIOHandler.class.getDeclaredField("context");
		field2.setAccessible(true);
		field2.set(dsHandler, context);
		
		Field field3 = DatasourceHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(dsHandler, shareHandler);
		
		Field shareUtilsField = ResourceIOHandler.class.getDeclaredField("shareUtils");
		shareUtilsField.setAccessible(true);
		shareUtilsField.set(dsHandler, shareUtils);
		
		Field dtoMapperField = ResourceIOHandler.class.getDeclaredField("dtoMapper");
		dtoMapperField.setAccessible(true);
		dtoMapperField.set(dsHandler, dtoMapper);
		injectRecycleBinHandler(dsHandler);
		
		
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		ArrayNode dsList = jsonNodeFactory.arrayNode();
		JsonNode textNode = jsonNodeFactory.textNode("Hello, World!");
		dsList.add(textNode);
		
		when(context.getRequest()).thenReturn(importRequest);
		when(importRequest.getOnConflict()).thenReturn("update");
		when(connectionService.getNoSQLConnectionById(anyInt())).thenReturn(null);
		when(gConnection.getId()).thenReturn(1);
		when(gConnection.getGlobalId()).thenReturn(1);
		when(gConnection.isDeleted()).thenReturn(false);
		when(connectionService.getGlobalConnectionBy(any(GlobalDatasourceLookupDTO.class))).thenReturn(gConnection);
		when(connectionService.getExtraOptions(anyInt())).thenReturn(Collections.emptyList());
		when(dsTypeNoSQL.getGlobalConnections()).thenReturn(gConnection);
		when(mapperUtils.mapToArray(any(FileInputStream.class))).thenReturn(dsList);
		when(mapperUtils.mapToDTO(textNode.toString(),DataSourceWrapper.class)).thenReturn(wrapper);
		when(wrapper.getNoSql()).thenReturn(dsTypeNoSQL);
		try(MockedConstruction<FileInputStream> construction = mockConstruction(FileInputStream.class)){
			try(MockedConstruction<GlobalDatasourceLookupDTO> lookup = mockConstruction(GlobalDatasourceLookupDTO.class,(mock,context1)->{
				
			})){
			dsHandler.importResource(resource,  "dsFileName", "onConflict");
			}
		}
	}
	
	@Test
	public void ut_a7_testImportResource() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GlobalDSHandler dsHandler = new GlobalDSHandler();
		HIResource resource = mock(HIResource.class);
		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
		DataSourceWrapper wrapper = mock(DataSourceWrapper.class);
		
		GlobalConnections gConnection = mock(GlobalConnections.class);
		GlobalConnectionService connectionService = mock(GlobalConnectionService.class);
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest importRequest = mock(ImportRequest.class);
		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
		DSTypeNoSQL dsTypeNoSQL = mock(DSTypeNoSQL.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		
		Field field = ResourceIOHandler.class.getDeclaredField("mapperUtils");
		field.setAccessible(true);
		field.set(dsHandler, mapperUtils);
		
		Field field1 = GlobalDSHandler.class.getDeclaredField("connectionService");
		field1.setAccessible(true);
		field1.set(dsHandler, connectionService);
		
		Field field2 = ResourceIOHandler.class.getDeclaredField("context");
		field2.setAccessible(true);
		field2.set(dsHandler, context);
		
		Field field3 = DatasourceHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(dsHandler, shareHandler);
		
		Field shareUtilsField = ResourceIOHandler.class.getDeclaredField("shareUtils");
		shareUtilsField.setAccessible(true);
		shareUtilsField.set(dsHandler, shareUtils);
		
		Field dtoMapperField = ResourceIOHandler.class.getDeclaredField("dtoMapper");
		dtoMapperField.setAccessible(true);
		dtoMapperField.set(dsHandler, dtoMapper);
		injectRecycleBinHandler(dsHandler);
		
		
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		ArrayNode dsList = jsonNodeFactory.arrayNode();
		JsonNode textNode = jsonNodeFactory.textNode("Hello, World!");
		dsList.add(textNode);
		
		when(context.getRequest()).thenReturn(importRequest);
		when(importRequest.getOnConflict()).thenReturn("update");
		when(connectionService.getNoSQLConnectionById(anyInt())).thenReturn(dsTypeNoSQL);
		when(gConnection.getId()).thenReturn(1);
		when(gConnection.getGlobalId()).thenReturn(1);
		when(gConnection.isDeleted()).thenReturn(false);
		when(connectionService.getGlobalConnectionBy(any(GlobalDatasourceLookupDTO.class))).thenReturn(gConnection);
		when(connectionService.getExtraOptions(anyInt())).thenReturn(Collections.emptyList());
		when(dsTypeNoSQL.getGlobalConnections()).thenReturn(gConnection);
		when(mapperUtils.mapToArray(any(FileInputStream.class))).thenReturn(dsList);
		when(mapperUtils.mapToDTO(textNode.toString(),DataSourceWrapper.class)).thenReturn(wrapper);
		when(wrapper.getNoSql()).thenReturn(dsTypeNoSQL);
		try(MockedConstruction<FileInputStream> construction = mockConstruction(FileInputStream.class)){
			try(MockedConstruction<GlobalDatasourceLookupDTO> lookup = mockConstruction(GlobalDatasourceLookupDTO.class,(mock,context1)->{
				
			})){
			dsHandler.importResource(resource, "dsFileName", "onConflict");
			}
		}
	}

	@Test
	public void ut_a8_testImportResource() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GlobalDSHandler dsHandler = new GlobalDSHandler();
		HIResource resource = mock(HIResource.class);
		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
		DataSourceWrapper wrapper = mock(DataSourceWrapper.class);
		
		GlobalConnections gConnection = mock(GlobalConnections.class);
		GlobalConnectionService connectionService = mock(GlobalConnectionService.class);
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest importRequest = mock(ImportRequest.class);
		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
		DSTypeTomcat dsTypeTomcat = mock(DSTypeTomcat.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		
		Field field = ResourceIOHandler.class.getDeclaredField("mapperUtils");
		field.setAccessible(true);
		field.set(dsHandler, mapperUtils);
		
		Field field1 = GlobalDSHandler.class.getDeclaredField("connectionService");
		field1.setAccessible(true);
		field1.set(dsHandler, connectionService);
		
		Field field2 = ResourceIOHandler.class.getDeclaredField("context");
		field2.setAccessible(true);
		field2.set(dsHandler, context);
		
		Field field3 = DatasourceHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(dsHandler, shareHandler);
		
		Field shareUtilsField = ResourceIOHandler.class.getDeclaredField("shareUtils");
		shareUtilsField.setAccessible(true);
		shareUtilsField.set(dsHandler, shareUtils);
		
		Field dtoMapperField = ResourceIOHandler.class.getDeclaredField("dtoMapper");
		dtoMapperField.setAccessible(true);
		dtoMapperField.set(dsHandler, dtoMapper);
		injectRecycleBinHandler(dsHandler);
		
		
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		ArrayNode dsList = jsonNodeFactory.arrayNode();
		JsonNode textNode = jsonNodeFactory.textNode("Hello, World!");
		dsList.add(textNode);
		
		when(context.getRequest()).thenReturn(importRequest);
		when(importRequest.getOnConflict()).thenReturn("update");
		when(connectionService.getTomcatConnectionById(anyInt())).thenReturn(dsTypeTomcat);
		when(gConnection.getId()).thenReturn(1);
		when(gConnection.getGlobalId()).thenReturn(1);
		when(gConnection.isDeleted()).thenReturn(false);
		when(connectionService.getGlobalConnectionBy(any(GlobalDatasourceLookupDTO.class))).thenReturn(gConnection);
		when(connectionService.getExtraOptions(anyInt())).thenReturn(Collections.emptyList());
		when(dsTypeTomcat.getGlobalConnections()).thenReturn(gConnection);
		when(mapperUtils.mapToArray(any(FileInputStream.class))).thenReturn(dsList);
		when(mapperUtils.mapToDTO(textNode.toString(),DataSourceWrapper.class)).thenReturn(wrapper);
		when(wrapper.getTomcat()).thenReturn(dsTypeTomcat);
		try(MockedConstruction<FileInputStream> construction = mockConstruction(FileInputStream.class)){
			try(MockedConstruction<GlobalDatasourceLookupDTO> lookup = mockConstruction(GlobalDatasourceLookupDTO.class,(mock,context1)->{
				
			})){
			dsHandler.importResource(resource,  "dsFileName", "onConflict");
			}
		}
	}
	@Test
	public void ut_a9_testImportResource() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GlobalDSHandler dsHandler = new GlobalDSHandler();
		HIResource resource = mock(HIResource.class);
		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
		DataSourceWrapper wrapper = mock(DataSourceWrapper.class);
		
		GlobalConnections gConnection = mock(GlobalConnections.class);
		GlobalConnectionService connectionService = mock(GlobalConnectionService.class);
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest importRequest = mock(ImportRequest.class);
		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
		DSTypeTomcat dsTypeTomcat = mock(DSTypeTomcat.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		
		Field field = ResourceIOHandler.class.getDeclaredField("mapperUtils");
		field.setAccessible(true);
		field.set(dsHandler, mapperUtils);
		
		Field field1 = GlobalDSHandler.class.getDeclaredField("connectionService");
		field1.setAccessible(true);
		field1.set(dsHandler, connectionService);
		
		Field field2 = ResourceIOHandler.class.getDeclaredField("context");
		field2.setAccessible(true);
		field2.set(dsHandler, context);
		
		Field field3 = DatasourceHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(dsHandler, shareHandler);
		
		Field shareUtilsField = ResourceIOHandler.class.getDeclaredField("shareUtils");
		shareUtilsField.setAccessible(true);
		shareUtilsField.set(dsHandler, shareUtils);
		
		Field dtoMapperField = ResourceIOHandler.class.getDeclaredField("dtoMapper");
		dtoMapperField.setAccessible(true);
		dtoMapperField.set(dsHandler, dtoMapper);
		injectRecycleBinHandler(dsHandler);
		
		
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		ArrayNode dsList = jsonNodeFactory.arrayNode();
		JsonNode textNode = jsonNodeFactory.textNode("Hello, World!");
		dsList.add(textNode);
		
		when(context.getRequest()).thenReturn(importRequest);
		when(importRequest.getOnConflict()).thenReturn("update");
		when(connectionService.getTomcatConnectionById(anyInt())).thenReturn(null);
		when(gConnection.getId()).thenReturn(1);
		when(gConnection.getGlobalId()).thenReturn(1);
		when(gConnection.isDeleted()).thenReturn(false);
		when(connectionService.getGlobalConnectionBy(any(GlobalDatasourceLookupDTO.class))).thenReturn(gConnection);
		when(connectionService.getExtraOptions(anyInt())).thenReturn(Collections.emptyList());
		when(dsTypeTomcat.getGlobalConnections()).thenReturn(gConnection);
		when(mapperUtils.mapToArray(any(FileInputStream.class))).thenReturn(dsList);
		when(mapperUtils.mapToDTO(textNode.toString(),DataSourceWrapper.class)).thenReturn(wrapper);
		when(wrapper.getTomcat()).thenReturn(dsTypeTomcat);
		try(MockedConstruction<FileInputStream> construction = mockConstruction(FileInputStream.class)){
			try(MockedConstruction<GlobalDatasourceLookupDTO> lookup = mockConstruction(GlobalDatasourceLookupDTO.class,(mock,context1)->{
				
			})){
			dsHandler.importResource(resource, "dsFileName", "onConflict");
			}
		}
	}
	@Test
	public void ut_b1_testWrite() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GlobalDSHandler dsHandler = new GlobalDSHandler();
		HIResourceDTO resource = mock(HIResourceDTO.class);
		Manifest manifest = mock(Manifest.class);
		HIMetadataResourceServiceDB mdServiceDB = mock(HIMetadataResourceServiceDB.class);
		HIResourceMetadata metadata = mock(HIResourceMetadata.class);
		HIMetadataConnections hiMetadataConnections = mock(HIMetadataConnections.class);
		List<HIMetadataConnections> connections = Arrays.asList(hiMetadataConnections);
		HIMetadataConnectionGlobal hiMetadataConnectionGlobal = mock(HIMetadataConnectionGlobal.class);
		List<HIMetadataConnectionGlobal> mdGlobalConnections = Arrays.asList(hiMetadataConnectionGlobal);
		GlobalConnections globalConnection = mock(GlobalConnections.class);		
		GlobalConnectionService connectionService = mock(GlobalConnectionService.class);
		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
		DSTypeJndi jndi = mock(DSTypeJndi.class);
		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		
		Field field1 = GlobalDSHandler.class.getDeclaredField("connectionService");
		field1.setAccessible(true);
		field1.set(dsHandler, connectionService);
		
		Field field2 = GlobalDSHandler.class.getDeclaredField("mdServiceDB");
		field2.setAccessible(true);
		field2.set(dsHandler, mdServiceDB);
		
		Field field3 = DatasourceHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(dsHandler, shareHandler);
		
		Field field4 = ResourceIOHandler.class.getDeclaredField("dataWriter");
		field4.setAccessible(true);
		field4.set(dsHandler, dataWriter);
		
		Field field5 = ResourceIOHandler.class.getDeclaredField("manifestUtils");
		field5.setAccessible(true);
		field5.set(dsHandler, manifestUtils);
		
		
		when(resource.getResourceId()).thenReturn(1);
		when(mdServiceDB.giveHIResourceMetadataByResourceId(anyInt())).thenReturn(metadata);
		when(metadata.getHiMetadataConnections()).thenReturn(connections);
		when(hiMetadataConnections.getMetadataGlobalConnList()).thenReturn(mdGlobalConnections);
		when(hiMetadataConnectionGlobal.getGlobalConnections()).thenReturn(globalConnection);
		when( connectionService.getJndiConnectionById(anyInt())).thenReturn(jndi);
		
		try(MockedConstruction<DataSourceWrapper> construction = mockConstruction(DataSourceWrapper.class,(mock,context1)->{
			
		})){
			dsHandler.write(resource, "dir", manifest);
		}
		
	}

	@Test
	public void ut_b2_testWrite() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GlobalDSHandler dsHandler = new GlobalDSHandler();
		HIResourceDTO resource = mock(HIResourceDTO.class);
		Manifest manifest = mock(Manifest.class);
		HIMetadataResourceServiceDB mdServiceDB = mock(HIMetadataResourceServiceDB.class);
		HIResourceMetadata metadata = mock(HIResourceMetadata.class);
		HIMetadataConnections hiMetadataConnections = mock(HIMetadataConnections.class);
		List<HIMetadataConnections> connections = Arrays.asList(hiMetadataConnections);
		HIMetadataConnectionGlobal hiMetadataConnectionGlobal = mock(HIMetadataConnectionGlobal.class);
		List<HIMetadataConnectionGlobal> mdGlobalConnections = Arrays.asList(hiMetadataConnectionGlobal);
		GlobalConnections globalConnection = mock(GlobalConnections.class);		
		GlobalConnectionService connectionService = mock(GlobalConnectionService.class);
		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
		DSTypeHikari hikari = mock(DSTypeHikari.class);
		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		
		Field field1 = GlobalDSHandler.class.getDeclaredField("connectionService");
		field1.setAccessible(true);
		field1.set(dsHandler, connectionService);
		
		Field field2 = GlobalDSHandler.class.getDeclaredField("mdServiceDB");
		field2.setAccessible(true);
		field2.set(dsHandler, mdServiceDB);
		
		Field field3 = DatasourceHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(dsHandler, shareHandler);
		
		Field field4 = ResourceIOHandler.class.getDeclaredField("dataWriter");
		field4.setAccessible(true);
		field4.set(dsHandler, dataWriter);
		
		Field field5 = ResourceIOHandler.class.getDeclaredField("manifestUtils");
		field5.setAccessible(true);
		field5.set(dsHandler, manifestUtils);
		
		
		when(resource.getResourceId()).thenReturn(1);
		when(mdServiceDB.giveHIResourceMetadataByResourceId(anyInt())).thenReturn(metadata);
		when(metadata.getHiMetadataConnections()).thenReturn(connections);
		when(hiMetadataConnections.getMetadataGlobalConnList()).thenReturn(mdGlobalConnections);
		when(hiMetadataConnectionGlobal.getGlobalConnections()).thenReturn(globalConnection);
		when( connectionService.getHikariConnectionById(anyInt())).thenReturn(hikari);
		
		try(MockedConstruction<DataSourceWrapper> construction = mockConstruction(DataSourceWrapper.class,(mock,context1)->{
			
		})){
			dsHandler.write(resource, "dir", manifest);
		}
		
	}

	@Test
	public void ut_b3_testWrite() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GlobalDSHandler dsHandler = new GlobalDSHandler();
		HIResourceDTO resource = mock(HIResourceDTO.class);
		Manifest manifest = mock(Manifest.class);
		HIMetadataResourceServiceDB mdServiceDB = mock(HIMetadataResourceServiceDB.class);
		HIResourceMetadata metadata = mock(HIResourceMetadata.class);
		HIMetadataConnections hiMetadataConnections = mock(HIMetadataConnections.class);
		List<HIMetadataConnections> connections = Arrays.asList(hiMetadataConnections);
		HIMetadataConnectionGlobal hiMetadataConnectionGlobal = mock(HIMetadataConnectionGlobal.class);
		List<HIMetadataConnectionGlobal> mdGlobalConnections = Arrays.asList(hiMetadataConnectionGlobal);
		GlobalConnections globalConnection = mock(GlobalConnections.class);		
		GlobalConnectionService connectionService = mock(GlobalConnectionService.class);
		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
		DSTypeTomcat dsTypeTomcat = mock(DSTypeTomcat.class);
		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		
		Field field1 = GlobalDSHandler.class.getDeclaredField("connectionService");
		field1.setAccessible(true);
		field1.set(dsHandler, connectionService);
		
		Field field2 = GlobalDSHandler.class.getDeclaredField("mdServiceDB");
		field2.setAccessible(true);
		field2.set(dsHandler, mdServiceDB);
		
		Field field3 = DatasourceHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(dsHandler, shareHandler);
		
		Field field4 = ResourceIOHandler.class.getDeclaredField("dataWriter");
		field4.setAccessible(true);
		field4.set(dsHandler, dataWriter);
		
		Field field5 = ResourceIOHandler.class.getDeclaredField("manifestUtils");
		field5.setAccessible(true);
		field5.set(dsHandler, manifestUtils);
		
		
		when(resource.getResourceId()).thenReturn(1);
		when(mdServiceDB.giveHIResourceMetadataByResourceId(anyInt())).thenReturn(metadata);
		when(metadata.getHiMetadataConnections()).thenReturn(connections);
		when(hiMetadataConnections.getMetadataGlobalConnList()).thenReturn(mdGlobalConnections);
		when(hiMetadataConnectionGlobal.getGlobalConnections()).thenReturn(globalConnection);
		when( connectionService.getTomcatConnectionById(anyInt())).thenReturn(dsTypeTomcat);
		
		try(MockedConstruction<DataSourceWrapper> construction = mockConstruction(DataSourceWrapper.class,(mock,context1)->{
			
		})){
			dsHandler.write(resource, "dir", manifest);
		}
		
	}

	@Test
	public void ut_b4_testWrite() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		GlobalDSHandler dsHandler = new GlobalDSHandler();
		HIResourceDTO resource = mock(HIResourceDTO.class);
		Manifest manifest = mock(Manifest.class);
		HIMetadataResourceServiceDB mdServiceDB = mock(HIMetadataResourceServiceDB.class);
		HIResourceMetadata metadata = mock(HIResourceMetadata.class);
		HIMetadataConnections hiMetadataConnections = mock(HIMetadataConnections.class);
		List<HIMetadataConnections> connections = Arrays.asList(hiMetadataConnections);
		HIMetadataConnectionGlobal hiMetadataConnectionGlobal = mock(HIMetadataConnectionGlobal.class);
		List<HIMetadataConnectionGlobal> mdGlobalConnections = Arrays.asList(hiMetadataConnectionGlobal);
		GlobalConnections globalConnection = mock(GlobalConnections.class);		
		GlobalConnectionService connectionService = mock(GlobalConnectionService.class);
		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
		DSTypeNoSQL dsTypeNoSQL = mock(DSTypeNoSQL.class);
		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		
		Field field1 = GlobalDSHandler.class.getDeclaredField("connectionService");
		field1.setAccessible(true);
		field1.set(dsHandler, connectionService);
		
		Field field2 = GlobalDSHandler.class.getDeclaredField("mdServiceDB");
		field2.setAccessible(true);
		field2.set(dsHandler, mdServiceDB);
		
		Field field3 = DatasourceHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(dsHandler, shareHandler);
		
		Field field4 = ResourceIOHandler.class.getDeclaredField("dataWriter");
		field4.setAccessible(true);
		field4.set(dsHandler, dataWriter);
		
		Field field5 = ResourceIOHandler.class.getDeclaredField("manifestUtils");
		field5.setAccessible(true);
		field5.set(dsHandler, manifestUtils);
		
		
		when(resource.getResourceId()).thenReturn(1);
		when(mdServiceDB.giveHIResourceMetadataByResourceId(anyInt())).thenReturn(metadata);
		when(metadata.getHiMetadataConnections()).thenReturn(connections);
		when(hiMetadataConnections.getMetadataGlobalConnList()).thenReturn(mdGlobalConnections);
		when(hiMetadataConnectionGlobal.getGlobalConnections()).thenReturn(globalConnection);
		when( connectionService.getNoSQLConnectionById(anyInt())).thenReturn(dsTypeNoSQL);
		
		try(MockedConstruction<DataSourceWrapper> construction = mockConstruction(DataSourceWrapper.class,(mock,context1)->{
			
		})){
			dsHandler.write(resource, "dir", manifest);
		}
		
	}

	@Test
	public void ut_c1_skipsRecycleBinImportWhenConnectionExistsAndConflictIsSkip() throws Exception {
		HikariImportFixture fixture = new HikariImportFixture(Conflict.SKIP, true);

		fixture.importResource();

		verifyNoInteractions(fixture.recycleBinHandler);
		verify(fixture.connectionService, never()).addGlobalConnections(any(GlobalConnections.class));
		verify(fixture.context, never()).recover(any(GlobalConnections.class));
	}

	@Test
	public void ut_c2_importsRecycleBinWhenNewConnectionIsCreatedOnSkip() throws Exception {
		HikariImportFixture fixture = new HikariImportFixture(Conflict.SKIP, false);
		User createdBy = mock(User.class);
		when(fixture.connection.getCreatedBy()).thenReturn(createdBy);
		when(fixture.shareUtils.resolveUser(createdBy, fixture.context)).thenReturn(createdBy);

		fixture.importResource();

		verify(fixture.connection).setGlobalId(null);
		verify(fixture.connection).setDeleted(false);
		verify(fixture.connection).setCreatedBy(createdBy);
		verify(fixture.connectionService).addGlobalConnections(fixture.connection);
		verify(fixture.recycleBinHandler).importGlobalConnection(eq(1), eq(fixture.connection), eq(fixture.context));
	}

	@Test
	public void ut_c3_recoversDeletedConnectionWithoutRecycleBinImportOnSkip() throws Exception {
		HikariImportFixture fixture = new HikariImportFixture(Conflict.SKIP, true);
		when(fixture.connection.isDeleted()).thenReturn(true);

		fixture.importResource();

		verify(fixture.context).recover(fixture.connection);
		verify(fixture.connectionService).editGlobalConnections(fixture.connection);
		verifyNoInteractions(fixture.recycleBinHandler);
	}

	@Test
	public void ut_c4_recoversDeletedConnectionAndImportsRecycleBinOnUpdate() throws Exception {
		HikariImportFixture fixture = new HikariImportFixture(Conflict.UPDATE, true);
		when(fixture.connection.isDeleted()).thenReturn(true);

		fixture.importResource();

		verify(fixture.context).recover(fixture.connection);
		verify(fixture.connectionService).editGlobalConnections(fixture.connection);
		verify(fixture.recycleBinHandler).importGlobalConnection(eq(1), eq(fixture.connection), eq(fixture.context));
	}

	@Test
	public void ut_c5_importsRecycleBinWhenExistingConnectionConflictIsUpdate() throws Exception {
		HikariImportFixture fixture = new HikariImportFixture(Conflict.UPDATE, true);

		fixture.importResource();

		verify(fixture.recycleBinHandler).importGlobalConnection(eq(1), eq(fixture.connection), eq(fixture.context));
		verify(fixture.connectionService, never()).addGlobalConnections(any(GlobalConnections.class));
		verify(fixture.context, never()).recover(any(GlobalConnections.class));
	}

	private static final class HikariImportFixture {
		private final GlobalDSHandler handler = new GlobalDSHandler();
		private final GlobalConnections connection = mock(GlobalConnections.class);
		private final GlobalConnectionService connectionService = mock(GlobalConnectionService.class);
		private final ImportManagerContext context = mock(ImportManagerContext.class);
		private final RecycleBinHandler recycleBinHandler = mock(RecycleBinHandler.class);
		private final ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		private final String onConflict;

		private HikariImportFixture(String onConflict, boolean connectionExists) throws Exception {
			this.onConflict = onConflict;
			JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
			DataSourceWrapper wrapper = mock(DataSourceWrapper.class);
			DSTypeHikari hikari = mock(DSTypeHikari.class);
			ImportRequest importRequest = mock(ImportRequest.class);
			DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);

			setField(ResourceIOHandler.class, "mapperUtils", mapperUtils);
			setField(GlobalDSHandler.class, "connectionService", connectionService);
			setField(ResourceIOHandler.class, "context", context);
			setField(DatasourceHandler.class, "shareHandler", shareHandler);
			setField(ResourceIOHandler.class, "shareUtils", shareUtils);
			setField(ResourceIOHandler.class, "dtoMapper", mock(ResourceDTOMapper.class));
			setField(ResourceIOHandler.class, "recycleBinHandler", recycleBinHandler);

			JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
			ArrayNode dsList = jsonNodeFactory.arrayNode();
			JsonNode textNode = jsonNodeFactory.textNode("Hello, World!");
			dsList.add(textNode);

			when(context.getRequest()).thenReturn(importRequest);
			when(importRequest.getOnConflict()).thenReturn(onConflict);
			when(connection.getId()).thenReturn(1);
			when(connection.getGlobalId()).thenReturn(1);
			when(connection.isDeleted()).thenReturn(false);
			when(connectionService.getGlobalConnectionBy(any(GlobalDatasourceLookupDTO.class)))
					.thenReturn(connectionExists ? connection : null);
			when(connectionService.getExtraOptions(anyInt())).thenReturn(Collections.emptyList());
			when(hikari.getGlobalConnections()).thenReturn(connection);
			when(mapperUtils.mapToArray(any(FileInputStream.class))).thenReturn(dsList);
			when(mapperUtils.mapToDTO(textNode.toString(), DataSourceWrapper.class)).thenReturn(wrapper);
			when(wrapper.getHikari()).thenReturn(hikari);
		}

		private void importResource() {
			try (MockedConstruction<FileInputStream> files = mockConstruction(FileInputStream.class);
					MockedConstruction<GlobalDatasourceLookupDTO> lookup = mockConstruction(
							GlobalDatasourceLookupDTO.class)) {
				handler.importResource(mock(HIResource.class), "dsFileName", onConflict);
			}
		}

		private void setField(Class<?> type, String name, Object value) throws Exception {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			field.set(handler, value);
		}
	}

}
