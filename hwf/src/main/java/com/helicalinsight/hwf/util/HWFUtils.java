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

package com.helicalinsight.hwf.util;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Somen
 * @date 11-May-2016
 */
public class HWFUtils {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HWFUtils.class);
    private static String settingPath = ApplicationProperties.getInstance().getSettingPath();
    private static Map<String, String> typeClassMap = new HashMap<>();


    public static boolean isClass(String className) {
        boolean exist = true;
        try {
            FactoryMethodWrapper.forName(className);
        } catch (ClassNotFoundException e) {
            try {
                FactoryMethodWrapper.extendedForName(className);
            } catch (ClassNotFoundException ex) {
                exist = false;
                logger.error("Class definition not found", ex);
            }
        }
        return exist;
    }

    public static Map<String, String> getTypeClassMap() {
        createClassMap();
        return typeClassMap;
    }

    public static void createClassMap() {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject jsonFxml = processor.getJSONObject(settingPath, false);
        JSONArray settingXmlHwfSourceList = jsonFxml.getJSONArray("HwfSources");
        for (int count = 0; count < settingXmlHwfSourceList.size(); count++) {
            String classType = settingXmlHwfSourceList.getJSONObject(count).getString("@type");
            String hwfClass = settingXmlHwfSourceList.getJSONObject(count).getString("@class");
            typeClassMap.put(classType, hwfClass);
        }
    }
}
