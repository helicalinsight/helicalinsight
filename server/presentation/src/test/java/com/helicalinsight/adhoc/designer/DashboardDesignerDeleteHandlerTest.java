package com.helicalinsight.adhoc.designer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DashboardDesignerDeleteHandlerTest {

	@Test(expected = IncompleteFormDataException.class)
	public void ut_a1_test_executeComponent() {
		DashboardDesignerDeleteHandler dashboardDesignerDeleteHandler = new DashboardDesignerDeleteHandler();
		JsonObject formData = new JsonObject();
		dashboardDesignerDeleteHandler.executeComponent(formData.toString());
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ut_a2_test_executeComponent() {
		DashboardDesignerDeleteHandler dashboardDesignerDeleteHandler = new DashboardDesignerDeleteHandler();
		JsonObject formData = new JsonObject();
		formData.addProperty("dir", "dir");
		formData.addProperty("file", "file");
		dashboardDesignerDeleteHandler.executeComponent(formData.toString());
		
	}
	
	@Test
	public void ut_a3_test_executeComponent() throws IOException {
		DashboardDesignerDeleteHandler dashboardDesignerDeleteHandler = new DashboardDesignerDeleteHandler();
		JsonObject formData = new JsonObject();
		formData.addProperty("dir", "System");
		formData.addProperty("file", "file.txt");
		String path =  ApplicationProperties.INSTANCE.getSystemDirectory() + File.separator+"file.txt";
		File file = new File(path);
		file.createNewFile();
		String executeComponent = dashboardDesignerDeleteHandler.executeComponent(formData.toString());
		String response = JsonParser.parseString(executeComponent).getAsJsonObject().get("message").getAsString();
		assertEquals("The requested file is deleted successfully", response);
		
		
	}
	@Test
	public void ut_a4_test_isThreadsafeToCache() {
		DashboardDesignerDeleteHandler dashboardDesignerDeleteHandler = new DashboardDesignerDeleteHandler();
		boolean threadSafeToCache = dashboardDesignerDeleteHandler.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
}
