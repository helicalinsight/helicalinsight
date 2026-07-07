package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.AdhocReportCreator;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.report.AdhocReport;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.CanvasElements;
import com.helicalinsight.resourcesecurity.jaxb.MetadataReference;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdhocReportCreatorTest {

	@Test(expected = IncompleteFormDataException.class)
	public void ut_a1_test_executeComponent() {
		AdhocReportCreator creator = new AdhocReportCreator();
		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("uniqueId", "23");
		String jsonFormData = formDataJson.toString();
		creator.executeComponent(jsonFormData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ut_a2_test_executeComponent() {
		AdhocReportCreator creator = new AdhocReportCreator();
		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("uniqueId", "23");
		JsonObject state = new JsonObject();
		formDataJson.add("state", state);
		formDataJson.addProperty("location", "location");
		formDataJson.addProperty("columns", "columns");
		String jsonFormData = formDataJson.toString();
		creator.executeComponent(jsonFormData);
	}

	@Test(expected = EfwServiceException.class)
	public void ut_a3_test_executeComponent() {
		AdhocReportCreator creator = new AdhocReportCreator();
		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("uniqueId", "23");
		JsonObject state = new JsonObject();
		formDataJson.add("state", state);
		formDataJson.addProperty("location", "location");
		formDataJson.addProperty("columns", "columns");
		formDataJson.addProperty("reportName", "reportName");
		String jsonFormData = formDataJson.toString();
		creator.executeComponent(jsonFormData);
	}

	@Test(expected = IncompleteFormDataException.class)
	public void ut_a4_test_executeComponent() {
		AdhocReportCreator creator = new AdhocReportCreator();
		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("uniqueId", "23");
		JsonObject state = new JsonObject();
		formDataJson.add("state", state);
		formDataJson.addProperty("location", "location");
		formDataJson.addProperty("columns", "columns");
		formDataJson.addProperty("reportName", "reportName");
		formDataJson.addProperty("uuid", "uuid");
		JsonObject metadata = new JsonObject();
		formDataJson.add("metadata", metadata);
		String jsonFormData = formDataJson.toString();
		creator.executeComponent(jsonFormData);
	}

	@Test(expected = EfwServiceException.class)
	public void ut_a5_test_executeComponent() {
		AdhocReportCreator creator = new AdhocReportCreator();
		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("uniqueId", "23");
		JsonObject state = new JsonObject();
		formDataJson.add("state", state);
		formDataJson.addProperty("location", "location");
		formDataJson.addProperty("columns", "columns");
		formDataJson.addProperty("reportName", "reportName");
		formDataJson.addProperty("uuid", "uuid");
		formDataJson.addProperty("groups", "groups");
		formDataJson.addProperty("description", "description");
		JsonObject metadata = new JsonObject();
		metadata.addProperty("location", "location");
		metadata.addProperty("metadataFileName", "metadataFileName");
		formDataJson.add("metadata", metadata);
		String jsonFormData = formDataJson.toString();
		creator.executeComponent(jsonFormData);
	}

	@Test(expected = EfwServiceException.class)
	public void ut_a6_test_executeComponent() {
		AdhocReportCreator creator = new AdhocReportCreator();
		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("uniqueId", "23");
		JsonObject state = new JsonObject();
		formDataJson.add("state", state);
		formDataJson.addProperty("location", "System");
		formDataJson.addProperty("columns", "columns");
		formDataJson.addProperty("reportName", "reportName");
		formDataJson.addProperty("uuid", "Temp");
		formDataJson.addProperty("groups", "groups");
		formDataJson.addProperty("description", "description");
		JsonObject metadata = new JsonObject();
		metadata.addProperty("location", "location");
		metadata.addProperty("metadataFileName", "metadataFileName");
		formDataJson.add("metadata", metadata);
		String jsonFormData = formDataJson.toString();

		String path = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
		File file = new File(path);
		file.mkdirs();
		JsonObject report = new JsonObject();
		report.addProperty("reportName", "reportName");
		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			mockedStatic.when(() -> JsonUtils.newGetAsJson(any(File.class))).thenReturn(report);
			creator.executeComponent(jsonFormData);
		} finally {
			file.delete();
		}

	}

	@Test
	public void ut_a7_test_process() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		AdhocReportCreator creator = new AdhocReportCreator();

		Method method = AdhocReportCreator.class.getDeclaredMethod("process", String.class, String.class, String.class,
				String.class, String.class, String.class, String.class, JsonObject.class, String.class, boolean.class,
				JsonObject.class, Boolean.class);
		method.setAccessible(true);
		String solutionDirectory = ApplicationProperties.INSTANCE.getSolutionDirectory();
		JsonObject state = new JsonObject();
		MetadataReference metadataReference = mock(MetadataReference.class);
		CanvasElements canvasElements = mock(CanvasElements.class);
		AdhocReport adhocReport = mock(AdhocReport.class);
		Metadata metadata = mock(Metadata.class);
		String tempFile = TempDirectoryCleaner.getTempDirectory() + File.separator+"uniqueId.metadata";
		File file = new File(tempFile);
		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			try (MockedStatic<JaxbUtils> mockedStatic2 = mockStatic(JaxbUtils.class)) {
				try (MockedStatic<ApplicationContextAccessor> mockedStatic3 = mockStatic(
						ApplicationContextAccessor.class)) {
					try(MockedStatic<SecurityUtils> mockedStatic4 = mockStatic(SecurityUtils.class)){
						
					
					mockedStatic3.when(() -> ApplicationContextAccessor.getBean(AdhocReport.class))
							.thenReturn(adhocReport);
					mockedStatic3.when(() -> ApplicationContextAccessor.getBean(CanvasElements.class))
							.thenReturn(canvasElements);
					mockedStatic3.when(() -> ApplicationContextAccessor.getBean(MetadataReference.class))
					.thenReturn(metadataReference);
					
					mockedStatic2.when(() -> JaxbUtils.unMarshal(Metadata.class, file)).thenReturn(metadata);

					mockedStatic.when(() -> JsonUtils.getMetadataExtension()).thenReturn("metadata");
					mockedStatic.when(() -> JsonUtils.getHrReportExtension()).thenReturn("report");
					JsonArray columns = new JsonArray();
					method.invoke(creator, solutionDirectory, "Temp", "System", "sampleReport", columns.toString(), "groups",
							"uniqueId", state, "description", true, null, true);
				}
				}
			}
		}

	}

	@Test
	public void ut_a8_test_process() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		AdhocReportCreator creator = new AdhocReportCreator();

		Method method = AdhocReportCreator.class.getDeclaredMethod("process", String.class, String.class, String.class,
				String.class, String.class, String.class, String.class, JsonObject.class, String.class, boolean.class,
				JsonObject.class, Boolean.class);
		method.setAccessible(true);
		String solutionDirectory =ApplicationProperties.INSTANCE.getSolutionDirectory();
		JsonObject state = new JsonObject();
		MetadataReference metadataReference = mock(MetadataReference.class);
		CanvasElements canvasElements = mock(CanvasElements.class);
		AdhocReport adhocReport = mock(AdhocReport.class);
		Metadata metadata = mock(Metadata.class);
		String tempFile = TempDirectoryCleaner.getTempDirectory() + File.separator+"uniqueId.metadata";
		File file = new File(tempFile);
		JsonObject metadataObj= new JsonObject();
		metadataObj.addProperty("fileName", "fileName");
		metadataObj.addProperty("metadataFileName", "metadataFileName");
		metadataObj.addProperty("location", "location");
		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			try (MockedStatic<JaxbUtils> mockedStatic2 = mockStatic(JaxbUtils.class)) {
				try (MockedStatic<ApplicationContextAccessor> mockedStatic3 = mockStatic(
						ApplicationContextAccessor.class)) {
					try(MockedStatic<SecurityUtils> mockedStatic4 = mockStatic(SecurityUtils.class)){
						
					
					mockedStatic3.when(() -> ApplicationContextAccessor.getBean(AdhocReport.class))
							.thenReturn(adhocReport);
					mockedStatic3.when(() -> ApplicationContextAccessor.getBean(CanvasElements.class))
							.thenReturn(canvasElements);
					mockedStatic3.when(() -> ApplicationContextAccessor.getBean(MetadataReference.class))
					.thenReturn(metadataReference);
					
					mockedStatic2.when(() -> JaxbUtils.unMarshal(Metadata.class, file)).thenReturn(metadata);

					mockedStatic.when(() -> JsonUtils.getMetadataExtension()).thenReturn("metadata");
					mockedStatic.when(() -> JsonUtils.getHrReportExtension()).thenReturn("report");
					JsonArray columns = new JsonArray();
					method.invoke(creator, solutionDirectory, "Temp", "System", "sampleReport", columns.toString(), "groups",
							"uniqueId", state, "description", true, metadataObj, true);
				}
				}
			}
		}

	}
	
	@Test
	public void ut_a9_test_process() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		AdhocReportCreator creator = new AdhocReportCreator();

		Method method = AdhocReportCreator.class.getDeclaredMethod("process", String.class, String.class, String.class,
				String.class, String.class, String.class, String.class, JsonObject.class, String.class, boolean.class,
				JsonObject.class, Boolean.class);
		method.setAccessible(true);
		String solutionDirectory = ApplicationProperties.INSTANCE.getSolutionDirectory();
		JsonObject state = new JsonObject();
		MetadataReference metadataReference = mock(MetadataReference.class);
		CanvasElements canvasElements = mock(CanvasElements.class);
		AdhocReport adhocReport = mock(AdhocReport.class);
		Metadata metadata = mock(Metadata.class);
		
		when(adhocReport.getCanvasElements()).thenReturn(canvasElements);
		when(adhocReport.getMetadataReference()).thenReturn(metadataReference);
		
		String tempFile = TempDirectoryCleaner.getTempDirectory() + File.separator+"uniqueId.metadata";
		File file = new File(tempFile);
		String tempPath = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
		File file2 = new File(tempPath);
		JsonObject metadataObj= new JsonObject();
		metadataObj.addProperty("fileName", "fileName");
		metadataObj.addProperty("metadataFileName", "metadataFileName");
		metadataObj.addProperty("location", "location");
		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			try (MockedStatic<JaxbUtils> mockedStatic2 = mockStatic(JaxbUtils.class)) {
				try (MockedStatic<ApplicationContextAccessor> mockedStatic3 = mockStatic(
						ApplicationContextAccessor.class)) {
					try(MockedStatic<SecurityUtils> mockedStatic4 = mockStatic(SecurityUtils.class)){
						
					
					mockedStatic3.when(() -> ApplicationContextAccessor.getBean(AdhocReport.class))
							.thenReturn(adhocReport);
					mockedStatic3.when(() -> ApplicationContextAccessor.getBean(CanvasElements.class))
							.thenReturn(canvasElements);
					mockedStatic3.when(() -> ApplicationContextAccessor.getBean(MetadataReference.class))
					.thenReturn(metadataReference);
					
					mockedStatic2.when(() -> JaxbUtils.unMarshal(Metadata.class, file)).thenReturn(metadata);
					mockedStatic2.when(() -> JaxbUtils.unMarshal(AdhocReport.class, file2)).thenReturn(adhocReport);

					mockedStatic.when(() -> JsonUtils.getMetadataExtension()).thenReturn("metadata");
					mockedStatic.when(() -> JsonUtils.getHrReportExtension()).thenReturn("report");
					JsonArray columns = new JsonArray();
					method.invoke(creator, solutionDirectory, "Temp", "System", "sampleReport", columns.toString(), "groups",
							"uniqueId", state, "description", false, metadataObj, true);
				}
				}
			}
		}

	}


		@Test
	public void ut_b1_test_isThreadSafeToCache() {
		AdhocReportCreator creator = new AdhocReportCreator();
		boolean threadSafeToCache = creator.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
}
