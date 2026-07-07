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