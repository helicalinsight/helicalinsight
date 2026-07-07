package com.helicalinsight.efw.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.exceptions.AccessDeniedException;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Karthik
 */
@Component
public class GlobalDSReaderUtility {
    private static final Logger logger = LoggerFactory.getLogger(GlobalDSReaderUtility.class);

    @Autowired
    private GlobalConnectionService globalConnectionService;

    public void addDataSources(@NotNull List<Map<String, Object>> dataSources, String access,String driver,String vendorName) {


        if (!JsonUtils.isDSTypeStorageDatabase()) {
            logger.debug("dataSources" + dataSources);
            GlobalXmlReaderUtility globalXmlReaderUtility = ApplicationContextAccessor.getBean
                    (GlobalXmlReaderUtility.class);
            globalXmlReaderUtility.addDataSources(dataSources, access);
            return;
        }

        List<Map<String, Object>> allConnectionsByUser = globalConnectionService.getAllConnectionsFromShared();
        List<Integer> collect = new ArrayList<>();
        Map<Integer, Integer> idPermissionMap = new HashMap<>();
        if (!allConnectionsByUser.isEmpty()) {
            for (Map m : allConnectionsByUser) {
                Integer globalId = (Integer) m.get("id");
                collect.add(globalId);
                idPermissionMap.put(globalId, (Integer) m.get("maxPermission"));
            }
        }

        String userId = AuthenticationUtils.getUserId();
        Integer createdBy = Integer.valueOf(userId);

        List<Map<String, Object>> allConnectionsOwner = globalConnectionService.getAllConnectionOfLoggedInUser(createdBy, collect);
        Map<Integer,Map<String, Object>> allConnOwnerMap =new HashMap<>();
        
        List<Integer> globalIdList = new ArrayList<>();
        Set<String> classNameList = new HashSet<>();
        for (Map globalConnections : allConnectionsOwner) {
            globalIdList.add((Integer) globalConnections.get("globalId"));
            classNameList.add((String) globalConnections.get("dsTypeClass"));
            allConnOwnerMap.put((Integer)globalConnections.get("globalId"), globalConnections);
        }

        List<Map<String, Object>> typeByAllGlobalIds = new ArrayList<>();
        if (!globalIdList.isEmpty()) {
            typeByAllGlobalIds = globalConnectionService.getTypeByAllGlobalIds(globalIdList, classNameList,driver,vendorName);
        }

        Map<Integer, Map<String, Object>> globaTypes = new HashMap<>();
        for (Map<String, Object> obj : typeByAllGlobalIds) {
            globaTypes.put((Integer) obj.get("globalId"), obj);
        }

        Set<Integer> connIds=globaTypes.keySet();
        for (Integer connId:connIds) {
            Integer permission = idPermissionMap.get(connId);
            Map<String, Object> globalConnections=allConnOwnerMap.get(connId);
			if (userId.equals(globalConnections.get("createdBy"))) {
				permission = DataSourceSecurityUtility.getPermissionLevel(DataSourceSecurityUtility.OWNER);
			} else if (globalConnections.get("createdBy") == null) {
				permission = DataSourceSecurityUtility.getPermissionLevel(DataSourceSecurityUtility.PUBLIC);
			}
            addADataSource(dataSources, globalConnections, access, permission, globaTypes);
        }

    }

    public Map<String, Object> addDataSourcesId(String access, Integer forGlobalId) {


        Map<String, Object> allConnectionsByUser = globalConnectionService.getAllConnectionsFromSharedIfId(forGlobalId);

        String userId = AuthenticationUtils.getUserId();

        Map<String, Object> allConnectionsOwner = globalConnectionService.getAConnectionById(forGlobalId);
        if (allConnectionsOwner == null || allConnectionsOwner.isEmpty()) {
            throw new ConfigurationException("The datasource is not found");
        }
        String className = (String) allConnectionsOwner.get("dsTypeClass");

        Map<String, Object> typeByGlobalId = globalConnectionService.getTypeByGlobalId(forGlobalId, className);
        if (typeByGlobalId == null || typeByGlobalId.isEmpty()) {
            throw new EfwServiceException("The globalId details not found");
        }

        Integer permission = (Integer) allConnectionsByUser.get("maxPermission");
        if (userId.equals(allConnectionsOwner.get("createdBy"))) {
            permission = DataSourceSecurityUtility.getPermissionLevel(DataSourceSecurityUtility.OWNER);
        } else if (allConnectionsOwner.get("createdBy") == null) {
            permission = DataSourceSecurityUtility.getPermissionLevel(DataSourceSecurityUtility.PUBLIC);
        }
        int permissionLevel = DataSourceSecurityUtility.getPermissionLevel(access);
        if (permission == null) {
            throw new AccessDeniedException("You may not have sufficient privilege to access the dataSource");
        }
        if (permission < permissionLevel) {
            return null;
        }

        Map<String,String> extraOptions =  globalConnectionService.getExtraOption(forGlobalId);
        String connectionProperties =  DataSourceUtils.buildConnectionProperties(extraOptions);
        typeByGlobalId.put("connectionProperties", connectionProperties);

        if (!extraOptions.isEmpty() && extraOptions.containsKey("dataFile")) {
        	String dataFileName = DataSourceUtils.getFileName("", extraOptions, typeByGlobalId.get("globalId").toString());
        	String newUrl = DataSourceUtils.updateFlatFileUrl(typeByGlobalId.get("url").toString(), dataFileName);
        	typeByGlobalId.put("url", newUrl);
        }

        Map<String, Object> stringObjectMap = putADatasourceItem(allConnectionsOwner, permission, typeByGlobalId);
        return stringObjectMap;

    }

    @NotNull
    private Map<String, Object> putADatasourceItemForList(@NotNull Map<String, Object> globalConnection, int actualPermission, Map<String, Object> dsTypeJson) {
        Map<String, Object> responseItem = new HashMap<>(dsTypeJson);
        responseItem.putAll(globalConnection);

        JSONObject eachElementsData = new JSONObject();
        String id = "" + globalConnection.get("globalId");
        eachElementsData.put("id", id);

        String type = (String) globalConnection.get("type");
        eachElementsData.put("type", type);
        eachElementsData.put("vendorName", globalConnection.get("vendorName"));
        JsonObject dataSourceType = EfwdReaderUtility.getDataSourceType(type);
        JSONObject dataSourceTypeInfo = JSONObject.fromObject(dataSourceType.toString());
        responseItem.put("dataSourceType", dataSourceTypeInfo.getString("name"));
        responseItem.put("classifier",dataSourceTypeInfo.getString("classifier"));
        responseItem.put("data", eachElementsData);

        responseItem.put("permissionLevel", actualPermission);

        if (dsTypeJson.containsKey("driverClassName")) {
            responseItem.put("driver", dsTypeJson.get("driverClassName"));
        }

        if (dsTypeJson.containsKey("driverName")) {
            responseItem.put("driver", dsTypeJson.get("driverName"));
        }
        responseItem.remove("dsTypeClass");
        responseItem.remove("globalId");
        responseItem.remove("createdBy");
        responseItem.remove("isMigrated");
        responseItem.remove("driverClassName");
        responseItem.remove("driverName");
        responseItem.remove("databaseName");

        return responseItem;
    }

    public boolean addADataSource(@NotNull List<Map<String, Object>> dataSources, @NotNull Map<String, Object> globalConnection, String access, int actualPermission, Map<Integer, Map<String, Object>> mapJsons) {
        int permissionLevel = DataSourceSecurityUtility.getPermissionLevel(access);
        if (actualPermission < permissionLevel) {
            return false;
        }

        Integer global_id = (Integer) globalConnection.get("globalId");
        Map<String, Object> dsTypeJson = mapJsons.get(global_id);
        Map<String, Object> responseItem = putADatasourceItemForList(globalConnection, actualPermission, dsTypeJson);
        dataSources.add(responseItem);
        logger.debug("dataSources" + dataSources);
        return true;
    }

    @NotNull
    private Map<String, Object> putADatasourceItem(@NotNull Map<String, Object> globalConnection, int actualPermission, Map<String, Object> dsTypeJson) {
        Map<String, Object> responseItem = new HashMap<>(dsTypeJson);
        responseItem.putAll(globalConnection);

        JSONObject eachElementsData = new JSONObject();
        String id = "" + globalConnection.get("globalId"); 
        eachElementsData.put("id", id);

        String type = (String) globalConnection.get("type");
        eachElementsData.put("type", type);


        JsonObject dataSourceType = EfwdReaderUtility.getDataSourceType(type);
        JSONObject dataSourceTypeInfo = JSONObject.fromObject(dataSourceType.toString());
        responseItem.put("dataSourceType", dataSourceTypeInfo.getString("name"));
        responseItem.put("classifier", dataSourceTypeInfo.getString("classifier"));
        responseItem.put("data", eachElementsData);
        responseItem.put("permissionLevel", actualPermission);
        if (dsTypeJson.containsKey("password")) {
            String decryptedPWD = CipherUtils.decrypt((String) dsTypeJson.get("password"));
            responseItem.put("password", decryptedPWD);
        }

        if (dsTypeJson.containsKey("driverClassName")) {
            responseItem.put("driver", dsTypeJson.get("driverClassName"));
        }

        if (dsTypeJson.containsKey("driverName")) {
            responseItem.put("driver", dsTypeJson.get("driverName"));
        }

        if (globalConnection.containsKey("globalId")) {
            responseItem.put("@id", globalConnection.get("globalId"));
            responseItem.put("id",globalConnection.get("globalId"));
        }
        if (globalConnection.containsKey("name")) {
            responseItem.put("@name", globalConnection.get("name"));
        }
        if (globalConnection.containsKey("type")) {
            responseItem.put("@type", globalConnection.get("type"));
        }
        if (globalConnection.containsKey("baseType")) {
            responseItem.put("@baseType", globalConnection.get("baseType"));
        }
        return responseItem;
    }

}

