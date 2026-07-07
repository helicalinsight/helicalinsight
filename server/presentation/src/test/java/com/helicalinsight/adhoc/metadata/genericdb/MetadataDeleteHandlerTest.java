package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.report.AdhocReport;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.resourcesecurity.jaxb.MetadataReference;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataDeleteHandlerTest {

	@Test
	public void ut_a1_test_isThreadSafeToCache() {
		MetadataDeleteHandler deleteHandler = new MetadataDeleteHandler();
		boolean threadSafeToCache = deleteHandler.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}

	@Test
	public void ut_a2_test_executeComponent() {
		MetadataDeleteHandler deleteHandler = new MetadataDeleteHandler();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("location", "location");
		formJson.addProperty("metadataFileName", "metadataFileName");

		deleteHandler.executeComponent(formJson.toString());
	}

	@Test
	public void ut_a3_test_executeComponent() throws IOException {
		MetadataDeleteHandler deleteHandler = new MetadataDeleteHandler();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("location", "System");
		formJson.addProperty("metadataFileName", "metadataFileName.txt");

		List<String> filteredReportList = new ArrayList<>();
		filteredReportList.add("/home/helical/Performance/hi/hi-repository/System/file.txt");
		File file = new File("/home/helical/Performance/hi/hi-repository/System/file.txt");
		file.createNewFile();
		File file1 = new File("/home/helical/Performance/hi/hi-repository/System/metadataFileName.txt");
		file1.createNewFile();
		AdhocReport report = mock(AdhocReport.class);
		MetadataReference metadataReference = mock(MetadataReference.class);
		when(report.getMetadataReference()).thenReturn(metadataReference);
		when(metadataReference.getMetadataFileName()).thenReturn("metadataFileName.txt");
		try (MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {
			try (MockedStatic<JaxbUtils> mockedStatic1 = mockStatic(JaxbUtils.class)) {
			mockedStatic1.when(()-> JaxbUtils.unMarshal(any(), any())).thenReturn(report);	
			mockedStatic.when(() -> FileUtils.getFilteredFileList(any(), any())).thenReturn(filteredReportList);
			deleteHandler.executeComponent(formJson.toString());
			}
		}
	}
	
	@Test
	public void ut_a4_test_executeComponent() throws IOException {
		MetadataDeleteHandler deleteHandler = new MetadataDeleteHandler();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("location", "System");
		formJson.addProperty("metadataFileName", "metadataFileName.txt");

		List<String> filteredReportList = new ArrayList<>();
		filteredReportList.add("/home/helical/Performance/hi/hi-repository/System/file.txt");
		AdhocReport report = mock(AdhocReport.class);
		MetadataReference metadataReference = mock(MetadataReference.class);
		when(report.getMetadataReference()).thenReturn(metadataReference);
		when(metadataReference.getMetadataFileName()).thenReturn("metadataFileName.txt");
		try (MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {
			try (MockedStatic<JaxbUtils> mockedStatic1 = mockStatic(JaxbUtils.class)) {
			mockedStatic1.when(()-> JaxbUtils.unMarshal(any(), any())).thenThrow(new ConfigurationException("message"));	
			mockedStatic.when(() -> FileUtils.getFilteredFileList(any(), any())).thenReturn(filteredReportList);
			deleteHandler.executeComponent(formJson.toString());
			}
		}
	}

	
}
