package com.helicalinsight.adhoc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.*;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;

/**
 * @Author Rajesh.
 * @created Date:09-02-2018
 * <p>
 * This class is the implementation of {@code IComponent} and extends
 * {@link MetadataWorkflowComponent} which implements the IComponent. This
 * class is the enhancement of the MetadataWorkflowComponent This
 * class picks the appropriate efwd file based on the driverClass.
 * This class checks the parameters from the formData to get the
 * appropriate mapId. This class passes the required information like
 * mapId, targetEFWDPath, efwd file and parameters to the
 * QueryExecuter for further process This class is responsible for
 * process the result in required format which is returned by the
 * QueryExecuter.
 */

public class EnhancedMetadataWorkflowComponent extends MetadataWorkflowComponent {
    private static final String DB_CONFIG_DIRECTORY = EfwdRequirements.DB_CONFIG_DIRECTORY.getData();
    private static final String ADMIN_DIRECTORY = EfwdRequirements.ADMIN_DIRECTORY.getData();
    private static final Logger logger = LoggerFactory.getLogger(EnhancedMetadataWorkflowComponent.class);
    private static ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    /**
     * Executes the metadata workflow component with enhancements based on the provided JSON form data.
     * @param jsonFormData 			 formData to get the component execute
     * @return String result in required format
     */
    @Override
    public String executeComponent(String jsonFormData) {
        logger.debug("jsonFormData    :" + jsonFormData);
        JsonObject requestJson = new Gson().fromJson(jsonFormData,JsonObject.class);
        JsonObject parameters = getValidatedParameters(requestJson);
        JsonObject resultJson = new JsonObject();
        String dataSourceType = requestJson.get("type").getAsString();
        //String datasourceId = requestJson.getString("id");
        String databaseDialect = requestJson.has("databaseDialect") ? requestJson.get("databaseDialect").getAsString() : null;
        JsonObject connectionDetails = DataSourceUtils.getConnectionJson(requestJson);
        requestJson.add("connectionJson",connectionDetails);
        DriverConnection driverConnection = null;
        driverConnection = (DriverConnection) ConnectionProviderFactory.getConnection(requestJson,dataSourceType);
        String driverClassName = null;


        if (connectionDetails.has("driverClassName")) {
            driverClassName = connectionDetails.get("driverClassName").getAsString();
        }
        if (connectionDetails.has("Driver")) {
            driverClassName = connectionDetails.get("Driver").getAsString();
        }
        if (connectionDetails.has("driverName")) {
            driverClassName = connectionDetails.get("driverName").getAsString();
        }
        if(driverClassName==null && driverConnection!=null && driverConnection.getDriverClass()!=null)
        	driverClassName=driverConnection.getDriverClass();
        String efwdFileNameWithExtension = databaseDialect!= null ? databaseDialect : getEFWDFileName(driverClassName);
        String systemDirectory = applicationProperties.getSystemDirectory();
        String targetEFWDPath = systemDirectory + File.separator + ADMIN_DIRECTORY + File.separator
                + DB_CONFIG_DIRECTORY + File.separator + efwdFileNameWithExtension;
        File efwdFile = new File(targetEFWDPath);

        if (!(efwdFileNameWithExtension != null && efwdFile.exists())) {

            //The driverConnection holder has a unused Connection object
            //Hence closing the connection to avoid the connection leakage.
            if (driverConnection != null)
                DbUtils.closeQuietly(driverConnection.getConnection());
            String superResult = super.executeComponent(requestJson.toString());
            superResult = addDataSourceInResult(requestJson, parameters, superResult);
            return superResult;

        }
        JsonObject connectionJson = DataSourceUtils.getConnectionJson(requestJson);
        Connection connection = null;
        try {
            if (driverConnection != null) {
                connection = driverConnection.getConnection();
            }
            JsonObject connectionWrapper = new JsonObject();
            connectionWrapper.add("connection", connectionJson);
            JsonObject createMetadata = createMetadata(parameters, efwdFileNameWithExtension, connection,
                    connectionWrapper);
            resultJson.add("metadata", createMetadata);
            resultJson.addProperty("classifier", requestJson.get("classifier").getAsString());
            logger.debug("result  :" + resultJson.toString());
            String result = addDataSourceInResult(requestJson, parameters, resultJson.toString());
            return result;
        } finally {
            DbUtils.closeQuietly(connection);
        }
    }
    /**
     * Adds the data source information in the result JSON.
     *
     * @param requestJson 			JSON form data for the component execution.
     * @param parameters  			Validated parameters from the form data.
     * @param superResult 			Result obtained from the superclass execution.
     * @return Result JSON string with added data source information.
     */
    private String addDataSourceInResult(JsonObject requestJson, JsonObject parameters, String superResult) {
        if (parameters.has("fetchTables")) {
            JsonObject superResultJson = new Gson().fromJson(superResult,JsonObject.class);
            if (superResultJson.has("metadata")) {
                JsonObject metadata = superResultJson.getAsJsonObject("metadata");
                if (!metadata.has("dataSource")) {
                    JsonObject dataSource = new JsonObject();
                    dataSource.addProperty("id", requestJson.get("id").getAsString());
                    String type = requestJson.get("type").getAsString();
                    dataSource.addProperty("type", type);
                    String baseType = GlobalJdbcTypeUtils.isJustGlobal(type) ? GlobalJdbcType.TYPE : type;
                    dataSource.addProperty("baseType", baseType);
                    dataSource.addProperty("catSchemaPredicted", false);
                    dataSource.addProperty("sync", false);

                    JsonObject catalog = metadata.getAsJsonArray("catalogs").get(0).getAsJsonObject();

                    String catalogName = catalog.get("name").getAsString();
                    String nullValue = ApplicationProperties.getInstance().getNullValue();
                    if (catalogName.equalsIgnoreCase(nullValue)) {
                        catalogName = "";
                    }
                    JsonArray schemas = catalog.getAsJsonArray("schemas");
                    String schemaName = schemas.isEmpty() ? "" : schemas.get(0).getAsJsonObject().get("name").getAsString();
                    if (schemaName.equalsIgnoreCase(nullValue)) {
                        schemaName = "";
                    }

                    dataSource.addProperty("catalog", catalogName);
                    dataSource.addProperty("schema", schemaName);
                    if (requestJson.has("dir")) {
                        dataSource.addProperty("dir", requestJson.get("dir").getAsString());
                    }
                    metadata.add("dataSource", dataSource);
                    if (!catalogName.isEmpty() && !schemaName.isEmpty()) {
                        schemaName = "."+schemaName;
                    }
                    metadata.addProperty("name", catalogName + schemaName);
                }
                return superResultJson.toString();
            }
        }
        return superResult;
    }

    /**
     * Retrieves the validated parameters from the input JSON.
     *
     * @param requestParameterJson 				JSON containing parameters.
     * @return Validated parameters as a JsonObject.
     * @throws IncompleteFormDataException 		If parameters are missing or empty.
     */
    private JsonObject getValidatedParameters(JsonObject requestParameterJson) {
        JsonObject parameters;
        if (!requestParameterJson.has("parameters")) {
            throw new IncompleteFormDataException("The parameter 'parameters' is empty or null");
        }
        parameters = requestParameterJson.getAsJsonObject("parameters");
        if (parameters.entrySet().isEmpty()) {
            throw new IncompleteFormDataException("The parameter 'parameters' is empty or null");
        }
        return parameters;
    }

    /**
     * Creates metadata based on the provided parameters and connection details.
     *
     * @param parameters                	Validated parameters from the form data.
     * @param efwdFileNameWithExtension 	EFWD file name with extension.
     * @param connection                	Connection object.
     * @param connectionJson            	Connection details as a JsonObject.
     * @return JsonObject representing the metadata.
     */
    private JsonObject createMetadata(JsonObject parameters, String efwdFileNameWithExtension, Connection connection,
    		JsonObject connectionJson) {
    	JsonObject metadata = new JsonObject();

        // TODO Need to create MetadataFactory which will take dataSourceDetail
        EfwdMetadataHandler metadataHandler = new EfwdMetadataHandler(parameters, efwdFileNameWithExtension, connection,
                connectionJson, metadata);
        metadataHandler.handleFetchCatalog();

        metadataHandler.handleFetchSchema();

        metadataHandler.handleFetchTables();

        metadataHandler.handleFetchColumns();

        if (metadata.entrySet().isEmpty()) {
            throw new EfwServiceException("Form data has no parameters");
        }
        return metadata;
    }

    /**
     * Retrieves the EFWD file name with extension based on the driver class name.
     *
     * @param driverClass 			Driver class name.
     * @return EFWD file name with extension.
     */
    private String getEFWDFileName(String driverClass) {
        String efwdFileName = JsonUtils.functionsReference(driverClass);
        if (efwdFileName != null) {
            efwdFileName = efwdFileName + "." + JsonUtils.getEfwdExtension();
        }

        return efwdFileName;

    }
    /**
     * Indicates whether the component is thread-safe to cache.
     *
     * @return {@code true} if the component is thread-safe to cache, {@code false} otherwise.
     */
    @Override
    public boolean isThreadSafeToCache() {

        return true;
    }

}
