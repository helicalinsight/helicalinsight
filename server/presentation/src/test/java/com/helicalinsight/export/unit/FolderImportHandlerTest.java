package com.helicalinsight.export.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceFolder;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceEfwContentsService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.ResourceDataReader;
import com.helicalinsight.export.handler.importres.AbstractResourceImportHandler;
import com.helicalinsight.export.handler.importres.FolderImportHandler;
import com.helicalinsight.export.service.DatasourceFactory;
import com.helicalinsight.export.service.DatasourceHandler;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FolderImportHandlerTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testImportResourceSkip()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		FolderImportHandler handler = new FolderImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceFolder folder = mock(HIResourceFolder.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, shareHandler, mock(ManifestUtils.class));

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(request.getOnConflict()).thenReturn("skip");
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileReader.read(context, "parent/folder.efwfolder", HIResourceFolder.class)).thenReturn(folder);
		when(resource.getResourceURL()).thenReturn("parent/folder");

		Map<String, HIResource> urlMap = new HashMap<>();
		when(context.getResourceUrlMap()).thenReturn(urlMap);

		HIResource result = handler.importResource("parent/folder.efwfolder");
		Assert.assertEquals(resource, result);
	}

	@Test
	public void ut_a2_testImportResourceUpdate()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		FolderImportHandler handler = new FolderImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceFolder folder = mock(HIResourceFolder.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		Manifest manifest = mock(Manifest.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceEfwContentsService contentsService = mock(ResourceEfwContentsService.class);

		inject(handler, context, serviceDb, fileReader, shareHandler, manifestUtils);
		setField(handler, "resourceEfwContentsService", contentsService);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getDate()).thenReturn(new Date());
		when(context.recover(resource)).thenReturn(true);
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(manifest.getImages()).thenReturn(null);
		when(manifestUtils.compareOptions(options, manifest, "datasource")).thenReturn(false);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileReader.read(context, "parent/folder.efwfolder", HIResourceFolder.class)).thenReturn(folder);
		when(resource.getResourceId()).thenReturn(1);
		when(resource.getResourceURL()).thenReturn("parent/folder");
		when(folder.getTitle()).thenReturn("folderTitle");
		when(folder.getCreatedBy()).thenReturn(5);
		when(options.getShare()).thenReturn(false);
		when(contentsService.fetchResourceEfwContentByResourceId(anyInt())).thenReturn(Collections.emptyList());

		Map<String, HIResource> urlMap = new HashMap<>();
		when(context.getResourceUrlMap()).thenReturn(urlMap);

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity();
				MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
			Security security = ExportTestSecuritySupport.mockSecurityWithCreatedBy("1");
			securityMock.when(SecurityUtils::securityObject).thenReturn(security);

			HIResource result = handler.importResource("parent/folder.efwfolder");
			Assert.assertEquals(resource, result);
		}
	}

	@Test
	public void ut_a3_testImportResourceCreateNew()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		FolderImportHandler handler = new FolderImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource parent = mock(HIResource.class);
		HIResourceFolder folder = mock(HIResourceFolder.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		Manifest manifest = mock(Manifest.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceEfwContentsService contentsService = mock(ResourceEfwContentsService.class);

		inject(handler, context, serviceDb, fileReader, shareHandler, manifestUtils);
		setField(handler, "resourceEfwContentsService", contentsService);

		Map<String, HIResource> urlMap = new HashMap<>();
		urlMap.put("parent.efwfolder", parent);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getDate()).thenReturn(new Date());
		when(context.getResourceUrlMap()).thenReturn(urlMap);
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(manifest.getImages()).thenReturn(new HashMap<>());
		when(manifestUtils.compareOptions(options, manifest, "datasource")).thenReturn(false);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(null);
		when(fileReader.read(context, "parent/folder.efwfolder", HIResourceFolder.class)).thenReturn(folder);
		when(folder.getTitle()).thenReturn("folderTitle");
		when(folder.getCreatedBy()).thenReturn(3);
		when(parent.getResourceId()).thenReturn(2);
		when(resource.getResourceId()).thenReturn(1);
		when(resource.getResourceURL()).thenReturn("parent/folder");
		when(options.getShare()).thenReturn(true);

		try (MockedStatic<ResourceUtils> resourceUtilsMock = mockStatic(ResourceUtils.class);
				MockedStatic<JsonUtils> jsonUtilsMock = mockStatic(JsonUtils.class)) {
			jsonUtilsMock.when(JsonUtils::getFolderFileExtension).thenReturn("efwfolder");
			resourceUtilsMock.when(() -> ResourceUtils.newHIResource(anyString(), any(), any(), anyString(),
					anyString(), anyString(), any(), anyBoolean())).thenReturn(resource);

			HIResource result = handler.importResource("parent/folder.efwfolder");
			Assert.assertEquals(resource, result);
			Assert.assertTrue(urlMap.containsKey("parent/folder.efwfolder"));
		}
	}

	@Test
	public void ut_a4_testImportResourceWithDatasource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		FolderImportHandler handler = new FolderImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceFolder folder = mock(HIResourceFolder.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		Manifest manifest = mock(Manifest.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceEfwContentsService contentsService = mock(ResourceEfwContentsService.class);
		DatasourceHandler dsHandler = mock(DatasourceHandler.class);

		inject(handler, context, serviceDb, fileReader, shareHandler, manifestUtils);
		setField(handler, "resourceEfwContentsService", contentsService);

		Map<String, HIResource> urlMap = new HashMap<>();

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getDate()).thenReturn(new Date());
		when(context.removeDestination(anyString())).thenReturn("parent/folder");
		when(context.getResourceUrlMap()).thenReturn(urlMap);
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(manifest.getImages()).thenReturn(new HashMap<>());
		when(manifestUtils.compareOptions(options, manifest, "datasource")).thenReturn(true);
		when(manifestUtils.getDatasource("parent/folder", manifest)).thenReturn("ds.json");
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(null);
		when(fileReader.read(context, "parent/folder.efwfolder", HIResourceFolder.class)).thenReturn(folder);
		when(folder.getTitle()).thenReturn("folderTitle");
		when(folder.getCreatedBy()).thenReturn(null);
		when(resource.getResourceURL()).thenReturn("parent/folder");
		when(options.getShare()).thenReturn(false);

		try (MockedStatic<ResourceUtils> resourceUtilsMock = mockStatic(ResourceUtils.class);
				MockedStatic<JsonUtils> jsonUtilsMock = mockStatic(JsonUtils.class);
				MockedStatic<DatasourceFactory> dsFactoryMock = mockStatic(DatasourceFactory.class)) {
			jsonUtilsMock.when(JsonUtils::getFolderFileExtension).thenReturn("efwfolder");
			resourceUtilsMock.when(() -> ResourceUtils.newHIResource(anyString(), any(), any(), anyString(),
					anyString(), anyString(), any(), anyBoolean())).thenReturn(resource);
			dsFactoryMock.when(() -> DatasourceFactory.getHandler("folder")).thenReturn(dsHandler);

			HIResource result = handler.importResource("parent/folder.efwfolder");
			Assert.assertEquals(resource, result);
		}
	}

	@Test
	public void ut_a5_testCreateNewFolder()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		FolderImportHandler handler = new FolderImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceFolder folder = mock(HIResourceFolder.class);

		setField(handler, "context", context);
		setField(handler, "serviceDb", serviceDb);

		when(context.getRequest()).thenReturn(request);
		when(context.getDate()).thenReturn(new Date());
		when(context.getResourceUrlMap()).thenReturn(new HashMap<>());
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(false);
		when(folder.getTitle()).thenReturn("My Folder");
		when(folder.getCreatedBy()).thenReturn(null);

		try (MockedStatic<ResourceUtils> resourceUtilsMock = mockStatic(ResourceUtils.class);
				MockedStatic<JsonUtils> jsonUtilsMock = mockStatic(JsonUtils.class)) {
			jsonUtilsMock.when(JsonUtils::getFolderFileExtension).thenReturn("efwfolder");
			resourceUtilsMock.when(() -> ResourceUtils.newHIResource(anyString(), any(), any(), anyString(),
					anyString(), anyString(), any(), anyBoolean())).thenReturn(resource);

			HIResource result = handler.createNewFolder(folder, "parent/", "parent/folder");
			Assert.assertEquals(resource, result);
		}
	}

	private void inject(FolderImportHandler handler, ImportManagerContext context, HIResourceServiceDB serviceDb,
			ResourceDataReader fileReader, ShareHandler shareHandler, ManifestUtils manifestUtils)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		setField(handler, "context", context);
		setField(handler, "serviceDb", serviceDb);
		setField(handler, "fileReader", fileReader);
		setField(handler, "shareHandler", shareHandler);
		setField(handler, "manifestUtils", manifestUtils);
	}

	protected void setField(Object target, String name, Object value)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = target.getClass();
		Field field = null;
		while (clazz != null) {
			try {
				field = clazz.getDeclaredField(name);
				break;
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		if (field == null) {
			field = AbstractResourceImportHandler.class.getDeclaredField(name);
		}
		field.setAccessible(true);
		field.set(target, value);
	}

}
