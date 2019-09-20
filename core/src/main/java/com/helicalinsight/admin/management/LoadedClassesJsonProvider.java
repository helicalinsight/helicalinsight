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

import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.*;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by user on 2/6/2017.
 *
 * @author Rajasekhar
 */
public class LoadedClassesJsonProvider implements IComponent {

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);

        String provideCore = "false";
        String providePlugins = "false";

        ClassLoader applicationClassLoader = LoadedClassesJsonProvider.class.getClassLoader();

        if (formData.has("provideCore")) {
            provideCore = formData.getString("provideCore");
        }

        if (formData.has("providePlugins")) {
            providePlugins = formData.getString("providePlugins");
        }
        JSONObject result;
        result = new JSONObject();

        if ("true".equalsIgnoreCase(provideCore)) {
            List<String> loadedClasses;
            try {
                loadedClasses = PluginUtils.getLoadedClasses(applicationClassLoader);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new EfwServiceException("Couldn't retrieve the classes list from JVM", ex);
            }
            result.accumulate("coreClasses", loadedClasses);
        }

        if ("true".equalsIgnoreCase(providePlugins)) {
            PluginsRegistry registry = PluginsRegistry.getInstance();
            Map<String, PluginEntry> enabledPlugins = registry.getEnabledPlugins();

            JSONArray pluginClasses;
            pluginClasses = new JSONArray();

            for (Map.Entry<String, PluginEntry> entry : enabledPlugins.entrySet()) {
                JSONObject json = new JSONObject();
                PluginEntry pluginEntry = entry.getValue();
                ParentLastClassLoader parentLastClassLoader = pluginEntry.getParentLastClassLoader();

                Plugin plugin = pluginEntry.getPlugin();
                json.accumulate("id", plugin.getUuid());
                json.accumulate("name", plugin.getName());
                json.accumulate("classes", parentLastClassLoader.getRegistry());
                json.accumulate("status", "Enabled");
                pluginClasses.add(json);
            }

            result.accumulate("pluginClasses", pluginClasses);
        }
        return result.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}