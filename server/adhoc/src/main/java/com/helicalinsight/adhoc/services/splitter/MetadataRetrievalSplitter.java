package com.helicalinsight.adhoc.services.splitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.components.EfwdReader;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import com.helicalinsight.efw.utility.SplitterUtils;
import com.helicalinsight.parallelprocessor.api.AbstractSplitter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MetadataRetrievalSplitter
 * This class splits the metadata retrieval process for datasources and tables.
 * 
 * Author: Created by helical021 on 1/24/2019.
 */
@Component
@Scope("prototype")
public class MetadataRetrievalSplitter extends AbstractSplitter {
    
	private String dir = null;
    private String id = null;
    
    /**
     * Retrieves metadata for datasources and tables.
     * @return A list of JsonObject containing metadata information
     */
    public List<JsonObject> newPrepareFormDataList() {


        String id = this.newActualFormData.get("id").getAsString();
        String dir = GsonUtility.optString(this.newActualFormData, "dir");
        if (StringUtils.isNotBlank(dir)) {
            EfwdReader efwdReader = new EfwdReader();
            String result = efwdReader.executeComponent(newActualFormData.toString());
            //JSONObject resultJson = JSONObject.fromObject(result);
            ObjectNode resultJson = null;
            try {
             resultJson = new ObjectMapper().readValue(result, ObjectNode.class);
            }
            catch (Exception e) {
				e.printStackTrace();
			}
            
            this.connectionName = resultJson.get("name") != null ? resultJson.get("name").asText():resultJson.get("@name").asText();
            this.dir = dir;


        } else {
            String json = DataSourceUtils.globalIdJson(Integer.valueOf(id));
            JsonObject resultJson = new Gson().fromJson(json,JsonObject.class);
            this.connectionName = resultJson.get("name").getAsString();
        }
        this.id = id;
        List<JsonObject> formDataList = new ArrayList<>();
        if (newActualFormData.has("parameters")) {
            JsonObject parameters = newActualFormData.getAsJsonObject("parameters");
            if (parameters.has("fetchColumns") && !parameters.has("fetchJoins")) {

                JsonArray fetchData = parameters.getAsJsonArray("fetchData");
                JsonObject fetchDataFirstItem = fetchData.get(0).getAsJsonObject();
                JsonArray schemas = fetchDataFirstItem.getAsJsonArray("schemas");
                JsonObject schemasFirstItem = schemas.get(0).getAsJsonObject();
                JsonArray tableArray = schemasFirstItem.getAsJsonArray("tables");
                Map<String, String> mapFromClasspathPropertiesFile = ConfigurationFileReader.mapFromClasspathPropertiesFile
                        ("project.properties");
                String threshold = mapFromClasspathPropertiesFile.get("metadataThreadsTableThreshold");
                Integer tableThreshold = Integer.valueOf(threshold);
                Double doubleTableThreshold = Double.valueOf(threshold);
                final int sizeOfTables = tableArray.size();
                if (sizeOfTables > tableThreshold) {
                    for (int i = 0; i < (int) Math.ceil(sizeOfTables / doubleTableThreshold); i++) {
                        //List<String> subTables = tableArray.subList(((i) * tableThreshold), Math.min((i + 1) * tableThreshold, sizeOfTables));
                    	List<String> subTables = new ArrayList<>();
                    	for (int j = ((i) * tableThreshold); j <Math.min((i + 1) * tableThreshold, sizeOfTables); j++) {
                            JsonElement element = tableArray.get(j);
                            subTables.add(element.getAsString());
                        }
                        
                        JsonObject newList = new Gson().fromJson(this.newActualFormData,JsonObject.class);
                        reduceFormData(newList, subTables);
                        formDataList.add(newList);
                    }
                } else {
                    formDataList.add(this.newActualFormData);
                }

            } else {
                formDataList.add(this.newActualFormData);
            }
        }
        return formDataList;
    }
    /**
     * Prepare and return a cache object containing necessary metadata information
     */
    @Override
    public Object getCacheObject() {
        DataSourceMapping dataSourceKey = new DataSourceMapping();
        dataSourceKey.setConnectionName(connectionName);

        if (this.newActualFormData.has("parameters")) {
            JsonObject parametersJson = this.newActualFormData.getAsJsonObject("parameters");
            String type = setDataSourceType(parametersJson);
            if (this.newActualFormData.has("tableNameIndex")) {
                dataSourceKey.setTableNameIndex(this.newActualFormData.get("tableNameIndex").getAsString());
                dataSourceKey.setTableIdIndex(this.newActualFormData.get("tableIdIndex").getAsString());
                type = this.newActualFormData.get("partial").getAsString();
            }

            dataSourceKey.setType(type);

            if (parametersJson.has("fetchData")) {
                JsonArray fetchData = parametersJson.getAsJsonArray("fetchData");
                JsonObject catalog = fetchData.get(0).getAsJsonObject();
                if (catalog.has("catalog")) {
                    dataSourceKey.setCatalog(catalog.get("catalog").getAsString());
                }
                if (catalog.has("schemas")) {
                    JsonArray schemas = catalog.getAsJsonArray("schemas");
                    if (schemas != null && !schemas.isEmpty()) {
                        JsonObject schema = schemas.get(0).getAsJsonObject();
                        if (schema.has("name")) {
                            dataSourceKey.setSchema(schema.get("name").getAsString());
                        }
                    }
                }
            }

        }

        dataSourceKey.setConnectionId(Integer.valueOf(id));
        dataSourceKey.setDir(dir);

        return dataSourceKey;
    }

    private String setDataSourceType(JsonObject parametersJson) {
        if (parametersJson.has("fetchCatalogs"))
            return "catschema";
        else if (parametersJson.has("fetchTables"))
            return "table";
        else if (parametersJson.has("fetchJoins"))
            return "joins";
        else if (parametersJson.has("fetchColumns"))
            return "column";
        return "";
    }

    
    private void reduceFormData(JsonObject newList, List<String> subTables) {
        JsonObject parameters = newList.getAsJsonObject("parameters");
        JsonArray fetchData = parameters.getAsJsonArray("fetchData");
        JsonObject fetchDataFirstItem = fetchData.get(0).getAsJsonObject();
        JsonArray schemas = fetchDataFirstItem.getAsJsonArray("schemas");
        JsonObject schemasFirstItem = schemas.get(0).getAsJsonObject();
        schemasFirstItem.add("tables", new Gson().fromJson(subTables.toString(),JsonArray.class));
    }
   
    /**
     * Prepares a unique identifier for metadata retrieval.
     * 
     * @param type 				 type of metadata retrieval
     * @param serviceType 		 service type
     * @param service 			 service name
     * @return The unique identifier for metadata retrieval
     */
    public String newPrepareUniqueId(String type, String serviceType, String service) {
        String supersUniqueId = super.newPrepareUniqueId(type, serviceType, service);
        if (this.newActualFormData.has("type")) {
            String classifierType = this.newActualFormData.get("type").getAsString();
            if (GlobalJdbcTypeUtils.isTypeGroovy(classifierType) ) {
                ObjectNode switchedConnection = GlobalJdbcTypeUtils.getSwitchedConnection( this.newActualFormData.get("id").getAsString(), classifierType);
                return supersUniqueId + SplitterUtils.prepareServiceId(switchedConnection.toString());
            }
        }
        return supersUniqueId;
    }

}
