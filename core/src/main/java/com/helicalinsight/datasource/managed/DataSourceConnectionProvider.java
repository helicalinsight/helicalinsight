/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.datasource.managed;

import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.exceptions.JdbcConnectionException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
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

    public Connection newConnection(String json) {
        return getConnection(json);
    }

    private Connection getConnection(String json) {
        long now = System.currentTimeMillis();
        Connection connection;
        javax.sql.DataSource dataSource;

        dataSource = this.dataSourceFactory.sqlDataSource(json);

        if (dataSource == null) {
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

    private Connection getConnection(javax.sql.DataSource dataSource) {
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