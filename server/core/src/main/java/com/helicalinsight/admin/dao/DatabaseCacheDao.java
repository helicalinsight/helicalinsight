package com.helicalinsight.admin.dao;

import com.helicalinsight.admin.model.ApplicationCache;
import com.helicalinsight.admin.model.DataSourceMapping;

import java.util.List;

/**
 * @author Somen
 */
public interface DatabaseCacheDao {

    public List<String> findUnClickedTables(DataSourceMapping dataSourceMapping, List<String> types);
    public String addDatabaseConnection(DataSourceMapping dataSourceMapping);

    public List<ApplicationCache> findApplicationCacheByDataSourceMapping(DataSourceMapping dataSourceMapping);

    public List<ApplicationCache> findPartialCache(DataSourceMapping dataSourceMapping, List<String> types);

    public void deleteAllCacheWithId(DataSourceMapping dataSourceMapping);

    public boolean isDatabaseCachedFully(DataSourceMapping dataSourceMapping);

    public String findKeyDataSourceMapping(DataSourceMapping dataSourceKey);

    public Boolean findMappingKey(String key);
    public Boolean isAConnectionCached(DataSourceMapping dataSourceMapping);
    public List<DataSourceMapping> getAllConnectionEntries();
    public String connectionStatus(DataSourceMapping dataSourceMapping);
}
