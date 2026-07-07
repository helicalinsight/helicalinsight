package com.helicalinsight.adhoc.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.DataSourceDeleteUtils;
import com.helicalinsight.datasource.DataSourceDeleteUtilsDB;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.utility.JsonUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SimpleDataSourceDeleteHandlerTest {

	@Test(expected = EfwServiceException.class)
	public void ut_a1_test_deleteDataSource() {
		SimpleDataSourceDeleteHandler dataSourceDeleteHandler = new SimpleDataSourceDeleteHandler();
		JsonObject formDataJson = new JsonObject();
		dataSourceDeleteHandler.deleteDataSource(formDataJson, "dataSourceProvider", "123");
	}

	@Test
	public void ut_a2_test_deleteDataSource() {
		SimpleDataSourceDeleteHandler dataSourceDeleteHandler = new SimpleDataSourceDeleteHandler();
		JsonObject formDataJson = new JsonObject();

		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			try (MockedStatic<DataSourceDeleteUtils> mockedStatic2 = mockStatic(DataSourceDeleteUtils.class)) {
				mockedStatic2.when(() -> DataSourceDeleteUtils.marshal(anyString(), anyString(), any(), anyString()))
						.thenReturn("success");
				mockedStatic.when(() -> JsonUtils.isDSTypeStorageDatabase()).thenReturn(false);

				String deleteDataSource = dataSourceDeleteHandler.deleteDataSource(formDataJson, "dataSourceProvider",
						"123");
				assertEquals("success", deleteDataSource);
			}

		}

	}

	@Test
	public void ut_a3_test_deleteDataSource() {
		SimpleDataSourceDeleteHandler dataSourceDeleteHandler = new SimpleDataSourceDeleteHandler();
		JsonObject formDataJson = new JsonObject();

		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			try (MockedConstruction<DataSourceDeleteUtilsDB> construction = mockConstruction(
					DataSourceDeleteUtilsDB.class, (mock, context) -> {
						when(mock.marshalDelete(anyString(), anyString(), any(), anyString()))
								.thenReturn(formDataJson.toString());
					})) {

				mockedStatic.when(() -> JsonUtils.isDSTypeStorageDatabase()).thenReturn(true);

				String deleteDataSource = dataSourceDeleteHandler.deleteDataSource(formDataJson, "dataSourceProvider",
						"123");
				String response = JsonParser.parseString(deleteDataSource).getAsJsonObject().get("message")
						.getAsString();
				assertEquals("The datasource 123 have been deleted successfully", response);
			}

		}

	}
}
