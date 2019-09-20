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

import com.helicalinsight.efw.framework.AvailablePluginClassesRepository;
import com.helicalinsight.efw.framework.Directory;
import com.helicalinsight.efw.framework.Plugin;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * Created by user on 2/6/2017.
 *
 * @author Rajasekhar
 */
public class AvailablePluginsJsonProvider implements IComponent {

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);

        String provideResources = "false";
        if (formData.has("provideResources")) {
            provideResources = formData.getString("provideResources");
        }

        String provideAvailableClasses = "false";
        if (formData.has("provideClassesList")) {
            provideAvailableClasses = formData.getString("provideClassesList");
        }

        AvailablePluginClassesRepository repository = AvailablePluginClassesRepository.getInstance();
        Map<String, Plugin> pluginsMap = repository.getRepository();

        JSONArray result = new JSONArray();

        boolean resourcesRequested = "true".equalsIgnoreCase(provideResources);
        boolean classesRequested = "true".equalsIgnoreCase(provideAvailableClasses);

        for (Map.Entry<String, Plugin> entry : pluginsMap.entrySet()) {
            String pluginName = entry.getKey();
            Plugin plugin = entry.getValue();

            JSONObject json = new JSONObject();
            json.accumulate("id", plugin.getUuid());
            json.accumulate("name", pluginName);

            JSONObject details = new JSONObject();
            details.accumulate("jdbcDriver", plugin.isDriver() ? "Yes" : "No");
            details.accumulate("entryPoint", plugin.getEntryPointClasses());

            json.accumulate("details", details);
            json.accumulate("status", plugin.isEnabled() ? "Enabled" : "Disabled");

            if (resourcesRequested) {
                Directory directory = plugin.getDirectory();
                json.accumulate("resources", directory == null ? "No Resources" : directory.getResources());
            }

            if (classesRequested) {
                json.accumulate("classes", plugin.getClasses());
            }

            result.add(json);
        }
        JSONObject response;
        response = new JSONObject();
        response.accumulate("data", result);

        return response.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}