package com.helicalinsight.scheduling;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;
import com.helicalinsight.scheduling.model.JobParameters;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.HiResourceService;
import com.helicalinsight.scheduling.service.JobParametersService;
import com.helicalinsight.scheduling.service.ResourceTypeService;
import com.helicalinsight.scheduling.service.SchedulesService;
import com.helicalinsight.scheduling.utils.ScheduleOperation;

public class ScheduleOperationTest {

	@Test
	public void testValidateTime_a1()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);

			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);

			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);

			operation.validateTime("2050-09-10 14:30:00", "2050-09-11 14:30:00", "Asia/Kolkata", "14:30:00",
					"14:30:00");
		}
	}

	@Test(expected = FormValidationException.class)
	public void testValidateTime_a2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);

			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);

			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);

			operation.validateTime("2023-09-10 14:30:00", "2023-09-11 14:30:00", "Asia/Kolkata", "14:30:00",
					"14:30:00");
		}
	}

	@Test(expected = FormValidationException.class)
	public void testValidateTime_a3()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);

			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);

			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);

			operation.validateTime("2050-09-08 14:30:00", "2050-08-10 14:30:00", "Asia/Kolkata", "14:30:00",
					"14:30:00");
		}
	}

	@Test(expected = FormValidationException.class)
	public void testValidateTime_a4()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);

			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);

			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);

			operation.validateTime("2050-09-08 14:30:00", "2023-08-10 14:30:00", "Asia/Kolkata", "14:30:00",
					"14:30:00");
		}
	}

	@Test
	public void testStringToDate()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);

			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);

			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);

			operation.stringToDate(new Date().toString(), "Asia/Kolkata", "14:30:00");
		}
	}

	@Test
	public void testEpochToDate_a1()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);

			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);

			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);
			JsonObject obj = new JsonObject();
			long currentTimeMillis = System.currentTimeMillis();
			obj.addProperty("time", currentTimeMillis);
			Date epochToDate = operation.epochToDate(obj);
			assertNotNull(epochToDate);

		}
	}

	@Test
	public void testEpochToDate_a2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);

			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);

			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);
			JsonArray jsonArray = new JsonArray();
			Date epochToDate = operation.epochToDate(jsonArray);
			assertNull(epochToDate);

		}
	}
//    @Test
//	public void testPrepareSchedulesEntity()
//			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
//				Principal principal = mock(Principal.class);
//
//				User loggedInUser = mock(User.class);
//				auth.when(() -> AuthenticationUtils.getUserDetails()).thenReturn(principal);
//				when(principal.getLoggedInUser()).thenReturn(loggedInUser);
//				ScheduleOperation operation = new ScheduleOperation();
//				ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
//				SchedulesService scheduleService = mock(SchedulesService.class);
//				HIResource HIResource = mock(HIResource.class);
//				Schedules schedules = mock(Schedules.class);
//				Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
//				field.setAccessible(true);
//				field.set(operation, resourceTypeService);
//
//				Field field1 = ScheduleOperation.class.getDeclaredField("scheduleService");
//				field1.setAccessible(true);
//				field1.set(operation, scheduleService);
//
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
//						.thenReturn(resourceTypeService);
//				JsonObject jsonObject = new JsonObject();
//				jsonObject.addProperty("id", 11);
//				jsonObject.addProperty("JobName", "JobName");
//				jsonObject.addProperty("isActive", true);
//				JsonObject ScheduleOptions = new JsonObject();
//
//				jsonObject.addProperty("ScheduleOptions", ScheduleOptions.toString());
//				JsonObject emailSettings = new JsonObject();
//				emailSettings.addProperty("Zip", true);
//				emailSettings.addProperty("Formats", "Formats");
//				emailSettings.addProperty("Recipients", "Recipients");
//				emailSettings.addProperty("Body", "Body");
//				emailSettings.addProperty("Subject", "Subject");
//				jsonObject.add("EmailSettings", emailSettings);
//				when(scheduleService.getSchedule(anyLong())).thenReturn(schedules);
//				operation.prepareSchedulesEntity(jsonObject, HIResource,"");
//			}
//		}
//	}

	@Test
	public void testPrepareSchedulesEntity_b1()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<FileUtils> fileUtils = mockStatic(FileUtils.class)) {

				try (MockedConstruction<Schedules> sch = mockConstruction(Schedules.class, (mock, context) -> {
				})) {
					fileUtils.when(() -> FileUtils.getExtension(anyString())).thenReturn("extension");

					User loggedInUser = mock(User.class);

					ScheduleOperation operation = new ScheduleOperation();
					ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
					UserService userService = mock(UserService.class);
					HIResource HIResource = mock(HIResource.class);
					Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
					field.setAccessible(true);
					field.set(operation, resourceTypeService);

					Field field1 = ScheduleOperation.class.getDeclaredField("userService");
					field1.setAccessible(true);
					field1.set(operation, userService);

					mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
							.thenReturn(resourceTypeService);
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("@id", 11);
					jsonObject.addProperty("JobName", "JobName");
					jsonObject.addProperty("isActive", true);
					jsonObject.addProperty("scheduleType", "scheduleType");
					JsonObject ScheduleOptions = new JsonObject();

					jsonObject.add("ScheduleOptions", ScheduleOptions);
					JsonObject emailSettings = new JsonObject();
					emailSettings.addProperty("Zip", true);
					emailSettings.addProperty("Formats", "Formats");
					emailSettings.addProperty("Recipients", "Recipients");
					emailSettings.addProperty("Body", "Body");
					emailSettings.addProperty("Subject", "Subject");
					JsonObject SchedulingJob = new JsonObject();
					SchedulingJob.addProperty("reportFile", "reportFile");
					SchedulingJob.add("EmailSettings", emailSettings);
					jsonObject.add("SchedulingJob", SchedulingJob);
					when(userService.findUser(anyInt())).thenReturn(loggedInUser);
					operation.prepareSchedulesEntity(jsonObject, HIResource, "11");
				}
			}
		}

	}

	@Test
	public void testPrepareSchedulesEntity_b2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<FileUtils> fileUtils = mockStatic(FileUtils.class)) {

				try (MockedConstruction<Schedules> sch = mockConstruction(Schedules.class, (mock, context) -> {
				})) {
					fileUtils.when(() -> FileUtils.getExtension(anyString())).thenReturn("extension");

					User loggedInUser = mock(User.class);

					ScheduleOperation operation = new ScheduleOperation();
					ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
					UserService userService = mock(UserService.class);
					HIResource HIResource = mock(HIResource.class);
					Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
					field.setAccessible(true);
					field.set(operation, resourceTypeService);

					Field field1 = ScheduleOperation.class.getDeclaredField("userService");
					field1.setAccessible(true);
					field1.set(operation, userService);

					mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
							.thenReturn(resourceTypeService);
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("id", 11);
					jsonObject.addProperty("JobName", "JobName");

					jsonObject.addProperty("scheduleType", "scheduleType");
					JsonObject ScheduleOptions = new JsonObject();
					ScheduleOptions.addProperty("EndAfterExecutions", 123345);
					ScheduleOptions.addProperty("StartDate", "2050-09-08 14:30:00");
					ScheduleOptions.addProperty("EndDate", "2050-09-08 14:30:00");

					jsonObject.add("ScheduleOptions", ScheduleOptions);
					JsonObject emailSettings = new JsonObject();
					emailSettings.addProperty("Zip", true);
					emailSettings.addProperty("Formats", "Formats");
					emailSettings.addProperty("Recipients", "Recipients");
					emailSettings.addProperty("Body", "Body");
					emailSettings.addProperty("Subject", "Subject");
					JsonObject SchedulingJob = new JsonObject();
					SchedulingJob.addProperty("reportFile", "reportFile");
					SchedulingJob.add("EmailSettings", emailSettings);
					jsonObject.add("SchedulingJob", SchedulingJob);
					when(userService.findUser(anyInt())).thenReturn(loggedInUser);
					operation.prepareSchedulesEntity(jsonObject, HIResource, "11");
				}
			}
		}

	}

	@Test
	public void testPrepareJsonToDate_a1() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);

			Method method = ScheduleOperation.class.getDeclaredMethod("prepareJsonToDate", Object.class);
			method.setAccessible(true);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("year", 2050);
			jsonObject.addProperty("month", 8);
			jsonObject.addProperty("date", 20);
			jsonObject.addProperty("hours", 10);
			jsonObject.addProperty("minutes", 30);
			jsonObject.addProperty("seconds", 30);
			Object invoke = method.invoke(operation, jsonObject);
			assertNotNull(invoke);
		}
	}

	@Test
	public void testPrepareJsonToDate_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);

			Method method = ScheduleOperation.class.getDeclaredMethod("prepareJsonToDate", Object.class);
			method.setAccessible(true);
			Object invoke = method.invoke(operation, new JsonArray());
			assertNull(invoke);
		}
	}

	//@Test
	public void testDeleteAllAssociatedHiResource() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
			HIResourceServiceDB hiResourceService = mock(HIResourceServiceDB.class);
			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);

			Field field1 = ScheduleOperation.class.getDeclaredField("hiResourceService");
			field1.setAccessible(true);
			field1.set(operation, hiResourceService);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);

			Method method = ScheduleOperation.class.getDeclaredMethod("deleteAllAssociatedHiResource", Long.class);
			method.setAccessible(true);
			method.invoke(operation, 11);
		}
	}

	@Test
	public void testDeleteScheduleEntity_a1()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
			SchedulesService scheduleService = mock(SchedulesService.class);
			JobParametersService jobParametersService = mock(JobParametersService.class);
			HiResourceService hiResourceService = mock(HiResourceService.class);
			Schedules schedule = mock(Schedules.class);
			HIResource resource = mock(HIResource.class);
			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);

			Field field1 = ScheduleOperation.class.getDeclaredField("scheduleService");
			field1.setAccessible(true);
			field1.set(operation, scheduleService);

			Field field2 = ScheduleOperation.class.getDeclaredField("jobParametersService");
			field2.setAccessible(true);
			field2.set(operation, jobParametersService);

			Field field3 = ScheduleOperation.class.getDeclaredField("hiResourceService");
			field3.setAccessible(true);
			field3.set(operation, hiResourceService);

			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);
			when(scheduleService.getSchedule(anyLong())).thenReturn(schedule);
			when(schedule.getHIResource()).thenReturn(resource);
			when(resource.getResourceId()).thenReturn(11);
			Integer id = 11;
			operation.deleteScheduleEntity(id.toString());
		}
	}

	@Test
	public void testDeleteScheduleEntity_a2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
			SchedulesService scheduleService = mock(SchedulesService.class);
			JobParametersService jobParametersService = mock(JobParametersService.class);
			Schedules schedule = mock(Schedules.class);
			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);

			Field field1 = ScheduleOperation.class.getDeclaredField("scheduleService");
			field1.setAccessible(true);
			field1.set(operation, scheduleService);

			Field field2 = ScheduleOperation.class.getDeclaredField("jobParametersService");
			field2.setAccessible(true);
			field2.set(operation, jobParametersService);

			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);
			when(scheduleService.getSchedule(anyLong())).thenReturn(schedule);
			when(schedule.getHIResource()).thenReturn(null);

			Integer id  = 11;
			operation.deleteScheduleEntity(id.toString());
		}
	}

	@Test
	public void testPrepareHiResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
			HIResourceServiceDB hiResourceService = mock(HIResourceServiceDB.class);
			Efwsr efwsr = mock(Efwsr.class);
			HIResource eachDirResource = mock(HIResource.class);
			

			Field field3 = ScheduleOperation.class.getDeclaredField("serviceDb");
			field3.setAccessible(true);
			field3.set(operation, hiResourceService);

			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);
			when(efwsr.getReportFile()).thenReturn("reportFile");
			when(efwsr.getReportDirectory()).thenReturn("Temp");

			when(hiResourceService.getResourceByUrl(anyString())).thenReturn(eachDirResource);
			when(eachDirResource.getResourceId()).thenReturn(11);
			when(hiResourceService.getResourceByUrl(anyString())).thenReturn(eachDirResource);
			operation.prepareHiResource(efwsr);
		}
	}

	@Test
	public void testPrepareHiResource_b1()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			JsonObject json = new JsonObject();
			JsonObject SchedulingJob = new JsonObject();
			SchedulingJob.addProperty("reportDirectory", "Temp");
			json.add("SchedulingJob", SchedulingJob);

			HIResource prepareHiResource = operation.prepareHiResource(json);
			assertNull(prepareHiResource);
		}

	}

	@Test
	public void testPrepareHiResource_b2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
				try (MockedConstruction<HIResource> hi = mockConstruction(HIResource.class)) {
					ScheduleOperation operation = new ScheduleOperation();
					ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
					HIResourceServiceDB hiResourceService = mock(HIResourceServiceDB.class);
					
					
					Field field3 = ScheduleOperation.class.getDeclaredField("serviceDb");
					field3.setAccessible(true);
					field3.set(operation, hiResourceService);

					mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
							.thenReturn(resourceTypeService);
					auth.when(() -> AuthenticationUtils.getUserId(any(JsonObject.class))).thenReturn("123");
					JsonObject json = new JsonObject();
					JsonObject SchedulingJob = new JsonObject();
					SchedulingJob.addProperty("reportDirectory", "Temp");
					SchedulingJob.addProperty("reportFile", "reportFile");

					json.add("SchedulingJob", SchedulingJob);
					when(hiResourceService.getResourceByUrl(anyString())).thenReturn(null);

					operation.prepareHiResource(json);
				}
			}
		}
	}

	@Test
	public void testPrepareHiResource_b3()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
				try (MockedConstruction<HIResource> hi = mockConstruction(HIResource.class)) {
					ScheduleOperation operation = new ScheduleOperation();
					ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
					HIResourceServiceDB hiResourceService = mock(HIResourceServiceDB.class);
					HIResource eachDirResource = mock(HIResource.class);
					

					Field field3 = ScheduleOperation.class.getDeclaredField("serviceDb");
					field3.setAccessible(true);
					field3.set(operation, hiResourceService);

					mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
							.thenReturn(resourceTypeService);
					auth.when(() -> AuthenticationUtils.getUserId(any(JsonObject.class))).thenReturn("123");
					when(hiResourceService.getResourceByUrl(anyString())).thenReturn(eachDirResource);
					when(eachDirResource.getResourceId()).thenReturn(11);
					when(hiResourceService.getResourceByUrl(anyString())).thenReturn(eachDirResource);
					JsonObject json = new JsonObject();
					JsonObject SchedulingJob = new JsonObject();
					SchedulingJob.addProperty("reportDirectory", "Temp");
					SchedulingJob.addProperty("reportFile", "reportFile");

					json.add("SchedulingJob", SchedulingJob);

					operation.prepareHiResource(json);
				}
			}
		}
	}

	@Test
	public void testSaveJobParameters()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
			JobParametersService jobParametersService = mock(JobParametersService.class);

			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);

			Field field2 = ScheduleOperation.class.getDeclaredField("jobParametersService");
			field2.setAccessible(true);
			field2.set(operation, jobParametersService);

			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);
			JsonObject reportParametersJson = new JsonObject();

			reportParametersJson.addProperty("key1", "value1");
			reportParametersJson.addProperty("printOptions", "value2");
			operation.saveJobParameters(new Schedules(), reportParametersJson, true);
		}
	}

	@Test
	public void testPrepareSecurityJson() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
			JobParametersService jobParametersService = mock(JobParametersService.class);
			Schedules schedules = mock(Schedules.class);
			User user = mock(User.class);
			Organization organization = mock(Organization.class);
			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);
			Method method = ScheduleOperation.class.getDeclaredMethod("prepareSecurityJson", Schedules.class);
			method.setAccessible(true);
			when(user.getOrganization()).thenReturn(organization);
			when(organization.getId()).thenReturn(11);
			when(schedules.getCreatedBy()).thenReturn(user);
			method.invoke(operation, schedules);
		}
	}

	@Test
	public void testPrepareReportParameters_a1()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
			Schedules schedules = mock(Schedules.class);
			SchedulesService schedulesService = mock(SchedulesService.class);
			JobParameters jobParameters = mock(JobParameters.class);
			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(SchedulesService.class))
					.thenReturn(schedulesService);
			when(schedules.getId()).thenReturn(11l);
			List<JobParameters> listOfAllJobParameters = new ArrayList<>();
			listOfAllJobParameters.add(jobParameters);
			when(jobParameters.getType()).thenReturn("Integer");
			when(jobParameters.getKey()).thenReturn("key");
			when(jobParameters.getValue()).thenReturn("11");

			when(schedulesService.getAllJobParametersById(anyLong())).thenReturn(listOfAllJobParameters);

			operation.prepareReportParameters(schedules);
		}
	}

	@Test
	public void testPrepareReportParameters_a2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
			Schedules schedules = mock(Schedules.class);
			SchedulesService schedulesService = mock(SchedulesService.class);
			JobParameters jobParameters = mock(JobParameters.class);
			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
					.thenReturn(resourceTypeService);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(SchedulesService.class))
					.thenReturn(schedulesService);
			when(schedules.getId()).thenReturn(11l);
			List<JobParameters> listOfAllJobParameters = new ArrayList<>();
			listOfAllJobParameters.add(jobParameters);
			when(jobParameters.getType()).thenReturn("string");
			when(jobParameters.getKey()).thenReturn("key");
			when(jobParameters.getValue()).thenReturn("11");

			when(schedulesService.getAllJobParametersById(anyLong())).thenReturn(listOfAllJobParameters);

			operation.prepareReportParameters(schedules);
		}
	}

	@Test
	public void testUpdateScheduleStatus()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
			Schedules schedules = mock(Schedules.class);
			SchedulesService scheduleService = mock(SchedulesService.class);
			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);
			Field field1 = ScheduleOperation.class.getDeclaredField("scheduleService");
			field1.setAccessible(true);
			field1.set(operation, scheduleService);

			when(scheduleService.getSchedule(anyLong())).thenReturn(schedules);
			JsonObject json = new JsonObject();
			JsonObject ScheduleOptions = new JsonObject();
			ScheduleOptions.addProperty("EndAfterExecutions", "11");

			json.add("ScheduleOptions", ScheduleOptions);
			json.addProperty("NoOfExecutions", "11");
			;
			Map<String, Object> updatedScheduleStatusJson = new HashMap<>();
			updatedScheduleStatusJson.put("NoOfExecutions", 12345);
			updatedScheduleStatusJson.put("NextExecutionOn", new Date());
			updatedScheduleStatusJson.put("LastExecutedOn", new Date());
			updatedScheduleStatusJson.put("LastExecutionStatus", "1");

			// TODO : Fix sheculeOperation class
//			operation.updateScheduleStatus(11l, updatedScheduleStatusJson, json);

		}
	}

	@Test
	public void testPrepareScheduleEntityJson()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
			SchedulesService scheduleService = mock(SchedulesService.class);
			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);
			Field field1 = ScheduleOperation.class.getDeclaredField("scheduleService");
			field1.setAccessible(true);
			field1.set(operation, scheduleService);
			when(scheduleService.getSchedule(anyLong())).thenReturn(null);
			operation.prepareScheduleEntityJson(11);
		}
	}

	@Test
	public void testTriggerMigrationProcessForScheduling_a1()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {

				ScheduleOperation operation = new ScheduleOperation();
				ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
				SchedulesService scheduleService = mock(SchedulesService.class);
				Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
				field.setAccessible(true);
				field.set(operation, resourceTypeService);
				Field field1 = ScheduleOperation.class.getDeclaredField("scheduleService");
				field1.setAccessible(true);
				field1.set(operation, scheduleService);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
						.thenReturn(resourceTypeService);

				jsonUtils.when(() -> JsonUtils.isSchedulingXMLExists()).thenReturn(true);
				operation.triggerMigrationProcessForScheduling(true);
			}
		}
	}

	@Test
	public void testTriggerMigrationProcessForScheduling_a2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {

				ScheduleOperation operation = new ScheduleOperation();
				ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
				SchedulesService scheduleService = mock(SchedulesService.class);
				Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
				field.setAccessible(true);
				field.set(operation, resourceTypeService);
				Field field1 = ScheduleOperation.class.getDeclaredField("scheduleService");
				field1.setAccessible(true);
				field1.set(operation, scheduleService);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
						.thenReturn(resourceTypeService);

				jsonUtils.when(() -> JsonUtils.isSchedulingXMLExists()).thenThrow(new RuntimeException());
				operation.triggerMigrationProcessForScheduling(true);
			}
		}
	}

	// TODO :  Revisit this ( Error : Unparseable date: "Tue Jan 09 11:11:37 IST 2024 14:30:00" )
//	@Test(expected = InvocationTargetException.class)
	public void testMigrateSchedulingXmlToDatabase()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {
				try (MockedConstruction<PropertiesFileReader> con = mockConstruction(PropertiesFileReader.class,
						(mock, context) -> {
							Map<String, String> hashMap = new HashMap<>();
							hashMap.put("schedulerPath",
									"/home/helical/Performance/hi/hi-repository/System/Admin/Validation/scheduling.xml");
							when(mock.read(anyString())).thenReturn(hashMap);
						})) {
					ScheduleOperation operation = new ScheduleOperation();
					IProcessor processor = mock(IProcessor.class);
					ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
					SchedulesService scheduleService = mock(SchedulesService.class);
					Schedules existingSchedule = mock(Schedules.class);
					Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
					field.setAccessible(true);
					field.set(operation, resourceTypeService);
					Field field1 = ScheduleOperation.class.getDeclaredField("scheduleService");
					field1.setAccessible(true);
					field1.set(operation, scheduleService);

					mockedStatic.when(() -> ApplicationContextAccessor.getBean(ResourceTypeService.class))
							.thenReturn(resourceTypeService);
					factory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
					JsonArray schedulesJsonArray = new JsonArray();
					 JsonObject eachJson =  new JsonObject();
					 Integer id  = 11;
					 eachJson.addProperty("@id", id);
					schedulesJsonArray.add(eachJson);
					when(processor.getJsonArray(anyString(), anyBoolean())).thenReturn(schedulesJsonArray);
					when(scheduleService.getSchedule(anyLong())).thenReturn(existingSchedule);
					when(existingSchedule.getIsMigrated()).thenReturn(false);
					Method method = ScheduleOperation.class.getDeclaredMethod("migrateSchedulingXmlToDatabase");
					method.setAccessible(true);
					method.invoke(operation);
				}
			}

		}
	}
	@Test
	public void testPrepareScheduleEntityFromJson() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation operation = new ScheduleOperation();
			SchedulesService scheduleService = mock(SchedulesService.class);
			JobParametersService jobParametersService = mock(JobParametersService.class);
			ResourceTypeService resourceTypeService = mock(ResourceTypeService.class);
			Field field = ScheduleOperation.class.getDeclaredField("resourceTypeService");
			field.setAccessible(true);
			field.set(operation, resourceTypeService);
			Field field1 = ScheduleOperation.class.getDeclaredField("scheduleService");
			field1.setAccessible(true);
			field1.set(operation, scheduleService);
			Field field2 = ScheduleOperation.class.getDeclaredField("jobParametersService");
			field2.setAccessible(true);
			field2.set(operation, jobParametersService);
			JsonObject json = new JsonObject();
			JsonObject SchedulingJob = new JsonObject();
			JsonObject emailSettings = new JsonObject();
			emailSettings.addProperty("Zip", true);
			emailSettings.addProperty("Formats", "Formats");
			emailSettings.addProperty("Recipients", "Recipients");
			emailSettings.addProperty("Body", "Body");
			emailSettings.addProperty("Subject", "Subject");
			JsonObject reportParametersJson = new JsonObject();
			reportParametersJson.addProperty("key1", "value1");
			reportParametersJson.addProperty("printOptions", "value2");
			SchedulingJob.add("reportParameters", reportParametersJson);
			SchedulingJob.add("EmailSettings", emailSettings);
			SchedulingJob.addProperty("reportDirectory", "Temp");
			json.add("ScheduleOptions",new JsonObject());
			json.add("SchedulingJob", SchedulingJob);
			
			operation.prepareScheduleEntityFromJson(json);
		}
	}
	
}
