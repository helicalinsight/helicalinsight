package com.helicalinsight.adhoc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.utility.FlatFilesDeleteUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteFilesHandlerTest {

	@Test
	public void ut_a1_test_executeComponent() {
		DeleteFilesHandler deleteFilesHandler = new DeleteFilesHandler();
		JsonObject formData = new JsonObject();
		JsonArray listOfFilesPath = new JsonArray();
		formData.add("listOfFilesToBeDeleted", listOfFilesPath);
		String executeComponent = deleteFilesHandler.executeComponent(formData.toString());
		String response = JsonParser.parseString(executeComponent).getAsJsonObject().get("message").getAsString();
		assertEquals("file(s) deleted successfully..", response);
	}
	
	@Test
	public void ut_a2_test_executeComponent() {
		DeleteFilesHandler deleteFilesHandler = new DeleteFilesHandler();
		JsonObject formData = new JsonObject();
		JsonArray listOfFilesPath = new JsonArray();
		formData.add("listOfFilesToBeDeleted", listOfFilesPath);
		
		try(MockedStatic<FlatFilesDeleteUtils> mockedStatic = mockStatic(FlatFilesDeleteUtils.class)){
			mockedStatic.when(()-> FlatFilesDeleteUtils.deleteFolderOrFile(any())).thenThrow(new IOException());
			
			String executeComponent = deleteFilesHandler.executeComponent(formData.toString());
			assertEquals("Cannot able to perform the operation.",JsonParser.parseString(executeComponent).getAsJsonObject().get("message").getAsString());
		}
		
	}
	
	@Test
	public void ut_a3_test_isThreadSafeToCache() {
		DeleteFilesHandler deleteFilesHandler = new DeleteFilesHandler();
		boolean threadSafeToCache = deleteFilesHandler.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}

}
