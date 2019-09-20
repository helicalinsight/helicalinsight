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

package com.helicalinsight.efw.components;

import java.util.List;

import org.springframework.stereotype.Component;

import com.helicalinsight.efw.utility.JsonUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by author on 05-02-2015.
 *
 * @author Rajasekhar
 */
@Component
//This class is used as singleton class, please ensure while adding class variable
public class GlobalXmlReaderUtility {

    public void addDataSources(List<JSONObject> dataSources, String access) {
        JSONObject globalJson = JsonUtils.getGlobalConnectionsJson();
        List<String> keys = JsonUtils.getKeys(globalJson);

        for (String key : keys) {
            Object theKey = globalJson.get(key);
            if (theKey instanceof JSONArray) {
                JSONArray jsonArray = globalJson.getJSONArray(key);
                for (int counter = 0; counter < jsonArray.size(); counter++) {
                    JSONObject aDataSource = jsonArray.getJSONObject(counter);
                    addADataSource(dataSources, aDataSource,access);
                }
            } else if (theKey instanceof JSONObject) {
                JSONObject aDataSource = globalJson.getJSONObject(key);
                addADataSource(dataSources, aDataSource,access);
            }
        }
    }

    private void addADataSource( List<JSONObject> dataSources,  JSONObject aDataSource,String access) {
        Integer maxPermissionOnResource = DataSourceSecurityUtility.getMaxPermissionDataSources(aDataSource,access);
        if (maxPermissionOnResource == null) return;

        JSONObject eachXmlElementJson;
        eachXmlElementJson = new JSONObject();
        String name = aDataSource.getString("@name");
        eachXmlElementJson.accumulate("name", name);

        JSONObject eachElementsData = new JSONObject();
        String id = aDataSource.getString("@id");
        eachElementsData.accumulate("id", id);

        String type = aDataSource.getString("@type");
        eachElementsData.accumulate("type", type);



        if(aDataSource.has("driverClassName")){
            eachXmlElementJson.accumulate("driver",aDataSource.getString("driverClassName"));
        }
        if(aDataSource.has("driverName")){
            eachXmlElementJson.accumulate("driver",aDataSource.getString("driverName"));
        }
        JSONObject dataSourceTypeInfo = EfwdReaderUtility.getDataSourceType(type);
        if(dataSourceTypeInfo !=null){
            eachXmlElementJson.accumulate("dataSourceType", dataSourceTypeInfo.getString("name"));
            eachXmlElementJson.accumulate("classifier", dataSourceTypeInfo.getString("classifier"));
        }
        eachXmlElementJson.accumulate("type", type);
        eachXmlElementJson.accumulate("data", eachElementsData);
        eachXmlElementJson.accumulate("permissionLevel", maxPermissionOnResource);
        eachXmlElementJson.accumulate("dataSourceProvider", aDataSource.getString("dataSourceProvider"));
        dataSources.add(eachXmlElementJson);
    }

}
