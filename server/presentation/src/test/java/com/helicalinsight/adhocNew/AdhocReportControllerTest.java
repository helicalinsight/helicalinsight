package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;
import org.springframework.web.servlet.ModelAndView;

import com.helicalinsight.adhoc.AdhocReportController;
import com.helicalinsight.adhoc.report.AdhocReport;
import com.helicalinsight.adhoc.report.ReportOpenHelper;
import com.helicalinsight.efw.controllerutils.ControllerUtils;

import jakarta.servlet.http.HttpServletRequest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdhocReportControllerTest {

	@Test
	public void ut_a1_test_openReport() throws IOException {
		AdhocReportController adhocReportController = new AdhocReportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		AdhocReport adhocReport = mock(AdhocReport.class);
		
		when(request.getParameter("dir")).thenReturn("dir");
		when(request.getParameter("file")).thenReturn("file");
		
		try(MockedStatic<ReportOpenHelper> mockedStatic1 = mockStatic(ReportOpenHelper.class)){
			try(MockedStatic<ControllerUtils> mockedStatic2 = mockStatic(ControllerUtils.class)){
				mockedStatic1.when(()-> ReportOpenHelper.getAdhocReport(anyString(), anyString())).thenReturn(adhocReport);
				ModelAndView openReport = adhocReportController.openReport(request);
				assertEquals("adhocReport",openReport.getViewName());
			}
			
		}
		
		
	}
	
	@Test
	public void ut_a2_test_openReport() throws IOException {
		AdhocReportController adhocReportController = new AdhocReportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		
		when(request.getParameter("dir")).thenReturn("dir");
		when(request.getParameter("file")).thenReturn("file");
		
		try(MockedStatic<ReportOpenHelper> mockedStatic1 = mockStatic(ReportOpenHelper.class)){
			try(MockedStatic<ControllerUtils> mockedStatic2 = mockStatic(ControllerUtils.class)){
				mockedStatic1.when(()-> ReportOpenHelper.getAdhocReport(anyString(), anyString())).thenReturn(null);
				ModelAndView openReport = adhocReportController.openReport(request);
				assertEquals("adhocReport",openReport.getViewName());
			}
			
		}
		
		
	}
}
