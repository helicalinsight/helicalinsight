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

import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONObject;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import java.net.URL;
import java.util.Arrays;

public class LogLevelModifier implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        String logLevel = formJson.optString("setLevel");
        String info = formJson.optString("getLevel");
        JSONObject response = new JSONObject();

        if (info != null && !info.isEmpty()) {
            if ("options".equalsIgnoreCase(info)) {
                response.put("data", Arrays.asList("ALL,DEBUG,INFO,WARN,ERROR,FATAL,OFF,TRACE".split(",")));
            } else if ("currentLevel".equalsIgnoreCase(info)) {
                response.put("currentLevel", LogManager.getRootLogger().getLevel().toString());
            } else {
                throw new FormValidationException("The formData level is invalid");
            }
        } else if (logLevel == null || logLevel.isEmpty()) {
            throw new FormValidationException("The formData does not have logLevel information");
        } else {
            logLevel = logLevel.toUpperCase();
            Level level = Level.toLevel(logLevel);
            response.put("message", "Log level is set to " + level);
            response.put("currentLevel", logLevel);
            LogManager.getRootLogger().setLevel(level);
            updateFile(logLevel);
        }
        return response.toString();
    }

    private void updateFile(String level) {
        try {
            URL resource = this.getClass().getClassLoader().getResource("log4j.properties");
            PropertiesConfiguration properties = new PropertiesConfiguration(resource);
            properties.setProperty("log4j.rootLogger", level + ", file, stdout");

            properties.save();
        } catch (ConfigurationException ignore) {
        }
    }
}
