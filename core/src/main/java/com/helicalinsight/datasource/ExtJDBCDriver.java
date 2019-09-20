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

import java.sql.Connection;

import com.helicalinsight.efw.utility.GroovyUtils;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.managed.IJdbcDao;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

/**
 * This class is responsible for obtaining the connection depending on the different users, roles, profile attributes.
 *
 * @author Nitin U
 * @since M2
 */

public class ExtJDBCDriver extends JDBCDriver {

    private static final Logger logger = LoggerFactory.getLogger(ExtJDBCDriver.class);

    /**
     * Return the json of the result of the database query
     *
     * @param requestParameterJson The Http Request parameter
     * @param connectionDetails    The connection details from the EFWD
     * @param dataMapTagContent    The content of the data map tag from the corresponding file
     * @param properties           The singleton instanc`e of ApplicationProperties
     * @return The json of the result of the database query
     */
    public JsonObject getJSONData(JSONObject requestParameterJson, JSONObject connectionDetails,
                                  JSONObject dataMapTagContent, ApplicationProperties properties) {
        String url = connectionDetails.getString("Url");
        String user = connectionDetails.getString("User");
        String password = connectionDetails.getString("Pass");
        String driver = connectionDetails.getString("Driver");
        String queryresult = this.getQuery(dataMapTagContent, requestParameterJson);

        JSONObject dataMapContent = dataMapTagContent;
        String query;

        if (dataMapContent.getString("@type").equals("sql.groovy")) {
            query = GroovyUtils.executeGroovy(queryresult, "evalCondition", String.class);
        } else {
            query = queryresult;
        }

        JSONObject condition = GroovyUtils.executeGroovy(connectionDetails.getString("Condition"), "evalCondition", JSONObject.class);

        if (condition.containsKey("url")) {
            url = condition.getString("url");
        }
        if (condition.containsKey("user")) {
            user = condition.getString("user");
            //System.out.println(url.contains("services"));
        }
        if (condition.containsKey("password")) {
            password = condition.getString("password");
        }
        if (condition.containsKey("driver")) {
            driver = condition.getString("driver");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("URL for JDBC Driver: " + url + "User Name for JDBC Driver: " + user + "Password for JDBC Driver:" + password);
        }

        Connection connection = getConnection(url, user, password, driver);
        //All query executions should be through IJdbcDao for the purpose of query cancellation if timeout occurs
        return ApplicationContextAccessor.getBean(IJdbcDao.class).query(connection, query);
    }

}