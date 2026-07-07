package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.helicalinsight.adhoc.MetadataCacheStatus;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataCacheStatusTest {

	@Test
	public void ut_a1_test_getCached() {
		MetadataCacheStatus cacheStatus = new MetadataCacheStatus();
		cacheStatus.setCached(true);
		assertTrue(cacheStatus.getCached());
	}
	
	@Test
	public void ut_a2_test_getLastUpdateTime() {
		MetadataCacheStatus cacheStatus = new MetadataCacheStatus();
		Date d = new Date();
		cacheStatus.setLastUpdateTime(d);
		Date lastUpdateTime = cacheStatus.getLastUpdateTime();
		assertEquals(d,lastUpdateTime);
	}
	@Test
	public void ut_a3_test_setMetadataId() {
		Date d = new Date();
		MetadataCacheStatus cacheStatus = new MetadataCacheStatus(true, d, 123);
		cacheStatus.setMetadataId(123);
		Integer id = 123;
		Integer metadataId = cacheStatus.getMetadataId();
		assertEquals(id, metadataId);
	}
}
