package com.helicalinsight.export;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ExportException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;

public class ExportThreadFactoryTest {

	@Test
	public void ut_a1_testHandleThreads() {
		ExportThreadFactory exportThreadFactory = new ExportThreadFactory();
		JsonObject settings = new JsonObject();
		PhantomServiceManager manager = mock(PhantomServiceManager.class);
		List<String> stringList = new ArrayList<>();
		try(MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)){
			mockedStatic.when(()->  FactoryMethodWrapper
								.getUntypedInstance("com.helicalinsight.export.HeadlessChromeManager")).thenReturn(manager);
			when(manager.handlePhantomjs(settings)).thenReturn(stringList);
			List<String> list = exportThreadFactory.handleThreads(settings);
			Assert.assertEquals(stringList, list);
		}
		
	}
	@Test(expected = NullPointerException.class)
	public void ut_a2_testHandleThreads() {
		ExportThreadFactory exportThreadFactory = new ExportThreadFactory();
		JsonObject settings = new JsonObject();
		PhantomServiceManager manager = mock(PhantomServiceManager.class);
		List<String> stringList = new ArrayList<>();
		try(MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)){
			mockedStatic.when(()->  FactoryMethodWrapper
								.getUntypedInstance("com.helicalinsight.export.HeadlessChromeManager")).thenReturn(null);
			when(manager.handlePhantomjs(settings)).thenThrow(new ExportException("check Exception"));
			List<String> list = exportThreadFactory.handleThreads(settings);
			Assert.assertEquals(stringList, list);
		}
		
	}
}
