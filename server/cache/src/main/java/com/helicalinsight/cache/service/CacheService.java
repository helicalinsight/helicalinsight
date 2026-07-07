package com.helicalinsight.cache.service;

import java.util.List;

import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;

/**
 * @author Somen
 *         Created on 5/27/2015.
 */
public interface CacheService {
    Long addCache(Cache cache);

    void editCache(Cache cache);

    void deleteCache(Long cacheId);

    void deleteAllCache();

    Cache findCache(Long cacheId);

    Cache findUniqueCache(Cache sampleCache);

    void addReport(CacheReport report);

    List<Cache> findAllReportedCaches(String reportName);
    
    Cache findUniqueCacheByFilePath(String filePath);
}
