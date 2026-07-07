package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.export.components.ExcelImageBinder;

public class ExcelImageBinderTest {

	@Test
	public void ut_a1_testIsThreadSafeToCache() {
		ExcelImageBinder binder  = new ExcelImageBinder();
		boolean threadSafeToCache = binder.isThreadSafeToCache();
		Assert.assertTrue(threadSafeToCache);
	}
	@Test
	public void ut_a2_testExecuteComponent() throws IOException {
		ExcelImageBinder binder  = new ExcelImageBinder();
		JsonObject formData = new JsonObject();
		formData.addProperty("targetJson", "targetJson");
		String jsonFormData = formData.toString();
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("destinationFile", "/home/helical/Performance/dummyFile1.jpg");
		JsonArray arr = new JsonArray();
		JsonObject image = new JsonObject();
		image.addProperty("index", "/home/helical/Performance/dummyFile1.jpg");
		image.addProperty("left", "200");
		image.addProperty("top", "300");
		arr.add(image);
		jsonObject.add("report", arr);
		String json = jsonObject.toString();
		File file = new File("D:/dummyFile1.jpg");
		file.createNewFile();
		
		try(MockedStatic<ExportUtils> static1 = mockStatic(ExportUtils.class)){
			static1.when(()->  ExportUtils.getFileAsString(anyString())).thenReturn(json);
			String executeComponent = binder.executeComponent(jsonFormData);
			Assert.assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().get("message").getAsString().equals("Successfully created object"));
		}finally {
			if(file.exists()) {
				file.delete();
			}
		}
		
		
	}
	
	@Test
	public void ut_a3_testExecuteComponent() {
		ExcelImageBinder binder  = new ExcelImageBinder();
		JsonObject formData = new JsonObject();
		formData.addProperty("targetJson", "targetJson");
		String jsonFormData = formData.toString();
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("destinationFile", "destinationFile");
		JsonArray arr = new JsonArray();
		JsonObject image = new JsonObject();
		image.addProperty("index", "index");
		image.addProperty("left", "200");
		image.addProperty("top", "300");
		arr.add(image);
		jsonObject.add("report", arr);
		String json = jsonObject.toString();
		
		try(MockedStatic<ExportUtils> static1 = mockStatic(ExportUtils.class)){
			static1.when(()->  ExportUtils.getFileAsString(anyString())).thenReturn(json);
			String executeComponent = binder.executeComponent(jsonFormData);
			Assert.assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().get("message").getAsString().equals("Successfully created object"));
		}
		
		
	}
}
