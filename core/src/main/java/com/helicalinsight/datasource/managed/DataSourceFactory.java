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

import com.helicalinsight.datasource.DataSourceProviders;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.utility.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by author on 14-Dec-14.
 *
 * @author Rajasekhar
 */
@Component
class DataSourceFactory implements IDataSourceFactory {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceFactory.class);

    private final IDataSourcePool dataSourcePool = DataSourcePool.getInstance();

    @Value("#{projectProperties['enableDataSourceSharing']}")
    private String enableSharing;

    @Autowired
    private HikariConfigurer hikariConfigurer;

    @Autowired
    private TomcatConfigurer tomcatConfigurer;

    @Autowired
    private JndiConfigurer jndiConfigurer;

    @Autowired
    private ISharableDataSource sharableDataSource;

    public javax.sql.DataSource sqlDataSource(String json) {
        if (JsonUtils.isJndiLookUpRequested(json)) {
            //Don't worry about thread safety. DS is already created by Server.
            return this.jndiConfigurer.getJndiDataSource(json);
        }

        Map<HashMapKey, String> jsonMap = this.dataSourcePool.getJsonMap();
        Map<HashMapKey, javax.sql.DataSource> pooledDataSources = this.dataSourcePool.getPooledReferences();

        HashMapKey hashMapKey;
        if (jsonMap.containsValue(json)) {
            hashMapKey = CollectionUtils.getKeyByValue(jsonMap, json);

            if (hashMapKey != null) {
                return pooledDataSources.get(hashMapKey);
            } else {//Usually we never reach here
                hashMapKey = PoolUtils.newMapKey(json);
            }
        } else {
            hashMapKey = PoolUtils.newMapKey(json);
        }

        return getDataSource(hashMapKey, json, pooledDataSources);
    }

    private javax.sql.DataSource getDataSource(HashMapKey hashMapKey, String json, Map<HashMapKey,
            DataSource> pooledReferences) {
        //Check whether the data sources can be shared
        if ("true".equalsIgnoreCase(this.enableSharing)) {
            DataSource dataSource = this.sharableDataSource.shared(hashMapKey, pooledReferences);
            String jdbcUrl = hashMapKey.getJdbcUrl();
            if ((dataSource != null)) {
                if (logger.isInfoEnabled()) {
                    logger.info(String.format("Found a cached sharable DataSource for the " + "database url %s ",
                            jdbcUrl));
                }
                return dataSource;
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info(String.format("Caching a new DataSource for the url %s", jdbcUrl));
                }
            }
        }

        String dataSourceProvider = hashMapKey.getDataSourceProvider();
        if (DataSourceProviders.TOMCAT.equalsIgnoreCase(dataSourceProvider)) {
            return this.tomcatConfigurer.newDataSource(hashMapKey, json);
        } else if (DataSourceProviders.HIKARI.equalsIgnoreCase(dataSourceProvider)) {
            return this.hikariConfigurer.newDataSource(hashMapKey, json);
        } else {
            throw new MalformedJsonException("The configuration dataSourceProvider " +
                    dataSourceProvider + " can't be used to get a connection. Unknown " +
                    "Configuration.");
        }
    }
}