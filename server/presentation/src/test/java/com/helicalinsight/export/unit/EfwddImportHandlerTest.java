package com.helicalinsight.export.unit;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceEFWDD;
import com.helicalinsight.admin.model.HIResourceMapping;
import com.helicalinsight.admin.service.HIResourceConstituentMappingService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.utility.ApplicationSettings;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.ResourceDataReader;
import com.helicalinsight.export.handler.importres.AbstractResourceImportHandler;
import com.helicalinsight.export.handler.importres.EfwddImportHandler;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EfwddImportHandlerTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testImportResourceSkip()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwddImportHandler handler = new EfwddImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceEFWDD efwdd = mock(HIResourceEFWDD.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ResourceIOHandler scheduleHandler = mock(ResourceIOHandler.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, shareHandler);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(request.getOnConflict()).thenReturn("skip");
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileReader.read(context, "parent/dash/file.efwdd", HIResourceEFWDD.class)).thenReturn(efwdd);
		when(resource.getResourceURL()).thenReturn("parent/dash/file.efwdd");

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity()) {
			appMock.when(() -> ApplicationContextAccessor.getBean("scheduleIOHandler")).thenReturn(scheduleHandler);
			HIResource result = handler.importResource("parent/dash/file.efwdd");
			Assert.assertEquals(resource, result);
		}
	}

	@Test
	public void ut_a2_testImportResourceUpdate()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwddImportHandler handler = new EfwddImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceEFWDD efwdd = mock(HIResourceEFWDD.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		HIResourceConstituentMappingService pathService = mock(HIResourceConstituentMappingService.class);
		ResourceIOHandler scheduleHandler = mock(ResourceIOHandler.class);
		Manifest manifest = mock(Manifest.class);
		ApplicationSettings applicationSettings = mock(ApplicationSettings.class);

		inject(handler, context, serviceDb, fileReader, shareHandler);
		setField(handler, "pathService", pathService);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getDate()).thenReturn(new Date());
		when(context.recover(resource)).thenReturn(true);
		when(context.destinationExists()).thenReturn(false);
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(false);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileReader.read(context, "parent/dash/file.efwdd", HIResourceEFWDD.class)).thenReturn(efwdd);
		when(resource.getResourceId()).thenReturn(1);
		when(resource.getResourceURL()).thenReturn("parent/dash/file.efwdd");
		when(efwdd.getState()).thenReturn("{}");
		when(efwdd.getFileName()).thenReturn("dashTitle");
		when(efwdd.getCreatedBy()).thenReturn(5);

		JsonObject settingsJson = new JsonObject();
		settingsJson.addProperty("autoSyncCutPasteDesigner", false);
		when(applicationSettings.getSettingJson()).thenReturn(settingsJson);

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity();
				MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
			appMock.when(() -> ApplicationContextAccessor.getBean("scheduleIOHandler")).thenReturn(scheduleHandler);
			appMock.when(() -> ApplicationContextAccessor.getBean(ApplicationSettings.class))
					.thenReturn(applicationSettings);
			Security security = ExportTestSecuritySupport.mockSecurityWithCreatedBy("1");
			securityMock.when(SecurityUtils::securityObject).thenReturn(security);

			HIResource result = handler.importResource("parent/dash/file.efwdd");
			Assert.assertEquals(resource, result);
		}
	}

	@Test
	public void ut_a3_testImportResourceCreateNew()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwddImportHandler handler = new EfwddImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource parent = mock(HIResource.class);
		HIResourceEFWDD efwdd = mock(HIResourceEFWDD.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		HIResourceConstituentMappingService pathService = mock(HIResourceConstituentMappingService.class);
		ResourceIOHandler scheduleHandler = mock(ResourceIOHandler.class);
		Manifest manifest = mock(Manifest.class);
		ApplicationSettings applicationSettings = mock(ApplicationSettings.class);

		inject(handler, context, serviceDb, fileReader, shareHandler);
		setField(handler, "pathService", pathService);

		Map<String, HIResource> urlMap = new HashMap<>();
		urlMap.put("parent/dash.efwfolder", parent);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getDate()).thenReturn(new Date());
		when(context.destinationExists()).thenReturn(false);
		when(context.getResourceUrlMap()).thenReturn(urlMap);
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(true);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(null);
		when(fileReader.read(context, "parent/dash/file.efwdd", HIResourceEFWDD.class)).thenReturn(efwdd);
		when(efwdd.getState()).thenReturn("{}");
		when(efwdd.getFileName()).thenReturn("dashTitle");
		when(efwdd.getCreatedBy()).thenReturn(3);
		when(parent.getResourceId()).thenReturn(2);
		when(resource.getResourceId()).thenReturn(1);
		when(resource.getCreatedBy()).thenReturn(3);
		when(resource.getResourceURL()).thenReturn("parent/dash/file.efwdd");

		JsonObject settingsJson = new JsonObject();
		settingsJson.addProperty("autoSyncCutPasteDesigner", false);
		when(applicationSettings.getSettingJson()).thenReturn(settingsJson);

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity();
				MockedStatic<ResourceUtils> resourceUtilsMock = mockStatic(ResourceUtils.class);
				MockedStatic<JsonUtils> jsonUtilsMock = mockStatic(JsonUtils.class)) {
			appMock.when(() -> ApplicationContextAccessor.getBean("scheduleIOHandler")).thenReturn(scheduleHandler);
			appMock.when(() -> ApplicationContextAccessor.getBean(ApplicationSettings.class))
					.thenReturn(applicationSettings);
			jsonUtilsMock.when(JsonUtils::getDesignerExtension).thenReturn("efwdd");
			jsonUtilsMock.when(JsonUtils::getFolderFileExtension).thenReturn("efwfolder");
			resourceUtilsMock.when(() -> ResourceUtils.newHIResource(anyString(), any(), any(), anyString(),
					anyString(), anyString(), any(), anyBoolean())).thenReturn(resource);

			HIResource result = handler.importResource("parent/dash/file.efwdd");
			Assert.assertEquals(resource, result);
		}
	}

	@Test
	public void ut_a4_testUpdateEfwddResourceNullCreatedBy()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwddImportHandler handler = new EfwddImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceEFWDD efwdd = mock(HIResourceEFWDD.class);
		HIResourceConstituentMappingService pathService = mock(HIResourceConstituentMappingService.class);
		ApplicationSettings applicationSettings = mock(ApplicationSettings.class);

		setField(handler, "context", context);
		setField(handler, "serviceDb", serviceDb);
		setField(handler, "pathService", pathService);

		when(context.getRequest()).thenReturn(request);
		when(context.getDate()).thenReturn(new Date());
		when(context.destinationExists()).thenReturn(false);
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(false);
		when(resource.getResourceId()).thenReturn(1);
		when(efwdd.getState()).thenReturn("{}");
		when(efwdd.getFileName()).thenReturn("title");
		when(efwdd.getCreatedBy()).thenReturn(null);

		JsonObject settingsJson = new JsonObject();
		settingsJson.addProperty("autoSyncCutPasteDesigner", false);
		when(applicationSettings.getSettingJson()).thenReturn(settingsJson);

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity();
				MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
			appMock.when(() -> ApplicationContextAccessor.getBean(ApplicationSettings.class))
					.thenReturn(applicationSettings);
			Security security = ExportTestSecuritySupport.mockSecurityWithCreatedBy("1");
			securityMock.when(SecurityUtils::securityObject).thenReturn(security);
			handler.updateEfwddResource(efwdd, resource);
		}
	}

	@Test
	public void ut_a5_testUpdateReportPathsWithMappings()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwddImportHandler handler = new EfwddImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource child = mock(HIResource.class);
		HIResourceEFWDD efwdd = mock(HIResourceEFWDD.class);
		HIResourceConstituentMappingService pathService = mock(HIResourceConstituentMappingService.class);
		ApplicationSettings applicationSettings = mock(ApplicationSettings.class);

		setField(handler, "context", context);
		setField(handler, "serviceDb", serviceDb);
		setField(handler, "pathService", pathService);

		when(context.getRequest()).thenReturn(request);
		when(context.destinationExists()).thenReturn(false);
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(false);
		when(resource.getResourceId()).thenReturn(1);
		when(serviceDb.getHIResourceById(10)).thenReturn(child);
		when(efwdd.getState()).thenReturn("{}");

		List<Integer> idList = new ArrayList<>();
		idList.add(10);
		Map<String, Object> stateMap = new HashMap<>();
		stateMap.put("state", "{}");
		stateMap.put("idList", idList);

		JsonObject settingsJson = new JsonObject();
		settingsJson.addProperty("autoSyncCutPasteDesigner", true);
		when(applicationSettings.getSettingJson()).thenReturn(settingsJson);

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity();
				MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
			appMock.when(() -> ApplicationContextAccessor.getBean(ApplicationSettings.class))
					.thenReturn(applicationSettings);
			Security security = ExportTestSecuritySupport.mockSecurityWithCreatedBy("1");
			securityMock.when(SecurityUtils::securityObject).thenReturn(security);
			handler.updateEfwddResource(efwdd, resource);
		}
	}

	private void inject(EfwddImportHandler handler, ImportManagerContext context, HIResourceServiceDB serviceDb,
			ResourceDataReader fileReader, ShareHandler shareHandler)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		setField(handler, "context", context);
		setField(handler, "serviceDb", serviceDb);
		setField(handler, "fileReader", fileReader);
		setField(handler, "shareHandler", shareHandler);
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
