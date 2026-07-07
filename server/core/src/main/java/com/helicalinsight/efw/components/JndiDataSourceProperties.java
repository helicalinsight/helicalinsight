package com.helicalinsight.efw.components;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.JndiDataSource;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.ShareRuleXmlUpdateHandler;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Prashansa
 * @author Rajasekhar
 */
public class JndiDataSourceProperties {

    private static final Logger logger = LoggerFactory.getLogger(JndiDataSourceProperties.class);


    @NotNull
    public static synchronized String writeStaticDataSourceType(JndiDataSource connection, int maxId,
                                                                @NotNull JsonObject formData, String mode) {

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
            JsonObject shareJson = formData.getAsJsonObject("share");
            JsonObject revoke = null;
            if (formData.has("revoke")) {
                revoke = GsonUtility.optJsonObject(formData, "revoke");
            }
            connection.setShare(ShareRuleXmlUpdateHandler.setShareDataSource(share, shareJson, revoke));
        }

        JsonObject result = new JsonObject();
        result.addProperty("message", "A new Jndi data source is created successfully.");
        result.addProperty("dataSourceId", maxId);
        return result.toString();
    }

    private static void edit(JndiDataSource connection, JsonObject formData) {
        connection.setName(formData.get("name").getAsString());
        connection.setDataSourceProvider("jndi");
        connection.setDatabaseDialect(DataSourceUtils.getDatabaseDialect(formData));
        if (formData.has("driverClassName")) {
            connection.setDriverClassName(formData.get("driverClassName").getAsString());
        } else {
            connection.setDriverClassName(JsonUtils.defaultDriverClassName());
        }
        connection.setDatabaseName(GsonUtility.optString(formData, "database"));
        connection.setLookUpName("java:comp/env/" + formData.get("lookUpName").getAsString().replace("java:comp/env/", ""));

    }


    private static boolean validate(@NotNull JsonObject formData) {
        return (formData.has("lookUpName") && formData.has("name"));
    }
}
