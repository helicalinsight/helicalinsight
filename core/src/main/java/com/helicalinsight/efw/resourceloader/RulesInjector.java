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

package com.helicalinsight.efw.resourceloader;

import com.helicalinsight.efw.utility.ConfigurationFileReader;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by author on 15-05-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("rawtypes")
public final class RulesInjector {


    private static final String RULES_PACKAGE = "com.helicalinsight.efw.resourceloader.rules.";
    private final Map<String, String> rules;
    private final List listOfKeys;
    private final JSONObject extensions;

    public RulesInjector(List listOfKeys, JSONObject extensions) {
        this.listOfKeys = listOfKeys;
        this.extensions = extensions;
        Map<String, String> properties = ConfigurationFileReader.mapFromClasspathPropertiesFile("project.properties");
        this.rules = new HashMap<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().contains(RULES_PACKAGE)) {
                rules.put(entry.getKey().replace(RULES_PACKAGE, ""), entry.getValue());
            }
        }
    }

    public void injectRules() {
        if (this.listOfKeys != null) {
            for (Map.Entry<String, String> entry : this.rules.entrySet()) {
                String key = entry.getKey();
                if (containsCaseInsensitive(key, this.listOfKeys)) {
                    String text = this.extensions.getString(key);
                    this.extensions.discard(key);
                    JSONObject rule = new JSONObject();
                    rule.accumulate("@visible", "true");
                    rule.accumulate("@rule", entry.getValue());
                    rule.accumulate("#text", text);
                    this.extensions.accumulate(key, rule);
                }
            }
        }
    }

    private boolean containsCaseInsensitive(String strToCompare, List list) {
        for (Object object : list) {
            String str = (String) object;
            if (str.equalsIgnoreCase(strToCompare)) {
                return true;
            }
        }
        return false;
    }
}
