package com.helicalinsight.adhoc.recycle.handler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.OrganizationService;
/**
 * HIRecycleBinOrganizationRestoreHandler
 * 
 * This class handles the restoration of organizations from the recycle bin.
 * It implements the {@link RecycleBinHandler} interface.
 */
@Component("organization_restoreHandler")
public class HIRecycleBinOrganizationRestoreHandler implements RecycleBinHandler {

	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private HIRecycleBinService recycleBinService;
	 /**
	  * @deprecated this due to performance issue. Use {@link #handle(RecycleBinDTO, Map)} instead.
     * Handles the restoration of a single HIRecycleBin entry.
     * Restores the HIRecycleBin entry and marks the associated organization as not deleted.
     * 
     * @param bin 			 HIRecycleBin entry to be handled
     * @param completed 	 map to be updated after handling the entry
     * @return {@code true} if the restoration is successful, {@code false} otherwise
     */
	@Override
	public boolean handle(HIRecycleBin bin,Map<Long,Boolean> completed) {
		Organization org = bin.getHiRecycleBinOrganization().getOrganization();
		org.setDeleted(false);
		Long id = bin.getId();
		organizationService.edit(org);
		recycleBinService.delete(id);
		completed.put(id,true);
		return true;
	}
	
	@Override
	public boolean handle(RecycleBinDTO bin,Map<Long,Boolean> completed) {
		organizationService.restoreOrganization(bin.getResourceId());
		recycleBinService.delete(bin.getRecycleBinId());
		completed.put(bin.getRecycleBinId(),true);
		return true;
	}
}
