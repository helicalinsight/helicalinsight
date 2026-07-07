package com.helicalinsight.datasource;

import com.helicalinsight.efw.components.EfwdReader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.calcite.CalciteConnectionProvider;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;


import java.sql.Connection;

/**
 * CalciteConnectionFactory
 * This class implements the {@link IConnectionFactory} interface and provides a connection details for Apache Calcite.
 *
 * Created by user on 11/26/2015.
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class CalciteConnectionFactory implements IConnectionFactory {

    private static final String CALCITE_DRIVER = "org.apache.calcite.jdbc.Driver";
    /**
     * getConnection(String type, String jsonInfo)
     * This method provides connection details for the Apache Calcite driver based on the provided connection type
     * and JSON information.
     * @param type     		The connection type.
     * @param jsonInfo 		formData in string format
     * @return A DriverConnection object with  {@code java.sql.connection} details and calcite driver.
     */
    @Override
    public DriverConnection getConnection(String type, String jsonInfo) {
        EfwdReader efwdReader = new EfwdReader();
        String result = efwdReader.executeComponent(jsonInfo);
        String model = new Gson().fromJson(result,JsonObject.class).get("model").getAsString();
        CalciteConnectionProvider provider = ApplicationContextAccessor.getBean(CalciteConnectionProvider.class);
        Connection connection = provider.getConnection(model);
        DriverConnection driverConnection = new DriverConnection();
        driverConnection.setConnection(connection);
        driverConnection.setDriverClass(CALCITE_DRIVER);
        return driverConnection;
    }
    
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

