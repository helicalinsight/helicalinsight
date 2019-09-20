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

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.managed.IJdbcDao;
import com.helicalinsight.datasource.managed.PlainJdbcConnectionProvider;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

/**
 * Queries the database and gives the response as a json data
 *
 * @author Rajasekhar
 */
public class JDBCDriver implements IDriver {

    private static final Logger logger = LoggerFactory.getLogger(JDBCDriver.class);

    public static Connection getConnection(String url, String user, String password, String driver) {
        PlainJdbcConnectionProvider plainJdbcConnectionProvider = ApplicationContextAccessor.getBean
                (PlainJdbcConnectionProvider.class);

        JSONObject json;
        json = new JSONObject();
        json.accumulate("jdbcUrl", url);
        json.accumulate("userName", user);
        json.accumulate("password", password);
        json.accumulate("driverName", driver);

        return plainJdbcConnectionProvider.newConnection(json.toString());
    }

    /**
     * Return the json of the result of the database query
     *
     * @param requestParameterJson The Http Request parameter
     * @param connectionDetails    The connection details from the EFWD
     * @param dataMapTagContent    The content of the data map tag from the corresponding file
     * @param properties           The singleton instance of ApplicationProperties
     * @return The json of the result of the database query
     */
    @Override
    public JsonObject getJSONData(JSONObject requestParameterJson, JSONObject connectionDetails,
                                  JSONObject dataMapTagContent, ApplicationProperties properties) {
        String url = connectionDetails.getString("Url");
        String user = connectionDetails.getString("User");
        String password = connectionDetails.getString("Pass");
        String driver = connectionDetails.getString("Driver");

        String query = this.getQuery(dataMapTagContent, requestParameterJson);

        if (logger.isDebugEnabled()) {
            logger.debug("EFWD query being run is " + query);
        }

        Connection connection = getConnection(url, user, password, driver);
        //All query executions should be through IJdbcDao for the purpose of query cancellation if timeout occurs
        return ApplicationContextAccessor.getBean(IJdbcDao.class).query(connection, query);
    }

    @Override
    public String getQuery(JSONObject dataMapTagContent, JSONObject requestParameterJson) {
        EfwdQueryProcessor queryProcessor = ApplicationContextAccessor.getBean(EfwdQueryProcessor.class);
        return queryProcessor.getQuery(dataMapTagContent, requestParameterJson);
    }

// --Commented out by Inspection START (12-08-2015 18:26):
//    public JSONObject getJSONData(JSONObject connectionDetails, ApplicationProperties appProp,
//                                  String query) {
//        String url = connectionDetails.getString("Url");
//        String user = connectionDetails.getString("User");
//        String password = connectionDetails.getString("Pass");
//        String driver = connectionDetails.getString("Driver");
//
//        // String query = this.getQuery(dataMapTagContent, requestParameterJson);
//
//        Connection connection = getConnection(url, user, password, driver);
//
//        Statement statement = null;
//        ResultSet resultSet = null;
//        ResultSetMetaData metaData;
//        JSONArray jsonArray;
//        JSONObject jasonObject;
//        try {
//            statement = connection.createStatement();
//            resultSet = statement.executeQuery(query);
//            metaData = resultSet.getMetaData();
//
//            jsonArray = new JSONArray();
//            jasonObject = new JSONObject();
//            for (int i = 1; i <= metaData.getColumnCount(); i++) {
//                JSONObject object = new JSONObject();
//                object.accumulate("name", metaData.getColumnLabel(i)); // md.getColumnName(i)
//                object.accumulate("type", metaData.getColumnTypeName(i));
//                jasonObject.put(i, object);
//            }
//
//            jsonArray.add(jasonObject);
//            JSONArray array = new JSONArray();
//            JSONObject object = new JSONObject();
//            while (resultSet.next()) {
//                for (int i = 1; i <= metaData.getColumnCount(); i++) {
//                    if (metaData.getColumnType(i) == Types.DATE) {
//                        object.put(metaData.getColumnName(i), resultSet.getObject(i).toString());
//                    } else if (metaData.getColumnType(i) == Types.TIMESTAMP) {
//                        object.put(metaData.getColumnName(i), resultSet.getObject(i).toString());
//                    } else if (metaData.getColumnType(i) == Types.TIME) {
//                        object.put(metaData.getColumnName(i), resultSet.getObject(i).toString());
//                    } else {
//                        object.put(metaData.getColumnLabel(i), resultSet.getObject(i) == null ?
//                                appProp.getNullValues() : resultSet.getObject(i));
//                    }
//                }
//                array.add(object);
//            }
//
//            response.accumulate("data", array);
//            response.accumulate("metadata", jsonArray);
//        } catch (SQLException e) {
//            logger.error("SQL Exception has occurred", e);
//            //handle error
//        } finally {
//            DbUtils.closeQuietly(connection, statement, resultSet);
//        }
//        return response;
//    }
// --Commented out by Inspection STOP (12-08-2015 18:26)
}
