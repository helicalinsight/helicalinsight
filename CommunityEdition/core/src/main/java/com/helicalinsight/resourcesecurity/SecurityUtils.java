/**
 *    Copyright (C) 2013-2017 Helical IT Solutions (http://www.helicalinsight.com).
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

import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.XmlConfigurationException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcesecurity.jaxb.Security;

import java.util.Map;

/**
 * Created by author on 26-05-2015.
 *
 * @author Rajasekhar
 */
public class SecurityUtils {

    /**
     * @return The Security object that represents the xml structure of security
     */
    public static Security securityObject() {
        final Security security = ApplicationContextAccessor.getBean(Security.class);

        final String userId = AuthenticationUtils.getUserId();
        security.setCreatedBy(userId);
        return security;
    }

    /**
     * Returns the jaxb class name of the extension from the setting.xml
     * <p/>
     *
     * @param extension An extension of a file that is configured in xml
     * @return Returns the jaxb class of the extension. Only the resources extensions
     * configured in the Extentions tag of the setting.xml are returned.
     */
    public static String jaxBClassForKey(String extension) {
        if (extension == null) {
            throw new IllegalArgumentException("The parameter extension can't be null");
        }

        Map<String, String> jaxbClassesMap = ApplicationContextAccessor.getBean(FileExtensionKeyAndJaxbClassMapBean
                .class).resourceKeyClassMap();

        String value = null;
        for (Map.Entry<String, String> entry : jaxbClassesMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(extension)) {
                value = entry.getValue();
                break;
            }
        }

        if (value == null) {
            throw new XmlConfigurationException(String.format("No class is configured in the" + "" +
                    " application configuration for %s.", extension));
        }
        return value;
    }
}
