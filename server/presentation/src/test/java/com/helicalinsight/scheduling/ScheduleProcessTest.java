package com.helicalinsight.scheduling;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.spi.MutableTrigger;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.utils.ScheduleOperation;

public class ScheduleProcessTest {

	@Test
	public void testScheduleJob_a1() throws ParseException, SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();

		ISchedule className = new HCRScheduler();

		Scheduler sch = mock(Scheduler.class);
		ApplicationProperties instance = mock(ApplicationProperties.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		Trigger trigger = mock(Trigger.class);
		JobKey job = mock(JobKey.class);
		JsonObject jsonObject = new JsonObject();
		JsonObject ScheduleOptions = new JsonObject();
		ScheduleOptions.addProperty("StartDate", "2023-09-08 14:30:00");
		ScheduleOptions.addProperty("timeZone", "Asia/Kolkata");
		ScheduleOptions.addProperty("ScheduledTime", "14:30:00");
		ScheduleOptions.addProperty("endsRadio", "on");
		ScheduleOptions.addProperty("EndDate", "2023-10-08 14:30:00");
		jsonObject.add("ScheduleOptions", ScheduleOptions);
		JsonObject SchedulingJob = new JsonObject();
		SchedulingJob.addProperty("@type", "type");
		jsonObject.add("SchedulingJob", SchedulingJob);

		// when(className.getClass()).thenReturn( mockJobClass);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = dateFormat.parse("2023-10-01 14:30:00");
		when(sch.checkExists(any(JobKey.class))).thenReturn(false);
		when(scheduleOperation.findDateAtTimeZone(anyString(), any(Date.class))).thenReturn(date);
		when(instance.getAdhocReportUrl()).thenReturn("report");
		try (MockedStatic<SchedulerUtility> schUtility = mockStatic(SchedulerUtility.class)) {
			try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {
				try (MockedStatic<ApplicationProperties> app = mockStatic(ApplicationProperties.class)) {
					try (MockedStatic<ApplicationContextAccessor> accessor = mockStatic(
							ApplicationContextAccessor.class)) {
						try (MockedStatic<JobKey> jobKey = mockStatic(JobKey.class)) {
							try (MockedConstruction<TriggerUtility> tr = mockConstruction(TriggerUtility.class,
									(mock, context) -> {
										when(mock.getInstance(anyString(), anyString(), any(Date.class),
												any(Date.class), anyString())).thenReturn(trigger);
									})) {

								jobKey.when(() -> JobKey.jobKey(anyString())).thenReturn(job);
								accessor.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
										.thenReturn(scheduleOperation);
								app.when(() -> ApplicationProperties.getInstance()).thenReturn(instance);
								jsonUtils.when(() -> JsonUtils.getReportExtension()).thenReturn("type");
								schUtility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
								scheduleProcess.scheduleJob("cronExpression", className, "jobName", "jobGroup", "path",
										jsonObject, "hi-ee/hi.html");
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void testScheduleJob_a2() throws ParseException, SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();

		ISchedule className = new HCRScheduler();

		Scheduler sch = mock(Scheduler.class);
		ApplicationProperties instance = mock(ApplicationProperties.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		Trigger trigger = mock(Trigger.class);
		JobKey job = mock(JobKey.class);
		JsonObject jsonObject = new JsonObject();
		JsonObject ScheduleOptions = new JsonObject();
		ScheduleOptions.addProperty("StartDate", "2023-09-08 14:30:00");
		ScheduleOptions.addProperty("timeZone", "Asia/Kolkata");
		ScheduleOptions.addProperty("ScheduledTime", "14:30:00");
		ScheduleOptions.addProperty("endsRadio", "on");
		ScheduleOptions.addProperty("EndDate", "2023-10-08 14:30:00");
		jsonObject.add("ScheduleOptions", ScheduleOptions);
		JsonObject SchedulingJob = new JsonObject();
		SchedulingJob.addProperty("@type", "type");
		jsonObject.add("SchedulingJob", SchedulingJob);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = dateFormat.parse("2023-10-08 14:30:00");
		when(sch.checkExists(any(JobKey.class))).thenReturn(true);
		when(scheduleOperation.findDateAtTimeZone(anyString(), any(Date.class))).thenReturn(date);
		when(instance.getAdhocReportUrl()).thenReturn("report");
		try (MockedStatic<SchedulerUtility> schUtility = mockStatic(SchedulerUtility.class)) {
			try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {
				try (MockedStatic<ApplicationProperties> app = mockStatic(ApplicationProperties.class)) {
					try (MockedStatic<ApplicationContextAccessor> accessor = mockStatic(
							ApplicationContextAccessor.class)) {
						try (MockedStatic<JobKey> jobKey = mockStatic(JobKey.class)) {

							jobKey.when(() -> JobKey.jobKey(anyString())).thenReturn(job);
							accessor.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
									.thenReturn(scheduleOperation);
							app.when(() -> ApplicationProperties.getInstance()).thenReturn(instance);
							jsonUtils.when(() -> JsonUtils.getReportExtension()).thenReturn("type");
							schUtility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
							scheduleProcess.scheduleJob("cronExpression", className, "jobName", "jobGroup", "path",
									jsonObject, "hi-ee/hi.html");

						}
					}
				}
			}
		}
	}

	@Test
	public void testScheduleJob_a3() throws ParseException, SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();

		ISchedule className = new HCRScheduler();

		Scheduler sch = mock(Scheduler.class);
		ApplicationProperties instance = mock(ApplicationProperties.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		Trigger trigger = mock(Trigger.class);
		JobKey job = mock(JobKey.class);
		JsonObject jsonObject = new JsonObject();
		JsonObject ScheduleOptions = new JsonObject();
		ScheduleOptions.addProperty("StartDate", new Date().toString());
		ScheduleOptions.addProperty("timeZone", "Asia/Kolkata");
		ScheduleOptions.addProperty("ScheduledTime", "14:30:00");
		ScheduleOptions.addProperty("endsRadio", "on");
		ScheduleOptions.addProperty("EndDate", new Date().toString());
		jsonObject.add("ScheduleOptions", ScheduleOptions);
		JsonObject SchedulingJob = new JsonObject();
		SchedulingJob.addProperty("@type", "type");
		jsonObject.add("SchedulingJob", SchedulingJob);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = dateFormat.parse("2023-10-01 14:30:00");
		when(sch.checkExists(any(JobKey.class))).thenThrow(new SchedulerException());
		when(scheduleOperation.findDateAtTimeZone(anyString(), any(Date.class))).thenReturn(date);
		when(instance.getAdhocReportUrl()).thenReturn("report");
		try (MockedStatic<SchedulerUtility> schUtility = mockStatic(SchedulerUtility.class)) {
			try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {
				try (MockedStatic<ApplicationProperties> app = mockStatic(ApplicationProperties.class)) {
					try (MockedStatic<ApplicationContextAccessor> accessor = mockStatic(
							ApplicationContextAccessor.class)) {
						try (MockedStatic<JobKey> jobKey = mockStatic(JobKey.class)) {
							try (MockedConstruction<TriggerUtility> tr = mockConstruction(TriggerUtility.class,
									(mock, context) -> {
										when(mock.getInstance(anyString(), anyString(), any(Date.class),
												any(Date.class), anyString())).thenReturn(trigger);
									})) {

								jobKey.when(() -> JobKey.jobKey(anyString())).thenReturn(job);
								accessor.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
										.thenReturn(scheduleOperation);
								app.when(() -> ApplicationProperties.getInstance()).thenReturn(instance);
								jsonUtils.when(() -> JsonUtils.getReportExtension()).thenReturn("type");
								schUtility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
								scheduleProcess.scheduleJob("cronExpression", className, "jobName", "jobGroup", "path",
										jsonObject, "hi-ee/hi.html");
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void testScheduleJobB_a1() throws ParseException, SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		ResourceType resourceType = mock(ResourceType.class);
		ISchedule className = new HCRScheduler();
		Schedules schedules = mock(Schedules.class);
		Scheduler sch = mock(Scheduler.class);
		ApplicationProperties instance = mock(ApplicationProperties.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		Trigger trigger = mock(Trigger.class);
		JobKey job = mock(JobKey.class);

		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = dateFormat1.parse("2023-09-08 14:30:00");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date2 = dateFormat2.parse("2023-10-08 14:30:00");
		SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date3 = dateFormat3.parse("2023-10-01 14:30:00");

		when(schedules.getScheduledTime()).thenReturn("14:30:00");

		when(schedules.getEndDate()).thenReturn(date2);
		when(schedules.getEndsOn()).thenReturn("on");
		when(schedules.getScheduleType()).thenReturn(resourceType);
		when(resourceType.getExtension()).thenReturn("type");
		when(schedules.getTimeZone()).thenReturn("Asia/Kolkata");
		when(schedules.getStartDate()).thenReturn(date1);
		when(sch.checkExists(any(JobKey.class))).thenReturn(false);
		when(scheduleOperation.findDateAtTimeZone(anyString(), any(Date.class))).thenReturn(date3);
		when(instance.getAdhocReportUrl()).thenReturn("report");
		try (MockedStatic<SchedulerUtility> schUtility = mockStatic(SchedulerUtility.class)) {
			try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {
				try (MockedStatic<ApplicationProperties> app = mockStatic(ApplicationProperties.class)) {
					try (MockedStatic<ApplicationContextAccessor> accessor = mockStatic(
							ApplicationContextAccessor.class)) {
						try (MockedStatic<JobKey> jobKey = mockStatic(JobKey.class)) {
							try (MockedConstruction<TriggerUtility> tr = mockConstruction(TriggerUtility.class,
									(mock, context) -> {
										when(mock.getInstance(anyString(), anyString(), any(Date.class),
												any(Date.class), anyString())).thenReturn(trigger);
									})) {

								jobKey.when(() -> JobKey.jobKey(anyString())).thenReturn(job);
								accessor.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
										.thenReturn(scheduleOperation);
								app.when(() -> ApplicationProperties.getInstance()).thenReturn(instance);
								jsonUtils.when(() -> JsonUtils.getReportExtension()).thenReturn("type");
								schUtility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
								scheduleProcess.scheduleJob("cronExpression", className, "jobName", "jobGroup",
										schedules, "hi-ee/hi.html");
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void testScheduleJobB_a2() throws ParseException, SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		ResourceType resourceType = mock(ResourceType.class);
		ISchedule className = new HCRScheduler();
		Schedules schedules = mock(Schedules.class);
		Scheduler sch = mock(Scheduler.class);
		ApplicationProperties instance = mock(ApplicationProperties.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		Trigger trigger = mock(Trigger.class);
		JobKey job = mock(JobKey.class);

		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = dateFormat1.parse("2023-09-08 14:30:00");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date2 = dateFormat2.parse("2023-10-08 14:30:00");
		SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date3 = dateFormat3.parse("2023-10-01 14:30:00");

		when(schedules.getScheduledTime()).thenReturn("14:30:00");

		when(schedules.getEndDate()).thenReturn(date2);
		when(schedules.getEndsOn()).thenReturn("on");
		when(schedules.getScheduleType()).thenReturn(resourceType);
		when(resourceType.getExtension()).thenReturn("type");
		when(schedules.getTimeZone()).thenReturn("Asia/Kolkata");
		when(schedules.getStartDate()).thenReturn(date1);
		when(sch.checkExists(any(JobKey.class))).thenReturn(true);
		when(scheduleOperation.findDateAtTimeZone(anyString(), any(Date.class))).thenReturn(date3);
		when(instance.getAdhocReportUrl()).thenReturn("report");
		try (MockedStatic<SchedulerUtility> schUtility = mockStatic(SchedulerUtility.class)) {
			try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {
				try (MockedStatic<ApplicationProperties> app = mockStatic(ApplicationProperties.class)) {
					try (MockedStatic<ApplicationContextAccessor> accessor = mockStatic(
							ApplicationContextAccessor.class)) {
						try (MockedStatic<JobKey> jobKey = mockStatic(JobKey.class)) {
							try (MockedConstruction<TriggerUtility> tr = mockConstruction(TriggerUtility.class,
									(mock, context) -> {
										when(mock.getInstance(anyString(), anyString(), any(Date.class),
												any(Date.class), anyString())).thenReturn(trigger);
									})) {

								jobKey.when(() -> JobKey.jobKey(anyString())).thenReturn(job);
								accessor.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
										.thenReturn(scheduleOperation);
								app.when(() -> ApplicationProperties.getInstance()).thenReturn(instance);
								jsonUtils.when(() -> JsonUtils.getReportExtension()).thenReturn("type");
								schUtility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
								scheduleProcess.scheduleJob("cronExpression", className, "jobName", "jobGroup",
										schedules, "hi-ee/hi.html");
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void testScheduleJobB_a3() throws ParseException, SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		ResourceType resourceType = mock(ResourceType.class);
		ISchedule className = new HCRScheduler();
		Schedules schedules = mock(Schedules.class);
		Scheduler sch = mock(Scheduler.class);
		ApplicationProperties instance = mock(ApplicationProperties.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		Trigger trigger = mock(Trigger.class);
		JobKey job = mock(JobKey.class);

		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = dateFormat1.parse("2023-09-08 14:30:00");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date2 = dateFormat2.parse("2023-10-08 14:30:00");
		SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date3 = dateFormat3.parse("2023-10-09 14:30:00");

		when(schedules.getScheduledTime()).thenReturn("14:30:00");

		when(schedules.getEndDate()).thenReturn(date2);
		when(schedules.getEndsOn()).thenReturn("on");
		when(schedules.getScheduleType()).thenReturn(resourceType);
		when(resourceType.getExtension()).thenReturn("type");
		when(schedules.getTimeZone()).thenReturn("Asia/Kolkata");
		when(schedules.getStartDate()).thenReturn(date1);
		when(sch.checkExists(any(JobKey.class))).thenThrow(new SchedulerException());
		when(scheduleOperation.findDateAtTimeZone(anyString(), any(Date.class))).thenReturn(date3);
		when(instance.getAdhocReportUrl()).thenReturn("report");
		try (MockedStatic<SchedulerUtility> schUtility = mockStatic(SchedulerUtility.class)) {
			try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {
				try (MockedStatic<ApplicationProperties> app = mockStatic(ApplicationProperties.class)) {
					try (MockedStatic<ApplicationContextAccessor> accessor = mockStatic(
							ApplicationContextAccessor.class)) {
						try (MockedStatic<JobKey> jobKey = mockStatic(JobKey.class)) {

							jobKey.when(() -> JobKey.jobKey(anyString())).thenReturn(job);
							accessor.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
									.thenReturn(scheduleOperation);
							app.when(() -> ApplicationProperties.getInstance()).thenReturn(instance);
							jsonUtils.when(() -> JsonUtils.getReportExtension()).thenReturn("type");
							schUtility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
							scheduleProcess.scheduleJob("cronExpression", className, "jobName", "jobGroup", schedules,
									"hi-ee/hi.html");

						}
					}
				}
			}
		}
	}

	@Test
	public void testStopJob_a1() {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		Scheduler sch = mock(Scheduler.class);
		String stopJob = scheduleProcess.stopJob(sch);
		assertEquals(stopJob, "jobStopped");
	}

	@Test
	public void testStopJob_a2() throws SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		Scheduler sch = mock(Scheduler.class);
		doThrow(new SchedulerException()).when(sch).shutdown();
		String stopJob = scheduleProcess.stopJob(sch);
		assertEquals(stopJob, "jobStopped");
	}

	@Test
	public void testDelete_a1() {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		Scheduler sch = mock(Scheduler.class);
		try (MockedStatic<SchedulerUtility> utility = mockStatic(SchedulerUtility.class)) {
			utility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
			scheduleProcess.delete("key");
		}

	}

	@Test
	public void testDelete_a2() throws SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		Scheduler sch = mock(Scheduler.class);
		try (MockedStatic<SchedulerUtility> utility = mockStatic(SchedulerUtility.class)) {
			utility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
			when(sch.deleteJob(any(JobKey.class))).thenThrow(new SchedulerException());
			scheduleProcess.delete("key");
		}
	}

	@Test
	public void testListOfExecutingJob_a1() throws SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		Scheduler sch = mock(Scheduler.class);
		List<JobExecutionContext> listOfJobs = new ArrayList<>();
		try (MockedStatic<SchedulerUtility> utility = mockStatic(SchedulerUtility.class)) {
			utility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
			when(sch.getCurrentlyExecutingJobs()).thenReturn(listOfJobs);
			scheduleProcess.listOfExecutingJob();
		}
	}

	@Test
	public void testListOfExecutingJob_a2() throws SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		Scheduler sch = mock(Scheduler.class);
		try (MockedStatic<SchedulerUtility> utility = mockStatic(SchedulerUtility.class)) {
			utility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
			when(sch.getCurrentlyExecutingJobs()).thenThrow(new SchedulerException());
			scheduleProcess.listOfExecutingJob();
		}
	}

	@Test
	public void testListOfCurrentlyExecutingJob_a1() throws SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		Scheduler sch = mock(Scheduler.class);
		JobDetail jobDetail = mock(JobDetail.class);
		try (MockedStatic<SchedulerUtility> utility = mockStatic(SchedulerUtility.class)) {
			utility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
			when(sch.getJobDetail(any(JobKey.class))).thenReturn(jobDetail);
			String listOfCurrentlyExecutingJob = scheduleProcess.listOfCurrentlyExecutingJob();
			assertEquals(listOfCurrentlyExecutingJob, "ok");
		}
	}

	@Test
	public void testListOfCurrentlyExecutingJob_a2() throws SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		Scheduler sch = mock(Scheduler.class);
		try (MockedStatic<SchedulerUtility> utility = mockStatic(SchedulerUtility.class)) {
			utility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
			when(sch.getJobDetail(any(JobKey.class))).thenThrow(new SchedulerException());
			String listOfCurrentlyExecutingJob = scheduleProcess.listOfCurrentlyExecutingJob();
			assertEquals(listOfCurrentlyExecutingJob, "ok");
		}
	}

	@Test
	public void testJobDetail_a1() throws SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		Scheduler sch = mock(Scheduler.class);
		JobDetail jobDetail = mock(JobDetail.class);
		try (MockedStatic<SchedulerUtility> utility = mockStatic(SchedulerUtility.class)) {
			utility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
			when(sch.getJobDetail(any(JobKey.class))).thenReturn(jobDetail);
			scheduleProcess.jobDetail();
		}
	}

	@Test
	public void testJobDetail_a2() throws SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		Scheduler sch = mock(Scheduler.class);
		try (MockedStatic<SchedulerUtility> utility = mockStatic(SchedulerUtility.class)) {
			utility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
			when(sch.getJobDetail(any(JobKey.class))).thenThrow(new SchedulerException());
			scheduleProcess.jobDetail();
		}
	}

	@Test
	public void testPauseJob_a1() throws SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		Scheduler sch = mock(Scheduler.class);

		try (MockedStatic<SchedulerUtility> utility = mockStatic(SchedulerUtility.class)) {
			utility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
			String pauseJob = scheduleProcess.pauseJob("key");
			assertEquals(pauseJob, "pausejob");
		}
	}

	@Test
	public void testPauseJob_a2() throws SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		Scheduler sch = mock(Scheduler.class);

		try (MockedStatic<SchedulerUtility> utility = mockStatic(SchedulerUtility.class)) {
			utility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
			doThrow(new SchedulerException()).when(sch).pauseJob(any(JobKey.class));
			String pauseJob = scheduleProcess.pauseJob("key");
			assertEquals(pauseJob, "pausejob");
		}
	}

	@Test
	public void testPauseAllJob_a1() {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		Scheduler sch = mock(Scheduler.class);
		String pauseAllJob = scheduleProcess.pauseAllJob(sch);
		assertEquals(pauseAllJob, "pausealljob");
	}

	@Test
	public void testPauseAllJob_a2() throws SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		Scheduler sch = mock(Scheduler.class);
		doThrow(new SchedulerException()).when(sch).pauseAll();
		String pauseAllJob = scheduleProcess.pauseAllJob(sch);
		assertEquals(pauseAllJob, "pausealljob");
	}

	@Test
	public void testUpdateTrigger_a1() {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		TriggerBuilder triggerBuilder = mock(TriggerBuilder.class);
		TriggerKey key = mock(TriggerKey.class);
		Trigger trigger = mock(Trigger.class);
		Scheduler sch = mock(Scheduler.class);
		MutableTrigger mutableTrigger = mock(MutableTrigger.class);
		CronScheduleBuilder builder = mock(CronScheduleBuilder.class);
		when(builder.inTimeZone(any(TimeZone.class))).thenReturn(builder);
		when(builder.build()).thenReturn(mutableTrigger);
		when(trigger.getTriggerBuilder()).thenReturn(triggerBuilder);
		when(triggerBuilder.withSchedule(builder)).thenReturn(triggerBuilder);
		when(trigger.getKey()).thenReturn(key);
		try (MockedConstruction<TriggerUtility> reader = mockConstruction(TriggerUtility.class, (mock, context) -> {
			when(mock.getInstance(anyString(), anyString(), any(Date.class), any(Date.class), anyString()))
					.thenReturn(trigger);
		})) {
			try(MockedStatic<CronScheduleBuilder> cron = mockStatic(CronScheduleBuilder.class)){
				try (MockedStatic<SchedulerUtility> utility = mockStatic(SchedulerUtility.class)) {
					utility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
				
				cron.when(() -> CronScheduleBuilder.cronSchedule(anyString())).thenReturn(builder);
			
			scheduleProcess.updateTrigger("previousCronExpression", "newCronExpression", "jobName", new Date(),
					new Date(), "Asia/Kolkata");
			}
		}
		}
	}
	@Test
	public void testUpdateTrigger_a2() throws SchedulerException {
		ScheduleProcess scheduleProcess = new ScheduleProcess();
		TriggerBuilder triggerBuilder = mock(TriggerBuilder.class);
		TriggerKey key = mock(TriggerKey.class);
		Trigger trigger = mock(Trigger.class);
		Scheduler sch = mock(Scheduler.class);
		MutableTrigger mutableTrigger = mock(MutableTrigger.class);
		CronScheduleBuilder builder = mock(CronScheduleBuilder.class);
		when(builder.inTimeZone(any(TimeZone.class))).thenReturn(builder);
		when(builder.build()).thenReturn(mutableTrigger);
		when(trigger.getTriggerBuilder()).thenReturn(triggerBuilder);
		when(triggerBuilder.withSchedule(builder)).thenReturn(triggerBuilder);
		when(trigger.getKey()).thenReturn(key);
		when(sch.rescheduleJob(any(TriggerKey.class), any())).thenThrow(new SchedulerException());
		try (MockedConstruction<TriggerUtility> reader = mockConstruction(TriggerUtility.class, (mock, context) -> {
			when(mock.getInstance(anyString(), anyString(), any(Date.class), any(Date.class), anyString()))
					.thenReturn(trigger);
		})) {
			try(MockedStatic<CronScheduleBuilder> cron = mockStatic(CronScheduleBuilder.class)){
				try (MockedStatic<SchedulerUtility> utility = mockStatic(SchedulerUtility.class)) {
					utility.when(() -> SchedulerUtility.getInstance()).thenReturn(sch);
				
				cron.when(() -> CronScheduleBuilder.cronSchedule(anyString())).thenReturn(builder);
			
			scheduleProcess.updateTrigger("previousCronExpression", "newCronExpression", "jobName", new Date(),
					new Date(), "Asia/Kolkata");
			}
		}
		}
	}
}
