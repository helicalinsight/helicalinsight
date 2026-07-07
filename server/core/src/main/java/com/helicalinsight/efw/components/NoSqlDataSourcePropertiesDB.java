package com.helicalinsight.efw.components;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.DsOperation;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.model.DSTypeNoSQL;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.nosql.NoSQLLoader;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.NoSqlUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

import static com.helicalinsight.efw.components.NoSqlDataSourceProperties.getSubType;

/**
 * @author Somen
 *         Created  on 11/9/2017.
 */
@Component("noSqlDsManager")
public class NoSqlDataSourcePropertiesDB implements DsOperation {
    private static final Logger logger = LoggerFactory.getLogger(NoSqlDataSourcePropertiesDB.class);

    @Autowired
    private GlobalConnectionService globalConnectionService;

    public static String returnMessage(String message, Integer maxId) {
        JsonObject result;
        result = new JsonObject();
        result.addProperty("message", message);
        result.addProperty("dataSourceId", maxId);
        return result.toString();
    }


    public String writeDataSource(JsonObject formData, String mode) {


        Security security = SecurityUtils.securityObject();
        String createdBy = security.getCreatedBy();

        Map<String, String> map = ApplicationUtilities.getDefaultsMap();
        if (!formData.has("testQuery")) {
            formData.addProperty("testQuery", map.get("tomcat.testQuery"));
        }else{
            formData.addProperty("testQuery",GsonUtility.optString(formData, "validationQuery"));
        }

        if (!formData.has("poolSize")) {
            formData.addProperty("poolSize", map.get("tomcat.maxActive"));
        }

        if(!formData.has("maxActive")){
            formData.addProperty("maxActive", map.get("tomcat.maxActive"));
        }

        if(!formData.has("minIdle")){
            formData.addProperty("minIdle", map.get("tomcat.minIdle"));
        }

        if(!formData.has("maxWait")){
            formData.addProperty("maxWait", map.get("tomcat.maxWait"));
        }

        if(!formData.has("initialSize")){
            formData.addProperty("initialSize", map.get("tomcat.initialSize"));
        }

        if(!formData.has("removeAbandonedTimeout")){
            formData.addProperty("removeAbandonedTimeout", map.get("tomcat.removeAbandonedTimeout"));
        }

        if(!formData.has("removeAbandoned")){
            formData.addProperty("removeAbandoned", map.get("tomcat.removeAbandoned"));
        }

        if(!formData.has("logAbandoned")){
            formData.addProperty("logAbandoned", map.get("tomcat.logAbandoned"));
        }

        if(!formData.has("minEvictableIdleTimeMillis")){
            formData.addProperty("minEvictableIdleTimeMillis", map.get("tomcat.minEvictableIdleTimeMillis"));
        }

        if(!formData.has("jmxEnabled")){
            formData.addProperty("jmxEnabled", map.get("tomcat.jmxEnabled"));
        }

        if(!formData.has("jdbcInterceptors")){
            formData.addProperty("jdbcInterceptors", map.get("tomcat.jdbcInterceptors"));
        }

        if(!formData.has("jdbcUrl")){
            formData.addProperty("jdbcUrl", GsonUtility.optString(formData,"url"));
        }

        if(!formData.has("validationInterval")){
            formData.addProperty("validationInterval", map.get("tomcat.validationInterval"));
        }

        if(!formData.has("timeBetweenEvictionRunsMillis")){
            formData.addProperty("timeBetweenEvictionRunsMillis", map.get("tomcat.timeBetweenEvictionRunsMillis"));
        }

        if(!formData.has("testOnBorrow")){
            formData.addProperty("testOnBorrow", map.get("tomcat.testOnBorrow"));
        }

        if(!formData.has("minIdle")){
            formData.addProperty("minIdle", map.get("tomcat.minIdle"));
        }



        int globalId = 0;
        if ("create".equalsIgnoreCase(mode)) {
            globalId = addDataSource(formData, mode, createdBy);

        } else if (("edit").equalsIgnoreCase(mode)) {
            globalId = editDataSource(formData, mode, createdBy);

        }
        return returnMessage("A new NoSql data source is created successfully.", globalId);
    }


    private Integer editDataSource(JsonObject formData, String mode, String createdBy) {
        int globalId;
        Integer id = formData.get("id").getAsInt();
        GlobalConnections globalConnection = globalConnectionService.findGlobalConnectionById(id);

        String driverName = null;
        if (formData.has("driverName")) {
            driverName = formData.get("driverName").getAsString();
        }
        if (formData.has("driverClassName")) {
            driverName = formData.get("driverClassName").getAsString();
        }

        if (null != driverName) {
            String name = formData.get("name").getAsString();
            if(name.contains(" ")){
                throw new EfwServiceException("Blank space is not allowed in datasource name ");
            }
            globalConnection.setName(name);
        }


        globalConnection.setCreatedBy(createdBy);
        globalConnection.setVendor(GsonUtility.optString(formData,"vendorName"));

        globalConnection.setLastUpdatedTime(new Date());



        globalId = id;
        DSTypeNoSQL connection = globalConnectionService.getNoSQLConnectionById(id);
        connection.setDataSourceProvider(formData.get("dataSourceProvider").getAsString());
        connection.setUsername(formData.get("userName").getAsString());
        connection.setPassword(CipherUtils.encrypt(formData.get("password").getAsString()));
        connection.setUrl(formData.get("jdbcUrl").getAsString());
        connection.setDriverClassName(formData.get("driverName").getAsString());
        connection.setMaxActive(formData.get("poolSize").getAsInt());
        connection.setDataSourcePoolId("nosql_" + id);
        connection.setDatabaseDialect(DataSourceUtils.getDatabaseDialect(formData));
        connection.setDatabaseName(GsonUtility.optString(formData, "database"));
        connection.setCollection(GsonUtility.optString(formData,"collection"));
        String subType = getSubType(formData);
        connection.setSubType(subType);
        connection.setHiveReferenceId(GsonUtility.optInt(formData, "hiveReferenceId"));
        if (driverName != null && driverName.contains("nosql")) {
            connection.setHiveReferenceId(-1);
            connection.setDataSourcePoolId("nosql_" + id);
        } else {
            connection.setHiveReferenceId(0);
        }
        if (!mode.equalsIgnoreCase("share")) {
            String collection = GsonUtility.optString(formData,"collection");
            connection.setCollection(collection);
        }

        connection.setLastUpdatedTime(new Date());
        connection.setGlobalConnections(globalConnection);
        Boolean aBoolean = globalConnectionService.editDataSourceDetails(globalConnection, connection);
        if(!aBoolean){
            logger.error("Something went wrong while updating the Datasource");
        }
        if (subType != null && subType.trim().length() > 0) {
            formData.addProperty("theId",globalConnection.getGlobalId());
            NoSQLLoader noSqlImplementation = NoSqlUtils.getNoSqlImplementation(subType);
            noSqlImplementation.loadToMiddleWare(formData);
        }
        return globalId;
    }

    private int addDataSource(JsonObject formData, String mode, String createdBy) {
        int globalId;
        GlobalConnections globalConnection = new GlobalConnections();
        String driverName = null;
        if (formData.has("driverName")) {
            driverName = formData.get("driverName").getAsString();
        }else{
            driverName = GsonUtility.optString(formData,"dataSourceProvider");
        }
        if (formData.has("driverClassName")) {
            driverName = formData.get("driverClassName").getAsString();
        }

        if (null != driverName) {
            String name = formData.get("name").getAsString();
            if(name.contains(" ")){
                throw new EfwServiceException("Blank space is not allowed in datasource name ");
            }
            globalConnection.setName(name);
        }

        globalConnection.setCreatedBy(createdBy);
        globalConnection.setVendor(GsonUtility.optString(formData,"vendorName"));
        globalConnection.setCreatedDate(new Date());
        if (driverName != null && driverName.contains("nosql")) {
            globalConnection.setType(GlobalJdbcType.DYNAMIC_DATASOURCE);
        } else {
            globalConnection.setType(GlobalJdbcType.NOSQL_DATASOURCE);
        }
        globalConnection.setBaseType(GlobalJdbcType.TYPE);
        globalConnection.setLastUpdatedTime(new Date());
        String dsType = JsonUtils.getDSType(DSTypeNoSQL.class.getName());
        globalConnection.setDsType(dsType);
        globalConnection.setIsMigrated(false);


        DSTypeNoSQL connection = new DSTypeNoSQL();
        connection.setVisible(true);
        connection.setDataSourceProvider(formData.get("dataSourceProvider").getAsString());
        connection.setUsername(formData.get("userName").getAsString());
        connection.setPassword(CipherUtils.encrypt(formData.get("password").getAsString()));
        connection.setUrl(formData.get("jdbcUrl").getAsString());
        connection.setDriverClassName(formData.get("driverName").getAsString());
        connection.setMaxActive(formData.get("poolSize").getAsInt());
        connection.setDatabaseDialect(DataSourceUtils.getDatabaseDialect(formData));
        connection.setDatabaseName(GsonUtility.optString(formData,"database"));
        connection.setValidationInterval(GsonUtility.optInt(formData, "validationInterval"));
        connection.setTimeBetweenEvictionRunsMillis(GsonUtility.optInt(formData, "timeBetweenEvictionRunsMillis"));
        connection.setMinIdle(GsonUtility.optInt(formData, "minIdle"));
        connection.setMaxWait(GsonUtility.optInt(formData, "maxWait"));
        connection.setInitialSize(GsonUtility.optInt(formData, "initialSize"));
        connection.setRemoveAbandonedTimeout(GsonUtility.optInt(formData, "removeAbandonedTimeout"));
        connection.setRemoveAbandoned(GsonUtility.optBoolean(formData,"removeAbandoned"));
        connection.setLogAbandoned(GsonUtility.optBoolean(formData,"logAbandoned"));
        connection.setMinEvictableIdleTimeMillis(GsonUtility.optInt(formData, "minEvictableIdleTimeMillis"));
        connection.setJmxEnabled(GsonUtility.optBoolean(formData,"jmxEnabled"));
        connection.setTestOnBorrow(GsonUtility.optBoolean(formData,"testOnBorrow"));
        String subType = getSubType(formData);
        connection.setSubType(subType);
        connection.setHiveReferenceId(GsonUtility.optInt(formData, "hiveReferenceId"));
        if (driverName != null && driverName.contains("nosql")) {
            connection.setHiveReferenceId(-1);
            connection.setDataSourcePoolId("nosql_replace");
        } else {
            connection.setHiveReferenceId(0);
        }
        if (!mode.equalsIgnoreCase("share")) {
            String collection = GsonUtility.optString(formData, "collection");
            connection.setCollection(collection);
        }

        connection.setLastUpdatedTime(new Date());
        connection.setGlobalConnections(globalConnection);
        connection.setValidationQuery(formData.get("testQuery").getAsString());
        connection.setJdbcInterceptors(GsonUtility.optString(formData,"jdbcInterceptors"));
        Boolean aBoolean = globalConnectionService.addDataSourceDetails(globalConnection, connection);
        if(!aBoolean){
            logger.error("Something went wrong while saving the Datasource");
        }
        if (subType != null && subType.trim().length() > 0) {
            formData.addProperty("theId",globalConnection.getGlobalId());
            NoSQLLoader noSqlImplementation = NoSqlUtils.getNoSqlImplementation(subType);
            noSqlImplementation.loadToMiddleWare(formData);
        }
        return globalConnection.getGlobalId();
    }
}
