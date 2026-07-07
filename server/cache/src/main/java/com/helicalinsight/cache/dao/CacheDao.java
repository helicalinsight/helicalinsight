package com.helicalinsight.cache.dao;

import java.util.List;

import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;

public interface CacheDao {
    Long addCache(Cache cache);

    void editCache(Cache cache);

    void deleteCache(Long cacheId);

    Cache getCache(Long cacheId);

    Cache findUniqueCache(Cache sampleCache);

    void addReport(CacheReport report);

    void deleteAllCache();
    
    List<Cache> findAllReportedCaches(String reportName);
    
    Cache findUniqueCacheByFilePath(String filePath);

}