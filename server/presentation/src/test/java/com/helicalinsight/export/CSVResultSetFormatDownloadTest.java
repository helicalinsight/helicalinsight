package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.ExportWatermarkHelper;

import static org.mockito.Mockito.mockStatic;
import org.mockito.MockedStatic;
public class CSVResultSetFormatDownloadTest {
	
	@Test
	public void ut_a1_testDownloadFormat() throws SQLException {
		CSVResultSetFormatDownload csvResultSetFormatDownload = new CSVResultSetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);
		
		JsonObject conversionOptions = new JsonObject();
		JsonArray columns = new JsonArray();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("hidden", true);
		jsonObject.addProperty("alias", "duplicatName");
		columns.add(jsonObject);
		conversionOptions.add("columns", columns);
		conversionOptions.addProperty("blockQuote", "true");
		
		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(1);
		when( metaData.getColumnLabel(1)).thenReturn("Name");
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		StringBuilder downloadFormat = csvResultSetFormatDownload.downloadFormat(resultSet, conversionOptions);
		Assert.assertNotNull(downloadFormat);
		
	}
	
	@Test
	public void ut_a2_testDownloadFormat() throws SQLException {
		CSVResultSetFormatDownload csvResultSetFormatDownload = new CSVResultSetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);
		
		JsonObject conversionOptions = new JsonObject();
		JsonArray columns = new JsonArray();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("hidden", true);
		jsonObject.addProperty("alias", "duplicatName");
		columns.add(jsonObject);
		conversionOptions.add("columns", columns);
		conversionOptions.addProperty("blockQuote", "true");
		
		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(1);
		when( metaData.getColumnLabel(1)).thenReturn("Name");
		when(resultSet.getObject(1)).thenReturn(new Object());
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		StringBuilder downloadFormat = csvResultSetFormatDownload.downloadFormat(resultSet, conversionOptions);
		Assert.assertNotNull(downloadFormat);
		
	}
	
	@Test
	public void ut_a3_testDownloadFormat() throws SQLException {
		CSVResultSetFormatDownload csvResultSetFormatDownload = new CSVResultSetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);
		
		JsonObject conversionOptions = new JsonObject();
		JsonArray columns = new JsonArray();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("hidden", true);
		jsonObject.addProperty("alias", "duplicatName");
		columns.add(jsonObject);
		conversionOptions.add("columns", columns);
		conversionOptions.addProperty("blockQuote", "false");
		
		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(1);
		when( metaData.getColumnLabel(1)).thenReturn("Name");
		when(resultSet.getObject(1)).thenReturn(new Object());
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		StringBuilder downloadFormat = csvResultSetFormatDownload.downloadFormat(resultSet, conversionOptions);
		Assert.assertNotNull(downloadFormat);
		
	}
	@Test
	public void ut_a4_testDownloadFormat() throws SQLException {
		CSVResultSetFormatDownload csvResultSetFormatDownload = new CSVResultSetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);
		
		JsonObject conversionOptions = new JsonObject();
		JsonArray columns = new JsonArray();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("hidden", true);
		jsonObject.addProperty("alias", "duplicatName");
		columns.add(jsonObject);
		conversionOptions.add("columns", columns);
		conversionOptions.addProperty("blockQuote", "false");
		
		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(1);
		when( metaData.getColumnLabel(1)).thenReturn("duplicatName");
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		csvResultSetFormatDownload.downloadFormat(resultSet, conversionOptions);
		
	}
	
	@Test
	public void ut_a5_testDownloadFormat_Exception() throws SQLException {
		CSVResultSetFormatDownload csvResultSetFormatDownload = new CSVResultSetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);
		
		JsonObject conversionOptions = new JsonObject();
		JsonArray columns = new JsonArray();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("hidden", true);
		jsonObject.addProperty("alias", "duplicatName");
		columns.add(jsonObject);
		conversionOptions.add("columns", columns);
		conversionOptions.addProperty("blockQuote", "false");
		
		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(1);
		when( metaData.getColumnLabel(1)).thenThrow(new SQLException("Result is null"));
		StringBuilder downloadFormat = csvResultSetFormatDownload.downloadFormat(resultSet, conversionOptions);
		Assert.assertNull(downloadFormat);
	}

	@Test
	public void ut_a6_testDownloadFormat_appendsWatermarkFooter() throws SQLException {
		CSVResultSetFormatDownload csvResultSetFormatDownload = new CSVResultSetFormatDownload();
		ResultSet resultSet = mock(ResultSet.class);
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);

		JsonObject conversionOptions = new JsonObject();
		conversionOptions.addProperty("isAdhoc", true);
		conversionOptions.addProperty("blockQuote", "true");

		when(resultSet.getMetaData()).thenReturn(metaData);
		when(metaData.getColumnCount()).thenReturn(1);
		when(metaData.getColumnLabel(1)).thenReturn("Name");
		when(resultSet.getObject(1)).thenReturn("value");
		when(resultSet.next()).thenReturn(true).thenReturn(false);
		when(resultSet.isLast()).thenReturn(true);

		try (MockedStatic<ExportWatermarkHelper> watermarkHelper = mockStatic(ExportWatermarkHelper.class)) {
			watermarkHelper.when(() -> ExportWatermarkHelper.shouldApplyWatermark(any(JsonObject.class))).thenReturn(true);
			watermarkHelper.when(ExportWatermarkHelper::getWatermarkText).thenReturn("Powered By Helical Insight \u00A9 7.0");

			StringBuilder downloadFormat = csvResultSetFormatDownload.downloadFormat(resultSet, conversionOptions);
			Assert.assertTrue(downloadFormat.toString().endsWith("\"Powered By Helical Insight \u00A9 7.0\""));
		}
	}
	
	
	
}
