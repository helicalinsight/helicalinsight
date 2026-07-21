package com.helicalinsight.scheduling;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.io.FileOperationsUtility;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ExecuteReport;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.jaxb.Efwfav;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;
import com.helicalinsight.scheduling.utils.ScheduleOperation;
import com.helicalinsight.scheduling.utils.SchedulerUtils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SaveReportControllerTest {

	@Test
	public void testDownloadEnableSavedResult_a1() throws IOException {
		SaveReportController controller = new SaveReportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		IProcessor processor = mock(IProcessor.class);
		JsonObject resultObject = new JsonObject();
		resultObject.addProperty("resultFile", "resultFile");
		resultObject.addProperty("resultDirectory", "resultDirectory");
		resultObject.addProperty("resultName", "resultName");

		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(resultObject);
		try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {

			try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {
				factory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
				controller.downloadEnableSavedResult("dir", "file", request, response);
			}
		}

	}

	@Test
	public void testDownloadEnableSavedResult_a2() throws IOException {
		SaveReportController controller = new SaveReportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		IProcessor processor = mock(IProcessor.class);
		JsonObject resultObject = new JsonObject();
		resultObject.addProperty("resultFile", "[file]");
		resultObject.addProperty("resultDirectory", "[resultDirectory]");
		resultObject.addProperty("resultName", "resultName");

		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(resultObject);
		try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {

			try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {
				factory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
				controller.downloadEnableSavedResult("dir", "file", request, response);
			}
		}

	}

	@Test
	public void testDownloadEnableSavedResult_a3() throws IOException {
		SaveReportController controller = new SaveReportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		IProcessor processor = mock(IProcessor.class);
		JsonObject resultObject = new JsonObject();
		resultObject.addProperty("resultFile", "file");
		resultObject.addProperty("resultDirectory", "resultDirectory");
		resultObject.addProperty("resultName", "[resultName]");

		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(resultObject);
		try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {
			try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {
				factory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
				controller.downloadEnableSavedResult("dir", "file", request, response);
			}
		}
	}

	@Test
	public void testDownloadEnableSavedResult_a4() throws IOException {
		SaveReportController controller = new SaveReportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		IProcessor processor = mock(IProcessor.class);
		JsonObject resultObject = new JsonObject();
		resultObject.addProperty("resultFile", "file");
		resultObject.addProperty("resultDirectory", "System");
		resultObject.addProperty("resultName", "resultName");

		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(resultObject);
		try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {
			try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {
				factory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
				controller.downloadEnableSavedResult("dir", "file", request, response);
			}
		}
	}

//	@Test
	public void testDownloadEnableSavedResult_a5() throws IOException {
		SaveReportController controller = new SaveReportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		IProcessor processor = mock(IProcessor.class);
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		JsonObject resultObject = new JsonObject();
		resultObject.addProperty("resultFile", "Test.txt");
		resultObject.addProperty("resultDirectory", "System");
		resultObject.addProperty("resultName", "resultName");

		String filePath = "/home/helical/Performance/hi/hi-repository/System/Test.txt";
		File file = new File(filePath);
		file.createNewFile();
		when(response.getOutputStream()).thenReturn(outputStream);
		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(resultObject);
		try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {

			try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {

				factory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
				controller.downloadEnableSavedResult("dir", "file", request, response);
				file.delete();
			}

		}

	}

//	@Test
	public void testDownloadEnableSavedResult_a6() throws IOException {
		SaveReportController controller = new SaveReportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		IProcessor processor = mock(IProcessor.class);
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		JsonObject resultObject = new JsonObject();
		resultObject.addProperty("resultFile", "Test.txt");
		resultObject.addProperty("resultDirectory", "System");
		resultObject.addProperty("resultName", "resultName");

		String filePath = "/home/helical/Performance/hi/hi-repository/System/Test.txt";
		File file = new File(filePath);
		file.createNewFile();
		when(response.getOutputStream()).thenThrow(new IOException());
		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(resultObject);
		try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {

			try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {

				factory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
				controller.downloadEnableSavedResult("dir", "file", request, response);
				file.delete();
			}
		}

	}

//	@Test
	public void testDownloadEnableSavedResult_a7() throws IOException {
		SaveReportController controller = new SaveReportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		IProcessor processor = mock(IProcessor.class);
		JsonObject resultObject = new JsonObject();
		resultObject.addProperty("resultFile", "Test");
		resultObject.addProperty("resultDirectory", "System");
		resultObject.addProperty("resultName", "resultName");

		String filePath = "/home/helical/Performance/hi/hi-repository/System/Test";
		File file = new File(filePath);
		file.createNewFile();
		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(resultObject);
		try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {

			try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {

				factory.when(() -> ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
				controller.downloadEnableSavedResult("dir", "file", request, response);
				file.delete();
			}

		}

	}

	@Test(expected = InvocationTargetException.class)
	public void testSchedule() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Method method = SaveReportController.class.getDeclaredMethod("schedule", HttpServletRequest.class);
		method.setAccessible(true);
		HttpServletRequest request = mock(HttpServletRequest.class);
		ScheduleOperation scheduleOperation = mock(ScheduleOperation.class);
		JsonObject scheduleOptions = new JsonObject();
		scheduleOptions.addProperty("StartDate", "11-11-2023");
		scheduleOptions.addProperty("EndDate", "11-12-2023");

		when(request.getParameter("ScheduleOptions")).thenReturn(scheduleOptions.toString());
		when(request.getParameter("command")).thenReturn("add");
		when(request.getParameter("reportParameters")).thenReturn("reportParameters");
		when(request.getParameter("isActive")).thenReturn("true");
		when(request.getParameter("reportFile")).thenReturn("reportFile");
		when(request.getParameter("reportDirectory")).thenReturn("reportDirectory");
		when(request.getParameter("reportName")).thenReturn("reportName");
		JsonObject object = new JsonObject();
		try (MockedStatic<FileUtils> fileUtils = mockStatic(FileUtils.class)) {
			try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
				try (MockedStatic<SchedulerUtils> schUtils = mockStatic(SchedulerUtils.class)) {

					schUtils.when(() -> SchedulerUtils.prepareJsonFromUserData(any(String.class), any(String.class),
							any(String.class), any(String.class), any(String.class), any(String.class),
							any(String.class), any(String.class))).thenReturn(object);
					fileUtils.when(() -> FileUtils.getExtension(anyString())).thenReturn("hcr");
					mockedStatic.when(() -> ApplicationContextAccessor.getBean(ScheduleOperation.class))
							.thenReturn(scheduleOperation);
					method.invoke(new SaveReportController(), request);
				}
			}
		}
	}
	@Test
	public void testGetActualFile_a1() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = SaveReportController.class.getDeclaredMethod("getActualFile", String.class,String.class);
		method.setAccessible(true);
		IProcessor processor = mock(IProcessor.class);
		JsonObject object = new JsonObject();
		object.addProperty("reportFile", "reportFile");
		object.addProperty("reportDirectory", "reportDirectory");
		
		when(processor.getJsonObject(anyString(), anyBoolean())).thenReturn(object);
		try (MockedStatic<JsonUtils> json = mockStatic(JsonUtils.class)) {
			
		try (MockedStatic<ResourceProcessorFactory> factory = mockStatic(ResourceProcessorFactory.class)) {
			factory.when(() ->ResourceProcessorFactory.getIProcessor()).thenReturn(processor);
			json.when(() -> JsonUtils.getEFWSRExtension()).thenReturn("efwsr");
			 Object invoke = method.invoke(new SaveReportController(), "report.efwsr","dir");
			assertNotNull(invoke);
		}
		}
	
	}
	@Test
	public void testGetActualFile_a2() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = SaveReportController.class.getDeclaredMethod("getActualFile", String.class,String.class);
		method.setAccessible(true);
	    Object invoke = method.invoke(new SaveReportController(), "report","dir");
	    assertNull(invoke);
	}
	
	
	@Test()
	public void testExecuteFavourite() {
		SaveReportController saveReportController = new SaveReportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		Efwfav efwfav = mock(Efwfav.class);
		Class<Efwfav> clzz = Efwfav.class;
		Efwsr efwsr = mock(Efwsr.class);
		Class<Efwsr> clzz2 = Efwsr.class;
		ApplicationProperties instance = mock(ApplicationProperties.class);
		when(instance.getSolutionDirectory()).thenReturn("dir");
		when(efwfav.getSavedReportFileName()).thenReturn("file");
		JsonObject obj = new JsonObject();
		List<String> list = new ArrayList<>();
		list.add("data");
		list.add("dir");
		when(efwsr.getReportParameters()).thenReturn(obj.toString());
		when(efwsr.getReportDirectory()).thenReturn("report");
		when(efwsr.getReportFile()).thenReturn("file");
		try (MockedStatic<JaxbUtils> jaxb = mockStatic(JaxbUtils.class)) {
			try (MockedStatic<ApplicationProperties> properties = mockStatic(ApplicationProperties.class)) {
				try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class)) {

					try (MockedConstruction<FileOperationsUtility> utility = mockConstruction(
							FileOperationsUtility.class, (mock, context) -> {
								when(mock.search(anyString(), anyString())).thenReturn("Test.txt");
							})) {
						try (MockedConstruction<ExecuteReport> executeReport = mockConstruction(ExecuteReport.class,
								(mock, context) -> {
									when(mock.execute(anyString(), anyString(), any(JsonObject.class)))
											.thenReturn(list);
								})) {
							properties.when(() -> ApplicationProperties.getInstance()).thenReturn(instance);
							jaxb.when(() -> JaxbUtils.unMarshal(eq(clzz), any(File.class))).thenReturn(efwfav);
							jaxb.when(() -> JaxbUtils.unMarshal(eq(clzz2), any(File.class))).thenReturn(efwsr);
							ModelAndView executeFavourite = saveReportController.executeFavourite("dir", "fileName", request);
							assertNotNull(executeFavourite);
						}
					}
				}
			}
		}

	}

}
