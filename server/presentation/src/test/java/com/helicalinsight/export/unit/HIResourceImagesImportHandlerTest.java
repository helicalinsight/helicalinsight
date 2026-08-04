package com.helicalinsight.export.unit;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceImages;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.ResourceDataReader;
import com.helicalinsight.export.handler.importres.AbstractResourceImportHandler;
import com.helicalinsight.export.handler.importres.HIResourceImagesImportHandler;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HIResourceImagesImportHandlerTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testImportResourceSkip()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HIResourceImagesImportHandler handler = new HIResourceImagesImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource fileResource = mock(HIResource.class);
		HIResourceImages images = mock(HIResourceImages.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);

		inject(handler, context, serviceDb, fileReader, shareHandler);

		when(context.getRequest()).thenReturn(request);
		when(context.getNewOldImageIds()).thenReturn(new HashMap<>());
		when(request.getOnConflict()).thenReturn("skip");
		when(request.getOptions()).thenReturn(options);
		when(options.getSchedules()).thenReturn(false);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileReader.read(context, "parent/image.hiimg", HIResource.class)).thenReturn(fileResource);
		when(fileResource.getHiResourceImages()).thenReturn(images);
		when(resource.getResourceURL()).thenReturn("parent/image.hiimg");

		HIResource result = handler.importResource("parent/image.hiimg");
		Assert.assertEquals(resource, result);
	}

	@Test
	public void ut_a2_testImportResourceUpdate()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HIResourceImagesImportHandler handler = new HIResourceImagesImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource fileResource = mock(HIResource.class);
		HIResourceImages importedImages = mock(HIResourceImages.class);
		HIResourceImages dbImages = mock(HIResourceImages.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		Manifest manifest = mock(Manifest.class);
		Map<Integer, String> imageIdMap = new HashMap<>();

		inject(handler, context, serviceDb, fileReader, shareHandler);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.recover(resource)).thenReturn(true);
		when(context.getNewOldImageIds()).thenReturn(imageIdMap);
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(false);
		when(options.getSchedules()).thenReturn(false);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileReader.read(context, "parent/image.hiimg", HIResource.class)).thenReturn(fileResource);
		when(fileResource.getHiResourceImages()).thenReturn(importedImages);
		when(fileResource.getResourceId()).thenReturn(100);
		when(resource.getHiResourceImages()).thenReturn(dbImages);
		when(resource.getResourceId()).thenReturn(200);
		when(resource.getResourceURL()).thenReturn("parent/image.hiimg");

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity();
				MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
			Security security = ExportTestSecuritySupport.mockSecurityWithCreatedBy("1");
			securityMock.when(SecurityUtils::securityObject).thenReturn(security);

			HIResource result = handler.importResource("parent/image.hiimg");
			Assert.assertEquals(resource, result);
			Assert.assertTrue(imageIdMap.containsKey(100));
		}
	}

	@Test
	public void ut_a3_testImportResourceCreateNew()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HIResourceImagesImportHandler handler = new HIResourceImagesImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource fileResource = mock(HIResource.class);
		HIResourceImages images = mock(HIResourceImages.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		Manifest manifest = mock(Manifest.class);
		Map<Integer, String> imageIdMap = new HashMap<>();

		inject(handler, context, serviceDb, fileReader, shareHandler);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getDate()).thenReturn(new Date());
		when(context.getResourceUrlMap()).thenReturn(new HashMap<>());
		when(context.getNewOldImageIds()).thenReturn(imageIdMap);
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(true);
		when(options.getSchedules()).thenReturn(false);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(null);
		when(fileReader.read(context, "parent/image.hiimg", HIResource.class)).thenReturn(fileResource);
		when(fileResource.getHiResourceImages()).thenReturn(images);
		when(fileResource.getResourceId()).thenReturn(100);
		when(fileResource.getCreatedBy()).thenReturn(3);
		when(serviceDb.addHIResource(resource)).thenReturn(300);
		when(resource.getResourceURL()).thenReturn("parent/image.hiimg");

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity();
				MockedStatic<ResourceUtils> resourceUtilsMock = mockStatic(ResourceUtils.class);
				MockedStatic<JsonUtils> jsonUtilsMock = mockStatic(JsonUtils.class);
				MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
			jsonUtilsMock.when(JsonUtils::getImageExtension).thenReturn("hiimg");
			jsonUtilsMock.when(JsonUtils::getFolderFileExtension).thenReturn("efwfolder");
			resourceUtilsMock.when(() -> ResourceUtils.newHIResource(anyString(), any(), any(), anyString(),
					anyString(), anyString(), any(), anyBoolean())).thenReturn(resource);
			Security security = ExportTestSecuritySupport.mockSecurityWithCreatedBy("1");
			securityMock.when(SecurityUtils::securityObject).thenReturn(security);

			HIResource result = handler.importResource("parent/image.hiimg");
			Assert.assertEquals(resource, result);
			Assert.assertTrue(imageIdMap.containsKey(100));
		}
	}

	@Test
	public void ut_a4_testImportResourceWithSchedules()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HIResourceImagesImportHandler handler = new HIResourceImagesImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource fileResource = mock(HIResource.class);
		HIResourceImages images = mock(HIResourceImages.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ResourceIOHandler scheduleHandler = mock(ResourceIOHandler.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, shareHandler);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getNewOldImageIds()).thenReturn(new HashMap<>());
		when(request.getOnConflict()).thenReturn("skip");
		when(request.getOptions()).thenReturn(options);
		when(options.getSchedules()).thenReturn(true);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileReader.read(context, "parent/image.hiimg", HIResource.class)).thenReturn(fileResource);
		when(fileResource.getHiResourceImages()).thenReturn(images);
		when(resource.getResourceURL()).thenReturn("parent/image.hiimg");

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity()) {
			appMock.when(() -> ApplicationContextAccessor.getBean("scheduleIOHandler")).thenReturn(scheduleHandler);
			HIResource result = handler.importResource("parent/image.hiimg");
			Assert.assertEquals(resource, result);
		}
	}

	private void inject(HIResourceImagesImportHandler handler, ImportManagerContext context,
			HIResourceServiceDB serviceDb, ResourceDataReader fileReader, ShareHandler shareHandler)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		setField(handler, "context", context);
		setField(handler, "serviceDb", serviceDb);
		setField(handler, "fileReader", fileReader);
		setField(handler, "shareHandler", shareHandler);
	}

	protected void setField(Object target, String name, Object value)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = AbstractResourceImportHandler.class.getDeclaredField(name);
		field.setAccessible(true);
		field.set(target, value);
	}

}
