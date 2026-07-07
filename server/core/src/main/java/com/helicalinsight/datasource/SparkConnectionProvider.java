package com.helicalinsight.datasource;

import com.helicalinsight.datasource.managed.ConnectionProviderUtility;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.utility.HISparkContext;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * SparkConnectionProvider is responsible for creating and providing Spark JDBC connections. 
 * Created by user on 11/24/2015.
 * @author Rajasekhar
 */
@Component
public class SparkConnectionProvider {

    //TODO bind the url with spark
    //TODO bind the username and password
    private static final String URL = "jdbc:hive2://localhost:10001";

    /**
     * sparkConnection()
     * Establish a Spark JDBC connection.
     *
     * @return A JDBC connection to the Spark cluster.
     * @throws EfwdServiceException If an error occurs while obtaining the connection.
     */
    public Connection sparkConnection() {
        try {
            String url =HISparkContext.getDatasourceUrl();
            ConnectionProviderUtility.registerDriver(HISparkContext.getDriverClass());
            Connection connection;
            if (!url.isEmpty()) {
                connection = DriverManager.getConnection(url);
            } else {
                connection = DriverManager.getConnection(URL);
            }
            return connection;
        } catch (SQLException ex) {
            throw new EfwdServiceException("Couldn't obtain connection.", ex);
        }
    }

}