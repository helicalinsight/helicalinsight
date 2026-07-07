package com.helicalinsight.cache.store;

import java.util.Map;

import com.helicalinsight.admin.dto.CacheKeyDTO;

public interface ApplicationCacheStore {

	void put(CacheKeyDTO key, Object context);

	Object get(String key);

	void remove(String key);

	void rebuild();

	void clear();

	Map<String, Object> dump();

	Long size();
}