package com.helicalinsight.cache;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;

public class CacheTest {

	Cache cache = new Cache();
	@Test
	public void ut_a1_setPrioprity() {
		Integer id = 123;
		cache.setPriority(id);
		Integer priority = cache.getPriority();
		assertEquals(priority, id);
	}
	
	@Test
	public void ut_a2_setCacheId() {
		long id = 123l;
		cache.setCacheId(id);
		long priority = cache.getCacheId();
		assertEquals(priority, id);
	}
	@Test
	public void ut_a3_test() {
		List<CacheReport> reportList = new ArrayList<>();
		Cache cache = new Cache(reportList);
	}
	@Test
	public void testreportList() {
		Cache cache = new Cache();
		cache.getMapId();
	}
	@Test
	public void testsetCacheExpiryTime() {
		Cache cache = new Cache();
		cache.setCacheExpiryTime(new Date());
		Assert.assertNotNull(cache.getCacheExpiryTime());
	}
	@Test
	public void testsetCacheFilePath() {
		Cache cache = new Cache();
		cache.setCacheFilePath("test");
		Assert.assertNotNull(cache.getCacheFilePath());
	}
	@Test
	public void testsetCacheFileSize() {
		Cache cache = new Cache();
		cache.setCacheFileSize(11l);
		Assert.assertNotNull(cache.getCacheFileSize());
	}
	@Test
	public void testsetCacheFileTimeStamp() {
		Cache cache = new Cache();
		cache.setCacheFileTimeStamp(new Date());
		Assert.assertNotNull(cache.getCacheFileTimeStamp());
	}
	@Test
	public void testgetConnectionId() {
		Cache cache = new Cache();
		cache.setConnectionId(1l);
		Assert.assertNotNull(cache.getConnectionId());
	}
	@Test
	public void testsetConnectionType() {
		Cache cache = new Cache();
		cache.setConnectionType("type");
		Assert.assertNotNull(cache.getConnectionType());
	}
	@Test
	public void testsetQuery() {
		Cache cache = new Cache();
		cache.setQuery("from cahce");
		Assert.assertNotNull(cache.getQuery());
	}
	@Test
	public void testsetReportList() {
		Cache cache = new Cache();
		List<CacheReport> reportList = new ArrayList<>();
		cache.setReportList(reportList);
		Assert.assertNotNull(cache.getReportList());
	}
	@Test
	public void testsetStatus() {
		Cache cache = new Cache();
		cache.setStatus("temp");
		Assert.assertNotNull(cache.getStatus());
	}
	@Test
	public void testsetParameters() {
		Cache cache = new Cache();
		cache.setParameters("temp");
		Assert.assertNotNull(cache.getParameters());
	}
	@Test
	public void testsetNoOfRecords() {
		Cache cache = new Cache();
		cache.setNoOfRecords(12);
		Assert.assertNotNull(cache.getNoOfRecords());
		cache.toString();
	}
	@Test
	public void testSetMapId() {
		Integer id = 12;
		cache.setMapId(id);
		Integer mapId = cache.getMapId();
		assertEquals(mapId,id);
		
	}
	@Test
	public void testSetConnectionFilePath() {
		String path = "path";
		cache.setConnectionFilePath(path);
		String connectionFilePath = cache.getConnectionFilePath();
		assertEquals(connectionFilePath,path);
	}
}
