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

package com.helicalinsight.cache.dao.impl;


import com.helicalinsight.cache.dao.CacheDao;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Somen

 */
@Repository
public class CacheDaoImpl implements CacheDao {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CacheDaoImpl.class);
    @Autowired
    private SessionFactory session;

    @Override
    public Long addCache(Cache cache) {
        try {
            session.getCurrentSession().save(cache);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return cache.getCacheId();
    }

    @Override
    public void editCache(Cache cache) {
        try {
            session.getCurrentSession().update(cache);

        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void deleteCache(Long cacheId) {
        try {
            Cache cache = getCache(cacheId);
            if (cache != null) {
                session.getCurrentSession().delete(cache);
            }

        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public Cache getCache(Long cacheId) {
        Cache cache = null;
        try {
            cache = (Cache) session.getCurrentSession().get(Cache.class, cacheId);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return cache;
    }

    @Override
    public Cache findUniqueCache(Cache sampleCache) {
        Cache cache = null;
        Query query = session.getCurrentSession().createQuery("from Cache  where query=:query " +
                "and" +
                "  connectionId=:connectionId and mapId=:mapId and connectionType=:connectionType" +
                " order by " +
                "cacheExpiryTime desc");

        query.setParameter("query", sampleCache.getQuery());
        query.setLong("connectionId", sampleCache.getConnectionId());
        query.setInteger("mapId", sampleCache.getMapId());
        query.setParameter("connectionType", sampleCache.getConnectionType());
        @SuppressWarnings("rawtypes") List results = query.list();
        try {
            if (results != null && results.size() > 0) {
                cache = (Cache) results.get(0);
            }
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return cache;
    }

    @Override
    public void addReport(CacheReport report) {
        try {
            session.getCurrentSession().save(report);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void deleteAllCache() {

        try {
            Query query = session.getCurrentSession().createQuery("delete from Cache");
            query.executeUpdate();
        } catch (Exception e) {
            logger.error("Exception occurred while deleting all cache", e);
        }

    }
}
