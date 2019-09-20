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

package com.helicalinsight.datasource;

import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import net.sf.json.JSONObject;


/**
 * Created by user on 11/26/2015.
 *
 * @author Rajasekhar
 */
public class ConnectionProviderFactory {


    public static Object getConnection(JSONObject formJson, String type) {
        DataSourceSecurityUtility.isDataSourceAuthenticated(formJson);
        IConnectionFactory iConnectionFactory = connectionFactory(formJson);
        return iConnectionFactory.getConnection(type, formJson.toString());
    }


    private static IConnectionFactory connectionFactory(JSONObject formData) {
        if (!formData.has("connectionProvider")) {
            throw new ImproperMethodCallException("Parameter connectionProvider is missing in the Json.");
        }

        String connectionProvider = formData.getString("connectionProvider");
        IConnectionFactory iConnectionFactory = FactoryMethodWrapper.getTypedInstance(connectionProvider,
                IConnectionFactory.class);

        if (iConnectionFactory == null) {
            throw new ConfigurationException("The application configuration(setting.xml) is incorrect.");
        }

        return iConnectionFactory;
    }

    private static class ImproperMethodCallException extends RuntimeException {
        public ImproperMethodCallException(String message) {
            super(message);
        }
    }
}
