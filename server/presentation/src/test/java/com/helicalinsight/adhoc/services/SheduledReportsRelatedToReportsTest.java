package com.helicalinsight.adhoc.services;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbContexts;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SheduledReportsRelatedToReportsTest {

	
	
	@Test
	public void ut_a1_test_executeComponent() throws JAXBException {
		SheduledReportsRelatedToReports report = new SheduledReportsRelatedToReports();
		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("adhocReportFileName", "adhocReportFileName");

		IComponent reportStatisticsProviderComponent = mock(IComponent.class);
		JaxbContexts jaxbContexts = mock(JaxbContexts.class);
		JAXBContext jaxbContext = mock(JAXBContext.class);
		Unmarshaller unmarshaller = mock(Unmarshaller.class);
		Efwsr efwsrFileObj = mock(Efwsr.class);

		JsonObject formdataObject = new JsonObject();
		JsonObject resultAsJson = new JsonObject();
		JsonArray allFilesAvailableToLoggedInUser = new JsonArray();
		resultAsJson.add("latestReports", allFilesAvailableToLoggedInUser);

		JsonArray designerReportJsonArray = new JsonArray();
		JsonObject childrenObject = new JsonObject();
		childrenObject.addProperty("reportPath", "designer.hr");
		childrenObject.addProperty("file", "designer.hr");
		designerReportJsonArray.add(childrenObject);

		JsonObject stateJSON = new JsonObject();
		JsonObject componentsJsonObject = new JsonObject();
		JsonObject singleJSON = new JsonObject();
		JsonObject optionJSON = new JsonObject();
		optionJSON.addProperty("file", "adhocReportFileName");
		optionJSON.addProperty("dir", "dir");
		singleJSON.add("options", optionJSON);;
		componentsJsonObject.add("key", singleJSON);
		stateJSON.add("components", componentsJsonObject);
		

		when(efwsrFileObj.getReportFile()).thenReturn("adhocReportFileName");
		when(unmarshaller.unmarshal(any(File.class))).thenReturn(efwsrFileObj);
		when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
		when(reportStatisticsProviderComponent.executeComponent(anyString())).thenReturn(resultAsJson.toString());
		when(jaxbContexts.getContextForClass(Efwsr.class)).thenReturn(jaxbContext);
		try (MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)) {
			try (MockedStatic<AdhocServiceUtils> mockedStatic2 = mockStatic(AdhocServiceUtils.class)) {
				try (MockedStatic<JaxbContexts> mockedStatic3 = mockStatic(JaxbContexts.class)) {
					mockedStatic3.when(() -> JaxbContexts.getJaxbContexts()).thenReturn(jaxbContexts);

					mockedStatic2.when(() -> AdhocServiceUtils.getSpecificExtension(any(JsonArray.class), anyString()))
							.thenReturn(designerReportJsonArray);
					mockedStatic2.when(() -> AdhocServiceUtils.prepareFormData()).thenReturn(formdataObject);
					mockedStatic.when(() -> FactoryMethodWrapper.getTypedInstance(anyString(), any()))
							.thenReturn(reportStatisticsProviderComponent);

					report.executeComponent(formDataJson.toString());
				}
			}
		}
	}

	@Test
	public void ut_a2_test_executeComponent() throws JAXBException {
		SheduledReportsRelatedToReports report = new SheduledReportsRelatedToReports();
		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("adhocReportFileName", "adhocReportFileName");

		IComponent reportStatisticsProviderComponent = mock(IComponent.class);
		JaxbContexts jaxbContexts = mock(JaxbContexts.class);
		JAXBContext jaxbContext = mock(JAXBContext.class);
		Unmarshaller unmarshaller = mock(Unmarshaller.class);
		Efwsr efwsrFileObj = mock(Efwsr.class);

		JsonObject formdataObject = new JsonObject();
		JsonObject resultAsJson = new JsonObject();
		JsonArray allFilesAvailableToLoggedInUser = new JsonArray();
		resultAsJson.add("latestReports", allFilesAvailableToLoggedInUser);

		JsonArray designerReportJsonArray = new JsonArray();
		JsonObject childrenObject = new JsonObject();
		childrenObject.addProperty("reportPath", "designer.hr");
		childrenObject.addProperty("file", "designer.hr");
		designerReportJsonArray.add(childrenObject);

		JsonObject stateJSON = new JsonObject();
		JsonObject componentsJsonObject = new JsonObject();
		JsonObject singleJSON = new JsonObject();
		JsonObject optionJSON = new JsonObject();
		optionJSON.addProperty("file", "adhocReportFileName");
		optionJSON.addProperty("dir", "dir");
		singleJSON.add("options", optionJSON);
		componentsJsonObject.add("key", singleJSON);
		stateJSON.add("components", componentsJsonObject);
		

		when(efwsrFileObj.getReportFile()).thenReturn("adhocReportFileName");
		when(unmarshaller.unmarshal(any(File.class))).thenReturn(efwsrFileObj);
		when(jaxbContext.createUnmarshaller()).thenThrow(new JAXBException("Mocked Exception"));
		when(reportStatisticsProviderComponent.executeComponent(anyString())).thenReturn(resultAsJson.toString());
		when(jaxbContexts.getContextForClass(Efwsr.class)).thenReturn(jaxbContext);
		try (MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)) {
			try (MockedStatic<AdhocServiceUtils> mockedStatic2 = mockStatic(AdhocServiceUtils.class)) {
				try (MockedStatic<JaxbContexts> mockedStatic3 = mockStatic(JaxbContexts.class)) {
					mockedStatic3.when(() -> JaxbContexts.getJaxbContexts()).thenReturn(jaxbContexts);

					mockedStatic2.when(() -> AdhocServiceUtils.getSpecificExtension(any(JsonArray.class), anyString()))
							.thenReturn(designerReportJsonArray);
					mockedStatic2.when(() -> AdhocServiceUtils.prepareFormData()).thenReturn(formdataObject);
					mockedStatic.when(() -> FactoryMethodWrapper.getTypedInstance(anyString(), any()))
							.thenReturn(reportStatisticsProviderComponent);

					report.executeComponent(formDataJson.toString());
				}
			}
		}
	}
	@Test
	public void ut_a3_test_isThreadSafeToCache() {
		SheduledReportsRelatedToReports report = new SheduledReportsRelatedToReports();
		boolean threadSafeToCache = report.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
	
}
