package com.helicalinsight.efw;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.model.*;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Helical on 6/2/2021.
 */


public class DatasourceOperations {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DatasourceOperations.class);

    private GlobalConnectionService globalConnectionService;

    public void migrateDataSourceToDatabase() throws Exception {
        globalConnectionService = ApplicationContextAccessor.getBean(GlobalConnectionService.class);
        readDataSource();
    }

    public void readDataSource() {
        JsonObject globalJson = JsonUtils.newGetGlobalConnectionsJson();
        List<String> gcXmlKeys = JsonUtils.getKeys(globalJson);
        for (String key : gcXmlKeys) {
            Object theKey = globalJson.get(key);
            if (theKey instanceof JsonArray) {
                JsonArray jsonArray = globalJson.getAsJsonArray(key);
                for (int counter = 0; counter < jsonArray.size(); counter++) {
                    JsonObject aDataSource = jsonArray.get(counter).getAsJsonObject();
                    addADataSource(aDataSource);
                }
            } else if (theKey instanceof JsonObject) {
            	JsonObject aDataSource = globalJson.getAsJsonObject(key);
                addADataSource(aDataSource);
            }
        }
    }

    //TODO optimization
    public void addADataSource(JsonObject jsonObject) {
        try {
            String id = GsonUtility.optString(jsonObject, "id");
            if (!id.equals("0") && !jsonObject.entrySet().isEmpty()) {
                Boolean isMigrated = false;
                GlobalConnections existingGlobalConnection = globalConnectionService.findGlobalConnectionById(Integer.valueOf(id));
                if (existingGlobalConnection != null) {
                    isMigrated = existingGlobalConnection.getIsMigrated();
                }
                if (!isMigrated)
                    prepareGlobalConnectionEntityFromJson(jsonObject, globalConnectionService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private JsonObject dataSourceXmlAsJson() {
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> hashMap = propertiesFileReader.read("project.properties");
        String globalConnectionsPath = hashMap.get("globalConnectionsPath");
        File file = new File(globalConnectionsPath);
        if (file.exists()) {
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            return processor.getJsonObject(globalConnectionsPath, false);
        }
        return null;
    }

    public void prepareGlobalConnectionEntityFromJson(JsonObject json, GlobalConnectionService globalConnectionService) throws Exception {
        logger.debug("eachGlobalConnectionJson :" + json);
        String createdBy = AuthenticationUtils.getUserId(json);
        Map<String, Object> stringObjectMap = prepareGlobalConnectionEntity(json, createdBy, globalConnectionService);


    }

    public Map<String, Object> prepareGlobalConnectionEntity(JsonObject gcJsonItem, String createdBy, GlobalConnectionService globalConnectionService) throws Exception {
        Map<String, Object> dataSourcesMap = new HashMap<String, Object>();
        String dsType = "";
        String dataSourceProvider = GsonUtility.optString(gcJsonItem, "dataSourceProvider");
        GlobalConnections globalConnections = new GlobalConnections();
        globalConnections.setGlobalId(GsonUtility.optInt(gcJsonItem,"id"));
        globalConnections.setName(GsonUtility.optString(gcJsonItem, "name"));
        globalConnections.setType(GsonUtility.optString(gcJsonItem, "type"));
        globalConnections.setBaseType(GsonUtility.optString(gcJsonItem, "baseType"));
        globalConnections.setCreatedDate(new Date());
        globalConnections.setLastUpdatedTime(new Date());
        globalConnections.setCreatedBy(createdBy);
        globalConnections.setVendor(GsonUtility.optString(gcJsonItem,"@vendor"));
        globalConnections.setIsMigrated(true);
        if (dataSourceProvider.equalsIgnoreCase("tomcat")) {
            dsType = JsonUtils.getDSType(DSTypeTomcat.class.getName());
            globalConnections.setDsType(dsType);
        } else if (dataSourceProvider.equalsIgnoreCase("none")) {
            dsType = JsonUtils.getDSType(DSTypePlainJDBC.class.getName());
            globalConnections.setDsType(dsType);
        } else if (dataSourceProvider.equalsIgnoreCase("jndi")) {
            dsType = JsonUtils.getDSType(DSTypeJndi.class.getName());
            globalConnections.setDsType(dsType);
        } else if (dataSourceProvider.equalsIgnoreCase("hikari")) {
            dsType = JsonUtils.getDSType(DSTypeHikari.class.getName());
            globalConnections.setDsType(dsType);
        } else if (dataSourceProvider.equalsIgnoreCase("nosql")) {
            dsType = JsonUtils.getDSType(DSTypeNoSQL.class.getName());
            globalConnections.setDsType(dsType);
        }
        //TODO need to check for entry , if exists remove/update or else add it in database
        try {
            globalConnectionService.addGlobalConnections(globalConnections);
        } catch (Exception e) {
            e.printStackTrace();
        }


        GlobalConnectionSecurity globalConnectionSecurity = new GlobalConnectionSecurity();
        GlobalConnections globalConnectionById = globalConnectionService.findGlobalConnectionById(GsonUtility.optInt(gcJsonItem,"id"));
        if (null != globalConnectionById) {
            globalConnectionSecurity.setGlobalConnections(globalConnectionById);
        }

        //GLobalConnectionSecurity
        //TODO optimization
        if (StringUtils.isNotEmpty(createdBy)) {
            if (gcJsonItem.has("share")) {
                JsonObject share = gcJsonItem.getAsJsonObject("share");
                JsonObject security = gcJsonItem.getAsJsonObject("security");
                if (share.has("roles")) {
                    Object roles = share.get("roles");
                    JsonObject userObject = (JsonObject) roles;
                    Object roleObj = userObject.get("role");
                    if (roleObj instanceof JsonArray) {
                        JsonArray role = (JsonArray) roleObj;
                        for (int i = 0; i < role.size(); i++) {
                            JsonObject usersObject = (JsonObject) role.get(i);
                            shareRoles(usersObject, GsonUtility.optInt(gcJsonItem,"id"), createdBy);
                        }
                    } else {
                        shareRoles((JsonObject) roleObj, GsonUtility.optInt(gcJsonItem,"id"), createdBy);
                    }

                }
                if (share.has("users")) {
                    Object user1 = share.get("users");
                    JsonObject userObject = (JsonObject) user1;
                    Object user = userObject.get("user");
                    if (user instanceof JsonArray) {
                        JsonArray users = (JsonArray) user;
                        for (int i = 0; i < users.size(); i++) {
                            JsonObject usersObject = (JsonObject) users.get(i);
                            shareUsers(usersObject, GsonUtility.optInt(gcJsonItem, "id"), createdBy);
                        }
                    } else {
                        shareUsers((JsonObject) user, GsonUtility.optInt(gcJsonItem,"id"), createdBy);
                    }
                }

                if (share.has("organizations")) {
                    Object organizations = share.get("organizations");
                    JsonObject userObject = (JsonObject) organizations;
                    Object organization = userObject.get("organization");
                    if (organization instanceof JsonArray) {
                        JsonArray org = (JsonArray) organization;
                        for (int i = 0; i < org.size(); i++) {
                            JsonObject usersObject = (JsonObject) org.get(i);
                            shareOrg(usersObject, GsonUtility.optInt(gcJsonItem, "id"), createdBy);
                        }
                    } else {
                        shareOrg((JsonObject) organization, GsonUtility.optInt(gcJsonItem, "id"), createdBy);
                    }
                }
            }
        }

        switch (dataSourceProvider.toLowerCase()) {
            case "tomcat":
                dsType = JsonUtils.getDSType(DSTypeTomcat.class.getName());
                globalConnections.setDsType(dsType);
                DSTypeTomcat tomcatConnectionById = globalConnectionService.getTomcatConnectionById(GsonUtility.optInt(gcJsonItem, "id"));
                if (null == tomcatConnectionById) {
                    DSTypeTomcat dsTypeTomcat = addTomcatData(gcJsonItem);
                    if (null != globalConnectionById) {
                        dsTypeTomcat.setGlobalConnections(globalConnectionById);
                    }
                    try {
                        globalConnectionService.addTomcatConnections(dsTypeTomcat);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case "hikari":
                dsType = JsonUtils.getDSType(DSTypeHikari.class.getName());
                globalConnections.setDsType(dsType);
                DSTypeHikari dsObject = globalConnectionService.getHikariConnectionById(GsonUtility.optInt(gcJsonItem, "id"));
                if (null == dsObject) {
                    DSTypeHikari dsTypeObject = addDSTypeHikari(gcJsonItem);
                    if (null != globalConnectionById) {
                        dsTypeObject.setGlobalConnections(globalConnectionById);
                    }
                    try {
                        globalConnectionService.addHikariConnections(dsTypeObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case "nosql":
                dsType = JsonUtils.getDSType(DSTypeNoSQL.class.getName());
                globalConnections.setDsType(dsType);
                DSTypeNoSQL dsTypeNoSQL = globalConnectionService.getNoSQLConnectionById(GsonUtility.optInt(gcJsonItem, "id"));
                if (null == dsTypeNoSQL) {
                    DSTypeNoSQL dsTypeObject = addNoSqlData(gcJsonItem);
                    if (null != globalConnectionById) {
                        dsTypeObject.setGlobalConnections(globalConnectionById);
                    }
                    try {
                        globalConnectionService.addNoSqlConnections(dsTypeObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case "none":
                dsType = JsonUtils.getDSType(DSTypePlainJDBC.class.getName());
                globalConnections.setDsType(dsType);
                DSTypePlainJDBC dsTypePlainJDBC = globalConnectionService.getPlainJDBCConnectionById(GsonUtility.optInt(gcJsonItem, "id"));
                if (null == dsTypePlainJDBC) {
                    DSTypePlainJDBC dsTypeObject = addDsTypePlainJDBC(gcJsonItem);
                    if (null != globalConnectionById) {
                        dsTypeObject.setGlobalConnections(globalConnectionById);
                    }
                    try {
                        globalConnectionService.addPlainJdbcConnections(dsTypeObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "jndi":
                dsType = JsonUtils.getDSType(DSTypeJndi.class.getName());
                globalConnections.setDsType(dsType);
                DSTypeJndi dsTypeJndi = globalConnectionService.getJndiConnectionById(GsonUtility.optInt(gcJsonItem, "id"));
                if (null == dsTypeJndi) {
                    DSTypeJndi dsTypeObject = addDSTypeJndi(gcJsonItem);
                    if (null != globalConnectionById) {
                        dsTypeObject.setGlobalConnections(globalConnectionById);
                    }
                    try {
                        globalConnectionService.addJndiConnections(dsTypeObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                throw new Exception("Invalid DataSource Found");
        }
        dataSourcesMap.put("globalConnections", globalConnections);
        //TODO need to work on security
        return dataSourcesMap;
    }


    private DSTypeJndi addDSTypeJndi(JsonObject gcJsonItem) {
        DSTypeJndi dsObject = new DSTypeJndi();
        try {
            dsObject.setId(GsonUtility.optInt(gcJsonItem,"id"));
            dsObject.setVisible(GsonUtility.optBoolean(gcJsonItem,"visible"));
            dsObject.setDataSourceProvider(GsonUtility.optString(gcJsonItem,"dataSourceProvider"));
            dsObject.setDriverClassName(GsonUtility.optString(gcJsonItem,"driverClassName"));
            dsObject.setDatabaseName(GsonUtility.optString(gcJsonItem,"databaseName"));
            if (gcJsonItem.has("lookUpName")) {
                dsObject.setLookUpName(GsonUtility.optString(gcJsonItem,"lookUpName"));
            }
            if (gcJsonItem.has("databaseDialect")) {
                String databaseDialect = GsonUtility.optStringValue(gcJsonItem,"databaseDialect", "");
                dsObject.setDatabaseDialect(databaseDialect.equals("[]") ? null : databaseDialect);

            }
            dsObject.setLastUpdatedTime(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsObject;
    }


    private DSTypeHikari addDSTypeHikari(JsonObject gcJsonItem) {
        DSTypeHikari dsObject = new DSTypeHikari();
        try {
            dsObject.setId(GsonUtility.optInt(gcJsonItem,"id"));
            dsObject.setVisible(GsonUtility.optBoolean(gcJsonItem,"visible"));
            dsObject.setDataSourcePoolId(GsonUtility.optString(gcJsonItem,"dataSourcePoolId"));
            dsObject.setDataSourceProvider(GsonUtility.optString(gcJsonItem,"dataSourceProvider"));
            dsObject.setUserName(GsonUtility.optString(gcJsonItem,"userName"));
            if(gcJsonItem.has("password") && GsonUtility.optString(gcJsonItem,"password")!=null)
            	dsObject.setPassword(CipherUtils.encrypt(GsonUtility.optString(gcJsonItem,"password")));
            dsObject.setDriverName(GsonUtility.optString(gcJsonItem,"driverName"));
            dsObject.setJdbcUrl(GsonUtility.optString(gcJsonItem,"jdbcUrl"));
            dsObject.setValidationQuery(GsonUtility.optString(gcJsonItem,"validationQuery"));
            dsObject.setIdleTimeout(GsonUtility.optString(gcJsonItem,"idleTimeout"));
            dsObject.setMinimumIdle(GsonUtility.optInt(gcJsonItem,"minimumIdle"));
            dsObject.setMaxLifetime(GsonUtility.optInt(gcJsonItem,"maxLifetime"));
            dsObject.setMaximumPoolSize(GsonUtility.optInt(gcJsonItem,"maximumPoolSize"));
            dsObject.setPoolName(GsonUtility.optString(gcJsonItem,"poolName"));
            dsObject.setConnectionTimeout(GsonUtility.optString(gcJsonItem,"connectionTimeout"));
            dsObject.setDatabaseName(GsonUtility.optString(gcJsonItem,"databaseName"));
            String databaseDialect = GsonUtility.optStringValue(gcJsonItem,"databaseDialect", "");
            dsObject.setDatabaseDialect(databaseDialect.equals("[]") ? null : databaseDialect);
            dsObject.setLastUpdatedTime(new Date());
            dsObject.setValidationQuery(GsonUtility.optString(gcJsonItem,"connectionTestQuery"));
            dsObject.setLeakDetectionThreshold(GsonUtility.optString(gcJsonItem,"leakDetectionThreshold"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsObject;
    }


    private DSTypePlainJDBC addDsTypePlainJDBC(JsonObject gcJsonItem) {
        DSTypePlainJDBC dsTypePlainJDBC = new DSTypePlainJDBC();
        try {
            dsTypePlainJDBC.setId(GsonUtility.optInt(gcJsonItem, "id"));
            dsTypePlainJDBC.setVisible(GsonUtility.optBoolean(gcJsonItem, "visible"));
            dsTypePlainJDBC.setDataSourceProvider(GsonUtility.optString(gcJsonItem,"dataSourceProvider"));
            dsTypePlainJDBC.setUserName(GsonUtility.optString(gcJsonItem,"userName"));
            if(gcJsonItem.has("password") && GsonUtility.optString(gcJsonItem,"password")!=null)
            	dsTypePlainJDBC.setPassword(CipherUtils.encrypt(GsonUtility.optString(gcJsonItem,"password")));
            if (gcJsonItem.has("driverClassName")) {
                dsTypePlainJDBC.setDriverName(GsonUtility.optString(gcJsonItem,"driverClassName"));
            } else {
                dsTypePlainJDBC.setDriverName(GsonUtility.optString(gcJsonItem,"driverName"));
            }
            dsTypePlainJDBC.setJdbcUrl(GsonUtility.optString(gcJsonItem,"jdbcUrl"));
            dsTypePlainJDBC.setDatabaseName(GsonUtility.optString(gcJsonItem,"databaseName"));

            if (gcJsonItem.has("databaseDialect")) {
                String databaseDialect = GsonUtility.optStringValue(gcJsonItem,"databaseDialect", "");
                dsTypePlainJDBC.setDatabaseDialect(databaseDialect.equals("[]") ? null : databaseDialect);
            }
            dsTypePlainJDBC.setLastUpdatedTime(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsTypePlainJDBC;
    }


    private DSTypeTomcat addTomcatData(JsonObject gcJsonItem) {
        DSTypeTomcat dsTypeTomcat = new DSTypeTomcat();
        try {
            dsTypeTomcat.setId(GsonUtility.optInt(gcJsonItem, "id"));
            dsTypeTomcat.setVisible(GsonUtility.optBoolean(gcJsonItem, "visible"));
            dsTypeTomcat.setDataSourcePoolId(GsonUtility.optString(gcJsonItem, "dataSourcePoolId"));
            dsTypeTomcat.setDataSourceProvider(GsonUtility.optString(gcJsonItem,"dataSourceProvider"));
            String forceAlternateUsername = GsonUtility.optString(gcJsonItem,"forceAlternateUsername");
            dsTypeTomcat.setForceAlternateUsername(Boolean.valueOf(forceAlternateUsername));
            dsTypeTomcat.setUsername(GsonUtility.optString(gcJsonItem,"username"));
            if(gcJsonItem.has("password") && GsonUtility.optString(gcJsonItem,"password")!=null)
            	dsTypeTomcat.setPassword(CipherUtils.encrypt(GsonUtility.optString(gcJsonItem,"password")));
            dsTypeTomcat.setDriverClassName(GsonUtility.optString(gcJsonItem,"driverClassName"));
            dsTypeTomcat.setUrl(GsonUtility.optString(gcJsonItem,"url"));
            String testWhileIdle = GsonUtility.optString(gcJsonItem,"testWhileIdle");
            dsTypeTomcat.setTestWhileIdle(Boolean.valueOf(testWhileIdle));
            String testOnBorrow = GsonUtility.optString(gcJsonItem,"testOnBorrow");
            dsTypeTomcat.setTestOnBorrow(Boolean.valueOf(testOnBorrow));
            String testOnReturn = GsonUtility.optString(gcJsonItem,"testOnReturn");
            dsTypeTomcat.setTestOnReturn(Boolean.valueOf(testOnReturn));
            dsTypeTomcat.setValidationQuery(GsonUtility.optString(gcJsonItem,"validationQuery"));
            dsTypeTomcat.setValidationInterval(GsonUtility.optInt(gcJsonItem,"validationInterval"));
            dsTypeTomcat.setTimeBetweenEvictionRunsMillis(GsonUtility.optInt(gcJsonItem,"timeBetweenEvictionRunsMillis"));
            dsTypeTomcat.setMaxActive(GsonUtility.optInt(gcJsonItem,"maxActive"));
            dsTypeTomcat.setMinIdle(GsonUtility.optInt(gcJsonItem,"minIdle"));
            dsTypeTomcat.setMaxWait(GsonUtility.optInt(gcJsonItem,"maxWait"));
            dsTypeTomcat.setInitialSize(GsonUtility.optInt(gcJsonItem,"initialSize"));
            dsTypeTomcat.setRemoveAbandonedTimeout(GsonUtility.optInt(gcJsonItem,"removeAbandonedTimeout"));
            dsTypeTomcat.setRemoveAbandoned(GsonUtility.optBoolean(gcJsonItem, "removeAbandoned"));
            dsTypeTomcat.setLogAbandoned(GsonUtility.optBoolean(gcJsonItem, "logAbandoned"));
            dsTypeTomcat.setMinEvictableIdleTimeMillis(GsonUtility.optInt(gcJsonItem,"minEvictableIdleTimeMillis"));
            dsTypeTomcat.setJmxEnabled(GsonUtility.optBoolean(gcJsonItem, "jmxEnabled"));
            dsTypeTomcat.setJdbcInterceptors(GsonUtility.optString(gcJsonItem,"jdbcInterceptors"));
            dsTypeTomcat.setDatabaseName(GsonUtility.optString(gcJsonItem,"databaseName"));
            String databaseDialect = GsonUtility.optStringValue(gcJsonItem,"databaseDialect", "");

            dsTypeTomcat.setDatabaseDialect(databaseDialect.equals("[]") ? null : databaseDialect);
            dsTypeTomcat.setLastUpdatedTime(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsTypeTomcat;
    }


    private DSTypeNoSQL addNoSqlData(JsonObject gcJsonItem) {
        DSTypeNoSQL dsTypeNoSQL = new DSTypeNoSQL();
        try {
            dsTypeNoSQL.setId(GsonUtility.optInt(gcJsonItem,"id"));
            dsTypeNoSQL.setVisible(GsonUtility.optBoolean(gcJsonItem,"visible"));
            dsTypeNoSQL.setDataSourcePoolId(GsonUtility.optString(gcJsonItem,"dataSourcePoolId"));
            dsTypeNoSQL.setDataSourceProvider(GsonUtility.optString(gcJsonItem,"dataSourceProvider"));
            String forceAlternateUsername = GsonUtility.optString(gcJsonItem,"forceAlternateUsername");
            dsTypeNoSQL.setForceAlternateUsername(Boolean.valueOf(forceAlternateUsername));
            dsTypeNoSQL.setUsername(GsonUtility.optString(gcJsonItem,"username"));
            if(gcJsonItem.has("password") && GsonUtility.optString(gcJsonItem,"password")!=null)
            	dsTypeNoSQL.setPassword(CipherUtils.encrypt(GsonUtility.optString(gcJsonItem,"password")));
            dsTypeNoSQL.setDriverClassName(GsonUtility.optString(gcJsonItem,"driverClassName"));
            dsTypeNoSQL.setUrl(GsonUtility.optString(gcJsonItem,"url"));
            dsTypeNoSQL.setTestWhileIdle(GsonUtility.optBoolean(gcJsonItem,"testWhileIdle"));
            dsTypeNoSQL.setTestOnBorrow(GsonUtility.optBoolean(gcJsonItem,"testOnBorrow"));
            dsTypeNoSQL.setTestOnReturn(GsonUtility.optBoolean(gcJsonItem,"testOnReturn"));
            dsTypeNoSQL.setValidationQuery(GsonUtility.optString(gcJsonItem,"validationQuery"));
            dsTypeNoSQL.setValidationInterval(GsonUtility.optInt(gcJsonItem,"validationInterval"));
            dsTypeNoSQL.setTimeBetweenEvictionRunsMillis(GsonUtility.optInt(gcJsonItem,"timeBetweenEvictionRunsMillis"));
            dsTypeNoSQL.setMaxActive(GsonUtility.optInt(gcJsonItem,"maxActive"));
            dsTypeNoSQL.setMinIdle(GsonUtility.optInt(gcJsonItem,"minIdle"));
            dsTypeNoSQL.setMaxWait(GsonUtility.optInt(gcJsonItem,"maxWait"));
            dsTypeNoSQL.setInitialSize(GsonUtility.optInt(gcJsonItem,"initialSize"));
            dsTypeNoSQL.setRemoveAbandonedTimeout(GsonUtility.optInt(gcJsonItem,"removeAbandonedTimeout"));
            dsTypeNoSQL.setRemoveAbandoned(GsonUtility.optBoolean(gcJsonItem,"removeAbandoned"));
            dsTypeNoSQL.setLogAbandoned(GsonUtility.optBoolean(gcJsonItem,"logAbandoned"));
            dsTypeNoSQL.setMinEvictableIdleTimeMillis(GsonUtility.optInt(gcJsonItem,"minEvictableIdleTimeMillis"));
            dsTypeNoSQL.setJmxEnabled(GsonUtility.optBoolean(gcJsonItem,"jmxEnabled"));
            dsTypeNoSQL.setJdbcInterceptors(GsonUtility.optString(gcJsonItem,"jdbcInterceptors"));
            dsTypeNoSQL.setDatabaseName(GsonUtility.optString(gcJsonItem,"databaseName"));
            dsTypeNoSQL.setSubType(GsonUtility.optString(gcJsonItem,"subType"));
            if (gcJsonItem.has("databaseDialect")) {
                String databaseDialect = GsonUtility.optStringValue(gcJsonItem,"databaseDialect", "");
                dsTypeNoSQL.setDatabaseDialect(databaseDialect.equals("[]") ? null : databaseDialect);
            }
            if (gcJsonItem.has("collection")) {
                dsTypeNoSQL.setCollection(GsonUtility.optString(gcJsonItem,"collection"));
            }
            dsTypeNoSQL.setHiveReferenceId(GsonUtility.optInt(gcJsonItem,"hiveReferenceId"));
            dsTypeNoSQL.setLastUpdatedTime(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsTypeNoSQL;
    }

    public void shareUsers(JsonObject usersObject, int globalId, String createdBy) {
        int userId = GsonUtility.optInt(usersObject, "id");
        int permission = GsonUtility.optInt(usersObject, "");
        globalConnectionService.updateOrInsert(globalId, userId, null, null, permission, createdBy);
    }

    public void shareRoles(JsonObject roleObject, int globalId, String createdBy) {
        int roleId = GsonUtility.optInt(roleObject, "id");
        int permission = GsonUtility.optInt(roleObject,"");
        globalConnectionService.updateOrInsert(globalId, null, null, roleId, permission, createdBy);
    }

    public void shareOrg(JsonObject roleObject, int globalId, String createdBy) {
        int orgId = GsonUtility.optInt(roleObject,"id");
        int permission = GsonUtility.optInt(roleObject,"");
        globalConnectionService.updateOrInsert(globalId, null, orgId, null, permission, createdBy);
    }
}

