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

import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.managed.jaxb.HikariProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.ShareRuleXmlUpdateHandler;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Prashansa
 * @author Rajasekhar
 */
public class HikariCpDataSourceProperties {

    private static final Logger logger = LoggerFactory.getLogger(HikariCpDataSourceProperties.class);

    private static void accumulate(JSONObject formData, Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String theKey = entry.getKey();
            String theValue = entry.getValue();
            if (!formData.containsKey("poolSize")) {
                if ("hikari.maximumPoolSize".equalsIgnoreCase(theKey)) {
                    formData.accumulate("poolSize", theValue);
                }
            }

            if (!formData.containsKey("testQuery")) {
                if ("hikari.testQuery".equals(theKey)) {
                    formData.accumulate("testQuery", theValue);
                }
            }

            if (!formData.containsKey("connectionTimeout")) {
                if ("hikari.connectionTimeout".equals(theKey)) {
                    formData.accumulate("connectionTimeout", theValue);
                }
            }
        }
    }


    public synchronized static String writeHikariDataSourceType(HikariProperties connection, Integer maxId,
                                                                JSONObject formData, String mode) {
        Map<String, String> map = ApplicationUtilities.getDefaultsMap();
        String stringId = String.valueOf(maxId);
        accumulate(formData, map);
        if ("create".equalsIgnoreCase(mode)) {
            connection.setVisible("true");
            String stringDefaultMinIdle = map.get("hikari.minimumIdle");
            int intMinimumIdle = Integer.parseInt(stringDefaultMinIdle);
            connection.setMinimumIdle(intMinimumIdle);

            String maximumPoolSize = formData.getString("poolSize");
            int maximumPoolSizeIntegerValue = Integer.parseInt(maximumPoolSize);
            boolean flag = GlobalXmlWriter.checkPoolSize(maximumPoolSize, intMinimumIdle);
            if (flag) {
                intMinimumIdle = maximumPoolSizeIntegerValue - 1;
            }
            connection.setMinimumIdle(intMinimumIdle);

            connection.setMaxLifetime(Integer.valueOf(map.get("hikari.maxLifetime")));
            connection.setId(maxId);
            connection.setType(GlobalJdbcType.DYNAMIC_DATASOURCE);
            connection.setBaseType(GlobalJdbcType.TYPE);

            Security security = SecurityUtils.securityObject();
            connection.setDataSourceProvider("hikari");
            connection.setSecurity(security);
            edit(connection, formData, stringId);

            if (logger.isDebugEnabled()) {
                logger.debug("A new Hikari connection is created successfully.");
            }

        } else if ("edit".equalsIgnoreCase(mode)) {
            ControllerUtils.validate(formData);
            edit(connection, formData, stringId);
        } else if ("share".equalsIgnoreCase(mode)) {
            Security.Share share = connection.getShare();
            JSONObject shareJson = formData.getJSONObject("share");
            JSONObject revoke = null;
            if (formData.has("revoke")) {
                revoke = formData.optJSONObject("revoke");
            }
            connection.setShare(ShareRuleXmlUpdateHandler.setShareDataSource(share, shareJson, revoke));
        }


        JSONObject result;
        result = new JSONObject();
        result.accumulate("message", "A new Hikari connection is created successfully.");
        return result.toString();
    }

    private static void edit(HikariProperties connection, JSONObject formData, String stringId) {
        connection.setName(formData.getString("name"));
        connection.setConnectionTestQuery(formData.getString("testQuery"));

        connection.setUserName(formData.getString("userName"));
        connection.setPassword(formData.getString("password"));
        connection.setJdbcUrl(formData.getString("jdbcUrl"));
        connection.setDriverName(formData.getString("driverName"));
        connection.setMaximumPoolSize(Integer.valueOf(formData.getString("poolSize")));
        connection.setConnectionTimeout(formData.getString("connectionTimeout"));


        String driverName = formData.getString("driverName");
        String[] array = driverName.split("\\.");
        connection.setDataSourcePoolId("hikari_" + stringId);

        String vendorName = (array.length >= 2 ? array[1] : "");
        connection.setPoolName("hikari_" + vendorName + "_" + stringId);
    }
}