package com.helicalinsight.efw.components;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.DsOperation;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.model.DSTypeHikari;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * @author Prashansa
 * @author Rajasekhar
 */
@Component("hikariDsManager")
public class HikariDataSourceDB implements DsOperation {

    private static final Logger logger = LoggerFactory.getLogger(HikariDataSourceDB.class);

    @Autowired
    @Qualifier(value = "userDetailsService")
    private UserService userService;


    @Autowired
    private GlobalConnectionService globalConnectionService;


    @NotNull
    public String writeDataSource(JsonObject formData, String mode) {
        Map<String, String> map = ApplicationUtilities.getDefaultsMap();
        HikariCpDataSourceProperties.accumulate(formData, map);


        Security security = SecurityUtils.securityObject();
        String createdBy = security.getCreatedBy();
        Integer globalId=null;

        if ("create".equalsIgnoreCase(mode)) {
            //GlobalConnection
            globalId = createDataSource(formData, map, createdBy);

        } else if ("edit".equalsIgnoreCase(mode)) {
            globalId = editDataSource(formData, map);
        }
        JsonObject result;
        result = new JsonObject();
        result.addProperty("message", "A new Hikari connection is created successfully.");
        result.addProperty("dataSourceId", globalId);
        return result.toString();
    }

    private Integer editDataSource(JsonObject formData, Map<String, String> map) {
        Integer globalId;//GlobalConnection
        Integer id = formData.get("id").getAsInt();
        globalId = id;
        GlobalConnections globalConnection = globalConnectionService.findGlobalConnectionById(id);
        globalConnection.setName(formData.get("name").getAsString());
        globalConnection.setLastUpdatedTime(new Date());


        DSTypeHikari connection = globalConnectionService.getHikariConnectionById(id);
        connection.setUserName(formData.get("userName").getAsString());
        connection.setPassword(CipherUtils.encrypt(formData.get("password").getAsString()));
        connection.setJdbcUrl(formData.get("jdbcUrl").getAsString());
        connection.setDriverName(formData.get("driverName").getAsString());
        connection.setDatabaseDialect(DataSourceUtils.getDatabaseDialect(formData));
        connection.setDatabaseName(GsonUtility.optString(formData,"database"));
        connection.setGlobalConnections(globalConnection);
        connection.setDataSourceProvider(formData.get("dataSourceProvider").getAsString());
        connection.setLastUpdatedTime(new Date());
        String driverName = formData.get("driverName").getAsString();
        String[] array = driverName.split("\\.");
        connection.setDataSourcePoolId("hikari_" + id);

        String vendorName = (array.length >= 2 ? array[1] : "");
        connection.setPoolName("hikari_" + vendorName + "_" + id);
        connection.setVisible(true);
        Boolean aBoolean = globalConnectionService.editDataSourceDetails(globalConnection, connection);
        if(!aBoolean){
            logger.error("Something went wrong while updating the Datasource");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("A new Hikari connection is updated successfully.");
        }
        return globalId;
    }

    private Integer createDataSource(JsonObject formData, Map<String, String> map, String createdBy) {
        Integer globalId;GlobalConnections globalConnection = new GlobalConnections();
        globalConnection.setName(formData.get("name").getAsString());
        globalConnection.setCreatedBy(createdBy);
        globalConnection.setVendor(GsonUtility.optString(formData,"vendorName"));
        globalConnection.setType(GlobalJdbcType.DYNAMIC_DATASOURCE);
        globalConnection.setBaseType(GlobalJdbcType.TYPE);


        globalConnection.setCreatedDate(new Date());
        globalConnection.setLastUpdatedTime(new Date());
        String dsType = JsonUtils.getDSType(DSTypeHikari.class.getName());
        globalConnection.setDsType(dsType);
        globalConnection.setIsMigrated(false);


        DSTypeHikari connection = new DSTypeHikari();
        connection.setUserName(formData.get("userName").getAsString());
        connection.setPassword(CipherUtils.encrypt(formData.get("password").getAsString()));
        connection.setJdbcUrl(formData.get("jdbcUrl").getAsString());
        connection.setDriverName(formData.get("driverName").getAsString());
        connection.setMaximumPoolSize(Integer.valueOf(formData.get("poolSize").getAsString()));
        connection.setConnectionTimeout(formData.get("connectionTimeout").getAsString());
        connection.setDatabaseDialect(DataSourceUtils.getDatabaseDialect(formData));
        connection.setDatabaseName(GsonUtility.optString(formData,"database"));
        connection.setGlobalConnections(globalConnection);
        connection.setDataSourceProvider(formData.get("dataSourceProvider").getAsString());

        String driverName = formData.get("driverName").getAsString();
        String[] array = driverName.split("\\.");
        connection.setDataSourcePoolId("hikari_replace");

        String vendorName = (array.length >= 2 ? array[1] : "");
        connection.setPoolName("hikari_" + vendorName + "_" + "replace");
        connection.setVisible(true);
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

        connection.setMaxLifetime(formData.get("maxLifetime").getAsInt());

        connection.setLastUpdatedTime(new Date());
        connection.setValidationQuery(formData.get("testQuery").getAsString());

        Boolean aBoolean = globalConnectionService.addDataSourceDetails(globalConnection, connection);

        if(!aBoolean){
            logger.error("Something went wrong while saving the Datasource");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("A new Hikari connection is created successfully.");
        }
        return globalConnection.getGlobalId();
    }

}