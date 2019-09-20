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

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;

public class CsvToDatabaseDumpHandler {

    public static void dump(String csvFilePath, String tableName, Connection connection) {
        try {
            if (connection == null) {
                throw new RuntimeException("Could not obtain connection.");
            }
            CSVLoader loader = new CSVLoader(connection);
            loader.loadCSV(csvFilePath, tableName, true);
        } catch (Exception ex) {
            DbUtils.closeQuietly(connection);
            throw new RuntimeException(ex);
        }
    }
}