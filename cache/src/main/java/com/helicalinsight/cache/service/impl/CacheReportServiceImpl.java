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

package com.helicalinsight.cache.service.impl;

import com.helicalinsight.cache.dao.CacheReportDao;
import com.helicalinsight.cache.model.CacheReport;
import com.helicalinsight.cache.service.CacheReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Somen on 5/28/2015.
 */

@Service
public class CacheReportServiceImpl implements CacheReportService {
    @Autowired
    private CacheReportDao cachereportDao;

    @Override
    @Transactional
    public List<CacheReport> getAllReports() {
        return cachereportDao.getAllReports();
    }

    @Override
    @Transactional
    public List<CacheReport> getReports(String directory) {
        return cachereportDao.getReports(directory);
    }

    @Override
    @Transactional
    public Long addCacheReport(CacheReport cacheReport) {
        return cachereportDao.addCacheReport(cacheReport);
    }

    @Override
    @Transactional
    public void editCacheReport(CacheReport cacheReport) {
        cachereportDao.editCacheReport(cacheReport);
    }

    @Override
    @Transactional
    public void deleteCacheReport(Long cacheReportId) {
        cachereportDao.deleteCacheReport(cacheReportId);
    }

    @Override
    @Transactional
    public CacheReport getCacheReport(Long cacheReportId) {
        return cachereportDao.getCacheReport(cacheReportId);
    }

    @Override
    @Transactional
    public List<CacheReport> getCacheReportByCacheId(Long cacheId) {
        return cachereportDao.getCacheReportByCacheId(cacheId);
    }

    @Override
    @Transactional
    public List<String> getAllUniqueReports() {
        return cachereportDao.getAllUniqueReports();
    }

    @Override
    @Transactional
    public List<String> getUniqueReports(String directory) {
        return cachereportDao.getUniqueReports(directory);
    }

    @Override
    @Transactional
    public void deleteAllCacheReport() {
        cachereportDao.deleteAllCacheReport();
    }
}
