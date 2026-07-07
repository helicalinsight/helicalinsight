package com.helicalinsight.adhoc.recycle.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
* The {@code RecycleBinRestoreAction} class extends {@code RecycleBinAction} to provide
* the functionality for restoring items from the recycle bin. It leverages the
* {@code HIRecycleBinService} to check the presence of items and uses the appropriate
* {@code RecycleBinHandler} to perform the restore operation.
*/
@Component("restoreActionComponent")
public class RecycleBinRestoreAction extends RecycleBinAction {

	@Autowired
	private HIRecycleBinService recycleBinService;
	/**
	* Performs the action of restoring the specified items from the recycle bin. It processes
	* each item ID provided in the JSON array from the form data, checks if the item is present
	* in the recycle bin, and then uses the corresponding handler to restore it.
	*
	* @return String JSON string representing the status of the restore operation including
	*               messages and IDs of completed and incomplete items.
	* @throws EfwServiceException if no resources are provided for restoration.
	*/
	@Override
	public String performAction() {
		ObjectNode response = JacksonUtility.emptyNode();
		JsonObject formData = getFormData();
		ObjectNode recycleBin = JacksonUtility.emptyNode();
		JsonArray jsonArray = formData.getAsJsonArray("recycleBinIds");
		if (jsonArray.isEmpty()) {
			throw new EfwServiceException("Please provide resources to restore");
		}

		Map<Long, Boolean> deleteMap = new HashMap<>();
		for (Object eachResource : jsonArray) {
			Long id = Long.valueOf(eachResource.toString());
			if(recycleBinService.isRecycleBinPresent(id)) {
				RecycleBinDTO bin = recycleBinService.getHIRecycleBinById(id);
				if ( bin != null ) {
					RecycleBinHandler handler = RecycleBinHandlerFactory.getHandler(bin.getType().name(),"restore");
					handler.handle(bin, deleteMap);
				}
			}
		}

		Map<Boolean, List<Long>> result = deleteMap.entrySet().stream().collect(Collectors
				.partitioningBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())));

		List<Long> completed = result.get(true);
		List<Long> incomplete = result.get(false);
		recycleBin.putPOJO("incomplete", incomplete);
		recycleBin.putPOJO("completed", completed);
		String message = "";
		
		if (incomplete.isEmpty())
			message = "Resource(s) restored successfully.";
		else {
			message = "You can't restore this resource because its parent is in the recycle bin. To restore it, you need to restore the parent first.";
		}
		response.put("message", message);
		response.putPOJO("recycleBin", recycleBin);
		return response.toString();
	}

}
