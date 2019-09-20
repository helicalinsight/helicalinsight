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

package com.helicalinsight.efw.components;

import com.helicalinsight.efw.exceptions.ClassNotConfiguredException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.XmlConfigurationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by user on 11/26/2015.
 *
 * @author Rajasekhar
 */
public class DsTypeHandlerFactory {

    public static EfwdDataSourceHandler handler(String type) {
        JSONObject settingsJson = JsonUtils.getSettingsJson();
        if (!settingsJson.has("efwdDataSourceHandlers")) {
            throw new XmlConfigurationException("Setting XML is not configured with the efwd datasource handlers");
        }
        JSONArray efwdDataSourceHandlers = settingsJson.getJSONArray("efwdDataSourceHandlers");
        String clazz = null;
        for (Object typeHandler : efwdDataSourceHandlers) {
            JSONObject handler = JSONObject.fromObject(typeHandler);
            String classifier = handler.getString("@classifier");
            if (classifier.equalsIgnoreCase(type)) {
                clazz = handler.getString("@class");
                break;
            }
        }

        if (clazz == null) {
            throw new ClassNotConfiguredException("Efwd datasource handler class not configured");
        }

        EfwdDataSourceHandler handler = FactoryMethodWrapper.getTypedInstance(clazz, EfwdDataSourceHandler.class);

        if (handler == null) {
            throw new EfwServiceException("Couldn't produce instance of type EfwdDataSourceHandler");
        } else {
            return handler;
        }
    }
}
