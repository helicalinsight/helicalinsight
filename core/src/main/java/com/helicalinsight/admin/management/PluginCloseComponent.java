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

import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.PluginsRegistry;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONObject;

/**
 * Created by user on 1/20/2017.
 *
 * @author Rajasekhar
 */
public class PluginCloseComponent implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        String pluginClass = null;
        if (formJson.has("pluginClass")) {
            pluginClass = formJson.getString("pluginClass");
        }

        if (pluginClass != null) {
            PluginsRegistry registry = PluginsRegistry.getInstance();
            registry.deRegisterAndClosePlugin(pluginClass);
        } else {
            throw new RequiredParameterIsNullException("Parameter pluginClass is null");
        }

        JSONObject result;
        result = new JSONObject();
        result.accumulate("message", "Successfully closed the plugin");
        return result.toString();
    }
}