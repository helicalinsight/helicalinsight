package com.helicalinsight.efw.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.JsonUtils;
import java.util.List;

/**
 * @author Rajesh
 *         Created by helical019 on 1/7/2019.
 */
public class FormDataTemplateUtils {

    public static String getCatSchemFormData(String datasourceId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", datasourceId);
        jsonObject.addProperty("type", "dynamicDataSource");
        JsonObject parameters = new JsonObject();
        parameters.addProperty("fetchCatalogs", true);
        parameters.addProperty("fetchSchemas", true);
        parameters.addProperty("view", "tree");
        jsonObject.add("parameters", parameters);

        return jsonObject.toString();
    }

    public static String getTableFormData(String datasourceId, String type, String catalogName, JsonArray schemaArray) {


        JsonObject formObject = new JsonObject();
        formObject.addProperty("id", datasourceId);
        formObject.addProperty("type", type);

        JsonObject parameters = new JsonObject();
        parameters.addProperty("fetchTables", true);

        JsonObject fetchDataJson = new JsonObject();
        fetchDataJson.addProperty("catalog", catalogName);
        fetchDataJson.add("schemas", schemaArray);

        JsonArray fetchData = new JsonArray();
        fetchData.add(fetchDataJson);

        parameters.add("fetchData", fetchData);
        formObject.add("parameters", parameters);
        return formObject.toString();
    }

    public static String getColumnFormData(String datasourceId, String type, String catalogName, String schemaName, JsonArray tableArray) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", datasourceId);
        jsonObject.addProperty("type", type);
        JsonObject parameters = new JsonObject();
        parameters.addProperty("fetchColumns", true);
        JsonArray fetchData = new JsonArray();
        JsonObject fetchDataJsonObject = new JsonObject();
        fetchDataJsonObject.addProperty("catalog", catalogName);
        JsonArray schemaArray = new JsonArray();
        JsonObject schemaArrayJsonObj = new JsonObject();
        if (schemaName != null) {
            schemaArrayJsonObj.addProperty("name", schemaName);
        }

        schemaArrayJsonObj.add("tables", tableArray);
        schemaArray.add(schemaArrayJsonObj);
        fetchDataJsonObject.add("schemas", schemaArray);
        fetchData.add(fetchDataJsonObject);
        parameters.add("fetchData", fetchData);
        jsonObject.add("parameters", parameters);
        return jsonObject.toString();
    }

    public static JsonArray removeUnwantedCatalog(JsonArray catalogArray) {
        List<String> ignoreCatalogSchema = JsonUtils.getEncryptedParametersList("ignoreCatalogSchema");
        JsonArray clonedJsonArray;
        JsonObject jsonElement = new JsonObject();
        removeElementNamesFromJsonArray(jsonElement, ignoreCatalogSchema, catalogArray, "catalogs");

        clonedJsonArray = jsonElement.getAsJsonArray("catalogs");
        for (int arrInd = 0; arrInd < clonedJsonArray.size(); arrInd++) {
            JsonObject jsonObject = clonedJsonArray.get(arrInd).getAsJsonObject();
            removeSchemaName(jsonObject, ignoreCatalogSchema);
        }
        return clonedJsonArray;
    }

    private static void removeSchemaName(JsonObject jsonElement, List<String> ignoreCatalogSchema) {
        if (jsonElement.has("schemas")) {
            JsonArray schemaArray = jsonElement.getAsJsonArray("schemas");
            removeElementNamesFromJsonArray(jsonElement, ignoreCatalogSchema, schemaArray, "schemas");

        }

    }

    private static void removeElementNamesFromJsonArray(JsonObject jsonElement, List<String> ignoreCatalogSchema, JsonArray schemaArray, String schemaCatalog) {
        JsonArray newSchemaArray = new JsonArray();
        for (int index = 0; index < schemaArray.size(); index++) {
            JsonObject jsonObject = schemaArray.get(index).getAsJsonObject();
            String removeName = jsonObject.get("name").getAsString();
            if (!ignoreCatalogSchema.contains(removeName)) {
                newSchemaArray.add(jsonObject);
            }
        }
        jsonElement.add(schemaCatalog, newSchemaArray);
    }
}

