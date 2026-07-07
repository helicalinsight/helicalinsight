package com.helicalinsight.export.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.helicalinsight.adhoc.services.RecycleBinService;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.ImportResponse;
import com.helicalinsight.export.dto.Manifest;

public final class ImportManagerContext {

	private Date date;
	private Manifest manifest;
	private ImportRequest request;
	private ImportResponse response;
	private Map<String,Map<Integer,Integer>> newOldTableIdMap;
	private Map<String,Map<Integer,Integer>> newOldColumnIdMap;
	private Map<Integer,String> newOldImageIds;
	private Map<String,HIResource> resourceUrlMap;
	private String currentDirectory;
	private Map<Map<String,Integer>, Boolean> processedEfwdConnections;
	private Map<Integer,GlobalConnections> globalConnectionMap;
	private Map<Integer,HIEfwdConnection> efwdConnectionMap;
	
	
	public ImportManagerContext() {
		processedEfwdConnections = new HashMap<>();
		globalConnectionMap = new HashMap<>();
		efwdConnectionMap = new HashMap<>();
		newOldImageIds = new HashMap<>();
	}

	public Map<Integer, String> getNewOldImageIds() {
		return newOldImageIds;
	}

	public List<String> getDependency(String key) {
		return manifest.getDependencies().get(key);
	}
	
	
	public void appendInsert(String insertPath) {
		response.getInserts().add(insertPath);
		response.setInsertCount(response.getInsertCount()+1);
	}

	public void appendUpdate(String update) {
		response.getUpdates().add(update);
		response.setUpdateCount(response.getUpdateCount()+1);
	}

	public void appendSkip(String skip) {
		response.getSkips().add(skip);
		response.setSkipCount(response.getSkipCount()+1);
	}
	
	public void setResponse(ImportResponse response) {
		this.response = response;
	}

	public ImportResponse getResponse() {
		return response;
	}

	public void setRequest(ImportRequest request) {
		this.request = request;
	}

	public ImportRequest getRequest() {
		return request;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setManifest(Manifest manifest) {
		this.manifest = manifest;
	}

	public Manifest getManifest() {
		return manifest;
	}
	
	public void setTableIdMap(String key , Map<Integer,Integer> value) {
		if( newOldTableIdMap == null) {
			newOldTableIdMap = new HashMap<>();
		}
		newOldTableIdMap.put(key, value);
	}
	public void setColumnIdMap(String key, Map<Integer,Integer> value) {
		if( newOldColumnIdMap == null) {
			newOldColumnIdMap = new HashMap<>();
		}
		
		newOldColumnIdMap.put(key, value);
	}
	
	public Map<Integer,Integer> getTableIdMap(String key){
		return newOldTableIdMap.getOrDefault(key,new HashMap<>());
	}
	public Map<Integer,Integer> getColumnIdMap(String key){
		return newOldColumnIdMap.getOrDefault(key,new HashMap<>());
	}
	public void setCurrentDirectory(String currentDirectory) {
		this.currentDirectory = currentDirectory;
	}
	public String getCurrentDirectory() {
		return currentDirectory;
	}
	
	public String getResourcesDirectory() {
		return currentDirectory + "/resources/";
	}
	
	public void setResourceUrlIdMap(String url ,HIResource resource) {
		if(this.resourceUrlMap == null) {
			this.resourceUrlMap = new HashMap<>();
		}
		this.resourceUrlMap.put(url,resource);
	}
	public Map<String,HIResource>  getResourceUrlMap() {
		if(this.resourceUrlMap == null) {
			this.resourceUrlMap = new HashMap<>();
		}
		return this.resourceUrlMap;
	}
	
	public boolean recover(HIResource resource) {
		if(Boolean.TRUE.equals(resource.isDeleted())) {
			HIRecycleBinService binService = ApplicationContextAccessor.getBean(HIRecycleBinService.class);
			binService.deleteHIRecycleBinByResourceId(resource.getResourceId());
			resource.setDeleted(false);
		}
		return true;
	}
	
	public boolean recover(GlobalConnections gConnection) {
		if(Boolean.TRUE.equals(gConnection.isDeleted())) {
			HIRecycleBinService binServiceDB = ApplicationContextAccessor.getBean(HIRecycleBinService.class);
			HIRecycleBin bin =  binServiceDB.findHIRecycleBinByGlobalId(gConnection.getGlobalId());
			binServiceDB.delete(bin.getId());
			gConnection.setDeleted(false);
		}
		return true;
	}
	
	public boolean recover(HIEfwdConnection connection) {
		if(Boolean.TRUE.equals(connection.isDeleted())) {
			HIRecycleBinService binServiceDB = ApplicationContextAccessor.getBean(HIRecycleBinService.class);
			HIRecycleBin bin =  binServiceDB.findHIRecycleBinByEFWDId(connection.getId());
			binServiceDB.delete(bin.getId());
			connection.setDeleted(false);
		}
		return true;
	}
	
	public void setProcessed(String type , Integer connectionId) {
		this.processedEfwdConnections.put(Map.of(type,connectionId), true);
	}
	
	public boolean getProcessed(String type, Integer connectionId) {
		 return this.processedEfwdConnections.getOrDefault(Map.of(type,connectionId),false);
	}
	
	
	public void putGlobalConnection(int id, GlobalConnections globalConnection) {
		this.globalConnectionMap.put(id, globalConnection);
	}
	
	public void putEfwdConnection(int id, HIEfwdConnection efwdConnection) {
		this.efwdConnectionMap.put(id, efwdConnection);
	}
	
	public GlobalConnections getGlobalConnection(int id) {
		return this.globalConnectionMap.get(id);
	}
	public HIEfwdConnection getEfwdConnection(int id) {
		return this.efwdConnectionMap.get(id);
	}
	
	public String removeDestination(String resourceUrl) {
		 return destinationExists() ?
			  resourceUrl.substring(request.getDestination().length() + 1) : resourceUrl;
	}
	
	public String addDestination(String resourceUrl) {
		return destinationExists() ?
				String.join("/",request.getDestination() ,resourceUrl) : resourceUrl;
	}
	
	public boolean destinationExists() {
		return StringUtils.isNotBlank(request.getDestination());
	} 
}

