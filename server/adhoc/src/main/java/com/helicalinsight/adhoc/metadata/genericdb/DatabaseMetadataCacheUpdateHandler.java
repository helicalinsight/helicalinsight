package com.helicalinsight.adhoc.metadata.genericdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDatabase;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Connections;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.DriverClass;
import com.helicalinsight.adhoc.metadata.jaxb.ExternalRelationships;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIMetadataConnectionEFWD;
import com.helicalinsight.admin.model.HIMetadataConnectionGlobal;
import com.helicalinsight.admin.model.HIMetadataConnections;
import com.helicalinsight.admin.model.MetadataDatabases;
import com.helicalinsight.admin.model.ProcessDetails;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.InconsistentStateException;
import com.helicalinsight.efw.exceptions.RuntimeIOException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The DatabaseMetadataCacheUpdateHandler class is responsible for updating the metadata cache based on the provided JSON form data.
 * It implements the {@link IComponent} interface and provides methods to execute the component and determine its thread safety for caching purposes.
 * 
 * @author Somen
 */
@SuppressWarnings("unused")
public class DatabaseMetadataCacheUpdateHandler implements IComponent {
	
	
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    
    JsonObject formJson;

    /**
     * Executes the component logic to update the metadata cache based on the provided JSON form data.
     * 
     * @param jsonFormData 			 JSON data provides tables,views etc.
     * @return A JSON string containing the response message and updated metadata information.
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public String executeComponent(String jsonFormData) {

    	Integer originalTabCount,duplicateTabCount,viewsCount;
    	originalTabCount=duplicateTabCount=viewsCount=0;
         this.formJson = new Gson().fromJson(jsonFormData,JsonObject.class);
         ISaveMetadataCache saveHandler = SaveMetadataFactory.getSaveClass(formJson);
        saveHandler.setFormData(formJson);
        Metadata metadata = saveHandler.getMetadata();
        Integer duplicateTablesInclusionIfAny = formJson.getAsJsonObject("duplicate").getAsJsonArray("table").size();
        JsonObject addItemObj=formJson.getAsJsonObject("addItem");
        Integer addTablesInclusionIfAny=addItemObj.getAsJsonArray("tables").size();
        Integer addViewsInclusionIfAny=addItemObj.getAsJsonArray("views").size();
		if (formJson.get("metadataReload").getAsBoolean() && !formJson.has("uniqueId")) {
				if (duplicateTablesInclusionIfAny > 0
						&& addTablesInclusionIfAny == 0 && addItemObj.has("views")
						&& addViewsInclusionIfAny == 0) {
					throw new InconsistentStateException("You cannot have only duplicate table(s) in the metadata."
							+ " You need to have at least one table or view along with duplicate table in the metadata.");
				}
		}
		else {
			JsonObject dataSource=formJson.getAsJsonObject("dataSource");
			JsonObject removeItems=formJson.getAsJsonObject("removeItem");
			JsonArray removableTabs=removeItems.getAsJsonArray("tables");
			Integer tableRemoveCount=removableTabs.size();
			Integer viewsRemoveCount=removeItems.getAsJsonArray("views").size();
			Map<String,String> originalTabsMap=new HashMap<>();
			Map<String,String> alphaNumericOrgTabsMap=new HashMap<>();
			if(tableRemoveCount>0 || viewsRemoveCount>0) {
				List<Table> tableList = metadata.getDatabase().getTables().getTableList();
				for(Table t:tableList) {
					if(t.getType()!=null && t.getType().equals("view"))
						viewsCount++;
					else if(t.getOriginalName()!=null)
						duplicateTabCount++;
					else {
						originalTabsMap.put(t.getId(),t.getName());
						alphaNumericOrgTabsMap.put(t.getName(), MetadataUtils.getId(dataSource.get("catalog").getAsString(), dataSource.get("schema").getAsString(), t.getName()));
					}
				}
				originalTabCount=originalTabsMap.size();
				tableRemoveCount=getRemovableTablesCount(removableTabs,originalTabsMap,alphaNumericOrgTabsMap);
				if((originalTabCount-tableRemoveCount)+addTablesInclusionIfAny==0 && 
						duplicateTabCount+duplicateTablesInclusionIfAny > 0 && 
					   (viewsCount-viewsRemoveCount)+addViewsInclusionIfAny ==0) {
					throw new InconsistentStateException("You cannot have only duplicate table(s) in the metadata."
							+ " You need to have at least one table or view along with duplicate table in the metadata.");
				}
			}
		}
        if (formJson.has("fileName")) {
        	String fileName = formJson.get("fileName").getAsString();
        	if(fileName.length() < 3) {
        		throw new RuntimeIOException("Metadata filename should be between 3 and 60 characters.");
        	}
        	metadata.setFileName(fileName);
        }

        coreSaveProcess(formJson, saveHandler, metadata);
        handleMultiConnections(metadata);
        handleMetadataSecurity(formJson, metadata);
        if (metadata.getSecurity() == null) {
            metadata.setSecurity(SecurityUtils.securityObject());
        }
        JsonObject response = saveCube(formJson, saveHandler, metadata);
        return response.toString();
    }
    
    /**
     * Handles the processing of multiple connections in the metadata.
     * This includes adding, changing, or removing connections based on the form data.
     * 
     * @param metadata 		 metadata object to update.
     */
    private void handleMultiConnections(Metadata metadata) {
		
			List<String> changeItemList = new ArrayList<>();
			List<String> removeItemList = new ArrayList<>();
			List<String> addItemList = new ArrayList<>();
			Map<String, JsonObject> connectionMap = new HashMap<>();
			populateConnections(formJson, connectionMap, addItemList, changeItemList, removeItemList);
			MetadataDataSourceChangeHandler changeHandler = ApplicationContextAccessor.getBean(MetadataDataSourceChangeHandler.class);
			HIMetadataResourceServiceDB serviceDb = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
			if (formJson.has("connections")) {
				List<ConnectionDatabase> cdbList = null;
				Connections connections = metadata.getConnections();
				if (connections != null && connections.getConnectionDatabase() != null) {
					if (!connections.getConnectionDatabase().isEmpty()) {
						cdbList = metadata.getConnections().getConnectionDatabase();
					}
				} else {
					cdbList = new ArrayList<>();
				}
				if (!connectionMap.isEmpty()) {

					for (String item : addItemList) {
						String itemKey = item.replace("\"", "");
						JsonObject formData = connectionMap.get(itemKey);
						process(formData, metadata, cdbList);
						this.formJson.getAsJsonArray("connections").remove(formData);
					}
					for (String item : changeItemList) {
						JsonObject formData = connectionMap.get(item);
						JsonObject datasource = formData.getAsJsonObject("dataSource");
						if(this.formJson.has("newLocation")) {
							String oldConId = "";
							List<ConnectionDatabase> dbList = metadata.getConnections().getConnectionDatabase();
							for(ConnectionDatabase cdb : dbList) {
								if ( cdb.getDatabase().getId().equals(datasource.get("dbId").getAsString())) {
									oldConId = cdb.getConnectionDetails().getConnectionId();
								}
							}
							datasource.addProperty("saveas", true);
							datasource.addProperty("oldConnectionId",oldConId);
						}
						changeHandler.change(null, datasource, metadata);
						if(this.formJson.has("uuid")) formData.addProperty("uuid", this.formJson.get("uuid").getAsString());
						process(formData, metadata, cdbList);						
						this.formJson.getAsJsonArray("connections").remove(formData);
					}

				}
				JsonArray connectionsArr = formJson.getAsJsonArray("connections");
				if (!connectionsArr.isEmpty()) {
					for (Object eachConnection : connectionsArr) {
						JsonObject eachConnectionJson = (JsonObject) eachConnection;
						if(this.formJson.has("uuid")) {
							eachConnectionJson.addProperty("uuid", this.formJson.get("uuid").getAsString());
						}
						process((JsonObject) eachConnection, metadata, cdbList);
					}
				}
			}
			for (String item : removeItemList) {
				int conId = serviceDb.removeMetadataConnection(metadata.getMetadataId(), item,"cascade");
				changeHandler.removeOldConnection(metadata, "" + conId);
			}
    }
    /**
     * Processes the form data for each connection and updates the metadata accordingly.
     * 
     * @param formData 		 form data for the connection.
     * @param metadata 		 metadata object to update.
     * @param cdbList  		 list of ConnectionDatabase objects.
     */
    public void process(JsonObject formData, Metadata metadata,List<ConnectionDatabase> cdbList) {

    	Connections connections = metadata.getConnections();
        if (connections == null) {
            connections = ApplicationContextAccessor.getBean(Connections.class);            
        }
        ISaveMetadataCache mdSaveHandler = SaveMetadataFactory.getSaveClass(formData);
		String connectionId = formData.getAsJsonObject("dataSource").get("id").getAsString();
		mdSaveHandler.setFormData(formData);
		Metadata metadataNew = MetadataUtils.getNewMetadata(connectionId, metadata);
		mdSaveHandler.setMetadata(metadataNew);
		coreSaveProcess(formData, mdSaveHandler, metadataNew);
		ConnectionDatabase conDatabase = ApplicationContextAccessor.getBean(ConnectionDatabase.class);
		conDatabase.setConnectionDetails(metadataNew.getConnectionDetails());
		conDatabase.setDatabase(metadataNew.getDatabase());
		if(!cdbList.contains(conDatabase)) {
			cdbList.add(conDatabase);
		}
		connections.setConnectionDatabase(cdbList);
		metadata.setConnections(connections);
    }
    		
    /**
     * Performs the core saving process of the metadata cache.
     * This includes setting metadata type, adding connection details, database information, and others.
     * 
     * @param formJson    		 JSON form data.
     * @param saveHandler 		 metadata cache save handler.
     * @param metadata    		 metadata object to update.
     */
    private void coreSaveProcess(JsonObject formJson, ISaveMetadataCache saveHandler, Metadata metadata) {
        metadata.setType(formJson.get("classifier").getAsString());
        saveHandler.addConnectionDetails(formJson, metadata);
        if(metadata.getConnectionDetails()!=null)
        	metadata.setConnectionType(metadata.getConnectionDetails().getConnectionType());
        Database database = saveHandler.getDatabase();
        if(!GsonUtility.optJsonObject(formJson, "dataSource").entrySet().isEmpty()) {
        	saveHandler.setDatabaseName(database);
        	saveHandler.addTableColumns(database);
        	saveHandler.addJoins(database);
        }
        metadata.setDatabase(database);
        saveHandler.addOthers(metadata);
    }
    /**
     * Saves the cube metadata and returns the response.
     * 
     * @param formJson    	 form data.
     * @param saveHandler 	 metadata cache save handler.
     * @param metadata    	 metadata object to update.
     * @return A JSON object containing the response message and updated metadata.
     */
    private JsonObject saveCube(JsonObject formJson, ISaveMetadataCache saveHandler, Metadata metadata) {
        JsonObject response = saveHandler.saveFileToDisk(formJson, metadata);

        return response;
    }
    /**
     * Handles the security settings of the metadata.
     * 
     * @param formJson 	 JSON form data provides action , access .
     * @param metadata 	 metadata object to update.
     */
    private void handleMetadataSecurity(JsonObject formJson, Metadata metadata) {
        JsonObject access = GsonUtility.optJsonObject(formJson, "access");
        if (access != null && !access.entrySet().isEmpty()) {
            if (access.has("action") && "deleteAll".equalsIgnoreCase(access.get("action").getAsString())) {
                metadata.setMetadataSecurity(null);
            } else {
                JsonArray expressionArray = access.getAsJsonArray("expression");
                if (!expressionArray.isEmpty()) {
                    MetadataSecurityUpdateHandler.setAccessTag(expressionArray, metadata);
                }
            }
        }
    }
    /**
     * Populates the connections map and lists for adding, changing, and removing connections.
     * 
     * @param formData       			 form data containing connection details.
     * @param connectionsMap 			 map containing connection IDs and their corresponding form data.
     * @param addItemList    			 list of connection IDs to add.
     * @param changeItemList 			 list of connection IDs to change.
     * @param removeItemList 			 list of connection IDs to remove.
     */
    private void populateConnections(JsonObject formData,Map<String,JsonObject> connectionsMap,
    		List<String> addItemList,List<String> changeItemList , List<String> removeItemList){
    	JsonArray connections =  GsonUtility.optJsonArray(formData, "connections");
		if (connections != null && !connections.isEmpty()) {
			for (JsonElement object : connections) {
				JsonObject connectionObject = (JsonObject) object;
				String dbId = connectionObject.getAsJsonObject("dataSource").get("dbId").getAsString();
				if (StringUtils.isNotBlank(dbId))
					connectionsMap.put(dbId, connectionObject);
			}
		}
		JsonObject addItemObject = formData.getAsJsonObject("addItem");
    	JsonArray addConnections = GsonUtility.optJsonArray(addItemObject, "connections");
    	if(addConnections!=null &&  !addConnections.isEmpty()) {
    		for(JsonElement obj : addConnections)
    			addItemList.add(obj.getAsString());
    	}
    	JsonObject removeItemObject = formData.getAsJsonObject("removeItem");
    	JsonArray removeConnections = GsonUtility.optJsonArray(removeItemObject, "connections");
    	if(removeConnections!=null &&  !removeConnections.isEmpty()) {
    		for(JsonElement obj : removeConnections)
    			removeItemList.add(obj.getAsString());
    	}
    	JsonObject changeItemObject = formData.getAsJsonObject("changeItem");
    	JsonArray changeConnections = GsonUtility.optJsonArray(changeItemObject, "connections");
    	if(changeConnections!=null &&  !changeConnections.isEmpty()) {
    		for(JsonElement obj : changeConnections)
    			changeItemList.add(obj.getAsString());
    	}
    	
    }
    /**
     * Retrieves the count of removable tables based on the original tables and their corresponding alpha-numeric IDs.
     * 
     * @param removableTabs    		 	 JSON array containing removable table IDs.
     * @param originalTabs     			 map containing original table IDs and their names.
     * @param alphaNumericOrgTabsMap 	 map containing alpha-numeric table names and their corresponding IDs.
     * @return The count of actual removable tables.
     */
    public Integer getRemovableTablesCount(JsonArray removableTabs,Map<String,String> originalTabs,Map<String,String> alphaNumericOrgTabsMap) {
    	Integer actualTabRemovalCount=0;
    	String removableId;
    	for(JsonElement obj:removableTabs) {
    		removableId=obj.getAsString();
    		if(!StringUtils.isNumeric(obj.getAsString())) {
    			if(alphaNumericOrgTabsMap.containsKey(removableId))
    				actualTabRemovalCount++;
    		}else {
    			if(originalTabs.containsKey(removableId))
    				actualTabRemovalCount++;
    		}
    	}
    	return actualTabRemovalCount;
    }


}