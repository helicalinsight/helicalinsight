package com.helicalinsight.adhoc.utils;

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

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileDeleteHandlerTest {

	@Test
	public void ut_a1_test_executeComponent() {
		FileDeleteHandler deleteHandler = new FileDeleteHandler();

		JsonObject formJson = new JsonObject();
		formJson.addProperty("reportFileName", "reportFileName");
		formJson.addProperty("location", "location");
		formJson.addProperty("file", "");

		String executeComponent = deleteHandler.executeComponent(formJson.toString());
		String response = JsonParser.parseString(executeComponent).getAsJsonObject().get("message").getAsString();
		assertEquals("File cannot be deleted", response);
	}

	@Test
	public void ut_a2_test_executeComponent() throws IOException {
		FileDeleteHandler deleteHandler = new FileDeleteHandler();

		JsonObject formJson = new JsonObject();
		formJson.addProperty("reportFileName", "reportFileName");
		formJson.addProperty("location", "System");
		formJson.addProperty("file", "file.txt");

		File file = new File(ApplicationProperties.INSTANCE.getSystemDirectory() + File.separator + "file.txt");
		file.createNewFile();
		String executeComponent = deleteHandler.executeComponent(formJson.toString());
		String response = JsonParser.parseString(executeComponent).getAsJsonObject().get("message").getAsString();
		assertEquals("File deleted successfully.", response);
	}

	@Test
	public void ut_a3_test_executeComponent() throws IOException {
		FileDeleteHandler deleteHandler = new FileDeleteHandler();
		boolean threadSafeToCache = deleteHandler.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
}
