package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.managed.ResultSetHelper;
import com.helicalinsight.efw.utility.ExportWatermarkHelper;
import com.helicalinsight.efw.utility.ResponseMetadataEnricher;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSResultsetFormatDownloadTest {
	@Test
	public void ut_a1_testDownloadFormat() throws SQLException {
		XLSResultsetFormatDownload download = new XLSResultsetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		JsonObject conversionOptions = new JsonObject();
		conversionOptions.addProperty("sheetName", "Report");
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);
		List<String> hiddenList = new ArrayList<>();
		hiddenList.add("name");
		
		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(1);
		when(metaData.getColumnLabel(1)).thenReturn("name");
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		try(MockedStatic<ResultSetHelper> static1 = mockStatic(ResultSetHelper.class)){
			static1.when(()->   ResultSetHelper.getHiddenList(conversionOptions) ).thenReturn(hiddenList);
			download.downloadFormat(resultSet, conversionOptions);
		}
	}
	
	@Test
	public void ut_a2_testDownloadFormat() throws SQLException {
		XLSResultsetFormatDownload download = new XLSResultsetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		JsonObject conversionOptions = new JsonObject();
		conversionOptions.addProperty("sheetName", "Report");
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);
		List<String> hiddenList = new ArrayList<>();
		hiddenList.add("name");
		
		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(1);
		when(metaData.getColumnLabel(1)).thenReturn("helical");
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		try(MockedStatic<ResultSetHelper> static1 = mockStatic(ResultSetHelper.class)){
			static1.when(()->   ResultSetHelper.getHiddenList(conversionOptions) ).thenReturn(hiddenList);
			download.downloadFormat(resultSet, conversionOptions);
		}
	}
	@Test
	public void ut_a3_testDownloadFormat() throws SQLException {
		XLSResultsetFormatDownload download = new XLSResultsetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		JsonObject conversionOptions = new JsonObject();
		conversionOptions.addProperty("sheetName", "Report");
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);
		List<String> hiddenList = new ArrayList<>();
		hiddenList.add("name");
		Date date = new Date(0);
		when(resultSet.getObject(1)).thenReturn(date);
		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(1);
		when(metaData.getColumnLabel(1)).thenReturn("helical");
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		try(MockedStatic<ResultSetHelper> static1 = mockStatic(ResultSetHelper.class)){
			static1.when(()->   ResultSetHelper.getHiddenList(conversionOptions) ).thenReturn(hiddenList);
			download.downloadFormat(resultSet, conversionOptions);
		}
	}
	
	@Test
	public void ut_a4_testDownloadFormat() throws SQLException {
		XLSResultsetFormatDownload download = new XLSResultsetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		JsonObject conversionOptions = new JsonObject();
		conversionOptions.addProperty("sheetName", "Report");
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);
		List<String> hiddenList = new ArrayList<>();
		hiddenList.add("name");
		when(resultSet.getObject(1)).thenReturn(true);
		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(1);
		when(metaData.getColumnLabel(1)).thenReturn("helical");
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		try(MockedStatic<ResultSetHelper> static1 = mockStatic(ResultSetHelper.class)){
			static1.when(()->   ResultSetHelper.getHiddenList(conversionOptions) ).thenReturn(hiddenList);
			download.downloadFormat(resultSet, conversionOptions);
		}
	}
	
	@Test
	public void ut_a5_testDownloadFormat() throws SQLException {
		XLSResultsetFormatDownload download = new XLSResultsetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		JsonObject conversionOptions = new JsonObject();
		conversionOptions.addProperty("sheetName", "Report");
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);
		List<String> hiddenList = new ArrayList<>();
		hiddenList.add("name");
		when(resultSet.getObject(1)).thenReturn("String");
		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(1);
		when(metaData.getColumnLabel(1)).thenReturn("helical");
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		try(MockedStatic<ResultSetHelper> static1 = mockStatic(ResultSetHelper.class)){
			static1.when(()->   ResultSetHelper.getHiddenList(conversionOptions) ).thenReturn(hiddenList);
			download.downloadFormat(resultSet, conversionOptions);
		}
	}
	
	@Test
	public void ut_a6_testDownloadFormat() throws SQLException {
		XLSResultsetFormatDownload download = new XLSResultsetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		JsonObject conversionOptions = new JsonObject();
		conversionOptions.addProperty("sheetName", "Report");
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);
		List<String> hiddenList = new ArrayList<>();
		hiddenList.add("name");
		when(resultSet.getObject(1)).thenReturn(12.12);
		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(1);
		when(metaData.getColumnLabel(1)).thenReturn("helical");
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		try(MockedStatic<ResultSetHelper> static1 = mockStatic(ResultSetHelper.class)){
			static1.when(()->   ResultSetHelper.getHiddenList(conversionOptions) ).thenReturn(hiddenList);
			download.downloadFormat(resultSet, conversionOptions);
		}
	}
	@Test
	public void ut_a7_testDownloadFormat() throws SQLException {
		XLSResultsetFormatDownload download = new XLSResultsetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		JsonObject conversionOptions = new JsonObject();
		conversionOptions.addProperty("sheetName", "Report");
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);
		List<String> hiddenList = new ArrayList<>();
		hiddenList.add("name");
		when(resultSet.getObject(1)).thenReturn(111);
		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(1);
		when(metaData.getColumnLabel(1)).thenReturn("helical");
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		try(MockedStatic<ResultSetHelper> static1 = mockStatic(ResultSetHelper.class)){
			static1.when(()->   ResultSetHelper.getHiddenList(conversionOptions) ).thenReturn(hiddenList);
			download.downloadFormat(resultSet, conversionOptions);
		}
	}
	@Test
	public void ut_a8_testDownloadFormat() throws SQLException {
		XLSResultsetFormatDownload download = new XLSResultsetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		JsonObject conversionOptions = new JsonObject();
		conversionOptions.addProperty("sheetName", "Report");
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);
		List<String> hiddenList = new ArrayList<>();
		hiddenList.add("name");
		when(resultSet.getObject(1)).thenReturn(new JsonObject());
		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(1);
		when(metaData.getColumnLabel(1)).thenReturn("helical");
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		try(MockedStatic<ResultSetHelper> static1 = mockStatic(ResultSetHelper.class)){
			static1.when(()->   ResultSetHelper.getHiddenList(conversionOptions) ).thenReturn(hiddenList);
			download.downloadFormat(resultSet, conversionOptions);
		}
	}
	@Test
	public void ut_a9_testDownloadFormat() throws SQLException {
		XLSResultsetFormatDownload download = new XLSResultsetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		JsonObject conversionOptions = new JsonObject();
		conversionOptions.addProperty("sheetName", "");
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);
		List<String> hiddenList = new ArrayList<>();
		hiddenList.add("name");
		when(resultSet.getObject(1)).thenThrow(new SQLException());
		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(1);
		when(metaData.getColumnLabel(1)).thenReturn("helical");
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		try(MockedStatic<ResultSetHelper> static1 = mockStatic(ResultSetHelper.class)){
			static1.when(()->   ResultSetHelper.getHiddenList(conversionOptions) ).thenReturn(hiddenList);
			download.downloadFormat(resultSet, conversionOptions);
		}
	}

	@Test
	public void ut_a10_testDownloadFormat_appendsWatermarkFooterAndMetadata() throws Exception {
		XLSResultsetFormatDownload download = new XLSResultsetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		JsonObject conversionOptions = new JsonObject();
		conversionOptions.addProperty("sheetName", "Report");
		conversionOptions.addProperty("isAdhoc", true);
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);
		List<String> hiddenList = new ArrayList<>();

		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(2);
		when(metaData.getColumnLabel(1)).thenReturn("col1");
		when(metaData.getColumnLabel(2)).thenReturn("col2");
		when(resultSet.getObject(1)).thenReturn("a");
		when(resultSet.getObject(2)).thenReturn("b");
		when(resultSet.next()).thenReturn(true).thenReturn(false);

		try (MockedStatic<ResultSetHelper> resultSetHelper = mockStatic(ResultSetHelper.class);
			 MockedStatic<ExportWatermarkHelper> watermarkHelper = mockStatic(ExportWatermarkHelper.class)) {
			resultSetHelper.when(() -> ResultSetHelper.getHiddenList(conversionOptions)).thenReturn(hiddenList);
			watermarkHelper.when(() -> ExportWatermarkHelper.shouldApplyWatermark(any(JsonObject.class))).thenReturn(true);
			watermarkHelper.when(ExportWatermarkHelper::getWatermarkText).thenReturn("Powered By Helical Insight \u00A9 7.0");
			watermarkHelper.when(ExportWatermarkHelper::getWatermarkLink).thenReturn("https://www.helicalinsight.com");
			watermarkHelper.when(() -> ExportWatermarkHelper.applyWorkbookMetadata(any(XSSFWorkbook.class))).thenCallRealMethod();

			XSSFWorkbook workbook = download.downloadFormat(resultSet, conversionOptions);
			Assert.assertEquals("Powered By Helical Insight \u00A9 7.0",
					workbook.getSheetAt(0).getRow(3).getCell(0).getStringCellValue());
			Assert.assertEquals(
					ResponseMetadataEnricher.getMetaObject().get("productName").getAsString(),
					workbook.getProperties().getCoreProperties().getTitle());
			workbook.close();
		}
	}
}
