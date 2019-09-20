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

import com.helicalinsight.datasource.EfwdConnection;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import net.sf.json.JSONObject;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 11/26/2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class SqlJdbcHandler extends EfwdDataSourceHandler {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public JSONObject readDS(String id, String type, JSONObject json) {
        return getManipulatedJson(id, type, json);
    }


    private JSONObject getManipulatedJson(String id, String type, JSONObject connection) {
        JSONObject jdbcConnection = new JSONObject();
        jdbcConnection.accumulate("@id", id);
        jdbcConnection.accumulate("@name", (connection.has("@name") ? connection.getString("@name") : id));
        jdbcConnection.accumulate("@type", type);
        jdbcConnection.accumulate("@baseType", type);
        jdbcConnection.accumulate("jdbcUrl", connection.getString("Url"));
        jdbcConnection.accumulate("userName", connection.getString("User"));
        jdbcConnection.accumulate("password", connection.getString("Pass"));
        jdbcConnection.accumulate("driverName", connection.getString("Driver"));
        return jdbcConnection;
    }

    @Override
    public JSONObject writeDS(String id, String type, JSONObject json) {
        return null;
    }

    @Override
    public JSONObject updateDS(String id, String type, JSONObject json) {
        return null;
    }

    @Override
    public JSONObject testDS(JSONObject json) {
        JSONObject model;
        model = new JSONObject();

        JSONObject formDataJson = JSONObject.fromObject(json);
        String url;
        String userName;
        String password;
        String driverName;
        try {
            url = formDataJson.getString("jdbcUrl");
            userName = formDataJson.getString("userName");
            password = formDataJson.getString("password");
            driverName = formDataJson.getString("driverName");
        } catch (Exception ex) {
            throw new IncompleteFormDataException(ExceptionUtils.getRootCauseMessage(ex));
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("jdbcUrl", url);
        parameters.put("userName", userName);
        parameters.put("password", password);
        parameters.put("driverName", driverName);

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        EfwdConnection efwdConnection = getEfwdConnection(url, userName, password, driverName);

        Connection connection = efwdConnection.getConnection();
        if (connection != null) {
            model.accumulate("message", "The connection test is successful");
            DbUtils.closeQuietly(connection);
        }

        return model;
    }

    public EfwdConnection getEfwdConnection(String url, String userName, String password, String driverName) {
        EfwdConnection efwdConnection = ApplicationContextAccessor.getBean(EfwdConnection.class);

        efwdConnection.setUrl(url);
        efwdConnection.setUser(userName);
        efwdConnection.setPassword(password);
        efwdConnection.setDriver(driverName);
        return efwdConnection;
    }
}
