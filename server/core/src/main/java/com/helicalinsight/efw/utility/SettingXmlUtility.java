package com.helicalinsight.efw.utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.XmlConfigurationException;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by author on 02-02-2015.
 *
 * @author Rajasekhar
 */
public class SettingXmlUtility {

    /**
     * @return Returns the list of configured DataSources types in setting.xml
     */
    public static List<String> getListOfDataSources() {
        List<String> efwdDataSourcesTypes = new ArrayList<>();
        JSONObject settings = JsonUtils.getSettingsJson();
        JSONArray dataSources = settings.getJSONArray("DataSources");
        for (int count = 0; count < dataSources.size(); count++) {
            String dataSourceType = dataSources.getJSONObject(count).getString("@type");
            efwdDataSourcesTypes.add(dataSourceType);
        }
        return efwdDataSourcesTypes;
    }

    public static JsonObject getDataSourcesJson(Boolean searchHidden) {
        JsonObject jsonOfDataSources = new JsonObject();
        JsonObject settings = JsonUtils.newGetSettingsJson();
        JsonArray dataSources = settings.getAsJsonObject("DataSources").getAsJsonArray("DataSource");
        JsonObject dataSource;
        for (int count = 0; count < dataSources.size(); count++) {
            JsonObject aDataSource = dataSources.get(count).getAsJsonObject();
            dataSource = new JsonObject();
            dataSource.addProperty("type", aDataSource.get("type").getAsString());
            dataSource.addProperty("name", aDataSource.get("name").getAsString());
            dataSource.addProperty("classifier", aDataSource.get("classifier").getAsString());
            if (searchHidden) {
                if (!"true".equalsIgnoreCase(GsonUtility.optString(aDataSource,"hidden"))) {
                    GsonUtility.accumulate(jsonOfDataSources,"dataSources", dataSource);
                }
            } else {
                GsonUtility.accumulate(jsonOfDataSources,"dataSources", dataSource);
            }
        }
        return jsonOfDataSources;
    }

    /**
     * <p>This method will provide list of dataSource present  in setting.xml by checking hidden attribute's value </p>
     *
     * @return JSONObject
     */
    public static JSONObject getFilteredDataSourcesJson() {
        JSONArray jsonOfDataSources = new JSONArray();
        JSONObject settings = JsonUtils.getSettingsJson();
        JSONArray dataSources = settings.getJSONArray("DataSources");
        JSONObject dataSource;
        for (int count = 0; count < dataSources.size(); count++) {
            JSONObject aDataSource = dataSources.getJSONObject(count);
            dataSource = new JSONObject();
            dataSource.put("type", aDataSource.getString("@type"));
            dataSource.put("name", aDataSource.getString("@name"));
            dataSource.put("classifier", aDataSource.getString("@classifier"));
            if (!"true".equalsIgnoreCase(aDataSource.optString("@hidden"))) {
                jsonOfDataSources.add(dataSource);
            }

        }
        JSONObject dataSourcesList = new JSONObject();
        dataSourcesList.put("dataSources", jsonOfDataSources);
        return dataSourcesList;
    }

    /**
     * Returns the key of the extension from the setting.xml
     * <p>
     * If the key is folder then the relevant method result from JsonUtils.getFolderFileExtension
     * value will be returned.
     *
     * @param extension An extension of a file that is configured in xml
     * @return Returns the key of the text value, if the text value matches with
     * any of the configured settings.
     */
    public static String fileExtensionKey(String extension) {
        if (extension == null) {
            throw new IllegalArgumentException("The parameter extension can't be null");
        }

        JSONObject settingsJson = JsonUtils.getSettingsJson();
        JSONObject jsonOfExtensions = settingsJson.getJSONObject("Extentions");

        if ("folder".equals(extension)) {
            return JsonUtils.getFolderFileExtension();
        }

        Iterator<?> iterator = jsonOfExtensions.keys();

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            try {
                JSONObject typeOfExtension = jsonOfExtensions.getJSONObject(key);
                if (typeOfExtension != null) {
                    String textValue = typeOfExtension.getString("#text");
                    if (extension.equalsIgnoreCase(textValue)) {
                        return key;
                    }
                }
            } catch (JSONException ignore) {
                String textValue = jsonOfExtensions.getString(key);
                if (extension.equalsIgnoreCase(textValue)) {
                    return key;
                }
            }
        }

        throw new XmlConfigurationException(String.format("The parameter %s is not configured in " +
                "" + "the application configuration.", extension));
    }

    public static JsonObject getViewHandler() {
        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        JsonObject viewHandleJson = settingsJson.getAsJsonObject("viewHandler");
        if (viewHandleJson == null) {
            throw new XmlConfigurationException(String.format("No view handler is configured in the" + "" +
                    " setting.xml"));
        }
        return viewHandleJson;
    }
}
