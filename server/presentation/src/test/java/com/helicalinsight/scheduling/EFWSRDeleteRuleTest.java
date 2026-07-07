package com.helicalinsight.scheduling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.io.FileOperationsUtility;
import com.helicalinsight.efw.io.delete.IDeleteRule;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.scheduling.utils.ScheduleOperation;

public class EFWSRDeleteRuleTest {

	@Test
	public void testGetInstance() {
		EFWSRDeleteRule deleteRule = new EFWSRDeleteRule();
		IDeleteRule instance = EFWSRDeleteRule.getInstance();
		assertNotNull(instance);
	}

	@Test
	public void testIsDeletable_a1() throws IOException {
		EFWSRDeleteRule deleteRule = new EFWSRDeleteRule();
		IProcessor processor = mock(IProcessor.class);

		JsonObject object = new JsonObject();
		object.addProperty("favourite", "false");

		String filePath = "D:/Test.txt";
		File file = new File(filePath);
		file.createNewFile();

		try (MockedStatic<ResourceProcessorFactory> mockedStatic = mockStatic(ResourceProcessorFactory.class)) {
			mockedStatic.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
			when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(object);

			boolean deletable = deleteRule.isDeletable(file);
			assertTrue(deletable);
			file.delete();
		}

	}

//	@Test
	public void testIsDeletable_a2() throws IOException {
		EFWSRDeleteRule deleteRule = new EFWSRDeleteRule();
		IProcessor processor = mock(IProcessor.class);

		JsonObject object = new JsonObject();
		object.addProperty("favourite", "Test.txt");

		String filePath = "D:/Test.txt";
		File file = new File(filePath);
		file.createNewFile();

		try (MockedStatic<ResourceProcessorFactory> mockedStatic = mockStatic(ResourceProcessorFactory.class)) {
			mockedStatic.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
			when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(object);

			boolean deletable = deleteRule.isDeletable(file);
			assertTrue(deletable);
			file.delete();
		}

	}

	@Test
	public void testDelete_a1() throws IOException {
		EFWSRDeleteRule deleteRule = new EFWSRDeleteRule();
		IProcessor processor = mock(IProcessor.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		JsonObject object = new JsonObject();
		object.addProperty("schedulingReference", "1");
		object.addProperty("ScheduleStorageType", "DATABASE");
		
		object.addProperty("favourite", 123);
		String filePath = "D:/Test.txt";
		File file = new File(filePath);
		file.createNewFile();
		try (MockedStatic<ResourceProcessorFactory> mockedStatic = mockStatic(ResourceProcessorFactory.class)) {
			try (MockedStatic<ApplicationContextAccessor> context = mockStatic(ApplicationContextAccessor.class)) {
				context.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
						.thenReturn(scheduleOperation);
				doNothing().when(scheduleOperation).deleteScheduleEntity(anyString());

				mockedStatic.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
				when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(object);
				deleteRule.delete(file);
				
			}	
			
		}
		
	}
	
//	@Test
	public void testDelete_a2() throws IOException {
		EFWSRDeleteRule deleteRule = new EFWSRDeleteRule();
		IProcessor processor = mock(IProcessor.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		JsonObject object = new JsonObject();
		object.addProperty("schedulingReference", "1");
		object.addProperty("ScheduleStorageType", "XML");
		
		object.addProperty("favourite", "false");
		String filePath = "D:/Test.txt";
		File file = new File(filePath);
		file.createNewFile();
		try (MockedStatic<ResourceProcessorFactory> mockedStatic = mockStatic(ResourceProcessorFactory.class)) {
			try (MockedStatic<ApplicationContextAccessor> context = mockStatic(ApplicationContextAccessor.class)) {
				context.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
						.thenReturn(scheduleOperation);
				doNothing().when(scheduleOperation).deleteScheduleEntity(anyString());
				
				mockedStatic.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
				when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(object);
				deleteRule.delete(file);
				
			}	
			
		}
		
	}
	@Test
	public void testIsThreadSafeToCache() {
		EFWSRDeleteRule deleteRule = new EFWSRDeleteRule();
		boolean threadSafeToCache = deleteRule.isThreadSafeToCache();
		assertEquals(true, threadSafeToCache);
	}


}
