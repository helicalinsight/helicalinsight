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

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONObject;

/**
 * Created by author on 01-09-2015.
 *
 * @author Rajasekhar
 */
public class ConfigurationReLoader implements IComponent {

    private final ApplicationProperties properties = ApplicationProperties.getInstance();

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);
        JSONObject model;
        model = new JSONObject();

        if (this.properties != null && formData.has("refresh") && "true".equalsIgnoreCase(formData.getString
                ("refresh"))) {
            synchronized (this.properties) {
                SettingsLoader settingsLoader = new SettingsLoader(this.properties);
                settingsLoader.loadApplicationSettings();
            }
            model.put("message", "Application settings are reloaded");
        } else {
            model.put("message", "Couldn't reload application settings.");
        }
        return model.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
