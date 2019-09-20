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
import net.sf.json.JSONObject;

/**
 * @author Somen
 *         Created  on 2/9/2017.
 */
public class CancelQuery implements IComponent {

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        JSONObject responseJson = new JSONObject();
        ActiveQueryRegistry registry = ActiveQueryRegistry.getRegistry();
        if (formJson.has("queryId")) {
            String queryId = formJson.getString("queryId");

            responseJson.putAll(registry.cancelQuery(queryId));
        }
        if (formJson.has("threadName")) {
            String threadName = formJson.getString("threadName");
            responseJson.putAll(registry.cancelQueryByThreadName(threadName));

        }
        return responseJson.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
