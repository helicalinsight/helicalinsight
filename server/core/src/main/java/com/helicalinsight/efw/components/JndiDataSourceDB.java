package com.helicalinsight.efw.components;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.DsOperation;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.model.DSTypeJndi;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Prashansa
 * @author Rajasekhar
 */
@Component("jndiDsManager")
public class JndiDataSourceDB implements DsOperation {

    private static final Logger logger = LoggerFactory.getLogger(JndiDataSourceDB.class);



    @Autowired
    private GlobalConnectionService globalConnectionService;

    @Autowired
    private ResourcePermissionLevelsHolder resourcePermissionLevelsHolder;


    @NotNull
    public String writeDataSource(@NotNull JsonObject formData, String mode) {

        Security security = SecurityUtils.securityObject();
        String createdBy = security.getCreatedBy();
        Integer globalId=null;
        if ("create".equalsIgnoreCase(mode)) {
            globalId = createDataSource(formData,createdBy);
        } else if ("edit".equalsIgnoreCase(mode)) {
            globalId = editDataSource(formData);
        }

        JsonObject result = new JsonObject();
        result.addProperty("message", "A new Jndi data source is created successfully.");
        result.addProperty("dataSourceId", globalId);
        return result.toString();
    }

    private Integer editDataSource(JsonObject formData) {
        Integer globalId;
        int id = Integer.valueOf(formData.get("id").toString());
        GlobalConnections globalConnection = globalConnectionService.findGlobalConnectionById(id);
        globalConnection.setName(formData.get("name").getAsString());
        globalConnection.setLastUpdatedTime(new Date());
        globalId = id;
        //JNDI
        DSTypeJndi connection = globalConnectionService.getJndiConnectionById(id);
        if (logger.isDebugEnabled()) {
            logger.debug("A new Jndi data source is created successfully.");
        }
        globalConnection.setName(formData.get("name").getAsString());
        connection.setDataSourceProvider("jndi");
        connection.setDatabaseDialect(DataSourceUtils.getDatabaseDialect(formData));
        if (formData.has("driverClassName")) {
            connection.setDriverClassName(formData.get("driverClassName").getAsString());
        } else {
            connection.setDriverClassName(JsonUtils.defaultDriverClassName());
        }
        connection.setDatabaseName(GsonUtility.optString(formData,"databaseName"));
        connection.setLookUpName("java:comp/env/" + formData.get("lookUpName").getAsString().replace("java:comp/env/", ""));
        connection.setLastUpdatedTime(new Date());
        connection.setGlobalConnections(globalConnection);
        Boolean aBoolean = globalConnectionService.editDataSourceDetails(globalConnection, connection);
        if(!aBoolean){
            logger.error("Something went wrong while updating the Datasource");
        }
        return globalId;
    }

    private Integer createDataSource(JsonObject formData, String createdBy) {
        Integer globalId;//Global Connection
        GlobalConnections globalConnection = new GlobalConnections();
        globalConnection.setName(formData.get("name").getAsString());
        globalConnection.setCreatedBy(createdBy);
        globalConnection.setVendor(GsonUtility.optString(formData,"vendorName"));
        globalConnection.setType(GlobalJdbcType.STATIC_DATASOURCE);
        globalConnection.setBaseType(GlobalJdbcType.TYPE);
        globalConnection.setCreatedDate(new Date());
        globalConnection.setLastUpdatedTime(new Date());
        String dsType = JsonUtils.getDSType(DSTypeJndi.class.getName());
        globalConnection.setDsType(dsType);
        globalConnection.setIsMigrated(false);



        //JNDI
        DSTypeJndi connection = new DSTypeJndi();
        connection.setVisible(true);
        if (logger.isDebugEnabled()) {
            logger.debug("A new Jndi data source is created successfully.");
        }
        globalConnection.setName(formData.get("name").getAsString());
        connection.setDataSourceProvider("jndi");
        connection.setDatabaseDialect(DataSourceUtils.getDatabaseDialect(formData));
        if (formData.has("driverClassName")) {
            connection.setDriverClassName(formData.get("driverClassName").getAsString());
        } else {
            connection.setDriverClassName(JsonUtils.defaultDriverClassName());
        }
        connection.setDatabaseName(GsonUtility.optString(formData, "database"));
        connection.setLookUpName("java:comp/env/" + formData.get("lookUpName").getAsString().replace("java:comp/env/", ""));
        connection.setLastUpdatedTime(new Date());
        connection.setGlobalConnections(globalConnection);

        Boolean aBoolean = globalConnectionService.addDataSourceDetails(globalConnection, connection);

        if(!aBoolean){
            logger.error("Something went wrong while saving the Datasource");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("A new Jdbc connection is created successfully.");
        }
        return globalConnection.getGlobalId();
    }

   
}
