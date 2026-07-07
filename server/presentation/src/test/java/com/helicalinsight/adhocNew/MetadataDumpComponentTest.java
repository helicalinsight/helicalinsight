package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.MetadataDumpComponent;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataDBUtility;
import com.helicalinsight.resourcedb.MetadataDumpDTO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataDumpComponentTest {

	@Test
	public void ut_a1_test_executeComponent() {
		MetadataDumpComponent component = new MetadataDumpComponent();
		String invalidJson = "{ \"name\": \"John\" \"age\": \"thirty\" }";


		try(MockedConstruction<MetadataDBUtility> construction = mockConstruction(MetadataDBUtility.class,(mock,context)->{
			when(mock.getCachedMetadata()).thenReturn(null);
		})){
			String executeComponent = component.executeComponent(invalidJson);
			JsonObject asJsonObject = JsonParser.parseString(executeComponent).getAsJsonObject();
			assertTrue(asJsonObject.get("data").isJsonNull());
		}
	}
	
	@Test
	public void ut_a2_test_executeComponent() {
		MetadataDumpComponent component = new MetadataDumpComponent();
		String invalidJson = "{ \"name\": \"John\", \"age\": \"thirty\" }";
		MetadataDumpDTO dumpDTO = mock(MetadataDumpDTO.class);
		List<MetadataDumpDTO> metadata = new ArrayList<>();
		metadata.add(dumpDTO);
		try(MockedConstruction<MetadataDBUtility> construction = mockConstruction(MetadataDBUtility.class,(mock,context)->{
			when(mock.getCachedMetadata()).thenReturn(metadata);
		})){
			String executeComponent = component.executeComponent(invalidJson);
			JsonObject asJsonObject = JsonParser.parseString(executeComponent).getAsJsonObject();
			assertFalse(asJsonObject.getAsJsonArray("data").isEmpty());
		}
	}
	@Test
	public void ut_a3_test_isThreadSafeToCache() {
		MetadataDumpComponent component = new MetadataDumpComponent();
		boolean threadSafeToCache = component.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
}
