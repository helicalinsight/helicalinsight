package com.helicalinsight.adhoc.recycle.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.adhoc.recycle.factory.RecycleBinHandlerFactory;
import com.helicalinsight.adhoc.recycle.handler.RecycleBinHandler;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.utils.JacksonUtility;


/**
* The {@code RecycleBinClearAction} class extends the {@code RecycleBinAction} to provide
* functionality for clearing items from the recycle bin. It utilizes the {@code HIRecycleBinService}
* to retrieve all items from the recycle bin and the {@code Deletable} class to check if
* items can be deleted.
*/
@Component("clearActionComponent")
public class RecycleBinClearAction extends RecycleBinAction {

	@Autowired
	private HIRecycleBinService recycleBinService;
	
	@Autowired
	private Deletable deletable;
	
	private static final String COMPLETED = "completed";
	private static final String INCOMPLETE = "incomplete";
	/**
	* Performs the action of clearing the recycle bin. It checks each item in the recycle bin
	* to determine if it can be deleted and updates the status accordingly. If the 'force' option
	* is not provided, items that are not in a deleted state will be marked as incomplete.
	*
	* @return String JSON string representing the status of the clear operation including
	*               messages and IDs of completed and incomplete items.
	*/
	@Override
	public String performAction() {
		ObjectNode response = JacksonUtility.emptyNode();
		List<RecycleBinDTO> items = recycleBinService.getAll();
		Map<Long,Boolean> deleteStatusMap=new HashMap<>();
		items.forEach(rb-> deleteStatusMap.put(rb.getRecycleBinId(), false));
		if (items.isEmpty()) {
			response.put("message", "RecycleBin is Emtpy!");
		} else {
			Map<String,List<Long>> recycleBinIds = new HashMap<>();
			recycleBinIds.put(COMPLETED, new ArrayList<>());
			recycleBinIds.put(INCOMPLETE, new ArrayList<>());
			boolean hasAnyIncomplete = false;
			for (RecycleBinDTO item : items) {
				Long recycleBinId = item.getRecycleBinId();
				if(!deletable.check(recycleBinId,recycleBinIds) && !formData.has("force")) {
					recycleBinIds.get(INCOMPLETE).add(recycleBinId);
					hasAnyIncomplete = true;
					continue;
				}
				if (Boolean.FALSE.equals(deleteStatusMap.get(item.getRecycleBinId()))) {
					RecycleBinHandler handler = RecycleBinHandlerFactory.getHandler(item.getType().name(),"delete");
					handler.handle(item,deleteStatusMap);
					recycleBinIds.get(COMPLETED).add(recycleBinId);
				}
			}
			
			response.putPOJO("recycleBin", recycleBinIds);
			
			String message = "";
			
			if (hasAnyIncomplete) {
				message = "The clear operation was not completed, because some of the files linked to it are not in deleted state, Please delete them manually.";
			} else {
				message = "Resource(s) deleted successfully.";
			}
			response.put("message", message);
			
		}
		return response.toString();
	}
}
