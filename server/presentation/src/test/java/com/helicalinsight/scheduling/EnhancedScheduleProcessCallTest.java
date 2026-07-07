package com.helicalinsight.scheduling;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.utils.ScheduleOperation;

public class EnhancedScheduleProcessCallTest {

	@Test
	public void testScheduleOperation_a1()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {

			EnhancedScheduleProcessCall enhancedScheduleProcessCall = new EnhancedScheduleProcessCall();
			ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);

			Schedules eachSchedule = mock(Schedules.class);
			when(eachSchedule.getIsActive()).thenReturn(true);
			when(eachSchedule.getScheduleId()).thenReturn(11l);
			List<Schedules> listOfScheduleJobs = new ArrayList<>();
			listOfScheduleJobs.add(eachSchedule);

			String baseUrl = "hi.html";
			UserService userService = mock(UserService.class);
			JsonObject eachScheduleJson = new JsonObject();
			JsonObject scheduleOptions = new JsonObject();
			scheduleOptions.addProperty("EndAfterExecutions", "2");
			eachScheduleJson.addProperty("NoOfExecutions", "3");
			eachScheduleJson.add("ScheduleOptions", scheduleOptions);
			when(scheduleOperation.prepareSchedulesEntityToJSON(eachSchedule)).thenReturn(eachScheduleJson);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
					.thenReturn(scheduleOperation);
			Field field = EnhancedScheduleProcessCall.class.getDeclaredField("scheduleOperation");
			field.setAccessible(true);
			field.set(enhancedScheduleProcessCall, scheduleOperation);

			enhancedScheduleProcessCall.scheduleOperation(listOfScheduleJobs, baseUrl, userService);
		}
	}

	@Test
	public void testScheduleOperation_a2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<AuthenticationUtils> authenticationUtils = mockStatic(AuthenticationUtils.class)) {
				try (MockedConstruction<ScheduleProcess> schedule = mockConstruction(ScheduleProcess.class)) {
					try (MockedConstruction<ConvertIntoCronExpression> cron = mockConstruction(
							ConvertIntoCronExpression.class)) {
						ConvertIntoCronExpression cronExpression = new ConvertIntoCronExpression();
						doReturn("30 30 12 */1 2-3 ? 37-7").when(cronExpression)
								.convertDateIntoCronExpression(any(JsonObject.class));
						EnhancedScheduleProcessCall enhancedScheduleProcessCall = new EnhancedScheduleProcessCall();
						ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);

						Schedules eachSchedule = mock(Schedules.class);
						when(eachSchedule.getIsActive()).thenReturn(true);
						when(eachSchedule.getScheduleId()).thenReturn(11l);
						List<Schedules> listOfScheduleJobs = new ArrayList<>();
						listOfScheduleJobs.add(eachSchedule);

						String baseUrl = "hi.html";
						UserService userService = mock(UserService.class);
						JsonObject eachScheduleJson = new JsonObject();
						JsonObject scheduleOptions = new JsonObject();
						scheduleOptions.addProperty("Frequency", "Frequency");
						scheduleOptions.addProperty("EndAfterExecutions", "2");
						eachScheduleJson.add("ScheduleOptions", scheduleOptions);
						JsonObject SchedulingJob = new JsonObject();
						SchedulingJob.addProperty("@type", "type");
						eachScheduleJson.add("SchedulingJob", SchedulingJob);
						User user = mock(User.class);
						when(user.getUsername()).thenReturn("username");
						when(userService.findUser(anyInt())).thenReturn(user);
						when(scheduleOperation.prepareSchedulesEntityToJSON(eachSchedule)).thenReturn(eachScheduleJson);
						mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
								.thenReturn(scheduleOperation);
						authenticationUtils.when(() -> AuthenticationUtils.getUserId(any(JsonObject.class)))
								.thenReturn("12");
						Field field = EnhancedScheduleProcessCall.class.getDeclaredField("scheduleOperation");
						field.setAccessible(true);
						field.set(enhancedScheduleProcessCall, scheduleOperation);

						enhancedScheduleProcessCall.scheduleOperation(listOfScheduleJobs, baseUrl, userService);
					}
				}
			}
		}
	}

}
