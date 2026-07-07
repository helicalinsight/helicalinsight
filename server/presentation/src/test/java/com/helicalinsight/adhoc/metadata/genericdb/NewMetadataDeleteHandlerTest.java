package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertEquals;
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
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NewMetadataDeleteHandlerTest {

	@Test
	public void ut_a1_test_isThreadSafeToCache() {
		NewMetadataDeleteHandler deleteHandler = new NewMetadataDeleteHandler();
		boolean threadSafeToCache = deleteHandler.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}

	@Test
	public void ut_a2_test_executeComponent() {
		NewMetadataDeleteHandler deleteHandler = new NewMetadataDeleteHandler();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("location", "location");
		formJson.addProperty("metadataFileName", "metadataFileName");
		formJson.addProperty("type", "type");

		JsonObject dataSourceRelatedFilesJson = new JsonObject();
		JsonArray adhocReportsArray = new JsonArray();
		JsonObject singlereport = new JsonObject();
		singlereport.addProperty("reportFileName", "reportFileName");
		singlereport.addProperty("location", "location");
		
		JsonObject singlereport1 = new JsonObject();
		singlereport1.addProperty("reportFileName", "reportFileName");
		singlereport1.addProperty("location", "location");
		JsonArray savedReports = new JsonArray();
		JsonObject singleSavedValue = new JsonObject();
		singleSavedValue.addProperty("sheduledReportName", "mast_report");
		savedReports.add(singleSavedValue);
		singlereport1.add("savedReports", savedReports);
		adhocReportsArray.add(singlereport);
		adhocReportsArray.add(singlereport1);
		dataSourceRelatedFilesJson.add("adhocReports", adhocReportsArray);
		IComponent dataSourceRelatedComponent = mock(IComponent.class);

		when(dataSourceRelatedComponent.executeComponent(anyString())).thenReturn(dataSourceRelatedFilesJson.toString()).thenReturn("success");
		try (MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)) {
			mockedStatic
					.when(() -> FactoryMethodWrapper.getTypedInstance(
							anyString(), any()))
					.thenReturn(dataSourceRelatedComponent);
			
			String executeComponent = deleteHandler.executeComponent(formJson.toString());
			assertEquals("success",executeComponent);
		}
		

	}
}
