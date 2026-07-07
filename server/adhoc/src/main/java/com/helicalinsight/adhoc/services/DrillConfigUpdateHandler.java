package com.helicalinsight.adhoc.services;
/**
 * Created by author on 09/09/2018
 *
 * @author Rajesh
 *
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.customauth.DataSourceEncrypt;
import com.helicalinsight.datasource.AbstractFileOperationsOverNetwork;
import com.helicalinsight.datasource.FileOperationOverNetworkFactory;
import com.helicalinsight.datasource.managed.jaxb.DrillConfig;
import com.helicalinsight.datasource.managed.jaxb.DrillStorageLocations;
import com.helicalinsight.datasource.managed.jaxb.StorageLocation;
import com.helicalinsight.datasource.managed.jaxb.UrlConfig;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.DrillConfigUrlContext;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DrillConfigUpdateHandler {
    private final static Logger logger = LoggerFactory.getLogger(DrillConfigUpdateHandler.class);

    public static String updateDrillConfig(String drillConfigJsonString) {
        JSONObject drillConfigJson = JSONObject.fromObject(drillConfigJsonString);
        updateStorageConfiguration(drillConfigJson);
        File xml = new File(JsonUtils.getDrillConfigPath());
        DrillConfig drillConfig = JaxbUtils.unMarshal(DrillConfig.class, xml);
        if(drillConfig.getUrlConfig()!=null) {
            String pwd=drillConfig.getUrlConfig().getPassword();
            if(pwd!=null && !pwd.equals("")&&!pwd.equals("[]"))
                drillConfig.getUrlConfig().setPassword(DataSourceEncrypt.decrypt(pwd));
        }
        String path = writeDrillConfig(drillConfig, drillConfigJson);
        JaxbUtils.marshal1(drillConfig, xml);


        //String location = ApplicationProperties.getInstance().getSystemDirectory() + File.separator + path;

        updateStorageInDrillServer(path, drillConfig.getStorageImpl());
        drillConfig = JaxbUtils.unMarshal(DrillConfig.class, xml);
         path = writeDrillConfig(drillConfig, drillConfigJson);
        JaxbUtils.marshal1(drillConfig, xml);

        return "Updated the changes successfully";
    }

    private static void updateStorageConfiguration(JSONObject drillJson) {
        JSONObject drillAsJson = DrillConfigUrlContext.getDrill();
        JSONObject fileSystemConfiguration = drillJson.optJSONObject("fileSystemConfiguration");
        JSONObject enabledTypes = drillJson.optJSONObject("enabledTypes");

        XMLSerializer serializer = new XMLSerializer();
        if (fileSystemConfiguration != null && !fileSystemConfiguration.isEmpty()) {
           String ps= fileSystemConfiguration.getJSONObject("sftp").getString("password");
            String pwd=ps!="" || ps!=null ?DataSourceEncrypt.encrypt(ps):"";
            fileSystemConfiguration.getJSONObject("sftp").put("password", pwd);
            drillAsJson.put("fileSystemConfiguration", fileSystemConfiguration);
        }
        if (enabledTypes != null && !enabledTypes.isEmpty()) {
            enabledTypes.put("@mandatory", "true");
            drillAsJson.put("enabledTypes", enabledTypes);
        }
        serializer.setTypeHintsEnabled(false);
        serializer.setForceTopLevelObject(true);
        serializer.setElementName("storage");
        serializer.setRootName("drill");
        String modelXml = serializer.write(drillAsJson);
        String pretty = prettyFormat(modelXml, "5");
        writeStringIntoFile(pretty, JsonUtils.getDrillConfigPath());
    }

    public static String prettyFormat(String input, String indent) {
        Source xmlInput = new StreamSource(new StringReader(input));
        StringWriter stringWriter = new StringWriter();
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", indent);
            transformer.transform(xmlInput, new StreamResult(stringWriter));

            return stringWriter.toString().trim();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String writeDrillConfig(DrillConfig drillConfig, JSONObject drillConfigJson) {
        JSONObject urlConfigJson = drillConfigJson.optJSONObject("urlConfig");

        JSONArray drillStorageLocation = drillConfigJson.optJSONArray("drillStorageLocation");
        String enabled = drillConfigJson.optString("enabled");
        String storageImpl = drillConfigJson.optString("storageImpl");
        if (!enabled.isEmpty()) {
            drillConfig.setEnabled(enabled);
        }
        if (!storageImpl.isEmpty()) {
            drillConfig.setStorageImpl(storageImpl);
        }

        UrlConfig urlConfig = drillConfig.getUrlConfig();

        String host = urlConfigJson.optString("host");
        String port = urlConfigJson.optString("port");
        String extraParam = urlConfigJson.optString("extraParam");
        String username = urlConfigJson.optString("username");
        if("[]".equals(username)) username="";
        String password = urlConfigJson.optString("password");
        if("[]".equals(password)) password="";
        String dbPort = urlConfigJson.optString("dbPort");
        String https = urlConfigJson.optString("https");
        String httpsState = urlConfigJson.optString("httpsState");
        String zookeeperPort = urlConfigJson.optString("zookeeperPort");
        String distributedMode = urlConfigJson.optString("distributedMode");

        String securityEnabled = urlConfigJson.optString("securityEnabled");
        String securityMode = urlConfigJson.optString("securityMode");
        String securityCheckType = urlConfigJson.optString("securityCheckType");
        urlConfig.setHost(sanitizeValue(host));
        urlConfig.setPort(sanitizeValue(port));
        urlConfig.setExtraParam(sanitizeValue(extraParam));
        urlConfig.setUserName(sanitizeValue(username));
        urlConfig.setPassword(password!=""?DataSourceEncrypt.encrypt(sanitizeValue(password)):"");
        urlConfig.setDbPort(sanitizeValue(dbPort));
        urlConfig.setHttps(sanitizeValue(https));
        urlConfig.setHttpsState(sanitizeValue(httpsState));
        urlConfig.setSecurityEnabled(sanitizeValue(securityEnabled));
        urlConfig.setSecurityMode(sanitizeValue(securityMode));
        urlConfig.setSecurityCheckType(sanitizeValue(securityCheckType));
        urlConfig.setZookeeperPort(sanitizeValue(zookeeperPort));
        urlConfig.setDistributedMode(sanitizeValue(distributedMode));
        return updateStorageLocation(drillConfig, drillStorageLocation);

    }

    private static String sanitizeValue(String host) {
        return host.isEmpty() ? "" : host;
    }

    private static String updateStorageLocation(DrillConfig drillConfig, JSONArray drillStorageLocation) {
        DrillStorageLocations drillStorageLocations = drillConfig.getDrillStorageLocations();

        if (drillStorageLocations == null) {
            drillStorageLocations = ApplicationContextAccessor.getBean(DrillStorageLocations.class);
        }
        List<StorageLocation> storageDetails = drillStorageLocations.getStorageDetails();
        if (storageDetails == null) {
            storageDetails = new ArrayList<>();
        }

        return setPath(drillConfig, drillStorageLocation, drillStorageLocations, storageDetails);
    }

    private static String setPath(DrillConfig drillConfig, JSONArray drillStorageLocation, DrillStorageLocations drillStorageLocations, List<StorageLocation> storageDetails) {
        for (int arrayIndex = 0; arrayIndex < drillStorageLocation.size(); arrayIndex++) {
            JSONObject arrayItem = drillStorageLocation.getJSONObject(arrayIndex);
            String path = arrayItem.optString("path");
            if (path != null) {
                StorageLocation storageLocation;
                if (!storageDetails.isEmpty()) {
                    storageLocation = storageDetails.get(0);
                    storageLocation.setPath(path);
                } else {
                    storageLocation = ApplicationContextAccessor.getBean(StorageLocation.class);
                    storageLocation.setPath(path);
                    storageDetails.add(0, storageLocation);

                }
                drillStorageLocations.setStorageDetails(storageDetails);
                drillConfig.setDrillStorageLocations(drillStorageLocations);
                return path;
            }
        }
        return "";
    }

    private static JSONObject prepareHIDW(String storageLocation) {
        JSONObject hiDWJson = new JSONObject();
        hiDWJson.put("location", storageLocation);
        hiDWJson.put("writable", true);
        hiDWJson.put("defaultInputFormat", "null");
        hiDWJson.put("allowAccessOutsideWorkspace", "true");
        return hiDWJson;
    }


    public static void updateStorageInDrillServer(String location, String storageImpl) {
        AbstractFileOperationsOverNetwork fileOperationHandlerClass = FileOperationOverNetworkFactory.getFileOperationHandlerClass(storageImpl);
        JSONObject paramJson = new JSONObject();
        paramJson.put("createFolderPath", location);
        JsonObject js=JsonParser.parseString(paramJson.toString()).getAsJsonObject();
        fileOperationHandlerClass.createFolder( js);
        location = js.get("createFolderPath").getAsString();
        String drillStorageUrl = DrillCsvDataSourceCreator.getUrlOfDrill();
        String dfsUrl = drillStorageUrl + "/storage/dfs.json";
        logger.error("Dfs url is "+dfsUrl);
        String dfsJson = DrillCsvDataSourceCreator.drillRestApiCall(dfsUrl, "GET", null);
        if (dfsJson == null) {
            throw new EfwServiceException("The drill storage could not be fetched. Please check the details are correct");
        }
        JSONObject item = JSONObject.fromObject(dfsJson);
        JSONObject configJson = item.getJSONObject("config");
        if (configJson.isEmpty()) {
            configJson = new JSONObject();
        }
        syncEnabledTypes(configJson);


        fileOperationHandlerClass.setDrillConfigConnection(JsonParser.parseString(configJson.toString()).getAsJsonObject());

        JSONObject workspaces = configJson.getJSONObject("workspaces");
        if (workspaces.has("hidw")) {
            JSONObject hidw = workspaces.getJSONObject("hidw");
            hidw.put("location", location);
        } else {
            workspaces.put("hidw", prepareHIDW(location));
        }
        item.put("config", configJson);
        String postResult = DrillCsvDataSourceCreator.drillRestApiCall(dfsUrl, "POST", item.toString());


    }

    private static void syncEnabledTypes(JSONObject configJson) {
        if (configJson.isEmpty()) {
            configJson.putAll(JSONObject.fromObject("{\n" +
                    "    \"type\" : \"file\",\n" +
                    "    \"connection\" : \"file:///\",\n" +
                    "    \"config\" : null,\n" +
                    "    \"workspaces\" : {\n" +
                    "      \"root\" : {\n" +
                    "        \"location\" : \"/\",\n" +
                    "        \"writable\" : false,\n" +
                    "        \"defaultInputFormat\" : null,\n" +
                    "        \"allowAccessOutsideWorkspace\" : false\n" +
                    "      }\n" +
                    "     \n" +
                    "    },\n" +
                    "    \"formats\" : {\n" +
                    "      \"csv\" : {\n" +
                    "        \"type\" : \"text\",\n" +
                    "        \"extensions\" : [ \"csv\" ],\n" +
                    "        \"extractHeader\" : true,\n" +
                    "        \"delimiter\" : \",\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    \"enabled\" : true\n" +
                    "  }\n"));
        }
        JSONObject dfsFormatsJson = configJson.getJSONObject("formats");
        JSONObject enabledTypes = new JSONObject();
        enabledTypes.put("@mandatory", "true");
        Iterator keySet = dfsFormatsJson.keys();


        while (keySet.hasNext()) {
            String key = (String) keySet.next();

            JSONObject eachJsonElement = dfsFormatsJson.getJSONObject(key);
            Set keySetInnerElement = eachJsonElement.keySet();
            JSONObject outer = new JSONObject();
            JSONObject inner = new JSONObject();

            for (Object eachKey : keySetInnerElement) {

                String keyInner = "@" + eachKey;
                inner.put(keyInner, JsonUtils.applyDrillConfigXmlValueForAttribute(eachJsonElement.get(eachKey.toString())));
                if (eachJsonElement.has("extensions")) {
                    inner.put("@extensions", "." + eachJsonElement.getJSONArray("extensions").get(0));
                }


            }
            if (!(key.equals("image") || key.equals("httpd"))) {
                outer.put("config", inner);
                outer.put("@fileUpload", !inner.optString("@type").equals("httpd"));
                enabledTypes.put(key, outer);
            }


        }
        String types[] = DrillConfigUrlContext.getExtractHeaders().split(",");

        for (String type : types) {
            JSONObject inner = dfsFormatsJson.optJSONObject(type);
            if (inner != null && !inner.isEmpty())
                inner.put("extractHeader", true);
        }


        JSONObject request = new JSONObject();
        request.put("enabledTypes", enabledTypes);
        updateStorageConfiguration(request);
    }


    public static String writeStringIntoFile(String data, String path) {
        FileOutputStream fop = null;
        File file;
        try {
            file = new File(path);
            fop = new FileOutputStream(file);
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = data.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (IOException ex) {
        } finally {
            try {
                if (fop != null) {
                    fop.flush();
                    fop.close();
                    System.gc();
                }
            } catch (IOException ex) {
            }
        }
        return "ok";
    }


}
