package com.helicalinsight.export;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.export.components.AnySettingsGroovyProcessor;

public class AnySettingsGroovyProcessorTest {

	@Test
	public void ut_a1_testIsThreadSafeToCache() {
		AnySettingsGroovyProcessor processor = new AnySettingsGroovyProcessor();
		boolean threadSafeToCache = processor.isThreadSafeToCache();
		Assert.assertTrue(threadSafeToCache);
	}
	
	@Test
	public void ut_a2_testExecuteComponent() {
		AnySettingsGroovyProcessor processor = new AnySettingsGroovyProcessor();
		JsonObject formData = new JsonObject();
		JsonArray arr = new JsonArray();
		arr.add("all");
		formData.add("contentId", arr);
		String json = formData.toString();
		processor.executeComponent(json);
	}
	@Test
	public void ut_a3_testExecuteComponent() {
		AnySettingsGroovyProcessor processor = new AnySettingsGroovyProcessor();
		JsonObject formData = new JsonObject();
		JsonArray arr = new JsonArray();
		arr.add("allTypes");
		formData.add("contentId", arr);
		String json = formData.toString();
		processor.executeComponent(json);
	}
	@Test
	public void ut_a4_testExecuteComponent() {
		AnySettingsGroovyProcessor processor = new AnySettingsGroovyProcessor();
		JsonObject formData = new JsonObject();
		
		formData.addProperty("contentId", "hcrConfigurations");
		String json = formData.toString();
		processor.executeComponent(json);
	}
}
