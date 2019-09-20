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

package com.helicalinsight.datasource.managed;

import com.helicalinsight.datasource.DriverShim;
import com.helicalinsight.efw.exceptions.JdbcConnectionException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
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
     * <p/>
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
}