package com.helicalinsight.datasource.managed;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


import com.helicalinsight.datasource.DataSourceUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.DatabaseConnectionFactory;
import com.helicalinsight.datasource.DriverShim;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.JdbcConnectionException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import java.sql.*;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by author on 28-Nov-14.
 *
 * @author Rajasekhar
 */
public class ConnectionProviderUtility {

    public static Connection getConnection(String jdbcUrl, String userName, String password) {
        Connection connection;
        try {
            connection = DriverManager.getConnection(jdbcUrl, userName, password);
            if (connection != null) {
                if ("com.facebook.presto.jdbc.PrestoConnection".equalsIgnoreCase(connection.getClass().getName())) {
                    DatabaseMetaData dbMetadata = connection.getMetaData();
                    ResultSet rs = dbMetadata.getCatalogs();
                    ResultSet rs2 = dbMetadata.getSchemas();
                }
            }

        } catch (SQLException ex) {
            throw new JdbcConnectionException("Could not obtain JDBC Connection.", ex);
        }
        return connection;
    }

    public static Connection getConnection(String jdbcUrl, String userName, String password, Map<String,String> extraOptions) {
        Connection connection;
        try {
        	Properties info = new Properties();

        	DataSourceUtils.loadMapToProperties(info, extraOptions);

        	if ( userName != null  ) {
        		info.put("user", userName);
        	}

        	if ( password != null ) {
        		info.put("password", password);
        	}

        	// Required for HttpDriver
        	info.put("executionType", "test");
            connection = DriverManager.getConnection(jdbcUrl, info);
        } catch (SQLException ex) {
            throw new JdbcConnectionException("Could not obtain JDBC Connection.", ex);
        }
        return connection;
    }




    public static int getDriverCount() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        return Collections.list(drivers).size();
    }

    public static boolean isDriverRegistered(String clazz) {
        if (clazz == null) {
            return false;
        }
        Enumeration<Driver> enumeration = DriverManager.getDrivers();
        while (enumeration.hasMoreElements()) {
            Driver driverAsObject = enumeration.nextElement();
            if (driverAsObject instanceof DriverShim) {
                if (clazz.equals(((DriverShim) driverAsObject).getDriverName())) {
                    return true;
                }
            } else {
                if (driverAsObject.getClass().getName().equals(clazz)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Find the class definition of the Driver from one of the plugins using a custom class loader.
     * Get the instance of the Driver and make a shim. Register with the DriverManager.
     * <p>
     * Ideally per database driver this method should be called exactly once
     *
     * @param clazz The driver to be registered
     * @throws SQLException If registration fails
     */
    public static void registerDriver(String clazz) throws SQLException {
        if (!isDriverRegistered(clazz)) {
            Object instance = FactoryMethodWrapper.getUntypedInstance(clazz);
            DriverShim driver = new DriverShim((Driver) instance);
            DriverManager.registerDriver(driver);
        }
    }

    public static boolean deRegisterDriver(String clazz) throws SQLException {
        if (clazz == null) {
            return false;
        }
        Driver driver = null;
        Enumeration<Driver> enumeration = DriverManager.getDrivers();
        while (enumeration.hasMoreElements()) {
            Driver driverAsObject = enumeration.nextElement();
            if (driverAsObject instanceof DriverShim) {
                if (clazz.equals(((DriverShim) driverAsObject).getDriverName())) {
                    driver = driverAsObject;
                }
            } else {
                if (driverAsObject.getClass().getName().equals(clazz)) {
                    driver = driverAsObject;
                }
            }
        }

        if (driver == null) {
            return false;
        } else {
            DriverManager.deregisterDriver(driver);
            return true;
        }
    }



    public static String checkIfInternalConnection(String json) {
        JsonObject formDataJson = new Gson().fromJson(json,JsonObject.class);
        String driverNameExisting = GsonUtility.optString(formDataJson, "driverClassName");
        if(driverNameExisting==null || driverNameExisting.isEmpty()){
            driverNameExisting=GsonUtility.optString(formDataJson, "driverName");
        }
        if (driverNameExisting.startsWith(com.helicalinsight.efw.utility.JsonUtils.getHiMiddleWareName())) {
            String formDataMinus = DatabaseConnectionFactory.getMinus1DataSource();
            JsonObject formDataMinusJson = new Gson().fromJson(formDataMinus,JsonObject.class);
            formDataMinusJson.addProperty("jdbcUrl", formDataMinusJson.get("url").getAsString());
            formDataMinusJson.addProperty("driverName", formDataMinusJson.get("driverClassName").getAsString());
            formDataMinusJson.addProperty("userName", formDataMinusJson.get("username").getAsString());
            json =formDataMinusJson.toString();
        }
        return json;
    }
}