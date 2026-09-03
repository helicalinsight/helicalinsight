package com.helicalinsight.adhoc.recycle.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.recycle.PurgeEligibility;
import com.helicalinsight.adhoc.recycle.RecycleBinPurgeEligibility;
import com.helicalinsight.adhoc.recycle.RecycleBinPurgePlanner;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.datasource.GsonUtility;
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
	private RecycleBinPurgeEligibility purgeEligibility;
	
	@Autowired
	private RecycleBinPurgePlanner purgePlanner;
	
	@Override
	public String performAction() {
		ObjectNode response = JacksonUtility.emptyNode();
		JsonObject formData = getFormData();
		
		Map<String, Set<Long>> recycleBinIds = new HashMap<>();
		
		JsonArray jsonArray =  formData.getAsJsonArray("recycleBinIds");
		
		if(jsonArray.isEmpty()) {
			throw new EfwServiceException("Please provide resource(s) to delete.");
		}
		
		List<RecycleBinDTO> selected = new ArrayList<>();
		
		for(Object eachResource : jsonArray) {
			Long recycleBinId = Long.valueOf(""+eachResource);
			
			if (!recycleBinService.isRecycleBinPresent(recycleBinId)) {
				continue;
			}
			selected.add(recycleBinService.getHIRecycleBinById(recycleBinId));
		}
		
		if(selected.isEmpty()) {
			throw new EfwServiceException("Please provide resource(s) to delete.");
		}
		
		boolean force = GsonUtility.optBooleanValue(formData, "force", false);
		
		
		PurgeEligibility eligibility = purgeEligibility.evaluate(selected, force);
		
		recycleBinIds.put(COMPLETED, new LinkedHashSet<>());
		recycleBinIds.put(INCOMPLETE, new LinkedHashSet<>(eligibility.getBlocked()));
		
		Map<Long,Boolean> deletedStatusMap = new HashMap<>();
		
		selected.forEach(bin -> deletedStatusMap.put(bin.getRecycleBinId(), false));
		
		int size = jsonArray.size();
		Set<Long> purged = purgePlanner.purge(selected, eligibility, deletedStatusMap, force);
		recycleBinIds.get(COMPLETED).addAll(purged);

		for (RecycleBinDTO item : selected) {
			Long id = item.getRecycleBinId();
			if (eligibility.isEligible(id) && !purged.contains(id)) {
				recycleBinIds.get(INCOMPLETE).add(id);
			}
		}
		
		response.putPOJO("recycleBin", recycleBinIds);
		boolean hasAnyIncomplete = !recycleBinIds.get(INCOMPLETE).isEmpty();
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
