package com.helicalinsight.adhoc.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.DataSourceDeleteUtilsDB;
import com.helicalinsight.efw.components.EfwdDeleteUtility;
import com.helicalinsight.efw.exceptions.DuplicateDatasourceConnectionException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CascadedDatasourceDeleteHandlerTest {

	@Test(expected = EfwServiceException.class)
	public void ut_a1_test_deleteDataSource() {
		CascadedDatasourceDeleteHandler handler = new CascadedDatasourceDeleteHandler();
		JsonObject formDataJson = new JsonObject();
		IComponent dataSourceRelatedComponent = mock(IComponent.class);
		
		when(dataSourceRelatedComponent.executeComponent(anyString())).thenReturn(formDataJson.toString());
		try(MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)){
			mockedStatic.when(()-> FactoryMethodWrapper.getTypedInstance("com.helicalinsight.adhoc.services.DataSourceRelatedFiles", IComponent.class))
								.thenReturn(dataSourceRelatedComponent);
			handler.deleteDataSource(formDataJson, "dataSourceProvider", "12");
		}
		
	}
	
	@Test
	public void ut_a2_test_deleteDataSource() {
		CascadedDatasourceDeleteHandler handler = new CascadedDatasourceDeleteHandler();
		JsonObject formDataJson = new JsonObject();
		IComponent dataSourceRelatedComponent = mock(IComponent.class);
		
		when(dataSourceRelatedComponent.executeComponent(anyString())).thenReturn(formDataJson.toString());
		try(MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)){
			try(MockedConstruction<DataSourceDeleteUtilsDB> construction = mockConstruction(DataSourceDeleteUtilsDB.class,(mock,context)->{
				when(mock.marshalDelete(anyString(), anyString(), any(JsonObject.class), anyString())).thenReturn("dataSourceDeleteMessage");
			})){
				mockedStatic.when(()-> FactoryMethodWrapper.getTypedInstance("com.helicalinsight.adhoc.services.DataSourceRelatedFiles", IComponent.class))
				.thenReturn(dataSourceRelatedComponent);
				String deleteDataSource = handler.deleteDataSource(formDataJson, "dataSourceProvider", "12");
				assertEquals("dataSourceDeleteMessage",deleteDataSource);
			}
			
		}
		
	}
	
	@Test
	public void ut_a3_test_deleteDataSource() {
		CascadedDatasourceDeleteHandler handler = new CascadedDatasourceDeleteHandler();
		JsonObject formDataJson = new JsonObject();
		IComponent dataSourceRelatedComponent = mock(IComponent.class);
		
		when(dataSourceRelatedComponent.executeComponent(anyString())).thenReturn(formDataJson.toString());
		try(MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)){
			try(MockedConstruction<EfwdDeleteUtility> construction = mockConstruction(EfwdDeleteUtility.class,(mock,context)->{
				
			})){
				mockedStatic.when(()-> FactoryMethodWrapper.getTypedInstance("com.helicalinsight.adhoc.services.DataSourceRelatedFiles", IComponent.class))
				.thenReturn(dataSourceRelatedComponent);
				String deleteDataSource = handler.deleteDataSource("12", "classifier","type", "directory");
				String result = JsonParser.parseString(deleteDataSource).getAsJsonObject().get("message").getAsString();
				assertEquals(" Could not delete metadata Datasource deleted successfully",result);
			}
			
		}
		
	}
	
	@Test
	public void ut_a4_test_deleteDataSource() {
		CascadedDatasourceDeleteHandler handler = new CascadedDatasourceDeleteHandler();
		JsonObject formDataJson = new JsonObject();
		JsonArray metadataFiles = new JsonArray();
		JsonObject singleMetadata = new JsonObject();
		singleMetadata.addProperty("path", "path");
		JsonArray reportJsonArray = new JsonArray();
		JsonObject singlereport = new JsonObject();
		singlereport.addProperty("reportFileName", "reportFileName");
		singlereport.addProperty("location", "location");
		JsonArray savedReportsJson = new JsonArray();
		JsonObject singleSavedValue = new JsonObject();
		singleSavedValue.addProperty("sheduledReportName", "sheduledReportName");
		savedReportsJson.add(singleSavedValue);
		singlereport.add("savedReports", savedReportsJson);
		reportJsonArray.add(singlereport);
		singleMetadata.add("reportDetails", reportJsonArray);
		metadataFiles.add(singleMetadata);
		formDataJson.add("metadataFiles", metadataFiles);
		IComponent dataSourceRelatedComponent = mock(IComponent.class);
		JsonObject messageObj = new JsonObject();
		messageObj.addProperty("message", "Metadata and related files deleted successfully.");
		when(dataSourceRelatedComponent.executeComponent(anyString())).thenReturn(formDataJson.toString()).thenReturn(messageObj.toString());
		try(MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)){
			try(MockedConstruction<EfwdDeleteUtility> construction = mockConstruction(EfwdDeleteUtility.class,(mock,context)->{
				
			})){
				mockedStatic.when(()-> FactoryMethodWrapper.getTypedInstance(anyString(), any()))
				.thenReturn(dataSourceRelatedComponent);
				String deleteDataSource = handler.deleteDataSource("12", "classifier","type", "directory");
				String result = JsonParser.parseString(deleteDataSource).getAsJsonObject().get("message").getAsString();
				assertEquals("Metadata deleted successfully  Datasource deleted successfully",result);
				
			}
			
		}
		
	}
	
	@Test(expected = RuntimeException.class)
	public void ut_a5_test_deleteDataSource() {
		CascadedDatasourceDeleteHandler handler = new CascadedDatasourceDeleteHandler();
		JsonObject formDataJson = new JsonObject();
		JsonArray metadataFiles = new JsonArray();
		JsonObject singleMetadata = new JsonObject();
		singleMetadata.addProperty("path", "path");
		JsonArray reportJsonArray = new JsonArray();
		JsonObject singlereport = new JsonObject();
		singlereport.addProperty("reportFileName", "reportFileName");
		singlereport.addProperty("location", "location");
		JsonArray savedReportsJson = new JsonArray();
		JsonObject singleSavedValue = new JsonObject();
		singleSavedValue.addProperty("sheduledReportName", "sheduledReportName");
		savedReportsJson.add(singleSavedValue);
		singlereport.add("savedReports", savedReportsJson);
		reportJsonArray.add(singlereport);
		singleMetadata.add("reportDetails", reportJsonArray);
		metadataFiles.add(singleMetadata);
		formDataJson.add("metadataFiles", metadataFiles);
		IComponent dataSourceRelatedComponent = mock(IComponent.class);
		JsonObject messageObj = new JsonObject();
		messageObj.addProperty("message", "Metadata and related files deleted successfully.");
		when(dataSourceRelatedComponent.executeComponent(anyString())).thenReturn(formDataJson.toString()).thenReturn(messageObj.toString());
		try(MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)){
			try(MockedConstruction<EfwdDeleteUtility> construction = mockConstruction(EfwdDeleteUtility.class,(mock,context)->{
				doAnswer(invocation -> {
					throw new DuplicateDatasourceConnectionException("Mocked exception");
				}).when(mock).delete();
			})){
				mockedStatic.when(()-> FactoryMethodWrapper.getTypedInstance(anyString(), any()))
				.thenReturn(dataSourceRelatedComponent);
				handler.deleteDataSource("12", "classifier","type", "directory");
				
			}
			
		}
		
	}
}
