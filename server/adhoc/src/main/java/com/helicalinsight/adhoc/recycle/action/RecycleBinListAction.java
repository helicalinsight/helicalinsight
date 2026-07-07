package com.helicalinsight.adhoc.recycle.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.core.request.RecycleBinItem;

/**
* The {@code RecycleBinListAction} class extends the {@code RecycleBinAction} to provide
* functionality for listing all items currently in the recycle bin. It uses the
* {@code HIRecycleBinService} to retrieve a list of {@code RecycleBinItem} objects.
*/
@Component("listActionComponent")
public class RecycleBinListAction extends RecycleBinAction {

	@Autowired
	private HIRecycleBinService recycleBinService;
	/**
	* Performs the action of listing all recycle bin items. It retrieves the list of items
	* from the {@code HIRecycleBinService} and constructs a JSON response containing the data.
	*
	* @return String JSON string representing the list of all recycle bin items.
	*/
	@Override
	public String performAction() {
		List<RecycleBinItem> items = recycleBinService.list();
		ObjectNode response = JacksonUtility.emptyNode();
		response.putPOJO("data", items);
		return response.toString();
	}

}
