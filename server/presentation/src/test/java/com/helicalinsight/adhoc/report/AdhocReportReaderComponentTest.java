package com.helicalinsight.adhoc.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.ConfigurationFileReader;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdhocReportReaderComponentTest {

	@Test(expected = IllegalArgumentException.class)
	public void ut_a1_test_executeComponent() {
		AdhocReportReaderComponent component = new AdhocReportReaderComponent();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("dir", "dir");
		formJson.addProperty("file", "file");
		try (MockedStatic<ConfigurationFileReader> mockedStatic0 = mockStatic(ConfigurationFileReader.class)) {
			try (MockedStatic<ControllerUtils> mockedStatic1 = mockStatic(ControllerUtils.class)) {
				try (MockedStatic<ReportOpenHelper> mockedStatic2 = mockStatic(ReportOpenHelper.class)) {
					mockedStatic2.when(() -> ReportOpenHelper.getAdhocReport(anyString(), anyString()))
							.thenReturn(null);
					component.executeComponent(formJson.toString());
				}
			}
		}
	}
	
	@Test
	public void ut_a2_test_executeComponent() {
		AdhocReportReaderComponent component = new AdhocReportReaderComponent();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("dir", "dir");
		formJson.addProperty("file", "file");
		AdhocReport adhocReport = mock(AdhocReport.class);
		JsonObject response = new JsonObject();
		try (MockedStatic<ConfigurationFileReader> mockedStatic0 = mockStatic(ConfigurationFileReader.class)) {
			try (MockedStatic<ControllerUtils> mockedStatic1 = mockStatic(ControllerUtils.class)) {
				try (MockedStatic<ReportOpenHelper> mockedStatic2 = mockStatic(ReportOpenHelper.class)) {
					mockedStatic2.when(() -> ReportOpenHelper.getAdhocReport(anyString(), anyString()))
							.thenReturn(adhocReport);
					mockedStatic2.when(() -> ReportOpenHelper.reportContentAsJson(any()))
					.thenReturn(response);
			
					String executeComponent = component.executeComponent(formJson.toString());
					assertEquals(response.toString(), executeComponent);
				}
			}
		}
	}
	@Test
	public void ut_a3_test_isThreadSafeToCache() {
		AdhocReportReaderComponent component = new AdhocReportReaderComponent();
		boolean threadSafeToCache = component.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
}
