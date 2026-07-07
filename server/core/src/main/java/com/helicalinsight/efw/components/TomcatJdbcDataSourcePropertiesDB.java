package com.helicalinsight.efw.components;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.DsOperation;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.model.DSTypeTomcat;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Prashansa
 * @author Rajasekhar
 * @author Somen
 */
@Component("tomcatDsManager")
public class TomcatJdbcDataSourcePropertiesDB implements DsOperation {

    private static final Logger logger = LoggerFactory.getLogger(TomcatJdbcDataSourcePropertiesDB.class);


    @Autowired
    private GlobalConnectionService globalConnectionService;


    @NotNull
    public String writeDataSource(@NotNull JsonObject formData, String mode) {


        Security security = SecurityUtils.securityObject();
        String createdBy = security.getCreatedBy();
        int globalId = 0;

        //acumulate
        if ("create".equalsIgnoreCase(mode)) {
            globalId = addDataSource(formData, createdBy);
        } else if ("edit".equalsIgnoreCase(mode)) {
            Integer id = editDataSource(formData);
            globalId = id;

        }


        return returnMessage("A new Tomcat data source is created successfully.", globalId);
    }

    private Integer editDataSource(JsonObject formData) {
        Integer id = formData.get("id").getAsInt();
        GlobalConnections globalConnection = globalConnectionService.findGlobalConnectionById(id);

        globalConnection.setName(formData.get("name").getAsString());
        globalConnection.setLastUpdatedTime(new Date());

        //Tomcat Connection
        DSTypeTomcat connection = globalConnectionService.getTomcatConnectionById(id);
        connection.setVisible(true);
        connection.setDataSourceProvider(formData.get("dataSourceProvider").getAsString());
        connection.setUsername(formData.get("userName").getAsString());
        connection.setPassword(CipherUtils.encrypt(formData.get("password").getAsString()));
        connection.setUrl(formData.get("jdbcUrl").getAsString());
        connection.setDriverClassName(formData.get("driverName").getAsString());
        connection.setDataSourcePoolId("tomcat_" + id);
        connection.setDatabaseDialect(DataSourceUtils.getDatabaseDialect(formData));
        connection.setDatabaseName(GsonUtility.optString(formData,"database"));
        connection.setLastUpdatedTime(new Date());
        connection.setGlobalConnections(globalConnection);
        Map<String, String> map = ApplicationUtilities.getDefaultsMap();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();

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
                connection.setInitialSize(initialSizeVal);
            } else if ("tomcat.forceAlternateUsername".equals(theKey)) {
                connection.setForceAlternateUsername(Boolean.valueOf(theValue));
            } else if ("tomcat.testWhileIdle".equals(theKey)) {
                connection.setTestWhileIdle(Boolean.valueOf(theValue));
            } else if ("tomcat.testOnBorrow".equals(theKey)) {
                connection.setTestOnBorrow(Boolean.valueOf(theValue));
            } else if ("tomcat.testOnReturn".equals(theKey)) {
                connection.setTestOnReturn(Boolean.valueOf(theValue));
            } else if ("tomcat.validationInterval".equals(theKey)) {
                connection.setValidationInterval(Integer.valueOf(theValue));
            } else if ("tomcat.timeBetweenEvictionRunsMillis".equals(theKey)) {
                connection.setTimeBetweenEvictionRunsMillis(Integer.valueOf(theValue));
            } else if ("tomcat.minIdle".equals(theKey)) {
                //Let the the minIdle be equal to initialSize. If the user has provided
                //pool size, then use initialSize is one less than maxActive. The same is used.
                //Else, the default value from the properties file is used.
                if (userProvidedPoolSize) {
                    connection.setMinIdle(Integer.valueOf(minimumNumberOfIdleConnections));
                } else {
                    connection.setMinIdle(Integer.valueOf(theValue));
                }
            } else if ("tomcat.maxWait".equals(theKey)) {
                connection.setMaxWait(Integer.valueOf(theValue));
            } else if ("tomcat.removeAbandonedTimeout".equals(theKey)) {
                connection.setRemoveAbandonedTimeout(Integer.valueOf(theValue));
            } else if ("tomcat.removeAbandoned".equals(theKey)) {
                connection.setRemoveAbandoned(Boolean.valueOf(theValue));
            } else if ("tomcat.logAbandoned".equals(theKey)) {
                connection.setLogAbandoned(Boolean.valueOf(theValue));
            } else if ("tomcat.minEvictableIdleTimeMillis".equals(theKey)) {
                connection.setMinEvictableIdleTimeMillis(Integer.valueOf(theValue));
            } else if ("tomcat.jmxEnabled".equals(theKey)) {
                connection.setJmxEnabled(Boolean.valueOf(theValue));
            } else if ("tomcat.jdbcInterceptors".equals(theKey)) {
                connection.setJdbcInterceptors(theValue);
            }else if ("tomcat.maxActive".equals(theKey)){
                connection.setMaxActive(Integer.valueOf(theValue));
            }

        }
        connection.setValidationQuery(formData.get("testQuery").getAsString());
        Boolean aBoolean = globalConnectionService.editDataSourceDetails(globalConnection, connection);
        if(!aBoolean){
            logger.error("Something went wrong while updating the Datasource");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("A new Tomcat data source is updated successfully.");
        }
        return id;
    }

    private int addDataSource(JsonObject formData, String createdBy) {
        int globalId;GlobalConnections globalConnection = new GlobalConnections();
        globalConnection.setType(GlobalJdbcType.DYNAMIC_DATASOURCE);
        globalConnection.setBaseType(GlobalJdbcType.TYPE);
        globalConnection.setName(formData.get("name").getAsString());
        globalConnection.setCreatedBy(createdBy);
        globalConnection.setVendor(GsonUtility.optString(formData,"vendorName"));
        globalConnection.setCreatedDate(new Date());
        globalConnection.setLastUpdatedTime(new Date());
        String dsType = JsonUtils.getDSType(DSTypeTomcat.class.getName());
        globalConnection.setDsType(dsType);
        globalConnection.setIsMigrated(false);



        DSTypeTomcat connection = new DSTypeTomcat();
        connection.setVisible(true);
        connection.setDataSourceProvider(formData.get("dataSourceProvider").getAsString());
        connection.setUsername(formData.get("userName").getAsString());
        connection.setPassword(CipherUtils.encrypt(formData.get("password").getAsString()));
        connection.setUrl(formData.get("jdbcUrl").getAsString());
        connection.setDriverClassName(formData.get("driverName").getAsString());
        connection.setDatabaseDialect(DataSourceUtils.getDatabaseDialect(formData));
        connection.setDatabaseName(GsonUtility.optString(formData, "database"));
        connection.setLastUpdatedTime(new Date());
        connection.setGlobalConnections(globalConnection);
        Map<String, String> map = ApplicationUtilities.getDefaultsMap();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();

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
                connection.setInitialSize(initialSizeVal);
            } else if ("tomcat.forceAlternateUsername".equals(theKey)) {
                connection.setForceAlternateUsername(Boolean.valueOf(theValue));
            } else if ("tomcat.testWhileIdle".equals(theKey)) {
                connection.setTestWhileIdle(Boolean.valueOf(theValue));
            } else if ("tomcat.testOnBorrow".equals(theKey)) {
                connection.setTestOnBorrow(Boolean.valueOf(theValue));
            } else if ("tomcat.testOnReturn".equals(theKey)) {
                connection.setTestOnReturn(Boolean.valueOf(theValue));
            } else if ("tomcat.validationInterval".equals(theKey)) {
                connection.setValidationInterval(Integer.valueOf(theValue));
            } else if ("tomcat.timeBetweenEvictionRunsMillis".equals(theKey)) {
                connection.setTimeBetweenEvictionRunsMillis(Integer.valueOf(theValue));
            } else if ("tomcat.minIdle".equals(theKey)) {
                //Let the the minIdle be equal to initialSize. If the user has provided
                //pool size, then use initialSize is one less than maxActive. The same is used.
                //Else, the default value from the properties file is used.
                if (userProvidedPoolSize) {
                    connection.setMinIdle(Integer.valueOf(minimumNumberOfIdleConnections));
                } else {
                    connection.setMinIdle(Integer.valueOf(theValue));
                }
            } else if ("tomcat.maxWait".equals(theKey)) {
                connection.setMaxWait(Integer.valueOf(theValue));
            } else if ("tomcat.removeAbandonedTimeout".equals(theKey)) {
                connection.setRemoveAbandonedTimeout(Integer.valueOf(theValue));
            } else if ("tomcat.removeAbandoned".equals(theKey)) {
                connection.setRemoveAbandoned(Boolean.valueOf(theValue));
            } else if ("tomcat.logAbandoned".equals(theKey)) {
                connection.setLogAbandoned(Boolean.valueOf(theValue));
            } else if ("tomcat.minEvictableIdleTimeMillis".equals(theKey)) {
                connection.setMinEvictableIdleTimeMillis(Integer.valueOf(theValue));
            } else if ("tomcat.jmxEnabled".equals(theKey)) {
                connection.setJmxEnabled(Boolean.valueOf(theValue));
            } else if ("tomcat.jdbcInterceptors".equals(theKey)) {
                connection.setJdbcInterceptors(theValue);
            }else if ("tomcat.maxActive".equals(theKey)){
                connection.setMaxActive(Integer.valueOf(theValue));
            }
        }
        connection.setValidationQuery(formData.get("testQuery").getAsString());
        Boolean aBoolean = globalConnectionService.addDataSourceDetails(globalConnection, connection);
        if(!aBoolean){
            logger.error("Something went wrong while saving the Datasource");
        }


        if (logger.isDebugEnabled()) {
            logger.debug("A new Tomcat data source is created successfully.");
        }
        return globalConnection.getGlobalId();
    }

    public static String returnMessage(String message, Integer maxId) {
        JsonObject result;
        result = new JsonObject();
        result.addProperty("message", message);
        result.addProperty("dataSourceId", maxId);
        return result.toString();
    }


}