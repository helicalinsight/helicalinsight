package com.helicalinsight.adhoc.services;

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

import com.google.gson.JsonObject;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatasourceDeleteComponentTest {

	@Test
	public void ut_a1_test_executeComponent() {
		DatasourceDeleteComponent datasourceDeleteComponent = new DatasourceDeleteComponent();
		IComponent managedDsShutdown = mock(IComponent.class);
		IDataSourceDeleteRule iDataSourceDeleteClass = mock(IDataSourceDeleteRule.class);

		JsonObject formDataJson = new JsonObject();
		formDataJson.addProperty("dataSourceProvider", "dataSourceProvider");
		formDataJson.addProperty("id", "123");
		formDataJson.addProperty("type", "type");
		when(iDataSourceDeleteClass.deleteDataSource(any(), anyString(), anyString())).thenReturn("done");
		try (MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)) {
			try (MockedStatic<DatasourceDeleteClassFactory> mockedStatic2 = mockStatic(
					DatasourceDeleteClassFactory.class)) {
				mockedStatic2.when(() -> DatasourceDeleteClassFactory.getIDataSourceDeleteClass(anyString()))
						.thenReturn(iDataSourceDeleteClass);

				mockedStatic.when(() -> FactoryMethodWrapper.getTypedInstance(anyString(), any()))
						.thenReturn(managedDsShutdown);
				String executeComponent = datasourceDeleteComponent.executeComponent(formDataJson.toString());
				assertEquals("done", executeComponent);
			}
		}

	}
	@Test
	public void ut_a2_test_isThreadSafeToCache() {
		DatasourceDeleteComponent datasourceDeleteComponent = new DatasourceDeleteComponent();
		boolean threadSafeToCache = datasourceDeleteComponent.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
}
