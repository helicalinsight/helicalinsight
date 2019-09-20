/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.cache.dao;

import com.helicalinsight.cache.model.CacheReport;

import java.util.List;

/**
 * @author Somen
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


