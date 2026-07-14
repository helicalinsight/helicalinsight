// package com.helicalinsight.cache;

// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.mockStatic;
// import static org.mockito.Mockito.when;

// import java.io.File;
// import java.io.IOException;
// import java.lang.reflect.Field;
// import java.util.Date;
// import org.junit.Test;
// import org.mockito.MockedStatic;

// import com.google.gson.JsonArray;
// import com.google.gson.JsonObject;
// import com.helicalinsight.cache.manager.CacheManager;
// import com.helicalinsight.cache.model.Cache;
// import com.helicalinsight.cache.service.CacheService;
// import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

// public class CacheUtilsTest {

	
// 	@Test
// 	public void testisCacheEnabled() {
// 		CacheUtils cacheUtils = new CacheUtils();
// 		CacheUtils.isCacheEnabled();
// 	}


// 	@Test
// 	public void testgetCacheDirectory() {
// 		CacheUtils cacheUtils = new CacheUtils();
// 		CacheUtils.getCacheDirectory();
// 	}
// 	@Test
// 	public void testinit() {
// 		CacheUtils cacheUtils = new CacheUtils();
// 		CacheUtils.init();
// 	}

// 	@Test
// 	public void testgetCacheXmlJson() {
// 		CacheUtils cacheUtils = new CacheUtils();
// 		CacheUtils.getCacheXmlJson();
// 	}
	
	
// 	//----------------------------
// 	//@Test
// 	public void testGetRefreshUrl() throws NoSuchFieldException, SecurityException,
// 								IllegalArgumentException, IllegalAccessException {
// 		JsonObject object = new JsonObject();
// 		JsonArray refreshUrl = new JsonArray();
// 		object.addProperty("cacheExtension", ".cache");
// 		object.addProperty("enableThread", "true");
// 		object.add("refreshUrl", refreshUrl);
// 		Field field = CacheUtils.class.getDeclaredField("cacheXmlJson");
// 		field.setAccessible(true);
// 		CacheUtils cacheUtils = new CacheUtils();
// 		field.set(cacheUtils, object);
// 		CacheUtils.getRefreshUrl();
// 		CacheUtils.getCacheExtension();
// 		CacheUtils.isThreadingEnabled();
// 	}
	
// 	@Test
// 	public void testGetCacheManager_a1() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
// 		try(MockedStatic<ApplicationContextAccessor> mockedstatic = mockStatic(ApplicationContextAccessor.class)){
// 			CacheManager cacheMock = mock(CacheManager.class);
// 			mockedstatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(cacheMock);
// 		JsonObject object = new JsonObject();
// 		JsonObject cacheManager = new JsonObject();
// 		JsonArray url = new JsonArray();
// 		JsonObject urlJson = new JsonObject();
// 		urlJson.addProperty("pattern", "url");
// 		urlJson.addProperty("bean", "bean");
// 		url.add(urlJson);
// 		cacheManager.add("url", url);
// 		object.add("cacheManager", cacheManager);
// 		Field field = CacheUtils.class.getDeclaredField("cacheXmlJson");
// 		field.setAccessible(true);
// 		CacheUtils cacheUtils = new CacheUtils();
// 		field.set(cacheUtils, object);
// 		CacheUtils.getCacheManager("url");
// 		}
// 	}
// 	@Test
// 	public void testGetCacheManager_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
// 		JsonObject object = new JsonObject();
// 		JsonObject cacheManager = new JsonObject();
// 		JsonArray url = new JsonArray();
// 		JsonObject urlJson = new JsonObject();
// 		urlJson.addProperty("pattern", "urlUrl");
// 		urlJson.addProperty("bean", "bean");
// 		url.add(urlJson);
// 		cacheManager.add("url", url);
// 		object.add("cacheManager", cacheManager);
// 		Field field = CacheUtils.class.getDeclaredField("cacheXmlJson");
// 		field.setAccessible(true);
// 		CacheUtils cacheUtils = new CacheUtils();
// 		field.set(cacheUtils, object);
// 		CacheUtils.getCacheManager("url");
// 	}
// 	@Test
// 	public void testGetCacheManager_a3() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
// 		JsonObject object = null;
// 		Field field = CacheUtils.class.getDeclaredField("cacheXmlJson");
// 		field.setAccessible(true);
// 		CacheUtils cacheUtils = new CacheUtils();
// 		field.set(cacheUtils, object);
// 		CacheUtils.getCacheManager("url");
// 	}
    
// 	@Test
// 	public void testgetActualCacheExpireDuration_a1() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
// 		CacheUtils cacheUtils = new CacheUtils();
// 		Field field = CacheUtils.class.getDeclaredField("durationUnit");
// 		field.setAccessible(true);
// 		field.set(cacheUtils, "minutes");
// 		CacheUtils.getActualCacheExpireDuration();
// 	}
// 	@Test
// 	public void testgetActualCacheExpireDuration_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
// 		CacheUtils cacheUtils = new CacheUtils();
// 		Field field = CacheUtils.class.getDeclaredField("durationUnit");
// 		field.setAccessible(true);
// 		field.set(cacheUtils, "hours");
// 		CacheUtils.getActualCacheExpireDuration();
// 	}
// 	@Test
// 	public void testgetActualCacheExpireDuration_a3() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
// 		CacheUtils cacheUtils = new CacheUtils();
// 		Field field = CacheUtils.class.getDeclaredField("durationUnit");
// 		field.setAccessible(true);
// 		field.set(cacheUtils, "min");
// 		CacheUtils.getActualCacheExpireDuration();
// 	}
	
// 	@Test(expected = NullPointerException.class)
// 	public void testSaveFileToDisk_a1() {
		
// 		Cache enquiryCache = new Cache();
// 		enquiryCache.setCacheExpiryTime(new Date());
// 		JsonObject object = new JsonObject();
// 		String cacheFileName = "file.cache";
// 		CacheUtils.saveFileToDisk(object, cacheFileName);
// 	}
// 	@Test(expected = NullPointerException.class)
// 	public void testSaveFileToDisk_a2() {
		
// 		Cache enquiryCache = new Cache();
// 		enquiryCache.setCacheExpiryTime(new Date());
// 		JsonArray object = new JsonArray();
// 		String cacheFileName = "file.cache";
// 		CacheUtils.saveFileToDisk(object, cacheFileName);
// 	}
// 	@Test(expected = NullPointerException.class)
// 	public void testSaveFileToDisk_a3() {
		
// 		Cache enquiryCache = new Cache();
// 		enquiryCache.setCacheExpiryTime(new Date());
		
// 		String cacheFileName = "file.cache";
// 		CacheUtils.saveFileToDisk(enquiryCache, cacheFileName);
// 	}

// 	@Test
// 	public void testSaveFileToDisk_a4() {
// 		Cache enquiryCache = new Cache();
// 		enquiryCache.setCacheExpiryTime(new Date());
// 		Object throwableObject = new Throwable("Test exception");
// 		String cacheFileName = "System/Temp";
// 		CacheUtils.saveFileToDisk(throwableObject, cacheFileName);
// 	}
// 	@Test
// 	public void testSaveFileToDisk_a5() {
// 		Cache enquiryCache = new Cache();
// 		enquiryCache.setCacheExpiryTime(new Date());
// 		JsonObject object = new JsonObject();
// 		String cacheFileName = "System/Temp";
// 		CacheUtils.saveFileToDisk(object, cacheFileName);
// 	}
// 	@Test
// 	public void testSaveFileToDisk_a6() {
// 		Cache enquiryCache = new Cache();
// 		enquiryCache.setCacheExpiryTime(new Date());
// 		Object object = new Object();
// 		String cacheFileName = "System/Temp";
// 		CacheUtils.saveFileToDisk(object, cacheFileName);
// 	}
// 	@Test
// 	public void testDeleteOldCache_a1() {
// 		Cache cache = mock(Cache.class);
// 		CacheService cacheService = mock(CacheService.class);
// 		when(cache.getNoOfRecords()).thenReturn(2);
// 		CacheUtils.deleteOldCache(cache, "file", cacheService);
// 	}
// 	@Test
// 	public void testDeleteOldCache_a2() throws IOException {
// 		Cache cache = mock(Cache.class);
// 		CacheService cacheService = mock(CacheService.class);
// 		when(cache.getNoOfRecords()).thenReturn(null);
// 		String filePath = "/home/helical/Performance/Test.txt";
// 		File file = new File(filePath);
// 		file.createNewFile();
// 		CacheUtils.deleteOldCache(cache, filePath, cacheService);
// 	}
// 	@Test
// 	public void testDeleteOldCache_a3() throws IOException {
// 		Cache cache = mock(Cache.class);
// 		CacheService cacheService = mock(CacheService.class);
// 		when(cache.getNoOfRecords()).thenReturn(null);
// 		String filePath = "/home/helical/Performance/Test.txt";
// 		CacheUtils.deleteOldCache(cache, filePath, cacheService);
// 	}
// 	@Test
// 	public void testDeleteOldCache_a4() throws IOException {
// 		Cache cache = null;
// 		CacheService cacheService = mock(CacheService.class);
// 		String filePath = "/home/helical/Performance/Test.txt";
// 		CacheUtils.deleteOldCache(cache, filePath, cacheService);
// 	}
	
// }
