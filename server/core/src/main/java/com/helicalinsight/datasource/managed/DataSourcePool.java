 package com.helicalinsight.datasource.managed;

import com.helicalinsight.admin.management.ManagedDataSourceShutdownHandler;
import com.helicalinsight.efw.exceptions.JdbcConnectionException;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by author on 29-Nov-14.
 *
 * @author Rajasekhar
 */
public enum DataSourcePool implements IDataSourcePool {
    DATA_SOURCE_POOL;

    //These two maps can be clubbed into one Pojo with composition
    private static final ConcurrentHashMap<HashMapKey, String> jsonMap = new ConcurrentHashMap<>(16, 0.9f, 1);

    private static final ConcurrentHashMap<HashMapKey, DataSource> pooledReferences = new ConcurrentHashMap<>(16,
            0.9f, 1);

    DataSourcePool() {
    }

    @NotNull
    public static IDataSourcePool getInstance() {
        return DATA_SOURCE_POOL;
    }

    @Override
    public final void register(@Nullable HashMapKey hashMapKey, @Nullable DataSource dataSource,
                               @Nullable String json) {
        if ((hashMapKey == null) || (dataSource == null) || (json == null)) {
            throw new JdbcConnectionException("Could not register the data source. Malformed json might be the issue.");
        }

        validatePoolEntry(dataSource);

        store(hashMapKey, dataSource, json);
    }

    public final void removeEntries(String driverClass) {
        List<HashMapKey> toBeRemoved = new ArrayList<>();
        for (Map.Entry<HashMapKey, String> entry : jsonMap.entrySet()) {
            HashMapKey key = entry.getKey();
            if (key.getDriverName().equals(driverClass)) {
                toBeRemoved.add(key);
            }
        }

        for (Map.Entry<HashMapKey, DataSource> entry : pooledReferences.entrySet()) {
            HashMapKey key = entry.getKey();
            if (key.getDriverName().equals(driverClass)) {
                if (!toBeRemoved.contains(key)) {
                    toBeRemoved.add(key);
                }
            }
        }

        for (HashMapKey hashMapKey : toBeRemoved) {
            jsonMap.remove(hashMapKey);

            DataSource dataSource = pooledReferences.remove(hashMapKey);
            ManagedDataSourceShutdownHandler.close(dataSource);
        }
    }

    private synchronized void store(HashMapKey hashMapKey, DataSource dataSource, String json) {
        jsonMap.put(hashMapKey, json);//Simply override. Because the DS ID is same and from same file.
        DataSource previousDS = pooledReferences.putIfAbsent(hashMapKey, dataSource);
        if (previousDS != null) {
            shutdown(previousDS);
        }
    }

    private void shutdown(DataSource dataSource) {
        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).close();
        } else if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }

    private void validatePoolEntry(DataSource dataSource) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            if (connection == null) {
                throw new JdbcConnectionException("Couldn't obtain database connection");
            }
        } catch (SQLException ex) {
            throw new JdbcConnectionException(ex);
        } finally {
            DbUtils.closeQuietly(connection);
        }
    }

    @NotNull
    @Override
    public Map<HashMapKey, String> getJsonMap() {
        return jsonMap;
    }

    @NotNull
    @Override
    public Map<HashMapKey, DataSource> getPooledReferences() {
        return pooledReferences;
    }
}