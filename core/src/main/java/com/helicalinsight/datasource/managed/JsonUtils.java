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

package com.helicalinsight.datasource.managed;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.DataSourceProviders;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import net.sf.json.JSONObject;
import org.springframework.http.ResponseEntity;


/**
 * Created by author on 25-Dec-14.
 *
 * @author Rajasekhar
 */
public class JsonUtils {


    public static String extractPassword(String json) {
        String password = JsonUtils.getKeyFromJson(json, "password");
        if (password == null) {
            throw new MalformedJsonException("Can't obtain connection with null password");
        }
        return password;
    }


    public static String getKeyFromJson(String json, String key) {
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement != null) {
            return jsonElement.getAsString();
        } else {
            return null;
        }
    }


    public static String extractUserName(String json) {
        String username = JsonUtils.getKeyFromJson(json, "username");
        if (username == null) {
            username = JsonUtils.getKeyFromJson(json, "userName");
            if (username == null) {
                throw new MalformedJsonException("Can't obtain connection with 'null' username");
            }
        }
        return username;
    }

    public static boolean isJndiLookUpRequested(String json) {
        String dataSourceProvider = JsonUtils.getKeyFromJson(json, "dataSourceProvider");
        return DataSourceProviders.JNDI.equalsIgnoreCase(dataSourceProvider);
    }

    public static boolean isNonPooled(String json) {
        String dataSourceProvider = JsonUtils.getKeyFromJson(json, "dataSourceProvider");
        return DataSourceProviders.NONE.equalsIgnoreCase(dataSourceProvider);
    }

    public static String extractDriverName(String json) {
        String driverName = JsonUtils.getKeyFromJson(json, "driverName");
        if (driverName == null) {
            driverName = JsonUtils.getKeyFromJson(json, "driverClassName");
            if (driverName == null) {
                throw new MalformedJsonException("The json is malformed as there is no " + "driverName or " +
                        "driverClassName parameter");
            }
        }
        return driverName;
    }


    public static String extractUrl(String json) {
        String jdbcUrl = JsonUtils.getKeyFromJson(json, "jdbcUrl");
        if (jdbcUrl == null) {
            jdbcUrl = JsonUtils.getKeyFromJson(json, "url");
            if (jdbcUrl == null) {
                throw new MalformedJsonException("The json parameters jdbcUrl or url is not " + "present in json");
            }
        }
        return jdbcUrl;
    }

    public static ResponseEntity<?> get500ErrorResponse(Exception e) {
        JSONObject status = new JSONObject();
        status.put("cause", e.getCause());
        status.put("message", e.getMessage());
        return ResponseEntity.status(500).body(status.toString());
    }
}
