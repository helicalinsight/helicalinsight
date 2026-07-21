package com.helicalinsight.datasource.managed;

import com.google.gson.Gson;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.datasource.managed.jaxb.TomcatPoolProperties;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import javax.sql.DataSource;

/**
 * Created by author on 14-Dec-14.
 *
 * @author Rajasekhar
 */
@Component
class TomcatConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(TomcatConfigurer.class);

    private final IDataSourcePool dataSourcePool = DataSourcePool.getInstance();

    @NotNull
    DataSource newDataSource(@NotNull HashMapKey hashMapKey, String json) {
        TomcatPoolProperties tomcatPoolProperties = new Gson().fromJson(json, TomcatPoolProperties.class);
        PoolProperties poolProperties = new PoolProperties();

        String username = tomcatPoolProperties.getUsername();
        String password = tomcatPoolProperties.getPassword();
        String url = tomcatPoolProperties.getUrl();

        if (url == null || password == null || username == null) {
            throw new MalformedJsonException("The url or username or password is null. Provide " + "proper details.");
        }

        poolProperties.setUsername(username);
        poolProperties.setPassword(password);

        poolProperties.setUrl(tomcatPoolProperties.getUrl());
        poolProperties.setTestWhileIdle(tomcatPoolProperties.isTestWhileIdle());
        poolProperties.setTestOnBorrow(tomcatPoolProperties.isTestOnBorrow());
        poolProperties.setTestOnReturn(tomcatPoolProperties.isTestOnReturn());
        poolProperties.setValidationQuery(tomcatPoolProperties.getValidationQuery());
        poolProperties.setValidationInterval(tomcatPoolProperties.getValidationInterval());
        poolProperties.setTimeBetweenEvictionRunsMillis(tomcatPoolProperties.getTimeBetweenEvictionRunsMillis());
        poolProperties.setMaxActive(tomcatPoolProperties.getMaxActive());
        poolProperties.setMinIdle(tomcatPoolProperties.getMinIdle());
        poolProperties.setMaxWait(tomcatPoolProperties.getMaxWait());
        poolProperties.setInitialSize(tomcatPoolProperties.getInitialSize());
        poolProperties.setRemoveAbandonedTimeout(tomcatPoolProperties.getRemoveAbandonedTimeout());
        poolProperties.setRemoveAbandoned(tomcatPoolProperties.isRemoveAbandoned());
        poolProperties.setLogAbandoned(tomcatPoolProperties.isLogAbandoned());
        poolProperties.setMinEvictableIdleTimeMillis(tomcatPoolProperties.getMinEvictableIdleTimeMillis());
        poolProperties.setJmxEnabled(tomcatPoolProperties.isJmxEnabled());
        poolProperties.setJdbcInterceptors(tomcatPoolProperties.getJdbcInterceptors());
        poolProperties.setDriverClassName(com.helicalinsight.efw.utility.DriverClassCompat.normalize(
                tomcatPoolProperties.getDriverClassName()));
        String connectionProperties = tomcatPoolProperties.getConnectionProperties();
        if ( StringUtils.isNotBlank(connectionProperties)) {
        	poolProperties.setConnectionProperties(connectionProperties);
        }

        boolean forceAlternateUsername = tomcatPoolProperties.isForceAlternateUsername();
        if (forceAlternateUsername) {
            if (logger.isDebugEnabled()) {
                logger.debug("The property forceAlternateUsername is true. Setting it to false(default) improves the " +
                        "" + "performance while retrieving connections from the pool.");
            }
        }

        javax.sql.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);
        if (logger.isInfoEnabled()) {
            logger.info("Successfully prepared the Data Source " + dataSource.getClass() + ". " +
                    "Registering key for the DataSource provider " + hashMapKey.getDataSourceProvider());
        }

        ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).setAlternateUsernameAllowed(forceAlternateUsername);

        if (logger.isInfoEnabled()) {
            logger.info("Registering DataSource pool of type TomcatJdbc");
        }

        this.dataSourcePool.register(hashMapKey, dataSource, json);
        return dataSource;
    }
}
