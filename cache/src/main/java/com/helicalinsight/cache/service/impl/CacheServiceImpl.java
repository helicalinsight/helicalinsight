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


import com.helicalinsight.cache.dao.CacheDao;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;
import com.helicalinsight.cache.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private CacheDao cacheDao;


    @Transactional
    public Long addCache(Cache cache) {
        return cacheDao.addCache(cache);
    }


    @Transactional
    public void editCache(Cache cache) {
        cacheDao.editCache(cache);
    }


    @Transactional
    public void deleteCache(Long cacheId) {
        cacheDao.deleteCache(cacheId);
    }


    @Transactional
    public Cache findCache(Long cacheId) {
        return cacheDao.getCache(cacheId);
    }

    @Transactional
    public Cache findUniqueCache(Cache sampleCache) {
        return cacheDao.findUniqueCache(sampleCache);
    }


    @Transactional
    public void addReport(CacheReport report) {
        cacheDao.addReport(report);
    }

    @Transactional
    public void deleteAllCache() {
        cacheDao.deleteAllCache();
    }

}
