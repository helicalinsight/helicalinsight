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
import com.helicalinsight.adhoc.services.DatasourceDeleteClassFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataDeleteHandlerWithTypeTest {

	@Test
	public void ut_a1_test_isThreadSafeToCache() {
		MetadataDeleteHandlerWithType deleteHandlerWithType = new MetadataDeleteHandlerWithType();
		boolean threadSafeToCache = deleteHandlerWithType.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
	
	@Test
	public void ut_a2_test_executeComponent() {
		MetadataDeleteHandlerWithType deleteHandlerWithType = new MetadataDeleteHandlerWithType();
		JsonObject formJson = new JsonObject();
		JsonArray arr = new JsonArray();
		formJson.addProperty("type", "type");
		formJson.add("metadataFileName", arr);
		formJson.add("reportFileNames", arr);
		IMetadataDeleteRule iMetadataDeleteClass = mock(IMetadataDeleteRule.class);
		when(iMetadataDeleteClass.deleteMetadata(any(), any(), any())).thenReturn("response");
		try(MockedStatic<DatasourceDeleteClassFactory> mockedStatic = mockStatic(DatasourceDeleteClassFactory.class)){
			mockedStatic.when(()-> DatasourceDeleteClassFactory.getIMetadataDeleteClass(anyString())).thenReturn(iMetadataDeleteClass);
			String executeComponent = deleteHandlerWithType.executeComponent(formJson.toString());
			assertEquals("response", executeComponent);
			
		}
	}
}
