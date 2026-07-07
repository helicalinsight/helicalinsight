package com.helicalinsight.cache.dao;

import com.helicalinsight.cache.model.CacheReport;

import java.util.List;

/**
 * Created by Somen on 5/27/2015.
 */
public interface CacheReportDao {
    Long addCacheReport(CacheReport cacheReport);

    void editCacheReport(CacheReport cacheReport);

    void deleteCacheReport(Long cacheReportId);

    CacheReport getCacheReport(Long cacheReportId);

    List<CacheReport> getCacheReportByCacheId(Long cacheId);

    List<CacheReport> getAllReports();

    List<CacheReport> getReports(String directory);

    List<String> getAllUniqueReports();

    List<String> getUniqueReports(String directory);

    void deleteAllCacheReport();
}


