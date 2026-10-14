package com.helicalinsight.export.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helicalinsight.admin.dto.Notification;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.ImportResponse;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.service.RecycleBinHandler;
import com.helicalinsight.stream.ProgressEventPublisher;

public final class ImportManagerContext {

	private static final Logger logger = LoggerFactory.getLogger(ImportManagerContext.class);
	private Date date;
	private Manifest manifest;
	private ImportRequest request;
	private ImportResponse response;
	private Map<String,Map<Integer,Integer>> newOldTableIdMap;
	private Map<String,Map<Integer,Integer>> newOldColumnIdMap;
	private Map<Integer,String> newOldImageIds;
	private Map<String,HIResource> resourceUrlMap;
	private String currentDirectory;
	private String currentResourceUrl;
	private Map<Map<String,Integer>, Boolean> processedEfwdConnections;
	private Map<Integer,GlobalConnections> globalConnectionMap;
	private Map<Integer,HIEfwdConnection> efwdConnectionMap;
	private ProgressEventPublisher progressPublisher;
	
	
	public ImportManagerContext() {
		processedEfwdConnections = new HashMap<>();
		globalConnectionMap = new HashMap<>();
		efwdConnectionMap = new HashMap<>();
		newOldImageIds = new HashMap<>();
	}

	public void setProgressPublisher(ProgressEventPublisher progressPublisher) {
		this.progressPublisher = progressPublisher;
	}

	public ProgressEventPublisher getProgressPublisher() {
		return progressPublisher;
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

	public void addWarning(String resourcePath, String message) {
		ensurePublisher().warning(resourcePath, message);
	}

	public void addError(String resourcePath, String message) {
		ensurePublisher().error(resourcePath, message);
	}

	public void addInfo(String resourcePath, String message) {
		ensurePublisher().info(resourcePath, message);
	}

	public void addLog(String resourcePath, String message) {
		ensurePublisher().log(resourcePath, message);
	}

	private ProgressEventPublisher ensurePublisher() {
		if (progressPublisher == null) {
			progressPublisher = new ProgressEventPublisher(ensureNotification(), null);
		}
		return progressPublisher;
	}

	private Notification ensureNotification() {
		if (response.getNotification() == null) {
			response.setNotification(new Notification());
		}
		return response.getNotification();
	}

	public void setCurrentResourceUrl(String currentResourceUrl) {
		this.currentResourceUrl = currentResourceUrl;
	}

	public String getCurrentResourceUrl() {
		return currentResourceUrl;
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
			if (shouldPreserveDeleted(resource)) {
				return true;
			}
			HIRecycleBinService binService = ApplicationContextAccessor.getBean(HIRecycleBinService.class);
			binService.deleteHIRecycleBinByResourceId(resource.getResourceId());
			resource.setDeleted(false);
		}
		return true;
	}

	private boolean shouldPreserveDeleted(HIResource resource) {
		String key = removeDestination(resource.getResourceURL());
		if (shouldPreserveDeleted(key)) {
			return true;
		}
		return shouldPreserveDeleted(key + ResourceExtension.FOLDER.getValue());
	}

	private boolean shouldPreserveDeleted(String key) {
		if (request == null || request.getOptions() == null || !Boolean.TRUE.equals(request.getOptions().getRecycleBin())) {
			return false;
		}
		if (manifest == null || manifest.getOptions() == null || !Boolean.TRUE.equals(manifest.getOptions().getRecycleBin())) {
			return false;
		}
		if (manifest.getRecycleBins() == null || manifest.getRecycleBins().isEmpty() || key == null) {
			return false;
		}
		return manifest.getRecycleBins().containsKey(key);
	}
	
	public boolean recover(GlobalConnections gConnection) {
		if(Boolean.TRUE.equals(gConnection.isDeleted())) {
			if (shouldPreserveDeleted(RecycleBinHandler.KEY_DS_GLOBAL + gConnection.getGlobalId())) {
				return true;
			}
			HIRecycleBinService binServiceDB = ApplicationContextAccessor.getBean(HIRecycleBinService.class);
			HIRecycleBin bin =  binServiceDB.findHIRecycleBinByGlobalId(gConnection.getGlobalId());
			binServiceDB.delete(bin.getId());
			gConnection.setDeleted(false);
		}
		return true;
	}
	
	public boolean recover(HIEfwdConnection connection) {
		if(Boolean.TRUE.equals(connection.isDeleted())) {
			if (shouldPreserveDeleted(RecycleBinHandler.KEY_DS_EFWD + connection.getId())) {
				return true;
			}
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
		GlobalConnections connection = this.globalConnectionMap.get(id);
		if (connection == null) {
			logger.debug("Could not find connection with id : {} , assigning the default global connection", id);
			id = JsonUtils.getDefaultDatasourceId();
			GlobalConnectionService service = ApplicationContextAccessor.getBean(GlobalConnectionService.class);
			GlobalConnections defaultConnection = service.findGlobalConnectionById(id);
			this.globalConnectionMap.put(id, defaultConnection);
			if (StringUtils.isNotBlank(currentResourceUrl)) {
				addWarning(currentResourceUrl,
						"Datasource connection not found; assigned default datasource (id=" + id + ").");
			}
			return defaultConnection;
		}
		return connection;
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

