package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.helicalinsight.adhoc.MetadataDriverReferences;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataDriverReferencesTest {

	@Test
	public void ut_a1_test_getConnectionType() {
		MetadataDriverReferences metadataDriverReferences = new MetadataDriverReferences();
		String connectionType = "TestConnectionType";
		metadataDriverReferences.setConnectionType(connectionType);
		assertEquals(connectionType, metadataDriverReferences.getConnectionType());
	}

	@Test
	public void ut_a2_test_getDialect() {
		MetadataDriverReferences metadataDriverReferences = new MetadataDriverReferences();
		String dialect = "TestDialect";
		metadataDriverReferences.setDialect(dialect);
		assertEquals(dialect, metadataDriverReferences.getDialect());
	}

	@Test
	public void ut_a3_test_getDriverClass() {
		MetadataDriverReferences metadataDriverReferences = new MetadataDriverReferences();
		String driverClass = "TestDriverClass";
		metadataDriverReferences.setDriverClass(driverClass);
		assertEquals(driverClass, metadataDriverReferences.getDriverClass());
	}

	@Test
	public void ut_a4_test_getDriverClassReference() {
		MetadataDriverReferences metadataDriverReferences = new MetadataDriverReferences();
		String driverClassReference = "TestDriverClassReference";
		metadataDriverReferences.setDriverClassReference(driverClassReference);
		assertEquals(driverClassReference, metadataDriverReferences.getDriverClassReference());
	}

	@Test
	public void ut_a5_test_getGlobalConnectionId() {
		MetadataDriverReferences metadataDriverReferences = new MetadataDriverReferences();
		Integer globalConnectionId = 42;
		metadataDriverReferences.setGlobalConnectionId(globalConnectionId);
		assertEquals(globalConnectionId, metadataDriverReferences.getGlobalConnectionId());
	}

	@Test
	public void ut_a6_test_getEfwId() {
		MetadataDriverReferences metadataDriverReferences = new MetadataDriverReferences();
		Integer efwId = 99;
		metadataDriverReferences.setEfwId(efwId);
		assertEquals(efwId, metadataDriverReferences.getEfwId());
	}
	
	@Test
    public void ut_a7_test_MetadataDriverReferencesConstructor() {
        String dialect = "TestDialect";
        String driverClass = "TestDriverClass";
        String driverClassReference = "TestDriverClassReference";
        Integer globalConnectionId = 1;
        Integer efwId = 2;

        MetadataDriverReferences metadataDriverReferences = new MetadataDriverReferences(
            dialect, driverClass, driverClassReference, globalConnectionId, efwId);

        assertEquals(dialect, metadataDriverReferences.getDialect());
        assertEquals(driverClass, metadataDriverReferences.getDriverClass());
        assertEquals(driverClassReference, metadataDriverReferences.getDriverClassReference());
        assertEquals(globalConnectionId, metadataDriverReferences.getGlobalConnectionId());
        assertEquals(efwId, metadataDriverReferences.getEfwId());
    }
}
