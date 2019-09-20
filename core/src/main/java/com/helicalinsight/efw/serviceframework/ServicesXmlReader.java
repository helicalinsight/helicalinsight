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

package com.helicalinsight.efw.serviceframework;

import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by author on 24-02-2015.
 *
 * @author Rajasekhar
 */
@Component
public class ServicesXmlReader {

    /**
     * Returns either null or the actual service implementation class. Picks the first matching
     * class among the configuration files.
     *
     * @param services    The json of services.xml file
     * @param type        Determines the type of feature
     * @param serviceType Actual service type
     * @param service     A specific service
     * @return The specific service implementation class
     */

    public String getServiceClass(JSONObject services, String type, String serviceType, String service) {
        String serviceClass;
        if (services.has(type)) {
            serviceClass = getClass(services, type, serviceType, service);
            if (serviceClass == null) {
                serviceClass = getServiceClassFromImports(services, type, serviceType, service);
            }
        } else {
            serviceClass = getServiceClassFromImports(services, type, serviceType, service);
        }
        return serviceClass;
    }


    private String getClass(JSONObject services, String type, String serviceType, String service) {
        try {
            JSONObject typeJson = services.getJSONObject(type);
            if (!typeJson.has(serviceType)) {
                return getServiceClassFromImports(services, type, serviceType, service);
            }
            JSONObject serviceTypeJson = typeJson.getJSONObject(serviceType);
            if (!serviceTypeJson.has(service)) {
                return getServiceClassFromImports(services, type, serviceType, service);
            }
            JSONObject actualService = serviceTypeJson.getJSONObject(service);
            return actualService.getString("@class");
        } catch (Exception ignore) {
        }
        return null;
    }


    private String getServiceClassFromImports(JSONObject services, String type, String serviceType,
                                              String service) {
        try {
            JSONArray importsArray;
            try {
                importsArray = services.getJSONArray("import");
            } catch (Exception e) {
                throw new EfwServiceException("The services.xml has no imports and the " + "serviceType configuration" +
                        " is not found in the existing xml.");
            }
            for (Object importedXml : importsArray) {
                JSONObject eachXml = (JSONObject) importedXml;
                String name = eachXml.getString("@name");
                if (name == null || "".equals(name) || "".equals(name.trim())) {
                    throw new ConfigurationException(String.format("The import element of " +
                            "components configuration file %s is not configured properly. The " +
                            "name is null of empty.", name));
                }
                JSONObject importedComponents = JsonUtils.getImportedXmlJson(name);
                String serviceClass = getServiceClass(importedComponents, type, serviceType, service);
                if (serviceClass != null) {
                    return serviceClass;
                }
            }
        } catch (Exception e) {
            throw new EfwServiceException(String.format("The expected type %s is not available " + "even after " +
                    "searching in importable configuration files. Check xml " +
                    "configuration. Could not produce service class object.", type), e);
        }
        return null;
    }
}