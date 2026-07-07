package com.helicalinsight.datasource.managed;

import com.helicalinsight.datasource.CustomWatcherUtils;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.exceptions.JdbcConnectionException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by author on 28-Nov-14.
 *
 * @author Rajasekhar
 */
@Component
class DataSourceConnectionProvider implements IConnectionProvider {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConnectionProvider.class);

    @Autowired
    private IDataSourceFactory dataSourceFactory;

    public synchronized Connection newConnection(String json) {
        return getConnection(json);
    }

    private synchronized Connection getConnection(String json) {

        json = ConnectionProviderUtility.checkIfInternalConnection(json);

        long now = System.currentTimeMillis();
        Connection connection;
        javax.sql.DataSource dataSource = null;
        String driverName = JsonUtils.extractDriverName(json);
        try {
            dataSource = this.dataSourceFactory.sqlDataSource(json);
        } catch (LinkageError err) {
            CustomWatcherUtils.closeClassLoader(driverName);
        }
        if (dataSource == null) {
            CustomWatcherUtils.closeClassLoader(driverName);
            throw new MalformedJsonException("Could not complete request as the json is malformed" +
                    ". " + "Failed to get JDBC connection.");
        }

        try {
            connection = getConnection(dataSource);
        } catch (Exception ex) {
            throw new EfwdServiceException("Could not obtain a JDBC connection " + "even " +
                    "after dynamic loading of driver jar", ex);
        }

        long after = System.currentTimeMillis();
        if (logger.isInfoEnabled()) {
            logger.info("A Connection is obtained in " + (after - now) + " milli seconds and " +
                    "the registered drivers count is " + ConnectionProviderUtility.getDriverCount());
        }
        return connection;
    }

    private Connection getConnection(@Nullable javax.sql.DataSource dataSource) {
        Connection connection;
        try {
            if (dataSource != null) {
                connection = dataSource.getConnection();
            } else {
                throw new ConfigurationException("Could not obtain DataSource. DataSource is null");
            }
        } catch (SQLException ex) {
            throw new JdbcConnectionException("Could not obtain a JDBC connection", ex);
        }
        return connection;
    }
}