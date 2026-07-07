package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.dto.OrganizationDTO;
import com.helicalinsight.admin.dto.RoleDTO;
import com.helicalinsight.admin.dto.UserDTO;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceSecurityDB;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.export.utils.JsonMapperUtils;
import com.helicalinsight.export.utils.ResourceShareUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShareHandlerTest {

	@Test
	public void ut_a1_testImportResource() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ShareHandler shareHandler = new ShareHandler();
		HIResource resource = mock(HIResource.class);
		ImportManagerContext context = mock(ImportManagerContext.class);
		Field field = ResourceIOHandler.class.getDeclaredField("context");
		field.setAccessible(true);
		field.set(shareHandler, context);
		
		shareHandler.importResource(resource,  "dsFileName", "onConflict");
	}

	@Test
	public void ut_a2_testImportResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ShareHandler shareHandler = new ShareHandler();
		HIResource resource = mock(HIResource.class);
		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
		HIResourceSecurityDB share = mock(HIResourceSecurityDB.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		ResourceDTOMapper mapper = mock(ResourceDTOMapper.class);
		User user = mock(User.class);
		UserDTO userDTO = mock(UserDTO.class);
		
		ImportManagerContext context = mock(ImportManagerContext.class);
		Field field0 = ResourceIOHandler.class.getDeclaredField("context");
		field0.setAccessible(true);
		field0.set(shareHandler, context);
		
		Field field = ResourceIOHandler.class.getDeclaredField("mapperUtils");
		field.setAccessible(true);
		field.set(shareHandler, mapperUtils);

		Field field1 = ResourceIOHandler.class.getDeclaredField("serviceDb");
		field1.setAccessible(true);
		field1.set(shareHandler, serviceDb);
		
		Field field2 = ResourceIOHandler.class.getDeclaredField("shareUtils");
		field2.setAccessible(true);
		field2.set(shareHandler, shareUtils);
		
		Field field3 = ShareHandler.class.getDeclaredField("dtoMapper");
	    field3.setAccessible(true);
	    field3.set(shareHandler, mapper);
		
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		ArrayNode shareList = jsonNodeFactory.arrayNode();
		ObjectNode node = jsonNodeFactory.objectNode();
	    node.put("createdBy", "User1");
		ObjectNode node1 = jsonNodeFactory.objectNode();
	    node1.put("createdBy",node);
	    shareList.add(node1);
	    
	    List<HIResourceSecurityDB> existingShareList = Arrays.asList(share);
	     
		when(mapperUtils.mapToArray(any(FileInputStream.class))).thenReturn(shareList);
		when(mapper.map(any(User.class))).thenReturn(userDTO);
		when(mapperUtils.mapToDTO("{}", HIResourceSecurityDB.class)).thenReturn(share);
		when(mapperUtils.mapToDTO(node.toString(), User.class)).thenReturn(user);
		when(serviceDb.getHIResourceSecurityByResourceId(anyInt())).thenReturn(existingShareList);
		when(shareUtils.getOrInsertUser(any(UserDTO.class))).thenReturn(user);
		when(share.getUserId()).thenReturn(user);
		when(user.getUsername()).thenReturn("helical");
		when(share.getPermission()).thenReturn(5);
		try (MockedConstruction<FileInputStream> construction = mockConstruction(FileInputStream.class)) {
			shareHandler.importResource(resource,  "dsFileName", "onConflict");
		}
	}

	@Test
	public void ut_a3_testImportResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ShareHandler shareHandler = new ShareHandler();
		HIResource resource = mock(HIResource.class);
		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
		HIResourceSecurityDB share = mock(HIResourceSecurityDB.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		ResourceDTOMapper mapper = mock(ResourceDTOMapper.class);
		UserDTO userDTO = mock(UserDTO.class);
		RoleDTO roleDTO = mock(RoleDTO.class);
		User user = mock(User.class);
		Role role = mock(Role.class);
		
		ImportManagerContext context = mock(ImportManagerContext.class);
		Field field0 = ResourceIOHandler.class.getDeclaredField("context");
		field0.setAccessible(true);
		field0.set(shareHandler, context);
		
		Field field = ResourceIOHandler.class.getDeclaredField("mapperUtils");
		field.setAccessible(true);
		field.set(shareHandler, mapperUtils);

		Field field1 = ResourceIOHandler.class.getDeclaredField("serviceDb");
		field1.setAccessible(true);
		field1.set(shareHandler, serviceDb);
		
		Field field2 = ResourceIOHandler.class.getDeclaredField("shareUtils");
		field2.setAccessible(true);
		field2.set(shareHandler, shareUtils);
		
		Field field3 = ShareHandler.class.getDeclaredField("dtoMapper");
	    field3.setAccessible(true);
	    field3.set(shareHandler, mapper);
		
		
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		ArrayNode shareList = jsonNodeFactory.arrayNode();
		ObjectNode node = jsonNodeFactory.objectNode();
	    node.put("createdBy", "User1");
		ObjectNode node1 = jsonNodeFactory.objectNode();
	    node1.put("createdBy",node);
	    shareList.add(node1);
	    
	    List<HIResourceSecurityDB> existingShareList = Arrays.asList(share);
	     
	    when(mapper.map(any(User.class))).thenReturn(userDTO);
	    when(mapper.map(any(Role.class))).thenReturn(roleDTO);
		when(mapperUtils.mapToArray(any(FileInputStream.class))).thenReturn(shareList);
		when(mapperUtils.mapToDTO("{}", HIResourceSecurityDB.class)).thenReturn(share);
		when(mapperUtils.mapToDTO(node.toString(), User.class)).thenReturn(user);
		when(serviceDb.getHIResourceSecurityByResourceId(anyInt())).thenReturn(existingShareList);
		when(shareUtils.getOrInsertUser(any(UserDTO.class))).thenReturn(user);
		when(share.getRoleId()).thenReturn(role);
		when(role.getRole_name()).thenReturn("helical");
		when(share.getPermission()).thenReturn(5);
		try (MockedConstruction<FileInputStream> construction = mockConstruction(FileInputStream.class)) {
			shareHandler.importResource(resource,  "dsFileName", "onConflict");
		}
	}

	@Test
	public void ut_a4_testImportResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ShareHandler shareHandler = new ShareHandler();
		HIResource resource = mock(HIResource.class);
		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
		HIResourceSecurityDB share = mock(HIResourceSecurityDB.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		ResourceDTOMapper mapper = mock(ResourceDTOMapper.class);
		User user = mock(User.class);
		Organization organization = mock(Organization.class);
		UserDTO userDTO = mock(UserDTO.class);
		RoleDTO roleDTO = mock(RoleDTO.class);
		OrganizationDTO orgDTO =  mock(OrganizationDTO.class);
		
		
		ImportManagerContext context = mock(ImportManagerContext.class);
		Field field0 = ResourceIOHandler.class.getDeclaredField("context");
		field0.setAccessible(true);
		field0.set(shareHandler, context);
		
		Field field = ResourceIOHandler.class.getDeclaredField("mapperUtils");
		field.setAccessible(true);
		field.set(shareHandler, mapperUtils);

		Field field1 = ResourceIOHandler.class.getDeclaredField("serviceDb");
		field1.setAccessible(true);
		field1.set(shareHandler, serviceDb);
		
		Field field2 = ResourceIOHandler.class.getDeclaredField("shareUtils");
		field2.setAccessible(true);
		field2.set(shareHandler, shareUtils);
		
		Field field3 = ShareHandler.class.getDeclaredField("dtoMapper");
	    field3.setAccessible(true);
	    field3.set(shareHandler, mapper);
		
		
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		ArrayNode shareList = jsonNodeFactory.arrayNode();
		ObjectNode node = jsonNodeFactory.objectNode();
	    node.put("createdBy", "User1");
		ObjectNode node1 = jsonNodeFactory.objectNode();
	    node1.put("createdBy",node);
	    shareList.add(node1);
	    
	    List<HIResourceSecurityDB> existingShareList = Arrays.asList(share);
	    
	    when(mapper.map(any(User.class))).thenReturn(userDTO);
	    when(mapper.map(any(Role.class))).thenReturn(roleDTO);
	    when(mapper.map(any(Organization.class))).thenReturn(orgDTO);
		when(mapperUtils.mapToArray(any(FileInputStream.class))).thenReturn(shareList);
		when(mapperUtils.mapToDTO("{}", HIResourceSecurityDB.class)).thenReturn(share);
		when(mapperUtils.mapToDTO(node.toString(), User.class)).thenReturn(user);
		when(serviceDb.getHIResourceSecurityByResourceId(anyInt())).thenReturn(existingShareList);
		when(shareUtils.getOrInsertUser(any(UserDTO.class))).thenReturn(user);
		when(share.getOrgId()).thenReturn(organization);
		when(organization.getOrg_name()).thenReturn("helical");
		when(share.getPermission()).thenReturn(5);
		try (MockedConstruction<FileInputStream> construction = mockConstruction(FileInputStream.class)) {
			shareHandler.importResource(resource, "dsFileName", "onConflict");
		}
	}
	
	@Test
	public void ut_a5_testImportResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ShareHandler shareHandler = new ShareHandler();
		HIResource resource = mock(HIResource.class);
		ResourceDTOMapper mapper = mock(ResourceDTOMapper.class);
		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
		HIResourceSecurityDB share = mock(HIResourceSecurityDB.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		User user = mock(User.class);
		Role role = mock(Role.class);
		
		UserDTO userDTO = mock(UserDTO.class);
		RoleDTO roleDTO = mock(RoleDTO.class);
		
		HIResourceSecurityDB hiResourceSecurityDB = mock(HIResourceSecurityDB.class);
		
		ImportManagerContext context = mock(ImportManagerContext.class);
		Field field0 = ResourceIOHandler.class.getDeclaredField("context");
		field0.setAccessible(true);
		field0.set(shareHandler, context);
		
		Field field = ResourceIOHandler.class.getDeclaredField("mapperUtils");
		field.setAccessible(true);
		field.set(shareHandler, mapperUtils);

		Field field1 = ResourceIOHandler.class.getDeclaredField("serviceDb");
		field1.setAccessible(true);
		field1.set(shareHandler, serviceDb);
		
		Field field2 = ResourceIOHandler.class.getDeclaredField("shareUtils");
		field2.setAccessible(true);
		field2.set(shareHandler, shareUtils);
		
		Field field3 = ShareHandler.class.getDeclaredField("dtoMapper");
	    field3.setAccessible(true);
	    field3.set(shareHandler, mapper);
		
		
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		ArrayNode shareList = jsonNodeFactory.arrayNode();
		ObjectNode node = jsonNodeFactory.objectNode();
	    node.put("createdBy", "User1");
		ObjectNode node1 = jsonNodeFactory.objectNode();
	    node1.put("createdBy",node);
	    shareList.add(node1);
	    
	    List<HIResourceSecurityDB> existingShareList = Arrays.asList(hiResourceSecurityDB);
	    
	    when(mapper.map(any(User.class))).thenReturn(userDTO);
	    when(mapper.map(any(Role.class))).thenReturn(roleDTO);
		when(mapperUtils.mapToArray(any(FileInputStream.class))).thenReturn(shareList);
		when(mapperUtils.mapToDTO("{}", HIResourceSecurityDB.class)).thenReturn(share);
		when(mapperUtils.mapToDTO(node.toString(), User.class)).thenReturn(user);
		when(serviceDb.getHIResourceSecurityByResourceId(anyInt())).thenReturn(existingShareList);
		when(shareUtils.getOrInsertUser(any(UserDTO.class))).thenReturn(user);
		when(share.getRoleId()).thenReturn(role);
		when(user.getUsername()).thenReturn("helical");
		when(share.getPermission()).thenReturn(5);
		try (MockedConstruction<FileInputStream> construction = mockConstruction(FileInputStream.class)) {
			shareHandler.importResource(resource,  "dsFileName", "onConflict");
		}
	}

	@Test
	public void ut_a6_testImportResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ShareHandler shareHandler = new ShareHandler();
		HIResource resource = mock(HIResource.class);
		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
		HIResourceSecurityDB share = mock(HIResourceSecurityDB.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		User user = mock(User.class);
		Organization organization = mock(Organization.class);
		
		ResourceDTOMapper mapper = mock(ResourceDTOMapper.class);
		UserDTO userDTO = mock(UserDTO.class);
		RoleDTO roleDTO = mock(RoleDTO.class);
		
		
		HIResourceSecurityDB hiResourceSecurityDB = mock(HIResourceSecurityDB.class);
		ImportManagerContext context = mock(ImportManagerContext.class);
		Field field0 = ResourceIOHandler.class.getDeclaredField("context");
		field0.setAccessible(true);
		field0.set(shareHandler, context);
		
		Field field = ResourceIOHandler.class.getDeclaredField("mapperUtils");
		field.setAccessible(true);
		field.set(shareHandler, mapperUtils);

		Field field1 = ResourceIOHandler.class.getDeclaredField("serviceDb");
		field1.setAccessible(true);
		field1.set(shareHandler, serviceDb);
		
		Field field2 = ResourceIOHandler.class.getDeclaredField("shareUtils");
		field2.setAccessible(true);
		field2.set(shareHandler, shareUtils);
		
		Field field3 = ShareHandler.class.getDeclaredField("dtoMapper");
	    field3.setAccessible(true);
	    field3.set(shareHandler, mapper);
		
		
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		ArrayNode shareList = jsonNodeFactory.arrayNode();
		ObjectNode node = jsonNodeFactory.objectNode();
	    node.put("createdBy", "User1");
		ObjectNode node1 = jsonNodeFactory.objectNode();
	    node1.put("createdBy",node);
	    shareList.add(node1);
	    
	    List<HIResourceSecurityDB> existingShareList = Arrays.asList(hiResourceSecurityDB);
	    
	    when(mapper.map(any(User.class))).thenReturn(userDTO);
	    when(mapper.map(any(Role.class))).thenReturn(roleDTO);
		when(mapperUtils.mapToArray(any(FileInputStream.class))).thenReturn(shareList);
		when(mapperUtils.mapToDTO("{}", HIResourceSecurityDB.class)).thenReturn(share);
		when(mapperUtils.mapToDTO(node.toString(), User.class)).thenReturn(user);
		when(serviceDb.getHIResourceSecurityByResourceId(anyInt())).thenReturn(existingShareList);
		when(shareUtils.getOrInsertUser(any(UserDTO.class))).thenReturn(user);
		when(share.getOrgId()).thenReturn(organization);
		when(user.getUsername()).thenReturn("helical");
		when(share.getPermission()).thenReturn(5);
		try (MockedConstruction<FileInputStream> construction = mockConstruction(FileInputStream.class)) {
			shareHandler.importResource(resource,  "dsFileName", "onConflict");
		}
	}

	@Test
	public void ut_a7_testImportResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ShareHandler shareHandler = new ShareHandler();
		HIResource resource = mock(HIResource.class);
		JsonMapperUtils mapperUtils = mock(JsonMapperUtils.class);
		HIResourceSecurityDB share = mock(HIResourceSecurityDB.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		User user = mock(User.class);
		Organization organization = mock(Organization.class);
		ResourceDTOMapper mapper = mock(ResourceDTOMapper.class);
		
		UserDTO userDTO = mock(UserDTO.class);
		RoleDTO roleDTO = mock(RoleDTO.class);
		
		when(mapper.map(any(User.class))).thenReturn(userDTO);
		when(mapper.map(any(Role.class))).thenReturn(roleDTO);
		
		HIResourceSecurityDB hiResourceSecurityDB = mock(HIResourceSecurityDB.class);
		
		ImportManagerContext context = mock(ImportManagerContext.class);
		Field field0 = ResourceIOHandler.class.getDeclaredField("context");
		field0.setAccessible(true);
		field0.set(shareHandler, context);
		
		Field field = ResourceIOHandler.class.getDeclaredField("mapperUtils");
		field.setAccessible(true);
		field.set(shareHandler, mapperUtils);

		Field field1 = ResourceIOHandler.class.getDeclaredField("serviceDb");
		field1.setAccessible(true);
		field1.set(shareHandler, serviceDb);
		
		Field field2 = ResourceIOHandler.class.getDeclaredField("shareUtils");
		field2.setAccessible(true);
		field2.set(shareHandler, shareUtils);
		
		Field field3 = ShareHandler.class.getDeclaredField("dtoMapper");
	    field3.setAccessible(true);
	    field3.set(shareHandler, mapper);
		
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		ArrayNode shareList = jsonNodeFactory.arrayNode();
		ObjectNode node = jsonNodeFactory.objectNode();
	    node.put("createdBy", "User1");
		ObjectNode node1 = jsonNodeFactory.objectNode();
	    node1.put("createdBy",node);
	    shareList.add(node1);
	    
	    List<HIResourceSecurityDB> existingShareList = Arrays.asList(hiResourceSecurityDB);
	     
		when(mapperUtils.mapToArray(any(FileInputStream.class))).thenReturn(shareList);
		when(mapperUtils.mapToDTO("{}", HIResourceSecurityDB.class)).thenReturn(share);
		when(mapperUtils.mapToDTO(node.toString(), User.class)).thenReturn(user);
		when(serviceDb.getHIResourceSecurityByResourceId(anyInt())).thenReturn(existingShareList);
		when(shareUtils.getOrInsertUser(any(UserDTO.class))).thenReturn(user);
		when(share.getUserId()).thenReturn(user);
		when(user.getUsername()).thenReturn("helical");
		when(share.getPermission()).thenReturn(5);
		try (MockedConstruction<FileInputStream> construction = mockConstruction(FileInputStream.class)) {
			shareHandler.importResource(resource,  "dsFileName", "onConflict");
		}
	}

}
