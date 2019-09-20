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

import com.google.gson.Gson;
import com.helicalinsight.datasource.managed.jaxb.HikariProperties;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.ParentLastClassLoader;
import com.helicalinsight.efw.framework.PluginFinder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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


    public DataSource newDataSource(HashMapKey hashMapKey, String json) {
        javax.sql.DataSource hikariDataSource = prepareHikariDataSource(json);

        if (logger.isInfoEnabled()) {
            logger.info("Registering DataSource pool of type Hikari");
        }

        this.dataSourcePool.register(hashMapKey, hikariDataSource, json);
        return hikariDataSource;
    }


    private HikariDataSource prepareHikariDataSource(String json) {
        HikariProperties hikariProperties = new Gson().fromJson(json, HikariProperties.class);

        String userName = hikariProperties.getUserName();
        String password = hikariProperties.getPassword();
        String jdbcUrl = hikariProperties.getJdbcUrl();

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(userName);
        hikariConfig.setPassword(password);

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
        String driverName = JsonUtils.getKeyFromJson(json, "driverName");
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