package com.helicalinsight.export;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CSVFormatDownloadTest {

	@Test
	public void ut_a1_testDownloadFormat() {
		 JsonArray arr = new JsonArray();
		 JsonObject object = new JsonObject();
		 object.addProperty("Name", "Amar");
		 object.addProperty("Id", "1");
		 arr.add(object);
		 
		 JsonObject conversionOptions = new JsonObject();
		 conversionOptions.addProperty("blockQuote", "false");
		 CSVFormatDownload csvFormatDownload = new CSVFormatDownload();
		 StringBuilder downloadFormat = csvFormatDownload.downloadFormat(arr, conversionOptions);
		 Assert.assertNotNull(downloadFormat);
	}
	
	
	@Test
	public void ut_a2_testDownloadFormat() {
		 JsonArray arr = new JsonArray();
		 JsonObject object = new JsonObject();
		 object.addProperty("Name", "Amar");
		 object.addProperty("Id", "1");
		 arr.add(object);
		 
		 JsonObject conversionOptions = new JsonObject();
		 conversionOptions.addProperty("blockQuote", "true");
		 CSVFormatDownload csvFormatDownload = new CSVFormatDownload();
		 StringBuilder downloadFormat = csvFormatDownload.downloadFormat(arr, conversionOptions);
		 Assert.assertNotNull(downloadFormat);
		 
	}
}
