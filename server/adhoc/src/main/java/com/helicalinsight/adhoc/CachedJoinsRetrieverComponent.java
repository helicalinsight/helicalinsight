package com.helicalinsight.adhoc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataRetrievalException;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataUtils;
import com.helicalinsight.admin.model.ApplicationCache;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.admin.service.DatabaseCacheService;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The {@code CachedJoinsRetrieverComponent} class is an implementation of the {@link IComponent} interface, designed
 * to retrieve cached joins information for metadata table. It interacts with the database cache and metadata cache
 * to efficiently fetch and present join details based on the provided form data.
 *
 * @author Rajesh
 * Created by author on 2/28/2019.
 */
public class CachedJoinsRetrieverComponent implements IComponent {
	
	/**
     * Method responsible to retrieve cached joins information . It processes join details
     * based on the provided form data, handling the auto-triggering of cache refresh for certain operations.
     *
     * @param formData 			 JSON-formatted form data containing parameters in string format.
     * @return A JSON-formatted string containing join information for the requested table.
     */
    @Override
    public String executeComponent(String formData) {

        JsonObject formJsonData = new Gson().fromJson(formData,JsonObject.class);
        if (formJsonData.has("joins")) {
            JsonArray joins = formJsonData.getAsJsonArray("joins");
            JsonArray joinDetails = new JsonArray();
            formJsonData.remove("joins");
            for (int index = 0; index < joins.size(); index++) {
                JsonObject newCopy = new Gson().fromJson(formJsonData,JsonObject.class);
                //newCopy.putAll(joins.get(index).getAsJsonObject());
                JsonObject jsonObject = joins.get(index).getAsJsonObject();
                for (String key : jsonObject.keySet()) {
                    newCopy.add(key, jsonObject.get(key));
                }
                joinDetails.add(new Gson().fromJson(joinProcess(newCopy),JsonObject.class));
            }
            JsonObject response = new JsonObject();
            response.add("joinsDetails", joinDetails);
            return response.toString();
        } else {

            return joinProcess(formJsonData);
        }
    }
    /**
     * Processes join details based on the provided form data, handling the auto-triggering of cache refresh for
     * certain operations. It iterates through joins, prepares join details, and builds the response JSON.
     *
     * @param formJsonData 		 	JSON-formatted form data containing parameters for metadata joins.
     * @return A JSON-formatted string containing join details for the requested ad-hoc report.
     */
    private String joinProcess(JsonObject formJsonData) {
        DataSourceMapping dataSourceKey = new DataSourceMapping();
        JsonArray tablesJsonArray = prepareKey(dataSourceKey, formJsonData);
        DatabaseCacheService databaseCacheService = ApplicationContextAccessor.getBean(DatabaseCacheService.class);
        List<String> addList = new ArrayList<>();
        Map<String, String> idNameMap = CachedColumnsProviderComponent.prepareIdNameMap(dataSourceKey);
        addList.addAll(idNameMap.keySet());
        boolean isAll = false;
        List<String> unClickedTables = databaseCacheService.findUnClickedTables(dataSourceKey, addList);
        JsonObject dataSourceJson = formJsonData.getAsJsonObject("dataSource");
        String catalog = dataSourceJson.get("catalog").getAsString();
        String schema = dataSourceJson.get("schema").getAsString();
        List<String> requiredTable = new ArrayList<>();
        List<String> requiredIds = new ArrayList<>();
        if (tablesJsonArray.size() == 1) {
            String firstTable = tablesJsonArray.get(0).getAsString();
            isAll = firstTable.equals("__all__");
            if (isAll) {
                for (String un : unClickedTables) {
                    String idAfterRemovingQuote = un.substring(1, un.length() - 1);
                    requiredTable.add(idNameMap.get(idAfterRemovingQuote));
                    requiredIds.add(idAfterRemovingQuote);
                }
            } else {
                requiredTable.add(firstTable);
                requiredIds.add(MetadataUtils.getId(catalog, schema, firstTable));
            }

        } else {
            for (int i = 0; i < tablesJsonArray.size(); i++) {
                String tableItem = tablesJsonArray.get(i).getAsString();
                String tableId = MetadataUtils.getId(catalog, schema, tableItem);
                if (unClickedTables.contains("\"" + tableId + "\"")) {
                    requiredTable.add(tableItem);
                    requiredIds.add(tableId);
                }
            }
        }
        if (!requiredTable.isEmpty()) {
            formJsonData.add("tableNames", new Gson().fromJson(requiredTable.toString(),JsonArray.class));
            formJsonData.add("tableIds", new Gson().fromJson(requiredIds.toString(),JsonArray.class));
            JsonObject columnResults = CachedColumnsProviderComponent.getResult(formJsonData);
        }
        List<JsonObject> rawCache = getPartialData(dataSourceKey);
        JsonObject response = prepareJoins(tablesJsonArray, rawCache, isAll);
        return response.get("response").getAsJsonObject().toString();
    }

    /**
     * Retrieves partial data (cached joins information) from the database cache.
     *
     * @param dsMapping 		DataSourceMapping instance specifying the data source.
     * @return A {@code List} JsonObject containing the partial cached data.
     */
    private List<JsonObject> getPartialData(DataSourceMapping dsMapping) {
        DatabaseCacheService databaseCacheService = ApplicationContextAccessor.getBean(DatabaseCacheService.class);
        List<ApplicationCache> allData = databaseCacheService.findApplicationCacheByDataSourceMapping(dsMapping);
        List<JsonObject> jsonArray = new ArrayList<>();
        if (allData != null && !allData.isEmpty()) {
            allData.forEach(item -> {
                Object deSerialize = ApplicationUtilities.unCompressObject(item);
                jsonArray.add(new Gson().fromJson(deSerialize.toString(),JsonObject.class));
            });
        }
        return jsonArray;
    }
    /**
     * Prepares the key for retrieving cached joins information based on the form data.
     *
     * @param dataSourceKey 			 {@code DataSourceMapping} instance to be populated as a key.
     * @param formJsonData 				 JSON-formatted form data containing parameters for the metadata Joins.
     * @return A {@code JsonArray} containing the requested tables for join information.
     */
    private JsonArray prepareKey(DataSourceMapping dataSourceKey, JsonObject formJsonData) {
        JsonObject formDataMetadataJson = formJsonData.getAsJsonObject("metadata");
        JsonArray tablesArray = formDataMetadataJson.getAsJsonArray("table");
        if (tablesArray.size() == 0) {
            throw new MetadataRetrievalException("Please select any table to obtain joins");
        }

        JsonObject dataSourceJson = formJsonData.getAsJsonObject("dataSource");
        String requestedCatalogName = GsonUtility.optString(dataSourceJson, "catalog");
        String requestedSchemaName = GsonUtility.optString(dataSourceJson,"schema");
        dataSourceKey.setCatalog(requestedCatalogName);
        dataSourceKey.setConnectionId(GsonUtility.optInt(dataSourceJson,"id"));
        dataSourceKey.setSchema(requestedSchemaName);
        dataSourceKey.setDir(GsonUtility.optString(dataSourceJson,"dir"));
        dataSourceKey.setType("partial_joins");
        return tablesArray;
    }

    /**
     * Prepares join details based on the requested tables, partial data, and a flag indicating whether to include
     * joins for all tables.
     *
     * @param requestedTablesArray 		JsonArray containing the requested tables.
     * @param listOfJsonData 			{@code List} of {@code JsonObject} containing partial cached data.
     * @param all 						boolean flag indicating whether to include joins for all tables.
     * @return A {@code JsonObject} containing join details for the requested tables.
     */
    private JsonObject prepareJoins(JsonArray requestedTablesArray, List<JsonObject> listOfJsonData, boolean all) {
        JsonObject requiredJson = new JsonObject();
        JsonObject joins = new JsonObject();
        JsonArray addJoinJson = new JsonArray();
        if (listOfJsonData.isEmpty()) {
            throw new MetadataRetrievalException("There is no cache for joins or existing cache for the joins might have been cleared. Please refresh the datasource");
        }
        JsonObject jsonObject = listOfJsonData.get(0).getAsJsonObject();
        JsonObject firstResponseJson = jsonObject.getAsJsonObject("response");
        joins.addProperty("classifier", GsonUtility.optString(firstResponseJson, "classifier"));
        joins.addProperty("name", GsonUtility.optString(firstResponseJson,"name"));
        JsonObject dataSource = firstResponseJson.getAsJsonObject("dataSource");
        joins.add("dataSource", dataSource);


        listOfJsonData.forEach(eachJsonElement -> {
            JsonObject responseJson = eachJsonElement.getAsJsonObject("response");
            if (responseJson.has("joins")) {
//                JSONArray joinsArray = responseJson.getJSONArray("joins");
//                List<JSONObject> listOfJoins = joinsArray;

            	JsonArray joinsArray = responseJson.getAsJsonArray("joins");
            	List<JsonObject> listOfJoins = new ArrayList<>();

            	for (int i = 0; i < joinsArray.size(); i++) {
            	    JsonObject json = joinsArray.get(i).getAsJsonObject();
            	    listOfJoins.add(json);
            	}
                listOfJoins.forEach(eachJoinJson -> {
                	JsonObject leftTable = eachJoinJson.getAsJsonObject("left");
                    leftTable.addProperty("dbId", dataSource.get("dbId").getAsString());
                    JsonObject rightTable = eachJoinJson.getAsJsonObject("right");
                    rightTable.addProperty("dbId", dataSource.get("dbId").getAsString());
                    
                    if (all) {
                        addJoinJson.add(eachJoinJson);
                    } else {
                        String leftTableName = leftTable.get("table").getAsString();
                        String rightTableName = rightTable.get("table").getAsString();
                        if (requestedTablesArray.contains(JsonParser.parseString(leftTableName)) && requestedTablesArray.contains(JsonParser.parseString(rightTableName))) {
                            addJoinJson.add(eachJoinJson);
                        }
                    }

                });
            }
        });

        prepareKeyForAll(dataSource, requestedTablesArray);
        joins.add("joins", addJoinJson);
        requiredJson.add("response", joins);

        return requiredJson;
    }

    /**
     * Prepares additional key information for retrieving joins when joins are requested for all tables.
     *
     * @param dataSource 			  containing data source information.
     * @param requestedTablesArray 	  containing the requested tables.
     */
    private void prepareKeyForAll(JsonObject dataSource, JsonArray requestedTablesArray) {
        JsonObject newDataSource = new JsonObject();
        newDataSource.addProperty("id", dataSource.get("id").getAsString());
        newDataSource.addProperty("catalog", dataSource.get("catalog").getAsString());
        newDataSource.addProperty("schema", dataSource.get("schema").getAsString());
        for (Map.Entry<String, JsonElement> entry : newDataSource.entrySet()) {
        	requestedTablesArray.add(entry.getValue().getAsString());       
        }

       
    }
    /**
     * Determines whether the component is thread-safe to cache.
     * @return {@code true} if the component is thread-safe to cache, {@code false} otherwise.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
