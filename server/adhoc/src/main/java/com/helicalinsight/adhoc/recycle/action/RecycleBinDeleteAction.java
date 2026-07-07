package com.helicalinsight.adhoc.recycle.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.recycle.factory.RecycleBinHandlerFactory;
import com.helicalinsight.adhoc.recycle.handler.RecycleBinHandler;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.efw.exceptions.EfwServiceException;

/**
* The {@code RecycleBinDeleteAction} class extends the {@code RecycleBinAction} to provide
* functionality for deleting items from the recycle bin. It uses the {@code HIRecycleBinService}
* to find specific recycle bin items and the {@code Deletable} class to check if items can be
* permanently deleted.
*/
@Component("deleteActionComponent")
public class RecycleBinDeleteAction extends RecycleBinAction {
	
	
	private static final String COMPLETED = "completed";
	private static final String INCOMPLETE = "incomplete";

	@Autowired
	private HIRecycleBinService recycleBinService;
	
	@Autowired
	private Deletable deletable;
	
	/**
	* Performs the action of deleting the specified items from the recycle bin. It processes
	* each item ID provided in the JSON array from the form data and uses the {@code Deletable}
	* class to check if the item can be deleted. If the 'force' option is not present and an item
	* cannot be deleted, it is marked as incomplete.
	*
	* @return String JSON string representing the status of the delete operation including
	*               messages and IDs of completed and incomplete items.
	* @throws EfwServiceException if no resource IDs are provided for deletion.
	*/
	@Override
	public String performAction() {
		ObjectNode response = JacksonUtility.emptyNode();
		JsonObject formData = getFormData();
		Map<String,List<Long>> recycleBinIds = new HashMap<>();
		recycleBinIds.put(COMPLETED, new ArrayList<>());
		recycleBinIds.put(INCOMPLETE, new ArrayList<>());
		
		JsonArray jsonArray =  formData.getAsJsonArray("recycleBinIds");
		if(jsonArray.isEmpty()) {
			throw new EfwServiceException("Please provide resource(s) to delete.");
		}
		
		boolean hasAnyIncomplete = false;
		int size = jsonArray.size();
		for(Object eachResource : jsonArray) {
			Long recycleBinId = Long.valueOf(""+eachResource);
			if(!deletable.check(recycleBinId,recycleBinIds) && !formData.has("force")) {
				recycleBinIds.get(INCOMPLETE).add(recycleBinId);
				hasAnyIncomplete = true;
				continue;
			}
			if (recycleBinService.isRecycleBinPresent(recycleBinId)) {
				RecycleBinDTO bin = recycleBinService.getHIRecycleBinById(recycleBinId);
				RecycleBinHandler handler = RecycleBinHandlerFactory.getHandler(bin.getType().name(),"delete");
				handler.handle(bin, Map.of());
				recycleBinIds.get(COMPLETED).add(recycleBinId);
			}
		}
		
		response.putPOJO("recycleBin", recycleBinIds);
		
		String message = "";
		
		if (hasAnyIncomplete) {
			message = size > 1
					? "The delete operation was not completed successfully. Some of the items were deleted, but some of them were not"
					: "The resource could not be deleted, because some of the files linked to it are not in deleted state.";
		} else {
			message = "The selected resource have been deleted and any related content(s).";
		}
		
		response.put("message", message);
		
		return response.toString();
	}
	

}
