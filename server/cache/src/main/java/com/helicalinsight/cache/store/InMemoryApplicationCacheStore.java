package com.helicalinsight.cache.store;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.helicalinsight.admin.dto.CacheKeyDTO;
import com.helicalinsight.cache.CacheHelper;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.efw.jasperintegration.JReportExecutionContext;

import jakarta.annotation.PreDestroy;


@Component
public class InMemoryApplicationCacheStore implements ApplicationCacheStore {

	private static final Logger logger = LoggerFactory.getLogger(InMemoryApplicationCacheStore.class);

	private final AtomicReference<Cache<String, Object>> storeRef = new AtomicReference<>();

	private final CacheService cacheService;
	private final CacheHelper cacheHelper;

	public InMemoryApplicationCacheStore(CacheService cacheService, CacheHelper cacheHelper) {
		this.cacheService = cacheService;
		this.cacheHelper = cacheHelper;
	}

	private Cache<String, Object> store() {

		Cache<String, Object> cache = storeRef.get();
		if (cache == null) {
			Cache<String, Object> newCache = buildCache();
			if (storeRef.compareAndSet(null, newCache)) {
				logger.debug("In-Memory store  cache initialized");
				return newCache;
			}
			return storeRef.get();
		}
		return cache;
	}

	@Override
	public void rebuild() {
		Cache<String, Object> newCache = buildCache();
		Cache<String, Object> oldCache = storeRef.getAndSet(newCache);
		logger.debug("Rebuilding the JReport Cache ");
		if (oldCache != null) {
			oldCache.invalidateAll();
			oldCache.cleanUp();
		}
	}

	@Transactional
	@Override
	public void put(CacheKeyDTO cacheKey, Object context) {
		String key = cacheKey.key();
		logger.debug("inserting cache  : {}", key);
		store().put(key, context);
		addCacheEntry(key, cacheKey.reference());
	}

	@Override
	public Object get(String key) {
		return store().getIfPresent(key);
	}

	@Transactional
	@Override
	public void remove(String key) {
		logger.info("Invalidating cache for key : {}", key);
		com.helicalinsight.cache.model.Cache cache = cacheService.findUniqueCacheByFilePath(key);
		if (cache != null) {
			cacheService.deleteCache(cache.getCacheId());
		}
		store().invalidate(key);
	}

	public Cache<String, Object> buildCache() {

		Cache<String, Object> cache = Caffeine.newBuilder()
				.expireAfterWrite(CacheUtils.getActualCacheExpireDuration(), TimeUnit.MILLISECONDS)
				.maximumSize(CacheUtils.getInMemoryCacheSize())
				.removalListener((String key, Object object, RemovalCause cause) -> {
					if (object != null) {
						logger.debug("Cleanup cache for key :  {}  , cause : {}", key, cause);
						if (object instanceof JReportExecutionContext ctx) {
							ctx.cleanup();
						}
					}
				}).scheduler(Scheduler.systemScheduler()).build();
		return cache;
	}

	@PreDestroy
	@Override
	public void clear() {
		Cache<String, Object> cache = storeRef.get();
		logger.debug("Clearing the Cache");
		if (cache != null) {
			cache.invalidateAll();
			cache.cleanUp();
		}
	}

	@Override
	public Map<String, Object> dump() {
		return store().asMap();
	}

	@Override
	public Long size() {
		return store().estimatedSize();
	}

	private void addCacheEntry(String key, String reference) {
		com.helicalinsight.cache.model.Cache cache = new com.helicalinsight.cache.model.Cache();
		cache.setCacheFilePath(key);
		cache.setQueryLookup(key);
		cache.setQuery(key);
		com.helicalinsight.cache.model.Cache  dbCache = cacheService.findUniqueCacheByFilePath(key);
		if ( dbCache == null ) {
			cacheService.addCache(cache);
			cacheHelper.saveCacheReport(cache.getCacheId(), reference, true);
		}
	}
}
