package com.helicalinsight.adhoc;

import com.helicalinsight.admin.dao.HIRecycleBinDao;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.core.request.RecycleBinResourceItem;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.helicalinsight.adhoc.exception.OwnershipTransferException;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The {@code GlobalConnectionOwnershipChangeHandler} class is part of the ownership change.
 * It handles the transfer of ownership for global connections based on specified rules.
 * it extends {@link AbstractOwnershipChangeHandler}
 */
@Component("ds_global_connections_ownershipChangeHandler")
public class GlobalConnectionOwnershipChangeHandler extends AbstractOwnershipChangeHandler{

	@Autowired
	private GlobalConnectionService globalConnectionService;

	@Autowired
	private HIResourceServiceDB serviceDb;

	@Autowired
	private HIRecycleBinService recycleBinDao;
	
	/**
     * Handles the ownership change for global connections.
     *
     * @param globalId 		 ID of the global connection.
     * @param ownerId  		 ID of the new owner.
     * @return {@code true} if ownership change is successful.
     * @throws OwnershipTransferException 	if ownership change is not possible.
     */
	@Override
	boolean change(Integer globalId, Integer ownerId) {
		GlobalConnections globalConnections = requestedFromAdminRole()? 
				globalConnectionService.getDeletedGlobalConnectionById(globalId)
				:globalConnectionService.findGlobalConnectionById(globalId); 
		
		if(globalConnections == null ) {
			throw new OwnershipTransferException("Datasource not found.");
		}
		
		Integer currentOwnerId = Integer.valueOf(globalConnections.getCreatedBy());
		
		if(currentOwnerId.equals(ownerId)) {
			throw new OwnershipTransferException("The ownership of the resource(s) cannot be changed to the same user, as it is already assigned. The ownership will remain unchanged.");
		}
		
		User user = userService.findUser(ownerId);
		if(!isAdmin(user)) {
			throw new OwnershipTransferException("The user doesn't have admin rights. Please grant them admin privileges and try again.");
		}

		Map<String, List<Object>> associatedFiles = recycleBinDao.getGlobalConnectionResources(globalId, Integer.valueOf(globalConnections.getCreatedBy()));
		if (associatedFiles == null) {
			throw new OwnershipTransferException("Failed to retrieve associated files for the Global connection.");
		}

		User targetUser = userService.getUser(ownerId);
		if (targetUser == null) {
			throw new OwnershipTransferException("Target user not found.");
		}
		List<String> roleIds = targetUser.getRoles().stream()
				.map(role -> String.valueOf(role.getId()))
				.collect(Collectors.toList());



		HIResourceOfActiveUser allResourcesOfAnyUsr = serviceDb.getAllResourcesOfAnyUsr(ownerId, roleIds, targetUser.getOrg_id());
		List<Integer> listOfPermittedResource = (allResourcesOfAnyUsr != null && allResourcesOfAnyUsr.getResourceDTOList() != null)
				? allResourcesOfAnyUsr.getResourceDTOList().stream().map(HIResourceDTO::getResourceId).collect(Collectors.toList())
				: new ArrayList<>();

		List<Object> resources = associatedFiles.get("resources");
		List<Integer> listOfAssociatedResourceId = (resources != null)
				? resources.stream().map(obj -> ((RecycleBinResourceItem) obj).getResourceId()).collect(Collectors.toList())
				: new ArrayList<>();

// Validate ownership eligibility
		if (!listOfAssociatedResourceId.isEmpty() && !listOfPermittedResource.containsAll(listOfAssociatedResourceId)) {
			throw new OwnershipTransferException("Ownership transfer failed. Not all associated files are available for the new owner.");
		}

		globalConnections.setCreatedBy(""+user.getId());
		globalConnectionService.editGlobalConnections(globalConnections);
		return true;
	}

}
