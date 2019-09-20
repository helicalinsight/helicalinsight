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

import au.com.bytecode.opencsv.CSVReader;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CSVLoader {

    private static final String SQL_INSERT = "INSERT INTO ${table}(${keys}) VALUES(${values})";

    private static final String TABLE_REGEX = "\\$\\{table\\}";

    private static final String KEYS_REGEX = "\\$\\{keys\\}";

    private static final String VALUES_REGEX = "\\$\\{values\\}";
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CSVLoader.class);
    private Connection connection;
    private char separator;

    /**
     * Public constructor to build CSVLoader object with
     * Connection details. The connection is closed on success
     * or failure.
     *
     * @param connection Jdbc Connection
     */
    public CSVLoader(Connection connection) {
        this.connection = connection;
        //Set default separator
        this.separator = ',';
    }

    /**
     * Parse CSV file using OpenCSV library and load in
     * given database table.
     *
     * @param csvFile            Input CSV file
     * @param tableName          Database table name to import data
     * @param truncateBeforeLoad Truncate the table before inserting
     *                           new records.
     * @throws Exception
     */
    public void loadCSV(String csvFile, String tableName, boolean truncateBeforeLoad) throws Exception {
        CSVReader csvReader;
        if (null == this.connection) {
            throw new RuntimeException("Not a valid connection.");
        }

        try {
            csvReader = new CSVReader(new FileReader(csvFile), this.separator);
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while executing file. " + ex.getMessage());
        }

        String[] headerRow = csvReader.readNext();

        if (null == headerRow) {
            throw new EfwServiceException("No columns defined in given CSV file." + "Please " +
                    "check the CSV file format.");
        }

        int length = headerRow.length;

        String[] columns = new String[length];

        int index = 0;
        for (String column : headerRow) {
            columns[index++] = column.replaceAll("[\\s\\.]+", "_");
        }

        String fields = StringUtils.join(columns, ",");

        String questionMarks = StringUtils.repeat("?,", length);
        questionMarks = (String) questionMarks.subSequence(0, questionMarks.length() - 1);

        String query = SQL_INSERT.replaceFirst(TABLE_REGEX, tableName);
        query = query.replaceFirst(KEYS_REGEX, fields);
        query = query.replaceFirst(VALUES_REGEX, questionMarks);

        CreateDatabaseToDumpCSV.createTable(fields, tableName, this.connection);

        String[] nextLine;
        PreparedStatement preparedStatement = null;
        try {
            this.connection.setAutoCommit(false);
            preparedStatement = this.connection.prepareStatement(query);

            if (truncateBeforeLoad) {
                //delete data from table before loading csv
                this.connection.createStatement().execute("DELETE FROM " + tableName);
            }

            final int batchSize = 1000;
            int count = 0;
            while ((nextLine = csvReader.readNext()) != null) {
                index = 1;
                for (String string : nextLine) {
                    preparedStatement.setString(index++, string);
                }
                preparedStatement.addBatch();
                if (++count % batchSize == 0) {
                    preparedStatement.executeBatch();
                }
            }
            preparedStatement.executeBatch(); // Insert remaining records
            this.connection.commit();
        } catch (Exception ex) {
            this.connection.rollback();
            logger.error("Error: ", ex);
            throw new EfwServiceException("Error occurred while loading data from file to " +
                    "database." + ex.getMessage());
        } finally {
            DbUtils.closeQuietly(preparedStatement);
            csvReader.close();
        }
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }
}