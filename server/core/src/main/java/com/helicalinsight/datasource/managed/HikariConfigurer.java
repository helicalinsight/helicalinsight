package com.helicalinsight.datasource.managed;

import com.google.gson.Gson;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.datasource.managed.jaxb.HikariProperties;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.ParentLastClassLoader;
import com.helicalinsight.efw.framework.PluginFinder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
class HikariConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(HikariConfigurer.class);

    private final IDataSourcePool dataSourcePool = DataSourcePool.getInstance();

    @NotNull
    public DataSource newDataSource(@NotNull HashMapKey hashMapKey, String json) {
        javax.sql.DataSource hikariDataSource = prepareHikariDataSource(json);

        if (logger.isInfoEnabled()) {
            logger.info("Registering DataSource pool of type Hikari");
        }

        this.dataSourcePool.register(hashMapKey, hikariDataSource, json);
        return hikariDataSource;
    }

    @NotNull
    private HikariDataSource prepareHikariDataSource(String json) {
        HikariProperties hikariProperties = new Gson().fromJson(json, HikariProperties.class);

        String userName = hikariProperties.getUserName();
        String password = CipherUtils.decrypt(hikariProperties.getPassword());
        String jdbcUrl = hikariProperties.getJdbcUrl();

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(userName);
        hikariConfig.setPassword(password);
        final String leakDetectionThreshold = hikariProperties.getLeakDetectionThreshold();
        if (leakDetectionThreshold != null) {
            hikariConfig.setLeakDetectionThreshold(Long.valueOf(leakDetectionThreshold));
        }

        final String idleTimeout = hikariProperties.getIdleTimeout();
        if (idleTimeout != null) {
            hikariConfig.setIdleTimeout(Long.valueOf(idleTimeout));
        }
        hikariConfig.setMinimumIdle(hikariProperties.getMinimumIdle());
        hikariConfig.setMaxLifetime(hikariProperties.getMaxLifetime());
        hikariConfig.setMaximumPoolSize(hikariProperties.getMaximumPoolSize());

        Long connectionTimeoutMs = null;
        try {
            connectionTimeoutMs = Long.valueOf(hikariProperties.getConnectionTimeout());
        } catch (NumberFormatException ignore) {
        }

        if (connectionTimeoutMs == null) {
            //Default is 30 seconds. Make it 180
            connectionTimeoutMs = 180000L;
        }

        hikariConfig.setConnectionTimeout(connectionTimeoutMs);

        hikariConfig.setConnectionTestQuery(hikariProperties.getConnectionTestQuery());
        hikariConfig.setPoolName(hikariProperties.getPoolName());
        String driverName = com.helicalinsight.efw.utility.DriverClassCompat.normalize(
                JsonUtils.getKeyFromJson(json, "driverName"));
        hikariConfig.setDriverClassName(driverName);

        try {
            FactoryMethodWrapper.forName(driverName);
        } catch (ClassNotFoundException ignore) {
            //Not available in class path. Let hikari find the class using our pulugin class loader
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                PluginFinder pluginFinder = new PluginFinder();
                ParentLastClassLoader pluginClassLoader = pluginFinder.getPluginClassLoader(driverName);
                if (pluginClassLoader == null) {
                    throw new EfwdServiceException("Failed to find the driver class definition in plugins");
                }
                Thread.currentThread().setContextClassLoader(pluginClassLoader);
                return new HikariDataSource(hikariConfig);
            } catch (Exception ex) {
                throw new EfwdServiceException(ex);
            } finally {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }
        }

        return new HikariDataSource(hikariConfig);
    }
}