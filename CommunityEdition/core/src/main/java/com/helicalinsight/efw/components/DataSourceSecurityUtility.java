/**
 *    Copyright (C) 2013-2017 Helical IT Solutions (http://www.helicalinsight.com).
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

import com.helicalinsight.efw.exceptions.AccessDeniedException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Somen
  */
public class DataSourceSecurityUtility {
    public static final String WRITE = "write";
    public static final String READ = "read";
    private static GlobalXmlReaderUtility globalXmlReaderUtility = new GlobalXmlReaderUtility();

    public static Integer getMaxPermissionDataSources(JSONObject connection, String access) {
        if (!connection.has("visible")) {
            connection.accumulate("visible", "true");
        }
        IResourceAuthenticator IResourceAuthenticator = ApplicationContextAccessor.getBean(ResourceAuthenticator.class);
        ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean
                (ResourcePermissionLevelsHolder.class);
        Integer maxPermissionOnResource = IResourceAuthenticator.maxPermissionOnResource(connection);
        int required = -1;

        if (READ.equalsIgnoreCase(access)) {
            required = resourcePermissionLevelsHolder.readAccessLevel();
        } else if (WRITE.equals(access)) {
            required = resourcePermissionLevelsHolder.readWriteAccessLevel();
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
            dataSources = efwd.getJSONArray(0);
        }
        checkDataSources(id, dataSources, operation);
    }

    private static void checkDataSources(String id, JSONArray dataSources, String operation) {
        int counter = 0;
        for (Object object : dataSources) {
            JSONObject connection = JSONObject.fromObject(object);
            String connectionId = connection.getString("@id");
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
        throw new ResourceNotFoundException("The  datasource was not found");
    }

    public static void isDataSourceAuthenticated(JSONObject formJson) {
        String id = formJson.getString("id");
        String dir = formJson.optString("dir");

        List<JSONObject> dataSources = new ArrayList<>();
        if (!dir.isEmpty()) {
            File efwdFile = ApplicationUtilities.getEfwdFile(dir);
            checkEfwdPermission(id, efwdFile, READ);

        } else {
            if (!hasId(id)) {
                throwResourceNotFoundException();
            }
            globalXmlReaderUtility.addDataSources(dataSources);
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
        if (mode.equalsIgnoreCase("edit") || mode.equalsIgnoreCase("share")) {
            JSONObject globalJson = JsonUtils.getGlobalConnectionsJson();
            List<String> keys = JsonUtils.getKeys(globalJson);

            for (String key : keys) {
                Object theKey = globalJson.get(key);
                if (theKey instanceof JSONArray) {
                    JSONArray jsonArray = globalJson.getJSONArray(key);
                    for (int counter = 0; counter < jsonArray.size(); counter++) {
                        JSONObject aDataSource = jsonArray.getJSONObject(counter);
                        if (validateDataSource(id, aDataSource)) break;
                    }
                } else if (theKey instanceof JSONObject) {
                    JSONObject aDataSource = globalJson.getJSONObject(key);
                    validateDataSource(id, aDataSource);
                }
            }
        }
    }

    private static boolean validateDataSource(String id, JSONObject aDataSource) {
        String theId = aDataSource.getString("@id");
        if (theId.equalsIgnoreCase(id)) {
            if (getMaxPermissionDataSources(aDataSource, WRITE) == null) {
                throwException();
            }
            return true;
        }
        return false;
    }

    public static boolean hasId(String id) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(new File(JsonUtils.getGlobalConnectionsPath()));

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            Element element = (Element) xpath.evaluate("//*[@id='" + id + "']", document, XPathConstants.NODE);
            return element != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new EfwServiceException("There was an error while parsing " + ex.getMessage());
        }
    }
}
