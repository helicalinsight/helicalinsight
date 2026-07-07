package com.helicalinsight.cache.service.impl;


import com.helicalinsight.cache.dao.CacheDao;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.model.CacheReport;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.efw.utility.SplitterUtils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private CacheDao cacheDao;


    @Transactional
    public Long addCache(Cache cache) {
    	String lookup = SplitterUtils.prepareServiceId(cache.getQuery());
    	cache.setQueryLookup(lookup);
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


	@Override
	@Transactional
	public List<Cache> findAllReportedCaches(String reportName) {
		return cacheDao.findAllReportedCaches(reportName);
	}
	
	@Transactional
	@Override
	public Cache findUniqueCacheByFilePath(String filePath) {
		return cacheDao.findUniqueCacheByFilePath(filePath);
	}

}
