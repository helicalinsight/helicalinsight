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

import com.helicalinsight.datasource.managed.TestConnectionProvider;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONObject;


/**
 * Created by author on 09-02-2015.
 *
 * @author Rajasekhar
 */
public class GlobalConnectionsTester implements IComponent {

    @Override
    public String executeComponent(String formData) {
        JSONObject formDataJson = JSONObject.fromObject(formData);
        if (formDataJson.has("id")) {
            DataSourceSecurityUtility.isDataSourceAuthenticated(formDataJson);
        }
        String dataSourceProvider = JSONObject.fromObject(formData).getString("dataSourceProvider");
        if (!"jndi".equalsIgnoreCase(dataSourceProvider)) {
            if (!formDataJson.has("model")) {
                ControllerUtils.validate(formDataJson);
            }
        }

        TestConnectionProvider testConnectionProvider = ApplicationContextAccessor.getBean(TestConnectionProvider
                .class);

        JSONObject model;
        model = new JSONObject();

        if (!testConnectionProvider.testConnection(formData)) {
            model.accumulate("message", "The data source details provided are incorrect. Could " + "not get database " +
                    "connection.");
        } else {
            model.accumulate("message", "The connection test is successful.");
        }
        return model.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
