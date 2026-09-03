package com.helicalinsight.adhoc.recycle.action;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.adhoc.recycle.PurgeEligibility;
import com.helicalinsight.adhoc.recycle.RecycleBinPurgeEligibility;
import com.helicalinsight.adhoc.recycle.RecycleBinPurgePlanner;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.datasource.GsonUtility;


/**
* The {@code RecycleBinClearAction} class extends the {@code RecycleBinAction} to provide
* functionality for clearing items from the recycle bin. It utilizes the {@code HIRecycleBinService}
* to retrieve all items from the recycle bin and the {@code Deletable} class to check if
* items can be deleted.
*/
@Component("clearActionComponent")
public class RecycleBinClearAction extends RecycleBinAction {
	
	private static final Logger logger = LoggerFactory.getLogger(RecycleBinClearAction.class);
	
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
		
		List<RecycleBinDTO> items = recycleBinService.getAll();
		
		if (items.isEmpty()) {
			response.put("message", "RecycleBin is Emtpy!");
			return response.toString();
		}
		
		boolean force = GsonUtility.optBooleanValue(formData, "force", false);
		
		logger.debug("Forceful clear requested: {}", force);
		
		
		PurgeEligibility eligibility = purgeEligibility.evaluate(items, force);
		
		Map<Long,Boolean> deleteStatusMap=new HashMap<>();
		
		items.forEach(rb-> deleteStatusMap.put(rb.getRecycleBinId(), false));
		
		
		Map<String, Set<Long>> recycleBinIds = new HashMap<>();
		recycleBinIds.put(COMPLETED, new LinkedHashSet<>());
		recycleBinIds.put(INCOMPLETE, new LinkedHashSet<>(eligibility.getBlocked()));
		
		Set<Long> purged  = purgePlanner.purge(items, eligibility, deleteStatusMap,force);
		recycleBinIds.get(COMPLETED).addAll(purged);
		
		for (RecycleBinDTO item : items) {
			Long recycleBinId = item.getRecycleBinId();
			if (eligibility.isEligible(recycleBinId) && !purged.contains(recycleBinId)) {
				recycleBinIds.get(INCOMPLETE).add(recycleBinId);
			}
		}

		response.putPOJO("recycleBin", recycleBinIds);
		boolean hasAnyIncomplete = !recycleBinIds.get(INCOMPLETE).isEmpty();
		String message = "";

		if (hasAnyIncomplete) {
			message = "The clear operation was not completed, because some of the files linked to it are not in deleted state, Please delete them manually.";
		} else {
			message = "Resource(s) deleted successfully.";
		}
		response.put("message", message);

		return response.toString();
	}
}
