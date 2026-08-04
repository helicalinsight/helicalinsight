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
import com.helicalinsight.admin.model.HIResourceHCR;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.service.HIResourceConstituentMappingService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.ResourceDataReader;
import com.helicalinsight.export.handler.importres.AbstractResourceImportHandler;
import com.helicalinsight.export.handler.importres.HCRImportHandler;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HCRImportHandlerTest extends ExportUnitTestBase {

	private static final String PREVIEW_JSON = "{\"connectionDbDetails\":[]}";
	private static final String STATE_JSON = "{}";

	@Test
	public void ut_a1_testImportResourceSkip()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HCRImportHandler handler = new HCRImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceHCR report = mock(HIResourceHCR.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);

		injectBase(handler, context, serviceDb, fileReader);

		when(context.getRequest()).thenReturn(request);
		when(context.getNewOldImageIds()).thenReturn(new HashMap<>());
		when(request.getOnConflict()).thenReturn("skip");
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileReader.read(context, "parent/report.hcr", HIResourceHCR.class)).thenReturn(report);
		when(report.getPreviewFormData()).thenReturn(PREVIEW_JSON);
		when(report.getDiagram()).thenReturn("");
		when(resource.getResourceURL()).thenReturn("parent/report.hcr");

		HIResource result = handler.importResource("parent/report.hcr");
		Assert.assertEquals(resource, result);
	}

	
	@Test
	public void ut_a2_testImportResourceCreateNew()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HCRImportHandler handler = new HCRImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceHCR report = mock(HIResourceHCR.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		Manifest manifest = mock(Manifest.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		EFWDConnectionService efwdConnectionService = mock(EFWDConnectionService.class);
		HIResourceConstituentMappingService mappingService = mock(HIResourceConstituentMappingService.class);
		ResourceTypeServiceDB resourceTypeService = mock(ResourceTypeServiceDB.class);
		ResourceType resourceType = mock(ResourceType.class);

		injectFull(handler, context, serviceDb, fileReader, shareHandler, manifestUtils);
		setField(handler, "efwdConnectionService", efwdConnectionService);
		setField(handler, "mappingService", mappingService);
		setField(handler, "resourceTypeService", resourceTypeService);

		Map<String, HIResource> urlMap = new HashMap<>();

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getDate()).thenReturn(new Date());
		when(context.getNewOldImageIds()).thenReturn(new HashMap<>());
		when(context.removeDestination(anyString())).thenReturn("parent/report.hcr");
		when(context.getResourceUrlMap()).thenReturn(urlMap);
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(true);
		when(options.getSchedules()).thenReturn(false);
		when(manifestUtils.compareOptions(options, manifest, "datasource")).thenReturn(false);
		when(manifestUtils.getDatasource(anyString(), any())).thenReturn(null);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(null);
		when(fileReader.read(context, "parent/report.hcr", HIResourceHCR.class)).thenReturn(report);
		when(report.getPreviewFormData()).thenReturn(PREVIEW_JSON);
		when(report.getDiagram()).thenReturn("");
		when(report.getFileName()).thenReturn("reportName");
		when(report.getState()).thenReturn(STATE_JSON);
		when(report.getCreatedBy()).thenReturn(3);
		when(resource.getResourceId()).thenReturn(1);
		when(resource.getResourceURL()).thenReturn("parent/report.hcr");
		when(efwdConnectionService.fetchAllHcrConnectionsByResourceId(anyInt())).thenReturn(Collections.emptyList());
		when(resourceTypeService.getResourceTypeByTypeAndExtension(anyString(), anyString())).thenReturn(resourceType);
		when(resourceType.getResourceTypeId()).thenReturn(1L);

		try (MockedStatic<ResourceUtils> resourceUtilsMock = mockStatic(ResourceUtils.class);
				MockedStatic<JsonUtils> jsonUtilsMock = mockStatic(JsonUtils.class)) {
			jsonUtilsMock.when(JsonUtils::getHCRExtension).thenReturn("hcr");
			jsonUtilsMock.when(JsonUtils::getFolderFileExtension).thenReturn("efwfolder");
			jsonUtilsMock.when(JsonUtils::getImageExtension).thenReturn("hiimg");
			resourceUtilsMock.when(() -> ResourceUtils.newHIResource(anyString(), any(), any(), anyString(),
					anyString(), anyString(), any(), anyBoolean())).thenReturn(resource);

			HIResource result = handler.importResource("parent/report.hcr");
			Assert.assertEquals(resource, result);
		}
	}

	@Test
	public void ut_a3_testCreateNewReport()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HCRImportHandler handler = new HCRImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceHCR report = mock(HIResourceHCR.class);

		setField(handler, "context", context);
		setField(handler, "serviceDb", serviceDb);

		when(context.getRequest()).thenReturn(request);
		when(context.getDate()).thenReturn(new Date());
		when(context.getResourceUrlMap()).thenReturn(new HashMap<>());
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(false);
		when(report.getFileName()).thenReturn("reportName");
		when(report.getCreatedBy()).thenReturn(null);
		when(resource.getCreatedBy()).thenReturn(null);

		try (MockedStatic<ResourceUtils> resourceUtilsMock = mockStatic(ResourceUtils.class);
				MockedStatic<JsonUtils> jsonUtilsMock = mockStatic(JsonUtils.class)) {
			jsonUtilsMock.when(JsonUtils::getHCRExtension).thenReturn("hcr");
			jsonUtilsMock.when(JsonUtils::getFolderFileExtension).thenReturn("efwfolder");
			resourceUtilsMock.when(() -> ResourceUtils.newHIResource(anyString(), any(), any(), anyString(),
					anyString(), anyString(), any(), anyBoolean())).thenReturn(resource);

			HIResource result = handler.createNewReport(report, "report.hcr", "parent/", "parent/report.hcr");
			Assert.assertEquals(resource, result);
		}
	}

	private void injectBase(HCRImportHandler handler, ImportManagerContext context, HIResourceServiceDB serviceDb,
			ResourceDataReader fileReader)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		setField(handler, "context", context);
		setField(handler, "serviceDb", serviceDb);
		setField(handler, "fileReader", fileReader);
	}

	private void injectFull(HCRImportHandler handler, ImportManagerContext context, HIResourceServiceDB serviceDb,
			ResourceDataReader fileReader, ShareHandler shareHandler, ManifestUtils manifestUtils)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		injectBase(handler, context, serviceDb, fileReader);
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
