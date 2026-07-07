package com.helicalinsight.efw.components;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.TomcatPoolProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.ShareRuleXmlUpdateHandler;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.jetbrains.annotations.NotNull;
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

    @NotNull
    public synchronized static String writeTomcatJdbcDataSourceType(TomcatPoolProperties connection, int maxId,
                                                                    @NotNull JsonObject formData, String mode) {
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
            connection.setValidationQuery(formData.get("testQuery").getAsString());

            if (logger.isDebugEnabled()) {
                logger.debug("A new Tomcat data source is created successfully.");
            }
            edit(connection, formData, stringId);
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


        return returnMessage("A new Tomcat data source is created successfully.",maxId);
    }

    public static String returnMessage(String message,Integer maxId) {
        JsonObject result;
        result = new JsonObject();
        GsonUtility.accumulate(result,"message", message);
        GsonUtility.accumulateInt(result,"dataSourceId", maxId);
        return result.toString();
    }

    private static void edit(TomcatPoolProperties connection, JsonObject formData, String stringId) {
        connection.setName(formData.get("name").getAsString());
        connection.setDataSourceProvider(formData.get("dataSourceProvider").getAsString());
        connection.setUsername(formData.get("userName").getAsString());
        connection.setPassword(CipherUtils.encrypt(formData.get("password").getAsString()));
        connection.setUrl(formData.get("jdbcUrl").getAsString());
        connection.setDriverClassName(formData.get("driverName").getAsString());
        connection.setMaxActive(Integer.valueOf(formData.get("poolSize").getAsString()));
        connection.setDataSourcePoolId("tomcat_" + stringId);
        connection.setDatabaseDialect(DataSourceUtils.getDatabaseDialect(formData));
        connection.setDatabaseName(GsonUtility.optString(formData,"database"));
    }



    private static void accumulate(@NotNull JsonObject formData, @NotNull TomcatPoolProperties jdbcConnection,
                                   @NotNull Iterator<Map.Entry<String, String>> iterator) {
        int initialSizeVal;
        String minimumNumberOfIdleConnections = null;
        boolean userProvidedPoolSize = false;
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String theKey = entry.getKey();
            String theValue = entry.getValue();

            if (!formData.has("poolSize")) {
                if (theKey.equals("tomcat.maxActive")) {
                    formData.addProperty("poolSize", theValue);
                }
            }

            if (!formData.has("testQuery")) {
                if (theKey.equals("tomcat.testQuery")) {
                    formData.addProperty("testQuery", theValue);
                }
            }

            if ("tomcat.initialSize".equals(theKey)) {
                String initialSize = theValue;
                initialSizeVal = Integer.parseInt(initialSize);

                if (formData.has("poolSize")) {
                    String maxActive = formData.get("poolSize").getAsString();
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
