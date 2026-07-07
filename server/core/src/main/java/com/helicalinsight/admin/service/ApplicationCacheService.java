package com.helicalinsight.admin.service;

import com.helicalinsight.admin.model.ApplicationCache;

import java.util.List;

/**
 * Created by helical019 on 2/15/2019.
 */
public interface ApplicationCacheService {

    public String addApplicationCache(ApplicationCache applicationCache);

    public void editApplicationCache(ApplicationCache applicationCache);

    public void deleteApplicationCache(String applicationCacheId);

    public List<ApplicationCache> findApplicationCache(String applicationCacheId);

    public List<ApplicationCache> readApplicationCache();

    public Boolean findByKeyAndPage(String key, Integer position);

    public boolean deleteAllCache();

    public List<ApplicationCache> findCacheByKey(String key);

    public boolean deleteByKey(String key);
}
