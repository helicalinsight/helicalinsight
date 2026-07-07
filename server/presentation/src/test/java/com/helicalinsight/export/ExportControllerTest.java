package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.export.components.DownloadCacheManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExportControllerTest {

	@Test
	public void testExportData_a1() throws IOException {
		ExportController controller = new ExportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		List<String> locationsList = new ArrayList<>();
		locationsList.add("destinationFile");
		try (MockedConstruction<ServerSideExport> cons = mockConstruction(ServerSideExport.class, (mock, context) -> {
			when(mock.listOfLocations()).thenReturn(locationsList);
		})) {
			try(MockedStatic<DownloadCacheManager> static1 = mockStatic(DownloadCacheManager.class)){
			String exportData = controller.exportData(null, null, "pdf", request, response);
			Assert.assertNull(exportData);
			}
		}
	}

	@Test
	public void testExportData_a2() throws IOException {
		ExportController controller = new ExportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		List<String> locationsList = new ArrayList<>();
		locationsList.add("destinationFile");

		try (MockedConstruction<ReportsProcessor> cons1 = mockConstruction(ReportsProcessor.class, (mock, context) -> {

			when(mock.generateReportUsingHTMLSource(anyString(), anyString(), anyString())).thenReturn(locationsList);
		})) {
			try(MockedStatic<DownloadCacheManager> static1 = mockStatic(DownloadCacheManager.class)){
			String exportData = controller.exportData(null, "htmlString", "pdf", request, response);
			Assert.assertNull(exportData);
			}
		}

	}
	
	@Test
	public void testExportData_a3() throws IOException {
		ExportController controller = new ExportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		DownloadCacheManager downloadCacheManager = mock(DownloadCacheManager.class);
		
		JsonObject object = new JsonObject();
		object.addProperty("isAdhoc", "true");
		object.addProperty("type", "xls");
		String data = object.toString();
		List<String> locationsList = new ArrayList<>();
		locationsList.add("destinationFile");
		try(MockedStatic<ControllerUtils> mockedStatic1 = mockStatic(ControllerUtils.class)){
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()->ApplicationContextAccessor.getBean(DownloadCacheManager.class)).thenReturn(downloadCacheManager);
			String exportData = controller.exportData(data, null, "pdf", request, response);
			Assert.assertNull(exportData);
		}
			
		}
		
		
	}

	@Test
	public void testExportData_a4() throws IOException {
		ExportController controller = new ExportController();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		DownloadCacheManager downloadCacheManager = mock(DownloadCacheManager.class);
		
		JsonObject object = new JsonObject();
		String data = object.toString();
		List<String> locationsList = new ArrayList<>();
		locationsList.add("destinationFile");
		try(MockedConstruction<CSVUtility> construction = mockConstruction(CSVUtility.class,(mock,context)->{
			when(mock.getCSVData(anyString())).thenReturn("csvData");
		})){
		try(MockedStatic<ControllerUtils> mockedStatic1 = mockStatic(ControllerUtils.class)){
		try(MockedStatic<DownloadCacheManager> mockedStatic2 = mockStatic(DownloadCacheManager.class)){	
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()->ApplicationContextAccessor.getBean(DownloadCacheManager.class)).thenReturn(downloadCacheManager);
			String exportData = controller.exportData(data, null, "pdf", request, response);
			Assert.assertNull(exportData);
		}
		}
		}	
		
		}
		
	}
	
}
