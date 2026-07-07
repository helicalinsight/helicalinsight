package com.helicalinsight.scheduling;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.springframework.context.ApplicationContext;

import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.SchedulesService;
import com.helicalinsight.scheduling.utils.ScheduleOperation;

public class InitializeScheduleDataTest {

	@Test
	public void testInitializeData_a1() {
		InitializeScheduleData data = new InitializeScheduleData();
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		when(applicationContext.getBean(ScheduleOperation.class)).thenReturn(scheduleOperation);
		data.initializeData(applicationContext);
	}

	@Test
	public void testInitializeData_a2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		InitializeScheduleData data = new InitializeScheduleData();
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		SchedulesService schedulesService = mock(SchedulesService.class);
		List<Schedules> allSchedules = new ArrayList<>();
		Schedules schedules = new Schedules();
		schedules.setScheduleId(11l);
		allSchedules.add(schedules);
		when(schedulesService.getAllSchedules()).thenReturn(allSchedules);
		Field field = InitializeScheduleData.class.getDeclaredField("schedulesService");
		field.setAccessible(true);
		field.set(data, schedulesService);
		try (MockedConstruction<EnhancedScheduleProcessCall> construction = mockConstruction(
				EnhancedScheduleProcessCall.class)) {
			EnhancedScheduleProcessCall call = new EnhancedScheduleProcessCall();
			data.initializeData(applicationContext);
		}

	}

	@Test
	public void testInitializeData_a3() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, IOException {
		InitializeScheduleData data = new InitializeScheduleData();
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		SchedulesService schedulesService = mock(SchedulesService.class);
		List<String> idsList = new ArrayList<>();
		idsList.add("11");
		String filePath = "D:/Test.txt";
		File file = new File(filePath);
		file.createNewFile();

		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			try (MockedConstruction<ScheduleProcessCall> construction = mockConstruction(ScheduleProcessCall.class,
					(mock, context) -> {
						when(mock.getSchedulePath()).thenReturn("D:/Test.txt");

					})) {

				try (MockedConstruction<XmlOperation> xmlOperation = mockConstruction(XmlOperation.class,
						(mock, context) -> {
							when(mock.getIdFromJson(anyString())).thenReturn(idsList);

						})) {
					mockedStatic.when(() -> JsonUtils.newIsScheduleStorageTypeIsDatabase()).thenReturn(false);
					data.initializeData(applicationContext);

				}
			}
		} finally {
			file.delete();
		}

	}

	@Test
	public void testInitializeData_a4() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, IOException {
		InitializeScheduleData data = new InitializeScheduleData();
		ApplicationContext applicationContext = mock(ApplicationContext.class);

		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {

			mockedStatic.when(() -> JsonUtils.newIsScheduleStorageTypeIsDatabase()).thenThrow(new Error());
			data.initializeData(applicationContext);

		}

	}

}
