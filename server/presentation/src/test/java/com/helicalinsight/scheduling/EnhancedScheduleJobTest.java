package com.helicalinsight.scheduling;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.SendMail;
import com.helicalinsight.export.ReportsProcessor;
import com.helicalinsight.scheduling.utils.ScheduleOperation;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

public class EnhancedScheduleJobTest {

	@Test
	public void testExecute_a1() {
		EnhancedScheduleJob enhancedScheduleJob = new EnhancedScheduleJob();
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		JobExecutionContext context = mock(JobExecutionContext.class);
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
			enhancedScheduleJob.execute(context);
		}

	}

	@Test(expected = NumberFormatException.class)
	public void testExecute_a2() {
		EnhancedScheduleJob enhancedScheduleJob = new EnhancedScheduleJob();
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
			enhancedScheduleJob.execute(context);
		}

	}

	@Test(expected = NullPointerException.class)
	public void testExecute_a3() throws JobExecutionException {
		EnhancedScheduleJob enhancedScheduleJob = new EnhancedScheduleJob();
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
		SchedulingJob.add("reportParameters", new JsonObject());
		json.add("SchedulingJob", SchedulingJob);
		json.add("ScheduleOptions", ScheduleOptions);
		when(context.getJobDetail()).thenReturn(jobDetail);
		when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		when(jobDataMap.getInt("jobinput")).thenReturn(12);
		when(jobDataMap.getString("path")).thenReturn("path");
		when(scheduleOperation.prepareScheduleEntityJson(anyInt())).thenReturn(json);

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<ScheduleJobFactory> factory = mockStatic(ScheduleJobFactory.class)) {
				factory.when(() -> ScheduleJobFactory.getScheduleClass(anyString())).thenReturn("class");
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
						.thenReturn(scheduleOperation);
				enhancedScheduleJob.execute(context);
			}
		}

	}

	@Test
	public void testExecute_a4() throws JobExecutionException {
		EnhancedScheduleJob enhancedScheduleJob = new EnhancedScheduleJob();
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
		SchedulingJob.add("reportParameters", new JsonObject());
		JsonObject emailSettings = new JsonObject();
		emailSettings.addProperty("Recipients", "123@gmail.com, abc@gmail.com");
		emailSettings.addProperty("Subject", "Subject");
		emailSettings.addProperty("Body", "Body");
		emailSettings.addProperty("Formats", "pdf,csv");
		SchedulingJob.add("EmailSettings", emailSettings);
		json.add("SchedulingJob", SchedulingJob);
		json.add("ScheduleOptions", ScheduleOptions);
		json.addProperty("JobName", "jobName");
		when(context.getJobDetail()).thenReturn(jobDetail);
		when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		when(jobDataMap.getInt("jobinput")).thenReturn(12);
		when(jobDataMap.getString("path")).thenReturn("path");
		when(scheduleOperation.prepareScheduleEntityJson(anyInt())).thenReturn(json);
		when(context.getFireTime()).thenReturn(new Date());
		when(context.getNextFireTime()).thenReturn(new Date());
	
		Map<String, String> realNames = new HashMap<>();
		realNames.put("username", "username");
		realNames.put("organization", "organization");
		List<String> list =  new ArrayList<>();
		list.add("pdf");
		list.add("csv");
			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
				try (MockedStatic<ScheduleJobFactory> factory = mockStatic(ScheduleJobFactory.class)) {
					try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {
						try (MockedStatic<AuthenticationUtils> authenticationUtils = mockStatic(
								AuthenticationUtils.class)) {
							try(MockedConstruction<ReportsProcessor> mockedConstruction = mockConstruction(ReportsProcessor.class,(mock, text) -> {
								
								when(mock.generateReportFromURI(anyString(), anyString(), anyString(),any(JsonObject.class))).thenReturn(list);
							})){	
								try(MockedConstruction<SendMail> sendMail = mockConstruction(SendMail.class,(mock, text) -> {
									mock.sendMessage(anyString(), anyString(), any(String[].class),
											anyString(), anyString(), anyString(), anyString(), anyString(),
											anyString(), anyString(), any(String[].class));
									
								})){	
							authenticationUtils.when(() -> AuthenticationUtils.getRealNames(any(JsonObject.class)))
									.thenReturn(realNames);

							utils.when(() -> ControllerUtils.concatenateParameters(any(JsonObject.class)))
									.thenReturn("parameters");

							factory.when(() -> ScheduleJobFactory.getScheduleClass(anyString()))
									.thenReturn("com.helicalinsight.scheduling.EnhancedScheduleJob");
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
									.thenReturn(scheduleOperation);
							
							enhancedScheduleJob.execute(context);
							
							}
							}
					}
				}
			}
		}
	}
	@Test
	public void testExecute_a5() throws JobExecutionException {
		EnhancedScheduleJob enhancedScheduleJob = new EnhancedScheduleJob();
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
		JsonObject parametersJSON = new JsonObject();
		JsonObject adhocFormData = new JsonObject();
		adhocFormData.addProperty("type", "adhoc");
		parametersJSON.add("adhocFormData", adhocFormData);
		SchedulingJob.add("reportParameters", parametersJSON);
		JsonObject emailSettings = new JsonObject();
		emailSettings.addProperty("Recipients", "123@gmail.com, abc@gmail.com");
		emailSettings.addProperty("Subject", "Subject");
		emailSettings.addProperty("Formats", "xxls,csvv");
		SchedulingJob.add("EmailSettings", emailSettings);
		json.add("SchedulingJob", SchedulingJob);
		json.add("ScheduleOptions", ScheduleOptions);
		json.addProperty("JobName", "jobName");
		when(context.getJobDetail()).thenReturn(jobDetail);
		when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		when(jobDataMap.getInt("jobinput")).thenReturn(12);
		when(jobDataMap.getString("path")).thenReturn("path");
		when(jobDataMap.getString("baseUrl")).thenReturn("hi.html");
		when(scheduleOperation.prepareScheduleEntityJson(anyInt())).thenReturn(json);
		when(context.getFireTime()).thenReturn(new Date());
		when(context.getNextFireTime()).thenReturn(new Date());
	
		Map<String, String> realNames = new HashMap<>();
		realNames.put("username", "username");
		realNames.put("organization", null);
		List<String> list =  new ArrayList<>();
		list.add("pdf");
		list.add("csv");
			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
				try (MockedStatic<ScheduleJobFactory> factory = mockStatic(ScheduleJobFactory.class)) {
					try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {
						try (MockedStatic<AuthenticationUtils> authenticationUtils = mockStatic(
								AuthenticationUtils.class)) {
							try(MockedConstruction<ReportsProcessor> mockedConstruction = mockConstruction(ReportsProcessor.class,(mock, text) -> {
								
								when(mock.generateReportFromURI(anyString(), anyString(), anyString(),any(JsonObject.class))).thenReturn(list);
							})){	
								try(MockedConstruction<SendMail> sendMail = mockConstruction(SendMail.class,(mock, text) -> {
									mock.sendMessage(anyString(), anyString(), any(String[].class),
											anyString(), anyString(), anyString(), anyString(), anyString(),
											anyString(), anyString(), any(String[].class));
									
								})){	
									try (MockedStatic<EmailUtility> emailUtility = mockStatic(EmailUtility.class)) {
										
										emailUtility.when(() -> EmailUtility.makeHttpCall(any(JsonObject.class), any(List.class))).thenReturn("response");
							
										
										authenticationUtils.when(() -> AuthenticationUtils.getRealNames(any(JsonObject.class)))
									.thenReturn(realNames);

							utils.when(() -> ControllerUtils.concatenateParameters(any(JsonObject.class)))
									.thenReturn("parameters");

							factory.when(() -> ScheduleJobFactory.getScheduleClass(anyString()))
									.thenReturn("com.helicalinsight.scheduling.EnhancedScheduleJob");
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
									.thenReturn(scheduleOperation);
							
							enhancedScheduleJob.execute(context);
									}
							}
							}
					}
				}
			}
		}
	}
	@Test
	public void testExecute_a6() throws JobExecutionException, MessagingException, IOException {
		EnhancedScheduleJob enhancedScheduleJob = new EnhancedScheduleJob();
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
		JsonObject parametersJSON = new JsonObject();
		JsonObject adhocFormData = new JsonObject();
		adhocFormData.addProperty("type", "adhoc");
		parametersJSON.add("adhocFormData", adhocFormData);
		SchedulingJob.add("reportParameters", parametersJSON);
		JsonObject emailSettings = new JsonObject();
		emailSettings.addProperty("Recipients", "123@gmail.com, abc@gmail.com");
		emailSettings.addProperty("Subject", "Subject");
		emailSettings.addProperty("Formats", "xxls,csvv");
		SchedulingJob.add("EmailSettings", emailSettings);
		json.add("SchedulingJob", SchedulingJob);
		json.add("ScheduleOptions", ScheduleOptions);
		json.addProperty("JobName", "jobName");
		when(context.getJobDetail()).thenReturn(jobDetail);
		when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		when(jobDataMap.getInt("jobinput")).thenReturn(12);
		when(jobDataMap.getString("path")).thenReturn("path");
		when(jobDataMap.getString("baseUrl")).thenReturn("hi.html");
		when(scheduleOperation.prepareScheduleEntityJson(anyInt())).thenReturn(json);
		when(context.getFireTime()).thenReturn(new Date());
		when(context.getNextFireTime()).thenReturn(new Date());
	
		Map<String, String> realNames = new HashMap<>();
		realNames.put("username", "username");
		realNames.put("organization", "organization");
		List<String> list =  new ArrayList<>();
		list.add("pdf");
		list.add("csv");
			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
				try (MockedStatic<ScheduleJobFactory> factory = mockStatic(ScheduleJobFactory.class)) {
					try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {
						try (MockedStatic<AuthenticationUtils> authenticationUtils = mockStatic(
								AuthenticationUtils.class)) {
								
								try(MockedConstruction<SendMail> sendMail = mockConstruction(SendMail.class,(mock, text) -> {
									mock.sendMessage(anyString(), anyString(), any(String[].class),
											anyString(), anyString(), anyString(), anyString(), anyString(),
											anyString(), anyString(), any(String[].class));
									
								})){	
									try (MockedStatic<EmailUtility> emailUtility = mockStatic(EmailUtility.class)) {
										
										emailUtility.when(() -> EmailUtility.makeBinaryHttpCall(any(JsonObject.class), any(List.class))).thenReturn("response");
							
										
										authenticationUtils.when(() -> AuthenticationUtils.getRealNames(any(JsonObject.class)))
									.thenReturn(realNames);

							utils.when(() -> ControllerUtils.concatenateParameters(any(JsonObject.class)))
									.thenReturn("parameters");

							factory.when(() -> ScheduleJobFactory.getScheduleClass(anyString()))
									.thenReturn("com.helicalinsight.scheduling.EnhancedScheduleJob");
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
									.thenReturn(scheduleOperation);
							
							enhancedScheduleJob.execute(context);
									}
							
							}
					}
				}
			}
		}
	}

}
