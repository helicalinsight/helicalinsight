/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.utility;

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

    public static JSONObject getDataSourcesJson(Boolean searchHidden) {
        JSONObject jsonOfDataSources = new JSONObject();
        JSONObject settings = JsonUtils.getSettingsJson();
        JSONArray dataSources = settings.getJSONArray("DataSources");
        JSONObject dataSource;
        for (int count = 0; count < dataSources.size(); count++) {
            JSONObject aDataSource = dataSources.getJSONObject(count);
            dataSource = new JSONObject();
            dataSource.put("type", aDataSource.getString("@type"));
            dataSource.put("name", aDataSource.getString("@name"));
            dataSource.put("classifier", aDataSource.getString("@classifier"));
            if (searchHidden) {
                if (!"true".equalsIgnoreCase(aDataSource.optString("@hidden"))) {
                    jsonOfDataSources.accumulate("dataSources", dataSource);
                }
            } else {
                jsonOfDataSources.accumulate("dataSources", dataSource);
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

    public static JSONObject getViewHandler() {
        JSONObject settingsJson = JsonUtils.getSettingsJson();
        JSONObject viewHandleJson = settingsJson.getJSONObject("viewHandler");
        if (viewHandleJson == null) {
            throw new XmlConfigurationException(String.format("No view handler is configured in the" + "" +
                    " setting.xml"));
        }
        return viewHandleJson;
    }
}
