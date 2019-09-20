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

package com.helicalinsight.efw.resourceloader.rules;

import com.helicalinsight.efw.exceptions.XmlConfigurationException;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by author on 12-01-2015.
 *
 * @author Rajasekhar
 */
public class ConfigurableRulesHelper {

    public static List<String> getConfigurableRulesList(JSONObject settings) {
        List<String> rules = new ArrayList<>();
        if (settings.has("security")) {
            JSONObject jsonObject = settings.getJSONObject("security");
            if (jsonObject.has("rules")) {
                JSONObject rulesJson = jsonObject.getJSONObject("rules");
                return getRules(rules, rulesJson);
            }
        }
        return null;
    }


    private static List<String> getRules(List<String> rules, JSONObject rulesJson) {
        try {
            rules.add(0, rulesJson.getJSONObject("rule").getString("@class"));
        } catch (JSONException ex) {
            try {
                JSONArray rulesArray = rulesJson.getJSONArray("rule");
                Iterator<?> iterator = rulesArray.iterator();
                //noinspection WhileLoopReplaceableByForEach
                while (iterator.hasNext()) {
                    rules.add(((JSONObject) iterator.next()).getString("@class"));
                }
            } catch (Exception e) {
                throw new XmlConfigurationException("The configuration of setting.xml is " +
                        "incorrect. Expected class attribute(s) for the rules node child node(s) " +
                        "of security tag.", e);
            }
        }
        return rules;
    }

    public static String getConfigurableRulesMode(JSONObject settings) {
        String mode;
        try {
            mode = settings.getJSONObject("security").getJSONObject("rules").getString("@mode");
        } catch (Exception e) {
            mode = "and";
        }
        return mode;
    }
}
