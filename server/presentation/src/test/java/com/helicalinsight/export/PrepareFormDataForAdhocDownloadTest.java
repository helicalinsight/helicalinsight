package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.export.components.DownloadCacheManager;

public class PrepareFormDataForAdhocDownloadTest {

	@Test
	public void ut_a1_testPrepareFormData() throws Exception {
		PrepareFormDataForAdhocDownload adhocDownload = new PrepareFormDataForAdhocDownload();
		DownloadCacheManager cacheManager = mock(DownloadCacheManager.class);
		JsonObject adhocFormData = new JsonObject();
		File materialized = new File("file");

		when(cacheManager.materializeToFile(eq("reportName"), eq(false))).thenReturn(materialized);

		try (MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class)) {
			mockedStatic.when(() -> CacheUtils.getCacheManager(anyString())).thenReturn(cacheManager);

			String prepareFormData = adhocDownload.prepareFormData(adhocFormData, "format", "reportName");
			Assert.assertEquals(materialized.getAbsolutePath(), prepareFormData);
			Assert.assertEquals("format", adhocFormData.get("type").getAsString());
			Assert.assertEquals("reportName", adhocFormData.get("reportName").getAsString());
			verify(cacheManager).setRequestData(adhocFormData.toString());
		}
	}

	@Test
	public void ut_a2_testPrepareFormData() throws Exception {
		PrepareFormDataForAdhocDownload adhocDownload = new PrepareFormDataForAdhocDownload();
		DownloadCacheManager cacheManager = mock(DownloadCacheManager.class);
		JsonObject adhocFormData = new JsonObject();
		File materialized = new File("file");

		when(cacheManager.materializeToFile(eq("reportName"), anyBoolean())).thenReturn(materialized);

		try (MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class)) {
			mockedStatic.when(() -> CacheUtils.getCacheManager(anyString())).thenReturn(cacheManager);

			String prepareFormData = adhocDownload.prepareFormData(adhocFormData, null, "reportName");
			Assert.assertEquals(materialized.getAbsolutePath(), prepareFormData);
			Assert.assertFalse(adhocFormData.has("type"));
			Assert.assertEquals("reportName", adhocFormData.get("reportName").getAsString());
			verify(cacheManager).setRequestData(adhocFormData.toString());
		}
	}
}
