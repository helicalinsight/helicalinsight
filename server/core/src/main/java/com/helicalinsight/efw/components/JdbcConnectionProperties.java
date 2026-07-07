package com.helicalinsight.efw.components;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.JdbcConnection;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
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
public class JdbcConnectionProperties {

    private static final Logger logger = LoggerFactory.getLogger(JdbcConnectionProperties.class);

    @NotNull
    public static synchronized String writeNonPooledType(JdbcConnection connection, Integer maxId,
                                                         @NotNull JsonObject formData, String mode) {
        if ("create".equalsIgnoreCase(mode)) {
            connection.setVisible("true");
            connection.setId(maxId);
            connection.setType(GlobalJdbcType.NON_POOLED);
            connection.setBaseType(GlobalJdbcType.TYPE);
            Security security = SecurityUtils.securityObject();
            connection.setSecurity(security);
            if (logger.isDebugEnabled()) {
                logger.debug("A new Jdbc connection is created successfully.");
            }

            edit(connection, formData);
        } else if ("edit".equalsIgnoreCase(mode)) {
            ControllerUtils.validate(formData);
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

        JsonObject result;
        result = new JsonObject();
        result.addProperty("message", "A new Jdbc connection is created successfully.");
        result.addProperty("dataSourceId", maxId);
        return result.toString();
    }

    private static void edit(JdbcConnection connection, JsonObject formData) {
        connection.setName(formData.get("name").getAsString());
        connection.setDataSourceProvider(formData.get("dataSourceProvider").getAsString());
        connection.setUserName(formData.get("userName").getAsString());
        connection.setPassword(CipherUtils.encrypt(formData.get("password").getAsString()));
        connection.setJdbcUrl(formData.get("jdbcUrl").getAsString());
        connection.setDriverName(formData.get("driverName").getAsString());
        connection.setDatabaseDialect(DataSourceUtils.getDatabaseDialect(formData));
        connection.setDatabaseName(GsonUtility.optString(formData, "database"));
    }


}