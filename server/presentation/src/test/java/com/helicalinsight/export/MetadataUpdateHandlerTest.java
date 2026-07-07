package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.dto.GlobalConnectionDTO;
import com.helicalinsight.admin.dto.HIEfwdConnectionDTO;
import com.helicalinsight.admin.dto.HIMetadataConnectionDTO;
import com.helicalinsight.admin.dto.HIMetadataConnectionEFWDDTO;
import com.helicalinsight.admin.dto.HIMetadataConnectionGlobalDTO;
import com.helicalinsight.admin.dto.MetadataDatabasesDTO;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIMetadataColumns;
import com.helicalinsight.admin.model.HIMetadataConnectionEFWD;
import com.helicalinsight.admin.model.HIMetadataConnectionGlobal;
import com.helicalinsight.admin.model.HIMetadataConnections;
import com.helicalinsight.admin.model.HIMetadataTables;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.model.HIResourceMetadataDTO;
import com.helicalinsight.admin.model.MetadataDatabases;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.importres.AbstractResourceImportHandler;
import com.helicalinsight.export.handler.importres.MetadataUpdateHandler;

public class MetadataUpdateHandlerTest {

	@Test
	public void ut_a1_testUpdate() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		MetadataUpdateHandler metadataUpdateHandler = new MetadataUpdateHandler();
		
		HIResourceMetadataDTO fileMetadata = mock(HIResourceMetadataDTO.class);
		HIMetadataConnectionDTO hiMetadataConnectionDTO = mock(HIMetadataConnectionDTO.class);
		MetadataDatabasesDTO dbDto = mock(MetadataDatabasesDTO.class);
		HIMetadataConnectionGlobalDTO hiMetadataConnectionGlobalDTO = mock(HIMetadataConnectionGlobalDTO.class);
		HIMetadataConnectionEFWDDTO hiMetadataConnectionEFWDTO = mock(HIMetadataConnectionEFWDDTO.class);
		GlobalConnectionDTO globalConnectionDTO = mock(GlobalConnectionDTO.class);

		
		HIResourceMetadata dbMetadata   = mock(HIResourceMetadata.class);
		HIMetadataResourceServiceDB mdServiceDb = mock(HIMetadataResourceServiceDB.class);
		HIMetadataConnections hiMetadataConnections = mock(HIMetadataConnections.class);
		MetadataDatabases db = mock(MetadataDatabases.class);
		HIMetadataConnectionGlobal hiMetadataConnectionGlobal = mock(HIMetadataConnectionGlobal.class);
		HIMetadataConnectionEFWD hiMetadataConnectionEFWD = mock(HIMetadataConnectionEFWD.class);
		GlobalConnections global = mock(GlobalConnections.class);
		ImportManagerContext context = mock(ImportManagerContext.class);
		
		Field field  = AbstractResourceImportHandler.class.getDeclaredField("mdServiceDb");
		field.setAccessible(true);
		field.set(metadataUpdateHandler, mdServiceDb);
		
		Field field2 = AbstractResourceImportHandler.class.getDeclaredField("context");
		field2.setAccessible(true);
		field2.set(metadataUpdateHandler, context);
		
		List<HIMetadataConnectionDTO> connectionDTOs = Arrays.asList(hiMetadataConnectionDTO);
		List<MetadataDatabasesDTO> metadataDatabaseDTO = Arrays.asList(dbDto);
		List<HIMetadataConnectionGlobalDTO> hiMetadataConnectionGlobalsDTO = Arrays.asList(hiMetadataConnectionGlobalDTO);
		List<HIMetadataConnectionEFWDDTO> hiMetadataConnectionEFWDsDTO = Arrays.asList(hiMetadataConnectionEFWDTO);
		
		List<HIMetadataConnections> connections = Arrays.asList(hiMetadataConnections);
		List<MetadataDatabases> metadataDatabases = Arrays.asList(db);
		List<HIMetadataConnectionGlobal> hiMetadataConnectionGlobals = Arrays.asList(hiMetadataConnectionGlobal);
		List<HIMetadataConnectionEFWD> hiMetadataConnectionEFWDs = Arrays.asList(hiMetadataConnectionEFWD);
		
		when(mdServiceDb.getHIMetadataConnections(any())).thenReturn(connections);
		when(mdServiceDb.getHIMetadataDatabases(anyInt(),anyString())).thenReturn(db);
		when(db.getName()).thenReturn("name");
		when(db.getCatalog()).thenReturn("catlog");
		when(db.getSchema()).thenReturn("schema");
		
		when(dbDto.getName()).thenReturn("name");
		when(dbDto.getCatalog()).thenReturn("catlog");
		when(dbDto.getSchema()).thenReturn("schema");
		
		
		when(fileMetadata.getHiMetadataConnections()).thenReturn(connectionDTOs);
		when(hiMetadataConnectionDTO.getMetadataDatabases()).thenReturn(metadataDatabaseDTO);
		when(hiMetadataConnectionDTO.getMetadataGlobalConnList()).thenReturn(hiMetadataConnectionGlobalsDTO);
		when(hiMetadataConnectionDTO.getMetadataConnectionEfwd()).thenReturn(hiMetadataConnectionEFWDsDTO);
		
		when(hiMetadataConnections.getMetadataDatabases()).thenReturn(metadataDatabases);
		when(hiMetadataConnections.getConnectionType()).thenReturn("nonPooled");
		when(hiMetadataConnectionDTO.getConnectionType()).thenReturn("nonPooled");
		when(hiMetadataConnections.getMetadataGlobalConnList()).thenReturn(hiMetadataConnectionGlobals);
		when(hiMetadataConnections.getMetadataConnectionEfwd()).thenReturn(hiMetadataConnectionEFWDs);
		when(hiMetadataConnectionGlobal.getGlobalConnections()).thenReturn(global);
		when(global.getId()).thenReturn(1);
		when(context.getGlobalConnection(anyInt())).thenReturn(global);
		when(hiMetadataConnectionGlobalDTO.getGlobalConnections()).thenReturn(globalConnectionDTO);
		when(globalConnectionDTO.getGlobalId()).thenReturn(1);

		metadataUpdateHandler.update(fileMetadata, dbMetadata);
	}
	
	@Test
	public void ut_a2_testUpdate() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		MetadataUpdateHandler metadataUpdateHandler = new MetadataUpdateHandler();
		
		HIResourceMetadataDTO fileMetadata = mock(HIResourceMetadataDTO.class);
		HIMetadataConnectionDTO hiMetadataConnectionDTO = mock(HIMetadataConnectionDTO.class);
		MetadataDatabasesDTO dbDto = mock(MetadataDatabasesDTO.class);
		HIMetadataConnectionGlobalDTO hiMetadataConnectionGlobalDTO = mock(HIMetadataConnectionGlobalDTO.class);
		HIMetadataConnectionEFWDDTO hiMetadataConnectionEFWDTO = mock(HIMetadataConnectionEFWDDTO.class);
		HIEfwdConnectionDTO hiEfwdConnectionDTO = mock(HIEfwdConnectionDTO.class);
		
		when(hiMetadataConnectionEFWDTO.getHiEfwdConnection()).thenReturn(hiEfwdConnectionDTO);
		when(hiEfwdConnectionDTO.getId()).thenReturn(1);
		
		HIResourceMetadata dbMetadata   = mock(HIResourceMetadata.class);
		HIMetadataResourceServiceDB mdServiceDb = mock(HIMetadataResourceServiceDB.class);
		HIMetadataConnections hiMetadataConnections = mock(HIMetadataConnections.class);
		MetadataDatabases db = mock(MetadataDatabases.class);
		HIMetadataConnectionGlobal hiMetadataConnectionGlobal = mock(HIMetadataConnectionGlobal.class);
		HIMetadataConnectionEFWD hiMetadataConnectionEFWD = mock(HIMetadataConnectionEFWD.class);
		HIEfwdConnection hiEfwdConnection = mock(HIEfwdConnection.class);
		ImportManagerContext context = mock(ImportManagerContext.class);
		
		Field field  = AbstractResourceImportHandler.class.getDeclaredField("mdServiceDb");
		field.setAccessible(true);
		field.set(metadataUpdateHandler, mdServiceDb);
		
		Field field2 = AbstractResourceImportHandler.class.getDeclaredField("context");
		field2.setAccessible(true);
		field2.set(metadataUpdateHandler, context);
		
		List<HIMetadataConnectionDTO> connectionDTOs = Arrays.asList(hiMetadataConnectionDTO);
		List<MetadataDatabasesDTO> metadataDatabaseDTO = Arrays.asList(dbDto);
		List<HIMetadataConnectionGlobalDTO> hiMetadataConnectionGlobalsDTO = Arrays.asList(hiMetadataConnectionGlobalDTO);
		List<HIMetadataConnectionEFWDDTO> hiMetadataConnectionEFWDsDTO = Arrays.asList(hiMetadataConnectionEFWDTO);
	
		
		List<HIMetadataConnections> connections = Arrays.asList(hiMetadataConnections);
		List<MetadataDatabases> metadataDatabases = Arrays.asList(db);
		List<HIMetadataConnectionGlobal> hiMetadataConnectionGlobals = Arrays.asList(hiMetadataConnectionGlobal);
		List<HIMetadataConnectionEFWD> hiMetadataConnectionEFWDs = Arrays.asList(hiMetadataConnectionEFWD);
		
		when(mdServiceDb.getHIMetadataConnections(any())).thenReturn(connections);
		when(mdServiceDb.getHIMetadataDatabases(anyInt(),anyString())).thenReturn(db);
		
		when(dbDto.getName()).thenReturn("name");
		when(dbDto.getCatalog()).thenReturn("catlog");
		when(dbDto.getSchema()).thenReturn("schema");
		
		
		when(db.getName()).thenReturn("name");
		when(db.getCatalog()).thenReturn("catlog");
		when(db.getSchema()).thenReturn("schema");
		
		when(fileMetadata.getHiMetadataConnections()).thenReturn(connectionDTOs);
		when(hiMetadataConnectionDTO.getMetadataDatabases()).thenReturn(metadataDatabaseDTO);
		when(hiMetadataConnectionDTO.getMetadataGlobalConnList()).thenReturn(hiMetadataConnectionGlobalsDTO);
		when(hiMetadataConnectionDTO.getMetadataConnectionEfwd()).thenReturn(hiMetadataConnectionEFWDsDTO);
	
		
		when(hiMetadataConnections.getMetadataDatabases()).thenReturn(metadataDatabases);
		when(hiMetadataConnections.getConnectionType()).thenReturn("non").thenReturn("global.jdbc");
		when(hiMetadataConnectionDTO.getConnectionType()).thenReturn("non").thenReturn("global.jdbc");
		when(hiMetadataConnections.getMetadataGlobalConnList()).thenReturn(hiMetadataConnectionGlobals);
		when(hiMetadataConnections.getMetadataConnectionEfwd()).thenReturn(hiMetadataConnectionEFWDs);
		when(hiMetadataConnectionEFWD.getHiEfwdConnection()).thenReturn(hiEfwdConnection);
		when(hiEfwdConnection.getId()).thenReturn(1);
		when(context.getEfwdConnection(anyInt())).thenReturn(hiEfwdConnection);
		metadataUpdateHandler.update(fileMetadata, dbMetadata);
	}
	
	@Test
	public void ut_a3_testUpdate() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		MetadataUpdateHandler metadataUpdateHandler = new MetadataUpdateHandler();
		
		HIResourceMetadataDTO fileMetadata = mock(HIResourceMetadataDTO.class);
		HIMetadataConnectionDTO hiMetadataConnectionDTO = mock(HIMetadataConnectionDTO.class);
		MetadataDatabasesDTO dbDto = mock(MetadataDatabasesDTO.class);
		HIMetadataConnectionGlobalDTO hiMetadataConnectionGlobalDTO = mock(HIMetadataConnectionGlobalDTO.class);
		HIMetadataConnectionEFWDDTO hiMetadataConnectionEFWDTO = mock(HIMetadataConnectionEFWDDTO.class);
		HIEfwdConnectionDTO hiEfwdConnectionDTO = mock(HIEfwdConnectionDTO.class);
		
		HIResourceMetadata dbMetadata   = mock(HIResourceMetadata.class);
		HIMetadataResourceServiceDB mdServiceDb = mock(HIMetadataResourceServiceDB.class);
		HIMetadataConnections hiMetadataConnections = mock(HIMetadataConnections.class);
		MetadataDatabases db = mock(MetadataDatabases.class);
		HIMetadataConnectionGlobal hiMetadataConnectionGlobal = mock(HIMetadataConnectionGlobal.class);
		HIMetadataConnectionEFWD hiMetadataConnectionEFWD = mock(HIMetadataConnectionEFWD.class);
		HIEfwdConnection hiEfwdConnection = mock(HIEfwdConnection.class);
		ImportManagerContext context = mock(ImportManagerContext.class);
		
		Field field  = AbstractResourceImportHandler.class.getDeclaredField("mdServiceDb");
		field.setAccessible(true);
		field.set(metadataUpdateHandler, mdServiceDb);
		
		Field field2 = AbstractResourceImportHandler.class.getDeclaredField("context");
		field2.setAccessible(true);
		field2.set(metadataUpdateHandler, context);
		
		List<HIMetadataConnectionDTO> connectionDTOs = Arrays.asList(hiMetadataConnectionDTO);
		List<MetadataDatabasesDTO> metadataDatabaseDTO = Arrays.asList(dbDto);
		List<HIMetadataConnectionGlobalDTO> hiMetadataConnectionGlobalsDTO = Arrays.asList(hiMetadataConnectionGlobalDTO);
		List<HIMetadataConnectionEFWDDTO> hiMetadataConnectionEFWDsDTO = Arrays.asList(hiMetadataConnectionEFWDTO);
	
		
		
		List<HIMetadataConnections> connections = Arrays.asList(hiMetadataConnections);
		List<MetadataDatabases> metadataDatabases = Arrays.asList(db);
		List<HIMetadataConnectionGlobal> hiMetadataConnectionGlobals = Arrays.asList(hiMetadataConnectionGlobal);
		List<HIMetadataConnectionEFWD> hiMetadataConnectionEFWDs = Arrays.asList(hiMetadataConnectionEFWD);
		
		when(mdServiceDb.getHIMetadataConnections(any())).thenReturn(connections);
		when(mdServiceDb.getHIMetadataDatabases(anyInt(),anyString())).thenReturn(db).thenReturn(db);
		
		when(dbDto.getName()).thenReturn("name2");
		when(dbDto.getCatalog()).thenReturn("catlog2");
		when(dbDto.getSchema()).thenReturn("schema2");
		
		when(db.getName()).thenReturn("name").thenReturn("name2");
		when(db.getCatalog()).thenReturn("catlog").thenReturn("catlog2");
		when(db.getSchema()).thenReturn("schema").thenReturn("schema2");
		
		
		when(fileMetadata.getHiMetadataConnections()).thenReturn(connectionDTOs);
		when(hiMetadataConnectionDTO.getMetadataDatabases()).thenReturn(metadataDatabaseDTO);
		when(hiMetadataConnectionDTO.getMetadataGlobalConnList()).thenReturn(hiMetadataConnectionGlobalsDTO);
		when(hiMetadataConnectionDTO.getMetadataConnectionEfwd()).thenReturn(hiMetadataConnectionEFWDsDTO);
	
		
		when(hiMetadataConnections.getMetadataDatabases()).thenReturn(metadataDatabases);
		when(hiMetadataConnections.getConnectionType()).thenReturn("non").thenReturn("global.jdbc");
		when(hiMetadataConnectionDTO.getConnectionType()).thenReturn("non").thenReturn("global.jdbc");
		when(hiMetadataConnections.getMetadataGlobalConnList()).thenReturn(hiMetadataConnectionGlobals);
		when(hiMetadataConnections.getMetadataConnectionEfwd()).thenReturn(hiMetadataConnectionEFWDs);
		when(hiMetadataConnectionEFWD.getHiEfwdConnection()).thenReturn(hiEfwdConnection);
		when(hiEfwdConnection.getId()).thenReturn(1);
		when(context.getEfwdConnection(anyInt())).thenReturn(hiEfwdConnection);
		
		when(hiMetadataConnectionEFWDTO.getHiEfwdConnection()).thenReturn(hiEfwdConnectionDTO);
		when(hiEfwdConnectionDTO.getId()).thenReturn(1);
		
		metadataUpdateHandler.update(fileMetadata, dbMetadata);
	}
	@Test
	public void ut_a4_testGetExpressionOn() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		MetadataUpdateHandler metadataUpdateHandler = new MetadataUpdateHandler();
		HIMetadataColumns columns = mock(HIMetadataColumns.class);
		when(columns.getId()).thenReturn(100).thenReturn(200);
		Method method = MetadataUpdateHandler.class.getDeclaredMethod("getExpressionOn", String.class,String.class,Map.class,Map.class);
		method.setAccessible(true);
		Map<Integer,HIMetadataTables> tableIdMap = new HashMap<>();
		Map<Integer,HIMetadataColumns> columnIdMap = new HashMap<>();
		columnIdMap.put(1, columns);
		columnIdMap.put(2, columns);
		
		Object invoke = method.invoke(metadataUpdateHandler, "1,2","column",tableIdMap,columnIdMap);
		Assert.assertEquals(invoke.toString(),"100,200");
	}

	@Test
	public void ut_a5_testGetExpressionOn() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		MetadataUpdateHandler metadataUpdateHandler = new MetadataUpdateHandler();
		HIMetadataColumns columns = mock(HIMetadataColumns.class);
		Method method = MetadataUpdateHandler.class.getDeclaredMethod("getExpressionOn", String.class,String.class,Map.class,Map.class);
		method.setAccessible(true);
		Map<Integer,HIMetadataTables> tableIdMap = new HashMap<>();
		Map<Integer,HIMetadataColumns> columnIdMap = new HashMap<>();
		columnIdMap.put(1, columns);
		columnIdMap.put(2, columns);
		
		Object invoke = method.invoke(metadataUpdateHandler, "1,2","columntype",tableIdMap,columnIdMap);
		Assert.assertEquals(invoke.toString(),"");
	}

}
