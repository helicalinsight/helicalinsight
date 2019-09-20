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

import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.resourceloader.DirectoryLoaderProxy;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.SettingXmlUtility;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * @author Rajasekhar
 * @author Muqtar Ahmed
 * @author Prashansa
 * @author Somen
 */
public class EfwdReaderUtility {

    private static final Logger logger = LoggerFactory.getLogger(EfwdReaderUtility.class);
    private final String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
    private JSONArray extensions;

    public EfwdReaderUtility(JSONArray extensions) {
        if (extensions == null) {
            throw new IllegalArgumentException("Extentions can not be null.");
        }
        this.extensions = extensions;
    }

    public List<JSONObject> getAllEfwdConnections(String subType) {
        if (subType == null) {
            logger.debug("The subType parameter is null" +
                    ". It is required to list the required type of data sources in " +
                    "case of efwd. Reading all the data sources of all types.");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Received a request to read all the connections in all efwd files, " +
                    "" + "which are accessible to the currently logged in user.");
        }

        Map<String, String> mapOfEfwds = prepareMapOfEfwd();
        List<JSONObject> connections = new ArrayList<>();
        Set<Map.Entry<String, String>> entries = mapOfEfwds.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String directory = entry.getKey();
            try {
                addEfwdConnectionsToList(directory, entry.getValue(), connections, subType);
            } catch (JSONException ignore) {
                logger.error("A file named " + directory + " has improper xml file of efwd. The " +
                        "exception is ", ignore);
            }
        }
        return connections;
    }

    private Map<String, String> prepareMapOfEfwd() {
        String efwdExtension = JsonUtils.getEfwdExtension();
        List<String> listOfDirectories = getRelativePaths();
        Map<String, String> map = new HashMap<>();
        for (String directory : listOfDirectories) {
            File directoryPath = new File(directory);
            File[] files = directoryPath.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(("." + efwdExtension))) {
                        map.put(directory, file.getName());
                    }
                }
            }
        }
        return map;
    }

    private void addEfwdConnectionsToList(String efwdPath, String efwdFileName,
                                          List<JSONObject> connections, String subType) {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject fileAsJson = processor.getJSONObject(efwdPath + File.separator + efwdFileName, true);
        try {
            JSONObject efwd = fileAsJson.getJSONObject("EFWD");
            JSONArray dataSources = efwd.getJSONArray("DataSources");
            for (Object object : dataSources) {
                JSONObject connection = JSONObject.fromObject(object);
                accumulate(connection, efwdPath, connections, subType);
            }
        } catch (Exception e) {
            //The exception is due to the newly created efwds with only DataSources
            //So, EFWD is an array of another array with actual connections
            JSONArray efwd = fileAsJson.getJSONArray("EFWD");
            //Get the inner array at index 0. The info regarding DataSources is missing.

            JSONArray innerArray =  DataSourceSecurityUtility.checkInstanceOfJsonArray(efwd);

            for (Object object : innerArray) {
                JSONObject connection = JSONObject.fromObject(object);
                accumulate(connection, efwdPath, connections, subType);
            }
        }
    }

    private List<String> getRelativePaths() {
        List<String> list = new ArrayList<>();
        List<String> directoriesList;
        if (this.extensions != null) {
            directoriesList = new DirectoryLoaderProxy(this.extensions.subList(0,
                    this.extensions.size())).getAccessibleListOfDirectories(true);
            for (String folder : directoriesList) {
                list.add(this.solutionDirectory + File.separator + folder);
            }
        }
        return list;
    }

    private void accumulate(JSONObject connection, String efwdPath, List<JSONObject> connections,
                            String subType) {
        if (subType == null) {
            //SubType is null. So, accumulate all.
            accumulate(connection, efwdPath, connections);
        } else {
            accumulateOnlyRequiredSubType(connection, efwdPath, connections, subType);
        }
    }

    private void accumulate(JSONObject connection, String efwdPath, List<JSONObject> connections) {
        Integer maxPermissionOnResource = DataSourceSecurityUtility.getMaxPermissionDataSources(connection,
                DataSourceSecurityUtility.READ);
        if (maxPermissionOnResource == null) {
            return;
        }

        JSONObject eachConnection = new JSONObject();
        String id = connection.getString("@id");
        String type = connection.getString("@type");
        JSONObject dataSourceTypeInfo = getDataSourceType(type);
        try {
            String name = connection.getString("@name");
            eachConnection.accumulate("name", name);
            eachConnection.accumulate("type", type);
        } catch (Exception ignore) {
            eachConnection.accumulate("name", id);
        }
        JSONObject eachConnectionData = new JSONObject();
        eachConnectionData.accumulate("id", id);
        eachConnectionData.accumulate("dir", StringUtils.remove(efwdPath, this.solutionDirectory + File.separator));
        eachConnectionData.accumulate("type", type);
        if (connection.has("Driver")) {
            eachConnectionData.accumulate("driver", connection.getString("Driver"));
            eachConnection.accumulate("driver", connection.getString("Driver"));
        }else if (connection.has("driverName")) {
            eachConnectionData.accumulate("driver", connection.getString("driverName"));
            eachConnection.accumulate("driver", connection.getString("driverName"));
        }
        if (dataSourceTypeInfo != null) {
            eachConnection.accumulate("dataSourceType", dataSourceTypeInfo.getString("name"));
            eachConnection.accumulate("classifier", dataSourceTypeInfo.getString("classifier"));
        }
        eachConnection.accumulate("permissionLevel", maxPermissionOnResource);
        eachConnection.accumulate("data", eachConnectionData);
        connections.add(eachConnection);
    }

    private void accumulateOnlyRequiredSubType(JSONObject connection, String efwdPath,
                                               List<JSONObject> connections, String subType) {
        String type = connection.getString("@type");
        //Accumulate only if subType and type are equal. Else ignore.
        if (subType.equalsIgnoreCase(type)) {
            accumulate(connection, efwdPath, connections);
        }


    }

    public static JSONObject getDataSourceType(String type) {
        switch (type) {
            case GlobalJdbcType.TYPE:
            case GlobalJdbcType.NON_POOLED:
            case GlobalJdbcType.DYNAMIC_DATASOURCE:
            case GlobalJdbcType.STATIC_DATASOURCE:
                type = "global.jdbc";

        }

        JSONObject dataSourceJson = SettingXmlUtility.getDataSourcesJson(false);
        JSONArray dataSources = dataSourceJson.getJSONArray("dataSources");
        JSONObject dataSourceTypesInfo = new JSONObject();
        for (int index = 0; index < dataSources.size(); index++) {
            JSONObject aDataSource = dataSources.getJSONObject(index);
            if (aDataSource.getString("type").equals(type)) {
                dataSourceTypesInfo.put("classifier", aDataSource.getString("classifier"));
                dataSourceTypesInfo.put("name", aDataSource.getString("name"));
                break;
            }

        }

        return dataSourceTypesInfo;

    }
}
