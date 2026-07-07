package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.cache.CacheHelper;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.model.Cache;



public class PrepareFormDataForAdhocDownloadTest {

	@Test
	public void ut_a1_testPrepareFormData() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		PrepareFormDataForAdhocDownload adhocDownload = new PrepareFormDataForAdhocDownload();
		CacheManager cacheManager = mock(CacheManager.class);
		JsonObject adhocFormData = new JsonObject();
		JsonObject object = new JsonObject();
		object.addProperty("fileToDownload", "file");
		Cache cache = mock(Cache.class);
		CacheHelper cacheHelper = mock(CacheHelper.class);
				
		Field field = PrepareFormDataForAdhocDownload.class.getDeclaredField("cacheHelper");
		field.setAccessible(true);
		field.set(adhocDownload, cacheHelper);
		when(cacheManager.getRequestData()).thenReturn(object.toString());
		when(cacheHelper.prepareCacheFromRequest(cacheManager)).thenReturn(cache);
		try(MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class)){
			mockedStatic.when(()-> CacheUtils.getCacheManager(anyString())).thenReturn(cacheManager);
			
			String prepareFormData = adhocDownload.prepareFormData(adhocFormData, "format", "reportName");
			Assert.assertEquals("file", prepareFormData);
		}
	}
	@Test
	public void ut_a2_testPrepareFormData() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		PrepareFormDataForAdhocDownload adhocDownload = new PrepareFormDataForAdhocDownload();
		CacheManager cacheManager = mock(CacheManager.class);
		JsonObject adhocFormData = new JsonObject();
		JsonObject object = new JsonObject();
		object.addProperty("fileToDownload", "file");
		Cache cache = mock(Cache.class);
		CacheHelper cacheHelper = mock(CacheHelper.class);
				
		Field field = PrepareFormDataForAdhocDownload.class.getDeclaredField("cacheHelper");
		field.setAccessible(true);
		field.set(adhocDownload, cacheHelper);
		when(cacheManager.getRequestData()).thenReturn(object.toString());
		when(cacheHelper.prepareCacheFromRequest(cacheManager)).thenReturn(cache);
		try(MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class)){
			mockedStatic.when(()-> CacheUtils.getCacheManager(anyString())).thenReturn(cacheManager);
			
			String prepareFormData = adhocDownload.prepareFormData(adhocFormData, null, "reportName");
			Assert.assertEquals("file", prepareFormData);
		}
	}
}
