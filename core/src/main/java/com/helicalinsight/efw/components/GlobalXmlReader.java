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

package com.helicalinsight.efw.components;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by author on 05-02-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class GlobalXmlReader implements IComponent {

    @Override
    public String executeComponent(String formData) {
        JSONObject formDataJson = JSONObject.fromObject(formData);
        String type = formDataJson.getString("type");
        String id = formDataJson.getString("id");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", id);
        parameters.put("type", type);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        JSONObject theDataSource = findDataSource(id, type);

        if (theDataSource == null) {
            throw new EfwServiceException(String.format("The given data source with id %s and " +
                    "type %s could " + "not be found", id, type));
        }

        @SuppressWarnings("rawtypes") Iterator iterator = theDataSource.keys();
        JSONObject dataSourceJson = new JSONObject();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            if ("username".equals(key)) {
                dataSourceJson.put("userName", theDataSource.get(key));
                continue;
            }
            if ("driverClassName".equals(key)) {
                dataSourceJson.put("driverName", theDataSource.get(key));
                continue;
            }
            if ("url".equals(key)) {
                dataSourceJson.put("jdbcUrl", theDataSource.get(key));
                continue;
            }
            dataSourceJson.put(key, theDataSource.get(key));
        }
        return dataSourceJson.toString();
    }

    private JSONObject findDataSource(String id, String type) {
        JSONObject globalJson = JsonUtils.getGlobalConnectionsJson();
        List<String> keys = JsonUtils.getKeys(globalJson);

        for (String key : keys) {
            Object theKey = globalJson.get(key);
            if (theKey instanceof JSONArray) {
                JSONArray jsonArray = globalJson.getJSONArray(key);
                for (int counter = 0; counter < jsonArray.size(); counter++) {
                    JSONObject aDataSource = jsonArray.getJSONObject(counter);
                    if (check(id, type, aDataSource)) {
                        return aDataSource;
                    }
                }
            } else if (theKey instanceof JSONObject) {
                JSONObject aDataSource = globalJson.getJSONObject(key);
                if (check(id, type, aDataSource)) {
                    return aDataSource;
                }
            }
        }
        return null;
    }

    private boolean check(String id, String type, JSONObject aDataSource) {
        String theId = aDataSource.getString("@id");
        String theType = aDataSource.getString("@type");
        return id.equals(theId) && type.equals(theType);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
