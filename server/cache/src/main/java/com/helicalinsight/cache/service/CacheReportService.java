package com.helicalinsight.cache.service;

import com.helicalinsight.cache.model.CacheReport;

import java.util.List;

public interface CacheReportService {

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
