//package com.helicalinsight.export.unit;
//
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.mockConstruction;
//import static org.mockito.Mockito.when;
//
//import java.io.FileInputStream;
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import org.junit.Assert;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runners.MethodSorters;
//import org.mockito.MockedConstruction;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.JsonNodeFactory;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
//import com.helicalinsight.admin.dto.EFWDConnGroovyDTO;
//import com.helicalinsight.admin.dto.EFWDConnSqlJDBCDTO;
//import com.helicalinsight.admin.dto.HIEfwdConnSecurityDTO;
//import com.helicalinsight.admin.dto.HIEfwdConnectionDTO;
//import com.helicalinsight.admin.model.EFWDConnGroovy;
//import com.helicalinsight.admin.model.EFWDConnSqlJDBC;
//import com.helicalinsight.admin.model.HIEFWD;
//import com.helicalinsight.admin.model.HIEfwdConnection;
//import com.helicalinsight.admin.model.HIMetadataConnectionEFWD;
//import com.helicalinsight.admin.model.HIMetadataConnections;
//import com.helicalinsight.admin.model.HIResource;
//import com.helicalinsight.admin.model.HIResourceMetadata;
//import com.helicalinsight.admin.service.HIResourceServiceDB;
//import com.helicalinsight.admin.utils.ResourceDTOMapper;
//import com.helicalinsight.datasource.GlobalJdbcType;
//import com.helicalinsight.datasource.service.EFWDConnectionService;
//import com.helicalinsight.export.dto.AdvancedDatasourceWrapper;
//import com.helicalinsight.export.dto.Manifest;
//import com.helicalinsight.export.exception.ResourceImportException;
//import com.helicalinsight.export.handler.ImportManagerContext;
//import com.helicalinsight.export.handler.ResourceDataWriter;
//import com.helicalinsight.export.service.AdvancedDSHandler;
//import com.helicalinsight.export.service.DatasourceHandler;
//import com.helicalinsight.export.service.DatasourceShareHandler;
//import com.helicalinsight.export.service.ResourceIOHandler;
//import com.helicalinsight.export.utils.JsonMapperUtils;
//import com.helicalinsight.export.utils.ManifestUtils;
//import com.helicalinsight.resourcedb.HIResourceDTO;
//
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class AdvancedDSHandlerTest {
//
//	private void injectCommonImportFields(AdvancedDSHandler dsHandler, JsonMapperUtils mapperUtils,
//			EFWDConnectionService connectionService, ImportManagerContext context,
//			DatasourceShareHandler shareHandler, HIResourceServiceDB serviceDb)
//			throws NoSuchFieldException, IllegalAccessException {
//		Field mapperField = ResourceIOHandler.class.getDeclaredField("mapperUtils");
//		mapperField.setAccessible(true);
//		mapperField.set(dsHandler, mapperUtils);
//
//		Field connectionField = AdvancedDSHandler.class.getDeclaredField("connectionService");
//		connectionField.setAccessible(true);
//		connectionField.set(dsHandler, connectionService);
//
//		Field contextField = ResourceIOHandler.class.getDeclaredField("context");
//		contextField.setAccessible(true);
//		contextField.set(dsHandler, context);
//
//		Field shareField = DatasourceHandler.class.getDeclaredField("shareHandler");
//		shareField.setAccessible(true);
//		shareField.set(dsHandler, shareHandler);
//
//		Field serviceDbField = ResourceIOHandler.class.getDeclaredField("serviceDb");
//		serviceDbField.setAccessible(true);
//		serviceDbField.set(dsHandler, serviceDb);
//	}
//
//	private ObjectNode buildImportObjectNode(JsonNodeFactory factory) {
//		ObjectNode objectNode = factory.objectNode();
//		ArrayNode jdbcArray = factory.arrayNode();
//		jdbcArray.add(factory.textNode("jdbc-entry"));
//		objectNode.set("jdbc", jdbcArray);
//		objectNode.set("groovy", factory.arrayNode());
//		objectNode.set("securities", factory.objectNode());
//		return objectNode;
//	}
//
//	private void setupJdbcImportMocks(EFWDConnSqlJDBC conn, HIEfwdConnection efwdConnection, HIEFWD efwd,
//			HIResource parentResource, ImportManagerContext context) {
//		when(conn.getHiEfwdConnection()).thenReturn(efwdConnection);
//		when(efwdConnection.getId()).thenReturn(1);
//		when(efwdConnection.getType()).thenReturn(GlobalJdbcType.PLAIN_JDBC);
//		when(efwdConnection.getHiResourceEFWD()).thenReturn(efwd);
//		when(efwd.getParentResource()).thenReturn(parentResource);
//		when(parentResource.getResourceURL()).thenReturn("AllDatasources");
//		when(context.getProcessed(anyString(), anyInt())).thenReturn(false);
//		when(context.addDestination(anyString())).thenReturn("imported/AllDatasources");
//		when(context.getResourcesDirectory()).thenReturn("/tmp/resources");
//	}
//
//	@Test(expected = ResourceImportException.class)
//	public void ut_a1_testImportResource() throws NoSuchFieldException, IllegalAccessException {
//		AdvancedDSHandler dsHandler = new AdvancedDSHandler();
//		HIResource resource = mock(HIResource.class);
//		ImportManagerContext context = mock(ImportManagerContext.class);
//		Field contextField = ResourceIOHandler.class.getDeclaredField("context");
//		contextField.setAccessible(true);
//		contextField.set(dsHandler, context);
//		dsHandler.importResource(resource, "dsFileName", "onConflict");
//	}
//
//	@Test
//	public void ut_a2_testImportResource() throws NoSuchFieldException, IllegalAccessException {
//		AdvancedDSHandler dsHandler = new AdvancedDSHandler();
//		HIResource resource = mock(HIResource.class);
//		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
//		EFWDConnectionService connectionService = mock(EFWDConnectionService.class);
//		ImportManagerContext context = mock(ImportManagerContext.class);
//		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
//		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
//		EFWDConnSqlJDBC conn = mock(EFWDConnSqlJDBC.class);
//		HIEfwdConnection efwdConnection = mock(HIEfwdConnection.class);
//		HIEFWD efwd = mock(HIEFWD.class);
//		HIResource parentResource = mock(HIResource.class);
//		HIResource dbResource = mock(HIResource.class);
//
//		injectCommonImportFields(dsHandler, mapperUtils, connectionService, context, shareHandler, serviceDb);
//
//		JsonNodeFactory factory = JsonNodeFactory.instance;
//		ObjectNode objectNode = buildImportObjectNode(factory);
//		JsonNode jdbcNode = objectNode.withArray("jdbc").get(0);
//
//		when(mapperUtils.mapToDTO(any(FileInputStream.class), any(Class.class))).thenReturn(objectNode);
//		when(mapperUtils.mapToDTO(jdbcNode.toString(), EFWDConnSqlJDBC.class)).thenReturn(conn);
//		setupJdbcImportMocks(conn, efwdConnection, efwd, parentResource, context);
//		when(connectionService.findConnectionByLookup(any())).thenReturn(null);
//		when(serviceDb.getResourceByUrl(anyString())).thenReturn(dbResource);
//		when(efwdConnection.getId()).thenReturn(10);
//
//		try (MockedConstruction<FileInputStream> ignored = mockConstruction(FileInputStream.class)) {
//			dsHandler.importResource(resource, "dsFileName", "update");
//		}
//	}
//
//	@Test
//	public void ut_a3_testImportResourceHCR() throws NoSuchFieldException, IllegalAccessException {
//		AdvancedDSHandler dsHandler = new AdvancedDSHandler();
//		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
//		EFWDConnectionService connectionService = mock(EFWDConnectionService.class);
//		ImportManagerContext context = mock(ImportManagerContext.class);
//		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
//		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
//		EFWDConnSqlJDBC conn = mock(EFWDConnSqlJDBC.class);
//		HIEfwdConnection efwdConnection = mock(HIEfwdConnection.class);
//		HIEFWD efwd = mock(HIEFWD.class);
//		HIResource parentResource = mock(HIResource.class);
//		HIResource dbResource = mock(HIResource.class);
//
//		injectCommonImportFields(dsHandler, mapperUtils, connectionService, context, shareHandler, serviceDb);
//
//		JsonNodeFactory factory = JsonNodeFactory.instance;
//		ObjectNode objectNode = buildImportObjectNode(factory);
//		JsonNode jdbcNode = objectNode.withArray("jdbc").get(0);
//
//		when(mapperUtils.mapToDTO(any(FileInputStream.class), any(Class.class))).thenReturn(objectNode);
//		when(mapperUtils.mapToDTO(jdbcNode.toString(), EFWDConnSqlJDBC.class)).thenReturn(conn);
//		setupJdbcImportMocks(conn, efwdConnection, efwd, parentResource, context);
//		when(connectionService.findConnectionByLookup(any())).thenReturn(null);
//		when(serviceDb.getResourceByUrl(anyString())).thenReturn(dbResource);
//
//		try (MockedConstruction<FileInputStream> ignored = mockConstruction(FileInputStream.class)) {
//			List<String> mappings = dsHandler.importResourceHCR("dsFileName", "update");
//			Assert.assertEquals(1, mappings.size());
//			Assert.assertEquals("1:1", mappings.get(0));
//		}
//	}
//
//	@Test
//	public void ut_a4_testImportResourceGroovy() throws NoSuchFieldException, IllegalAccessException {
//		AdvancedDSHandler dsHandler = new AdvancedDSHandler();
//		HIResource resource = mock(HIResource.class);
//		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
//		EFWDConnectionService connectionService = mock(EFWDConnectionService.class);
//		ImportManagerContext context = mock(ImportManagerContext.class);
//		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
//		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
//		EFWDConnGroovy groovy = mock(EFWDConnGroovy.class);
//		HIEfwdConnection efwdConnection = mock(HIEfwdConnection.class);
//		HIEFWD efwd = mock(HIEFWD.class);
//		HIResource parentResource = mock(HIResource.class);
//		HIResource dbResource = mock(HIResource.class);
//
//		injectCommonImportFields(dsHandler, mapperUtils, connectionService, context, shareHandler, serviceDb);
//
//		JsonNodeFactory factory = JsonNodeFactory.instance;
//		ObjectNode objectNode = factory.objectNode();
//		objectNode.set("jdbc", factory.arrayNode());
//		ArrayNode groovyArray = factory.arrayNode();
//		groovyArray.add(factory.textNode("groovy-entry"));
//		objectNode.set("groovy", groovyArray);
//		objectNode.set("securities", factory.objectNode());
//		JsonNode groovyNode = groovyArray.get(0);
//
//		when(mapperUtils.mapToDTO(any(FileInputStream.class), any(Class.class))).thenReturn(objectNode);
//		when(mapperUtils.mapToDTO(groovyNode.toString(), EFWDConnGroovy.class)).thenReturn(groovy);
//		when(groovy.getHiEfwdConnection()).thenReturn(efwdConnection);
//		when(efwdConnection.getId()).thenReturn(2);
//		when(efwdConnection.getType()).thenReturn(GlobalJdbcType.GROOVY_DATASOURCE);
//		when(efwdConnection.getHiResourceEFWD()).thenReturn(efwd);
//		when(efwd.getParentResource()).thenReturn(parentResource);
//		when(parentResource.getResourceURL()).thenReturn("GroovyFolder");
//		when(context.getProcessed(anyString(), anyInt())).thenReturn(false);
//		when(context.addDestination(anyString())).thenReturn("imported/GroovyFolder");
//		when(context.getResourcesDirectory()).thenReturn("/tmp/resources");
//		when(connectionService.findConnectionByLookup(any())).thenReturn(null);
//		when(serviceDb.getResourceByUrl(anyString())).thenReturn(dbResource);
//
//		try (MockedConstruction<FileInputStream> ignored = mockConstruction(FileInputStream.class)) {
//			dsHandler.importResource(resource, "dsFileName", "update");
//		}
//	}
//
//	@Test
//	public void ut_b1_testWriteWithEfwdIds() throws NoSuchFieldException, IllegalAccessException {
//		AdvancedDSHandler dsHandler = new AdvancedDSHandler();
//		HIResourceDTO resource = mock(HIResourceDTO.class);
//		Manifest manifest = mock(Manifest.class);
//		EFWDConnectionService connectionService = mock(EFWDConnectionService.class);
//		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
//		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
//		ManifestUtils manifestUtils = mock(ManifestUtils.class);
//		EFWDConnSqlJDBCDTO sqlJdbc = mock(EFWDConnSqlJDBCDTO.class);
//		HIEfwdConnectionDTO connectionDTO = mock(HIEfwdConnectionDTO.class);
//		List<HIEfwdConnSecurityDTO> securityList = Arrays.asList(new HIEfwdConnSecurityDTO());
//
//		Field connectionField = AdvancedDSHandler.class.getDeclaredField("connectionService");
//		connectionField.setAccessible(true);
//		connectionField.set(dsHandler, connectionService);
//
//		Field shareField = DatasourceHandler.class.getDeclaredField("shareHandler");
//		shareField.setAccessible(true);
//		shareField.set(dsHandler, shareHandler);
//
//		Field dataWriterField = ResourceIOHandler.class.getDeclaredField("dataWriter");
//		dataWriterField.setAccessible(true);
//		dataWriterField.set(dsHandler, dataWriter);
//
//		Field manifestField = ResourceIOHandler.class.getDeclaredField("manifestUtils");
//		manifestField.setAccessible(true);
//		manifestField.set(dsHandler, manifestUtils);
//
//		when(resource.getEfwdIds()).thenReturn(Arrays.asList(1));
//		when(connectionService.findSqlConnectionByID(1)).thenReturn(sqlJdbc);
//		when(connectionService.findGroovyConnectionById(1)).thenReturn(null);
//		when(sqlJdbc.getHiEfwdConnection()).thenReturn(connectionDTO);
//		when(connectionDTO.getId()).thenReturn(5);
//		when(shareHandler.getAdvancedConnectionShare(5)).thenReturn(securityList);
//
//		dsHandler.write(resource, "dir", manifest);
//	}
//
//	@Test
//	public void ut_b2_testWriteNoDatasourcesFound() throws NoSuchFieldException, IllegalAccessException {
//		AdvancedDSHandler dsHandler = new AdvancedDSHandler();
//		HIResourceDTO resource = mock(HIResourceDTO.class);
//		Manifest manifest = mock(Manifest.class);
//		EFWDConnectionService connectionService = mock(EFWDConnectionService.class);
//
//		Field connectionField = AdvancedDSHandler.class.getDeclaredField("connectionService");
//		connectionField.setAccessible(true);
//		connectionField.set(dsHandler, connectionService);
//
//		when(resource.getEfwdIds()).thenReturn(Collections.emptyList());
//		when(resource.getType()).thenReturn("report");
//		when(resource.getResourceId()).thenReturn(1);
//		when(connectionService.findConnectionByParentId(1)).thenReturn(Collections.emptyList());
//		when(connectionService.findGroovyByParentId(1)).thenReturn(Collections.emptyList());
//
//		dsHandler.write(resource, "dir", manifest);
//	}
//
//	@Test
//	public void ut_b3_testWriteFileTypeResource() throws NoSuchFieldException, IllegalAccessException {
//		AdvancedDSHandler dsHandler = new AdvancedDSHandler();
//		HIResourceDTO resource = mock(HIResourceDTO.class);
//		Manifest manifest = mock(Manifest.class);
//		HIMetadataResourceServiceDB mdServiceDb = mock(HIMetadataResourceServiceDB.class);
//		ResourceDTOMapper mapper = mock(ResourceDTOMapper.class);
//		EFWDConnectionService connectionService = mock(EFWDConnectionService.class);
//		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
//		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
//		ManifestUtils manifestUtils = mock(ManifestUtils.class);
//		HIResourceMetadata metadata = mock(HIResourceMetadata.class);
//		HIMetadataConnections mdConnection = mock(HIMetadataConnections.class);
//		HIMetadataConnectionEFWD efwdMetadataConnection = mock(HIMetadataConnectionEFWD.class);
//		HIEfwdConnection hiEfwdConnection = mock(HIEfwdConnection.class);
//		HIEfwdConnectionDTO efwdDto = mock(HIEfwdConnectionDTO.class);
//		EFWDConnSqlJDBCDTO sqlJdbc = mock(EFWDConnSqlJDBCDTO.class);
//
//		Field connectionField = AdvancedDSHandler.class.getDeclaredField("connectionService");
//		connectionField.setAccessible(true);
//		connectionField.set(dsHandler, connectionService);
//
//		Field mdField = AdvancedDSHandler.class.getDeclaredField("mdServiceDb");
//		mdField.setAccessible(true);
//		mdField.set(dsHandler, mdServiceDb);
//
//		Field mapperField = AdvancedDSHandler.class.getDeclaredField("mapper");
//		mapperField.setAccessible(true);
//		mapperField.set(dsHandler, mapper);
//
//		Field shareField = DatasourceHandler.class.getDeclaredField("shareHandler");
//		shareField.setAccessible(true);
//		shareField.set(dsHandler, shareHandler);
//
//		Field dataWriterField = ResourceIOHandler.class.getDeclaredField("dataWriter");
//		dataWriterField.setAccessible(true);
//		dataWriterField.set(dsHandler, dataWriter);
//
//		Field manifestField = ResourceIOHandler.class.getDeclaredField("manifestUtils");
//		manifestField.setAccessible(true);
//		manifestField.set(dsHandler, manifestUtils);
//
//		when(resource.getEfwdIds()).thenReturn(null);
//		when(resource.getType()).thenReturn("file");
//		when(resource.getResourceId()).thenReturn(1);
//		when(mdServiceDb.giveHIResourceMetadataByResourceId(1)).thenReturn(metadata);
//		when(metadata.getHiMetadataConnections()).thenReturn(Arrays.asList(mdConnection));
//		when(mdConnection.getMetadataConnectionEfwd()).thenReturn(Arrays.asList(efwdMetadataConnection));
//		when(efwdMetadataConnection.getHiEfwdConnection()).thenReturn(hiEfwdConnection);
//		when(mapper.toDTO(hiEfwdConnection)).thenReturn(efwdDto);
//		when(efwdDto.getType()).thenReturn(GlobalJdbcType.PLAIN_JDBC);
//		when(efwdDto.getEfwdConnSqlJDBC()).thenReturn(Arrays.asList(sqlJdbc));
//		when(sqlJdbc.getHiEfwdConnection()).thenReturn(efwdDto);
//		when(efwdDto.getId()).thenReturn(7);
//		when(shareHandler.getAdvancedConnectionShare(7)).thenReturn(new ArrayList<>());
//
//		dsHandler.write(resource, "dir", manifest);
//	}
//
//	@Test
//	public void ut_b4_testWriteByParentId() throws NoSuchFieldException, IllegalAccessException {
//		AdvancedDSHandler dsHandler = new AdvancedDSHandler();
//		HIResourceDTO resource = mock(HIResourceDTO.class);
//		Manifest manifest = mock(Manifest.class);
//		EFWDConnectionService connectionService = mock(EFWDConnectionService.class);
//		DatasourceShareHandler shareHandler = mock(DatasourceShareHandler.class);
//		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
//		ManifestUtils manifestUtils = mock(ManifestUtils.class);
//		EFWDConnSqlJDBCDTO sqlJdbc = mock(EFWDConnSqlJDBCDTO.class);
//		HIEfwdConnectionDTO connectionDTO = mock(HIEfwdConnectionDTO.class);
//
//		Field connectionField = AdvancedDSHandler.class.getDeclaredField("connectionService");
//		connectionField.setAccessible(true);
//		connectionField.set(dsHandler, connectionService);
//
//		Field shareField = DatasourceHandler.class.getDeclaredField("shareHandler");
//		shareField.setAccessible(true);
//		shareField.set(dsHandler, shareHandler);
//
//		Field dataWriterField = ResourceIOHandler.class.getDeclaredField("dataWriter");
//		dataWriterField.setAccessible(true);
//		dataWriterField.set(dsHandler, dataWriter);
//
//		Field manifestField = ResourceIOHandler.class.getDeclaredField("manifestUtils");
//		manifestField.setAccessible(true);
//		manifestField.set(dsHandler, manifestUtils);
//
//		when(resource.getEfwdIds()).thenReturn(null);
//		when(resource.getType()).thenReturn("folder");
//		when(resource.getResourceId()).thenReturn(3);
//		when(connectionService.findConnectionByParentId(3)).thenReturn(Arrays.asList(sqlJdbc));
//		when(connectionService.findGroovyByParentId(3)).thenReturn(Collections.emptyList());
//		when(sqlJdbc.getHiEfwdConnection()).thenReturn(connectionDTO);
//		when(connectionDTO.getId()).thenReturn(9);
//		when(shareHandler.getAdvancedConnectionShare(9)).thenReturn(new ArrayList<>());
//
//		try (MockedConstruction<AdvancedDatasourceWrapper> ignored = mockConstruction(AdvancedDatasourceWrapper.class)) {
//			dsHandler.write(resource, "dir", manifest);
//		}
//	}
//}
