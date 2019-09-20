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
import com.helicalinsight.datasource.managed.jaxb.TomcatPoolProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.ShareRuleXmlUpdateHandler;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Prashansa
 * @author Rajasekhar
 * @author Somen
 */
public class TomcatJdbcDataSourceProperties {

    private static final Logger logger = LoggerFactory.getLogger(TomcatJdbcDataSourceProperties.class);


    public synchronized static String writeTomcatJdbcDataSourceType(TomcatPoolProperties connection, int maxId,
                                                                    JSONObject formData, String mode) {
        Map<String, String> map = ApplicationUtilities.getDefaultsMap();
        String stringId = String.valueOf(maxId);

        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();

        accumulate(formData, connection, iterator);

        if ("create".equalsIgnoreCase(mode)) {
            connection.setVisible("true");
            Security security = SecurityUtils.securityObject();
            connection.setSecurity(security);
            connection.setId(maxId);

            connection.setType(GlobalJdbcType.DYNAMIC_DATASOURCE);
            connection.setBaseType(GlobalJdbcType.TYPE);
            connection.setValidationQuery(formData.getString("testQuery"));

            if (logger.isDebugEnabled()) {
                logger.debug("A new Tomcat data source is created successfully.");
            }
            edit(connection, formData, stringId);
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
        result.accumulate("message", "A new Tomcat data source is created successfully.");
        return result.toString();
    }

    private static void edit(TomcatPoolProperties connection, JSONObject formData, String stringId) {
        connection.setName(formData.getString("name"));
        connection.setDataSourceProvider(formData.getString("dataSourceProvider"));
        connection.setUsername(formData.getString("userName"));
        connection.setPassword(formData.getString("password"));
        connection.setUrl(formData.getString("jdbcUrl"));
        connection.setDriverClassName(formData.getString("driverName"));
        connection.setMaxActive(Integer.valueOf(formData.getString("poolSize")));
        connection.setDataSourcePoolId("tomcat_" + stringId);
    }


    private static void accumulate(JSONObject formData, TomcatPoolProperties jdbcConnection,
                                   Iterator<Map.Entry<String, String>> iterator) {
        int initialSizeVal;
        String minimumNumberOfIdleConnections = null;
        boolean userProvidedPoolSize = false;
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String theKey = entry.getKey();
            String theValue = entry.getValue();

            if (!formData.containsKey("poolSize")) {
                if (theKey.equals("tomcat.maxActive")) {
                    formData.accumulate("poolSize", theValue);
                }
            }

            if (!formData.containsKey("testQuery")) {
                if (theKey.equals("tomcat.testQuery")) {
                    formData.accumulate("testQuery", theValue);
                }
            }

            if ("tomcat.initialSize".equals(theKey)) {
                String initialSize = theValue;
                initialSizeVal = Integer.parseInt(initialSize);

                if (formData.containsKey("poolSize")) {
                    String maxActive = formData.getString("poolSize");
                    int maxActiveValue = Integer.parseInt(maxActive);
                    boolean flag = GlobalXmlWriter.checkPoolSize(maxActive, initialSizeVal);
                    if (flag) {
                        initialSize = String.valueOf(maxActiveValue - 1);
                        minimumNumberOfIdleConnections = initialSize;
                        userProvidedPoolSize = true;
                        if (logger.isDebugEnabled()) {
                            logger.debug("The value of initialSize of the tomcat pool being " +
                                    "created is " + initialSize);
                        }
                    }
                }
                jdbcConnection.setInitialSize(initialSizeVal);
            } else if ("tomcat.forceAlternateUsername".equals(theKey)) {
                jdbcConnection.setForceAlternateUsername(Boolean.valueOf(theValue));
            } else if ("tomcat.testWhileIdle".equals(theKey)) {
                jdbcConnection.setTestWhileIdle(Boolean.valueOf(theValue));
            } else if ("tomcat.testOnBorrow".equals(theKey)) {
                jdbcConnection.setTestOnBorrow(Boolean.valueOf(theValue));
            } else if ("tomcat.testOnReturn".equals(theKey)) {
                jdbcConnection.setTestOnReturn(Boolean.valueOf(theValue));
            } else if ("tomcat.validationInterval".equals(theKey)) {
                jdbcConnection.setValidationInterval(Integer.valueOf(theValue));
            } else if ("tomcat.timeBetweenEvictionRunsMillis".equals(theKey)) {
                jdbcConnection.setTimeBetweenEvictionRunsMillis(Integer.valueOf(theValue));
            } else if ("tomcat.minIdle".equals(theKey)) {
                //Let the the minIdle be equal to initialSize. If the user has provided
                //pool size, then use initialSize is one less than maxActive. The same is used.
                //Else, the default value from the properties file is used.
                if (userProvidedPoolSize) {
                    jdbcConnection.setMinIdle(Integer.valueOf(minimumNumberOfIdleConnections));
                } else {
                    jdbcConnection.setMinIdle(Integer.valueOf(theValue));
                }
            } else if ("tomcat.maxWait".equals(theKey)) {
                jdbcConnection.setMaxWait(Integer.valueOf(theValue));
            } else if ("tomcat.removeAbandonedTimeout".equals(theKey)) {
                jdbcConnection.setRemoveAbandonedTimeout(Integer.valueOf(theValue));
            } else if ("tomcat.removeAbandoned".equals(theKey)) {
                jdbcConnection.setRemoveAbandoned(Boolean.valueOf(theValue));
            } else if ("tomcat.logAbandoned".equals(theKey)) {
                jdbcConnection.setLogAbandoned(Boolean.valueOf(theValue));
            } else if ("tomcat.minEvictableIdleTimeMillis".equals(theKey)) {
                jdbcConnection.setMinEvictableIdleTimeMillis(Integer.valueOf(theValue));
            } else if ("tomcat.jmxEnabled".equals(theKey)) {
                jdbcConnection.setJmxEnabled(Boolean.valueOf(theValue));
            } else if ("tomcat.jdbcInterceptors".equals(theKey)) {
                jdbcConnection.setJdbcInterceptors(theValue);
            }
        }
    }
}
