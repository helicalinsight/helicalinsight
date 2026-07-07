package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;



public class IMetadataTest {

	@Test
	public void ut_a1_test_getMetadataDB() {
		IMetadata iMetadata = new GenericDatabaseMetadataProvider();
		Map<String,Object> jsonInformation = new HashMap<>();
		String metadataDB = iMetadata.getMetadataDB(jsonInformation);
		assertNull(metadataDB);
	}
	@Test
	public void ut_a2_test_MetadataContextAndViewNameExistsException () {
		MetadataContext context = new MetadataContext();
		ViewNameExistsException exception = new ViewNameExistsException("message");
	}
}
