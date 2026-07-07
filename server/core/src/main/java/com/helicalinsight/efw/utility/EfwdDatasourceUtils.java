package com.helicalinsight.efw.utility;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.efw.components.DsTypeHandlerFactory;
import com.helicalinsight.efw.components.EfwdDataSourceHandler;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class EfwdDatasourceUtils {


	private static final Logger logger = LoggerFactory.getLogger(EfwdDatasourceUtils.class);


	@NotNull
	public static ObjectNode getEfwdConnection(String id, String type) {
		EfwdDataSourceHandler handler = DsTypeHandlerFactory.handler(type);
		return  handler.readDS(id, type);
	}
	@NotNull
	public static ObjectNode getContent(String id, String type) {
		EfwdDataSourceHandler handler = DsTypeHandlerFactory.handler(type);
		return handler.readDsContent(id, type);
	}
	/**
	 * using gson
	 * getEfwdConnection(@NotNull JsonObject fileAsJson, @NotNull String id, @NotNull String type)
	 * @param fileAsJson
	 * @param id
	 * @param type
	 * @return
	 */
	@NotNull
	public static JsonObject getEfwdConnection(@NotNull JsonObject fileAsJson, @NotNull String id, @NotNull String type) {
		EfwdDataSourceHandler handler = DsTypeHandlerFactory.handler(type);
		try {
			JsonObject dataSources = fileAsJson.getAsJsonObject("DataSources");
			
				JsonObject connection = dataSources.getAsJsonObject("Connection");
				if (check(id, type, connection)) {
					return handler.readDSFile(id, type, connection);
				}
			
		} catch (Exception ex) {


			log(ex);
			if(!(ex instanceof JsonSyntaxException)){
				throw ex;
			}
			//The exception is due to the newly created efwds with only DataSources
			//So, EFWD is an array of another array with actual connections
			JsonArray efwd = fileAsJson.getAsJsonArray("EFWD");
			//Get the inner array at index 0. The info regarding DataSources is missing.
			Object unkObject = efwd.get(0);
			if(unkObject instanceof JsonArray) {
				JsonArray innerArray = (JsonArray) unkObject;
				for (Object object : innerArray) {
					JsonObject connection = new Gson().fromJson((JsonObject)object,JsonObject.class);
					if (check(id, type, connection)) {
						return handler.readDSFile(id, type, connection);
					}
				}
			}
		}
		throw new EfwServiceException(String.format("Invalid request. There is no connection with the given id " +
				"%s, and type %s", id, type));
	}

	public static void log(Exception ex) {
		if (logger.isDebugEnabled()) {
			logger.debug("There was an exception. The exception is " + ExceptionUtils.getRootCauseMessage(ex));
		}
	}
	/**
	 * check(@NotNull String id, @NotNull String type, @NotNull JSONObject connection)
	 * @deprecated
	 * This method is no longer acceptable 
	 * <p> Use {@link EfwdDataSourceUtils#check(@NotNull String id, @NotNull String type, @NotNull JsonObject connection)} instead.</p>
	 * @param id
	 * @param type
	 * @param connection
	 * @return
	 */
	private static boolean check(@NotNull String id, @NotNull String type, @NotNull JSONObject connection) {
		String theId = connection.getString("@id");
		String theType = connection.getString("@type");
		return id.equals(theId) && type.equals(theType);
	}
	/**
	 * using gson 
	 * check(@NotNull String id, @NotNull String type, @NotNull JsonObject connection)
	 * @param id
	 * @param type
	 * @param connection
	 * @return
	 */
	private static boolean check(@NotNull String id, @NotNull String type, @NotNull JsonObject connection) {
		String theId = connection.get("id").getAsString();
		String theType = connection.get("type").getAsString();
		return id.equals(theId) && type.equals(theType);
	}
	/**
	 * getJsonOfEfwd(@NotNull File efwdFile)
	 * @deprecated
	 *  * This method is no longer acceptable 
	 * <p> Use {@link EfwdDatasourceUtils#newGetJsonOfEfwd((@NotNull File efwdFile)} instead.</p>
	 * @param efwdFile
	 * @return
	 */
	public static JSONObject getJsonOfEfwd(@NotNull File efwdFile) {
		JSONObject fileAsJson;
		try {
			fileAsJson = JsonUtils.getAsJson(efwdFile);
		} catch (Exception ex) {
			log(ex);
			//Problem due to the newly created efwds with only DataSources
			IProcessor processor = ResourceProcessorFactory.getIProcessor();
			fileAsJson = processor.getJSONObject(efwdFile.toString(), true);
		}
		return fileAsJson;
	}
	
	
	public static void validatePermission(String dir) {
		
		if(StringUtils.isNotBlank(dir) &&  !hasPermission(dir)) {
	        	throw new EfwServiceException("Access Denied. You don't have sufficient privileges to access  the requested resource");
		}
	}
	
	
	public static  boolean hasPermission(String  dir) {
		
		if(StringUtils.isBlank(dir)) {
			return false;
		}
		
		HIResourceServiceDB serviceDb = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
		Integer currentLoggedInUser = Integer.valueOf(AuthenticationUtils.getUserId());
		HIResource resource = serviceDb.getResourceByUrl(dir);
		
		if(resource == null ) {
			return false;
		}
		
		if(resource.getCreatedBy() == null ) {
			return true;
		}
			
		if(resource.getCreatedBy().equals(currentLoggedInUser)) {
			return true;
		}
		else {
			Map<Integer,Integer> securityMap =  serviceDb.getSecurityMap();
			return checkIfShared(resource,securityMap,serviceDb);
		}	
	}
	
	public static  boolean hasPermission(String dir, Map<Integer,HIResource> hierarchyMap, Map<String,Boolean> permissionMap) {
			if(StringUtils.isBlank(dir)) {
				return false;
			}
			
			if (permissionMap.containsKey(dir)) {
				return permissionMap.get(dir);
			}
			
			HIResourceServiceDB serviceDb = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
			Integer currentLoggedInUser = Integer.valueOf(AuthenticationUtils.getUserId());
			HIResource resource = serviceDb.findResourceByUrl(dir);
			
			if(resource == null ) {
				permissionMap.put(dir, false);
				return false;
			}
			
			if(resource.getCreatedBy() == null ) {
				permissionMap.put(dir, true);
				return true;
			}
				
			if(resource.getCreatedBy().equals(currentLoggedInUser)) {
				permissionMap.put(dir, true);
				return true;
			}
			else {
				boolean isShared =  checkIfShared(resource,hierarchyMap);
				permissionMap.put(resource.getResourceURL(), isShared);
				return isShared;
			}	
		}
	
	
	private static boolean checkIfShared(HIResource resource , Map<Integer,HIResource> resourceHierarchy) {
		if(resourceHierarchy.containsKey(resource.getResourceId())  || resource.getCreatedBy()==null) {
			return true;
		}
		if( resource.getParentId() != null ) {
			HIResource parent = resourceHierarchy.get(resource.getParentId());
			return checkIfShared(parent, resourceHierarchy);
		}
		return false;
	}
	
	private static boolean checkIfShared( HIResource resource , Map<Integer,Integer> map, HIResourceServiceDB serviceDb ) {
		if(map.containsKey(resource.getResourceId())  || resource.getCreatedBy()==null) {
			return true;
		}
		if( resource.getParentId() != null ) {
			HIResource parent = serviceDb.getHIResourceById(resource.getParentId());
			return checkIfShared(parent, map,serviceDb);
		}
		return false;
	}	
	/**
	 * using gson
	 * getJsonOfEfwd(@NotNull File efwdFile) 
	 * @param efwdFile
	 * @return
	 */
	public static JsonObject newGetJsonOfEfwd(@NotNull File efwdFile) {
		JsonObject fileAsJson;
		try {
			fileAsJson = JsonUtils.newGetAsJson(efwdFile);
		} catch (Exception ex) {
			log(ex);
			//Problem due to the newly created efwds with only DataSources
			IProcessor processor = ResourceProcessorFactory.getIProcessor();
			fileAsJson = processor.getJsonObject(efwdFile.toString(), true);
		}
		return fileAsJson;
	}
}
