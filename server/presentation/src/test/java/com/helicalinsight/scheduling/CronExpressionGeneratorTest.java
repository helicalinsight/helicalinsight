package com.helicalinsight.scheduling;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;

public class CronExpressionGeneratorTest {

	@Test(expected = RequiredParameterIsNullException.class)
	public void testExpressionGenerator_a1() {
		CronExpressionGenerator expressionGenerator = new CronExpressionGenerator();
		expressionGenerator.executeComponent(null);
	}
	@Test
	public void testExpressionGenerator_a2() {
		CronExpressionGenerator expressionGenerator = new CronExpressionGenerator();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "daily");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "on");
		json.addProperty("EndDate", "31-01-2024 14:30:00");
		JsonObject jsonFormData = new JsonObject();
		jsonFormData.add("ScheduleOptions", json);
		String executeComponent = expressionGenerator.executeComponent(jsonFormData.toString());
		assertNotNull(executeComponent);
	}
	@Test
	public void testIsThreadSafeToCache() {
		CronExpressionGenerator expressionGenerator = new CronExpressionGenerator();
		boolean threadSafeToCache = expressionGenerator.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
}
