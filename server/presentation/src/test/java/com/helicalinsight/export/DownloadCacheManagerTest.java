package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.sql.ResultSet;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.AdhocCacheManager;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.manager.EfwvfCacheManager;
import com.helicalinsight.cache.manager.HCRQueryProcessCacheManager;
import com.helicalinsight.datasource.ActiveQueryRegistry;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.components.DownloadCacheManager;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DownloadCacheManagerTest {

	@Test
	public void ut_a1_testInit() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		String json = "{\"downloadManager\":{\"contentType\":[{\"bean\":\"CSVResultSetFormatDownload\",\"type\":\"csv\"},{\"bean\":\"XLSResultsetFormatDownload\",\"type\":\"xls\"},{\"bean\":\"HCRFileDownload\",\"type\":\"hcr\"}]}}";
		JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

		try(MockedStatic<CacheUtils> static1 = mockStatic(CacheUtils.class)){
			static1.when(()-> CacheUtils.getCacheXmlJson()).thenReturn(jsonObject);
			cacheManager.init();
		}
	}
	
	@Test
	public void ut_a2_testServeCachedContent() {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		JsonObject rawObject = new JsonObject();
		when(request.getParameter("requestId")).thenReturn("11");
		boolean serveCachedContent = cacheManager.serveCachedContent(request, response, rawObject);
		Assert.assertFalse(serveCachedContent);
	}
	@Test
	public void ut_a3_testServeCachedContent() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		HttpServletRequest request = null;
		HttpServletResponse response = null;
		JsonObject rawObject = new JsonObject();
		Field field = DownloadCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("reportName", "reportName");
		field.set(cacheManager, requestParameterJson);
		
		boolean serveCachedContent = cacheManager.serveCachedContent(request, response, rawObject);
		Assert.assertTrue(serveCachedContent);
	}
	@Test
	public void ut_a4_testServeCachedContent() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		IDownload iDownload = mock(IDownload.class);
		JsonObject rawObject = new JsonObject();
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getParameter("sendFileName")).thenReturn("true");
		when(request.getParameter("requestId")).thenReturn("11");
		
		Field field = DownloadCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("reportName", "reportName");
		requestParameterJson.addProperty("fileToDownload", "fileToDownload");
		field.set(cacheManager, requestParameterJson);
		
		Field field2 = DownloadCacheManager.class.getDeclaredField("iDownload");
		field2.setAccessible(true);
		StringBuilder stringBuilder = new StringBuilder();
		when(iDownload.downloadFormat(any(), any())).thenReturn(stringBuilder);
		field2.set(cacheManager, iDownload);
		
		boolean serveCachedContent = cacheManager.serveCachedContent(request, response, rawObject);
		Assert.assertTrue(serveCachedContent);
	}
	
	@Test
	public void ut_a5_testServeCachedContent() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		IDownload iDownload = mock(IDownload.class);
		JsonObject rawObject = new JsonObject();
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getParameter("sendFileName")).thenReturn("false");
		when(request.getParameter("requestId")).thenReturn("11");
		
		Field field = DownloadCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("reportName", "reportName");
		requestParameterJson.addProperty("fileToDownload", "fileToDownload");
		field.set(cacheManager, requestParameterJson);
		
		Field field2 = DownloadCacheManager.class.getDeclaredField("iDownload");
		field2.setAccessible(true);
		StringBuilder stringBuilder = new StringBuilder();
		when(iDownload.downloadFormat(any(), any())).thenReturn(stringBuilder);
		field2.set(cacheManager, iDownload);
		
		boolean serveCachedContent = cacheManager.serveCachedContent(request, response, rawObject);
		Assert.assertTrue(serveCachedContent);
	}
	
	@Test
	public void ut_a6_testServeCachedContent() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		IDownload iDownload = mock(IDownload.class);
		JsonObject rawObject = new JsonObject();
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getParameter("sendFileName")).thenReturn("false");
		when(request.getParameter("requestId")).thenReturn("11");
		
		Field field = DownloadCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("reportName", "reportName");
		requestParameterJson.addProperty("fileToDownload", "fileToDownload");
		field.set(cacheManager, requestParameterJson);
		
		Field field2 = DownloadCacheManager.class.getDeclaredField("iDownload");
		field2.setAccessible(true);
		XSSFWorkbook workbook = mock(XSSFWorkbook.class);
		when(iDownload.downloadFormat(any(), any())).thenReturn(workbook);
		field2.set(cacheManager, iDownload);
		
		boolean serveCachedContent = cacheManager.serveCachedContent(request, response, rawObject);
		Assert.assertTrue(serveCachedContent);
	}
	
	@Test
	public void ut_a7_testServeCachedContent() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		IDownload iDownload = mock(IDownload.class);
		JsonObject rawObject = new JsonObject();
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getParameter("sendFileName")).thenReturn("false");
		when(request.getParameter("requestId")).thenReturn("11");
		
		Field field = DownloadCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("reportName", "reportName");
		requestParameterJson.addProperty("fileToDownload", "fileToDownload");
		field.set(cacheManager, requestParameterJson);
		
		Field field2 = DownloadCacheManager.class.getDeclaredField("iDownload");
		field2.setAccessible(true);
		JsonObject jsonObject = new JsonObject();
		JsonObject jrxmlData = new JsonObject();
		jrxmlData.addProperty("uuid", "report");
		String path = TempDirectoryCleaner.getTempDirectory() + File.separator + "reportnull";
		File file = new File(path);
		file.createNewFile();
		jsonObject.add("jrxmlData", jrxmlData);
		when(iDownload.downloadFormat(any(), any())).thenReturn(jsonObject);
		field2.set(cacheManager, iDownload);
		try {
		boolean serveCachedContent = cacheManager.serveCachedContent(request, response, rawObject);
		Assert.assertTrue(serveCachedContent);
		}finally {
			file.delete();
		}
		
	}
	
	@Test
	public void ut_a8_testServeCachedContent() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		IDownload iDownload = mock(IDownload.class);
		JsonObject rawObject = new JsonObject();
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getParameter("sendFileName")).thenReturn("false");
		when(request.getParameter("requestId")).thenReturn("11");
		
		Field field = DownloadCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("reportName", "reportName");
		requestParameterJson.addProperty("fileToDownload", "fileToDownload");
		field.set(cacheManager, requestParameterJson);
		
		Field field2 = DownloadCacheManager.class.getDeclaredField("iDownload");
		field2.setAccessible(true);
		JsonObject jsonObject = new JsonObject();
		JsonObject jrxmlData = new JsonObject();
		jrxmlData.addProperty("uuid", "report");
		jsonObject.add("jrxmlData", jrxmlData);
		JsonArray arr = new JsonArray();
		arr.add(jsonObject);
		when(iDownload.downloadFormat(any(), any())).thenReturn(arr);
		field2.set(cacheManager, iDownload);
		
		boolean serveCachedContent = cacheManager.serveCachedContent(request, response, rawObject);
		Assert.assertTrue(serveCachedContent);
		
		
	}
	
	@Test
	public void ut_a9_testServeCachedContent() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		IDownload iDownload = mock(IDownload.class);
		JsonObject rawObject = new JsonObject();
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getParameter("sendFileName")).thenReturn("true");
		when(request.getParameter("requestId")).thenReturn("11");
		
		Field field = DownloadCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("reportName", "reportName");
		requestParameterJson.addProperty("fileToDownload", "fileToDownload");
		field.set(cacheManager, requestParameterJson);
		
		Field field2 = DownloadCacheManager.class.getDeclaredField("iDownload");
		field2.setAccessible(true);
		StringBuilder stringBuilder = new StringBuilder();
		when(iDownload.downloadFormat(any(), any())).thenReturn(stringBuilder);
		field2.set(cacheManager, iDownload);
		
		Field field3 = DownloadCacheManager.class.getDeclaredField("isHCR");
		field3.setAccessible(true);
		field3.set(cacheManager, true);
		
		boolean serveCachedContent = cacheManager.serveCachedContent(request, response, rawObject);
		Assert.assertTrue(serveCachedContent);
	}
	
	@Test
	public void ut_b1_testServeCachedContent() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		IDownload iDownload = mock(IDownload.class);
		JsonObject rawObject = new JsonObject();
		JsonArray data = new JsonArray();
		rawObject.add("data", data);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getParameter("sendFileName")).thenReturn("true");
		when(request.getParameter("requestId")).thenReturn("11");
		
		Field field = DownloadCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("reportName", "reportName");
		requestParameterJson.addProperty("fileToDownload", "fileToDownload");
		field.set(cacheManager, requestParameterJson);
		
		Field field2 = DownloadCacheManager.class.getDeclaredField("iDownload");
		field2.setAccessible(true);
		StringBuilder stringBuilder = new StringBuilder();
		when(iDownload.downloadFormat(any(), any())).thenReturn(stringBuilder);
		field2.set(cacheManager, iDownload);
		
		Field field3 = DownloadCacheManager.class.getDeclaredField("isHCR");
		field3.setAccessible(true);
		field3.set(cacheManager, true);
		
		boolean serveCachedContent = cacheManager.serveCachedContent(request, response, rawObject);
		Assert.assertTrue(serveCachedContent);
	}
	
	@Test
	public void ut_b2_testServeCachedContent() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		IDownload iDownload = mock(IDownload.class);
		Object rawObject = mock(ResultSet.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getParameter("sendFileName")).thenReturn("true");
		when(request.getParameter("requestId")).thenReturn("11");
		
		Field field = DownloadCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("reportName", "reportName");
		requestParameterJson.addProperty("fileToDownload", "fileToDownload");
		field.set(cacheManager, requestParameterJson);
		
		Field field2 = DownloadCacheManager.class.getDeclaredField("iDownload");
		field2.setAccessible(true);
		StringBuilder stringBuilder = new StringBuilder();
		when(iDownload.downloadFormat(any(), any())).thenReturn(stringBuilder);
		field2.set(cacheManager, iDownload);
		
		Field field3 = DownloadCacheManager.class.getDeclaredField("isHCR");
		field3.setAccessible(true);
		field3.set(cacheManager, true);
		
		boolean serveCachedContent = cacheManager.serveCachedContent(request, response, rawObject);
		Assert.assertTrue(serveCachedContent);
	}
	
	@Test
	public void ut_b3_testServeCachedContent() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		IDownload iDownload = mock(IDownload.class);
		Object rawObject = mock(ResultSet.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getParameter("sendFileName")).thenReturn("true");
		when(request.getParameter("requestId")).thenReturn("11");
		
		Field field = DownloadCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("reportName", "reportName");
		requestParameterJson.addProperty("fileToDownload", "fileToDownload");
		field.set(cacheManager, requestParameterJson);
		
		Field field2 = DownloadCacheManager.class.getDeclaredField("iDownload");
		field2.setAccessible(true);
		StringBuilder stringBuilder = new StringBuilder();
		when(iDownload.downloadFormat(any(), any())).thenReturn(stringBuilder);
		field2.set(cacheManager, iDownload);
		
		Field field3 = DownloadCacheManager.class.getDeclaredField("isAdhoc");
		field3.setAccessible(true);
		field3.set(cacheManager, true);
		
		boolean serveCachedContent = cacheManager.serveCachedContent(request, response, rawObject);
		Assert.assertTrue(serveCachedContent);
	}
	@Test
	public void ut_b4_testSaveToTemp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		XSSFWorkbook workbook = mock(XSSFWorkbook.class);
		
		Field field = DownloadCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("reportName", "reportName");
		requestParameterJson.addProperty("fileToDownload", "fileToDownload");
		field.set(cacheManager, requestParameterJson);
		
		boolean saveToTemp = cacheManager.saveToTemp(workbook);
		Assert.assertTrue(saveToTemp);
		
	}
	@Test
	public void ut_b5_testSaveToTemp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		XSSFWorkbook workbook = mock(XSSFWorkbook.class);
		
		Field field = DownloadCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("reportName", "reportName");
		requestParameterJson.addProperty("fileToDownload", "fileToDownload");
		field.set(cacheManager, requestParameterJson);
		doThrow(new IOException()).when(workbook).write(any());
		boolean saveToTemp = cacheManager.saveToTemp(workbook);
		Assert.assertFalse(saveToTemp);
		
	}
	@Test
	public void ut_b6_testGetContentTypeString() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		Field field = DownloadCacheManager.class.getDeclaredField("contentType");
		field.setAccessible(true);
		field.set(cacheManager, "SampleString");
		String contentTypeString = cacheManager.getContentTypeString();
		Assert.assertEquals("SampleString", contentTypeString);
	}
	
	@Test
	public void ut_b7_testGetBinaryObject() {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		ResultSet resultSet = mock(ResultSet.class);
		Object binaryObject = cacheManager.getBinaryObject(resultSet);
		Assert.assertNull(binaryObject);
	}
	@Test
	public void ut_b8_testGetRequestData() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		
		Field field = DownloadCacheManager.class.getDeclaredField("requestParameterJson");
		field.setAccessible(true);
		JsonObject requestParameterJson = new JsonObject();
		requestParameterJson.addProperty("reportName", "dummyReport");
		field.set(cacheManager, requestParameterJson);
		
		String requestData = cacheManager.getRequestData();
		Assert.assertTrue(JsonParser.parseString(requestData).getAsJsonObject().get("reportName").getAsString().equals("dummyReport"));
	}
	@Test
	public void ut_b9_testSetRequestData() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = mock(DownloadCacheManager.class);
		AdhocCacheManager adhocCacheManager = mock(AdhocCacheManager.class);
		JsonObject object = new JsonObject();
		object.addProperty("type", "csv");
		object.addProperty("isAdhoc", "true");;
		String data = object.toString();
		
		
		Field field = DownloadCacheManager.class.getDeclaredField("adhocCacheManager");
		field.setAccessible(true);
		field.set(cacheManager, adhocCacheManager);
		
		try(MockedStatic<ApplicationContextAccessor> static1 = mockStatic(ApplicationContextAccessor.class)){
			cacheManager.setRequestData(data);
		}
	}
	
	@Test
	public void ut_c1_testSetRequestData() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		EfwvfCacheManager efwvfManager = mock(EfwvfCacheManager.class);
		JsonObject object = new JsonObject();
		object.addProperty("type", "");
		object.addProperty("isAdhoc", "false");
		String data = object.toString();
		
		
		Field field = DownloadCacheManager.class.getDeclaredField("efwvfManager");
		field.setAccessible(true);
		field.set(cacheManager, efwvfManager);
		
		try(MockedStatic<ApplicationContextAccessor> static1 = mockStatic(ApplicationContextAccessor.class)){
			cacheManager.setRequestData(data);
		}
	}
	
	@Test
	public void ut_c2_testSetRequestData() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = mock(DownloadCacheManager.class);
		
		HCRQueryProcessCacheManager hcrQueryProcessCacheManager = mock(HCRQueryProcessCacheManager.class);
		JsonObject object = new JsonObject();
		object.addProperty("type", "type");
		object.addProperty("isHCR", true);
		object.addProperty("isAdhoc", "false");
		String data = object.toString();
		
		try(MockedStatic<ApplicationContextAccessor> static1 = mockStatic(ApplicationContextAccessor.class)){
			static1.when(()->ApplicationContextAccessor.getBean(HCRQueryProcessCacheManager.class)).thenReturn(hcrQueryProcessCacheManager);
			cacheManager.setRequestData(data);
		}
	}
	
	@Test
	public void ut_c3_testSetRequestData() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager cacheManager = new DownloadCacheManager();
		
		EfwvfCacheManager efwvfManager = mock(EfwvfCacheManager.class);
		JsonObject object = new JsonObject();
		object.addProperty("type", "xls");
		object.addProperty("isHCR", false);
		object.addProperty("isAdhoc", "false");
		String data = object.toString();
		
		Field field = DownloadCacheManager.class.getDeclaredField("efwvfManager");
		field.setAccessible(true);
		field.set(cacheManager, efwvfManager);
		
		try(MockedStatic<ApplicationContextAccessor> static1 = mockStatic(ApplicationContextAccessor.class)){
			cacheManager.setRequestData(data);
		}
	}
	@Test
	public void ut_c4_testWriteFileToStream() throws IOException {
		OutputStream outputStream = mock(OutputStream.class);
		FileInputStream fileInputStream = mock(FileInputStream.class);
		String requestId = "1";
		ActiveQueryRegistry registry = mock(ActiveQueryRegistry.class);
		Long totalBytes = 8192L;
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("percentage", 99);
		when(registry.getFileProcessedPercentage(requestId)).thenReturn(jsonObject);
		when(fileInputStream.read(any())).thenReturn(1).thenReturn(-1);
		DownloadCacheManager.writeFileToStream(totalBytes, outputStream, fileInputStream, requestId, registry);
	}
	@Test
	public void ut_c5_testWriteFileToStream() throws IOException {
		OutputStream outputStream = mock(OutputStream.class);
		FileInputStream fileInputStream = mock(FileInputStream.class);
		String requestId = "1";
		ActiveQueryRegistry registry = mock(ActiveQueryRegistry.class);
		Long totalBytes = 4000L;
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("percentage", 99);
		when(registry.getFileProcessedPercentage(requestId)).thenReturn(jsonObject);
		when(fileInputStream.read(any())).thenReturn(1).thenReturn(-1);
		DownloadCacheManager.writeFileToStream(totalBytes, outputStream, fileInputStream, requestId, registry);
	}
	
	@Test
	public void ut_c6_testGetConnectionFilePath() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager downloadCacheManager = new DownloadCacheManager();
		CacheManager cacheManager = mock(CacheManager.class);
		
		Field field = DownloadCacheManager.class.getDeclaredField("cacheManager");
		field.setAccessible(true);
		field.set(downloadCacheManager, cacheManager);
		when(cacheManager.getConnectionFilePath()).thenReturn("ConnectionPath");
		String connectionFilePath = downloadCacheManager.getConnectionFilePath();
		Assert.assertEquals("ConnectionPath", connectionFilePath);		
	}
	
	@Test
	public void ut_c7_testGetConnectionId() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager downloadCacheManager = new DownloadCacheManager();
		CacheManager cacheManager = mock(CacheManager.class);
		
		Field field = DownloadCacheManager.class.getDeclaredField("cacheManager");
		field.setAccessible(true);
		field.set(downloadCacheManager, cacheManager);
		when(cacheManager.getConnectionId()).thenReturn(1l);
		Long connectionId = downloadCacheManager.getConnectionId();
		Long actual = 1l;
		Assert.assertEquals(actual, connectionId);		
	}
	
	@Test
	public void ut_c8_testGetMapId() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager downloadCacheManager = new DownloadCacheManager();
		CacheManager cacheManager = mock(CacheManager.class);
		
		Field field = DownloadCacheManager.class.getDeclaredField("cacheManager");
		field.setAccessible(true);
		field.set(downloadCacheManager, cacheManager);
		when(cacheManager.getMapId()).thenReturn(111);
		Integer mapId = downloadCacheManager.getMapId();
		Integer actual = 111;
		Assert.assertEquals(actual, mapId);		
	}
	@Test
	public void ut_c9_testGetConnectionType() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager downloadCacheManager = new DownloadCacheManager();
		CacheManager cacheManager = mock(CacheManager.class);
		
		Field field = DownloadCacheManager.class.getDeclaredField("cacheManager");
		field.setAccessible(true);
		field.set(downloadCacheManager, cacheManager);
		when(cacheManager.getConnectionType(1l)).thenReturn("ConnectionPath");
		String connectionFilePath = downloadCacheManager.getConnectionType(1l);
		Assert.assertEquals("ConnectionPath", connectionFilePath);		
	}
	
	
	@Test
	public void ut_d1_testGetQuery() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager downloadCacheManager = new DownloadCacheManager();
		CacheManager cacheManager = mock(CacheManager.class);
		
		Field field = DownloadCacheManager.class.getDeclaredField("cacheManager");
		field.setAccessible(true);
		field.set(downloadCacheManager, cacheManager);
		when(cacheManager.getQuery(anyString())).thenReturn("select * from emp");
		String query = downloadCacheManager.getQuery("string");
		Assert.assertEquals("select * from emp", query);		
	}
	
	@Test
	public void ut_d2_testGetDataFromDatabase() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager downloadCacheManager = new DownloadCacheManager();
		CacheManager cacheManager = mock(CacheManager.class);
		
		Field field = DownloadCacheManager.class.getDeclaredField("cacheManager");
		field.setAccessible(true);
		field.set(downloadCacheManager, cacheManager);
		when(cacheManager.getDataFromDatabase(anyString())).thenReturn(new JsonObject());
		Object obj = downloadCacheManager.getDataFromDatabase("string");
		Assert.assertTrue(((JsonObject) obj).entrySet().isEmpty());	
	}
	
	@Test
	public void ut_d3_testGetDirectory() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DownloadCacheManager downloadCacheManager = new DownloadCacheManager();
		CacheManager cacheManager = mock(CacheManager.class);
		
		Field field = DownloadCacheManager.class.getDeclaredField("cacheManager");
		field.setAccessible(true);
		field.set(downloadCacheManager, cacheManager);
		when(cacheManager.getDirectory()).thenReturn("directory");
		String dir = downloadCacheManager.getDirectory();
		Assert.assertEquals("directory", dir);		
	}
	
}

