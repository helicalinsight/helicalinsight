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

package com.helicalinsight.datasource;

import com.helicalinsight.datasource.managed.IJdbcConnectionService;
import com.helicalinsight.efw.components.EfwdReader;
import com.helicalinsight.efw.components.SqlJdbcHandler;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * Created by author on 27-02-2015.
 *
 * @author Rajasekhar
 */
@Component
public class DatabaseConnectionFactory implements IConnectionFactory {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionFactory.class);

    @Override
    public DriverConnection getConnection(String type, String jsonInfo) {
        Connection connection = null;

        JSONObject formData = JSONObject.fromObject(jsonInfo);

        DriverConnection driverConnection = new DriverConnection();

        if (!GlobalJdbcTypeUtils.isTypeGlobal(type)) {
            String efwdConnectionJson = efwdJson(formData);

            JSONObject efwdJson = JSONObject.fromObject(efwdConnectionJson);

            String jdbcUrl = efwdJson.getString("jdbcUrl");
            String userName = efwdJson.getString("userName");
            String password = efwdJson.getString("password");
            String driverName = efwdJson.getString("driverName");

            driverConnection.setDriverClass(driverName);

            SqlJdbcHandler sqlJdbcHandler = new SqlJdbcHandler();

            EfwdConnection efwdConnection = sqlJdbcHandler.getEfwdConnection(jdbcUrl, userName, password, driverName);

            if (logger.isDebugEnabled()) {
                logger.debug("EFWD details are" + efwdConnection);
            }

            connection = efwdConnection.getConnection();
            driverConnection.setConnection(connection);
        } else {
            boolean nonPooled = GlobalJdbcType.NON_POOLED.equalsIgnoreCase(type);
            boolean staticDataSource = GlobalJdbcType.STATIC_DATASOURCE.equalsIgnoreCase(type);
            boolean dynamicDataSource = GlobalJdbcType.DYNAMIC_DATASOURCE.equalsIgnoreCase(type);

            if (nonPooled || staticDataSource || dynamicDataSource) {
                int id;
                try {
                    id = Integer.parseInt(formData.getString("id"));
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("The parameter id should be an integer.");
                }
                String json = DataSourceUtils.globalIdJson(id);

                driverConnection.setDriverClass(com.helicalinsight.datasource.managed.JsonUtils.extractDriverName
                        (json));

                IJdbcConnectionService connectionService = ApplicationContextAccessor.getBean(IJdbcConnectionService
                        .class);

                connection = connectionService.getDatabaseConnection(json);
                driverConnection.setConnection(connection);
            }
        }

        if (connection == null) {
            throw new ConnectionException("Could not get database connection");
        }

        return driverConnection;
    }

    private String efwdJson(JSONObject formData) {
        EfwdReader efwdReader = new EfwdReader();
        return efwdReader.executeComponent(formData.toString());
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}