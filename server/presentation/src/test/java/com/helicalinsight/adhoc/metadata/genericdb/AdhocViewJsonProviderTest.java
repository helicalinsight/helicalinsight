package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import com.google.gson.JsonObject;

public class AdhocViewJsonProviderTest {

	@Test
	public void ut_a1_test_getFileLocation() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		AdhocViewJsonProvider adhocViewJsonProvider = new AdhocViewJsonProvider();
		Method method = AdhocViewJsonProvider.class.getDeclaredMethod("getFileLocation", JsonObject.class);
		method.setAccessible(true);
		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("location", "location");
		formDataJson.addProperty("metadataFileName", "metadataFileName");
		
		Object invoke = method.invoke(adhocViewJsonProvider, formDataJson);
		assertNotNull(invoke);
	}
	
	@Test
	public void ut_a2_test_getFileLocation() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		AdhocViewJsonProvider adhocViewJsonProvider = new AdhocViewJsonProvider();
		Method method = AdhocViewJsonProvider.class.getDeclaredMethod("getFileLocation", JsonObject.class);
		method.setAccessible(true);
		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("location", "location");
		Object invoke = method.invoke(adhocViewJsonProvider, formDataJson);
		assertNull(invoke);
	}
}
