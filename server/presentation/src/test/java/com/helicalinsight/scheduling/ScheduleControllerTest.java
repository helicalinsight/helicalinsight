package com.helicalinsight.scheduling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.rules.RulesUtils;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.scheduling.model.HiResource;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.JobParametersService;
import com.helicalinsight.scheduling.service.SchedulesService;
import com.helicalinsight.scheduling.utils.ScheduleOperation;
import com.helicalinsight.scheduling.utils.SchedulerUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ScheduleControllerTest {

//	@Test
	public void testGetScheduleData_a1() throws IOException {
		ScheduleController controller = new ScheduleController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(request.getParameter("id")).thenReturn("11");
		try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {
			String scheduleData = controller.getScheduleData(request, response);
			assertNull(scheduleData);
		}

	}
	// TODO : Revisit this ( Error: Unparseable date: "Tue Jan 09 11:12:48 IST 2024")
//	@Test
	public void testGetScheduleData_a2() throws IOException {
		ScheduleController controller = new ScheduleController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		IProcessor processor = mock(IProcessor.class);
		JsonObject resultObject = new JsonObject();
		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(resultObject);
		when(request.getParameter("id")).thenReturn("11");
		Map<String, String> map = new HashMap<>();
		map.put("schedulerPath", "/home/helical/Performance/hi/hi-repository/System/Admin/Validation/scheduling.xml");
		try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {
			try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {
				try (MockedConstruction<PropertiesFileReader> reader = mockConstruction(PropertiesFileReader.class,
						(mock, context) -> {
							when(mock.read("project.properties")).thenReturn(map);
						})) {
					try (MockedConstruction<XmlOperation> xml = mockConstruction(XmlOperation.class,
							(mock, context) -> {
								when(mock.searchId(any(JsonObject.class), anyString())).thenReturn(true);
								when(mock.getParticularObject(anyString(), anyString())).thenReturn(resultObject);

							})) {

						factory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
						String scheduleData = controller.getScheduleData(request, response);
						assertNotNull(scheduleData);
					}
				}
			}
		}
	}

	@Test
	public void testGetScheduleData_a3() throws IOException {
		ScheduleController controller = new ScheduleController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		IProcessor processor = mock(IProcessor.class);
		JsonObject resultObject = new JsonObject();
		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(resultObject);
		when(request.getParameter("id")).thenReturn("11");
		Map<String, String> map = new HashMap<>();
		map.put("schedulerPath", "/home/helical/Performance/hi/hi-repository/System/Admin/Validation/scheduling.xml");
		try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {
			try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {
				try (MockedConstruction<PropertiesFileReader> reader = mockConstruction(PropertiesFileReader.class,
						(mock, context) -> {
							when(mock.read("project.properties")).thenReturn(map);
						})) {
					try (MockedConstruction<XmlOperation> xml = mockConstruction(XmlOperation.class,
							(mock, context) -> {
								when(mock.searchId(any(JsonObject.class), anyString())).thenReturn(false);

							})) {

						factory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
						String scheduleData = controller.getScheduleData(request, response);
						assertNull(scheduleData);
					}
				}
			}
		}
	}

//	@Test
	public void testUpdateScheduleData_a1() throws IOException {
		ScheduleController controller = new ScheduleController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {
			String updateScheduleData = controller.updateScheduleData(request, response);
			assertNull(updateScheduleData);
		}
	}

	@Test
	public void testUpdateScheduleData_a2() throws IOException {
		ScheduleController controller = new ScheduleController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		try (MockedStatic<JsonUtils> json = mockStatic(JsonUtils.class)) {
			try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {
				json.when(() -> JsonUtils.newIsScheduleStorageTypeIsDatabase()).thenReturn(false);
				String updateScheduleData = controller.updateScheduleData(request, response);
				assertNull(updateScheduleData);
			}
		}
	}

//	@Test
	public void testDeleteSchedule() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		ScheduleController controller = new ScheduleController();
		HttpServletResponse response = mock(HttpServletResponse.class);

		Method method = ScheduleController.class.getDeclaredMethod("deleteSchedule", String.class,
				HttpServletResponse.class);
		method.setAccessible(true);
		Object invoke = method.invoke(controller, "11", response);
		assertEquals(invoke, "schedule deleted successfully..");

	}

	@Test
	public void testDeleteScheduleInDb() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		ScheduleController controller = new ScheduleController();
		HttpServletResponse response = mock(HttpServletResponse.class);
		SchedulesService schedulesService = mock(SchedulesService.class);
		Field field = ScheduleController.class.getDeclaredField("schedulesService");
		field.setAccessible(true);
		field.set(controller, schedulesService);
		Method method = ScheduleController.class.getDeclaredMethod("deleteScheduleInDb", String.class,
				HttpServletResponse.class);
		method.setAccessible(true);

		Object invoke = method.invoke(controller, "11", response);
		assertEquals(invoke, "schedule deleted successfully..;");

	}

	@Test
	public void testPrepareScheduleJson_a1() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		ScheduleController controller = new ScheduleController();
		Method method = ScheduleController.class.getDeclaredMethod("prepareScheduleJson", HttpServletRequest.class,
				ScheduleOperation.class);
		method.setAccessible(true);
		HttpServletRequest request = mock(HttpServletRequest.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		when(request.getParameter("id")).thenReturn("11");
		try (MockedStatic<FileUtils> file = mockStatic(FileUtils.class)) {
			try (MockedStatic<SchedulerUtils> utils = mockStatic(SchedulerUtils.class)) {

				utils.when(() -> SchedulerUtils.prepareJsonFromUserData(null, null, null, null, null, null, null, null))
						.thenReturn(new JsonObject());
				file.when(() -> FileUtils.getExtension(null)).thenReturn("extension");

				Object invoke = method.invoke(controller, request, scheduleOperation);
				assertNotNull(invoke);
			}
		}
	}

	@Test(expected = InvocationTargetException.class)
	public void testPrepareScheduleJson_a2() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		ScheduleController controller = new ScheduleController();
		Method method = ScheduleController.class.getDeclaredMethod("prepareScheduleJson", HttpServletRequest.class,
				ScheduleOperation.class);
		method.setAccessible(true);
		HttpServletRequest request = mock(HttpServletRequest.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		when(request.getParameter("id")).thenReturn("11");
		when(request.getParameter("ScheduleOptions")).thenReturn("schedule");
		method.invoke(controller, request, scheduleOperation);

	}

	@Test(expected = InvocationTargetException.class)
	public void testPrepareScheduleJson_a3() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		ScheduleController controller = new ScheduleController();
		Method method = ScheduleController.class.getDeclaredMethod("prepareScheduleJson", HttpServletRequest.class,
				ScheduleOperation.class);
		method.setAccessible(true);
		HttpServletRequest request = mock(HttpServletRequest.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		method.invoke(controller, request, scheduleOperation);

	}

	@Test
	public void testUpdateScheduleInDb_a1() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		ScheduleController controller = new ScheduleController();
		Method method = ScheduleController.class.getDeclaredMethod("updateScheduleInDb", HttpServletRequest.class,
				HttpServletResponse.class);
		method.setAccessible(true);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		SchedulesService schedulesService = mock(SchedulesService.class);
		Principal principal = mock(Principal.class);
		Schedules schedule = mock(Schedules.class);
		JobParametersService jobParametersService = mock(JobParametersService.class);
		User user = null;
		Field field = ScheduleController.class.getDeclaredField("schedulesService");
		field.setAccessible(true);
		field.set(controller, schedulesService);
		Field field1 = ScheduleController.class.getDeclaredField("jobParametersService");
		field1.setAccessible(true);
		field1.set(controller, jobParametersService);
		JsonObject scheduleEntityJson = new JsonObject();
		JsonObject existingScheduleOption = new JsonObject();
		existingScheduleOption.addProperty("StartDate", "2023-09-08");
		existingScheduleOption.addProperty("EndDate", "2023-09-10");
		existingScheduleOption.addProperty("timeZone", "Asia/Kolkata");
		existingScheduleOption.addProperty("endsRadio", "on");

		scheduleEntityJson.add("ScheduleOptions", existingScheduleOption);
		when(schedulesService.getSchedule(anyLong())).thenReturn(schedule);
		when(schedulesService.getSchedule(null)).thenReturn(schedule);
		when(request.getParameter("id")).thenReturn("11");
		when(schedule.getHIResource()).thenReturn(new HIResource());
		when(scheduleOperation.prepareSchedulesEntityToJSON(schedule)).thenReturn(scheduleEntityJson);
		when(scheduleOperation.prepareSchedulesEntity(any(JsonObject.class), any(HIResource.class)))
				.thenReturn(schedule);
		try (MockedStatic<FileUtils> file = mockStatic(FileUtils.class)) {
			try (MockedStatic<SchedulerUtils> utils = mockStatic(SchedulerUtils.class)) {
				try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
					try (MockedStatic<RulesUtils> rules = mockStatic(RulesUtils.class)) {
						try (MockedStatic<ControllerUtils> conUtils = mockStatic(ControllerUtils.class)) {

							try (MockedStatic<ApplicationContextAccessor> app = mockStatic(
									ApplicationContextAccessor.class)) {
								rules.when(() -> RulesUtils.validateUser(principal, user)).thenReturn(true);
								auth.when(() -> AuthenticationUtils.getUserDetails()).thenReturn(principal);
								app.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
										.thenReturn(scheduleOperation);
								JsonObject jsonObject = new JsonObject();
								jsonObject.add("reportParameters", new JsonObject());
								utils.when(() -> SchedulerUtils.prepareJsonFromUserData(null, null, null, null, null,
										null, null, null)).thenReturn(new JsonObject());
								file.when(() -> FileUtils.getExtension(null)).thenReturn("extension");

								Object invoke = method.invoke(controller, request, response);
								assertNull(invoke);
							}
						}
					}
				}
			}
		}

	}

	@Test
	public void testUpdateScheduleInDb_a2() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		ScheduleController controller = new ScheduleController();
		Method method = ScheduleController.class.getDeclaredMethod("updateScheduleInDb", HttpServletRequest.class,
				HttpServletResponse.class);
		method.setAccessible(true);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		SchedulesService schedulesService = mock(SchedulesService.class);
		Principal principal = mock(Principal.class);
		Schedules schedule = mock(Schedules.class);
		JobParametersService jobParametersService = mock(JobParametersService.class);
		User user = null;
		Field field = ScheduleController.class.getDeclaredField("schedulesService");
		field.setAccessible(true);
		field.set(controller, schedulesService);
		Field field1 = ScheduleController.class.getDeclaredField("jobParametersService");
		field1.setAccessible(true);
		field1.set(controller, jobParametersService);
		JsonObject scheduleEntityJson = new JsonObject();
		JsonObject existingScheduleOption = new JsonObject();
		existingScheduleOption.addProperty("StartDate", new Date().toString());
		existingScheduleOption.addProperty("EndDate", new Date().toString());
		existingScheduleOption.addProperty("timeZone", "Asia/Kolkata");
		existingScheduleOption.addProperty("endsRadio", "on");

		scheduleEntityJson.add("ScheduleOptions", existingScheduleOption);
		when(schedulesService.getSchedule(anyLong())).thenReturn(schedule);
		when(schedulesService.getSchedule(null)).thenReturn(schedule);
		when(request.getParameter("id")).thenReturn("11");
		when(schedule.getHIResource()).thenReturn(new HIResource());
		when(scheduleOperation.prepareSchedulesEntityToJSON(schedule)).thenReturn(scheduleEntityJson);
		when(scheduleOperation.prepareSchedulesEntity(any(JsonObject.class), any(HIResource.class)))
				.thenReturn(schedule);
		try (MockedStatic<FileUtils> file = mockStatic(FileUtils.class)) {
			try (MockedStatic<SchedulerUtils> utils = mockStatic(SchedulerUtils.class)) {
				try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
					try (MockedStatic<RulesUtils> rules = mockStatic(RulesUtils.class)) {
						try (MockedStatic<ControllerUtils> conUtils = mockStatic(ControllerUtils.class)) {

							try (MockedStatic<ApplicationContextAccessor> app = mockStatic(
									ApplicationContextAccessor.class)) {
								rules.when(() -> RulesUtils.validateUser(principal, user)).thenReturn(true);
								auth.when(() -> AuthenticationUtils.getUserDetails()).thenReturn(principal);
								app.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
										.thenReturn(scheduleOperation);
								JsonObject jsonObject = new JsonObject();
								jsonObject.add("reportParameters", new JsonObject());
								utils.when(() -> SchedulerUtils.prepareJsonFromUserData(null, null, null, null, null,
										null, null, null)).thenReturn(new JsonObject());
								file.when(() -> FileUtils.getExtension(null)).thenReturn("extension");

								Object invoke = method.invoke(controller, request, response);
								assertNull(invoke);
							}
						}
					}
				}
			}
		}

	}

	@Test
	public void testUpdateScheduleInDb_a3() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		ScheduleController controller = new ScheduleController();
		Method method = ScheduleController.class.getDeclaredMethod("updateScheduleInDb", HttpServletRequest.class,
				HttpServletResponse.class);
		method.setAccessible(true);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		SchedulesService schedulesService = mock(SchedulesService.class);
		Principal principal = mock(Principal.class);
		Schedules schedule = mock(Schedules.class);
		User user = null;
		Field field = ScheduleController.class.getDeclaredField("schedulesService");
		field.setAccessible(true);
		field.set(controller, schedulesService);

		when(schedulesService.getSchedule(anyLong())).thenReturn(schedule);
		when(schedulesService.getSchedule(null)).thenReturn(schedule);
		when(request.getParameter("id")).thenReturn("11");
		try (MockedStatic<FileUtils> file = mockStatic(FileUtils.class)) {
			try (MockedStatic<SchedulerUtils> utils = mockStatic(SchedulerUtils.class)) {
				try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
					try (MockedStatic<RulesUtils> rules = mockStatic(RulesUtils.class)) {
						try (MockedStatic<ControllerUtils> conUtils = mockStatic(ControllerUtils.class)) {

							try (MockedStatic<ApplicationContextAccessor> app = mockStatic(
									ApplicationContextAccessor.class)) {
								rules.when(() -> RulesUtils.validateUser(principal, user)).thenReturn(false);
								auth.when(() -> AuthenticationUtils.getUserDetails()).thenReturn(principal);
								app.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
										.thenReturn(scheduleOperation);
								JsonObject jsonObject = new JsonObject();
								jsonObject.add("reportParameters", new JsonObject());
								utils.when(() -> SchedulerUtils.prepareJsonFromUserData(null, null, null, null, null,
										null, null, null)).thenReturn(new JsonObject());
								file.when(() -> FileUtils.getExtension(null)).thenReturn("extension");

								Object invoke = method.invoke(controller, request, response);
								assertNull(invoke);
							}
						}
					}
				}
			}
		}

	}

	@Test
	public void testUpdateScheduleInDb_a4() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		ScheduleController controller = new ScheduleController();
		Method method = ScheduleController.class.getDeclaredMethod("updateScheduleInDb", HttpServletRequest.class,
				HttpServletResponse.class);
		method.setAccessible(true);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		SchedulesService schedulesService = mock(SchedulesService.class);
		Principal principal = mock(Principal.class);
		Schedules schedule = mock(Schedules.class);
		User user = null;
		Field field = ScheduleController.class.getDeclaredField("schedulesService");
		field.setAccessible(true);
		field.set(controller, schedulesService);

		when(schedulesService.getSchedule(anyLong())).thenReturn(null);
		when(schedulesService.getSchedule(null)).thenReturn(schedule);
		when(request.getParameter("id")).thenReturn("11");
		try (MockedStatic<FileUtils> file = mockStatic(FileUtils.class)) {
			try (MockedStatic<SchedulerUtils> utils = mockStatic(SchedulerUtils.class)) {
				try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
					try (MockedStatic<RulesUtils> rules = mockStatic(RulesUtils.class)) {
						try (MockedStatic<ControllerUtils> conUtils = mockStatic(ControllerUtils.class)) {

							try (MockedStatic<ApplicationContextAccessor> app = mockStatic(
									ApplicationContextAccessor.class)) {
								rules.when(() -> RulesUtils.validateUser(principal, user)).thenReturn(false);
								auth.when(() -> AuthenticationUtils.getUserDetails()).thenReturn(principal);
								app.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
										.thenReturn(scheduleOperation);
								JsonObject jsonObject = new JsonObject();
								jsonObject.add("reportParameters", new JsonObject());
								utils.when(() -> SchedulerUtils.prepareJsonFromUserData(null, null, null, null, null,
										null, null, null)).thenReturn(new JsonObject());
								file.when(() -> FileUtils.getExtension(null)).thenReturn("extension");

								Object invoke = method.invoke(controller, request, response);
								assertNull(invoke);
							}
						}
					}
				}
			}
		}

	}

	@Test(expected = InvocationTargetException.class)
	public void testValidateEmailAndGetEmail() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		ScheduleController controller = new ScheduleController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		JsonObject object = new JsonObject();
		object.addProperty("Recipients", "[]");
		Method method = ScheduleController.class.getDeclaredMethod("validateEmailAndGetEmail", HttpServletRequest.class,
				String.class);
		method.setAccessible(true);
		method.invoke(controller, request, object.toString());

	}

	@Test
	public void testIsValidUser() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		ScheduleController controller = new ScheduleController();
		Method method = ScheduleController.class.getDeclaredMethod("isValidUser", String.class, String.class);
		method.setAccessible(true);
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("security", new JsonObject());
		Principal principal = mock(Principal.class);

		try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
			try (MockedStatic<RulesUtils> rules = mockStatic(RulesUtils.class)) {
				try (MockedConstruction<XmlOperation> construction = mockConstruction(XmlOperation.class,
						(mock, context) -> {
							when(mock.getParticularObject("path", "11")).thenReturn(jsonObject);
						})) {
					rules.when(() -> RulesUtils.validateUser(any(Principal.class), any(JsonObject.class))).thenReturn(true);
					auth.when(() -> AuthenticationUtils.getUserDetails()).thenReturn(principal);

					method.invoke(controller, "11", "path");
				}
			}
		}

	}
	

}
