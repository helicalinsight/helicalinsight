package com.helicalinsight.efw.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.dto.EfwdConnDTO;
import com.helicalinsight.admin.dto.PlainConnDTO;
import com.helicalinsight.admin.model.HIEfwdConnSecurity;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.helper.EfwDatasourceHelper;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.datasource.utils.EFWDDBHandler;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.SettingXmlUtility;

/**
 * @author Rajasekhar
 * @author Muqtar Ahmed
 * @author Prashansa
 * @author Somen
 */
public class EfwdReaderUtility {

    private static final Logger logger = LoggerFactory.getLogger(EfwdReaderUtility.class);
    @Nullable
    private JsonArray extensions;
	

    public EfwdReaderUtility(@Nullable JsonArray extensions) {
        if (extensions == null) {
            throw new IllegalArgumentException("Extentions can not be null.");
        }
        this.extensions = extensions;
    }

    @NotNull
    public List<ObjectNode> getAllEfwdConnections(@Nullable String subType) {
        if (null != subType && !subType.isEmpty()) {
            logger.debug(
                    "The subType parameter is null" + ". It is required to list the required type of data sources in "
                            + "case of efwd. Reading all the data sources of all types.");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Received a request to read all the connections in all efwd files, " + ""
                    + "which are accessible to the currently logged in user.");
        }

        Set<PlainConnDTO> connections = new HashSet<>(findAllConnections(subType));        
        ArrayList<ObjectNode> connectionList = new ArrayList<>();
        EfwDatasourceHelper utility = ApplicationContextAccessor.getBean(EfwDatasourceHelper.class);
        connections.forEach(connection -> {
            utility.prepareResponseObject(connection, connectionList, subType);
        });
        return connectionList;
    }


    
    @Deprecated
    public static JsonObject getDataSourceType(String type) {
        switch (type) {
            case GlobalJdbcType.TYPE:
            case GlobalJdbcType.NON_POOLED:
            case GlobalJdbcType.DYNAMIC_DATASOURCE:
            case GlobalJdbcType.STATIC_DATASOURCE:
            case GlobalJdbcType.NOSQL_DATASOURCE:
            case GlobalJdbcType.NOSQL_DB_DATASOURCE:
                type = "global.jdbc";

        }

        JsonObject dataSourceJson = SettingXmlUtility.getDataSourcesJson(false);
        JsonArray dataSources = dataSourceJson.getAsJsonArray("dataSources");
        JsonObject dataSourceTypesInfo = new JsonObject();
        for (int index = 0; index < dataSources.size(); index++) {
            JsonObject aDataSource = dataSources.get(index).getAsJsonObject();
            if (aDataSource.get("type").getAsString().equals(type)) {
                dataSourceTypesInfo.addProperty("classifier", aDataSource.get("classifier").getAsString());
                dataSourceTypesInfo.addProperty("name", aDataSource.get("name").getAsString());
                break;
            }

        }

        return dataSourceTypesInfo;

    }

    // new method

    public List<PlainConnDTO> findAllConnections(String type) {

        EFWDDBHandler handler = ApplicationContextAccessor.getBean(EFWDDBHandler.class);
        return handler.findAllConnectionsByType(type);
    }
    
    /**
     * Share Related code.
     */
    
    public void addDataSources(@NotNull List<ObjectNode> dataSources,String type,String access) {
    	
    	EFWDConnectionService efwdConnectionService = ApplicationContextAccessor.getBean(EFWDConnectionService.class);
    	HIResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
       
        List<HIEfwdConnSecurity> allConnectionsByUser = efwdConnectionService.getAllConnectionsFromShared(type);
        List<Integer> collect = new ArrayList<>();
        Map<Integer, Integer> idPermissionMap = new HashMap<>();
        if (!allConnectionsByUser.isEmpty()) {
            for (HIEfwdConnSecurity security : allConnectionsByUser) {
                Integer efwdId = security.getId();
                collect.add(efwdId);
                idPermissionMap.put(efwdId, security.getPermission());
            }
        }

        String userId = AuthenticationUtils.getUserId();
        Integer currentLoggedInUserId = Integer.valueOf(userId);
        List<EfwdConnDTO> allConnectionsOwner = efwdConnectionService.getAllConnectionOfLoggedInUser(currentLoggedInUserId, collect,type);
        List<EfwdConnDTO> connectionFromFolders =  new ArrayList<>();
        Map<Integer , Integer> securityMap =  hiResourceServiceDB.getSecurityMap();
        List<Integer> foldersResourceIds =  hiResourceServiceDB.getChildrenResourceByParentIds(new ArrayList<>(securityMap.keySet()));
        List<EfwdConnDTO> connectionsOfCurrentFolder = efwdConnectionService.findConnectionByResourceIds(foldersResourceIds,Boolean.FALSE,Boolean.TRUE);
        if(!type.equalsIgnoreCase("all")) {
            connectionsOfCurrentFolder = connectionsOfCurrentFolder.stream().filter(item -> item.getType().equalsIgnoreCase(type)).collect(Collectors.toList());
            connectionFromFolders.addAll(connectionsOfCurrentFolder);
        }
        else connectionFromFolders.addAll(connectionsOfCurrentFolder);
        allConnectionsOwner.addAll(connectionFromFolders);
        Set<EfwdConnDTO> allConnections = new HashSet<>(allConnectionsOwner);
        List<Integer> efwdIdList = new ArrayList<>();
        Set<String> efwdTypeList = new HashSet<>();
        for (EfwdConnDTO efwdConnection : allConnections) {
            efwdIdList.add(efwdConnection.getId());
            efwdTypeList.add(efwdConnection.getType());
        }
        for (EfwdConnDTO connection : allConnections) {
        	efwdIdList.add(connection.getId());
            Integer permission = idPermissionMap.get(connection.getId());
				if (userId.equals(connection.getCreatedBy())) {
					permission = DataSourceSecurityUtility.getPermissionLevel(DataSourceSecurityUtility.OWNER);
				} else if (connection.getCreatedBy() == null) {
					permission = DataSourceSecurityUtility.getPermissionLevel(DataSourceSecurityUtility.PUBLIC);
				} else if(permission == null) {
					permission = securityMap.get(connection.getResource().getResourceId());
					if ( permission == null) {
						// considering it as public folder.
						permission = DataSourceSecurityUtility.getPermissionLevel(DataSourceSecurityUtility.PUBLIC);
					}
				}            
            addADataSource(dataSources, connection, access, permission);
        }
    }
    public boolean addADataSource(@NotNull List<ObjectNode> dataSources, @NotNull EfwdConnDTO connection , String access, int actualPermission) {
        int permissionLevel = DataSourceSecurityUtility.getPermissionLevel(access);
        if (actualPermission < permissionLevel) {
            return false;
        }
        dataSources.addAll(putADatasourceItemForList(connection, actualPermission));
        logger.debug("dataSources" + dataSources);
        return true;
    }
    
    
    @NotNull
    private List<ObjectNode> putADatasourceItemForList(EfwdConnDTO connection , int actualPermission) {
    	List<PlainConnDTO> plainConns = connection.getPlainConnections();
    	List<ObjectNode> dataSource = new ArrayList<>();
    	
    	plainConns.forEach(con -> {
    		dataSource.add(prepareData(con, actualPermission));
    	});
        return dataSource;
    }
   
    
    @NotNull
    public ObjectNode prepareData(@NotNull PlainConnDTO plain, int permission) {
    	ObjectNode response = JsonNodeFactory.instance.objectNode();
    	response.put("permissionLevel", permission);
    	response.put("driver", plain.getDriver());
    	ObjectNode data = JsonNodeFactory.instance.objectNode();
    	data.put("dir", plain.getDirectory());
    	data.put("isPublic", plain.getIsPublic());
    	data.put("driverName", plain.getDriver());
    	data.put("type",plain.getType());
    	data.put("id",plain.getEfwdId());
    	data.put("userName", plain.getUserName());
    	data.put("password",plain.getPass());
    	if(StringUtils.isNotBlank(plain.getCondition())) {
    		data.put("condition", plain.getCondition());
    	}
    	data.put("jdbcUrl", plain.getUrl());
    	response.put("name",plain.getName());
    	response.put("classifier", "efwd");
    	response.put("type",plain.getType());
    	response.put("baseType",plain.getType());
    	response.putPOJO("data", data);
    	response.put("dataSourceType",getDsType(plain.getType()));
    	return response;
    }
    
    private String getDsType(String type) {
    	if (GlobalJdbcType.PLAIN_JDBC.equalsIgnoreCase(type)) return "Plain Jdbc DataSource";
    	else if ( GlobalJdbcType.GROOVY_DATASOURCE.equalsIgnoreCase(type)) return "";
    	else if ( GlobalJdbcType.MANAGED_GROOVY_DATASOURCE.equalsIgnoreCase(type)) return "";
    	else throw new EfwdServiceException("Invalid Datasource type");
    }
}
