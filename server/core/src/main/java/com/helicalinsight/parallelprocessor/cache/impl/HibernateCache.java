package com.helicalinsight.parallelprocessor.cache.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.cache.CacheManager;

import org.ehcache.core.EhcacheManager;
import org.ehcache.core.spi.ServiceLocator;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.core.statistics.TierStatistics;
import org.hibernate.SessionFactory;
import org.hibernate.cache.jcache.internal.JCacheRegionFactory;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.parallelprocessor.cache.ICache;

@Component("hibernate")
public class HibernateCache implements ICache {
	
	private static final Logger logger = LoggerFactory.getLogger(HibernateCache.class);
	
	private final SessionFactory sessionFactory;
	private  CacheManager jCacheManager;
	private  StatisticsService statisticsService;

	public HibernateCache(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		
		SessionFactoryImplementor sfi = sessionFactory.unwrap(SessionFactoryImplementor.class);

        RegionFactory regionFactory = sfi.getServiceRegistry().getService(RegionFactory.class);

		if (!(regionFactory instanceof JCacheRegionFactory jCacheRegionFactory)) {
			logger.debug("Hibernate is not using JCacheRegionFactory: {} " , regionFactory);
		}
		else {
			this.jCacheManager = jCacheRegionFactory.getCacheManager();
			org.ehcache.CacheManager ehcacheManager = jCacheManager.unwrap(org.ehcache.CacheManager.class);
			this.statisticsService = resolveStatisticsService(ehcacheManager);
		}
	}

	private static StatisticsService resolveStatisticsService(org.ehcache.CacheManager ehcacheManager) {
		try {
			Field serviceLocatorField = EhcacheManager.class.getDeclaredField("serviceLocator");
			serviceLocatorField.setAccessible(true);
			ServiceLocator serviceLocator = (ServiceLocator) serviceLocatorField.get(ehcacheManager);
			StatisticsService statisticsService = serviceLocator.getService(StatisticsService.class);
			if (statisticsService == null) {
				logger.debug("Unable to resolve Ehcache StatisticsService");
				
			}
			return statisticsService;
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Unable to resolve Ehcache StatisticsService", e);
		}
	}
	
	
	@Override
	public Object getCache(String id) {
		Statistics statistics = sessionFactory.getStatistics();
		CacheRegionStatistics regionStats = statistics.getCacheRegionStatistics(id);
		
		if (regionStats == null) {
			return Collections.emptyList();
		}
		
		return  List.of(buildRegion(regionStats, id));
	}

	@Override
	public Map<Object, List<JsonObject>> getAllCache() {
		return null;
	}

	@Override
	public void putAllCache(List<Object> objects) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putACache(Object object) {

	}

	@Override
	public boolean updateCache(String id, List<JsonObject> listOfJson) {
		return false;
	}

	@Override
	public boolean searchInCache(Object requestedElement) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean deleteCache(String regionName) {
		try {
			sessionFactory.getCache().evictRegion(regionName);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean deleteAllCache() {
		try {
			sessionFactory.getCache().evictAllRegions();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void setPriority(int priority) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getPriority() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JsonObject getStatus() {

		Statistics statistics = sessionFactory.getStatistics();

		JsonObject status = new JsonObject();
		JsonObject secondLevelCache = new JsonObject();

		long cummulativeHits = statistics.getSecondLevelCacheHitCount();
		long cummulativeMisses = statistics.getSecondLevelCacheMissCount();
		long cummulativePuts = statistics.getSecondLevelCachePutCount();

		double cummulativeHitRatio = (cummulativeHits + cummulativeMisses) == 0 ? 0.0
				: ((double) cummulativeHits / (cummulativeHits + cummulativeMisses));

		secondLevelCache.addProperty("hits", cummulativeHits);
		secondLevelCache.addProperty("misses", cummulativeMisses);
		secondLevelCache.addProperty("puts", cummulativePuts);
		secondLevelCache.addProperty("hitRatio", cummulativeHitRatio);

		long totalEntriesInMemory = 0;
		long totalEntriesOnDisk = 0;

		JsonArray regions = new JsonArray();

		for (String regionName : statistics.getSecondLevelCacheRegionNames()) {

			CacheRegionStatistics regionStats = statistics.getCacheRegionStatistics(regionName);

			if (regionStats == null) {
				continue;
			}

			JsonObject region = buildRegion(regionStats, regionName);
			long entriesInMemory = Long.parseLong(GsonUtility.optStringValue(region, "entriesInMemory","0"));
			long bytesInMemory = Long.parseLong(GsonUtility.optStringValue(region, "bytesInMemory","0"));
			
			totalEntriesInMemory += Math.max(0, entriesInMemory);

			totalEntriesOnDisk += Math.max(0, bytesInMemory);

			regions.add(region);
		}

		secondLevelCache.addProperty("totalEntriesInMemory", validStatistic(totalEntriesInMemory));

		secondLevelCache.addProperty("bytesInMemory", validStatistic(totalEntriesOnDisk));

		secondLevelCache.add("regions", regions);

		status.add("secondLevelCache", secondLevelCache);

		return status;
	}


	private Long validStatistic(long value) {
		return value == Long.MIN_VALUE ? null : value;
	}

	@Override
	public Object getRawCache(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putARawCache(Object key, Object object) {
		throw new UnsupportedOperationException();
	}

	private CacheMetrics getMetrics(String regionName) {
		
		if ( statisticsService == null ) {
			return new CacheMetrics(0, 0);
		}
	    CacheStatistics cacheStatistics = statisticsService.getCacheStatistics(regionName);
	    Map<String, TierStatistics> tiers = cacheStatistics.getTierStatistics();
		TierStatistics heap = tiers.get("OnHeap");
		if (heap == null) {
			return new CacheMetrics(0, 0);
		}
		return new CacheMetrics(heap.getMappings(), heap.getOccupiedByteSize());
	}
	
	private JsonObject buildRegion(CacheRegionStatistics regionStats , String regionName) {

		JsonObject region = new JsonObject();

		region.addProperty("name", regionStats.getRegionName());

		region.addProperty("hits", regionStats.getHitCount());

		region.addProperty("misses", regionStats.getMissCount());

		region.addProperty("puts", regionStats.getPutCount());
		
		CacheMetrics metrics = getMetrics(regionName);
		
		long entriesInMemory = metrics.entries;
		long bytesInMemory = metrics.heapBytes;
		
		region.addProperty("entriesInMemory", validStatistic(entriesInMemory));
		region.addProperty("bytesInMemory", validStatistic(bytesInMemory));
		
		long entriesOnDisk = regionStats.getElementCountOnDisk();

		region.addProperty("entriesOnDisk", validStatistic(entriesOnDisk));

		region.addProperty("sizeInMemory", validStatistic(regionStats.getSizeInMemory()));

		long hits = regionStats.getHitCount();
		long misses = regionStats.getMissCount();

		double hitRatio = (hits + misses) == 0 ? 0.0 : ((double) hits / (hits + misses));

		region.addProperty("hitRatio", hitRatio);
		
		return region;
	}
	
	
	
	record CacheMetrics(long entries, long heapBytes) { }

}
