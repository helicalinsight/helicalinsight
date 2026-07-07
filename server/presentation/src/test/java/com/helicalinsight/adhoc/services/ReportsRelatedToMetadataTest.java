package com.helicalinsight.adhoc.services;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.report.AdhocReport;
import com.helicalinsight.adhoc.report.ReportOpenHelper;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.resourcesecurity.jaxb.MetadataReference;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReportsRelatedToMetadataTest {

	@Test
	public void ut_a1_test_executeComponent() {
		ReportsRelatedToMetadata metadata = new ReportsRelatedToMetadata();
		IComponent reportStatisticsProviderComponent = mock(IComponent.class);
		AdhocReport adhocReport = mock(AdhocReport.class);
		MetadataReference metadataReference = mock(MetadataReference.class);

		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("metadataFileName", "meta");
		formDataJson.addProperty("location", "loc");

		JsonObject formdataObject = new JsonObject();
		JsonObject resultAsJson = new JsonObject();
		JsonArray allFilesAvailableToLoggedInUser = new JsonArray();
		resultAsJson.add("latestReports", allFilesAvailableToLoggedInUser);

		JsonArray reportJsonArray = new JsonArray();
		JsonObject jsonItem = new JsonObject();
		jsonItem.addProperty("reportPath", "report.hr");
		jsonItem.addProperty("file", "report.hr");
		reportJsonArray.add(jsonItem);

		when(adhocReport.getMetadataReference()).thenReturn(metadataReference);
		when(metadataReference.getMetadataFileName()).thenReturn("meta");
		when(metadataReference.getLocation()).thenReturn("loc");

		when(reportStatisticsProviderComponent.executeComponent(anyString())).thenReturn(resultAsJson.toString());
		try (MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)) {
			try (MockedStatic<AdhocServiceUtils> mockedStatic2 = mockStatic(AdhocServiceUtils.class)) {
				try (MockedStatic<ReportOpenHelper> mockedStatic3 = mockStatic(ReportOpenHelper.class)) {
					try (MockedStatic<SheduledReportsRelatedToReports> mockedStatic4 = mockStatic(
							SheduledReportsRelatedToReports.class)) {
						try (MockedStatic<DesignerReportsRelatedToReport> mockedStatic5 = mockStatic(
								DesignerReportsRelatedToReport.class)) {
							mockedStatic5
									.when(() -> DesignerReportsRelatedToReport
											.extractFilesBasedOnReportFileName(anyString(), any()))
									.thenReturn(allFilesAvailableToLoggedInUser);

							mockedStatic4
									.when(() -> SheduledReportsRelatedToReports
											.extractFilesBasedOnReportFileName(anyString(), any()))
									.thenReturn(allFilesAvailableToLoggedInUser);

							mockedStatic3.when(() -> ReportOpenHelper.getAdhocReport(anyString(), anyString()))
									.thenReturn(adhocReport);
							mockedStatic2.when(() -> AdhocServiceUtils.prepareFormData()).thenReturn(formdataObject);
							mockedStatic2.when(
									() -> AdhocServiceUtils.getSpecificExtension(any(JsonArray.class), anyString()))
									.thenReturn(reportJsonArray);
							mockedStatic.when(() -> FactoryMethodWrapper.getTypedInstance(anyString(), any()))
									.thenReturn(reportStatisticsProviderComponent);
							String executeComponent = metadata.executeComponent(formDataJson.toString());
							assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().has("adhocReports"));
						}
					}
				}
			}
		}
	}

	
	@Test
	public void ut_a2_test_executeComponent() {
		ReportsRelatedToMetadata metadata = new ReportsRelatedToMetadata();
		IComponent reportStatisticsProviderComponent = mock(IComponent.class);
		AdhocReport adhocReport = mock(AdhocReport.class);
		MetadataReference metadataReference = mock(MetadataReference.class);

		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("metadataFileName", "meta");
		formDataJson.addProperty("location", "loc");

		JsonObject formdataObject = new JsonObject();
		JsonObject resultAsJson = new JsonObject();
		JsonArray allFilesAvailableToLoggedInUser = new JsonArray();
		resultAsJson.add("latestReports", allFilesAvailableToLoggedInUser);

		JsonArray reportJsonArray = new JsonArray();
		JsonObject jsonItem = new JsonObject();
		jsonItem.addProperty("reportPath", "System//report.hr");
		jsonItem.addProperty("file", "report.hr");
		reportJsonArray.add(jsonItem);

		when(adhocReport.getMetadataReference()).thenReturn(metadataReference);
		when(metadataReference.getMetadataFileName()).thenReturn("meta");
		when(metadataReference.getLocation()).thenReturn("loc");

		when(reportStatisticsProviderComponent.executeComponent(anyString())).thenReturn(resultAsJson.toString());
		try (MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)) {
			try (MockedStatic<AdhocServiceUtils> mockedStatic2 = mockStatic(AdhocServiceUtils.class)) {
				try (MockedStatic<ReportOpenHelper> mockedStatic3 = mockStatic(ReportOpenHelper.class)) {
					try (MockedStatic<SheduledReportsRelatedToReports> mockedStatic4 = mockStatic(
							SheduledReportsRelatedToReports.class)) {
						try (MockedStatic<DesignerReportsRelatedToReport> mockedStatic5 = mockStatic(
								DesignerReportsRelatedToReport.class)) {
							mockedStatic5
									.when(() -> DesignerReportsRelatedToReport
											.extractFilesBasedOnReportFileName(anyString(), any()))
									.thenReturn(allFilesAvailableToLoggedInUser);

							mockedStatic4
									.when(() -> SheduledReportsRelatedToReports
											.extractFilesBasedOnReportFileName(anyString(), any()))
									.thenReturn(allFilesAvailableToLoggedInUser);

							mockedStatic3.when(() -> ReportOpenHelper.getAdhocReport(anyString(), anyString()))
									.thenReturn(adhocReport);
							mockedStatic2.when(() -> AdhocServiceUtils.prepareFormData()).thenReturn(formdataObject);
							mockedStatic2.when(
									() -> AdhocServiceUtils.getSpecificExtension(any(JsonArray.class), anyString()))
									.thenReturn(reportJsonArray);
							mockedStatic.when(() -> FactoryMethodWrapper.getTypedInstance(anyString(), any()))
									.thenReturn(reportStatisticsProviderComponent);
							String executeComponent = metadata.executeComponent(formDataJson.toString());
							assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().has("adhocReports"));
						}
					}
				}
			}
		}
	}
	
	@Test
	public void ut_a3_test_isThreadSafeToCache() {
		ReportsRelatedToMetadata metadata = new ReportsRelatedToMetadata();
		boolean threadSafeToCache = metadata.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
}
