package com.helicalinsight.adhoc.recycle.handler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.helicalinsight.adhoc.recycle.factory.RecycleBinHandlerFactory;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIRecyclebinHelperService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.core.request.RecycleBinDatasource;
import com.helicalinsight.core.request.RecycleBinResourceItem;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IService;


/**
 * HIRecycleBinHIResourceDeleteHandler
 * 
 * This class handles the deletion of HI resource items from the recycle bin.
 * It implements the RecycleBinHandler interface.
 */
@Component("hi_resource_db_deleteHandler")
public class HIRecycleBinHIResourceDeleteHandler implements RecycleBinHandler {

	
	@Autowired
	private HIResourceServiceDB serviceDb;
	
	@Autowired
	private HIRecycleBinService recycleBinService;
	
	@Autowired
	private EFWDConnectionService efwdConnectionService;
	
	@Autowired
	private HIRecyclebinHelperService hiRecyclebinHelperService;
	
	
	/**
     * Handles the deletion of a single HIRecycleBin entry.
     * Deletes the HIRecycleBin entry and its associated HI resource item.
     * 
     * @param bin 					 HIRecycleBin entry to be handled
     * @param deleteStatusMap		 map to be updated after handling the entry
     * @return {@code true} if the deletion is successful, {@code false} otherwise
     */
	public boolean handle(RecycleBinDTO bin,Map<Long,Boolean> deleteStatusMap) {
		JsonObject links = getLinkedData(bin.getRecycleBinId());
		if(links.has("resources")) {
			JsonArray resources = links.get("resources").getAsJsonArray();
			Type listType = new TypeToken<List<Integer>>() {}.getType();
			List<Integer> linksList = new Gson().fromJson(resources, listType);
			softDeletableLinkedResourceItems(linksList,deleteStatusMap);
		}if(links.has("dataSources")) {
			JsonArray dataSources = links.get("dataSources").getAsJsonArray();
			Type listType = new TypeToken<List<Integer>>() {}.getType();
			List<Integer> linksList = new Gson().fromJson(dataSources, listType);
			softDeletableLinkedEfwDataSourceItems(linksList,deleteStatusMap);
		}
		
		hiRecyclebinHelperService.deleteHIResourceAndRecyclebin(bin);
		return true;
	}
	
	private void softDeletableLinkedEfwDataSourceItems(List<Integer> links, Map<Long, Boolean> deleteStatusMap) {
		RecycleBinHandler handler = RecycleBinHandlerFactory.getHandler("HI_EFWD_CONNECTION", "delete");
		links.forEach(connId->{
			HIRecycleBin bin=efwdConnectionService.getHiRecycleBinIdOfSoftDeletedItemByEfwdConnId(connId);
			if(bin!=null)
				handler.handle(bin,deleteStatusMap);
		});
	}

	private void softDeletableLinkedResourceItems(List<Integer> links,Map<Long,Boolean> deleteStatusMap) {
		links.forEach(resource->{
			Long hiRecycleBinId=serviceDb.getHiRecycleBinIdOfSoftDeletedItemByHiResourceId(resource);
			if(hiRecycleBinId!=null) {
				recycleBinService.delete(hiRecycleBinId);
				if(!deleteStatusMap.isEmpty() && deleteStatusMap.containsKey(hiRecycleBinId))
					deleteStatusMap.replace(hiRecycleBinId, true);
			}
		});
	}
	/**
     * Retrieves linked data for a given HIRecycleBin entry.
     * 
     * @param recycleBinId 			 ID of the recycle bin entry
     * @return A JsonObject containing linked resources and data sources
     */
	
	
	public JsonObject getLinkedData(Long recycleBinId) {
	    JsonObject outputOfLinkedItems = new JsonObject();
	    if (!recycleBinService.isRecycleBinPresent(recycleBinId)) {
	        return outputOfLinkedItems;
	    }
	    Map<String, List<Object>> associated =recycleBinService.findAllResourceOfRecycleBinItem(recycleBinId);

	    if (associated.containsKey("resources")) {
	        JsonArray jsonArray = new JsonArray();
	        for (Object resource : associated.get("resources")) {
	            jsonArray.add(((RecycleBinResourceItem) resource).getResourceId());
	        }
	        outputOfLinkedItems.add("resources", jsonArray);
	    }

	    if (associated.containsKey("dataSources")) {
	        JsonArray jsonArray = new JsonArray();
	        for (Object dataSource : associated.get("dataSources")) {
	            jsonArray.add(((RecycleBinDatasource) dataSource).getConnectionId());
	        }
	        outputOfLinkedItems.add("dataSources", jsonArray);
	    }

	    return outputOfLinkedItems;
	}
	
	/**
     * Handles the deletion of a single HIRecycleBin entry.
     * 
     * @param bin 				 HIRecycleBin entry to be handled
     * @return {@code true} if the deletion is successful, {@code false} otherwise
     */
	@Override
	public boolean handle(HIRecycleBin bin) {
		handle(bin,new HashMap<Long,Boolean>());
		return true;
	}


}
