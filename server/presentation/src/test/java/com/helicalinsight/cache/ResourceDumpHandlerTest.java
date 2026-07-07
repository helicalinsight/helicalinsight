package com.helicalinsight.cache;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.cache.management.ResourceDumpHandler;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;
import com.helicalinsight.cache.service.CacheReportService;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

public class ResourceDumpHandlerTest {

	@Test
	public void testisThreadSafeToCache() {
		ResourceDumpHandler dumpHandler = new ResourceDumpHandler();
		dumpHandler.isThreadSafeToCache();
	}

	@Test
	public void testprocessReport() {
		ResourceDumpHandler dumpHandler = new ResourceDumpHandler();
		JsonObject responseJson = new JsonObject();
		responseJson.addProperty("responsejson", "responseJson");
		List<String> reportList = new ArrayList<>();
		reportList.add("record");
		reportList.add("json record");
		dumpHandler.processReport(responseJson, reportList);
	}

	@Test
	public void testExecuteComponent_a1() {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			CacheReportService cacheReportService = mock(CacheReportService.class);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheReportService.class))
					.thenReturn(cacheReportService);
			
			ResourceDumpHandler dumpHandler = new ResourceDumpHandler();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("dir", "");
			jsonObject.addProperty("action", "list");
			dumpHandler.executeComponent(jsonObject.toString());
		}
	}
	@Test
	public void testExecuteComponent_a2() {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			CacheReportService cacheReportService = mock(CacheReportService.class);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheReportService.class))
					.thenReturn(cacheReportService);
			
			ResourceDumpHandler dumpHandler = new ResourceDumpHandler();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("dir", "AB");
			jsonObject.addProperty("action", "action");
			dumpHandler.executeComponent(jsonObject.toString());
		}
	}
	@Test
	public void testExecuteComponent_a3() {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			CacheReportService cacheReportService = mock(CacheReportService.class);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheReportService.class))
					.thenReturn(cacheReportService);
			
			ResourceDumpHandler dumpHandler = new ResourceDumpHandler();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("dir", "/");
			jsonObject.addProperty("action", "action");
			dumpHandler.executeComponent(jsonObject.toString());
		}
	}
	@Test
	public void testExecuteComponent_a4() {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			CacheReportService cacheReportService = mock(CacheReportService.class);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheReportService.class))
					.thenReturn(cacheReportService);
			
			ResourceDumpHandler dumpHandler = new ResourceDumpHandler();
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("dir", null);
			jsonObject.addProperty("action", "");
			dumpHandler.executeComponent(jsonObject.toString());
		}
	}
	@Test
	public void testGetReportCacheInformation_a1() {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			CacheReportService cacheReportService = mock(CacheReportService.class);
			CacheService cacheService = mock(CacheService.class);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheService.class))
					.thenReturn(cacheService);
			CacheReport report = mock(CacheReport.class);
			Cache cache = mock(Cache.class);
			when(report.getCacheId()).thenReturn(12l);
			when(cacheService.findCache(12l)).thenReturn(cache);
			when(cacheReportService.getReports(anyString())).thenReturn(Arrays.asList(report));
			when(cache.getCacheExpiryTime()).thenReturn(new Date());
			when(cache.getCacheFileTimeStamp()).thenReturn(new Date());
			when(cache.getCacheFileSize()).thenReturn(23l);
			
			ResourceDumpHandler dumpHandler = new ResourceDumpHandler();
			dumpHandler.getReportCacheInformation(anyString(), cacheReportService);
		}
			
	}
	@Test
	public void testGetReportCacheInformation_a2() {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			CacheReportService cacheReportService = mock(CacheReportService.class);
			CacheService cacheService = mock(CacheService.class);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheService.class))
					.thenReturn(cacheService);
			CacheReport report = mock(CacheReport.class);
			when(report.getCacheId()).thenReturn(12l);
			when(cacheService.findCache(12l)).thenReturn(null);
			when(cacheReportService.getReports(anyString())).thenReturn(Arrays.asList(report));
			
			ResourceDumpHandler dumpHandler = new ResourceDumpHandler();
			dumpHandler.getReportCacheInformation(anyString(), cacheReportService);
		}
			
	}
}
