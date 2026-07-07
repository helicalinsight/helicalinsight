package com.helicalinsight.scheduling;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.ApplicationDefaultUserAndRoleNamesConfigurer;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.SchedulesService;
import com.helicalinsight.scheduling.utils.ScheduleOperation;

public class EnhancedSchedulerActionHandlerTest {

	@Test(expected = EfwServiceException.class)
	public void testExecuteComponent_a1() {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<AuthenticationUtils> authenticationUtils = mockStatic(AuthenticationUtils.class)) {
				ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = mock(
						ApplicationDefaultUserAndRoleNamesConfigurer.class);

				Principal activeUser = mock(Principal.class);
				User user = mock(User.class);
				ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
				SchedulesService schedulesService = mock(SchedulesService.class);
				Schedules schedules = mock(Schedules.class);

				List<Schedules> allSchedules = new ArrayList<>();
				allSchedules.add(schedules);

				when(schedulesService.getAllSchedules()).thenReturn(allSchedules);
				when(namesConfigurer.getRoleAdmin()).thenReturn("admin");
				when(activeUser.getLoggedInUser()).thenReturn(user);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(SchedulesService.class))
						.thenReturn(schedulesService);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
						.thenReturn(scheduleOperation);
				mockedStatic.when(
						() -> ApplicationContextAccessor.getBean(ApplicationDefaultUserAndRoleNamesConfigurer.class))
						.thenReturn(namesConfigurer);
				List<String> roleList = new ArrayList<>();
				roleList.add("admin");
				authenticationUtils.when(() -> AuthenticationUtils.getUserRoles()).thenReturn(roleList);

				authenticationUtils.when(() -> AuthenticationUtils.getUserDetails()).thenReturn(activeUser);
				EnhancedSchedulerActionHandler actionHandler = new EnhancedSchedulerActionHandler();
				JsonObject formData = new JsonObject();
				formData.addProperty("action", "list");
				String string = formData.toString();
				actionHandler.executeComponent(string);
			}
		}
	}

	@Test(expected = EfwServiceException.class)
	public void testExecuteComponent_a2() {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);

			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
					.thenReturn(scheduleOperation);

			EnhancedSchedulerActionHandler actionHandler = new EnhancedSchedulerActionHandler();

			JsonObject formData = new JsonObject();

			String string = formData.toString();
			actionHandler.executeComponent(string);
		}
	}

	@Test
	public void testListJobKeyForSchedules_a1() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<AuthenticationUtils> authenticationUtils = mockStatic(AuthenticationUtils.class)) {
				SchedulesService schedulesService = mock(SchedulesService.class);
				
			ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(SchedulesService.class))
			.thenReturn(schedulesService);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
					.thenReturn(scheduleOperation);
			ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = mock(
					ApplicationDefaultUserAndRoleNamesConfigurer.class);
			mockedStatic.when(
					() -> ApplicationContextAccessor.getBean(ApplicationDefaultUserAndRoleNamesConfigurer.class))
					.thenReturn(namesConfigurer);
			List<String> roleList = new ArrayList<>();
			roleList.add("admin");
			authenticationUtils.when(() -> AuthenticationUtils.getUserRoles()).thenReturn(roleList);
			User user = mock(User.class);
			when(namesConfigurer.getRoleAdmin()).thenReturn("admin");
			Schedules schedules = mock(Schedules.class);
			List<Schedules> allSchedules = new ArrayList<>();
			allSchedules.add(schedules);
			when(schedules.getCreatedBy()).thenReturn(user);
			when(schedulesService.getAllSchedules()).thenReturn(allSchedules);
			
			EnhancedSchedulerActionHandler actionHandler = new EnhancedSchedulerActionHandler();
			
			Organization org = mock(Organization.class);
			when(org.getId()).thenReturn(11);
			when(user.getOrganization()).thenReturn(org);
			Method method = EnhancedSchedulerActionHandler.class.getDeclaredMethod("listJobKeyForSchedules",
					User.class);
			method.setAccessible(true);
			method.invoke(actionHandler, user);
			}
		}
	}
	@Test
	public void testListJobKeyForSchedules_aa1() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<AuthenticationUtils> authenticationUtils = mockStatic(AuthenticationUtils.class)) {
				SchedulesService schedulesService = mock(SchedulesService.class);
				
			ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(SchedulesService.class))
			.thenReturn(schedulesService);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
					.thenReturn(scheduleOperation);
			ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = mock(
					ApplicationDefaultUserAndRoleNamesConfigurer.class);
			mockedStatic.when(
					() -> ApplicationContextAccessor.getBean(ApplicationDefaultUserAndRoleNamesConfigurer.class))
					.thenReturn(namesConfigurer);
			List<String> roleList = new ArrayList<>();
			roleList.add("admin");
			authenticationUtils.when(() -> AuthenticationUtils.getUserRoles()).thenReturn(roleList);
			
			when(namesConfigurer.getRoleAdmin()).thenReturn("admin");
			Schedules schedules = mock(Schedules.class);
			List<Schedules> allSchedules = new ArrayList<>();
			allSchedules.add(schedules);
			User createdBy = new User();
			when(schedules.getCreatedBy()).thenReturn(createdBy);
			when(schedulesService.getAllSchedules()).thenReturn(allSchedules);
			
			EnhancedSchedulerActionHandler actionHandler = new EnhancedSchedulerActionHandler();
			User user = mock(User.class);
			Organization org = mock(Organization.class);
			when(org.getId()).thenReturn(11);
			when(user.getOrganization()).thenReturn(org);
			Method method = EnhancedSchedulerActionHandler.class.getDeclaredMethod("listJobKeyForSchedules",
					User.class);
			method.setAccessible(true);
			method.invoke(actionHandler, user);
			}
		}
	}
	@Test
	public void testListJobKeyForSchedules_a2() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<AuthenticationUtils> authenticationUtils = mockStatic(AuthenticationUtils.class)) {
				SchedulesService schedulesService = mock(SchedulesService.class);
				
			ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(SchedulesService.class))
			.thenReturn(schedulesService);
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
					.thenReturn(scheduleOperation);
			ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = mock(
					ApplicationDefaultUserAndRoleNamesConfigurer.class);
			mockedStatic.when(
					() -> ApplicationContextAccessor.getBean(ApplicationDefaultUserAndRoleNamesConfigurer.class))
					.thenReturn(namesConfigurer);
			List<String> roleList = new ArrayList<>();
			roleList.add("admin2");
			authenticationUtils.when(() -> AuthenticationUtils.getUserRoles()).thenReturn(roleList);
			when(namesConfigurer.getRoleAdmin()).thenReturn("admin");
			Schedules schedules = mock(Schedules.class);
			User user = mock(User.class);
			List<Schedules> allSchedules = new ArrayList<>();
			allSchedules.add(schedules);
			when(schedules.getCreatedBy()).thenReturn(user);
			when(schedulesService.getAllSchedules()).thenReturn(allSchedules);
			
			EnhancedSchedulerActionHandler actionHandler = new EnhancedSchedulerActionHandler();
			
			Organization org = mock(Organization.class);
			when(org.getId()).thenReturn(11);
			when(user.getOrganization()).thenReturn(org);
			Method method = EnhancedSchedulerActionHandler.class.getDeclaredMethod("listJobKeyForSchedules",
					User.class);
			method.setAccessible(true);
			method.invoke(actionHandler, user);
			}
		}
	}
	

}
