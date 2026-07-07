package com.helicalinsight.efw.components;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.AccessDeniedException;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.resourcesecurity.IResourceAuthenticator;
import com.helicalinsight.resourcesecurity.ResourceAuthenticator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Somen
 * Created on 10/4/2016.
 */
public class DataSourceSecurityUtility {
    public static final String NO_ACCESS = "noAccess";
    public static final String EXECUTE = "execute";
    public static final String READ = "read";
    public static final String READ_WRITE = "readWrite";
    public static final String READ_WRITE_DELETE = "readWriteDelete";
    public static final String OWNER = "owner";
    public static final String PUBLIC = "public";
    private static final Logger logger = LoggerFactory.getLogger(DataSourceSecurityUtility.class);

    public static Integer getMaxPermissionDataSources(JSONObject connection, String access) {
        if (!connection.has("visible")) {
            connection.accumulate("visible", "true");
        }
        IResourceAuthenticator resourceAuthenticator = ApplicationContextAccessor.getBean(ResourceAuthenticator.class);
        JsonObject jsonObject = JsonParser.parseString(connection.toString()).getAsJsonObject();
        Integer maxPermissionOnResource = resourceAuthenticator.maxPermissionOnResource(jsonObject);
        int required = getPermissionLevel(access);
        if (maxPermissionOnResource < required) {
            return null;
        }
        return maxPermissionOnResource;
    }

    public static int getPermissionLevel(String access) {
        int required = -1;
        ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean
                (ResourcePermissionLevelsHolder.class);

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
        return required;
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
            JsonArray jsonArray = JsonParser.parseString(efwd.toString()).getAsJsonArray();

            dataSources = checkInstanceOfJsonArray(jsonArray);
        }
        IResourceRule ruleInstance = FactoryMethodWrapper.getTypedInstance("com.helicalinsight.efw.resourceloader.rules.impl.EFWDRule", IResourceRule.class);
        fileAsJson.put("absolutePath", efwdFile.getAbsolutePath());
        fileAsJson.put("id", id);
        fileAsJson.put("dataSources", dataSources);
        fileAsJson.put("operation", operation);
        try {
            JsonObject jsonObject = JsonParser.parseString(fileAsJson.toString()).getAsJsonObject();

            ruleInstance.validateFile(jsonObject);
        } catch (UnSupportedRuleImplementationException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray checkInstanceOfJsonArray(JsonArray efwd) {
        JsonArray dataSources;
        Object unkonwn = efwd.get(0);
        if (unkonwn instanceof JsonArray) {
            dataSources = (JsonArray) unkonwn;
        }else {
            dataSources = new JsonArray();
        }
        return JSONArray.fromObject(dataSources.toString());
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
    public static void isDataSourceAuthenticated(JsonObject formJsons) {
        JSONObject formJson = JSONObject.fromObject(formJsons.toString());
        String id = formJson.optString("id");
        String dir = formJson.optString("dir");
        String type = formJson.optString("type", "");
        if (formJson.has("metadataFileJson") && GlobalJdbcTypeUtils.checkOtherConnections(type)) {
            JSONObject metadataFileJson = formJson.getJSONObject("metadataFileJson");
            JSONObject connectionDetails = metadataFileJson.getJSONObject("connectionDetails");
            dir = connectionDetails.getString("directory");
            id = connectionDetails.getString("connectionId");
        }
        JSONObject efwd = formJson.optJSONObject("efwd");
        String fileName = null;
        if (efwd != null && efwd.has("file")) {
            fileName = efwd.getString("file");
        }

        String accessLevel = formJson.optString("access");
        if (!dir.isEmpty()) {
        	isEFWDAccessible(id);

        } else {
            isGlobalAccessible(id, accessLevel);
        }
    }
    
    private static void isEFWDAccessible(String id) {
    	if(StringUtils.isBlank(id)) return ;
    	EFWDConnectionService efwdService =  ApplicationContextAccessor.getBean(EFWDConnectionService.class);
    	if(Boolean.TRUE.equals(efwdService.isDeleted(id))) {
    		throwResourceNotFoundException();
    	}
    }
    
   	
    public static void isGlobalAccessible(String id, String accessLevel) {
        if (id.equals("") || id.equals("0")) {
            return;
        }
        if (!hasId(id)) {
            throwResourceNotFoundException();
        }

        GlobalDSReaderUtility bean = ApplicationContextAccessor.getBean(GlobalDSReaderUtility.class);
        Map<String, Object> stringObjectMap = bean.addDataSourcesId(accessLevel, Integer.valueOf(id));
        if(stringObjectMap!=null) {
            Map data = (Map) stringObjectMap.get("data");
            if (data.get("id").equals(id)) {
                return;
            }
        }
        throwException();
    }

    public static void isDataSourceAuthenticatedFromTemp(JSONObject formJson) {
        String id = formJson.optString("id");
        String dir = formJson.optString("dir");
        String tempFileName = null;
        if (formJson.has("efwd")) {
            tempFileName = formJson.getJSONObject("efwd").getString("file");
        }
        String accessLevel = formJson.optString("access");
        if (dir.isEmpty()) {
            dir = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
            File efwdFile = null;
            if (tempFileName != null) {
                efwdFile = ApplicationUtilities.getEfwdFileFromTemp(dir, tempFileName);
            } else if (tempFileName == null) {
                efwdFile = ApplicationUtilities.getTempEfwdFile(dir);
            }
            checkEfwdPermission(id, efwdFile, accessLevel);
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

    public static boolean validateDataSource(String id, JSONObject aDataSource, String mode) {
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
    
    public static boolean validateGlobalDS(String id, JSONObject aDataSource, String mode) {
        String theId = aDataSource.getString("@id");
        if (theId.equalsIgnoreCase(id)) {
            String access = "edit".equalsIgnoreCase(mode) ? READ_WRITE : ("delete".equalsIgnoreCase(mode) ? READ_WRITE_DELETE : OWNER);
            int requiredPermission = DataSourceSecurityUtility.getPermissionLevel(access);
            int actualPermission = aDataSource.optInt("permissionLevel");
            if (actualPermission < requiredPermission) {
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
