package com.helicalinsight.admin.service.impl;

import com.helicalinsight.admin.dao.DatabaseCacheDao;
import com.helicalinsight.admin.model.ApplicationCache;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.admin.service.DatabaseCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

/**
 * @author Rajesh
 *         Created by helical019 on 2/15/2019.
 */
@Service
public class DatabaseCacheServiceImpl implements DatabaseCacheService {

    @Autowired
    protected DatabaseCacheDao databaseCacheDao;


    @Transactional
    @Override
    public String addDatabaseConnection(DataSourceMapping dataSourceMapping) {
        return databaseCacheDao.addDatabaseConnection(dataSourceMapping);
    }

    @Transactional
    @Override
    public List<String> findUnClickedTables(DataSourceMapping dataSourceMapping, List<String> types) {
        return databaseCacheDao.findUnClickedTables(dataSourceMapping, types);
    }

    @Transactional
    @Override
    public List<ApplicationCache> findApplicationCacheByDataSourceMapping(DataSourceMapping dataSourceMapping) {
        return databaseCacheDao.findApplicationCacheByDataSourceMapping(dataSourceMapping);
    }

    @Transactional
    @Override
    public List<ApplicationCache> findPartialCache(DataSourceMapping dataSourceMapping, List<String> types) {
        return databaseCacheDao.findPartialCache(dataSourceMapping, types);
    }

    @Transactional
    @Override
    public void deleteAllCacheWithId(DataSourceMapping dataSourceMapping) {
        databaseCacheDao.deleteAllCacheWithId(dataSourceMapping);
    }

    @Transactional
    @Override
    public boolean isDatabaseCachedFully(DataSourceMapping dataSourceMapping) {
        return databaseCacheDao.isDatabaseCachedFully(dataSourceMapping);
    }

    @Transactional
    @Override
    public String findKeyDataSourceMapping(DataSourceMapping dataSourceKey) {
        return databaseCacheDao.findKeyDataSourceMapping(dataSourceKey);
    }

    @Transactional
    @Override
    public Boolean findMappingKey(String key) {
        return databaseCacheDao.findMappingKey(key);
    }

    @Transactional
    @Override
    public Boolean isAConnectionCached(DataSourceMapping dataSourceMapping) {
        return databaseCacheDao.isAConnectionCached(dataSourceMapping);
    }

    @Transactional
    @Override
    public List<DataSourceMapping> getAllConnectionEntries() {
        return databaseCacheDao.getAllConnectionEntries();
    }


    @Transactional
    @Override
    public String connectionStatus(DataSourceMapping dataSourceMapping){
        return databaseCacheDao.connectionStatus(dataSourceMapping);
    }
}
