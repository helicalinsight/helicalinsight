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

package com.helicalinsight.admin.management;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.DataSourceProviders;
import com.helicalinsight.datasource.managed.DataSourcePool;
import com.helicalinsight.datasource.managed.HashMapKey;
import com.helicalinsight.datasource.managed.IDataSourcePool;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

/**
 * Created by user on 1/29/2016.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class RetrievePooledConnections implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(RetrievePooledConnections.class);

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject model;
        model = new JSONObject();

        synchronized (ManagedDataSourceShutdownHandler.class) {
            JSONArray array = new JSONArray();
            IDataSourcePool dataSourcePool = DataSourcePool.getInstance();

            Map<HashMapKey, DataSource> pooledReferences = dataSourcePool.getPooledReferences();
            if (pooledReferences.size() == 0) {
                if (logger.isDebugEnabled()) {
                    logger.debug("The number of data sources with connections pooled in memory are 0");
                }
                model.accumulate("dataSources", array);
                return model.toString();
            }

            Set<Map.Entry<HashMapKey, DataSource>> entries = pooledReferences.entrySet();
            Map<HashMapKey, String> jsonMap = dataSourcePool.getJsonMap();

            for (Map.Entry<HashMapKey, DataSource> entry : entries) {
                HashMapKey key = entry.getKey();
                String json = jsonMap.get(key);

                JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
                String dataSourceProvider = jsonObject.get("dataSourceProvider").getAsString();

                if (DataSourceProviders.HIKARI.equalsIgnoreCase(dataSourceProvider) || DataSourceProviders.TOMCAT
                        .equalsIgnoreCase(dataSourceProvider)) {
                    JSONObject dataSource = new JSONObject();
                    dataSource.accumulate("id", jsonObject.get("id").getAsString());
                    dataSource.accumulate("name", jsonObject.get("name").getAsString());
                    dataSource.accumulate("type", jsonObject.get("type").getAsString());
                    dataSource.accumulate("baseType", jsonObject.get("baseType").getAsString());
                    dataSource.accumulate("dataSourceProvider", dataSourceProvider);
                    array.add(dataSource);
                }
            }
            model.accumulate("dataSources", array);
        }

        return model.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
