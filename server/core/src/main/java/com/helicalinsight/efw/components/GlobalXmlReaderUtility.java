package com.helicalinsight.efw.components;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by author on 05-02-2015.
 *
 * @author Rajasekhar
 */
@Component
//This class is used as singleton class, please ensure while adding class variable
public class GlobalXmlReaderUtility {


    public void addDataSources(@NotNull List dataSources, String access) {
        JsonObject globalJson = JsonUtils.newGetGlobalConnectionsJson();
        List<String> keys = JsonUtils.getKeys(globalJson);
        for (String key : keys) {
            Object theKey = globalJson.get(key);
            if (theKey instanceof JsonArray) {
                JsonArray jsonArray = globalJson.getAsJsonArray(key);
                for (int counter = 0; counter < jsonArray.size(); counter++) {
                    JsonObject aDataSource = jsonArray.get(counter).getAsJsonObject();
                    addADataSource(dataSources, aDataSource,access);
                }
            } else if (theKey instanceof JsonObject) {
                JsonObject aDataSource = globalJson.getAsJsonObject(key);
                addADataSource(dataSources, aDataSource,access);
            }
        }
    }

    private void addADataSource(@NotNull List<Map> dataSources, @NotNull JsonObject aDataSource,String access) {
        Integer maxPermissionOnResource = DataSourceSecurityUtility.getMaxPermissionDataSources(JSONObject.fromObject(aDataSource.toString()),access);
        if (maxPermissionOnResource == null) return;

        JsonObject eachXmlElementJson;
        eachXmlElementJson = new JsonObject();
        String name = aDataSource.get("name").getAsString();
        eachXmlElementJson.addProperty("name", name);

        JsonObject eachElementsData = new JsonObject();
        String id = aDataSource.get("id").getAsString();
        eachElementsData.addProperty("id", id);

        String type = aDataSource.get("type").getAsString();
        eachElementsData.addProperty("type", type);
       
       

        if(aDataSource.has("driverClassName")){
            eachXmlElementJson.addProperty("driver",aDataSource.get("driverClassName").getAsString());
        }
        if(aDataSource.has("driverName")){
            eachXmlElementJson.addProperty("driver",aDataSource.get("driverName").getAsString());
        }
        String dataSourceTypeInfo = EfwdReaderUtility.getDataSourceType(type).toString();
        JsonObject dataSourceInfo = new Gson().fromJson(dataSourceTypeInfo,JsonObject.class);
        if(dataSourceInfo !=null){
        eachXmlElementJson.addProperty("dataSourceType", dataSourceInfo.get("name").getAsString());
        eachXmlElementJson.addProperty("classifier", dataSourceInfo.get("classifier").getAsString());
        }
        eachXmlElementJson.addProperty("type", type);
        eachXmlElementJson.add("data", eachElementsData);
        eachXmlElementJson.addProperty("permissionLevel", maxPermissionOnResource);
        eachXmlElementJson.addProperty("dataSourceProvider", aDataSource.get("dataSourceProvider").getAsString());
        Map<String,Object> resourceMap  = new Gson().fromJson(eachXmlElementJson, new TypeToken<Map<String, Object>>() {}.getType());
        dataSources.add(resourceMap);
    }
  
}
