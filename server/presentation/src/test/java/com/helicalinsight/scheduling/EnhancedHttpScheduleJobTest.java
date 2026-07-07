package com.helicalinsight.scheduling;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.scheduling.utils.ScheduleOperation;

public class EnhancedHttpScheduleJobTest {

	@Test
	public void testExecute_a1() {
		EnhancedHttpScheduleJob enhancedHttpScheduleJob = new EnhancedHttpScheduleJob();
		JobExecutionContext context = mock(JobExecutionContext.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		JobDetail jobDetail = mock(JobDetail.class);
		JobDataMap jobDataMap = mock(JobDataMap.class);
		JsonObject json = new JsonObject();
		JsonObject ScheduleOptions = new JsonObject();
		ScheduleOptions.addProperty("endsRadio", "After");
		ScheduleOptions.addProperty("EndAfterExecutions", "1");
		json.addProperty("NoOfExecutions", 2);
		json.add("ScheduleOptions", ScheduleOptions);
		when(context.getJobDetail()).thenReturn(jobDetail);
		when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		when(jobDataMap.getInt("jobinput")).thenReturn(12);
		when(jobDataMap.getString("path")).thenReturn("path");
		when(scheduleOperation.prepareScheduleEntityJson(anyInt())).thenReturn(json);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
					.thenReturn(scheduleOperation);
			enhancedHttpScheduleJob.execute(context);
		}

	}

	@Test(expected = NumberFormatException.class)
	public void testExecute_a2() throws JobExecutionException {
		EnhancedHttpScheduleJob enhancedHttpScheduleJob = new EnhancedHttpScheduleJob();
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		JobExecutionContext context = mock(JobExecutionContext.class);
		JobDetail jobDetail = mock(JobDetail.class);
		JobDataMap jobDataMap = mock(JobDataMap.class);
		JsonObject json = new JsonObject();
		JsonObject ScheduleOptions = new JsonObject();
		ScheduleOptions.addProperty("endsRadio", "After");
		json.add("ScheduleOptions", ScheduleOptions);
		when(context.getJobDetail()).thenReturn(jobDetail);
		when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		when(jobDataMap.getInt("jobinput")).thenReturn(12);
		when(jobDataMap.getString("path")).thenReturn("path");
		when(scheduleOperation.prepareScheduleEntityJson(anyInt())).thenReturn(json);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
					.thenReturn(scheduleOperation);
			enhancedHttpScheduleJob.execute(context);
		}

	}

	@Test(expected = NullPointerException.class)
	public void testExecute_a3() throws JobExecutionException {
		EnhancedHttpScheduleJob enhancedHttpScheduleJob = new EnhancedHttpScheduleJob();
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		JobExecutionContext context = mock(JobExecutionContext.class);
		JobDetail jobDetail = mock(JobDetail.class);
		JobDataMap jobDataMap = mock(JobDataMap.class);
		JsonObject json = new JsonObject();
		JsonObject ScheduleOptions = new JsonObject();
		ScheduleOptions.addProperty("endsRadio", "");
		JsonObject SchedulingJob = new JsonObject();
		SchedulingJob.addProperty("@type", "type");
		SchedulingJob.addProperty("reportFile", "reportFile");
		SchedulingJob.addProperty("reportDirectory", "reportDirectory");
		JsonObject emailSettings = new JsonObject();
		emailSettings.addProperty("Recipients", "123@gmail.com, abc@gmail.com");
		emailSettings.addProperty("Subject", "Subject");
		emailSettings.addProperty("Body", "Body");
		emailSettings.addProperty("Formats", "pdf,csv");
		JsonObject parametersJSON = new JsonObject();
		parametersJSON.addProperty("csvdata","csvdata");
		SchedulingJob.add("EmailSettings", emailSettings);
		SchedulingJob.add("reportParameters", parametersJSON);
		json.add("SchedulingJob", SchedulingJob);
		json.add("ScheduleOptions", ScheduleOptions);
		json.addProperty("JobName", "jobName");

		Map<String, String> realNames = new HashMap<>();
		realNames.put("username", "username");
		realNames.put("organization", "organization");
		realNames.put("password", "password");

			
		when(context.getJobDetail()).thenReturn(jobDetail);
		when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		when(jobDataMap.getInt("jobinput")).thenReturn(12);
		when(jobDataMap.getString("path")).thenReturn("path");
		when(scheduleOperation.prepareScheduleEntityJson(anyInt())).thenReturn(json);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<AuthenticationUtils> authenticationUtils = mockStatic(AuthenticationUtils.class)) {
				try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class)) {
					controllerUtils.when(() -> ControllerUtils.concatenateParameters(any(JsonObject.class)))
							.thenReturn("paramters");

					authenticationUtils.when(() -> AuthenticationUtils.getRealNames(any(JsonObject.class)))
							.thenReturn(realNames);
					mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
							.thenReturn(scheduleOperation);
					enhancedHttpScheduleJob.execute(context);
				}
			}
		}

	}

	@Test(expected = NullPointerException.class)
	public void testExecute_a4() throws JobExecutionException {
		EnhancedHttpScheduleJob enhancedHttpScheduleJob = new EnhancedHttpScheduleJob();
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		JobExecutionContext context = mock(JobExecutionContext.class);
		JobDetail jobDetail = mock(JobDetail.class);
		JobDataMap jobDataMap = mock(JobDataMap.class);
		JsonObject json = new JsonObject();
		JsonObject ScheduleOptions = new JsonObject();
		ScheduleOptions.addProperty("endsRadio", "");
		JsonObject SchedulingJob = new JsonObject();
		SchedulingJob.addProperty("@type", "type");
		SchedulingJob.addProperty("reportFile", "reportFile");
		SchedulingJob.addProperty("reportDirectory", "reportDirectory");
		JsonObject emailSettings = new JsonObject();
		emailSettings.addProperty("Recipients", "123@gmail.com, abc@gmail.com");
		emailSettings.addProperty("Subject", "Subject");
		emailSettings.addProperty("Body", "Body");
		emailSettings.addProperty("Formats", "pdf,csv");
		JsonObject parametersJSON = new JsonObject();
		parametersJSON.add("printOptions", new JsonObject());
		parametersJSON.addProperty("csvdata","csvdata");
		SchedulingJob.add("EmailSettings", emailSettings);
		SchedulingJob.add("reportParameters", parametersJSON);
		json.add("SchedulingJob", SchedulingJob);
		json.add("ScheduleOptions", ScheduleOptions);
		json.addProperty("JobName", "jobName");

		Map<String, String> realNames = new HashMap<>();
		realNames.put("username", "username");
		realNames.put("organization", "organization");
		realNames.put("password", "password");

			
		when(context.getJobDetail()).thenReturn(jobDetail);
		when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		when(jobDataMap.getInt("jobinput")).thenReturn(12);
		when(jobDataMap.getString("path")).thenReturn("path");
		when(scheduleOperation.prepareScheduleEntityJson(anyInt())).thenReturn(json);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<AuthenticationUtils> authenticationUtils = mockStatic(AuthenticationUtils.class)) {
				try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class)) {
					try(MockedStatic<URLEncoder> url = mockStatic(URLEncoder.class)){
						url.when(() -> URLEncoder.encode(anyString(), anyString())).thenThrow(new UnsupportedEncodingException());
					controllerUtils.when(() -> ControllerUtils.concatenateParameters(any(JsonObject.class)))
							.thenReturn("paramters");

					authenticationUtils.when(() -> AuthenticationUtils.getRealNames(any(JsonObject.class)))
							.thenReturn(realNames);
					mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
							.thenReturn(scheduleOperation);
					enhancedHttpScheduleJob.execute(context);
					}
				}
			}
		}

	}
	
	@Test
	public void testUpdateDatabase() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EnhancedHttpScheduleJob enhancedHttpScheduleJob = new EnhancedHttpScheduleJob();
		JobExecutionContext context = mock(JobExecutionContext.class);
		when(context.getFireTime()).thenReturn(new Date());
		when(context.getNextFireTime()).thenReturn(new Date());
		JsonObject object = new JsonObject();
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		
		Method method = EnhancedHttpScheduleJob.class.getDeclaredMethod("updateDatabase", 
				JobExecutionContext.class,JsonObject.class,int.class);
		method.setAccessible(true);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
			.thenReturn(scheduleOperation);
			method.invoke(enhancedHttpScheduleJob, context,object,11);
			
		}
	}

}
