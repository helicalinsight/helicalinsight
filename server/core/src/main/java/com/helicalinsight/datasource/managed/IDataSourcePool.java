package com.helicalinsight.datasource.managed;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Created by author on 04-Jan-15.
 *
 * @author Rajasekhar
 */
public interface IDataSourcePool {

    @NotNull
    Map<HashMapKey, String> getJsonMap();

    @NotNull
    Map<HashMapKey, javax.sql.DataSource> getPooledReferences();

    void register(HashMapKey hashMapKey, javax.sql.DataSource dataSource, String json);

    void removeEntries(String driverClass);
}
