package com.helicalinsight.scheduling;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mockStatic;

import java.util.Date;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.resourceloader.rules.RulesUtils;
import com.helicalinsight.scheduling.model.JobParameters;
import com.helicalinsight.scheduling.utils.SchedulerUtils;

public class SchedulerUtilsTest {

	@Test(expected = NullPointerException.class)
	public void testConvertStringIntoCalender() {
		SchedulerUtils schedulerUtils = new SchedulerUtils();
		SchedulerUtils.convertStringIntoCalender(new Date().toString(), "yyyy-MM-dd");
	}
	
	@Test
	public void testConvertStringIntoDate() {
		Date convertStringIntoDate = SchedulerUtils.convertStringIntoDate("2023-09-13","yyyy-MM-dd");
		assertNotNull(convertStringIntoDate);
	}
	@Test
	public void testConvertStringIntoDate_b1() {
		Date convertStringIntoDate = SchedulerUtils.convertStringIntoDate("2023-09-13");
		assertNotNull(convertStringIntoDate);
	}
	@Test
	public void testDateToString_a1() {
		String dateToString = SchedulerUtils.dateToString(new Date());
		assertNotNull(dateToString);
	}
	@Test
	public void testDateToString_a2() {
		String dateToString = SchedulerUtils.dateToString(null);
		assertNull(dateToString);
	}
	@Test
	public void testPrepareJsonFromUserData() {
		try(MockedStatic<RulesUtils> rules = mockStatic(RulesUtils.class)){
			rules.when(()-> RulesUtils.newGetSecurityJsonTemplate()).thenReturn(new JsonObject());
			JsonObject reportParameters = new JsonObject();
			JsonObject ScheduleOptions = new JsonObject();
			JsonObject emailSettings = new JsonObject();
		String parameters = reportParameters.toString();
		SchedulerUtils.prepareJsonFromUserData(ScheduleOptions.toString(), emailSettings.toString(), parameters, "isActive",
				"reportDirectory", "reportFile", "reportName", "");
		}
	}
	@Test
	public void testPrepareJobValue_a1() {
		JobParameters jobParameters = new JobParameters();
		JsonArray arr = new JsonArray();
		SchedulerUtils.prepareJobValue(jobParameters, arr);
	}
	@Test
	public void testPrepareJobValue_a2() {
		JobParameters jobParameters = new JobParameters();
		Integer num = 12;
		SchedulerUtils.prepareJobValue(jobParameters, num);
	}
	@Test
	public void testPrepareJobValue_a3() {
		JobParameters jobParameters = new JobParameters();
		String str = new String("value");
		SchedulerUtils.prepareJobValue(jobParameters, str);
	}
}

