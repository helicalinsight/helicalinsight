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

import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.managed.DataSourcePool;
import com.helicalinsight.datasource.managed.HashMapKey;
import com.helicalinsight.datasource.managed.IDataSourcePool;
import com.helicalinsight.datasource.managed.PoolUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.zaxxer.hikari.HikariDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by author on 08-09-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class ManagedDataSourceShutdownHandler implements IComponent {

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);
        JSONObject model;
        model = new JSONObject();

        JSONArray ids = getJsonArray(formData);

        List<Integer> integerList = new ArrayList<>();

        for (Object object : ids) {
            String id = (String) object;
            if ("".equals(id) || id.trim().length() == 0) {
                throw new RequiredParameterIsNullException("Parameter id value is null or empty");
            }

            Integer integer;
            try {
                integer = Integer.valueOf(id);
            } catch (NumberFormatException ex) {
                throw new EfwServiceException(ex);
            }
            integerList.add(integer);
        }

        for (Integer integer : integerList) {
            String json = DataSourceUtils.globalIdJson(integer);

            HashMapKey hashMapKey = PoolUtils.newMapKey(json);
            synchronized (ManagedDataSourceShutdownHandler.class) {
                IDataSourcePool dataSourcePool = DataSourcePool.getInstance();

                Map<HashMapKey, DataSource> pooledReferences = dataSourcePool.getPooledReferences();

                DataSource dataSource = pooledReferences.get(hashMapKey);
                if (dataSource != null) {
                    pooledReferences.remove(hashMapKey);

                    Map<HashMapKey, String> jsonMap = dataSourcePool.getJsonMap();
                    jsonMap.remove(hashMapKey);

                    close(dataSource);
                }
            }
        }

        model.accumulate("message", "The requested DataSource(s) is/are shutdown successfully.");
        return model.toString();
    }

    public static void close(DataSource dataSource) {
        if (dataSource == null) {
            return;
        }
        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            org.apache.tomcat.jdbc.pool.DataSource source = (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
            source.close(true);
        } else if (dataSource instanceof HikariDataSource) {
            HikariDataSource source = (HikariDataSource) dataSource;
            source.close();
        } else {
            throw new IllegalStateException("The DataSource is neither of type hikari nor tomcat.");
        }
    }

    private JSONArray getJsonArray(JSONObject formData) {
        JSONArray ids;
        if (formData.has("ids")) {
            try {
                ids = formData.getJSONArray("ids");
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        } else {
            throw new IncompleteFormDataException("The datasource ids array is missing in the request.");
        }

        if (ids == null || ids.size() == 0) {
            throw new IllegalArgumentException("Parameter ids is null or empty");
        }
        return ids;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
