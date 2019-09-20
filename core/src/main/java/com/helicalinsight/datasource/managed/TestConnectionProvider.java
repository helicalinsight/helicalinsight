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
import net.sf.json.JSONObject;
import org.apache.commons.dbutils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * Created by author on 09-02-2015.
 *
 * @author Rajasekhar
 */
@Component
public class TestConnectionProvider {

    @Autowired
    @Qualifier("plainJdbcConnectionProvider")
    private IConnectionProvider plainJdbcConnections;

    @Autowired
    @Qualifier("dataSourceConnectionProvider")
    private IConnectionProvider dataSourceConnections;

    public boolean testConnection(String formData) {
        String dataSourceProvider;
        try {
            dataSourceProvider = JSONObject.fromObject(formData).getString("dataSourceProvider");
        } catch (Exception e) {
            throw new IllegalArgumentException("The required parameter dataSourceProvider is " + "absent in the " +
                    "request.", e);
        }

        Connection connection = getConnection(formData, dataSourceProvider);

        if (connection == null) {
            return false;
        } else {
            DbUtils.closeQuietly(connection);
            return true;
        }
    }

    private Connection getConnection(String formData, String dataSourceProvider) {
        Connection connection;

        if (DataSourceProviders.JNDI.equals(dataSourceProvider)) {
            JSONObject formDataJson = JSONObject.fromObject(formData);
            String lookUpName = formDataJson.getString("lookUpName");
            if (!lookUpName.startsWith("java:comp/env/")) {
                formDataJson.discard("lookUpName");
                formDataJson.accumulate("lookUpName", "java:comp/env/" + lookUpName);
            }
            connection = dataSourceConnections.newConnection(formDataJson.toString());
        } else {
            connection = plainJdbcConnections.newConnection(formData);
        }
        return connection;
    }
}
