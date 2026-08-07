package com.helicalinsight.export.unit;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.export.ExportResourceManager;
import com.helicalinsight.export.dto.ResourceExportRequest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.export.handler.ResourceExportHandler;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;
import com.helicalinsight.test.utility.SkipTest;

import jakarta.servlet.http.HttpServletResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Category(SkipTest.class)
public class ResourceExportHandlerTest extends ExportUnitTestBase {

	private static final long FILE_RESOURCE_TYPE_ID = 1L;

	private List<ResourceType> fileResourceTypes() {
		ResourceType resourceType = new ResourceType();
		resourceType.setResourceTypeId(FILE_RESOURCE_TYPE_ID);
		resourceType.setExtension(".hr");
		resourceType.setName("file");
		return Collections.singletonList(resourceType);
	}

	private void configureFileResource(HIResource resource) {
		resource.setResourceTypeId(FILE_RESOURCE_TYPE_ID);
		resource.setVisible(true);
	}

	private ResourceExportHandler createHandler(HIResourceServiceDB serviceDb, ExportResourceManager manager)
			throws Exception {
		ResourceExportHandler handler = new ResourceExportHandler();
		setField(handler, "serviceDb", serviceDb);
		setField(handler, "manager", manager);
		return handler;
	}

	@Test
	public void ut_a1_testExportAllResources() throws Exception {
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		ExportResourceManager manager = mock(ExportResourceManager.class);
		ResourceExportHandler handler = createHandler(serviceDb, manager);
		ResourceExportRequest request = new ResourceExportRequest();
		request.setDir("");
		request.setFile("");
		request.setOptions(new ResourceOptions());
		HttpServletResponse response = mock(HttpServletResponse.class);
		HIResourceOfActiveUser activeUser = mock(HIResourceOfActiveUser.class);
		List<HIResourceDTO> dtoList = new ArrayList<>();
		byte[] expected = new byte[] { 1, 2, 3 };

		when(serviceDb.findAllResources()).thenReturn(activeUser);
		when(activeUser.getResourceDTOList()).thenReturn(dtoList);
		when(manager.write(dtoList, "", request.getOptions(), response)).thenReturn(expected);

		Assert.assertArrayEquals(expected, handler.export(request, response));
	}

	@Test
	public void ut_a2_testExportSingleResource() throws Exception {
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		ExportResourceManager manager = mock(ExportResourceManager.class);
		ResourceExportHandler handler = createHandler(serviceDb, manager);
		ResourceExportRequest request = new ResourceExportRequest();
		request.setDir("Reports");
		request.setFile("");
		request.setOptions(new ResourceOptions());
		HttpServletResponse response = mock(HttpServletResponse.class);
		HIResource resource = new HIResource();
		resource.setResourceId(1);
		resource.setFolder(false);
		resource.setResourceURL("Reports");
		resource.setCreatedBy(1);
		resource.setDeleted(false);
		resource.setTitle("Reports");
		resource.setLastUpdatedTime(new Date());
		configureFileResource(resource);
		Map<Integer, Integer> securityMap = new HashMap<>();
		byte[] expected = new byte[] { 4, 5 };

		when(serviceDb.getResourceByUrl("Reports")).thenReturn(resource);
		when(serviceDb.getSecurityMap()).thenReturn(securityMap);
		when(manager.write(any(), eq("Reports"), eq(request.getOptions()), eq(response))).thenReturn(expected);

		ResourcePermissionLevelsHolder permissionHolder = mock(ResourcePermissionLevelsHolder.class);
		ResourceTypeServiceDB resourceTypeService = mock(ResourceTypeServiceDB.class);
		when(permissionHolder.readAccessLevel()).thenReturn(2);
		when(resourceTypeService.getAllResourceTypes()).thenReturn(fileResourceTypes());

		try (MockedStatic<ApplicationContextAccessor> contextAccessor = mockStatic(ApplicationContextAccessor.class);
				MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
			contextAccessor.when(() -> ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class))
					.thenReturn(permissionHolder);
			contextAccessor.when(() -> ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class))
					.thenReturn(resourceTypeService);
			authUtils.when(AuthenticationUtils::getUserId).thenReturn("1");
			Assert.assertArrayEquals(expected, handler.export(request, response));
		}
	}

	@Test
	public void ut_a3_testExportNestedPath() throws Exception {
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		ExportResourceManager manager = mock(ExportResourceManager.class);
		ResourceExportHandler handler = createHandler(serviceDb, manager);
		ResourceExportRequest request = new ResourceExportRequest();
		request.setDir("parent/child");
		request.setFile("");
		request.setOptions(new ResourceOptions());
		HttpServletResponse response = mock(HttpServletResponse.class);
		HIResource parent = new HIResource();
		parent.setResourceId(1);
		parent.setFolder(true);
		parent.setResourceURL("parent");
		parent.setCreatedBy(1);
		parent.setDeleted(false);
		parent.setTitle("parent");
		parent.setLastUpdatedTime(new Date());
		HIResource child = new HIResource();
		child.setResourceId(2);
		child.setFolder(false);
		child.setResourceURL("parent/child");
		child.setParentId(1);
		child.setCreatedBy(1);
		child.setDeleted(false);
		child.setTitle("child");
		child.setLastUpdatedTime(new Date());
		configureFileResource(child);
		Map<Integer, Integer> securityMap = new HashMap<>();
		byte[] expected = new byte[] { 7 };

		when(serviceDb.findResourceByUrl("parent")).thenReturn(parent);
		when(serviceDb.findResourceByUrl("parent/child")).thenReturn(child);
		when(serviceDb.getResourceByParentId(2)).thenReturn(Collections.emptyList());
		when(serviceDb.getSecurityMap()).thenReturn(securityMap);
		when(manager.write(any(), eq("parent/child"), eq(request.getOptions()), eq(response))).thenReturn(expected);

		ResourcePermissionLevelsHolder permissionHolder = mock(ResourcePermissionLevelsHolder.class);
		ResourceTypeServiceDB resourceTypeService = mock(ResourceTypeServiceDB.class);
		when(permissionHolder.readAccessLevel()).thenReturn(2);
		when(resourceTypeService.getAllResourceTypes()).thenReturn(fileResourceTypes());

		try (MockedStatic<ApplicationContextAccessor> contextAccessor = mockStatic(ApplicationContextAccessor.class);
				MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
			contextAccessor.when(() -> ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class))
					.thenReturn(permissionHolder);
			contextAccessor.when(() -> ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class))
					.thenReturn(resourceTypeService);
			authUtils.when(AuthenticationUtils::getUserId).thenReturn("1");
			Assert.assertArrayEquals(expected, handler.export(request, response));
		}
	}

	@Test(expected = ResourceExportException.class)
	public void ut_a4_testExportResourceNotFound() throws Exception {
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		ExportResourceManager manager = mock(ExportResourceManager.class);
		ResourceExportHandler handler = createHandler(serviceDb, manager);
		ResourceExportRequest request = new ResourceExportRequest();
		request.setDir("missing");
		request.setFile("");
		request.setOptions(new ResourceOptions());
		HttpServletResponse response = mock(HttpServletResponse.class);

		when(serviceDb.getResourceByUrl("missing")).thenReturn(null);
		handler.export(request, response);
	}

	@Test(expected = ResourceExportException.class)
	public void ut_a5_testExportNestedResourceNotFound() throws Exception {
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		ExportResourceManager manager = mock(ExportResourceManager.class);
		ResourceExportHandler handler = createHandler(serviceDb, manager);
		ResourceExportRequest request = new ResourceExportRequest();
		request.setDir("parent/missing");
		request.setFile("");
		request.setOptions(new ResourceOptions());
		HttpServletResponse response = mock(HttpServletResponse.class);
		HIResource parent = new HIResource();
		parent.setResourceId(1);
		parent.setFolder(true);
		parent.setResourceURL("parent");

		when(serviceDb.findResourceByUrl("parent")).thenReturn(parent);
		when(serviceDb.findResourceByUrl("parent/missing")).thenReturn(null);
		handler.export(request, response);
	}

	@Test
	public void ut_a6_testExportWithFileAppendedToDir() throws Exception {
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		ExportResourceManager manager = mock(ExportResourceManager.class);
		ResourceExportHandler handler = createHandler(serviceDb, manager);
		ResourceExportRequest request = new ResourceExportRequest();
		request.setDir("Reports");
		request.setFile("report.hr");
		request.setOptions(new ResourceOptions());
		HttpServletResponse response = mock(HttpServletResponse.class);
		HIResource parent = new HIResource();
		parent.setResourceId(1);
		parent.setFolder(true);
		parent.setResourceURL("Reports");
		parent.setCreatedBy(1);
		parent.setDeleted(false);
		parent.setTitle("Reports");
		parent.setLastUpdatedTime(new Date());
		HIResource child = new HIResource();
		child.setResourceId(2);
		child.setFolder(false);
		child.setResourceURL("Reports/report.hr");
		child.setParentId(1);
		child.setCreatedBy(1);
		child.setDeleted(false);
		child.setTitle("report.hr");
		child.setLastUpdatedTime(new Date());
		configureFileResource(child);
		Map<Integer, Integer> securityMap = new HashMap<>();
		byte[] expected = new byte[] { 9 };

		when(serviceDb.findResourceByUrl("Reports")).thenReturn(parent);
		when(serviceDb.findResourceByUrl("Reports/report.hr")).thenReturn(child);
		when(serviceDb.getSecurityMap()).thenReturn(securityMap);
		when(manager.write(any(), eq("Reports"), eq(request.getOptions()), eq(response))).thenReturn(expected);

		ResourcePermissionLevelsHolder permissionHolder = mock(ResourcePermissionLevelsHolder.class);
		ResourceTypeServiceDB resourceTypeService = mock(ResourceTypeServiceDB.class);
		when(permissionHolder.readAccessLevel()).thenReturn(2);
		when(resourceTypeService.getAllResourceTypes()).thenReturn(fileResourceTypes());

		try (MockedStatic<ApplicationContextAccessor> contextAccessor = mockStatic(ApplicationContextAccessor.class);
				MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
			contextAccessor.when(() -> ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class))
					.thenReturn(permissionHolder);
			contextAccessor.when(() -> ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class))
					.thenReturn(resourceTypeService);
			authUtils.when(AuthenticationUtils::getUserId).thenReturn("1");
			Assert.assertArrayEquals(expected, handler.export(request, response));
		}
	}
}
