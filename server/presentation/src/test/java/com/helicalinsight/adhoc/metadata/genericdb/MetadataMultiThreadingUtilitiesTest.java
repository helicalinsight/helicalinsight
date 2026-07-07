package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.helicalinsight.efw.utility.ConfigurationFileReader;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataMultiThreadingUtilitiesTest {

	@Test
	public void ut_a1_test_getThreshold() {
		MetadataMultiThreadingUtilities metadataMultiThreadingUtilities = new MetadataMultiThreadingUtilities();
		Map<String, String> mapFromClasspathPropertiesFile = new HashMap<>();
		mapFromClasspathPropertiesFile.put("metadataThreadsTableThreshold", "metadataThreadsTableThreshold");
		
		try(MockedStatic<ConfigurationFileReader> mockedStatic = mockStatic(ConfigurationFileReader.class)){
			mockedStatic.when(()-> ConfigurationFileReader.getProjectPropertiesFile()).thenReturn(mapFromClasspathPropertiesFile);
			Integer threshold = MetadataMultiThreadingUtilities.getThreshold();
			Integer count = 10;
			assertEquals(count, threshold);
			
		}
	}
	
	@Test(expected = MetadataRetrievalException.class)
	public void ut_a2_test_pauseThreads() {
		
		List<Thread> threads = new ArrayList<>();
		Boolean[] handlerFlag = {true, false};
		MetadataMultiThreadingUtilities.pauseThreads(threads, handlerFlag);
	}
	
	@Test
	public void ut_a3_test_pauseThreads() {
		Thread thread = mock(Thread.class);
		List<Thread> threads = new ArrayList<>();
		threads.add(thread);
		Boolean[] handlerFlag = {false, false};
		MetadataMultiThreadingUtilities.pauseThreads(threads, handlerFlag);
	}
	
	@Test(expected = MetadataRetrievalException.class)
	public void ut_a4_test_pauseThreads() throws InterruptedException {
		Thread thread = mock(Thread.class);
		List<Thread> threads = new ArrayList<>();
		threads.add(thread);
		Boolean[] handlerFlag = {false, false};
		doThrow(new InterruptedException()).when(thread).join();
		MetadataMultiThreadingUtilities.pauseThreads(threads, handlerFlag);
	}
	@Test
	public void ut_a5_test_getUncaughtExceptionHandler() {
		Boolean[] handlerFlag = {true};
		UncaughtExceptionHandler uncaughtExceptionHandler = MetadataMultiThreadingUtilities.getUncaughtExceptionHandler(handlerFlag);
		Thread threadMock = mock(Thread.class);
        Throwable throwableMock = mock(Throwable.class);

		uncaughtExceptionHandler.uncaughtException(threadMock, throwableMock);
	}
}
