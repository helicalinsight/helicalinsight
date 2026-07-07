package com.helicalinsight.scheduling;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ConvertIntoCronExpressionTest {

	@Test
	public void testConvertDateIntoCronExpression_a1() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "daily");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "on");
		json.addProperty("EndDate", "31-08-2024 14:30:00");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_a2() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "quterly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("EndDate", "31-08-2024 14:30:00");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertEquals("",convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_a3() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertEquals("",convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_a4() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		
		JsonObject json = null;
		
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertEquals("",convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_daily_a1() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "daily");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "on");
		json.addProperty("EndDate", "31-01-2024 14:30:00");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_daily_a2() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "daily");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "never");
		json.addProperty("EndDate", "31-07-2024 14:30:00");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test(expected = NullPointerException.class)
	public void testConvertDateIntoCronExpression_daily_a3() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31/08/2023");
		json.addProperty("Frequency", "daily");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "never");
		json.addProperty("EndDate", "01-09-2023   14:30:00");
		expression.convertDateIntoCronExpression(json);
	}
	@Test
	public void testConvertDateIntoCronExpression_daily_a4() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "daily");
		json.addProperty("RepeatsEvery", 0);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "never");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	
	@Test
	public void testConvertDateIntoCronExpression_monthly_a1() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "Monthly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "after");
		json.addProperty("EndAfterExecutions", "01-09-2023 14:30:00");
		json.addProperty("RepeatBy", "dayOfTheMonth");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_monthly_a2() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "Monthly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "on");
		json.addProperty("EndDate", "31-08-2024 14:30:00");
		json.addProperty("RepeatBy", "dayOfTheMonth");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_monthly_a3() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "Monthly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "on");
		json.addProperty("EndDate", "31-01-2024 14:30:00");
		json.addProperty("RepeatBy", "dayOfTheMonth");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	
	@Test
	public void testConvertDateIntoCronExpression_monthly_a4() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "Monthly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "on");
		json.addProperty("EndDate", "31-08-2024 14:30:00");
		json.addProperty("RepeatBy", "dayOfTheWeek");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_monthly_a5() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "Monthly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "on");
		json.addProperty("EndDate", "31-01-2024 14:30:00");
		json.addProperty("RepeatBy", "dayOfTheWeek");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_monthly_a6() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "Monthly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "never");
		json.addProperty("EndDate", "31-01-2024 14:30:00");
		json.addProperty("RepeatBy", "dayOfTheWeek");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_monthly_a7() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "Monthly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "never");
		json.addProperty("EndDate", "31-01-2024 14:30:00");
		json.addProperty("RepeatBy", "[dayOfTheWeek]");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_yearly_a1() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "yearly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "on");
		json.addProperty("EndDate", "31-08-2024 14:30:00");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_yearly_a2() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "yearly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "on");
		json.addProperty("EndDate", "31-03-2025 14:30:00");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_yearly_a3() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "yearly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "never");
		json.addProperty("EndDate", "31-03-2025 14:30:00");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_yearly_a4() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "yearly");
		json.addProperty("RepeatsEvery", 0);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "never");
		json.addProperty("EndDate", "31-03-2025 14:30:00");
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_weekly_a1() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "Weekly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "on");
		json.addProperty("EndDate", "31-08-2024 14:30:00");
		JsonArray arr = new JsonArray();
		arr.add("MONDAY");
		arr.add("FRIDAY");
		json.addProperty("DaysofWeek", arr.toString());
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_weekly_a2() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "Weekly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "on");
		json.addProperty("EndDate", "31-01-2024 14:30:00");
		JsonArray arr = new JsonArray();
		json.addProperty("DaysofWeek", arr.toString());
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_weekly_a3() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "Weekly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "never");
		json.addProperty("EndDate", "31-01-2024 14:30:00");
		JsonArray arr = new JsonArray();
		json.addProperty("DaysofWeek", arr.toString());
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_weekly_a4() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "Weekly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "");
		json.addProperty("EndDate", "31-01-2024 14:30:00");
		JsonArray arr = new JsonArray();
		json.addProperty("DaysofWeek", arr.toString());
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test
	public void testConvertDateIntoCronExpression_weekly_a5() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "31-08-2023 12:30:00");
		json.addProperty("Frequency", "Weekly");
		json.addProperty("RepeatsEvery", 0);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "");
		json.addProperty("EndDate", "31-01-2024 14:30:00");
		JsonArray arr = new JsonArray();
		json.addProperty("DaysofWeek", arr.toString());
		String convertDateIntoCronExpression = expression.convertDateIntoCronExpression(json);
		assertNotNull(convertDateIntoCronExpression); 
	}
	@Test(expected = NullPointerException.class)
	public void testConvertDateIntoCronExpression_weekly_a6() {
		ConvertIntoCronExpression expression = new ConvertIntoCronExpression();
		JsonObject json = new JsonObject();
		json.addProperty("StartDate", "");
		json.addProperty("Frequency", "Weekly");
		json.addProperty("RepeatsEvery", 1);
		json.addProperty("ScheduledTime", "12:30:30");
		json.addProperty("endsRadio", "");
		json.add("EndDate", null);
		JsonArray arr = new JsonArray();
		json.addProperty("DaysofWeek", arr.toString());
		expression.convertDateIntoCronExpression(json);
		
	}
}
