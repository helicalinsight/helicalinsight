package com.helicalinsight.scheduling;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.JsonUtils;

public class ScheduleJobFactoryTest {

	
	//@Test
	public void testGetScheduleClass_a1() {
		ScheduleJobFactory factory = new ScheduleJobFactory();
		JsonObject object = new JsonObject();
		JsonArray schedule = new JsonArray();
		JsonObject serviceClass = new JsonObject();
		serviceClass.addProperty("@type", "type");
		serviceClass.addProperty("@class", "class");
		
		schedule.add(serviceClass);
		object.add("schedule", schedule);
		try(MockedStatic<JsonUtils> json = mockStatic(JsonUtils.class)){
			json.when(() -> JsonUtils.newGetSettingsJson()).thenReturn(object);
			ScheduleJobFactory.getScheduleClass("type");
			
		}
		
	}
	//@Test
	public void testGetScheduleClass_a2() {
		ScheduleJobFactory factory = new ScheduleJobFactory();
		JsonObject object = new JsonObject();
		JsonArray schedule = new JsonArray();
		JsonObject serviceClass = new JsonObject();
		serviceClass.addProperty("@type", "pipe");
		serviceClass.addProperty("@class", "class");
		
		schedule.add(serviceClass);
		object.add("schedule", schedule);
		try(MockedStatic<JsonUtils> json = mockStatic(JsonUtils.class)){
			json.when(() -> JsonUtils.newGetSettingsJson()).thenReturn(object);
			ScheduleJobFactory.getScheduleClass("type");
			
		}
		
	}
	@Test
	public void testGetScheduleClass_a3() {
		ScheduleJobFactory factory = new ScheduleJobFactory();
		JsonObject object = new JsonObject();
		try(MockedStatic<JsonUtils> json = mockStatic(JsonUtils.class)){
			json.when(() -> JsonUtils.newGetSettingsJson()).thenReturn(object);
			ScheduleJobFactory.getScheduleClass("type");
			
		}
		
	}
	@Test(expected = RuntimeException.class)
	public void testGetIScheduleJobInstance_a1() {
		ScheduleJobFactory.getIScheduleJobInstance("JsonUtils");
	}
	@Test(expected = NullPointerException.class)
	public void testGetIScheduleJobInstance_a2() throws InstantiationException, IllegalAccessException {
		try(MockedStatic<FactoryMethodWrapper> wrapper = mockStatic(FactoryMethodWrapper.class)){
			Class<ISchedule> iSchedule = ISchedule.class;
			wrapper.when(()->FactoryMethodWrapper.getClass(anyString())).thenReturn(iSchedule);
			ScheduleJobFactory.getIScheduleJobInstance("ISchedule");
		}
	}
}
