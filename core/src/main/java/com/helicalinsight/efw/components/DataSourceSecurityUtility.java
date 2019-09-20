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

import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.AccessDeniedException;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcesecurity.IResourceAuthenticator;
import com.helicalinsight.resourcesecurity.ResourceAuthenticator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Somen
 *         Created on 10/4/2016.
 */
public class DataSourceSecurityUtility {
    public static final String NO_ACCESS = "noAccess";
    public static final String EXECUTE = "execute";
    public static final String READ = "read";
    public static final String READ_WRITE = "readWrite";
    public static final String READ_WRITE_DELETE = "readWriteDelete";
    public static final String OWNER = "owner";
    public static final String PUBLIC = "public";


    private static GlobalXmlReaderUtility globalXmlReaderUtility = new GlobalXmlReaderUtility();

    public static Integer getMaxPermissionDataSources(JSONObject connection, String access) {
        if (!connection.has("visible")) {
            connection.accumulate("visible", "true");
        }
        IResourceAuthenticator resourceAuthenticator = ApplicationContextAccessor.getBean(ResourceAuthenticator.class);
        ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean
                (ResourcePermissionLevelsHolder.class);
        Integer maxPermissionOnResource = resourceAuthenticator.maxPermissionOnResource(connection);
        int required = -1;

        if (READ.equalsIgnoreCase(access)) {
            required = resourcePermissionLevelsHolder.readAccessLevel();
        } else if (READ_WRITE.equals(access)) {
            required = resourcePermissionLevelsHolder.readWriteAccessLevel();
        } else if (READ_WRITE_DELETE.equals(access)) {
            required = resourcePermissionLevelsHolder.readWriteDeleteAccessLevel();
        } else if (PUBLIC.equalsIgnoreCase(access)) {
            required = resourcePermissionLevelsHolder.publicResourceAccessLevel();
        } else if (NO_ACCESS.equalsIgnoreCase(access)) {
            required = resourcePermissionLevelsHolder.noAccessLevel();
        } else if (EXECUTE.equalsIgnoreCase(access)) {
            required = resourcePermissionLevelsHolder.executeAccessLevel();
        } else if (OWNER.equalsIgnoreCase(access)) {
            required = resourcePermissionLevelsHolder.ownerAccessLevel();
        }
        if (maxPermissionOnResource < required) {
            return null;
        }
        return maxPermissionOnResource;
    }

    public static void checkEfwdPermission(String id, File efwdFile, String operation) {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject fileAsJson = processor.getJSONObject(efwdFile.toString(), true);
        JSONArray dataSources;
        try {
            JSONObject efwd = fileAsJson.getJSONObject("EFWD");
            dataSources = efwd.getJSONArray("DataSources");
        } catch (Exception e) {
            //The exception is due to the newly created efwds with only DataSources
            //So, EFWD is an array of another array with actual connections
            JSONArray efwd = fileAsJson.getJSONArray("EFWD");
            //Get the inner array at index 0. The info regarding DataSources is missing.
            dataSources = checkInstanceOfJsonArray(efwd);
        }
        checkDataSources(id, dataSources, operation);
    }

    public static JSONArray checkInstanceOfJsonArray(JSONArray efwd) {
        JSONArray dataSources;
        Object unkonwn = efwd.get(0);
        if (unkonwn instanceof JSONArray) {
            dataSources = (JSONArray) unkonwn;
        } else {
            dataSources = new JSONArray();
        }
        return dataSources;
    }

    private static void checkDataSources(String id, JSONArray dataSources, String operation) {
        int counter = 0;
        for (Object object : dataSources) {
            JSONObject connection = JSONObject.fromObject(object);
            String connectionId = connection.getString("@id");
            if(connection.has("globalId")){
                connectionId=connection.getString("globalId");
                List<JSONObject> dataSourcesGlobal = new ArrayList<>();
                globalXmlReaderUtility.addDataSources(dataSourcesGlobal, operation);
                for (JSONObject jsonObject : dataSourcesGlobal) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (data.getString("id").equals(connectionId)) {
                        return;
                    }
                }
                throwException();
            }
            if (id.equalsIgnoreCase(connectionId)) {
                if (getMaxPermissionDataSources(connection, operation) == null) {
                    throwException();
                } else {
                    return;
                }
            }
            counter++;
        }
        if (counter == dataSources.size()) {
            throwResourceNotFoundException();
        }
    }


    public static Object throwException() {
        throw new AccessDeniedException("You may not have sufficient privilege to access the dataSource");
    }

    public static Object throwResourceNotFoundException() {
        throw new ResourceNotFoundException("The  dataSource was not found");
    }

    /*
    * The formJson string has to have 'access' key as set to read,write,noaccess,owner etc. refer static final string declared above
    * */
    public static void isDataSourceAuthenticated(JSONObject formJson) {
        String id = formJson.optString("id");
        String dir = formJson.optString("dir");
        String operation = formJson.optString("operation");

        List<JSONObject> dataSources = new ArrayList<>();
        String accessLevel = formJson.optString("access");
        if (!dir.isEmpty()) {
            String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
            String actualPath = solutionDirectory + File.separator + dir;
       /*     if(SecurityUtils.isTargetReachable(new File(actualPath+File.separator+"random.index"))){
               return;
            }*/
            File efwdFile = ApplicationUtilities.getEfwdFile(dir);


            checkEfwdPermission(id, efwdFile, accessLevel);

        } else {
            if (id.equals("0")) {
                return;
            }
            if (!hasId(id)) {
                throwResourceNotFoundException();
            }
            globalXmlReaderUtility.addDataSources(dataSources, accessLevel);
            for (JSONObject jsonObject : dataSources) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (data.getString("id").equals(id)) {
                    return;
                }
            }
            throwException();
        }

    }

    public static void validateGlobalDataSourceAccessForWriteOperation(String id, String mode) {
        if ("edit".equalsIgnoreCase(mode) || "share".equalsIgnoreCase(mode)) {
            JSONObject globalJson = JsonUtils.getGlobalConnectionsJson();
            List<String> keys = JsonUtils.getKeys(globalJson);

            for (String key : keys) {
                Object theKey = globalJson.get(key);
                if (theKey instanceof JSONArray) {
                    JSONArray jsonArray = globalJson.getJSONArray(key);
                    for (int counter = 0; counter < jsonArray.size(); counter++) {
                        JSONObject aDataSource = jsonArray.getJSONObject(counter);
                        if (validateDataSource(id, aDataSource, mode)) break;
                    }
                } else if (theKey instanceof JSONObject) {
                    JSONObject aDataSource = globalJson.getJSONObject(key);
                    validateDataSource(id, aDataSource, mode);
                }
            }
        }
    }

    public static void validateGlobalDataSourceAccessForDeleteOperation(String id, String mode) {
        if ("delete".equalsIgnoreCase(mode)) {
            JSONObject globalJson = JsonUtils.getGlobalConnectionsJson();
            List<String> keys = JsonUtils.getKeys(globalJson);

            for (String key : keys) {
                Object theKey = globalJson.get(key);
                if (theKey instanceof JSONArray) {
                    JSONArray jsonArray = globalJson.getJSONArray(key);
                    for (int counter = 0; counter < jsonArray.size(); counter++) {
                        JSONObject aDataSource = jsonArray.getJSONObject(counter);
                        if (validateDataSource(id, aDataSource, mode)) break;
                    }
                } else if (theKey instanceof JSONObject) {
                    JSONObject aDataSource = globalJson.getJSONObject(key);
                    validateDataSource(id, aDataSource, mode);
                }
            }
        }
    }

    private static boolean validateDataSource(String id, JSONObject aDataSource, String mode) {
        String theId = aDataSource.getString("@id");
        if (theId.equalsIgnoreCase(id)) {
            String access = "edit".equalsIgnoreCase(mode) ? READ_WRITE : ("delete".equalsIgnoreCase(mode) ? READ_WRITE_DELETE : OWNER);
            if (getMaxPermissionDataSources(aDataSource, access) == null) {
                throwException();
            }
            return true;
        }
        return false;
    }

    public static boolean hasId(String id) {
        try {
            DataSourceUtils.globalIdJson(Integer.parseInt(id));
        } catch (ConfigurationException ex) {
            return false;
        }
        return true;


    }
}
