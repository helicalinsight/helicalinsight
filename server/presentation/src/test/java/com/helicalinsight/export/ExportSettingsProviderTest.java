package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.helicalinsight.export.components.ExportSettingsProvider;

public class ExportSettingsProviderTest {

	@Test
	public void ut_a1_testIsThreadSafeToCache() {
		ExportSettingsProvider provider = new ExportSettingsProvider();
		boolean threadSafeToCache = provider.isThreadSafeToCache();
		Assert.assertTrue(threadSafeToCache);
	}
	@Test
	public void ut_a2_testExecuteComponent() {
		ExportSettingsProvider provider = new ExportSettingsProvider();
		
		try(MockedStatic<ExportUtils> mockedStatic = mockStatic(ExportUtils.class)){
			mockedStatic.when(()-> ExportUtils.getReportDirectory()).thenReturn("directory");
			mockedStatic.when(()-> ExportUtils.getFileAsString(anyString())).thenReturn("directory");
			String executeComponent = provider.executeComponent("json");
			Assert.assertNotNull(executeComponent);
		}
	}
}
