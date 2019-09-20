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

package com.helicalinsight.resourcesecurity;

import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by author on 09-07-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("singleton")
public class FileExtensionKeyAndJaxbClassMapBean {

    private final Map<String, String> jaxbMap;

    public FileExtensionKeyAndJaxbClassMapBean() {
        Map<String, String> jaxbMap = new HashMap<>();

        JSONObject settingsJson = JsonUtils.getSettingsJson();

        JSONObject jaxbClasses = settingsJson.getJSONObject("jaxbClasses");

        Iterator<?> iterator = jaxbClasses.keys();

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            jaxbMap.put(key, jaxbClasses.getString(key));
        }

        this.jaxbMap = jaxbMap;
    }

    public Map<String, String> resourceKeyClassMap() {
        return Collections.unmodifiableMap(this.jaxbMap);
    }
}
