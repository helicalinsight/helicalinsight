package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataUtilityTest {

	@Test
	public void ut_a1_test_MetadataUtility() {
		MetadataUtility metadataUtility = new MetadataUtility("connProvider", "metadataImpl", "type");
		String connectionProvider = metadataUtility.getConnectionProvider();
		String metadataImplementation = metadataUtility.getMetadataImplementation();
		String type = metadataUtility.getType();
		assertEquals("connProvider", connectionProvider);
		assertEquals("metadataImpl", metadataImplementation);
		assertEquals("type", type);
		
		String string = metadataUtility.toString();
		assertTrue(string.contains("metadataImpl"));
	}
	
}
