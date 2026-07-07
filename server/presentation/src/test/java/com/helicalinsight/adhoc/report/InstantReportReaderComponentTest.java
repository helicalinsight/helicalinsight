package com.helicalinsight.adhoc.report;

import static org.junit.Assert.assertEquals;

import com.helicalinsight.adhoc.report.ReportOpenHelper;
import com.helicalinsight.instant.report.InstantReportReaderComponent;
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

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InstantReportReaderComponentTest {

	@Test(expected = IllegalArgumentException.class)
	public void ut_a1_test_executeComponent() {
		InstantReportReaderComponent component = new InstantReportReaderComponent();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("dir", "dir");
		formJson.addProperty("file", "file");
		try(MockedStatic<ReportOpenHelper> mockedStatic = mockStatic(ReportOpenHelper.class)){
			mockedStatic.when(()-> ReportOpenHelper.getInstantReportDb(anyString(), anyString())).thenReturn(null);
			component.executeComponent(formJson.toString());
		}
		
	}
	
	@Test
	public void ut_a2_test_executeComponent() {
		InstantReportReaderComponent component = new InstantReportReaderComponent();
		AdhocReport adhocReport = mock(AdhocReport.class);
		JsonObject formJson = new JsonObject();
		formJson.addProperty("dir", "dir");
		formJson.addProperty("file", "file");
		try(MockedStatic<ReportOpenHelper> mockedStatic = mockStatic(ReportOpenHelper.class)){
			mockedStatic.when(()-> ReportOpenHelper.getInstantReportDb(anyString(), anyString())).thenReturn(adhocReport);
			mockedStatic.when(()-> ReportOpenHelper.reportContentAsJson(any())).thenReturn(formJson);
			
			String executeComponent = component.executeComponent(formJson.toString());
			assertEquals(formJson.toString(), executeComponent);
		}
		
	}
	@Test
	public void ut_a3_test_isThreadSafeToCache() {
		InstantReportReaderComponent component = new InstantReportReaderComponent();
		boolean threadSafeToCache = component.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
}
