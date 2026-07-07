package com.helicalinsight.datasource;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.HISparkContext;

import java.sql.Connection;

/**
 * SparkConnectionFactory implements {@link IConnectionFactory} interface
 * This class is responsible for providing connections for Spark.
 * Created by user on 11/16/2017
 * @author Somen
 */
@SuppressWarnings("unused")
public class SparkConnectionFactory implements IConnectionFactory {

	/**
	 * getConnection(String type, String jsonInfo)
	 * Obtains a connection of the specified type based on JSON configuration.
     * 
     * @param type     		type of dataSource.
     * @param jsonInfo      JSON information for establishing the connection.
     * @return A DriverConnection object containing the connection and driver class information.
     */
    @Override
    public DriverConnection getConnection(String type, String jsonInfo) {
        SparkConnectionProvider provider = ApplicationContextAccessor.getBean(SparkConnectionProvider.class);
        Connection connection = provider.sparkConnection();
        DriverConnection driverConnection = new DriverConnection();
        driverConnection.setConnection(connection);
        driverConnection.setDriverClass(HISparkContext.getDriverClass());
        return driverConnection;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

