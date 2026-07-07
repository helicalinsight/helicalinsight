package com.helicalinsight.datasource.csv;

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.Statement;

public class CreateDatabaseToDumpCSV {

    public static void createTable(String fields, String tableName, Connection connection) {
        Statement statement = null;
        try {
            statement = connection.createStatement();

            statement.execute("CREATE TABLE " + tableName + "(" + fields + ")");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(statement);
        }
    }
}
