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

package com.helicalinsight.datasource.csv;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.EfwdQueryProcessor;
import com.helicalinsight.datasource.IDriver;
import com.helicalinsight.datasource.JDBCDriver;
import com.helicalinsight.datasource.managed.IJdbcDao;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import net.sf.json.JSONObject;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

@SuppressWarnings("unused")
public class CsvDataSourceDriver implements IDriver {
    @Override
    public JsonObject getJSONData(JSONObject requestParameterJson, JSONObject connectionDetails,
                                  JSONObject dataMapTagContent, ApplicationProperties applicationProperties) {
        if (!connectionDetails.has("dir") || !connectionDetails.has("file")) {
            throw new EfwServiceException("One or both of the required parameters dir, file are missing.");
        }

        String dir = connectionDetails.getString("dir");
        String file = connectionDetails.getString("file");

        // Create the complete path of csv file
        String solutionDirectory = applicationProperties.getSolutionDirectory();

        String csvFilePath = solutionDirectory + File.separator + dir + File.separator + file;

        // Create table name and database name having the same name as the csv
        // file
        int periodIndex = file.lastIndexOf(".");

        String tableName;
        if (periodIndex == -1) {
            tableName = file;
        } else {
            tableName = file.substring(0, periodIndex);
        }

        String databaseName = tableName + ".db";

        String databaseFilePath = solutionDirectory + File.separator + dir + File.separator + databaseName;

        Connection jdbcConnection = getConnection(databaseFilePath);//Creates an empty database file if one doesn't
        // exist

        File fileToCheck = new File(databaseFilePath);
        if (fileToCheck.length() == 0) {
            CsvToDatabaseDumpHandler.dump(csvFilePath, tableName, jdbcConnection);
        }

        String query = getQuery(dataMapTagContent, requestParameterJson);

        IJdbcDao jdbcDao = ApplicationContextAccessor.getBean(IJdbcDao.class);
        return jdbcDao.query(jdbcConnection, query);
    }

    private Connection getConnection(String databaseFilePath) {
        Map<String, String> map = new PropertiesFileReader().read("Admin", "csvDataSource.properties");

        String csvDriverUrl = map.get("csvDriverUrl");
        String driver = map.get("Driver");
        String username = map.get("username");
        String password = map.get("password");

        return JDBCDriver.getConnection(csvDriverUrl + databaseFilePath, username, password, driver);
    }

    @Override
    public String getQuery(JSONObject dataMapTagContent, JSONObject requestParameterJson) {
        EfwdQueryProcessor queryProcessor = new EfwdQueryProcessor();
        return queryProcessor.getQuery(dataMapTagContent, requestParameterJson);
    }
}
