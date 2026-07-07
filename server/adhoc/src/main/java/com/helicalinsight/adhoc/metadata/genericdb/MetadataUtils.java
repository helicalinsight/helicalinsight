package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.components.EfwdReader;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.SplitterUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for working with metadata.
 * 
 * Created by author on 27-02-2015.
 * @author Rajasekhar
 */
public class MetadataUtils {
	/**
     * Retrieves a new metadata object based on the provided connection ID and metadata.
     *
     * @param connectionId 			 ID of the connection
     * @param metadata     			 existing metadata object
     * @return A new metadata object based on the provided connection ID
     */
    public static Metadata getNewMetadata(String connectionId, Metadata metadata) {
        Metadata metadataNew = ApplicationContextAccessor.getBean(Metadata.class);
        Connections connections = metadata.getConnections();
        if (connections == null) {
            return metadataNew;
        }
        List<ConnectionDatabase> connectionDatabase = connections.getConnectionDatabase();
        if (connectionDatabase == null) {
        	connectionDatabase = new ArrayList<>();
        }
//        ConnectionDatabase conDb =  ApplicationContextAccessor.getBean(ConnectionDatabase.class);
//        conDb.setConnectionDetails(metadata.getConnectionDetails());
//        Database db = metadata.getDatabase();
//        conDb.setDatabase(db);
//        connectionDatabase.add(conDb);
//        ExternalRelationships external = metadata.getExternalRelationships();
//        if(external != null) {
//        	List<Relationship> list =  external.getListOfRelations();
//        	Relationships relationships = ApplicationContextAccessor.getBean(Relationships.class);
//        	relationships.setListOfRelations(list);
//        	if(db.getRelationships() != null) {
//        		List<Relationship> existing = db.getRelationships().getListOfRelations();
//        		for(Relationship relationship : list) {
//        			if(!existing.contains(relationship)) {
//        				existing.add(relationship);
//        			}
//        		}
//        	}
//        	else db.setRelationships(relationships);
//        	
//        }
        for (ConnectionDatabase connectionDb : connectionDatabase) {
            if (connectionDb.getConnectionDetails().getConnectionId().equalsIgnoreCase(connectionId)) {
                metadataNew.setConnectionDetails(connectionDb.getConnectionDetails());
                metadataNew.setDatabase(connectionDb.getDatabase());
                ConnectionDetails connectionDetails=metadataNew.getConnectionDetails();
                if(connectionDetails!=null)
                	metadataNew.setConnectionType(connectionDetails.getConnectionType());
                return metadataNew;
            }
        }
        return metadataNew;

    }
    /**
     * Determines the driver class name based on the provided form data.
     *
     * @param formData 			 form data containing jdbc type
     * @return The driver class name
     */
    public static String databaseDriverClassName(JsonObject formData) {
        if (!formData.has("type")) {
            throw new IllegalArgumentException("The json has no type information. Incorrect datasource details.");
        }

        String type = formData.get("type").getAsString();

        String driverName = null;

        if (!GlobalJdbcTypeUtils.isTypeGlobal(type)) {
            EfwdReader efwdReader = new EfwdReader();
            String json = efwdReader.evaluateEFWDCondition(formData.toString());
            JsonObject resultJson = new Gson().fromJson(json,JsonObject.class);

            driverName = resultJson.getAsJsonObject("data").get("driverName").getAsString();
            if (resultJson.has("globalId")) {
                driverName = globalConnectionProcessor(resultJson, resultJson.get("type").getAsString());
            }
        } else {
            driverName = globalConnectionProcessor(formData, type);
        }

        if (driverName == null) {
            throw new EfwServiceException("The driver class name is not found.");
        }

        return driverName;
    }

    /**
     * globalConnectionProcessor(JsonObject formData, String type)
     * @param formData            formDataprovides id, dir.
     * @param type				  jdbc type
     * @return  driver name.
     */
    private static String globalConnectionProcessor(JsonObject formData, String type) {
        boolean nonPooled = GlobalJdbcType.NON_POOLED.equalsIgnoreCase(type);
        boolean staticDataSource = GlobalJdbcType.STATIC_DATASOURCE.equalsIgnoreCase(type);
        boolean dynamicDataSource = GlobalJdbcType.DYNAMIC_DATASOURCE.equalsIgnoreCase(type);
        if (GlobalJdbcTypeUtils.isManagedGroovyDataSource(type)) {
            if (formData.has("dir")) {
                String dir = formData.get("dir").getAsString();
                String id = formData.get("id").getAsString();
                String efwdFileName = null;
                if (formData.has("efwd")) {
                    efwdFileName = formData.getAsJsonObject("efwd").get("file").getAsString();
                }
                EfwdReader reader = new EfwdReader();
                String resultString = reader.evaluateEFWDCondition(formData.toString());
                JsonObject formDataJson = new Gson().fromJson(resultString,JsonObject.class);
                return formDataJson.has("driver") ? formDataJson.get("driver").getAsString() : formDataJson.get("driverClassName").getAsString();
//                JSONObject condition = GlobalJdbcTypeUtils.getSwitchedConnection(dir, id, efwdFileName);
//                        String globalId = condition.getString("globalId");
//                        JSONObject formDataJson = JSONObject.fromObject(DataSourceUtils.globalIdJson(Integer.parseInt(globalId)));
//                        return formDataJson.has("driverName") ? formDataJson.getString("driverName") : formDataJson.getString("driverClassName");


            } else {
                String json = DataSourceUtils.globalIdJson(Integer.parseInt(formData.get("id").getAsString()));
                JsonObject jsonObject =new Gson().fromJson(json,JsonObject.class);
                return jsonObject.has("driverName") ? jsonObject.get("driverName").getAsString() : jsonObject.get("driverClassName").getAsString();
            }
        }
        if (nonPooled || staticDataSource || dynamicDataSource || GlobalJdbcType.NOSQL_DATASOURCE.equalsIgnoreCase(type)) {
            int conId;
            try {
                conId = Integer.parseInt(formData.get("id").getAsString());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("The parameter id should be an integer.", ex);
            }

            String json = DataSourceUtils.globalIdJson(conId);
            return JsonUtils.extractDriverName(json);
        }
        return null;
    }
    /**
     * Retrieves a parameter from the provided form data.
     *
     * @param formData  			 form data containing the parameters
     * @param parameter 			 name of the parameter to retrieve
     * @return The value of the parameter
     * @throws RequiredParameterIsNullException If the required parameter is null
     * @throws IncompleteFormDataException       If the form data is incomplete
     */
    public static String parameter(JsonObject formData, String parameter) {
        String required;
        try {
            required = formData.get(parameter).getAsString();
        } catch (Exception ex) {
            throw new RequiredParameterIsNullException(ex);
        }

        if ("".equals(required) || required.trim().length() == 0) {
            throw new IncompleteFormDataException(String.format("Required parameter %s " + "information is incorrect" +
                    ".", parameter));
        }
        return required;
    }
    /**
     * Generates a JSON representation of the metadata's datasource.
     *
     * @param metadata 			 metadata object
     * @return A JSON object representing the datasource
     */
    @NotNull
    public static JsonObject dataSourceJson(@NotNull Metadata metadata) {
        ConnectionDetails connectionDetails = metadata.getConnectionDetails();
        Database database = metadata.getDatabase();
        String fetchMode = connectionDetails.getFetchMode();
        JsonObject dataSource = new JsonObject();
        GsonUtility.accumulateBoolean(dataSource,"sync", fetchMode != null);
        String directory = connectionDetails.getDirectory();
        String connectionId = connectionDetails.getConnectionId();
        GsonUtility.accumulate(dataSource,"id", connectionId);
        String catalog = database.getCatalog();
        String schema = database.getSchema();
        /*To support old metadata we need to check this.
        * The logic is if databaseName is present and catalog and schema are not present then catalog and schema are same as databaseName.
        * If databaseName is having any '.' in the name then split the first name as catalog and the other part as the schema. */
        if (catalog == null && schema == null) {
            String name = database.getName();
            if (!name.contains(".")) {
                catalog = schema = name;
            } else {
                String[] split = name.split("\\.");
                catalog = split[0];
                schema = name.substring(name.indexOf(name) + catalog.length() + 1, name.length());

            }
            GsonUtility.accumulateBoolean(dataSource,"catSchemaPredicted", true);
        } else {
            GsonUtility.accumulateBoolean(dataSource,"catSchemaPredicted", false);
        }
        GsonUtility.accumulate(dataSource,"catalog", catalog);
        GsonUtility.accumulate(dataSource,"schema", schema);

        if (directory == null) {
            String subType = connectionDetails.getSubType();
            String baseType = connectionDetails.getConnectionType();

            GsonUtility.accumulate(dataSource,"type", subType);
            GsonUtility.accumulate(dataSource,"baseType", baseType);
        } else {
            String connectionType = connectionDetails.getConnectionType();

            GsonUtility.accumulate(dataSource,"type", connectionType);
            GsonUtility.accumulate(dataSource,"baseType", connectionType);
            GsonUtility.accumulate(dataSource,"dir", directory);
        }
        if(StringUtils.isNotBlank(connectionDetails.getVendorName())) {
        	GsonUtility.accumulate(dataSource,"vendorName", connectionDetails.getVendorName());
        }
        return dataSource;
    }
    /**
     * Retrieves details about the database connected to the provided connection.
     *
     * @param connection 			 database connection
     * @return Details about the database
     * @throws MetadataRetrievalException If database details cannot be retrieved
     */
    @NotNull
    public static String getDatabaseDetails(@NotNull Connection connection) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            return metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion();
        } catch (SQLException exception) {
            throw new MetadataRetrievalException("Could not retrieve catalog information.", exception);
        }
    }
    /**
     * Determines the SQL dialect of the specified database driver.
     *
     * @param driverName 		 name of the database driver
     * @return The SQL dialect of the database
     */
    public static String dialectOfDatabase(String driverName) {
        final String defaultDialect = com.helicalinsight.efw.utility.JsonUtils.defaultDialect();
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> dialects = propertiesFileReader.read("Admin", "sqlDialects.properties");
        String dialect = dialects.get(driverName);
        if (dialect == null) {
            // Set postgres as default dialect
            dialect = defaultDialect;
        }
        return dialect;
    }
    /**
     * Sets the catalog and schema names in the provided database object.
     *
     * @param catalog  		 catalog name
     * @param schema   		 schema name
     * @param database 		 database object
     */
    public static void setCatalogAndSchemaNames(@Nullable String catalog, @Nullable String
            schema, @NotNull Database database) {

        database.setCatalog(catalog == null ? "" : catalog);
        database.setSchema(schema == null ? "" : schema);

    }

    /**
     * Generates an ID based on the provided catalog, schema, and table name.
     *
     * @param catalog    		 catalog name
     * @param schema     		 schema name
     * @param tableName  		 table name
     * @return The generated ID
     */
    public static String getId(String catalog, String schema, String tableName) {
        if (catalog == null) catalog = "";
        if (schema == null) schema = "";
        return SplitterUtils.prepareServiceId(catalog + schema + tableName);

    }
}
