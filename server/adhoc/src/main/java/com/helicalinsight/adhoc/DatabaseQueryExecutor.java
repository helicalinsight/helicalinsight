package com.helicalinsight.adhoc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.MultiConnectionMergeAdhocTable;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataDBUtility;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.adhoc.security.SqlQueryMetadataValidator;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.callback.CallBack;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.IJdbcDao;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.EfwdDatasourceUtils;
import com.helicalinsight.efw.utility.ResultSetToJson;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;

/**
 * The {@code DatabaseQueryExecutor} class is an implementation of the {@link IComponent} interface,
 * designed to execute a database query based on the provided form data and return the result in JSON format.
 *
 * @author Rajasekhar
 * Created by author on 02-03-2015.
 */
public class DatabaseQueryExecutor implements IComponent {

    /**
     * Method executes a database query based on the provided form data.
     * The result set is then converted to JSON format and returned as a string.
     *
     * @param jsonFormData form data containing parameters for executing the database query.
     * @return A JSON-formatted string containing the result of the database query.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        ResultSet componentLogic = componentLogic(jsonFormData);
        ResultSetToJson beanSet = ApplicationContextAccessor.getBean(ResultSetToJson.class);
        beanSet.setResultSet(componentLogic);
        JsonObject object = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String str = beanSet.resultSetToJson(false, object).toString();
        return str;
    }

    /**
     * Determines whether the component is thread-safe to cache.
     *
     * @return {@code true} if the component is thread-safe to cache, {@code false} otherwise.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Executes the core logic of the database query based on the provided form data.
     * This method returns a {@code ResultSet} containing the result of the query.
     *
     * @param jsonFormData form data containing parameters for executing the database query.
     * @return A {@code ResultSet} containing the result of the database query.
     */
    @Override
    public ResultSet componentLogic(String jsonFormData) {
        JdbcExecutionContext context = prepareExecutionContext(jsonFormData);
        IJdbcDao jdbcDao = ApplicationContextAccessor.getBean(IJdbcDao.class);
        return jdbcDao.getResultSet(context.connection, context.query);
    }
    
    @Override
    public void componentLogic(String jsonFormData, CallBack<ResultSet> callBack) {
    	JdbcExecutionContext context  = prepareExecutionContext(jsonFormData);
        IJdbcDao jdbcDao = ApplicationContextAccessor.getBean(IJdbcDao.class);
        jdbcDao.streamResult(context.connection, context.query, callBack);
    }
    
    
    private static class JdbcExecutionContext {
    	
    	Connection connection;
    	String query;
    	
    	public JdbcExecutionContext(Connection connection, String query) {
    		this.connection = connection;
    		this.query = query;
    	}
    	
    }
    
    private JdbcExecutionContext prepareExecutionContext(String jsonFormData) {
    	 JsonObject formJson = new Gson().fromJson(jsonFormData, JsonObject.class);

         String type;
         String id;
         String classifier;
         String query;

         if (formJson.has("location") && formJson.has("metadataFileName")) {
             String location = formJson.get("location").getAsString();
             String metadataFileName = formJson.get("metadataFileName").getAsString();

             Metadata metadata = MetadataDBUtility.getMetadata(location, metadataFileName);
             if (metadata == null) {
                 throw new EfwServiceException(
                         "The metadata file " + metadataFileName + " does " + "not exist in the specified location.");
             }
             query = formJson.get("query").getAsString();
             JsonObject metadataServiceData = fetchMetadataFromService(location, metadataFileName);
             if(GsonUtility.optBoolean(formJson, "runtimeView")){
                 /*Map<String, Set<String>> allowedTablesColumns =
                         SqlQueryMetadataValidator.buildAllowedTablesColumns(metadataServiceData);
                 SqlQueryMetadataValidator.validateQueryAgainstMetadata(query, allowedTablesColumns);
*/
                 JsonObject metadataFileJson = GsonUtility.optJsonObject(formJson, "metadataFileJson");
                 SqlQueryMetadataValidator.validateSelectAgainstRestrictedMetadata(
                         query, metadataFileJson, metadataServiceData);
             }
             if (GsonUtility.optBoolean(formJson, "replaceView")) {
                 query = DerivedTableViewReplacer.replaceViewsInQuery(
                         query, metadata, metadataServiceData, location, metadataFileName,
                         metadata.getType(), formJson);
             }

             classifier = metadata.getType();
             ConnectionDetails connection = metadata.getConnectionDetails();
             if (connection.getSubType() != null) {
                 type = connection.getSubType();
                 id = connection.getConnectionId();
             } else {
                 String dir = connection.getDirectory();
                 type = connection.getConnectionType();
                 id = connection.getConnectionId();
                 formJson.addProperty("dir", dir);
             }
             formJson.addProperty("id", id);
             formJson.addProperty("type", type);
             formJson.addProperty("classifier", classifier);
         } else {
             try {
                 query = formJson.get("query").getAsString();
                 type = formJson.get("type").getAsString();
                 id = formJson.get("id").getAsString();
                 classifier = formJson.get("classifier").getAsString();
             } catch (Exception ex) {
                 throw new RequiredParameterIsNullException("Required parameter is null or empty.", ex);
             }
         }

         if (!GlobalJdbcTypeUtils.isJustGlobal(type)) {
             String dir = formJson.get("dir").getAsString();
             if (dir == null || "".equals(dir) || "".equals(dir.trim())) {
                 throw new RequiredParameterIsNullException("Required parameter dir is null or empty.");
             }
             EfwdDatasourceUtils.validatePermission(dir);
         }

         Map<String, String> parameters = new HashMap<>();

         parameters.put("type", type);
         parameters.put("query", query);
         parameters.put("classifier", classifier);
         parameters.put("id", id);

         ControllerUtils.checkForNullsAndEmptyParameters(parameters);
         AdhocServiceUtils.addExtraDataForNormalProcess(formJson, type);

         JsonObject connectionDetails = formJson.getAsJsonObject("metadataFileJson").getAsJsonObject("connectionDetails");
         String cacheMode = GsonUtility.optString(connectionDetails, "fetchMode");
         if ("cache".equalsIgnoreCase(cacheMode)) {
             formJson.addProperty("cache", true);
         }

         formJson.addProperty("access", DataSourceSecurityUtility.EXECUTE);
         DriverConnection databaseConnection = (DriverConnection) ConnectionProviderFactory.getConnection(formJson,
                 type);

         Connection connection = null;
         if (databaseConnection != null) {
             connection = databaseConnection.getConnection();
         }
         return new JdbcExecutionContext(connection, query);
    }
    

    private static JsonObject fetchMetadataFromService(String location, String metadataFileName) {
        IService metadataProvider = FactoryMethodWrapper.getTypedInstance(
                "com.helicalinsight.adhoc.services.MetadataProvider", IService.class);
        JsonObject serviceFormData = new JsonObject();
        serviceFormData.addProperty("location", location);
        serviceFormData.addProperty("metadataFileName", metadataFileName);
        serviceFormData.addProperty("uniqueId", true);
        serviceFormData.addProperty("provideJoins", true);
        String metadataResult = metadataProvider.doService("adhoc", "metadata", "get", serviceFormData.toString());
        JsonObject serviceJson = JsonParser.parseString(metadataResult).getAsJsonObject();
        return ControllerUtils.getDataFromResponse(serviceJson);
    }

}
