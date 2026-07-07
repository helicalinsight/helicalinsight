package com.helicalinsight.efw.components;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.HikariProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.ShareRuleXmlUpdateHandler;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Prashansa
 * @author Rajasekhar
 */
public class HikariCpDataSourceProperties {

    private static final Logger logger = LoggerFactory.getLogger(HikariCpDataSourceProperties.class);

    public static void accumulate(@NotNull JsonObject formData, @NotNull Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String theKey = entry.getKey();
            String theValue = entry.getValue();
            if (!formData.has("poolSize")) {
                if ("hikari.maximumPoolSize".equalsIgnoreCase(theKey)) {
                    formData.addProperty("poolSize", theValue);
                }
            }

            if (!formData.has("testQuery")) {
                if ("hikari.testQuery".equals(theKey)) {
                    formData.addProperty("testQuery", theValue);
                }
            }

            if (!formData.has("connectionTimeout")) {
                if ("hikari.connectionTimeout".equals(theKey)) {
                    formData.addProperty("connectionTimeout", theValue);
                }
            }

            if(!formData.has("maxLifeTime")){
                if ("hikari.maxLifetime".equals(theKey)) {
                    formData.addProperty("maxLifetime", theValue);
                }
            }
        }
    }

    
    @NotNull
    public synchronized static String writeHikariDataSourceType(HikariProperties connection, Integer maxId,
                                                                @NotNull JsonObject formData, String mode) {
        Map<String, String> map = ApplicationUtilities.getDefaultsMap();
        String stringId = String.valueOf(maxId);
        accumulate(formData, map);
        if ("create".equalsIgnoreCase(mode)) {
            connection.setVisible("true");
            String stringDefaultMinIdle = map.get("hikari.minimumIdle");
            int intMinimumIdle = Integer.parseInt(stringDefaultMinIdle);
            connection.setMinimumIdle(intMinimumIdle);
            String idleTimeout = map.get("hikari.idleTimeout");
            final String leakDetectThreshold = map.get("hikari.leakDetectThreshold");

            connection.setIdleTimeout(idleTimeout);
            connection.setLeakDetectionThreshold(leakDetectThreshold);

            String maximumPoolSize = formData.get("poolSize").getAsString();
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
            JsonObject shareJson = formData.getAsJsonObject("share");
            JsonObject revoke = null;
            if (formData.has("revoke")) {
                revoke = GsonUtility.optJsonObject(formData, "revoke");
            }
            connection.setShare(ShareRuleXmlUpdateHandler.setShareDataSource(share, shareJson, revoke));
        }


        JsonObject result;
        result = new JsonObject();
        GsonUtility.accumulate(result,"message", "A new Hikari connection is created successfully.");
        GsonUtility.accumulateInt(result,"dataSourceId", maxId);
        return result.toString();
    }

    private static void edit(HikariProperties connection, JsonObject formData, String stringId) {
        connection.setName(formData.get("name").getAsString());
        connection.setConnectionTestQuery(formData.get("testQuery").getAsString());

        connection.setUserName(formData.get("userName").getAsString());
        connection.setPassword(CipherUtils.encrypt(formData.get("password").getAsString()));
        connection.setJdbcUrl(formData.get("jdbcUrl").getAsString());
        connection.setDriverName(formData.get("driverName").getAsString());
        connection.setMaximumPoolSize(Integer.valueOf(formData.get("poolSize").getAsString()));
        connection.setConnectionTimeout(formData.get("connectionTimeout").getAsString());
        connection.setDatabaseDialect(DataSourceUtils.getDatabaseDialect(formData));
        connection.setDatabaseName(GsonUtility.optString(formData, "database"));


        String driverName = formData.get("driverName").getAsString();
        String[] array = driverName.split("\\.");
        connection.setDataSourcePoolId("hikari_" + stringId);

        String vendorName = (array.length >= 2 ? array[1] : "");
        connection.setPoolName("hikari_" + vendorName + "_" + stringId);
    }


}