package com.helicalinsight.efw.components;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.DsOperation;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.model.DSTypePlainJDBC;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Helical on 5/19/2021.
 */
@Component("noneDsManager")
public class PlainJDBCIOHandler implements DsOperation {

    private static final Logger logger = LoggerFactory.getLogger(PlainJDBCIOHandler.class);


    @Autowired
    private GlobalConnectionService globalConnectionService;

    public String writeDataSource(@NotNull JsonObject formData, String mode) {
        Integer globalId = null;

        if(!formData.has("databaseName")){
            formData.addProperty("database",GsonUtility.optString(formData,"database"));
        }

        if ("create".equalsIgnoreCase(mode)) {
            globalId = createDataSource(formData);
        } else if ("edit".equalsIgnoreCase(mode)) {
            globalId = editDatasource(formData);
        }

        JsonObject result;
        result = new JsonObject();
        result.addProperty("message", "A new Jdbc connection is created successfully.");
        result.addProperty("dataSourceId", globalId);
        return result.toString();
    }

    //CREATE
    private Integer createDataSource(JsonObject formData) {
        Integer globalId;GlobalConnections globalConnection = new GlobalConnections();
        Security security = SecurityUtils.securityObject();
        String createdBy = security.getCreatedBy();

        //GlobalConnection
        globalConnection.setName(formData.get("name").getAsString());
        globalConnection.setCreatedBy(createdBy);
        globalConnection.setVendor(GsonUtility.optString(formData,"vendorName"));
        globalConnection.setType(GlobalJdbcType.NON_POOLED);
        globalConnection.setBaseType(GlobalJdbcType.TYPE);
        globalConnection.setCreatedDate(new Date());
        globalConnection.setLastUpdatedTime(new Date());
        String dsType = JsonUtils.getDSType(DSTypePlainJDBC.class.getName());
        globalConnection.setDsType(dsType);
        globalConnection.setIsMigrated(false);

        //PLAIN JDBC

        DSTypePlainJDBC connection = new DSTypePlainJDBC();
        connection.setGlobalConnections(globalConnection);
        connection.setDataSourceProvider(formData.get("dataSourceProvider").getAsString());
        connection.setUserName(formData.get("userName").getAsString());
        connection.setPassword(CipherUtils.encrypt(formData.get("password").getAsString()));
        connection.setJdbcUrl(formData.get("jdbcUrl").getAsString());
        connection.setDriverName(formData.get("driverName").getAsString());
        connection.setDatabaseDialect(DataSourceUtils.getDatabaseDialect(formData));
        connection.setDatabaseName(GsonUtility.optString(formData, "database"));
        connection.setVisible(true);
        connection.setLastUpdatedTime(new Date());

        Boolean aBoolean = globalConnectionService.addDataSourceDetails(globalConnection, connection);

        if(!aBoolean){
            logger.error("Something went wrong while saving the Datasource");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("A new Jdbc connection is created successfully.");
        }
        return globalConnection.getGlobalId();
    }

    private Integer editDatasource(JsonObject formData) {
        Integer globalId;
        Integer id = formData.get("id").getAsInt();
        GlobalConnections globalConnection = globalConnectionService.findGlobalConnectionById(id);


        //GlobalConnection
        globalConnection.setGlobalId(id);
        globalConnection.setName(formData.get("name").getAsString());
        globalConnection.setLastUpdatedTime(new Date());
        globalId = id;
        //PLAIN JDBC
        DSTypePlainJDBC connection = globalConnectionService.getPlainJDBCConnectionById(id);
        connection.setDataSourceProvider(formData.get("dataSourceProvider").getAsString());
        connection.setUserName(formData.get("userName").getAsString());
        connection.setPassword(CipherUtils.encrypt(formData.get("password").getAsString()));
        connection.setJdbcUrl(formData.get("jdbcUrl").getAsString());
        connection.setDriverName(formData.get("driverName").getAsString());
        connection.setDatabaseDialect(DataSourceUtils.getDatabaseDialect(formData));
        connection.setDatabaseName(GsonUtility.optString(formData, "database"));
        connection.setLastUpdatedTime(new Date());
        Boolean aBoolean = globalConnectionService.editDataSourceDetails(globalConnection, connection);
        if(!aBoolean){
            logger.error("Something went wrong while updating the Datasource");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("A new Jdbc connection is updated successfully.");
        }
        return globalId;
    }
}
