package com.helicalinsight.adhoc.services;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.helicalinsight.adhoc.ViewLabelsRetrievalComponent;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.datasource.EnhancedQueryExecutor;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.jasperintegration.JasperUtils;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;

/**
 * Handles the execution of Jasper queries and prepares field metadata.
 * Created by author on 10/17/2019.
 * @author Rajesh
 */
public class HIJasperQueryExecuter implements IComponent {
    public static final Integer maxRows = 10;
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    /**
     * Executes the Jasper query and prepares field metadata.
     * 
     * @param jsonFormData 		 form data containing mapJson for executing the query.
     * @return A JSON string representing the prepared field metadata.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject dataSetJson = executeQuery(jsonFormData);

        JsonObject fields = prepareFields(dataSetJson);
        return fields.toString();
    }

    private JsonObject executeQuery(String jsonFormData) {
        JsonObject connectionDetails = new Gson().fromJson(jsonFormData,JsonObject.class);

        JsonObject dataSetJson = null;
        JsonObject efwdJson = connectionDetails.getAsJsonObject("mapJson");
        JsonObject connectionPart;
        String dataSourceType;
        String query;
        String hibernateProcessedQuery;
        JsonObject formData = new JsonObject();
        efwdJson.addProperty("maxRows", maxRows);
        if (efwdJson.has("temp_uuid") && !efwdJson.has("dir")) {
            efwdJson.addProperty("isTemp", true);
            // efwdJson.put("dir", TempDirectoryCleaner.getTempDirectory().getAbsolutePath());
            JsonObject efwd = new JsonObject();
            efwd.addProperty("file", efwdJson.get("temp_uuid").getAsString() + "." + JsonUtils.getEfwdExtension());
            efwdJson.add("efwd", efwd);
            dataSourceType = efwdJson.get("type").getAsString();
            EnhancedQueryExecutor queryExecutor;
            if (!"sql.adhoc".equals(dataSourceType)) {
                queryExecutor = new EnhancedQueryExecutor(efwdJson.toString(), applicationProperties);
                connectionPart = queryExecutor.getConnectionPartFromTemp();
                String dialectOfDatabase = getDialect(efwdJson, connectionPart);
                query = queryExecutor.getQueryFromTemp();
                if(connectionDetails.has("limit")){
                    String limit = connectionDetails.get("limit").getAsString();
                    if("full".equalsIgnoreCase(limit)){
                        formData.addProperty("query", query);
                        hibernateProcessedQuery=query;
                    }else{
                        formData.addProperty("query", query);
                        hibernateProcessedQuery = ViewLabelsRetrievalComponent.query(formData, dialectOfDatabase, limit);
                    }
                }else{
                    formData.addProperty("query", query);
                    hibernateProcessedQuery = ViewLabelsRetrievalComponent.query(formData, dialectOfDatabase, maxRows.toString());
                }


                    efwdJson.addProperty("processedLimitQuery", hibernateProcessedQuery);

            }
            queryExecutor = new EnhancedQueryExecutor(efwdJson.toString(), applicationProperties);
            dataSetJson = queryExecutor.getResultSetFromTemp();
        } else {
            EnhancedQueryExecutor queryExecutor = new EnhancedQueryExecutor(efwdJson.toString(), applicationProperties);
            connectionPart = queryExecutor.getConnectionPart();
            prepareEfwdJson(efwdJson, connectionPart);
            dataSourceType = efwdJson.get("type").getAsString();
            if (!"sql.adhoc".equals(dataSourceType)) {
                DataSourceUtils.validate(dataSourceType);
                DataSourceUtils.addExtraDataForWorkflowProcess(efwdJson, dataSourceType);
                efwdJson.addProperty("access", DataSourceSecurityUtility.EXECUTE);
                DriverConnection driverConnection = (DriverConnection) ConnectionProviderFactory.getConnection(efwdJson, dataSourceType);
                String driverClass = driverConnection.getDriverClass();
                String dialectOfDatabase = com.helicalinsight.adhoc.metadata.genericdb.MetadataUtils.dialectOfDatabase(driverClass);
                query = queryExecutor.getQuery();

                formData.addProperty("query", query);
                hibernateProcessedQuery = ViewLabelsRetrievalComponent.query(formData, dialectOfDatabase, maxRows.toString());
                efwdJson.addProperty("processedLimitQuery", hibernateProcessedQuery);
            }
            queryExecutor = new EnhancedQueryExecutor(efwdJson.toString(), applicationProperties);
            dataSetJson = queryExecutor.getResultSet();

        }
        return dataSetJson;
    }

    private String getDialect(JsonObject efwdJson, JsonObject connectionPart) {
        String dataSourceType;
        prepareEfwdJson(efwdJson, connectionPart);
        dataSourceType = efwdJson.get("type").getAsString();
        DataSourceUtils.validate(dataSourceType);
        DataSourceUtils.addExtraDataForWorkflowProcess(efwdJson, dataSourceType);
        efwdJson.addProperty("access", DataSourceSecurityUtility.EXECUTE);
        DriverConnection driverConnection = (DriverConnection) ConnectionProviderFactory.getConnectionFromTemp(efwdJson, dataSourceType);
        String driverClass = driverConnection.getDriverClass();
        return com.helicalinsight.adhoc.metadata.genericdb.MetadataUtils.dialectOfDatabase(driverClass);
    }

    private void prepareEfwdJson(JsonObject efwdJson, JsonObject connectionPart) {
        String type,id;
        if(connectionPart.has("@type")) {
            type=connectionPart.get("@type").getAsString();
            id=connectionPart.get("@id").getAsString();	
        }
        else {
            type=connectionPart.get("type").getAsString();
            id=connectionPart.get("id").getAsString();
        }
        efwdJson.addProperty("type", type);
        efwdJson.addProperty("id", id);
    }

    private void replaceExpressionWithActual(JsonObject connectionDetails) {
        String query = connectionDetails.get("query").getAsString();
        JsonArray parameters = GsonUtility.optJsonArray(connectionDetails,"parameters");
        if (parameters == null)
            return;
        for (int index = 0; index < parameters.size(); index++) {
            JsonObject eachJson = parameters.get(index).getAsJsonObject();
            String parameterName = eachJson.get("name").getAsString();
            String parameterValue = eachJson.get("value").getAsString();
            String parameterWithExpression = "${" + parameterName + "}";
            if (query.contains(parameterWithExpression)) {
                query = query.replace(parameterWithExpression, parameterValue);
            }
        }
        Type type = new TypeToken<HashMap<String, Object>>() {}.getType();
        Map<String, Object> map = new Gson().fromJson(connectionDetails, type);
        map.replace("query", query);
        connectionDetails = new Gson().toJsonTree(map).getAsJsonObject();
    }

    private JsonObject prepareFields(JsonObject jsonObject) {
        JsonArray fieldArray = new JsonArray();
        if (jsonObject.has("labels")) {
            JsonArray labelsArray = jsonObject.getAsJsonArray("labels");
            for (int index = 0; index < labelsArray.size(); index++) {
                JsonObject eachJson = labelsArray.get(index).getAsJsonObject();
                findClass(fieldArray, eachJson);
            }
        }
        if (jsonObject.has("metadata")) {
          /*  JSONArray dataJson = jsonObject.getJSONArray("data");
            List data;
            if (dataJson.size() > 10)
                data = dataJson.subList(0, 10);
            else
                data = dataJson;
            jsonObject.replace("data", data);*/
            JsonArray metadataArray = jsonObject.getAsJsonArray("metadata");
            JsonObject metadataJson = metadataArray.get(0).getAsJsonObject();
            Iterator keys = metadataJson.keySet().iterator();
            while (keys.hasNext()) {
                String eachKey = (String) keys.next();
                JsonObject eachJson = metadataJson.getAsJsonObject(eachKey);
                findClass(fieldArray, eachJson);
            }
            jsonObject.remove("metadata");
            jsonObject.add("field", fieldArray);
        }
        return jsonObject;
    }

    private void findClass(JsonArray fieldArray, JsonObject eachJson) {
        String name = eachJson.get("name").getAsString();
        String type = eachJson.get("type").getAsString();
        JsonObject field = new JsonObject();
        field.addProperty("name", name);
        if (!StringUtils.contains(type, ".")) {
            type = JasperUtils.getEquivalentClass(type).getSimpleName();
        }
        field.addProperty("clazz", type);
        fieldArray.add(field);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
