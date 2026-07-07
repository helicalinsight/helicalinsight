package com.helicalinsight.efw.components;

import com.helicalinsight.admin.dto.DSTypeHikariDTO;
import com.helicalinsight.admin.dto.DSTypeJndiDTO;
import com.helicalinsight.admin.dto.DSTypeNoSQLDTO;
import com.helicalinsight.admin.dto.DSTypePlainJDBCDTO;
import com.helicalinsight.admin.dto.DSTypeTomcatDTO;
import com.helicalinsight.admin.dto.GlobalConnectionDTO;
import com.helicalinsight.datasource.model.*;

/**
 * Created by Helical on 5/31/2021.
 */
public class GlobalConnectionDTOHelper {

    public static GlobalConnectionDTO setGlobalConnectionDTO(GlobalConnections globalConnections) {
        GlobalConnectionDTO globalConnectionDTO = new GlobalConnectionDTO();
        globalConnectionDTO.setId(globalConnections.getGlobalId());
        globalConnectionDTO.setName(globalConnections.getName());
        globalConnectionDTO.setType(globalConnections.getType());
        globalConnectionDTO.setBaseType(globalConnections.getBaseType());
        return globalConnectionDTO;
    }

    public static DSTypePlainJDBCDTO setPlainJDBCDTO(DSTypePlainJDBC dsTypePlainJDBC) {
        DSTypePlainJDBCDTO dsTypePlainJDBCDTO = new DSTypePlainJDBCDTO();
        dsTypePlainJDBCDTO.setId(dsTypePlainJDBC.getId());
        dsTypePlainJDBCDTO.setVisible(dsTypePlainJDBC.getVisible());
        dsTypePlainJDBCDTO.setUserName(dsTypePlainJDBC.getUserName());
        dsTypePlainJDBCDTO.setPassword(dsTypePlainJDBC.getPassword());
        dsTypePlainJDBCDTO.setJdbcUrl(dsTypePlainJDBC.getJdbcUrl());
        dsTypePlainJDBCDTO.setDataSourceProvider(dsTypePlainJDBC.getDataSourceProvider());
        dsTypePlainJDBCDTO.setDatabaseDialect(dsTypePlainJDBC.getDatabaseDialect());
        dsTypePlainJDBCDTO.setDatabaseName(dsTypePlainJDBC.getDatabaseName());
        dsTypePlainJDBCDTO.setLastUpdatedTime(dsTypePlainJDBC.getLastUpdatedTime());
        dsTypePlainJDBCDTO.setDriverName(dsTypePlainJDBC.getDriverName());
        dsTypePlainJDBCDTO.setGlobalId(dsTypePlainJDBC.getGlobalConnections().getGlobalId());

        return dsTypePlainJDBCDTO;
    }

    public static DSTypeTomcatDTO setTomcatDTO(DSTypeTomcat dsTypeTomcat) {
        DSTypeTomcatDTO dsTypeTomcatDTO = new DSTypeTomcatDTO();
        dsTypeTomcatDTO.setGlobalId(dsTypeTomcat.getGlobalConnections().getGlobalId());
        dsTypeTomcatDTO.setId(dsTypeTomcat.getId());
        dsTypeTomcatDTO.setVisible(dsTypeTomcat.getVisible());
        dsTypeTomcatDTO.setDataSourcePoolId(dsTypeTomcat.getDataSourcePoolId());
        dsTypeTomcatDTO.setDataSourceProvider(dsTypeTomcat.getDataSourceProvider());
        dsTypeTomcatDTO.setForceAlternateUsername(dsTypeTomcat.isForceAlternateUsername());
        dsTypeTomcatDTO.setUsername(dsTypeTomcat.getUsername());
        dsTypeTomcatDTO.setPassword(dsTypeTomcat.getPassword());
        dsTypeTomcatDTO.setUrl(dsTypeTomcat.getUrl());
        dsTypeTomcatDTO.setTestWhileIdle(dsTypeTomcat.isTestWhileIdle());
        dsTypeTomcatDTO.setTestOnBorrow(dsTypeTomcat.isTestOnBorrow());
        dsTypeTomcatDTO.setTestOnReturn(dsTypeTomcat.isTestOnReturn());
        dsTypeTomcatDTO.setValidationInterval(dsTypeTomcat.getValidationInterval());
        dsTypeTomcatDTO.setTimeBetweenEvictionRunsMillis(dsTypeTomcat.getTimeBetweenEvictionRunsMillis());
        dsTypeTomcatDTO.setMaxActive(dsTypeTomcat.getMaxActive());
        dsTypeTomcatDTO.setMinIdle(dsTypeTomcat.getMinIdle());
        dsTypeTomcatDTO.setMaxWait(dsTypeTomcat.getMaxWait());
        dsTypeTomcatDTO.setInitialSize(dsTypeTomcat.getInitialSize());
        dsTypeTomcatDTO.setRemoveAbandonedTimeout(dsTypeTomcat.getRemoveAbandonedTimeout());
        dsTypeTomcatDTO.setRemoveAbandoned(dsTypeTomcat.isRemoveAbandoned());
        dsTypeTomcatDTO.setLogAbandoned(dsTypeTomcat.isLogAbandoned());
        dsTypeTomcatDTO.setMinEvictableIdleTimeMillis(dsTypeTomcat.getMinEvictableIdleTimeMillis());
        dsTypeTomcatDTO.setJmxEnabled(dsTypeTomcat.isJmxEnabled());
        dsTypeTomcatDTO.setJdbcInterceptors(dsTypeTomcat.getJdbcInterceptors());
        dsTypeTomcatDTO.setDatabaseDialect(dsTypeTomcat.getDatabaseDialect());
        dsTypeTomcatDTO.setDatabaseName(dsTypeTomcat.getDatabaseName());
        dsTypeTomcatDTO.setLastUpdatedTime(dsTypeTomcat.getLastUpdatedTime());
        dsTypeTomcatDTO.setDriverClassName(dsTypeTomcat.getDriverClassName());
        dsTypeTomcatDTO.setValidationQuery(dsTypeTomcat.getValidationQuery());

        return dsTypeTomcatDTO;

    }

    public static DSTypeHikariDTO setHikariDTO(DSTypeHikari dsTypeHikari) {
        DSTypeHikariDTO dsTypeHikariDTO = new DSTypeHikariDTO();
        dsTypeHikariDTO.setId(dsTypeHikari.getId());
        dsTypeHikariDTO.setGlobalConnections(dsTypeHikari.getGlobalConnections().getGlobalId());
        dsTypeHikariDTO.setVisible(dsTypeHikari.getVisible());
        dsTypeHikariDTO.setDataSourcePoolId(dsTypeHikari.getDataSourcePoolId());
        dsTypeHikariDTO.setDataSourceProvider(dsTypeHikari.getDataSourceProvider());
        dsTypeHikariDTO.setMaximumPoolSize(dsTypeHikari.getMaximumPoolSize());
        dsTypeHikariDTO.setMinimumIdle(dsTypeHikari.getMinimumIdle());
        dsTypeHikariDTO.setMaxLifetime(dsTypeHikari.getMaxLifetime());
        dsTypeHikariDTO.setUserName(dsTypeHikari.getUserName());
        dsTypeHikariDTO.setPassword(dsTypeHikari.getPassword());
        dsTypeHikariDTO.setPoolName(dsTypeHikari.getPoolName());
        dsTypeHikariDTO.setJdbcUrl(dsTypeHikari.getJdbcUrl());
        dsTypeHikariDTO.setConnectionTimeout(dsTypeHikari.getConnectionTimeout());
        dsTypeHikariDTO.setLeakDetectionThreshold(dsTypeHikari.getLeakDetectionThreshold());
        dsTypeHikariDTO.setIdleTimeout(dsTypeHikari.getIdleTimeout());
        dsTypeHikariDTO.setDatabaseDialect(dsTypeHikari.getDatabaseDialect());
        dsTypeHikariDTO.setDatabaseName(dsTypeHikari.getDatabaseName());
        dsTypeHikariDTO.setLastUpdatedTime(dsTypeHikari.getLastUpdatedTime());
        dsTypeHikariDTO.setDriverClassName(dsTypeHikari.getDriverName());
        dsTypeHikariDTO.setValidationQuery(dsTypeHikari.getValidationQuery());
        return dsTypeHikariDTO;


    }

    public static DSTypeJndiDTO setJndiDTO(DSTypeJndi dsTypeJndi) {
        DSTypeJndiDTO dsTypeJndiDTO = new DSTypeJndiDTO();
        dsTypeJndiDTO.setId(dsTypeJndi.getId());
        dsTypeJndiDTO.setGlobalConnections(dsTypeJndi.getGlobalConnections().getGlobalId());
        dsTypeJndiDTO.setVisible(dsTypeJndi.getVisible());
        dsTypeJndiDTO.setDataSourceProvider(dsTypeJndi.getDataSourceProvider());
        dsTypeJndiDTO.setDatabaseDialect(dsTypeJndi.getDatabaseDialect());
        dsTypeJndiDTO.setDatabaseName(dsTypeJndi.getDatabaseName());
        dsTypeJndiDTO.setDriverClassName(dsTypeJndi.getDriverClassName());
        dsTypeJndiDTO.setLookUpName(dsTypeJndi.getLookUpName());
        dsTypeJndiDTO.setLastUpdatedTime(dsTypeJndi.getLastUpdatedTime());

        return dsTypeJndiDTO;
    }

    public static DSTypeNoSQLDTO setNoSqlDTO(DSTypeNoSQL dsTypeNoSQL) {
        DSTypeNoSQLDTO dsTypeNoSQLDTO = new DSTypeNoSQLDTO();
        dsTypeNoSQLDTO.setGlobalConnections(dsTypeNoSQL.getGlobalConnections().getGlobalId());
        dsTypeNoSQLDTO.setId(dsTypeNoSQL.getId());
        dsTypeNoSQLDTO.setVisible(dsTypeNoSQL.getVisible());
        dsTypeNoSQLDTO.setDataSourcePoolId(dsTypeNoSQL.getDataSourcePoolId());
        dsTypeNoSQLDTO.setDataSourceProvider(dsTypeNoSQL.getDataSourceProvider());
        dsTypeNoSQLDTO.setForceAlternateUsername(dsTypeNoSQL.isForceAlternateUsername());
        dsTypeNoSQLDTO.setUsername(dsTypeNoSQL.getUsername());
        dsTypeNoSQLDTO.setPassword(dsTypeNoSQL.getPassword());
        dsTypeNoSQLDTO.setUrl(dsTypeNoSQL.getUrl());
        dsTypeNoSQLDTO.setTestWhileIdle(dsTypeNoSQL.isTestWhileIdle());
        dsTypeNoSQLDTO.setTestOnBorrow(dsTypeNoSQL.isTestOnBorrow());
        dsTypeNoSQLDTO.setTestOnReturn(dsTypeNoSQL.isTestOnReturn());
        dsTypeNoSQLDTO.setTimeBetweenEvictionRunsMillis(dsTypeNoSQL.getTimeBetweenEvictionRunsMillis());
        dsTypeNoSQLDTO.setMaxActive(dsTypeNoSQL.getMaxActive());
        dsTypeNoSQLDTO.setMinIdle(dsTypeNoSQL.getMinIdle());
        dsTypeNoSQLDTO.setMaxWait(dsTypeNoSQL.getMaxWait());
        dsTypeNoSQLDTO.setInitialSize(dsTypeNoSQL.getInitialSize());
        dsTypeNoSQLDTO.setRemoveAbandonedTimeout(dsTypeNoSQL.getRemoveAbandonedTimeout());
        dsTypeNoSQLDTO.setRemoveAbandoned(dsTypeNoSQL.isRemoveAbandoned());
        dsTypeNoSQLDTO.setLogAbandoned(dsTypeNoSQL.isLogAbandoned());
        dsTypeNoSQLDTO.setMinEvictableIdleTimeMillis(dsTypeNoSQL.getMinEvictableIdleTimeMillis());
        dsTypeNoSQLDTO.setJmxEnabled(dsTypeNoSQL.isJmxEnabled());
        dsTypeNoSQLDTO.setDatabaseDialect(dsTypeNoSQL.getDatabaseDialect());
        dsTypeNoSQLDTO.setDatabaseName(dsTypeNoSQL.getDatabaseName());
        dsTypeNoSQLDTO.setLastUpdatedTime(dsTypeNoSQL.getLastUpdatedTime());
        dsTypeNoSQLDTO.setDriverClassName(dsTypeNoSQL.getDriverClassName());
        dsTypeNoSQLDTO.setValidationQuery(dsTypeNoSQL.getValidationQuery());
        dsTypeNoSQLDTO.setHiveReferenceId(dsTypeNoSQL.getHiveReferenceId());
        dsTypeNoSQLDTO.setCollection(dsTypeNoSQL.getCollection());
        dsTypeNoSQLDTO.setSubType(dsTypeNoSQL.getSubType());
        return dsTypeNoSQLDTO;

    }

}
