package com.helicalinsight.adhoc.recycle.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.google.gson.JsonObject;

public class RecycleBinActionTest {

	@Test
	public void getAndSetFormData() {
		RecycleBinAction action = new RecycleBinListAction();
		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");

		action.setFormData(formData);

		assertSame(formData, action.getFormData());
		assertEquals("value", action.getFormData().get("key").getAsString());
	}
}
