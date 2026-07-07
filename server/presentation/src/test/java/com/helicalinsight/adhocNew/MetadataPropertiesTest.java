package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.helicalinsight.adhoc.MetadataProperties;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataPropertiesTest {

	@Test
	public void ut_a1_test_getDataBaseName() {
		MetadataProperties metadataProperties = new MetadataProperties();
		metadataProperties.setDataBaseName("dbName");
		assertEquals("dbName", metadataProperties.getDataBaseName());
	}
	
	@Test
	public void ut_a2_test_getMetadataId() {
		MetadataProperties metadataProperties = new MetadataProperties();
		metadataProperties.setMetadataId(123);
		Integer id = 123;
		assertEquals(id, metadataProperties.getMetadataId());
	}
	
	@Test
	public void ut_a3_test_getConnectionType() {
		MetadataProperties metadataProperties = new MetadataProperties();
		metadataProperties.setConnectionType("cType");
		assertEquals("cType", metadataProperties.getConnectionType());
	}
	
	@Test
	public void ut_a4_test_getFileName() {
		MetadataProperties metadataProperties = new MetadataProperties();
		metadataProperties.setFileName("fName");
		assertEquals("fName", metadataProperties.getFileName());
	}
}
