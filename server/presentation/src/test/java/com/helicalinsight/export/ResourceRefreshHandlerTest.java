package com.helicalinsight.export;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonObject;

public class ResourceRefreshHandlerTest {

	@Test
	public void ut_a1_testIsThreadSafeToCache() {
		ResourceRefreshHandler handler = new ResourceRefreshHandler();
		boolean threadSafeToCache = handler.isThreadSafeToCache();
		Assert.assertTrue(threadSafeToCache);
	}
	@Test
	public void ut_a2_testExecuteComponent() {
		ResourceRefreshHandler handler = new ResourceRefreshHandler();
		JsonObject jsonFormData = new JsonObject();
		jsonFormData.addProperty("refresh", "validation");
		String executeComponent = handler.executeComponent(jsonFormData.toString());
		Assert.assertNotNull(executeComponent);
	}
	@Test
	public void ut_a3_testExecuteComponent() {
		ResourceRefreshHandler handler = new ResourceRefreshHandler();
		JsonObject jsonFormData = new JsonObject();
		jsonFormData.addProperty("refresh", "cache");
		String executeComponent = handler.executeComponent(jsonFormData.toString());
		Assert.assertNotNull(executeComponent);
	}
	
	@Test
	public void ut_a4_testExecuteComponent() {
		ResourceRefreshHandler handler = new ResourceRefreshHandler();
		JsonObject jsonFormData = new JsonObject();
		jsonFormData.addProperty("refresh", "test");
		String executeComponent = handler.executeComponent(jsonFormData.toString());
		Assert.assertNotNull(executeComponent);
	}
}
