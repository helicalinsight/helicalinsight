package com.helicalinsight.admin.dao;

import com.helicalinsight.admin.model.ApplicationCache;

import java.util.List;

/**
 * @author Somen
 */
public interface ApplicationCacheDao {

    /**
     * Implementation class method add the user persistent object in database
     *
     * @param applicationCache persistent object
     */

    public String addApplicationCache(ApplicationCache applicationCache);

    /**
     * Implementation class method update the user persistent object
     *
     * @param applicationCache persistent object
     */

    public void editApplicationCache(ApplicationCache applicationCache);

    /**
     * Implementation class method delete persistent object user by user id from
     * database
     *
     * @param applicationCacheId connectionCatalogId
     */

    public void deleteApplicationCache(String applicationCacheId);

    /**
     * Implementation class method return the user object by user id from
     * database
     *
     * @param applicationCacheId catalogId
     * @return ConnectionCatalog object
     */

    public List<ApplicationCache> findApplicationCache(String applicationCacheId);

    /**
     * Implementation class method return list of all user object from database
     *
     * @return list of user objects
     */

    public List<ApplicationCache> readApplicationCache();

    public Boolean findByKeyAndPage(String key, Integer position);

    boolean deleteAllCache();

    List<ApplicationCache> findCacheByKey(String key);

    public boolean deleteByKey(String key);
}
