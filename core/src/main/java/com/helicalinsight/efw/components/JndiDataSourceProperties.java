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
import com.helicalinsight.datasource.managed.jaxb.JndiDataSource;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.ShareRuleXmlUpdateHandler;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Prashansa
 * @author Rajasekhar
 */
public class JndiDataSourceProperties {

    private static final Logger logger = LoggerFactory.getLogger(JndiDataSourceProperties.class);


    public static synchronized String writeStaticDataSourceType(JndiDataSource connection, int maxId,
                                                                JSONObject formData, String mode) {

        if ("create".equalsIgnoreCase(mode)) {
            connection.setVisible("true");
            connection.setId(maxId);
            connection.setType(GlobalJdbcType.STATIC_DATASOURCE);
            connection.setBaseType(GlobalJdbcType.TYPE);
            Security security = SecurityUtils.securityObject();
            connection.setSecurity(security);
            if (logger.isDebugEnabled()) {
                logger.debug("A new Jndi data source is created successfully.");
            }
            edit(connection, formData);
        } else if ("edit".equalsIgnoreCase(mode)) {
            if (!validate(formData)) {
                throw new IncompleteFormDataException("The Json form data lacks required parameters " + "lookUpName " +
                        "and " +
                        "name");
            }

            edit(connection, formData);
        } else if ("share".equalsIgnoreCase(mode)) {
            Security.Share share = connection.getShare();
            JSONObject shareJson = formData.getJSONObject("share");
            JSONObject revoke = null;
            if (formData.has("revoke")) {
                revoke = formData.optJSONObject("revoke");
            }
            connection.setShare(ShareRuleXmlUpdateHandler.setShareDataSource(share, shareJson, revoke));
        }

        JSONObject result = new JSONObject();
        result.accumulate("message", "A new Jndi data source is created successfully.");
        return result.toString();
    }

    private static void edit(JndiDataSource connection, JSONObject formData) {
        connection.setName(formData.getString("name"));
        connection.setDataSourceProvider("jndi");
        if (formData.has("driverClassName")) {
            connection.setDriverClassName(formData.getString("driverClassName"));
        } else {
            connection.setDriverClassName(JsonUtils.defaultDriverClassName());
        }
        connection.setLookUpName("java:comp/env/" + formData.getString("lookUpName").replace("java:comp/env/", ""));

    }


    private static boolean validate(JSONObject formData) {
        return (formData.containsKey("lookUpName") && formData.containsKey("name"));
    }
}
