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

package com.helicalinsight.efw.vf;

import com.helicalinsight.efw.utility.ConfigurationFileReader;

import java.util.Map;

public class EFWManager {

    private Map<String, String> c3ChartMap = ConfigurationFileReader
            .mapFromClasspathPropertiesFile("efwc3chart.properties");

    public EFWC3Chart getC3Chart(String subtype) {
        EFWC3Chart efwc3ChartObject = null;
        String c3ChartSubtypeKey = "";
        String c3ChartClassName;
        if (subtype.length() > 0) {
            for (Map.Entry<String, String> entry : c3ChartMap.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(subtype)) {
                    c3ChartSubtypeKey = entry.getKey();
                    break;
                }
            }
            c3ChartClassName = c3ChartMap.get(c3ChartSubtypeKey);
        } else {
            c3ChartClassName = c3ChartMap.get("line");
        }
        try {
            efwc3ChartObject = (EFWC3Chart) Class.forName(c3ChartClassName).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return efwc3ChartObject;
    }
}
