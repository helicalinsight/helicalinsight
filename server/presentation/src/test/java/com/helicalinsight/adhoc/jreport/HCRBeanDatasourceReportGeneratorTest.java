package com.helicalinsight.adhoc.jreport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.sql.ResultSet;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.cachemanager.HCRPrintCacheManager;
import com.helicalinsight.adhoc.report.ReportOpenHelper;
import com.helicalinsight.cache.CacheHelper;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.manager.HCRQueryProcessCacheManager;
import com.helicalinsight.cache.manager.HCRQueryProcessCacheManagerForResultSet;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.datasource.managed.jaxb.HCReport;

import net.sf.jasperreports.engine.JasperPrint;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HCRBeanDatasourceReportGeneratorTest {
	
	@Test
	public void ut_a1_test_generateHCReport() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HCRBeanDatasourceReportGenerator generator = new HCRBeanDatasourceReportGenerator();
		
		JsonObject formData = new JsonObject();
		formData.addProperty("refresh", true);
		formData.addProperty("isFromSaveService", true);
		formData.addProperty("isPreview", false);
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		formData.add("saveDetails", saveDetails);
		JsonObject targetFile = new JsonObject();
		targetFile.addProperty("dir", "dir");
		targetFile.addProperty("file", "file");
		formData.add("targetFile", targetFile);
		JsonObject connectionDetails = new JsonObject();
		JsonObject isEfwdStrOrJson = null;		
		connectionDetails.add("efwd", isEfwdStrOrJson);
		formData.add("connectionDetails", connectionDetails);
		
		JsonObject formData2 = new JsonObject();
		JsonObject newConnectionDetails = new JsonObject();
		JsonObject efwd = new JsonObject();
		newConnectionDetails.add("efwd", efwd);
		newConnectionDetails.addProperty("dir", "dir");
		newConnectionDetails.addProperty("map_id", "map_id");
		formData2.add("connectionDetails", newConnectionDetails);
		
		CacheHelper cacheHelper = mock(CacheHelper.class);
		HCReport hcrReport = mock(HCReport.class);
		CacheManager cacheManager = mock(HCRPrintCacheManager.class);
		Cache cache = mock(Cache.class);
		
		Field field = HCRBeanDatasourceReportGenerator.class.getDeclaredField("cacheHelper");
		field.setAccessible(true);
		field.set(generator, cacheHelper);
		
		when(hcrReport.getFormData()).thenReturn(formData2.toString());
		when(cacheHelper.prepareCacheFromRequest(cacheManager)).thenReturn(cache);
		when(cacheHelper.processCache(any(), any(), any(), any(), any(), any())).thenReturn(true);
		try(MockedStatic<ReportOpenHelper> mockedStatic = mockStatic(ReportOpenHelper.class)){
			try(MockedStatic<CacheUtils> mockedStatic1 = mockStatic(CacheUtils.class)){
				
			mockedStatic.when(()-> ReportOpenHelper.getAdhocReport(anyString(), anyString())).thenReturn(hcrReport);
			mockedStatic1.when(()-> CacheUtils.getCacheManager(anyString())).thenReturn(cacheManager);
			JsonObject generateHCReport = generator.generateHCReport(formData);
			assertNull(generateHCReport);
			}
		}
	
	}
	
	
	@Test
	public void ut_a2_test_generateHCReport() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HCRBeanDatasourceReportGenerator generator = new HCRBeanDatasourceReportGenerator();
		
		JsonObject formData = new JsonObject();
		formData.addProperty("refresh", true);
		formData.addProperty("isFromSaveService", false);
		formData.addProperty("isPreview", false);
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		formData.add("saveDetails", saveDetails);
		JsonObject targetFile = new JsonObject();
		targetFile.addProperty("dir", "dir");
		targetFile.addProperty("file", "file");
		formData.add("targetFile", targetFile);
		JsonObject connectionDetails = new JsonObject();
		JsonObject isEfwdStrOrJson = null;		
		connectionDetails.add("efwd", isEfwdStrOrJson);
		formData.add("connectionDetails", connectionDetails);
		
		JsonObject formData2 = new JsonObject();
		JsonObject newConnectionDetails = new JsonObject();
		JsonObject efwd = new JsonObject();
		newConnectionDetails.add("efwd", efwd);
		newConnectionDetails.addProperty("dir", "dir");
		newConnectionDetails.addProperty("map_id", "map_id");
		formData2.add("connectionDetails", newConnectionDetails);
		
		CacheHelper cacheHelper = mock(CacheHelper.class);
		HCReport hcrReport = mock(HCReport.class);
		CacheManager cacheManager = mock(HCRPrintCacheManager.class);
		Cache cache = mock(Cache.class);
		HCRHelper hcrHelper = mock(HCRHelper.class);
		
		
		Field field = HCRBeanDatasourceReportGenerator.class.getDeclaredField("cacheHelper");
		field.setAccessible(true);
		field.set(generator, cacheHelper);
		
		Field field1 = HCRBeanDatasourceReportGenerator.class.getDeclaredField("hcrHelper");
		field1.setAccessible(true);
		field1.set(generator, hcrHelper);
		
		when(hcrReport.getFormData()).thenReturn(formData2.toString());
		when(cacheHelper.prepareCacheFromRequest(cacheManager)).thenReturn(cache);
		when(cacheHelper.processCache(any(), any(), any(), any(), any(), any())).thenReturn(true);
		when(hcrHelper.provideResponseUsingPrint(any(), any(), any())).thenReturn(new JsonObject());
		try(MockedStatic<ReportOpenHelper> mockedStatic = mockStatic(ReportOpenHelper.class)){
			try(MockedStatic<CacheUtils> mockedStatic1 = mockStatic(CacheUtils.class)){
				
			mockedStatic.when(()-> ReportOpenHelper.getAdhocReport(anyString(), anyString())).thenReturn(hcrReport);
			mockedStatic1.when(()-> CacheUtils.getCacheManager(anyString())).thenReturn(cacheManager);
			JsonObject generateHCReport = generator.generateHCReport(formData);
			assertTrue(generateHCReport.has("updatedPreviewFormData"));
			}
		}
		
	}
	
	@Test
	public void ut_a3_test_generateHCReport() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HCRBeanDatasourceReportGenerator generator = new HCRBeanDatasourceReportGenerator();
		
		JsonObject formData = new JsonObject();
		formData.addProperty("refresh", true);
		formData.addProperty("isFromSaveService", false);
		formData.addProperty("isPreview", true);
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		formData.add("saveDetails", saveDetails);
		JsonObject targetFile = new JsonObject();
		targetFile.addProperty("dir", "dir");
		targetFile.addProperty("file", "file");
		formData.add("targetFile", targetFile);
		JsonObject connectionDetails = new JsonObject();
		JsonObject isEfwdStrOrJson = new JsonObject();		
		connectionDetails.add("efwd", isEfwdStrOrJson);
		formData.add("connectionDetails", connectionDetails);
		
		JsonObject formData2 = new JsonObject();
		JsonObject newConnectionDetails = new JsonObject();
		JsonObject efwd = new JsonObject();
		newConnectionDetails.add("efwd", efwd);
		newConnectionDetails.addProperty("dir", "dir");
		newConnectionDetails.addProperty("map_id", "map_id");
		formData2.add("connectionDetails", newConnectionDetails);
		JsonObject designerChange = new JsonObject();
		designerChange.addProperty("printUUID", "printUUID");
		formData2.add("designerChange", designerChange);
		
		CacheHelper cacheHelper = mock(CacheHelper.class);
		HCReport hcrReport = mock(HCReport.class);
		CacheManager cacheManager = mock(HCRPrintCacheManager.class);
		Cache cache = mock(Cache.class);
		HCRHelper hcrHelper = mock(HCRHelper.class);
		
		
		Field field = HCRBeanDatasourceReportGenerator.class.getDeclaredField("cacheHelper");
		field.setAccessible(true);
		field.set(generator, cacheHelper);
		
		Field field1 = HCRBeanDatasourceReportGenerator.class.getDeclaredField("hcrHelper");
		field1.setAccessible(true);
		field1.set(generator, hcrHelper);
		
		when(hcrReport.getFormData()).thenReturn(formData2.toString());
		when(cacheHelper.prepareCacheFromRequest(cacheManager)).thenReturn(cache);
		when(cacheHelper.processCache(any(), any(), any(), any(), any(), any())).thenReturn(true);
		when(hcrHelper.provideResponseUsingPrint(any(), any(), any())).thenReturn(new JsonObject());
		try(MockedStatic<ReportOpenHelper> mockedStatic = mockStatic(ReportOpenHelper.class)){
			try(MockedStatic<CacheUtils> mockedStatic1 = mockStatic(CacheUtils.class)){
				
			mockedStatic.when(()-> ReportOpenHelper.getAdhocReport(anyString(), anyString())).thenReturn(hcrReport);
			mockedStatic1.when(()-> CacheUtils.getCacheManager(anyString())).thenReturn(cacheManager);
			JsonObject generateHCReport = generator.generateHCReport(formData);
			assertNull(generateHCReport);
			}
		}
		
	}
	@Test
	public void ut_a4_test_generateHCRPrint() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HCRBeanDatasourceReportGenerator generator = new HCRBeanDatasourceReportGenerator();
		HCRHelper hcrHelper = mock(HCRHelper.class);
		HCRQueryProcessCacheManagerForResultSet cacheManager = mock(HCRQueryProcessCacheManagerForResultSet.class);
		CacheHelper cacheHelper = mock(CacheHelper.class);
		Cache cache = mock(Cache.class);
		ResultSet resultSet = mock(ResultSet.class);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		
		Field field = HCRBeanDatasourceReportGenerator.class.getDeclaredField("cacheHelper");
		field.setAccessible(true);
		field.set(generator, cacheHelper);
		
		Field field1 = HCRBeanDatasourceReportGenerator.class.getDeclaredField("hcrHelper");
		field1.setAccessible(true);
		field1.set(generator, hcrHelper);
		
		JsonObject formData = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("temp_uuid", "temp_uuid");
		formData.add("connectionDetails", connectionDetails);
		
		when(cacheHelper.prepareCacheFromRequest(cacheManager)).thenReturn(cache);
		when(cacheHelper.processCache(any(), any(), any(), any(), any(), any())).thenReturn(true);
		when(cacheManager.getResult()).thenReturn(resultSet);
		when(hcrHelper.provideJasperPrint(any(), any())).thenReturn(jasperPrint);
		
		try(MockedStatic<CacheUtils> mockedStatic1 = mockStatic(CacheUtils.class)){
			mockedStatic1.when(()-> CacheUtils.getCacheManager(anyString())).thenReturn(cacheManager);
			
			JasperPrint generateHCRPrint = generator.generateHCRPrint(formData);
			assertEquals(jasperPrint, generateHCRPrint);
		}
		
	}
	
	@Test
	public void ut_a5_test_generateHCRPrint() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HCRBeanDatasourceReportGenerator generator = new HCRBeanDatasourceReportGenerator();
		HCRHelper hcrHelper = mock(HCRHelper.class);
		HCRQueryProcessCacheManager cacheManager = mock(HCRQueryProcessCacheManager.class);
		CacheHelper cacheHelper = mock(CacheHelper.class);
		Cache cache = mock(Cache.class);
		ResultSet resultSet = mock(ResultSet.class);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		
		Field field = HCRBeanDatasourceReportGenerator.class.getDeclaredField("cacheHelper");
		field.setAccessible(true);
		field.set(generator, cacheHelper);
		
		Field field1 = HCRBeanDatasourceReportGenerator.class.getDeclaredField("hcrHelper");
		field1.setAccessible(true);
		field1.set(generator, hcrHelper);
		
		JsonObject formData = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("key", "value");
		formData.add("connectionDetails", connectionDetails);
		JsonObject saveDetails = new JsonObject();
		saveDetails.addProperty("uuid", "uuid");
		formData.add("saveDetails", saveDetails);;
		
		when(cacheHelper.prepareCacheFromRequest(cacheManager)).thenReturn(cache);
		when(cacheHelper.processCache(any(), any(), any(), any(), any(), any())).thenReturn(true);
		when(cacheManager.getResult()).thenReturn(new JsonArray());
		when(hcrHelper.provideJasperPrint(any(), any())).thenReturn(jasperPrint);
		
		try(MockedStatic<CacheUtils> mockedStatic1 = mockStatic(CacheUtils.class)){
			mockedStatic1.when(()-> CacheUtils.getCacheManager(anyString())).thenReturn(cacheManager);
			
			JasperPrint generateHCRPrint = generator.generateHCRPrint(formData);
			assertEquals(jasperPrint, generateHCRPrint);
		}
		
	}
	@Test
	public void ut_a6_test_getResponseUsingPrint() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HCRBeanDatasourceReportGenerator generator = new HCRBeanDatasourceReportGenerator();
		HCRHelper hcrHelper = mock(HCRHelper.class);
		
		Field field1 = HCRBeanDatasourceReportGenerator.class.getDeclaredField("hcrHelper");
		field1.setAccessible(true);
		field1.set(generator, hcrHelper);
		JsonObject jsonObject = new JsonObject();
		when(hcrHelper.provideResponseUsingPrint(any(), any(), any())).thenReturn(jsonObject);
		JsonObject responseUsingPrint = generator.getResponseUsingPrint(any(), any(), any());
		assertEquals(jsonObject, responseUsingPrint);
	}

	
}



