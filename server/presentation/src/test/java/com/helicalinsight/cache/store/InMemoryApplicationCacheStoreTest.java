package com.helicalinsight.cache.store;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.helicalinsight.admin.dto.CacheKeyDTO;
import com.helicalinsight.cache.CacheHelper;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.service.CacheService;

public class InMemoryApplicationCacheStoreTest {

	private CacheService cacheService;
	private CacheHelper cacheHelper;
	private InMemoryApplicationCacheStore store;

	@Before
	public void setup() {
		cacheService = mock(CacheService.class);
		cacheHelper = mock(CacheHelper.class);
		store = new InMemoryApplicationCacheStore(cacheService, cacheHelper);
	}

	private void withCacheUtils(MockedStatic<CacheUtils> mocked, Runnable action) {
		mocked.when(CacheUtils::getActualCacheExpireDuration).thenReturn(3_600_000L);
		mocked.when(CacheUtils::getInMemoryCacheSize).thenReturn(100);
		action.run();
	}

	@Test
	public void testRemove_invalidatesStoreWhenDbCacheNotFound() {
		try (MockedStatic<CacheUtils> mocked = mockStatic(CacheUtils.class)) {
			withCacheUtils(mocked, () -> {
				String key = "design-cache-key";
				store.put(new CacheKeyDTO(key, "report-ref"), new Object());
				when(cacheService.findUniqueCacheByFilePath(key)).thenReturn(null);

				store.remove(key);

				assertNull(store.get(key));
			});
		}
	}

	@Test
	public void testRemove_deletesDbCacheAndInvalidatesStore() {
		try (MockedStatic<CacheUtils> mocked = mockStatic(CacheUtils.class)) {
			withCacheUtils(mocked, () -> {
				String key = "design-cache-key";
				store.put(new CacheKeyDTO(key, "report-ref"), new Object());

				com.helicalinsight.cache.model.Cache dbCache = new com.helicalinsight.cache.model.Cache();
				dbCache.setCacheId(42L);
				when(cacheService.findUniqueCacheByFilePath(key)).thenReturn(dbCache);

				store.remove(key);

				verify(cacheService).deleteCache(42L);
				assertNull(store.get(key));
			});
		}
	}

}
