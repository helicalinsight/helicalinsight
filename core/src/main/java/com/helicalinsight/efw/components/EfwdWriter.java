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

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.DuplicateDatasourceConnectionException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by author on 30-01-2015.
 *
 * @author Rajasekhar
 */
public class EfwdWriter implements IComponent {

    @Override
    public String executeComponent(String formData) {
        JSONObject formDataJson = JSONObject.fromObject(formData);

        String name;
        String type;
        String directory;
        String driverName;
        String url;
        String userName;
        String password;

        try {
            directory = formDataJson.getString("directory");
            type = formDataJson.getString("type");

            userName = formDataJson.getString("userName");
            password = formDataJson.getString("password");
            url = formDataJson.getString("jdbcUrl");
            driverName = formDataJson.getString("driverName");
            name = formDataJson.getString("name");
        } catch (Exception ex) {
            throw new IncompleteFormDataException(ExceptionUtils.getRootCauseMessage(ex));
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("type", type);
        parameters.put("directory", directory);
        parameters.put("driverName", driverName);
        parameters.put("jdbcUrl", url);
        parameters.put("userName", userName);
        parameters.put("password", password);

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        EfwdWriterUtility utility = new EfwdWriterUtility(name, type, directory, driverName, url, userName, password);

        JSONObject model;
        model = new JSONObject();
        try {
            utility.write();
            model.accumulate("message", "The data source has been saved successfully.");
        } catch (DuplicateDatasourceConnectionException e) {
            throw new RuntimeException("The given data source already exists in the directory " + directory, e);
        }
        return model.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
