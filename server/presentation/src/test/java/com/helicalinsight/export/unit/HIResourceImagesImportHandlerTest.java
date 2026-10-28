package com.helicalinsight.export.unit;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentCaptor;
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
import com.helicalinsight.export.exception.ResourceImportException;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.ResourceDataReader;
import com.helicalinsight.export.handler.importres.AbstractResourceImportHandler;
import com.helicalinsight.export.handler.importres.HIResourceImagesImportHandler;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.export.utils.ResourceFileUtils;
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
		HIResourceImages sidecarImages = mock(HIResourceImages.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, shareHandler, manifestUtils, fileUtils);
		stubSidecarPath(context, manifestUtils, manifest, fileReader, fileUtils, fileResource, images, sidecarImages);

		when(context.getRequest()).thenReturn(request);
		when(context.getNewOldImageIds()).thenReturn(new HashMap<>());
		when(request.getOnConflict()).thenReturn("skip");
		when(request.getOptions()).thenReturn(options);
		when(options.getSchedules()).thenReturn(false);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(resource.getResourceURL()).thenReturn("parent/image.hiimg");

		HIResource result = handler.importResource("parent/image.hiimg");
		Assert.assertEquals(resource, result);
		verify(context).appendSkip("parent/image.hiimg");
		verify(serviceDb, never()).editHIResource(any(HIResource.class));
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
		HIResourceImages sidecarImages = mock(HIResourceImages.class);
		HIResourceImages dbImages = mock(HIResourceImages.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		Manifest manifest = mock(Manifest.class);
		Map<Integer, String> imageIdMap = new HashMap<>();

		inject(handler, context, serviceDb, fileReader, shareHandler, manifestUtils, fileUtils);
		stubSidecarPath(context, manifestUtils, manifest, fileReader, fileUtils, fileResource, importedImages, sidecarImages);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.recover(resource)).thenReturn(true);
		when(context.getNewOldImageIds()).thenReturn(imageIdMap);
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(false);
		when(options.getSchedules()).thenReturn(false);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileResource.getResourceId()).thenReturn(100);
		when(fileResource.getCreatedBy()).thenReturn(4);
		when(fileResource.getTitle()).thenReturn("Imported Title");
		when(fileResource.getResourcePath()).thenReturn("/imported/path");
		when(resource.getHiResourceImages()).thenReturn(dbImages);
		when(resource.getResourceId()).thenReturn(200);
		when(resource.getResourceURL()).thenReturn("parent/image.hiimg");
		when(sidecarImages.getContentType()).thenReturn("image/jpeg");
		when(sidecarImages.getContent()).thenReturn(new byte[] { 9 });
		when(importedImages.getContentType()).thenReturn("image/jpeg");
		when(importedImages.getContent()).thenReturn(new byte[] { 9 });

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity();
				MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
			Security security = ExportTestSecuritySupport.mockSecurityWithCreatedBy("1");
			securityMock.when(SecurityUtils::securityObject).thenReturn(security);

			HIResource result = handler.importResource("parent/image.hiimg");
			Assert.assertEquals(resource, result);
			Assert.assertEquals("200:parent/image.hiimg", imageIdMap.get(100));
			verify(importedImages).setContentType("image/jpeg");
			verify(importedImages).setContent(new byte[] { 9 });
			verify(dbImages).setContentType("image/jpeg");
			verify(dbImages).setContent(new byte[] { 9 });
			verify(resource).setCreatedBy(1);
			verify(resource).setTitle("Imported Title");
			verify(resource).setResourcePath("/imported/path");
			verify(resource).setLastUpdatedTime(any(Date.class));
			verify(resource).setHiResourceImages(dbImages);
			verify(serviceDb).editHIResource(resource);
			verify(context).appendUpdate("parent/image.hiimg");
			verify(shareHandler).importResource(eq(resource), eq(request), eq(manifest));
		}
	}

	@Test
	public void ut_a2b_testImportResourceUpdateClearsOwnerWhenSourceHasNone()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HIResourceImagesImportHandler handler = new HIResourceImagesImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource fileResource = mock(HIResource.class);
		HIResourceImages importedImages = mock(HIResourceImages.class);
		HIResourceImages sidecarImages = mock(HIResourceImages.class);
		HIResourceImages dbImages = mock(HIResourceImages.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, shareHandler, manifestUtils, fileUtils);
		stubSidecarPath(context, manifestUtils, manifest, fileReader, fileUtils, fileResource, importedImages, sidecarImages);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.recover(resource)).thenReturn(true);
		when(context.getNewOldImageIds()).thenReturn(new HashMap<>());
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(false);
		when(options.getSchedules()).thenReturn(false);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileResource.getResourceId()).thenReturn(100);
		when(fileResource.getCreatedBy()).thenReturn(null);
		when(fileResource.getTitle()).thenReturn("Imported Title");
		when(fileResource.getResourcePath()).thenReturn("/imported/path");
		when(resource.getHiResourceImages()).thenReturn(dbImages);
		when(resource.getResourceURL()).thenReturn("parent/image.hiimg");

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity();
				MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
			Security security = ExportTestSecuritySupport.mockSecurityWithCreatedBy("1");
			securityMock.when(SecurityUtils::securityObject).thenReturn(security);

			handler.importResource("parent/image.hiimg");
			verify(resource).setCreatedBy(isNull());
			verify(resource).setTitle("Imported Title");
			verify(serviceDb).editHIResource(resource);
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
		HIResourceImages sidecarImages = mock(HIResourceImages.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		Manifest manifest = mock(Manifest.class);
		Map<Integer, String> imageIdMap = new HashMap<>();

		inject(handler, context, serviceDb, fileReader, shareHandler, manifestUtils, fileUtils);
		stubSidecarPath(context, manifestUtils, manifest, fileReader, fileUtils, fileResource, images, sidecarImages);

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
		when(fileResource.getResourceId()).thenReturn(100);
		when(fileResource.getCreatedBy()).thenReturn(3);
		when(fileResource.getTitle()).thenReturn("Banner Title");
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
		HIResourceImages sidecarImages = mock(HIResourceImages.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ResourceIOHandler scheduleHandler = mock(ResourceIOHandler.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, shareHandler, manifestUtils, fileUtils);
		stubSidecarPath(context, manifestUtils, manifest, fileReader, fileUtils, fileResource, images, sidecarImages);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getNewOldImageIds()).thenReturn(new HashMap<>());
		when(request.getOnConflict()).thenReturn("skip");
		when(request.getOptions()).thenReturn(options);
		when(options.getSchedules()).thenReturn(true);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(resource.getResourceURL()).thenReturn("parent/image.hiimg");

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity()) {
			appMock.when(() -> ApplicationContextAccessor.getBean("scheduleIOHandler")).thenReturn(scheduleHandler);
			HIResource result = handler.importResource("parent/image.hiimg");
			Assert.assertEquals(resource, result);
			verify(scheduleHandler).setContext(context);
			verify(scheduleHandler).importResource(resource, "parent/image.hiimg", "skip");
		}
	}

	@Test
	public void ut_a5_testImportResourceLegacyEmbedded()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HIResourceImagesImportHandler handler = new HIResourceImagesImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource fileResource = mock(HIResource.class);
		HIResourceImages embeddedImages = mock(HIResourceImages.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		Manifest manifest = mock(Manifest.class);
		byte[] largeContent = new byte[1024 * 64];

		inject(handler, context, serviceDb, fileReader, shareHandler, manifestUtils, fileUtils);

		when(context.removeDestination("parent/image.hiimg")).thenReturn("parent/image.hiimg");
		when(context.getManifest()).thenReturn(manifest);
		when(context.getResourcesDirectory()).thenReturn("/tmp/resources/");
		when(manifest.getVersion()).thenReturn("1");
		when(manifestUtils.getImageFile("parent/image.hiimg", manifest)).thenReturn(null);
		when(fileUtils.readFile(any(File.class), eq(HIResource.class))).thenReturn(fileResource);
		when(fileResource.getHiResourceImages()).thenReturn(embeddedImages);
		when(embeddedImages.getContent()).thenReturn(largeContent);

		when(context.getRequest()).thenReturn(request);
		when(context.getNewOldImageIds()).thenReturn(new HashMap<>());
		when(request.getOnConflict()).thenReturn("skip");
		when(request.getOptions()).thenReturn(options);
		when(options.getSchedules()).thenReturn(false);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(resource.getResourceURL()).thenReturn("parent/image.hiimg");

		HIResource result = handler.importResource("parent/image.hiimg");
		Assert.assertEquals(resource, result);
		ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
		verify(fileUtils).readFile(fileCaptor.capture(), eq(HIResource.class));
		Assert.assertTrue(fileCaptor.getValue().getPath().replace('\\', '/').endsWith("/tmp/resources/parent/image.hiimg"));
	}

	@Test(expected = ResourceImportException.class)
	public void ut_a6_testImportResourceSidecarReadFailure()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HIResourceImagesImportHandler handler = new HIResourceImagesImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource fileResource = mock(HIResource.class);
		HIResourceImages metadataImages = mock(HIResourceImages.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, shareHandler, manifestUtils, fileUtils);

		when(context.getRequest()).thenReturn(request);
		when(request.getOnConflict()).thenReturn("skip");
		when(context.removeDestination("parent/image.hiimg")).thenReturn("parent/image.hiimg");
		when(context.getManifest()).thenReturn(manifest);
		when(context.getResourcesDirectory()).thenReturn("/tmp/resources/");
		when(manifestUtils.getImageFile("parent/image.hiimg", manifest)).thenReturn("parent/image.hiimg_image");
		when(fileReader.read(context, "parent/image.hiimg", HIResource.class)).thenReturn(fileResource);
		when(fileResource.getHiResourceImages()).thenReturn(metadataImages);
		when(fileUtils.readFile(any(File.class), eq(HIResourceImages.class))).thenReturn(null);

		handler.importResource("parent/image.hiimg");
	}

	@Test(expected = ResourceImportException.class)
	public void ut_a7_testImportResourceLegacyReadFailure()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HIResourceImagesImportHandler handler = new HIResourceImagesImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, shareHandler, manifestUtils, fileUtils);

		when(context.getRequest()).thenReturn(request);
		when(request.getOnConflict()).thenReturn("skip");
		when(context.removeDestination("parent/image.hiimg")).thenReturn("parent/image.hiimg");
		when(context.getManifest()).thenReturn(manifest);
		when(context.getResourcesDirectory()).thenReturn("/tmp/resources/");
		when(manifest.getVersion()).thenReturn("1");
		when(manifestUtils.getImageFile("parent/image.hiimg", manifest)).thenReturn(null);
		when(fileUtils.readFile(any(File.class), eq(HIResource.class))).thenReturn(null);

		handler.importResource("parent/image.hiimg");
	}

	@Test
	public void ut_a8_testImportResourceSidecarCreatesMissingImages()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HIResourceImagesImportHandler handler = new HIResourceImagesImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource fileResource = mock(HIResource.class);
		HIResourceImages sidecarImages = mock(HIResourceImages.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		Manifest manifest = mock(Manifest.class);
		byte[] content = new byte[] { 1, 2, 3 };

		inject(handler, context, serviceDb, fileReader, shareHandler, manifestUtils, fileUtils);

		when(context.removeDestination("parent/image.hiimg")).thenReturn("parent/image.hiimg");
		when(context.getManifest()).thenReturn(manifest);
		when(context.getResourcesDirectory()).thenReturn("/tmp/resources/");
		when(manifestUtils.getImageFile("parent/image.hiimg", manifest)).thenReturn("parent/image.hiimg_image");
		when(fileReader.read(context, "parent/image.hiimg", HIResource.class)).thenReturn(fileResource);
		when(fileResource.getHiResourceImages()).thenReturn(null);
		when(fileUtils.readFile(any(File.class), eq(HIResourceImages.class))).thenReturn(sidecarImages);
		when(sidecarImages.getContentType()).thenReturn("image/png");
		when(sidecarImages.getContent()).thenReturn(content);

		when(context.getRequest()).thenReturn(request);
		when(context.getNewOldImageIds()).thenReturn(new HashMap<>());
		when(request.getOnConflict()).thenReturn("skip");
		when(request.getOptions()).thenReturn(options);
		when(options.getSchedules()).thenReturn(false);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(resource.getResourceURL()).thenReturn("parent/image.hiimg");

		HIResource result = handler.importResource("parent/image.hiimg");
		Assert.assertEquals(resource, result);

		ArgumentCaptor<HIResourceImages> imagesCaptor = ArgumentCaptor.forClass(HIResourceImages.class);
		verify(fileResource).setHiResourceImages(imagesCaptor.capture());
		HIResourceImages created = imagesCaptor.getValue();
		Assert.assertEquals("image/png", created.getContentType());
		Assert.assertArrayEquals(content, created.getContent());
	}

	@Test
	public void ut_a9_testImportResourceUpdateRecoverFalseSkips()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HIResourceImagesImportHandler handler = new HIResourceImagesImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource fileResource = mock(HIResource.class);
		HIResourceImages images = mock(HIResourceImages.class);
		HIResourceImages sidecarImages = mock(HIResourceImages.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, shareHandler, manifestUtils, fileUtils);
		stubSidecarPath(context, manifestUtils, manifest, fileReader, fileUtils, fileResource, images, sidecarImages);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.recover(resource)).thenReturn(false);
		when(context.getNewOldImageIds()).thenReturn(new HashMap<>());
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(options.getSchedules()).thenReturn(false);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(resource.getResourceURL()).thenReturn("parent/image.hiimg");

		HIResource result = handler.importResource("parent/image.hiimg");
		Assert.assertEquals(resource, result);
		verify(context).appendSkip("parent/image.hiimg");
		verify(serviceDb, never()).editHIResource(any(HIResource.class));
	}

	@Test
	public void ut_b1_testImportResourceCreateWithParent()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HIResourceImagesImportHandler handler = new HIResourceImagesImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource parent = mock(HIResource.class);
		HIResource fileResource = mock(HIResource.class);
		HIResourceImages images = mock(HIResourceImages.class);
		HIResourceImages sidecarImages = mock(HIResourceImages.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		Manifest manifest = mock(Manifest.class);
		Map<Integer, String> imageIdMap = new HashMap<>();
		Map<String, HIResource> urlMap = new HashMap<>();
		urlMap.put("parent.efwfolder", parent);

		inject(handler, context, serviceDb, fileReader, shareHandler, manifestUtils, fileUtils);
		stubSidecarPath(context, manifestUtils, manifest, fileReader, fileUtils, fileResource, images, sidecarImages);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getDate()).thenReturn(new Date());
		when(context.getResourceUrlMap()).thenReturn(urlMap);
		when(context.getNewOldImageIds()).thenReturn(imageIdMap);
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(false);
		when(options.getSchedules()).thenReturn(false);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(null);
		when(fileResource.getResourceId()).thenReturn(100);
		when(fileResource.getCreatedBy()).thenReturn(3);
		when(fileResource.getTitle()).thenReturn("Banner Title");
		when(parent.getResourceId()).thenReturn(50);
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
			Security security = ExportTestSecuritySupport.mockSecurityWithCreatedBy("7");
			securityMock.when(SecurityUtils::securityObject).thenReturn(security);

			HIResource result = handler.importResource("parent/image.hiimg");
			Assert.assertEquals(resource, result);
			Assert.assertEquals("300:parent/image.hiimg", imageIdMap.get(100));
			verify(resource).setParentId(50);
			verify(resource).setHiResourceImages(images);
			verify(serviceDb).addHIResource(resource);
			verify(context).appendInsert("parent/image.hiimg");
			resourceUtilsMock.verify(() -> ResourceUtils.newHIResource(eq("hiimg"), any(Date.class), eq(7),
					eq("parent/image.hiimg"), eq("image"), eq("Banner Title"), any(), eq(false)));
		}
	}

	@Test
	public void ut_b2_testImportResourceLegacyVersionZeroUsesFileName()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HIResourceImagesImportHandler handler = new HIResourceImagesImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource fileResource = mock(HIResource.class);
		HIResourceImages embeddedImages = mock(HIResourceImages.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, shareHandler, manifestUtils, fileUtils);

		when(context.removeDestination("parent/image.hiimg")).thenReturn("parent/image.hiimg");
		when(context.getManifest()).thenReturn(manifest);
		when(context.getResourcesDirectory()).thenReturn("/tmp/resources/");
		when(manifest.getVersion()).thenReturn("0");
		when(manifestUtils.getImageFile("parent/image.hiimg", manifest)).thenReturn(null);
		when(fileUtils.readFile(any(File.class), eq(HIResource.class))).thenReturn(fileResource);
		when(fileResource.getHiResourceImages()).thenReturn(embeddedImages);

		when(context.getRequest()).thenReturn(request);
		when(context.getNewOldImageIds()).thenReturn(new HashMap<>());
		when(request.getOnConflict()).thenReturn("skip");
		when(request.getOptions()).thenReturn(options);
		when(options.getSchedules()).thenReturn(false);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(resource.getResourceURL()).thenReturn("parent/image.hiimg");

		HIResource result = handler.importResource("parent/image.hiimg");
		Assert.assertEquals(resource, result);

		ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
		verify(fileUtils).readFile(fileCaptor.capture(), eq(HIResource.class));
		Assert.assertTrue(fileCaptor.getValue().getPath().replace('\\', '/').endsWith("/tmp/resources/image.hiimg"));
	}

	private void stubSidecarPath(ImportManagerContext context, ManifestUtils manifestUtils, Manifest manifest,
			ResourceDataReader fileReader, ResourceFileUtils fileUtils, HIResource fileResource,
			HIResourceImages metadataImages, HIResourceImages sidecarImages) {
		when(context.removeDestination("parent/image.hiimg")).thenReturn("parent/image.hiimg");
		when(context.getManifest()).thenReturn(manifest);
		when(context.getResourcesDirectory()).thenReturn("/tmp/resources/");
		when(manifestUtils.getImageFile("parent/image.hiimg", manifest)).thenReturn("parent/image.hiimg_image");
		when(fileReader.read(context, "parent/image.hiimg", HIResource.class)).thenReturn(fileResource);
		when(fileResource.getHiResourceImages()).thenReturn(metadataImages);
		when(fileUtils.readFile(any(File.class), eq(HIResourceImages.class))).thenReturn(sidecarImages);
	}

	private void inject(HIResourceImagesImportHandler handler, ImportManagerContext context,
			HIResourceServiceDB serviceDb, ResourceDataReader fileReader, ShareHandler shareHandler,
			ManifestUtils manifestUtils, ResourceFileUtils fileUtils)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		setField(handler, "context", context);
		setField(handler, "serviceDb", serviceDb);
		setField(handler, "fileReader", fileReader);
		setField(handler, "shareHandler", shareHandler);
		setField(handler, "manifestUtils", manifestUtils);
		setField(handler, "fileUtils", fileUtils);
	}

	protected void setField(Object target, String name, Object value)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = AbstractResourceImportHandler.class.getDeclaredField(name);
		field.setAccessible(true);
		field.set(target, value);
	}

}
