package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.export.components.ExportTemplatesListProvider;

public class ExportTemplatesListProviderTest {

	@Test
	public void ut_a1_testIsThreadSafeToCache() {
		ExportTemplatesListProvider listProvider = new ExportTemplatesListProvider();
		boolean threadSafeToCache = listProvider.isThreadSafeToCache();
		Assert.assertTrue(threadSafeToCache);
	}
	@Test
	public void ut_a2_testExecuteComponent() {
		ExportTemplatesListProvider listProvider = new ExportTemplatesListProvider();	
		String json = null;
		JsonObject obj = new JsonObject();
		obj.add("execute", new JsonObject());
		JsonObject body = new JsonObject();
		body.addProperty("key", "value");
		obj.add("body", body);
		String jsonContent = obj.toString();
		List<File> files = new ArrayList<>();
		File f1 = new File("dir/file1.json");
		files.add(f1);
		try(MockedStatic<FileUtils> static1 = mockStatic(FileUtils.class)){
			try(MockedStatic<ExportUtils> static2 = mockStatic(ExportUtils.class)){
				static1.when(() -> FileUtils.listFiles(any(File.class), any(String[].class), anyBoolean())).thenReturn(files);
				static2.when(()-> ExportUtils.getFileAsString(anyString())).thenReturn(jsonContent);
				static2.when(()-> ExportUtils.getTemplatesDirectory()).thenReturn("dir");
				String executeComponent = listProvider.executeComponent(json);
				Assert.assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().has("templates"));
			}
			
		}
		
	}
	
	@Test
	public void ut_a3_testExecuteComponent() throws IOException {
		ExportTemplatesListProvider listProvider = new ExportTemplatesListProvider();	
		String json = null;
		
		JsonObject obj = new JsonObject();
		JsonObject execute = new JsonObject();
		execute.addProperty("helical", "insight");
		obj.add("execute", execute);
		JsonObject body = new JsonObject();
		body.addProperty("key", "value");
		obj.add("body", body);
		String jsonContent = obj.toString();
		List<File> files = new ArrayList<>();
		File f1 = new File("D:/dummyFile1.js");
		f1.createNewFile();
		files.add(f1);
		String stringWithMarkers = "This is some content before the marker.\n//BEGINS_HERE\nContent between the markers.\n//ENDS_HERE\nMore content after the marker.";

		try(MockedStatic<FileUtils> static1 = mockStatic(FileUtils.class)){
			try(MockedStatic<ExportUtils> static2 = mockStatic(ExportUtils.class)){
				static1.when(() -> FileUtils.listFiles(any(File.class), any(String[].class), anyBoolean())).thenReturn(files);
				static2.when(()-> ExportUtils.getFileAsString(anyString())).thenReturn(jsonContent).thenReturn(stringWithMarkers);
				static2.when(()-> ExportUtils.getTemplatesDirectory()).thenReturn("dir");
				String executeComponent = listProvider.executeComponent(json);
				Assert.assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().has("templates"));
			}
			
		}finally {
			if(f1.exists()) {
				f1.delete();
			}
		}
			
			
		
	}
}
