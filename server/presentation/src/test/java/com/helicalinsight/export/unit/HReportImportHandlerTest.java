package com.helicalinsight.export.unit;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceHReport;
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
import com.helicalinsight.export.handler.importres.HReportImportHandler;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HReportImportHandlerTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testImportResourceSkip()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HReportImportHandler handler = new HReportImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceHReport report = mock(HIResourceHReport.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ResourceOptions options = mock(ResourceOptions.class);
		inject(handler, context, serviceDb, fileReader, shareHandler);
		
		
		when(context.getRequest()).thenReturn(request);
		when(context.getRequest().getOptions()).thenReturn(options);
		when(request.getOnConflict()).thenReturn("skip");
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileReader.read(context, "parent/report.hr", HIResourceHReport.class)).thenReturn(report);
		when(resource.getResourceURL()).thenReturn("parent/report.hr");

		HIResource result = handler.importResource("parent/report.hr");
		Assert.assertEquals(resource, result);
	}

	@Test
	public void ut_a2_testImportResourceUpdate()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HReportImportHandler handler = new HReportImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource mdResource = mock(HIResource.class);
		HIResourceHReport report = mock(HIResourceHReport.class);
		HIResourceHReport dbReport = mock(HIResourceHReport.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, shareHandler);

		Map<String, List<String>> deps = new HashMap<>();
		deps.put("parent/report.hr", Arrays.asList("dep0", "md/url"));
		Map<String, HIResource> urlMap = new HashMap<>();
		urlMap.put("md/url", mdResource);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getDate()).thenReturn(new Date());
		when(context.recover(resource)).thenReturn(true);
		when(context.removeDestination("parent/report.hr")).thenReturn("parent/report.hr");
		when(context.addDestination("md/url")).thenReturn("md/url");
		when(context.getResourceUrlMap()).thenReturn(urlMap);
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(false);
		when(options.getSchedules()).thenReturn(false);
		when(manifest.getDependencies()).thenReturn(deps);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileReader.read(context, "parent/report.hr", HIResourceHReport.class)).thenReturn(report);
		when(resource.getHiResourceHReport()).thenReturn(dbReport);
		when(resource.getResourceURL()).thenReturn("parent/report.hr");
		when(report.getReportName()).thenReturn("reportName");
		when(report.getCreatedBy()).thenReturn(5);
		when(mdResource.getResourceId()).thenReturn(10);

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity();
				MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
			Security security = ExportTestSecuritySupport.mockSecurityWithCreatedBy("1");
			securityMock.when(SecurityUtils::securityObject).thenReturn(security);

			HIResource result = handler.importResource("parent/report.hr");
			Assert.assertEquals(resource, result);
		}
	}

	@Test
	public void ut_a3_testImportResourceCreateNew()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HReportImportHandler handler = new HReportImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource mdResource = mock(HIResource.class);
		HIResourceHReport report = mock(HIResourceHReport.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, shareHandler);

		Map<String, List<String>> deps = new HashMap<>();
		deps.put("parent/report.hr", Arrays.asList("dep0", "md/url"));
		Map<String, HIResource> urlMap = new HashMap<>();
		urlMap.put("md/url", mdResource);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getDate()).thenReturn(new Date());
		when(context.removeDestination("parent/report.hr")).thenReturn("parent/report.hr");
		when(context.addDestination("md/url")).thenReturn("md/url");
		when(context.getResourceUrlMap()).thenReturn(urlMap);
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(true);
		when(options.getSchedules()).thenReturn(false);
		when(manifest.getDependencies()).thenReturn(deps);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(null);
		when(fileReader.read(context, "parent/report.hr", HIResourceHReport.class)).thenReturn(report);
		when(report.getReportName()).thenReturn("reportName");
		when(report.getCreatedBy()).thenReturn(3);
		when(mdResource.getResourceId()).thenReturn(10);
		when(resource.getCreatedBy()).thenReturn(3);
		when(resource.getResourceURL()).thenReturn("parent/report.hr");

		try (MockedStatic<ResourceUtils> resourceUtilsMock = mockStatic(ResourceUtils.class);
				MockedStatic<JsonUtils> jsonUtilsMock = mockStatic(JsonUtils.class)) {
			jsonUtilsMock.when(JsonUtils::getHrReportExtension).thenReturn("hr");
			jsonUtilsMock.when(JsonUtils::getFolderFileExtension).thenReturn("efwfolder");
			resourceUtilsMock.when(() -> ResourceUtils.newHIResource(anyString(), any(), any(), anyString(),
					anyString(), anyString(), any(), anyBoolean())).thenReturn(resource);

			HIResource result = handler.importResource("parent/report.hr");
			Assert.assertEquals(resource, result);
		}
	}

	@Test
	public void ut_a4_testImportResourceWithSchedules()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HReportImportHandler handler = new HReportImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceHReport report = mock(HIResourceHReport.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ResourceIOHandler scheduleHandler = mock(ResourceIOHandler.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, shareHandler);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(request.getOnConflict()).thenReturn("skip");
		when(request.getOptions()).thenReturn(options);
		when(options.getSchedules()).thenReturn(true);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileReader.read(context, "parent/report.hr", HIResourceHReport.class)).thenReturn(report);
		when(resource.getResourceURL()).thenReturn("parent/report.hr");

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity()) {
			appMock.when(() -> ApplicationContextAccessor.getBean("scheduleIOHandler")).thenReturn(scheduleHandler);
			HIResource result = handler.importResource("parent/report.hr");
			Assert.assertEquals(resource, result);
		}
	}

	@Test
	public void ut_a5_testCreateNewReportNullCreatedBy()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HReportImportHandler handler = new HReportImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResource mdResource = mock(HIResource.class);
		HIResourceHReport report = mock(HIResourceHReport.class);
		Manifest manifest = mock(Manifest.class);

		setField(handler, "context", context);
		setField(handler, "serviceDb", serviceDb);

		Map<String, List<String>> deps = new HashMap<>();
		deps.put("parent/report.hr", Arrays.asList("dep0", "md/url"));
		Map<String, HIResource> urlMap = new HashMap<>();
		urlMap.put("md/url", mdResource);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getDate()).thenReturn(new Date());
		when(context.removeDestination("parent/report.hr")).thenReturn("parent/report.hr");
		when(context.addDestination("md/url")).thenReturn("md/url");
		when(context.getResourceUrlMap()).thenReturn(urlMap);
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(false);
		when(manifest.getDependencies()).thenReturn(deps);
		when(report.getReportName()).thenReturn("reportName");
		when(report.getCreatedBy()).thenReturn(null);
		when(mdResource.getResourceId()).thenReturn(10);
		when(resource.getCreatedBy()).thenReturn(null);

		try (MockedStatic<ResourceUtils> resourceUtilsMock = mockStatic(ResourceUtils.class);
				MockedStatic<JsonUtils> jsonUtilsMock = mockStatic(JsonUtils.class)) {
			jsonUtilsMock.when(JsonUtils::getHrReportExtension).thenReturn("hr");
			jsonUtilsMock.when(JsonUtils::getFolderFileExtension).thenReturn("efwfolder");
			resourceUtilsMock.when(() -> ResourceUtils.newHIResource(anyString(), any(), any(), anyString(),
					anyString(), anyString(), any(), anyBoolean())).thenReturn(resource);

			HIResource result = handler.createNewReport(report, "report.hr", "parent/", "parent/report.hr");
			Assert.assertEquals(resource, result);
		}
	}

	private void inject(HReportImportHandler handler, ImportManagerContext context, HIResourceServiceDB serviceDb,
			ResourceDataReader fileReader, ShareHandler shareHandler)
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
