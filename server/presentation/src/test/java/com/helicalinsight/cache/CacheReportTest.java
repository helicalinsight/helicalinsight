package com.helicalinsight.cache;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;

public class CacheReportTest {

	@Test
	public void testGetSerialVersionUID() {
		CacheReport report = new CacheReport();
		CacheReport.getSerialVersionUID();
	}
	
	@Test
	public void testGetCache() {
		CacheReport report = new CacheReport();
		report.getCache();
	}
	
	@Test
	public void testSetReportPath() {
		CacheReport report = new CacheReport();
		String path = "path";
		report.setReportPath(path);
		String reportPath = report.getReportPath();
		assertEquals(reportPath, path);
	}
	
	@Test
	public void testSetReportId() {
		CacheReport report = new CacheReport();
		Integer id = 12;
		report.setReportId(id);
		Integer reportId = report.getReportId();
		assertEquals(reportId, id);
	}
	@Test
	public void testSetCacheId() {
		CacheReport report = new CacheReport();
		long id = 12l;
		report.setCacheId(id);
		long reportId = report.getCacheId();
		assertEquals(reportId, id);
	}
	@Test
	public void testSetCache() {
		Cache cache = new Cache();
		CacheReport report = new CacheReport();
		report.setCache(cache);
	}
	
	
}
