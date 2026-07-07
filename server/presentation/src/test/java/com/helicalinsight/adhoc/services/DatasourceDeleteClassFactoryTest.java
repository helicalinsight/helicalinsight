package com.helicalinsight.adhoc.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.genericdb.IMetadataDeleteRule;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.io.ImportOperationHandler;
import com.helicalinsight.efw.utility.JsonUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatasourceDeleteClassFactoryTest {

	@Test
	public void ut_a1_test_getIDataSourceDeleteClass() {
		DatasourceDeleteClassFactory classFactory = new DatasourceDeleteClassFactory();

		IDataSourceDeleteRule iDataSourceDeleteClass = DatasourceDeleteClassFactory.getIDataSourceDeleteClass("type");
		assertEquals(null, iDataSourceDeleteClass);

	}

	@Test
	public void ut_a2_test_getIDataSourceDeleteClass() {
		IDataSourceDeleteRule dataSourceDeleteRule = mock(IDataSourceDeleteRule.class);

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(dataSourceDeleteRule);
			IDataSourceDeleteRule iDataSourceDeleteClass = DatasourceDeleteClassFactory
					.getIDataSourceDeleteClass("simple");
			assertEquals(dataSourceDeleteRule, iDataSourceDeleteClass);
		}

	}

	@Test
	public void ut_a3_test_getIDataSourceDeleteClass() {
		IDataSourceDeleteRule dataSourceDeleteRule = mock(IDataSourceDeleteRule.class);

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ImportOperationHandler.class))
					.thenReturn(dataSourceDeleteRule);
			IDataSourceDeleteRule iDataSourceDeleteClass = DatasourceDeleteClassFactory.getIDataSourceDeleteClass(null);
			assertEquals(dataSourceDeleteRule, iDataSourceDeleteClass);
		}

	}

	@Test
	public void ut_a4_test_getIDataSourceDeleteClass() {
		IDataSourceDeleteRule dataSourceDeleteRule = mock(IDataSourceDeleteRule.class);

		JsonObject settingsJson = new JsonObject();
		JsonObject datasourceDeleteHandlers = new JsonObject();
		JsonObject dataSourceDelete = new JsonObject();
		
		dataSourceDelete.addProperty("bean", "bean");
		
		datasourceDeleteHandlers.add("dataSourceDelete", dataSourceDelete);
		settingsJson.add("datasourceDeleteHandlers", datasourceDeleteHandlers);
		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			try (MockedStatic<ApplicationContextAccessor> mockedStatic1 = mockStatic(
					ApplicationContextAccessor.class)) {
				mockedStatic.when(() -> JsonUtils.newGetSettingsJson()).thenReturn(settingsJson);
				mockedStatic1.when(() -> ApplicationContextAccessor.getBean(anyString()))
						.thenReturn(dataSourceDeleteRule);
				IDataSourceDeleteRule iDataSourceDeleteClass = DatasourceDeleteClassFactory
						.getIDataSourceDeleteClass("type");
				assertEquals(dataSourceDeleteRule, iDataSourceDeleteClass);
			}
		}
	}
	
	
	@Test
	public void ut_a5_test_getIMetadataDeleteClass() {
		DatasourceDeleteClassFactory classFactory = new DatasourceDeleteClassFactory();

		IMetadataDeleteRule iMetadataDeleteClass = DatasourceDeleteClassFactory.getIMetadataDeleteClass("type");
		assertEquals(null, iMetadataDeleteClass);

	}

	@Test
	public void ut_a6_test_getIMetadataDeleteClass() {
		IMetadataDeleteRule iMetadataDeleteRule = mock(IMetadataDeleteRule.class);

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).thenReturn(iMetadataDeleteRule);
			IMetadataDeleteRule iMetadataDeleteClass = DatasourceDeleteClassFactory.getIMetadataDeleteClass("simple");
			assertEquals(iMetadataDeleteRule, iMetadataDeleteClass);
		}

	}

	@Test
	public void ut_a7_test_getIMetadataDeleteClass() {
		IMetadataDeleteRule iMetadataDeleteRule = mock(IMetadataDeleteRule.class);

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(ImportOperationHandler.class))
					.thenReturn(iMetadataDeleteRule);
			IMetadataDeleteRule iMetadataDeleteClass = DatasourceDeleteClassFactory.getIMetadataDeleteClass(null);
			assertEquals(iMetadataDeleteRule, iMetadataDeleteClass);
		}

	}

	@Test
	public void ut_a8_test_getIMetadataDeleteClass() {
		IMetadataDeleteRule iMetadataDeleteRule = mock(IMetadataDeleteRule.class);

		JsonObject settingsJson = new JsonObject();
		JsonObject metadataDeleteHandlers = new JsonObject();
		JsonObject metadataDelete = new JsonObject();
		
		metadataDelete.addProperty("bean", "bean");
		
		metadataDeleteHandlers.add("metadataDelete", metadataDelete);
		settingsJson.add("metadataDeleteHandlers", metadataDeleteHandlers);
		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			try (MockedStatic<ApplicationContextAccessor> mockedStatic1 = mockStatic(
					ApplicationContextAccessor.class)) {
				mockedStatic.when(() -> JsonUtils.newGetSettingsJson()).thenReturn(settingsJson);
				mockedStatic1.when(() -> ApplicationContextAccessor.getBean(anyString()))
						.thenReturn(iMetadataDeleteRule);
				IMetadataDeleteRule iMetadataDeleteClass = DatasourceDeleteClassFactory.getIMetadataDeleteClass("type");
				
				assertEquals(iMetadataDeleteRule, iMetadataDeleteClass);
			}
		}
	}
}
