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

import com.helicalinsight.datasource.calcite.CalciteConnectionProvider;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by author on 13-Dec-14.
 *
 * @author Rajasekhar
 */
@Component
public class PlainJdbcConnectionProvider implements IConnectionProvider {

    private static final Logger logger = LoggerFactory.getLogger(PlainJdbcConnectionProvider.class);

    @Autowired
    private CalciteConnectionProvider calciteConnectionProvider;

    public Connection newConnection(String json) {
        long time1 = System.currentTimeMillis();
        Connection connection = getConnection(json);
        long time2 = System.currentTimeMillis();
        if (logger.isInfoEnabled()) {
            logger.info(String.format("An ad-hoc Jdbc connection is obtained in %s milli seconds." +
                            " " + "The registered drivers count is %s ", (time2 - time1),
                    ConnectionProviderUtility.getDriverCount()));
        }
        return connection;
    }

    private Connection getConnection(String json) {
        String model = JsonUtils.getKeyFromJson(json, "model");
        if (model != null) {
            return this.calciteConnectionProvider.getConnection(model);
        }

        String jdbcUrl = JsonUtils.getKeyFromJson(json, "jdbcUrl");
        String userName = JsonUtils.getKeyFromJson(json, "userName");
        String password = JsonUtils.getKeyFromJson(json, "password");

        if (jdbcUrl == null || userName == null || password == null) {
            throw new MalformedJsonException("One or more of the parameters jdbcUrl, " +
                    "userName and password are null. Can't obtain the connection to database" + ".");
        }

        String driverName = JsonUtils.extractDriverName(json);
        Connection connection;
        try {
            if (ConnectionProviderUtility.isDriverRegistered(driverName)) {
                connection = ConnectionProviderUtility.getConnection(jdbcUrl, userName, password);
            } else {
                return fallBack(driverName, jdbcUrl, userName, password);
            }
        } catch (SQLException ex) {
            throw new EfwdServiceException(ex);
        }
        return connection;
    }

    private Connection fallBack(String driverName, String jdbcUrl, String userName,
                                String password) throws SQLException {
        try {
            //Check if Driver is available in classpath
            FactoryMethodWrapper.forName(driverName);
            //By now if the class is in classpath the Driver's static initializer code is run. Driver should have
            // registered by itself with the DriverManager
        } catch (ClassNotFoundException ignored) {
            ConnectionProviderUtility.registerDriver(driverName);
        }
        return ConnectionProviderUtility.getConnection(jdbcUrl, userName, password);
    }
}