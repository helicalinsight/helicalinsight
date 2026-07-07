package com.helicalinsight.export;



import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class XLSFormatDownloadTest {

	@Test
	public void ut_a1_testDownloadFormat() {
		XLSFormatDownload download = new XLSFormatDownload();
		JsonArray arr= new JsonArray();
		JsonObject object = new JsonObject();
		object.addProperty("name", "helical");
		object.addProperty("password", "insight");
		arr.add(object);
		JsonObject conversionOptions = new JsonObject();
		conversionOptions.addProperty("sheetName", "Report");
		HSSFWorkbook downloadFormat = download.downloadFormat(arr, conversionOptions);
		Assert.assertNotNull(downloadFormat);
	}
	@Test
	public void ut_a2_testDownloadFormat() {
		XLSFormatDownload download = new XLSFormatDownload();
		JsonArray arr= new JsonArray();
		JsonObject object = new JsonObject();
		String value = "helical";
		object.addProperty("name", value);
		object.addProperty("password", 123);
		object.addProperty("Boolean", true);
		object.addProperty("Double", 10.2);
		object.addProperty("date", new Date().getTime());
		arr.add(object);
		JsonObject conversionOptions = new JsonObject();
		conversionOptions.addProperty("sheetName", "");
		HSSFWorkbook downloadFormat = download.downloadFormat(arr, conversionOptions);
		Assert.assertNotNull(downloadFormat);

	}
}
