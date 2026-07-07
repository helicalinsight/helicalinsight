package com.helicalinsight.admin.service.impl;

import com.helicalinsight.admin.dao.ApplicationCacheDao;
import com.helicalinsight.admin.model.ApplicationCache;
import com.helicalinsight.admin.service.ApplicationCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

/**
 * @author Rajesh
 *         Created by helical019 on 2/15/2019.
 */
@Service
public class ApplicationCacheServiceImpl implements ApplicationCacheService {

    @Autowired
    protected ApplicationCacheDao applicationCacheDao;

    @Transactional
    @Override
    public String addApplicationCache(ApplicationCache applicationCache) {

        return applicationCacheDao.addApplicationCache(applicationCache);
    }


    @Transactional
    @Override
    public void editApplicationCache(ApplicationCache applicationCache) {
        applicationCacheDao.editApplicationCache(applicationCache);
    }

    @Transactional
    @Override
    public void deleteApplicationCache(String applicationCacheId) {
        applicationCacheDao.deleteApplicationCache(applicationCacheId);
    }

    @Transactional
    @Override
    public List<ApplicationCache> findApplicationCache(String applicationCacheId) {
        return applicationCacheDao.findApplicationCache(applicationCacheId);
    }

    @Transactional
    @Override
    public List<ApplicationCache> readApplicationCache() {
        return applicationCacheDao.readApplicationCache();
    }


    @Transactional
    @Override
    public Boolean findByKeyAndPage(String key, Integer position) {
        return applicationCacheDao.findByKeyAndPage(key, position);
    }


    @Transactional
    @Override
    public boolean deleteAllCache() {
        return applicationCacheDao.deleteAllCache();
    }


    @Transactional
    @Override
    public List<ApplicationCache> findCacheByKey(String key) {
        return applicationCacheDao.findCacheByKey(key);
    }

    @Transactional
    @Override
    public boolean deleteByKey(String key){
        return  applicationCacheDao.deleteByKey(key);
    }
}
