package com.helicalinsight.datasource;

import java.sql.Connection;

/**
 * The DriverConnection model class allows for setting and retrieving the JDBC driver class and the database connection .
 * Created by Rajasekhar on 10-05-2015.
 * @author Rajasekhar
 */
public class DriverConnection {

    private String driverClass;

    private Connection connection;

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
