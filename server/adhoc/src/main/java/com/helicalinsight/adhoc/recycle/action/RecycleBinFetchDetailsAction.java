package com.helicalinsight.adhoc.recycle.action;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
/**
* The {@code RecycleBinFetchDetailsAction} class extends the {@code RecycleBinAction} to provide
* functionality for fetching detailed information about items in the recycle bin. It uses the
* {@code HIRecycleBinService} to retrieve associated resources for each recycle bin item.
*/
@Component("fetchdetailsActionComponent")
public class RecycleBinFetchDetailsAction extends RecycleBinAction{
	
	@Autowired
	private HIRecycleBinService recycleBinService;
	/**
	* Performs the action of fetching detailed information for the specified recycle bin items.
	* It processes each item ID provided in the JSON array from the form data and retrieves
	* associated resources using the {@code HIRecycleBinService}.
	*
	* @return String JSON string representing the detailed information of each recycle bin item.
	* @throws EfwdServiceException if no recycle bin item IDs are provided.
	*/
	@Override
	public String performAction() {
		ObjectNode response = JacksonUtility.emptyNode();
		JsonObject formData = getFormData();
		JsonArray recycleBinIds =  formData.getAsJsonArray("recycleBinIds");
		if(recycleBinIds == null || recycleBinIds.isEmpty() ) {
			throw new EfwdServiceException("Please provide 'recycleBinIds'");
		}
		
		for(Object recycleBinIdObject : recycleBinIds) {
			Long id = Long.valueOf(""+recycleBinIdObject);
			ObjectNode dataHolder = JacksonUtility.emptyNode();
			Map<String,List<Object>> objects =  recycleBinService.findAllResourceOfRecycleBinItem(id);
			dataHolder.putPOJO("data", objects);
			response.putPOJO(""+id, dataHolder);
		}
		return response.toString();
	}

}
