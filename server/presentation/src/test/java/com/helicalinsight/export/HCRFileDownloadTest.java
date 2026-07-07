package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.jreport.IHCRGenerator;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

public class HCRFileDownloadTest {

	@Test
	public void testDownloadFormat() {
		HCRFileDownload download = new HCRFileDownload();
		Object jsonData = "";
		JsonObject formData = new JsonObject();
		IHCRGenerator generator = mock(IHCRGenerator.class);
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(generator);
			Object downloadFormat = download.downloadFormat(jsonData, formData);
			Assert.assertNull(downloadFormat);
			
		}
	}
}
