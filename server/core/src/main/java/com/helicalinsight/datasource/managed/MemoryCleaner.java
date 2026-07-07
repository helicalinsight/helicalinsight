package com.helicalinsight.datasource.managed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by author on 29-Dec-14.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
class MemoryCleaner {

    private static final Logger logger = LoggerFactory.getLogger(MemoryCleaner.class);

// --Commented out by Inspection START (02-09-2015 19:25):
//    public static void cleanUp() {
//        IDataSourcePool dataSourcePool = DataSourcePool.getInstance();
//        Map<HashMapKey, javax.sql.DataSource> pooledReferences = dataSourcePool
//                .getPooledReferences();
//        int size = pooledReferences.size();
//        Set<Map.Entry<HashMapKey, DataSource>> entries = pooledReferences.entrySet();
//        for (Map.Entry<HashMapKey, DataSource> entry : entries) {
//            Object dataSource = entry.getValue();
//            if (dataSource instanceof HikariDataSource) {
//                ((HikariDataSource) dataSource).close();
//            } else if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
//                ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).close();
//            }
//        }
//
//        deRegisterJdbcDrivers();
//
//        if (logger.isInfoEnabled()) {
//            logger.info(String.format("DataSources(%s), which were pooled have been shutdown " +
//                    "successfully. Jdbc drivers have been " + "de-registered to prevent memory " +
//                    "leak. Shutting down loggers.", size));
//        }
//        LogManager.shutdown();
//    }
// --Commented out by Inspection STOP (02-09-2015 19:25)

// --Commented out by Inspection START (02-09-2015 19:26):
//    private static void deRegisterJdbcDrivers() {
//        Enumeration<Driver> drivers = DriverManager.getDrivers();
//        while (drivers.hasMoreElements()) {
//            Driver driver = drivers.nextElement();
//            try {
//                DriverManager.deregisterDriver(driver);
//                logger.info(String.format("De-registering jdbc driver: %s", driver));
//            } catch (SQLException e) {
//                logger.info(String.format("Error de-registering driver %s", driver), e);
//            }
//        }
//    }
// --Commented out by Inspection STOP (02-09-2015 19:26)
}
