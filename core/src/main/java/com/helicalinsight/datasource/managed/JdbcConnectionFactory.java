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

import com.helicalinsight.datasource.DataSourceProviders;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * Created by author on 28-02-2015.
 *
 * @author Rajasekhar
 */
@Component
class JdbcConnectionFactory implements IJdbcConnectionService {

    @Autowired
    @Qualifier("dataSourceConnectionProvider")
    private IConnectionProvider dataSourceConnections;

    @Autowired
    @Qualifier("plainJdbcConnectionProvider")
    private IConnectionProvider plainJdbcConnections;

    public Connection getDatabaseConnection(String json) {
        String dataSourceProvider = JsonUtils.getKeyFromJson(json, "dataSourceProvider");

        if ((dataSourceProvider == null) || ("".equals(dataSourceProvider.trim()))) {
            throw new ConfigurationException("The connections file configuration is incorrect. " + "The json doesn't " +
                    "have the key dataSourceProvider");
        }

        Connection connection;

        if (DataSourceProviders.NONE.equalsIgnoreCase(dataSourceProvider) || DataSourceProviders.CALCITE
                .equalsIgnoreCase(dataSourceProvider)) {
            connection = plainJdbcConnections.newConnection(json);
        } else {
            connection = dataSourceConnections.newConnection(json);
        }
        return connection;
    }
}
