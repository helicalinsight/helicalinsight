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

import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Rajesh
 *         Created by helical019 on 4/23/2019.
 */
public class TypesDetailsProvider implements IComponent {
    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        JSONObject settingsJson = JsonUtils.getSettingsJson();
        JSONArray dataSources = removeAtFromKey(settingsJson.getJSONArray("DataSources"));
        JSONArray visualizationTypes = removeAtFromKey(settingsJson.getJSONArray("visualizationTypes"));
        JSONArray sqlTypes = removeAtFromKey(settingsJson.getJSONArray("sqlTypes"));
        JSONArray parameterTypes = removeAtFromKey(settingsJson.getJSONArray("parameterTypes"));
        JSONObject response = new JSONObject();

        if (formJson == null || formJson.isEmpty()) {
            response.put("sqlTypes", sqlTypes);
            response.put("vizTypes", visualizationTypes);
            response.put("connTypes", dataSources);
            response.put("parameterTypes", parameterTypes);
            return response.toString();
        } else if (formJson.has("actions")) {
            JSONArray actions = formJson.getJSONArray("actions");
            if (actions == null || actions.isEmpty()) {
                response.put("sqlTypes", sqlTypes);
                response.put("vizTypes", visualizationTypes);
                response.put("connTypes", dataSources);
                response.put("parameterTypes", parameterTypes);
                return response.toString();
            }
            for (int index = 0; index < actions.size(); index++) {
                String eachRequestedAction = actions.getString(index);
                if ("sqlTypes".equalsIgnoreCase(eachRequestedAction))
                    response.put("sqlTypes", sqlTypes);
                else if ("vizTypes".equalsIgnoreCase(eachRequestedAction))
                    response.put("vizTypes", visualizationTypes);
                else if ("connTypes".equalsIgnoreCase(eachRequestedAction))
                    response.put("connTypes", dataSources);
                else if ("parameterTypes".equalsIgnoreCase(eachRequestedAction))
                    response.put("parameterTypes", parameterTypes);
                else
                    response.put("message", "invalid action :" + eachRequestedAction);
            }
        } else {
            response.put("sqlTypes", sqlTypes);
            response.put("vizTypes", visualizationTypes);
            response.put("connTypes", dataSources);
            response.put("parameterTypes", parameterTypes);
            return response.toString();
        }
        return response.toString();
    }

    /**
     * This method is used to replace all the @ from the json.
     *
     * @param data
     * @return
     */
    private JSONArray removeAtFromKey(JSONArray data) {
        JSONArray newArray = new JSONArray();
        for (int index = 0; index < data.size(); index++) {
            JSONObject eachJsonObject = data.getJSONObject(index);
            JSONObject newJson = new JSONObject();
            for (Object eachK : eachJsonObject.keySet()) {
                String eachKey = (String) eachK;
                String eachValue = eachJsonObject.getString(eachKey);
                eachKey = eachKey.replaceFirst("@", "");
                eachKey = "class".equals(eachKey) ? "clazz" : eachKey;
                newJson.put(eachKey, eachValue);
            }
            newArray.add(newJson);
        }
        return newArray;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
