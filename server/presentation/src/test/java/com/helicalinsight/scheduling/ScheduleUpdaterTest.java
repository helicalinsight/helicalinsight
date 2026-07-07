//package com.helicalinsight.scheduling;
//
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.mockConstruction;
//import static org.mockito.Mockito.mockStatic;
//import static org.mockito.Mockito.when;
//
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import org.junit.Test;
//import org.mockito.MockedConstruction;
//import org.mockito.MockedStatic;
//import org.quartz.JobKey;
//import org.quartz.Scheduler;
//import org.quartz.SchedulerException;
//import org.quartz.Trigger;
//
//import com.google.gson.JsonObject;
//import com.helicalinsight.efw.exceptions.FormValidationException;
//import com.helicalinsight.efw.exceptions.RuntimeIOException;
//import com.helicalinsight.efw.framework.FactoryMethodWrapper;
//import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
//import com.helicalinsight.efw.resourceloader.rules.RulesUtils;
//import com.helicalinsight.scheduling.utils.ScheduleOperation;
//
//public class ScheduleUpdaterTest {
//
//	@Test
//	public void testStringToDate_a1() {
//		ScheduleUpdater scheduleUpdater = new ScheduleUpdater();
//		Date date = new Date();
//		String dateString = date.toString();
//		Date stringToDate = ScheduleUpdater.stringToDate(dateString, "Asia/Kolkata", "time");
//		assertNull(stringToDate);
//	}
//
//	@Test
//	public void testStringToDate_a2() {
//		String dateString = "2023-09-08 14:30:00";
//		Date stringToDate = ScheduleUpdater.stringToDate(dateString, "Asia/Kolkata", "14:30:00");
//		assertNotNull(stringToDate);
//	}
//
//	@Test
//	public void testFindDateAtTimeZone() {
//		Date findDateAtTimeZone = ScheduleUpdater.findDateAtTimeZone("Asia/Kolkata", new Date());
//		assertNotNull(findDateAtTimeZone);
//	}
//
//	@Test(expected = RuntimeIOException.class)
//	public void testUpdateSchedule_a1() throws NoSuchFieldException, SecurityException, IOException,
//			IllegalArgumentException, IllegalAccessException {
//		ScheduleUpdater scheduleUpdater = new ScheduleUpdater();
//
//		JsonObject scheduleData = new JsonObject();
//		scheduleData.addProperty("className", "className");
//		scheduleData.addProperty("command", "add");
//		scheduleData.addProperty("hcrFile", "hcrFile");
//		scheduleData.addProperty("hcrDirectory", "hcrDirectory");
//		scheduleData.addProperty("hcrParameters", "hcrParameters");
//		scheduleData.addProperty("isActive", "isActive");
//		JsonObject ScheduleOptions = new JsonObject();
//		ScheduleOptions.addProperty("StartDate", "2050-09-11 14:30:00");
//		ScheduleOptions.addProperty("timeZone", "Asia/Kolkata");
//		ScheduleOptions.addProperty("ScheduledTime", "14:30:00");
//		ScheduleOptions.addProperty("ScheduledEndTime", "14:30:00");
//		ScheduleOptions.addProperty("endsRadio", "on");
//		ScheduleOptions.addProperty("EndDate", "2050-10-08 14:30:00");
//
//		scheduleData.add("ScheduleOptions", ScheduleOptions);
//		try (MockedStatic<RulesUtils> rulesUtils = mockStatic(RulesUtils.class)) {
//			try (MockedConstruction<XmlOperationWithParser> reader = mockConstruction(XmlOperationWithParser.class)) {
//
//				rulesUtils.when(() -> RulesUtils.newGetSecurityJsonTemplate()).thenReturn(new JsonObject());
//				scheduleUpdater.UpdateSchedule(scheduleData);
//
//			}
//		}
//
//	}
//
//	@Test
//	public void testUpdateSchedule_a2() {
//		ScheduleUpdater scheduleUpdater = new ScheduleUpdater();
//		JsonObject scheduleData = new JsonObject();
//		scheduleData.addProperty("className", "className");
//		scheduleData.addProperty("command", "scheduleSpecificJob");
//		scheduleData.addProperty("hcrFile", "hcrFile");
//		scheduleData.addProperty("hcrDirectory", "hcrDirectory");
//		scheduleData.addProperty("hcrParameters", "hcrParameters");
//		scheduleData.addProperty("isActive", "isActive");
//		scheduleData.addProperty("id", "11");
//
//		JsonObject ScheduleOptions = new JsonObject();
//		ScheduleOptions.addProperty("StartDate", "2050-09-11 14:30:00");
//		ScheduleOptions.addProperty("timeZone", "Asia/Kolkata");
//		ScheduleOptions.addProperty("ScheduledTime", "14:30:00");
//		ScheduleOptions.addProperty("ScheduledEndTime", "14:30:00");
//		ScheduleOptions.addProperty("endsRadio", "on");
//		ScheduleOptions.addProperty("EndDate", "2050-10-08 14:30:00");
//
//		scheduleData.add("ScheduleOptions", ScheduleOptions);
//		try {
//			scheduleUpdater.UpdateSchedule(scheduleData);
//		} catch (RuntimeIOException e) {
//
//		}
//
//	}
//
//	@Test(expected = RuntimeIOException.class)
//	public void testUpdateScheduleForHcr_a1() {
//		ScheduleUpdater scheduleUpdater = new ScheduleUpdater();
//
//		JsonObject scheduleData = new JsonObject();
//		scheduleData.addProperty("className", "className");
//		scheduleData.addProperty("command", "add");
//		scheduleData.addProperty("hcrFile", "hcrFile");
//		scheduleData.addProperty("hcrDirectory", "hcrDirectory");
//		scheduleData.addProperty("hcrParameters", "hcrParameters");
//		scheduleData.addProperty("isActive", "isActive");
//		JsonObject ScheduleOptions = new JsonObject();
//		ScheduleOptions.addProperty("StartDate", "2050-09-11 14:30:00");
//		ScheduleOptions.addProperty("timeZone", "Asia/Kolkata");
//		ScheduleOptions.addProperty("ScheduledTime", "14:30:00");
//		ScheduleOptions.addProperty("ScheduledEndTime", "14:30:00");
//		ScheduleOptions.addProperty("endsRadio", "on");
//		ScheduleOptions.addProperty("EndDate", "2050-10-08 14:30:00");
//
//		scheduleData.add("ScheduleOptions", ScheduleOptions);
//		try (MockedStatic<RulesUtils> rulesUtils = mockStatic(RulesUtils.class)) {
//			try (MockedConstruction<XmlOperationWithParser> reader = mockConstruction(XmlOperationWithParser.class)) {
//
//				rulesUtils.when(() -> RulesUtils.newGetSecurityJsonTemplate()).thenReturn(new JsonObject());
//				scheduleUpdater.updateScheduleForHcr(scheduleData);
//
//			}
//		}
//
//	}
//
//	@Test(expected = RuntimeIOException.class)
//	public void testUpdateScheduleForHcr_a2() {
//		ScheduleUpdater scheduleUpdater = new ScheduleUpdater();
//		JsonObject scheduleData = new JsonObject();
//		scheduleData.addProperty("className", "className");
//		scheduleData.addProperty("command", "scheduleSpecificJob");
//		scheduleData.addProperty("hcrFile", "hcrFile");
//		scheduleData.addProperty("hcrDirectory", "hcrDirectory");
//		scheduleData.addProperty("hcrParameters", "hcrParameters");
//		scheduleData.addProperty("isActive", "isActive");
//		scheduleData.addProperty("id", "11");
//
//		JsonObject ScheduleOptions = new JsonObject();
//		ScheduleOptions.addProperty("StartDate", "2050-09-11 14:30:00");
//		ScheduleOptions.addProperty("timeZone", "Asia/Kolkata");
//		ScheduleOptions.addProperty("ScheduledTime", "14:30:00");
//		ScheduleOptions.addProperty("ScheduledEndTime", "14:30:00");
//		ScheduleOptions.addProperty("endsRadio", "on");
//		ScheduleOptions.addProperty("EndDate", "2050-10-08 14:30:00");
//
//		scheduleData.add("ScheduleOptions", ScheduleOptions);
//		scheduleUpdater.updateScheduleForHcr(scheduleData);
//	}
//
//	@Test
//	public void testPrepareJsonFromUserDataHcr() throws NoSuchMethodException, SecurityException,
//			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		ScheduleUpdater scheduleUpdater = new ScheduleUpdater();
//		Method method = ScheduleUpdater.class.getDeclaredMethod("prepareJsonFromUserDataHcr", String.class,
//				String.class, String.class, String.class, String.class);
//		method.setAccessible(true);
//		try (MockedStatic<RulesUtils> rulesUtils = mockStatic(RulesUtils.class)) {
//			rulesUtils.when(() -> RulesUtils.newGetSecurityJsonTemplate()).thenReturn(new JsonObject());
//			method.invoke(scheduleUpdater, "ScheduleOptions", "hcrParameters", "isActive", "hcrDirectory",
//					"hcrFileName");
//		}
//	}
//
//	@Test
//	public void testPrepareJsonFromUserData() throws NoSuchMethodException, SecurityException, IllegalAccessException,
//			IllegalArgumentException, InvocationTargetException {
//		ScheduleUpdater scheduleUpdater = new ScheduleUpdater();
//		Method method = ScheduleUpdater.class.getDeclaredMethod("prepareJsonFromUserData", String.class, String.class,
//				String.class, String.class, String.class);
//		method.setAccessible(true);
//		try (MockedStatic<RulesUtils> rulesUtils = mockStatic(RulesUtils.class)) {
//			rulesUtils.when(() -> RulesUtils.newGetSecurityJsonTemplate()).thenReturn(new JsonObject());
//			method.invoke(scheduleUpdater, "ScheduleOptions", "hcrParameters", "isActive", "hcrDirectory",
//					"hcrFileName");
//		}
//	}
//
//	@Test(expected = FormValidationException.class)
//	public void testValidateTime_a1() {
//		ScheduleUpdater scheduleUpdater = new ScheduleUpdater();
//		scheduleUpdater.validateTime("2023-09-10 14:30:00", "2023-09-11 14:30:00", "Asia/Kolkata", "14:30:00",
//				"14:30:00");
//	}
//
//	@Test(expected = FormValidationException.class)
//	public void testValidateTime_a2() {
//		ScheduleUpdater scheduleUpdater = new ScheduleUpdater();
//		scheduleUpdater.validateTime("2050-09-08 14:30:00", "2050-08-10 14:30:00", "Asia/Kolkata", "14:30:00",
//				"14:30:00");
//	}
//
//	@Test(expected = FormValidationException.class)
//	public void testValidateTime_a3() {
//		ScheduleUpdater scheduleUpdater = new ScheduleUpdater();
//		scheduleUpdater.validateTime("2050-09-08 14:30:00", "2023-08-10 14:30:00", "Asia/Kolkata", "14:30:00",
//				"14:30:00");
//	}
//
//	@Test(expected = NullPointerException.class)
//	public void testScheduleSpecificJob_a1() {
//		ScheduleUpdater scheduleUpdater = new ScheduleUpdater();
//		ISchedule scheduleClass = mock(ISchedule.class);
//		JsonObject jsonObject = new JsonObject();
//		jsonObject.addProperty("@id", "11");
//		JsonObject ScheduleOptions = new JsonObject();
//		JsonObject SchedulingJob = new JsonObject();
//		SchedulingJob.add("reportParameters", new JsonObject());
//		jsonObject.add("SchedulingJob", SchedulingJob);
//		jsonObject.add("ScheduleOptions", ScheduleOptions);
//		try (MockedConstruction<XmlOperation> xml = mockConstruction(XmlOperation.class, (mock, context) -> {
//			when(mock.getParticularObject(anyString(), anyString())).thenReturn(jsonObject);
//		})) {
//			try (MockedConstruction<ConvertIntoCronExpression> cron = mockConstruction(ConvertIntoCronExpression.class,
//					(mock, context) -> {
//						when(mock.convertDateIntoCronExpression(any(JsonObject.class))).thenReturn("cron");
//					})) {
//				try (MockedStatic<FactoryMethodWrapper> factory = mockStatic(FactoryMethodWrapper.class)) {
//					factory.when(() -> FactoryMethodWrapper.getUntypedInstance(anyString())).thenReturn(scheduleClass);
//
//					scheduleUpdater.scheduleSpecificJob("path", "id", "classname");
//				}
//			}
//		}
//	}
//
//	@Test(expected = NullPointerException.class)
//	public void testScheduleSpecificJob_a2() {
//		ScheduleUpdater scheduleUpdater = new ScheduleUpdater();
//		ISchedule scheduleClass = mock(ISchedule.class);
//		JsonObject jsonObject = new JsonObject();
//		jsonObject.addProperty("@id", "11");
//		JsonObject ScheduleOptions = new JsonObject();
//		jsonObject.add("ScheduleOptions", ScheduleOptions);
//		try (MockedConstruction<XmlOperation> xml = mockConstruction(XmlOperation.class, (mock, context) -> {
//			when(mock.getParticularObject(anyString(), anyString())).thenReturn(jsonObject);
//		})) {
//			try (MockedConstruction<ConvertIntoCronExpression> cron = mockConstruction(ConvertIntoCronExpression.class,
//					(mock, context) -> {
//						when(mock.convertDateIntoCronExpression(any(JsonObject.class))).thenReturn("cron");
//					})) {
//				try (MockedStatic<FactoryMethodWrapper> factory = mockStatic(FactoryMethodWrapper.class)) {
//					factory.when(() -> FactoryMethodWrapper.getUntypedInstance(anyString())).thenReturn(scheduleClass);
//
//					scheduleUpdater.scheduleSpecificJob("path", "id", "classname");
//				}
//			}
//		}
//	}
//
//	@Test
//	public void testScheduleJob_a1() throws SchedulerException {
//		ScheduleUpdater scheduleUpdater = new ScheduleUpdater();
//		Scheduler sch = mock(Scheduler.class);
//		Trigger trigger1 = mock(Trigger.class);
//		JobKey job = mock(JobKey.class);
//		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
//		ISchedule scheduleClass = new HCRScheduler();
//		JsonObject jsonobject = new JsonObject();
//		JsonObject ScheduleOptions = new JsonObject();
//		ScheduleOptions.addProperty("StartDate", "2023-09-11 14:30:00");
//		ScheduleOptions.addProperty("timeZone", "Asia/Kolkata");
//		ScheduleOptions.addProperty("ScheduledTime", "14:30:00");
//		ScheduleOptions.addProperty("ScheduledEndTime", "14:30:00");
//		ScheduleOptions.addProperty("endsRadio", "on");
//		ScheduleOptions.addProperty("EndDate", "2050-10-08 14:30:00");
//
//		jsonobject.add("ScheduleOptions", ScheduleOptions);
//		when(sch.checkExists(any(JobKey.class))).thenReturn(false);
//		when(scheduleOperation.findDateAtTimeZone(anyString(), any(Date.class))).thenReturn(new Date());
//		try (MockedStatic<SchedulerUtility> utility = mockStatic(SchedulerUtility.class)) {
//			try (MockedStatic<JobKey> jobKey = mockStatic(JobKey.class)) {
//
//				try (MockedStatic<ApplicationContextAccessor> app = mockStatic(ApplicationContextAccessor.class)) {
//					try (MockedConstruction<TriggerUtility> tr = mockConstruction(TriggerUtility.class,
//							(mock, context) -> {
//								when(mock.getInstance(anyString(), anyString(), any(Date.class), any(Date.class),
//										anyString())).thenReturn(trigger1);
//							})) {
//
//						jobKey.when(() -> JobKey.jobKey(anyString())).thenReturn(job);
//						app.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
//								.thenReturn(scheduleOperation);
//						utility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
//						scheduleUpdater.scheduleJob("cron", scheduleClass, "jobName", "jobGroup", "path", jsonobject);
//					}
//				}
//			}
//		}
//	}
//	
//	@Test
//	public void testScheduleJob_a2() throws SchedulerException, ParseException {
//		ScheduleUpdater scheduleUpdater = new ScheduleUpdater();
//		Scheduler sch = mock(Scheduler.class);
//		Trigger trigger1 = mock(Trigger.class);
//		JobKey job = mock(JobKey.class);
//		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
//		ISchedule scheduleClass = new HCRScheduler();
//		JsonObject jsonobject = new JsonObject();
//		JsonObject ScheduleOptions = new JsonObject();
//		ScheduleOptions.addProperty("StartDate", new Date().toString());
//		ScheduleOptions.addProperty("timeZone", "Asia/Kolkata");
//		ScheduleOptions.addProperty("ScheduledTime", "14:30:00");
//		ScheduleOptions.addProperty("ScheduledEndTime", "14:30:00");
//		ScheduleOptions.addProperty("endsRadio", "on");
//		ScheduleOptions.addProperty("EndDate", new Date().toString());
//
//		jsonobject.add("ScheduleOptions", ScheduleOptions);
//		
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date date = dateFormat.parse("2023-01-01 14:30:00");
//		when(sch.checkExists(any(JobKey.class))).thenReturn(true);
//		when(scheduleOperation.findDateAtTimeZone(anyString(), any(Date.class))).thenReturn(date);
//		try (MockedStatic<SchedulerUtility> utility = mockStatic(SchedulerUtility.class)) {
//			try (MockedStatic<JobKey> jobKey = mockStatic(JobKey.class)) {
//
//				try (MockedStatic<ApplicationContextAccessor> app = mockStatic(ApplicationContextAccessor.class)) {
//					try (MockedConstruction<TriggerUtility> tr = mockConstruction(TriggerUtility.class,
//							(mock, context) -> {
//								when(mock.getInstance(anyString(), anyString(), any(Date.class), any(Date.class),
//										anyString())).thenReturn(trigger1);
//							})) {
//
//						jobKey.when(() -> JobKey.jobKey(anyString())).thenReturn(job);
//						app.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
//								.thenReturn(scheduleOperation);
//						utility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
//						scheduleUpdater.scheduleJob("cron", scheduleClass, "jobName", "jobGroup", "path", jsonobject);
//					}
//				}
//			}
//		}
//	}
//	@Test
//	public void testScheduleJob_a3() throws SchedulerException, ParseException {
//		ScheduleUpdater scheduleUpdater = new ScheduleUpdater();
//		Scheduler sch = mock(Scheduler.class);
//		Trigger trigger1 = mock(Trigger.class);
//		JobKey job = mock(JobKey.class);
//		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
//		ISchedule scheduleClass = new HCRScheduler();
//		JsonObject jsonobject = new JsonObject();
//		JsonObject ScheduleOptions = new JsonObject();
//		ScheduleOptions.addProperty("StartDate", "2023-09-11 14:30:00");
//		ScheduleOptions.addProperty("timeZone", "Asia/Kolkata");
//		ScheduleOptions.addProperty("ScheduledTime", "14:30:00");
//		ScheduleOptions.addProperty("ScheduledEndTime", "14:30:00");
//		ScheduleOptions.addProperty("endsRadio", "on");
//		ScheduleOptions.addProperty("EndDate", "2023-10-08 14:30:00");
//
//		jsonobject.add("ScheduleOptions", ScheduleOptions);
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date date = dateFormat.parse("2023-11-01 15:30:00");
//		when(sch.checkExists(any(JobKey.class))).thenThrow(new SchedulerException());
//		when(scheduleOperation.findDateAtTimeZone(anyString(), any(Date.class))).thenReturn(date);
//		try (MockedStatic<SchedulerUtility> utility = mockStatic(SchedulerUtility.class)) {
//			try (MockedStatic<JobKey> jobKey = mockStatic(JobKey.class)) {
//
//				try (MockedStatic<ApplicationContextAccessor> app = mockStatic(ApplicationContextAccessor.class)) {
//					try (MockedConstruction<TriggerUtility> tr = mockConstruction(TriggerUtility.class,
//							(mock, context) -> {
//								when(mock.getInstance(anyString(), anyString(), any(Date.class), any(Date.class),
//										anyString())).thenReturn(trigger1);
//							})) {
//
//						jobKey.when(() -> JobKey.jobKey(anyString())).thenReturn(job);
//						app.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
//								.thenReturn(scheduleOperation);
//						utility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
//						scheduleUpdater.scheduleJob("cron", scheduleClass, "jobName", "jobGroup", "path", jsonobject);
//					}
//				}
//			}
//		}
//	}
//
//}
