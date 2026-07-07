package com.helicalinsight.adhoc.recycle.handler;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
/**
 * HIRecycleBinOrganizationDeleteHandler
 * 
 * This class handles the deletion of organizations from the recycle bin.
 * It implements the RecycleBinHandler interface.
 */
@Component("organization_deleteHandler")
public class HIRecycleBinOrganizationDeleteHandler implements RecycleBinHandler {

	private static final Logger logger = LoggerFactory.getLogger(HIRecycleBinOrganizationDeleteHandler.class);
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private HIRecycleBinService recycleBinService;
	
	@Qualifier("userDetailsService")
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	/**
     * Handles the deletion of a single HIRecycleBin entry.
     * Deletes the HIRecycleBin entry, organization, users, and roles associated with the organization.
     * 
     * @param bin 		 HIRecycleBin entry to be handled
     * @return true if the deletion is successful, false otherwise
     */
	@Override
	public boolean handle(RecycleBinDTO bin) {
		Integer organizationId = bin.getResourceId();
		recycleBinService.delete(bin.getRecycleBinId());
		List<User> users =  userService.getAllUsersOfOrganization(organizationId);
		for( User user : users ) {
			int userId = user.getId();
			try {
				HIRecycleBin binItem =  recycleBinService.findHIRecycleBinByUserId(userId);
				recycleBinService.delete(binItem.getId());
			}
			catch (Exception e) {
				logger.info("RecycleBin item not found.");
			}
			userService.deleteUser(user.getId());
		}
		roleService.deleteOrganization(organizationId);
        organizationService.delete(organizationId);
		return true;
	}
	/**
     * Handles the deletion of a single HIRecycleBin entry and updates a map.
     * Deletes the HIRecycleBin entry, organization, users, and roles associated with the organization.
     * 
     * @param bin 			 HIRecycleBin entry to be handled
     * @param map 			 map to be updated after handling the entry
     * @return {@code true} if the deletion is successful, {@code false} otherwise
     */
	public boolean handle(RecycleBinDTO bin,Map<Long,Boolean> map) {
		handle(bin);
		return true;
	}

}
