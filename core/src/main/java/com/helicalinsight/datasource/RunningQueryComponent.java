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

package com.helicalinsight.datasource;

import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Somen
 *         Created  on 2/10/2017.
 */
public class RunningQueryComponent implements IComponent {

    @Override
    public String executeComponent(String jsonFormData) {
        ActiveQueryRegistry registry = ActiveQueryRegistry.getRegistry();
        JSONObject responseJson = new JSONObject();
        JSONArray data = registry.listDetails();
        if (data.size() > 0) {
            responseJson.put("data", data);
        } else {
            responseJson.put("message", "No query running in the background found");
        }
        return responseJson.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}