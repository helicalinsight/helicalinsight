package com.helicalinsight.adhoc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.services.QueryGeneratorService;
import com.helicalinsight.admin.service.ComponentFactory;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.callback.CallBack;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.AppStatistics;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.ResultSetToJson;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import static com.helicalinsight.efw.serviceframework.ServiceUtils.componentClass;
import static com.helicalinsight.efw.serviceframework.ServiceUtils.componentJson;

/**
 * The {@code AdhocCacheManager} class is responsible for managing caching related to ad-hoc queries.
 * It extends the {@code CacheManager} class and implements specific methods related to caching and query handling.
 *
 * @author Somen
 * @since 5/30/2015
 */
@Component
@Scope("prototype")
public class AdhocCacheManager extends CacheManager {

    private String componentClass;

    private String componentJson;

    private JsonObject requestParameterJson;

    private String connectionId = "0";

    private String classifier;

    public void setRequestParameterJson(JsonObject requestParameterJson) {
        this.requestParameterJson = requestParameterJson;
    }

    @Override
    public String getRequestData() {
        return null;
    }
    /**
     * Sets the request data for the cache manager.
     * @param formData 		 form data provides service, serviceType, etc.
     */
    @Override
    public void setRequestData(String formData) {
        this.requestParameterJson = JsonParser.parseString(formData).getAsJsonObject();
        String type = requestParameterJson.get("requestType").getAsString();
        String serviceType = requestParameterJson.get("serviceType").getAsString();
        String service = requestParameterJson.get("service").getAsString();
        addComponentJson(type, serviceType, service);
    }
    /**
     * Method is responsible to get component from xml, using  provided type, service type, and service.
     *
     * @param type        		 type of the component.
     * @param serviceType 		 service type of the component.
     * @param service     		 service associated with the component.
     */
    public void addComponentJson(String type, String serviceType, String service) {
        QueryGeneratorService queryGeneratorService = new QueryGeneratorService();
        requestParameterJson = queryGeneratorService.newFindConfigurationSettings(requestParameterJson.toString());
        if (requestParameterJson != null) {
            classifier = requestParameterJson.get("classifier").getAsString();
            checkPermission();
            JsonObject connectionDetails = requestParameterJson.getAsJsonObject("metadataFileJson");
            boolean cached = GsonUtility.optBoolean(connectionDetails,"isCached");
            if (cached && AppStatistics.isSPARK_STARTED() && AppStatistics.isMASTER_STARTED()) {
                service = "fetchDataSpark";
                requestParameterJson.addProperty("service", service);
            }
            componentJson = componentJson(type, serviceType, service, classifier);
        }
        this.componentClass = componentClass(componentJson);
    }
    /**
     * Checks permission for the data source connection.
     */
    private void checkPermission() {
        JsonObject connectionDetails = requestParameterJson.getAsJsonObject("metadataFileJson").getAsJsonObject
                ("connectionDetails");
        String connectionType = connectionDetails.get("connectionType").getAsString();
        this.connectionId = connectionDetails.get("connectionId").getAsString();
        JsonObject formData = new JsonObject();
        if (!(GlobalJdbcType.TYPE.equalsIgnoreCase(connectionType) || GlobalJdbcType.NOSQL_DB_DATASOURCE.equalsIgnoreCase(connectionType))) {
            String directory = connectionDetails.get("directory").getAsString();
            formData.addProperty("dir", directory);
        }
        formData.addProperty("id", connectionId);
        formData.addProperty("access", DataSourceSecurityUtility.EXECUTE);
        DataSourceSecurityUtility.isDataSourceAuthenticated(formData);
    }
    /**
     * Returns the connection file path from the formData which gives location,metadataFileName, uniqueId.
     *
     * @return the connection file path.
     */
    @Override
    public String getConnectionFilePath() {
        String location;
        String metadataFile;
        if (this.requestParameterJson.has("location")) {
            location = this.requestParameterJson.get("location").getAsString();
        } else {
            location = "";
        }
        if (this.requestParameterJson.has("metadataFileName")) {
            metadataFile = this.requestParameterJson.get("metadataFileName").getAsString();
        } else {
            metadataFile = GsonUtility.optString(requestParameterJson,"uniqueId");
        }

        return location + File.separator + metadataFile;
    }

    public String getDirectory() {
        return GsonUtility.optString(this.requestParameterJson,"location");
    }
    /**
     * Returns SQL query based on the provided connection type. if connection type is groovy it returns groovy related query.
     *
     * @param connectionType 		 connection type.
     * @return the SQL query.
     */
    @Override
    public String getQuery(String connectionType) {

        DatabaseQueryGenerator databaseQueryGenerator = new DatabaseQueryGenerator();
        String query = JsonParser.parseString(databaseQueryGenerator.executeComponent(requestParameterJson.toString())).getAsJsonObject()
                .get("query").getAsString();
        query = appendExtraConditionInQueryIfGroovy(query);
        return query;
    }

    public Long getConnectionId() {
        return Long.valueOf(this.connectionId);
    }

    @Override
    public Integer getMapId() {
        return 0;
    }
    /**
     * Returns ResultSet data from the database based on the provided query.
     *
     * @param query 		 database query.
     * @return result set obtained from the database.
     */
    public ResultSet getDataFromDatabase(String query) {
        IComponent databaseQueryExecutor = prepareIComponent(query);
        ResultSet resultSet = (ResultSet) databaseQueryExecutor.componentLogic(requestParameterJson.toString());
        return resultSet;
    }
    
    
    private IComponent prepareIComponent(String query) {
    	if (!requestParameterJson.has("query")) {
            if (query.contains("##__")) {
                query = query.substring(0, query.indexOf("##__"));
            }
            GsonUtility.accumulate(requestParameterJson,"query", query);
        }
        String service = "executeQuery";
        JsonObject connectionDetails = requestParameterJson.getAsJsonObject("metadataFileJson");
        boolean cached = GsonUtility.optBoolean(connectionDetails,"isCached");

        if (cached && AppStatistics.isSPARK_STARTED() && AppStatistics.isMASTER_STARTED()) {
            service = "fetchDataSpark";
            requestParameterJson.addProperty("service", service);
        }
        String executorComponent = componentClass(componentJson("adhoc", "report", service, classifier));
        return  ComponentFactory.getComponentInstance(executorComponent);
    }
    
    
    @Override
    public void streamDataFromDatabase(String query, CallBack<ResultSet> callBack) {
        IComponent databaseQueryExecutor = prepareIComponent(query);
        databaseQueryExecutor.componentLogic(requestParameterJson.toString(), callBack);
    }
    
    
    
    
    
    
    /**
     * Serves the cached content to the client.
     *
     * @param request      	 object  provides modified cache.
     * @param response     	 response object sets the content type of object.
     * @param fileContent  	 content to be served.
     * @return {@code true} if successful, {@code false} otherwise.
     */
    @Override
    public boolean serveCachedContent(HttpServletRequest request, HttpServletResponse response,
                                      Object fileContent) {
        PrintWriter out = null;
        try {
            ResultSetToJson objectToJson = ApplicationContextAccessor.getBean(ResultSetToJson.class);
            objectToJson.setResultSet((ResultSet) fileContent);
            JsonObject requestParameterJson = this.requestParameterJson;

            JsonObject jsonObject1 = JsonParser.parseString(requestParameterJson.toString()).getAsJsonObject();
            JsonObject jsonObject = objectToJson.resultSetToJson(true, jsonObject1);


            Object lastModifiedCache = request.getAttribute("lastModifiedCache");
            if (lastModifiedCache != null)
                jsonObject.addProperty("lastModified", (Long) lastModifiedCache);
            response.setContentType(ControllerUtils.defaultContentType());
            response.setCharacterEncoding(ApplicationUtilities.getEncoding());
            out = response.getWriter();
            out.print(jsonObject.toString());
        } catch (IOException ignore) {
        } finally {
            ApplicationUtilities.closeResource(out);
        }
        return true;
    }
    /**
     * Returns the connection type based on the provided connection ID.
     *
     * @param connectionId 			 connection ID.
     * @return connection type.
     */
    @Override
    public String getConnectionType(Long connectionId) {
        return requestParameterJson.get("classifier").getAsString();
    }
    /**
     * it adds extra conditions to the query for Groovy connection type.
     *
     * @param query 		 original query.
     * @return modified query.
     */
    private String appendExtraConditionInQueryIfGroovy(String query) {
        JsonObject metadataFileJson = GsonUtility.optJsonObject(requestParameterJson,"metadataFileJson");
        if (metadataFileJson != null && !metadataFileJson.entrySet().isEmpty()) {
            JsonObject connectionDetailsJson = GsonUtility.optJsonObject(metadataFileJson,"connectionDetails");
            if (connectionDetailsJson != null && !connectionDetailsJson.entrySet().isEmpty()) {
                String connectionType = GsonUtility.optStringValue(connectionDetailsJson,"connectionType", null);
                String connectionId = GsonUtility.optStringValue(connectionDetailsJson,"connectionId", null);
                String directory = GsonUtility.optStringValue(connectionDetailsJson,"directory", null);
                Boolean isGroovy = GlobalJdbcTypeUtils.isTypeGroovy(connectionType);
                if (connectionType != null && isGroovy && connectionId != null) {
                    query += "##__" + GlobalJdbcTypeUtils.getSwitchedConnection(connectionId,connectionType).toString() + "__##";

                }
            }
        }

        return query;
    }
}
