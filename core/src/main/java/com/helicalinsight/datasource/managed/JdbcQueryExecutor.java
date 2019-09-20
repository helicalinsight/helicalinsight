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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.SQLTypeMap;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by author on 14-08-2015.
 *
 * @author Rajasekhar
 */
public class JdbcQueryExecutor implements Callable<JsonObject> {

    private final Statement statement;
    private final ApplicationProperties properties;
    private final String sql;

    public JdbcQueryExecutor(Statement statement, String sql) {
        this.statement = statement;
        this.sql = sql;
        this.properties = ApplicationProperties.getInstance();
    }

    @Override
    public JsonObject call() throws Exception {
        return executeSql();
    }

    public JsonObject executeSql() throws SQLException {
        ResultSet resultSet = null;
        try {
            resultSet = this.statement.executeQuery(this.sql);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int rowCount = 0; //To count the number of rows

            JsonObject queryResult = new JsonObject();

            JsonArray metaDataArray = new JsonArray();
            int columnCount = resultSetMetaData.getColumnCount();

            //Adding metadata of the result. This is a fix for SQLite. Earlier the method was
            // called late.
            addMetadata(resultSetMetaData, metaDataArray, columnCount);

            JsonArray dataArray = new JsonArray();
            while (resultSet.next()) {
                JsonObject row = new JsonObject();
                ++rowCount;
                addARow(resultSet, resultSetMetaData, columnCount, dataArray, row);
            }
            queryResult.add("data", dataArray);

            JsonObject rowsJson = new JsonObject();
            rowsJson.addProperty("rows", rowCount);
            metaDataArray.add(rowsJson);

            queryResult.add("metadata", metaDataArray);
            return queryResult;
        } catch (SQLException ex) {
            throw new EfwdServiceException("Couldn't query the database", ex);
        } finally {
            DbUtils.closeQuietly(resultSet);
        }
    }

    private void addMetadata(ResultSetMetaData resultSetMetaData, JsonArray metaDataArray,
                             int columnCount) throws SQLException {
        Map<String, String> dataTypesMapping = new PropertiesFileReader().read("Admin", "dataTypesMapping.properties");

        JsonObject columnNameAndType = new JsonObject();

        for (int counter = 1; counter <= columnCount; counter++) {
            JsonObject object = new JsonObject();
            object.addProperty("name", resultSetMetaData.getColumnLabel(counter));

            final int columnType = resultSetMetaData.getColumnType(counter);
            final Class<?> aClass = SQLTypeMap.toClass(columnType);
            object.addProperty("type", dataTypesMapping.get(ApplicationUtilities.removeClass(aClass)));

            columnNameAndType.add(Integer.toString(counter), object);
        }
        metaDataArray.add(columnNameAndType);
    }

    private void addARow(ResultSet resultSet, ResultSetMetaData resultSetMetaData, int columnCount,
                         JsonArray dataArray, JsonObject row) throws SQLException {
        String nullValue = this.properties.getNullValue();
        for (int index = 1; index <= columnCount; index++) {
            int columnType = resultSetMetaData.getColumnType(index);
            Object object = resultSet.getObject(index);
            String columnLabel = resultSetMetaData.getColumnLabel(index);
            if ((columnType == Types.DATE) || (columnType == Types.TIMESTAMP) || (columnType == Types.TIME)) {
                if (object == null) {
                    row.addProperty(columnLabel, nullValue);
                } else {
                    row.addProperty(columnLabel, object.toString());
                }
            } else {
                if (object instanceof Number) {
                    row.addProperty(columnLabel, (Number) (object));
                } else if (object instanceof Character) {
                    row.addProperty(columnLabel, (Character) (object));
                } else if (object instanceof Boolean) {
                    //UI Needs as string
                    row.addProperty(columnLabel, "" + object);
                } else {
                    row.addProperty(columnLabel, (object == null ? nullValue : object.toString()));
                }
            }
        }
        dataArray.add(row);
    }
}