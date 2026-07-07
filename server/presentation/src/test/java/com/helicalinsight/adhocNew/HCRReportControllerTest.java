package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.HCRReportController;
import com.helicalinsight.adhoc.jreport.HCRHelper;
import com.helicalinsight.adhoc.report.ReportOpenHelper;
import com.helicalinsight.datasource.managed.jaxb.HCReport;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;

import jakarta.servlet.http.HttpServletRequest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HCRReportControllerTest {

	@Test
	public void ut_a1_test_openReport() throws IOException {
		HCRReportController controller = new HCRReportController();
		HttpServletRequest request = mock(HttpServletRequest.class);

		try (MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)) {
			ModelAndView openReport = controller.openReport(request);
			assertEquals("hcr-report", openReport.getViewName());
		}

	}

	@Test
	public void ut_a2_test_openReport() throws IOException {
		HCRReportController controller = new HCRReportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HCReport hcrReport = mock(HCReport.class);
		IComponent anySettingsGroovyProcessor = mock(IComponent.class);
		
		JsonObject urlParametersJson = new JsonObject();
		urlParametersJson.addProperty("key", "value");
		
		when(request.getParameter("dir")).thenReturn("dir");
		when(request.getParameter("file")).thenReturn("file");
		when(anySettingsGroovyProcessor.executeComponent(anyString())).thenReturn("success");
		try (MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)) {
			try (MockedStatic<ReportOpenHelper> mockedStatic2 = mockStatic(ReportOpenHelper.class)) {
				try(MockedStatic<FactoryMethodWrapper> mockedStatic3 = mockStatic(FactoryMethodWrapper.class)){
					mockedStatic3.when(() -> FactoryMethodWrapper
                .getTypedInstance("com.helicalinsight.export.components.AnySettingsGroovyProcessor", IComponent.class)).thenReturn(anySettingsGroovyProcessor);
				
				mockedStatic2.when(() -> ReportOpenHelper.getAdhocReport(anyString(), anyString()))
						.thenReturn(null);
				mockedStatic.when(()-> ControllerUtils.addAndGetUrlParameters(any(HttpServletRequest.class), any(ModelAndView.class))).thenReturn(urlParametersJson);
				
				ModelAndView openReport = controller.openReport(request);
				assertEquals("success", openReport.getModel().get("hcrConfigurations"));
				
				}
			}
		}

	}
	
	
	@Test
	public void ut_a3_test_openReport() throws IOException {
		HCRReportController controller = new HCRReportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HCReport hcrReport = mock(HCReport.class);
		IComponent anySettingsGroovyProcessor = mock(IComponent.class);
		
		JsonObject urlParametersJson = new JsonObject();
		urlParametersJson.addProperty("key", "value");
		urlParametersJson.addProperty("key1", "value1");
		urlParametersJson.addProperty("key2", "value2");
		urlParametersJson.addProperty("key3", "value3");
		JsonObject hcrJsonData = new JsonObject();
		JsonObject previewFormData = new JsonObject();
		hcrJsonData.addProperty("previewFormData", previewFormData.toString());
		
		when(request.getParameter("dir")).thenReturn("dir");
		when(request.getParameter("file")).thenReturn("file");
		when(anySettingsGroovyProcessor.executeComponent(anyString())).thenReturn("success");
		
		try (MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)) {
			try (MockedStatic<ReportOpenHelper> mockedStatic2 = mockStatic(ReportOpenHelper.class)) {
				try(MockedStatic<FactoryMethodWrapper> mockedStatic3 = mockStatic(FactoryMethodWrapper.class)){
					try(MockedStatic<HCRHelper> mockedStatic4 = mockStatic(HCRHelper.class)){
						
					mockedStatic3.when(() -> FactoryMethodWrapper
                .getTypedInstance("com.helicalinsight.export.components.AnySettingsGroovyProcessor", IComponent.class)).thenReturn(anySettingsGroovyProcessor);
				
				mockedStatic2.when(() -> ReportOpenHelper.getAdhocReport(anyString(), anyString()))
						.thenReturn(hcrReport);
				mockedStatic2.when(()-> ReportOpenHelper.newReportContentAsJson(hcrReport)).thenReturn(hcrJsonData);
				mockedStatic.when(()-> ControllerUtils.addAndGetUrlParameters(any(HttpServletRequest.class), any(ModelAndView.class))).thenReturn(urlParametersJson);
				
				ModelAndView openReport = controller.openReport(request);
				assertEquals("success", openReport.getModel().get("hcrConfigurations"));
				
					}
				}
			}
		}

	}
	@Test
	public void ut_a4_test_editReport() throws IOException {
		HCRReportController controller = new HCRReportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		
		HCReport hcrReport = mock(HCReport.class);
		IComponent anySettingsGroovyProcessor = mock(IComponent.class);
		
		JsonObject urlParametersJson = new JsonObject();
		urlParametersJson.addProperty("key", "value");
		JsonObject hcrJsonData = new JsonObject();
		JsonObject previewFormData = new JsonObject();
		hcrJsonData.addProperty("previewFormData", previewFormData.toString());
		
		when(request.getParameter("dir")).thenReturn("dir");
		when(request.getParameter("file")).thenReturn("file");
		when(anySettingsGroovyProcessor.executeComponent(anyString())).thenReturn("success");
		
		try (MockedStatic<ControllerUtils> mockedStatic = mockStatic(ControllerUtils.class)) {
			try (MockedStatic<ReportOpenHelper> mockedStatic2 = mockStatic(ReportOpenHelper.class)) {
				try(MockedStatic<FactoryMethodWrapper> mockedStatic3 = mockStatic(FactoryMethodWrapper.class)){
					try(MockedStatic<HCRHelper> mockedStatic4 = mockStatic(HCRHelper.class)){
						
					mockedStatic3.when(() -> FactoryMethodWrapper
                .getTypedInstance("com.helicalinsight.export.components.AnySettingsGroovyProcessor", IComponent.class)).thenReturn(anySettingsGroovyProcessor);
				
				mockedStatic2.when(() -> ReportOpenHelper.getAdhocReport(anyString(), anyString()))
						.thenReturn(hcrReport);
				mockedStatic2.when(()-> ReportOpenHelper.newReportContentAsJson(hcrReport)).thenReturn(hcrJsonData);
				mockedStatic.when(()-> ControllerUtils.addAndGetUrlParameters(any(HttpServletRequest.class), any(ModelAndView.class))).thenReturn(urlParametersJson);
				
				ModelAndView openReport = controller.editReport(request);
				assertEquals("success", openReport.getModel().get("hcrConfigurations"));
				
					}
				}
			}
		}

	}
}
