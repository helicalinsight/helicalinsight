package com.helicalinsight.cache.dao.impl;


import com.helicalinsight.cache.dao.CacheDao;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;
import com.helicalinsight.efw.utility.SplitterUtils;

import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;
import org.hibernate.type.Type;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Clob;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Query;

/**
 * @author Somen
 *         Created on 5/26/2015.
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
            session.getCurrentSession().delete(getCache(cacheId));

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
    	String queryString  = "FROM Cache where queryLookup = :queryLookup and  connectionId=:connectionId and mapId =:mapId and connectionType =:connectionType";
        String connectionFilePath = sampleCache.getConnectionFilePath();
        boolean isNotNull = connectionFilePath != null;
        if (isNotNull) {
            queryString += " and connectionFilePath =:connectionFilePath";
        }
        queryString += " order by  cacheExpiryTime desc";
        org.hibernate.query.Query query = session.getCurrentSession().createQuery(queryString,Cache.class);
        query.setParameter("queryLookup", SplitterUtils.prepareServiceId(sampleCache.getQuery()));
        query.setParameter("connectionId", sampleCache.getConnectionId());
        query.setParameter("mapId", sampleCache.getMapId());
        query.setParameter("connectionType", sampleCache.getConnectionType());
        if (isNotNull) {
            query.setParameter("connectionFilePath", connectionFilePath);
        }
        List<Cache> caches=query.list();
        return caches!=null && caches.size() > 0 ? caches.get(0) : null;
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
            Query query = (Query) session.getCurrentSession().createQuery("delete from Cache");
            query.executeUpdate();
        } catch (Exception e) {
            logger.error("Exception occurred while deleting all cache", e);
        }

    }
    
    @Override
    public List<Cache> findAllReportedCaches(String reportName){
    	List<Cache> reportedCaches=new ArrayList<>();
		try {
			org.hibernate.query.Query query = session.getCurrentSession().createQuery("FROM Cache WHERE cacheId in (SELECT cacheId FROM CacheReport WHERE reportPath=:reportName)");
			query.setParameter("reportName", reportName);
			reportedCaches=query.list();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return reportedCaches;
    }
    
    @Override
	public Cache findUniqueCacheByFilePath(String filePath) {
		String query = "FROM Cache where cacheFilePath = :cacheFilePath";
		SelectionQuery<Cache> selectionQuery = session.getCurrentSession().createSelectionQuery(query, Cache.class);
		selectionQuery.setParameter("cacheFilePath", filePath);
		return selectionQuery.uniqueResult();
	}
}
