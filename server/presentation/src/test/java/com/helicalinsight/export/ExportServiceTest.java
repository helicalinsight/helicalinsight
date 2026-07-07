package com.helicalinsight.export;

import static org.mockito.Mockito.mockStatic;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.helicalinsight.efw.serviceframework.ServiceUtils;

public class ExportServiceTest {

	@Test
	public void testDoService() {
		ExportService exportService = new ExportService();
		try(MockedStatic<ServiceUtils> mockedStatic = mockStatic(ServiceUtils.class)){
			mockedStatic.when(()-> ServiceUtils.execute("type", "serviceType", "service", "formData")).thenReturn("result");
			String doService = exportService.doService("type", "serviceType", "service", "formData");
			Assert.assertEquals("result", doService);
		}
	}
	@Test
	public void testIsThreadSafeToCache() {
		ExportService exportService = new ExportService();
		boolean threadSafeToCache = exportService.isThreadSafeToCache();
		Assert.assertTrue(threadSafeToCache);
	}
}
