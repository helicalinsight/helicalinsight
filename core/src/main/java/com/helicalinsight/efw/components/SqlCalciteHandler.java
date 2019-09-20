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

import com.helicalinsight.datasource.calcite.CalciteConnectionProvider;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
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
public class SqlCalciteHandler extends EfwdDataSourceHandler {

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
        jdbcConnection.accumulate("driverName", connection.getString("driverName"));
        jdbcConnection.accumulate("model", connection.getString("model"));
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
        JSONObject response;
        response = new JSONObject();

        JSONObject formDataJson = JSONObject.fromObject(json);
        String model;
        String driverName;
        try {
            model = formDataJson.getString("model");
            driverName = formDataJson.getString("driverName");
        } catch (Exception ex) {
            throw new IncompleteFormDataException(ExceptionUtils.getRootCauseMessage(ex));
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("model", model);
        parameters.put("driverName", driverName);

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        CalciteConnectionProvider connectionProvider = ApplicationContextAccessor.getBean(CalciteConnectionProvider
                .class);

        Connection connection = connectionProvider.getConnection(model);
        if (connection != null) {
            response.accumulate("message", "The connection test is successful");
            DbUtils.closeQuietly(connection);
        } else {
            throw new EfwdServiceException("Couldn't get connection.");
        }

        return response;
    }
}
