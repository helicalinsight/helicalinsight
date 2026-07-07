//package com.helicalinsight.cache;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.mockStatic;
//import static org.mockito.Mockito.spy;
//import static org.mockito.Mockito.when;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import org.apache.commons.io.FileUtils;
//import org.junit.Test;
//import org.mockito.MockedStatic;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonNull;
//import com.google.gson.JsonObject;
//import com.helicalinsight.cache.management.ResourceEmptyHandler;
//import com.helicalinsight.cache.model.Cache;
//import com.helicalinsight.cache.model.CacheReport;
//import com.helicalinsight.cache.service.CacheReportService;
//import com.helicalinsight.cache.service.CacheService;
//import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
//import com.helicalinsight.efw.resourcecache.IResourceManager;
//import com.helicalinsight.efw.resourcecache.ResourceManager;
//
//
//public class ResourceEmptyHandlerTest {
//
//	@Test
//	public void test() {
//		ResourceEmptyHandler emptyHandler = new ResourceEmptyHandler();
//		emptyHandler.isThreadSafeToCache();
//	}
//
//	@Test
//	public void testExecuteComponent_a1() {
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			CacheReportService cacheReportService = mock(CacheReportService.class);
//			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheReportService.class))
//					.thenReturn(cacheReportService);
//			ResourceEmptyHandler emptyHandler = new ResourceEmptyHandler();
//			JsonObject jsonObject = new JsonObject();
//			JsonArray array = new JsonArray();
//			jsonObject.add("dir", array);
//			emptyHandler.executeComponent(jsonObject.toString());
//
//		}
//	}
//
//	@Test
//	public void testExecuteComponent_a2() {
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			CacheReportService cacheReportService = mock(CacheReportService.class);
//			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheReportService.class))
//					.thenReturn(cacheReportService);
//			try (MockedStatic<ResourceManager> resourceManager = mockStatic(ResourceManager.class)) {
//				IResourceManager iResource = mock(IResourceManager.class);
//				resourceManager.when(() -> ResourceManager.getInstance()).thenReturn(iResource);
//				ResourceEmptyHandler emptyHandler = new ResourceEmptyHandler();
//				JsonObject jsonObject = new JsonObject();
//
//				jsonObject.add("dir", JsonNull.INSTANCE);
//				emptyHandler.executeComponent(jsonObject.toString());
//
//			}
//		}
//	}
//
//	@Test
//	public void testExecuteComponent_a3() {
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			CacheReportService cacheReportService = mock(CacheReportService.class);
//			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheReportService.class))
//					.thenReturn(cacheReportService);
//
//			JsonObject jsonObject = new JsonObject();
//			JsonArray array = new JsonArray();
//			array.add("/");
//			jsonObject.add("dir", array);
//			ResourceEmptyHandler emptyHandler = spy(ResourceEmptyHandler.class);
//			doNothing().when(emptyHandler).deleteAllCaches();
//			emptyHandler.executeComponent(jsonObject.toString());
//
//		}
//	}
//
//	@Test
//	public void testExecuteComponent_a4() {
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			CacheReportService cacheReportService = mock(CacheReportService.class);
//			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheReportService.class))
//					.thenReturn(cacheReportService);
//
//			JsonObject jsonObject = new JsonObject();
//			JsonArray array = new JsonArray();
//			array.add("sampleString");
//			jsonObject.add("dir", array);
//			CacheReport report1 = new CacheReport();
//			when(cacheReportService.getReports(anyString())).thenReturn(Arrays.asList(report1));
//			ResourceEmptyHandler emptyHandler = spy(ResourceEmptyHandler.class);
//			doNothing().when(emptyHandler).processDelete(any());
//			emptyHandler.executeComponent(jsonObject.toString());
//
//		}
//	}
//
////	@Test
//	public void testProcessDelete_a1() throws IOException {
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			CacheService cacheService = mock(CacheService.class);
//			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheService.class)).thenReturn(cacheService);
//			try (MockedStatic<CacheUtils> cacheUtils = mockStatic(CacheUtils.class)) {
//				cacheUtils.when(() -> CacheUtils.getCacheDirectory()).thenReturn("D:");
//				cacheUtils.when(() -> CacheUtils.getCacheExtension()).thenReturn(".cache");
//
//				List<CacheReport> reportList = new ArrayList<>();
//				CacheReport report1 = new CacheReport();
//				report1.setCacheId(123l);
//				reportList.add(report1);
//
//				Cache cache = mock(Cache.class);
//				when(cacheService.findCache(anyLong())).thenReturn(cache);
//				when(cache.getCacheFilePath()).thenReturn("Test.txt");
//
//				String filePath = "D:/Test.txt";
//				File file = new File(filePath);
//				file.createNewFile();
//
//				ResourceEmptyHandler emptyHandler = new ResourceEmptyHandler();
//				emptyHandler.processDelete(reportList);
//			}
//		}
//
//	}
//
////	@Test
//	public void testProcessDelete_a2() throws IOException {
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			CacheService cacheService = mock(CacheService.class);
//			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheService.class)).thenReturn(cacheService);
//			try (MockedStatic<CacheUtils> cacheUtils = mockStatic(CacheUtils.class)) {
//				cacheUtils.when(() -> CacheUtils.getCacheDirectory()).thenReturn("D:");
//				cacheUtils.when(() -> CacheUtils.getCacheExtension()).thenReturn(".cache");
//
//				List<CacheReport> reportList = new ArrayList<>();
//				CacheReport report1 = new CacheReport();
//				report1.setCacheId(123l);
//				reportList.add(report1);
//
//				Cache cache = mock(Cache.class);
//				when(cacheService.findCache(anyLong())).thenReturn(cache);
//				when(cache.getCacheFilePath()).thenReturn("Test.txt");
//				when(cache.getNoOfRecords()).thenReturn(2);
//
//				String filePath = "D:/Test.txt";
//				File file = new File(filePath);
//				file.createNewFile();
//
//				ResourceEmptyHandler emptyHandler = new ResourceEmptyHandler();
//				emptyHandler.processDelete(reportList);
//			}
//		}
//
//	}
//
////	@Test
//	public void testProcessDelete_a3() {
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			CacheService cacheService = mock(CacheService.class);
//			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheService.class)).thenReturn(cacheService);
//			try (MockedStatic<CacheUtils> cacheUtils = mockStatic(CacheUtils.class)) {
//				cacheUtils.when(() -> CacheUtils.getCacheDirectory()).thenReturn("D:");
//				cacheUtils.when(() -> CacheUtils.getCacheExtension()).thenReturn(".cache");
//
//				List<CacheReport> reportList = new ArrayList<>();
//				CacheReport report1 = new CacheReport();
//				report1.setCacheId(123l);
//				reportList.add(report1);
//
//				Cache cache = mock(Cache.class);
//				when(cacheService.findCache(anyLong())).thenReturn(cache);
//				when(cache.getCacheFilePath()).thenReturn("Test.txt");
//				when(cache.getNoOfRecords()).thenReturn(1);
//
//				ResourceEmptyHandler emptyHandler = new ResourceEmptyHandler();
//				emptyHandler.processDelete(reportList);
//			}
//		}
//
//	}
//
////	@Test
//	public void testProcessDelete_a4() {
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			CacheService cacheService = mock(CacheService.class);
//			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheService.class)).thenReturn(cacheService);
//			try (MockedStatic<CacheUtils> cacheUtils = mockStatic(CacheUtils.class)) {
//				cacheUtils.when(() -> CacheUtils.getCacheDirectory()).thenReturn("D:");
//				cacheUtils.when(() -> CacheUtils.getCacheExtension()).thenReturn(".cache");
//
//				List<CacheReport> reportList = new ArrayList<>();
//				CacheReport report1 = new CacheReport();
//				report1.setCacheId(123l);
//				reportList.add(report1);
//
//				Cache cache = mock(Cache.class);
//				when(cacheService.findCache(anyLong())).thenReturn(cache);
//				when(cache.getCacheFilePath()).thenReturn("Test.txt");
//				when(cache.getNoOfRecords()).thenReturn(null);
//
//				ResourceEmptyHandler emptyHandler = new ResourceEmptyHandler();
//				emptyHandler.processDelete(reportList);
//			}
//		}
//
//	}
//
//	@Test
//	public void testProcessDelete_a5() {
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			CacheService cacheService = mock(CacheService.class);
//			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheService.class)).thenReturn(cacheService);
//			try (MockedStatic<CacheUtils> cacheUtils = mockStatic(CacheUtils.class)) {
//				cacheUtils.when(() -> CacheUtils.getCacheDirectory()).thenReturn("D:");
//				cacheUtils.when(() -> CacheUtils.getCacheExtension()).thenReturn(".cache");
//
//				List<CacheReport> reportList = new ArrayList<>();
//				CacheReport report1 = new CacheReport();
//				report1.setCacheId(123l);
//				reportList.add(report1);
//
//				Cache cache = mock(Cache.class);
//				when(cacheService.findCache(anyLong())).thenReturn(null);
//
//				ResourceEmptyHandler emptyHandler = new ResourceEmptyHandler();
//				emptyHandler.processDelete(reportList);
//			}
//		}
//
//	}
//	
//	//@Test
//	public void testDeleteAllCaches_a1() {
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			CacheService cacheService = mock(CacheService.class);
//			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheService.class)).thenReturn(cacheService);
//
//			CacheReportService cacheReportService = mock(CacheReportService.class);
//			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheReportService.class))
//					.thenReturn(cacheReportService);
//			ResourceEmptyHandler emptyHandler = new ResourceEmptyHandler();
//
//			JsonObject responseJson = new JsonObject();
//			emptyHandler.executeComponent(responseJson.toString());
//
//			doNothing().when(cacheReportService).deleteAllCacheReport();
//			doNothing().when(cacheService).deleteAllCache();
//
//			emptyHandler.deleteAllCaches();
//
//		}
//	}
//	
//	@Test
//	public void testDeleteAllCaches_a2() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			CacheService cacheService = mock(CacheService.class);
//			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheService.class)).thenReturn(cacheService);
//
//			CacheReportService cacheReportService = mock(CacheReportService.class);
//			mockedStatic.when(() -> ApplicationContextAccessor.getBean(CacheReportService.class))
//					.thenReturn(cacheReportService);
//			ResourceEmptyHandler emptyHandler = new ResourceEmptyHandler();
//
//			JsonObject responseJson = new JsonObject();
//			emptyHandler.executeComponent(responseJson.toString());
//			
//			doNothing().when(cacheReportService).deleteAllCacheReport();
//			doNothing().when(cacheService).deleteAllCache();
//			try (MockedStatic<FileUtils> fileUtils = mockStatic(FileUtils.class)) {
//				fileUtils.when(() ->  FileUtils.cleanDirectory(any())).thenThrow(new IOException("exception"));
//			emptyHandler.deleteAllCaches();
//			}
//
//		}
//	}
//
//}
