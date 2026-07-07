package com.helicalinsight.adhoc.metadata.genericdb;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.DriverClass;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.EfwdDatasourceUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Provides methods to set connection details in metadata.
 * 
 * Created by author on 26-06-2015.
 * @author Rajasekhar
 */
@Component
public class ConnectionTemplate {
	/**
     * Sets the connection details in the metadata based on the provided form data.
     *
     * @param formDataJson 		 JSON object containing form data provides jdbc type.
     * @param metadata     		 metadata object to which the connection details will be set.
     */
    public void setConnectionTag(@NotNull JsonObject formDataJson, @NotNull Metadata metadata) {
        // Reset connection
        metadata.setConnectionType(null);
        metadata.setConnectionDetails(null);

        String type = formDataJson.get("type").getAsString();
        if (!GlobalJdbcTypeUtils.isTypeGlobal(type)) {
            ConnectionDetails connectionDetails = getEfwdConnectionDetails(formDataJson);
            metadata.setConnectionDetails(connectionDetails);
            // For the sake of accessing the connection type at the service
            // level
            metadata.setConnectionType(type);
        } else {
            boolean nonPooled = GlobalJdbcType.NON_POOLED.equalsIgnoreCase(type);
            boolean staticDataSource = GlobalJdbcType.STATIC_DATASOURCE.equalsIgnoreCase(type);
            boolean dynamicDataSource = GlobalJdbcType.DYNAMIC_DATASOURCE.equalsIgnoreCase(type);
            boolean groovyManaged = GlobalJdbcTypeUtils.isManagedGroovyDataSource(type);

            if (nonPooled || staticDataSource || dynamicDataSource
                    || GlobalJdbcType.NOSQL_DATASOURCE.equalsIgnoreCase(type)) {
                ConnectionDetails connectionDetails = getGlobalConnectionDetails(formDataJson);
                metadata.setConnectionDetails(connectionDetails);
                // For the sake of accessing the connection type at the service
                // level
                metadata.setConnectionType(GlobalJdbcType.TYPE);
            } else if (groovyManaged) {
                ConnectionDetails connectionDetails = getGroovyManagedConnectionDetails(formDataJson);
                metadata.setConnectionDetails(connectionDetails);
                // For the sake of accessing the connection type at the service
                // level
                metadata.setConnectionType(connectionDetails.getConnectionType());
            } else {
                throw new IllegalArgumentException("The data source type should be one of the types of efwd or global");
            }
        }
    }
    /**
     * Retrieves connection details from the form JSON for Groovy managed connections.
     *
     * @param formJson 		 JSON object containing form data.
     * @return The ConnectionDetails object.
     */
    private ConnectionDetails getGroovyManagedConnectionDetails(JsonObject formJson) {
    	
    	return getEfwdConnectionDetails(formJson);
    }

    /**
     * Retrieves global connection details from the form JSON.
     *
     * @param formDataJson 		 JSON object containing id, dialect, type and dbId.
     * @return The ConnectionDetails object.
     */
    public ConnectionDetails getGlobalConnectionDetails(@NotNull JsonObject formDataJson) {
        ConnectionDetails connectionDetails = new ConnectionDetails();
        connectionDetails.setConnectionId(formDataJson.get("id").getAsString());
        if (formDataJson.has("dialect")) {
            connectionDetails.setDialect(formDataJson.get("dialect").getAsString());
        }
        DriverClass driverClass = driverClass(formDataJson);
        connectionDetails.setDriverClass(driverClass);

        connectionDetails.setConnectionType(GlobalJdbcType.TYPE);
        connectionDetails.setSubType(formDataJson.get("type").getAsString());
        if(formDataJson.has("dbId"))
        	connectionDetails.setDbId(formDataJson.get("dbId").getAsString());
        return connectionDetails;
    }
    
    /**
     * Retrieves EFWD connection details from the form JSON.
     *
     * @param formDataJson 		 JSON object containing type, dialect, id , dir and dbId.
     * @return The ConnectionDetails object.
     */
    public ConnectionDetails getEfwdConnectionDetails(@NotNull JsonObject formDataJson) {
        ConnectionDetails connectionDetails = new ConnectionDetails();
        connectionDetails.setConnectionType(formDataJson.get("type").getAsString());
        if (formDataJson.has("dialect")) {
            connectionDetails.setDialect(formDataJson.get("dialect").getAsString());
        }
        DriverClass driverClass = driverClassForEfwd(formDataJson);
        connectionDetails.setDriverClass(driverClass);

        connectionDetails.setConnectionId(formDataJson.get("id").getAsString());
        connectionDetails.setDirectory(formDataJson.get("dir").getAsString());
        if(formDataJson.has("dbId"))
        	connectionDetails.setDbId(formDataJson.get("dbId").getAsString());
        return connectionDetails;
    }
    
    /**
     * Retrieves the driver class for global connections from the form JSON.
     *
     * @param formDataJson 		 JSON object containing  id and database dialect.
     * @return The DriverClass object.
     */
    private DriverClass driverClass(@NotNull JsonObject formDataJson) {
        DriverClass driverClass = ApplicationContextAccessor.getBean(DriverClass.class);
        String driverClassName = MetadataUtils.databaseDriverClassName(formDataJson);

        String id = formDataJson.get("id").getAsString();

        String globalConnectionJson = DataSourceUtils.globalIdJson(Integer.parseInt(id));
        JsonObject globalConnectionJSONObject = new Gson().fromJson(globalConnectionJson,JsonObject.class);
        String reference = null;
        if (formDataJson.has("databaseDialect")) {
            reference = formDataJson.get("databaseDialect").getAsString();
        } else if ((globalConnectionJSONObject.has("databaseDialect"))
                && !("".equals(globalConnectionJSONObject.get("databaseDialect").getAsString()))) {
            reference = globalConnectionJSONObject.get("databaseDialect").getAsString();
        } else {
            reference = JsonUtils.functionsReference(driverClassName);
        }

        driverClass.setDriverClass(driverClassName);
        driverClass.setReference(reference);
        return driverClass;
    }
    
    /**
     * Retrieves the driver class for global connections from the form JSON.
     *
     * @param formDataJson The JSON object containing form data.
     * @return The DriverClass object.
     */
    private DriverClass driverClassManagedGroovy(@NotNull JsonObject formDataJson) {
        DriverClass driverClass = ApplicationContextAccessor.getBean(DriverClass.class);
        String driverClassName = GsonUtility.optStringValue(formDataJson,"driverName", GsonUtility.optStringValue(formDataJson,"driverName", GsonUtility.optString(formDataJson,"driverClassName")));
        return getDriverClass(formDataJson, driverClass, driverClassName);
    }
    /**
     * The getDriverClass method is responsible for retrieving the driver class information from the form JSON data. 
     * @param formDataJson 		 JSON object containing form data related to the database connection.
     * @param driverClass  		 An instance of the DriverClass class to be populated with driver information.
     * @param driverClassName 	 name of the JDBC driver class extracted from the form JSON.
     * @return DriverClass object containing driver class name and reference.
     */
    private DriverClass getDriverClass(JsonObject formDataJson, DriverClass driverClass, String driverClassName) {
        String reference = GsonUtility.optString(formDataJson,"databaseDialect");
        if ("".equals(reference)||"[]".equals(reference)) {
            reference = JsonUtils.functionsReference(driverClassName);

        }
        driverClass.setDriverClass(driverClassName);
        driverClass.setReference(reference);
        return driverClass;
    }
    /**
     * The getDriverClass method is responsible for retrieving the driver class information from the form JSON data. 
     * @param formDataJson 		 JSON object containing form data related to the database connection.
     * @param driverClass  		 An instance of the DriverClass class to be populated with driver information.
     * @param driverClassName 	 name of the JDBC driver class extracted from the form JSON.
     * @return DriverClass object containing driver class name and reference.
     */
    private DriverClass getDriverClass(ObjectNode formDataJson, DriverClass driverClass, String driverClassName) {
        String reference = formDataJson.path("databaseDialect").asText();
        if ("".equals(reference)||"[]".equals(reference)) {
            reference = JsonUtils.functionsReference(driverClassName);

        }
        driverClass.setDriverClass(driverClassName);
        driverClass.setReference(reference);
        return driverClass;
    }
    
    /**
     * Retrieves the driver class for EFWD connections from the form JSON.
     *
     * @param formDataJson 		 	object provides id and type.
     * @return The DriverClass object.
     */
    private DriverClass driverClassForEfwd(@NotNull JsonObject formDataJson) {
        DriverClass driverClass = ApplicationContextAccessor.getBean(DriverClass.class);
        String driverClassName = MetadataUtils.databaseDriverClassName(formDataJson);
        String id = GsonUtility.optString(formDataJson, "id");
        String type = GsonUtility.optString(formDataJson,"type");
        ObjectNode efwdConnection = EfwdDatasourceUtils.getEfwdConnection(id, type);
        return getDriverClass(efwdConnection, driverClass, driverClassName);
    }

}
