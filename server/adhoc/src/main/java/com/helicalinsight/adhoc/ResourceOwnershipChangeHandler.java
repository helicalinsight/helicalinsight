package com.helicalinsight.adhoc;

import com.helicalinsight.admin.dao.HIRecycleBinDao;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.core.request.RecycleBinResourceItem;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.helicalinsight.adhoc.exception.OwnershipTransferException;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.exceptions.EfwServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class Manages ownership transfer of resources in HI_RESOURCE_DB. Extends {@link AbstractOwnershipChangeHandler}.
 */
@Component("hi_resource_db_ownershipChangeHandler")
public  class ResourceOwnershipChangeHandler extends AbstractOwnershipChangeHandler {

	@Autowired
	private HIResourceServiceDB serviceDb;

	@Autowired
	@Qualifier("userDetailsService")
	private UserService userService;



	@Autowired
	private HIRecycleBinService recycleBinDao;
	/**
     * Handles the ownership change of the specified resource within the HI_RESOURCE_DB. It validates the resource,
     * checks for admin rights, and updates the owner information in the database.
     *
     * @param resourceId 						id of the resource to undergo ownership change.
     * @param ownerId    						id of the new owner for the resource.
     * @return {@code true} if the ownership change is successful; otherwise, an exception is thrown.
     * @throws OwnershipTransferException 		If an error occurs during the ownership transfer process.
     */
	@Override
	boolean change(Integer resourceId, Integer ownerId) {
		
		HIResource resource = requestedFromAdminRole()?serviceDb.getResourceByIdIgnoreFilter(resourceId)
				:serviceDb.getHIResourceById(resourceId);
		
		if(resource == null ) {
			throw new OwnershipTransferException("Resource not found.");
		}
		
		Integer parentId = resource.getParentId();
		if(parentId != null) {
			try {
				serviceDb.getHIResourceById(parentId);
			}
			catch (EfwServiceException e) {
					throw new EfwServiceException("Can not change ownership , since the parent of the resource has been deleted.");
			}
		}
		Integer oldOwner = resource.getCreatedBy();
		if( oldOwner != null &&  oldOwner.equals(ownerId) ) {
			throw new OwnershipTransferException("The ownership of the resource(s) cannot be changed to the same user, as it is already assigned. The ownership will remain unchanged.");
		}
		User user = userService.findUser(ownerId);
		if(!isAdmin(user) && ".metadata".equalsIgnoreCase(resource.getResourceType().getExtension())) {
			throw new OwnershipTransferException("The user doesn't have admin rights. Please grant them admin privileges and try again.");
		}

		Map<String, List<Object>> associatedFiles = recycleBinDao.prepareHIResources(resource, false, resource.getCreatedBy());
		if (associatedFiles == null) {
			throw new OwnershipTransferException("Failed to retrieve associated files.");
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
		resource.setCreatedBy(user.getId());
		serviceDb.editHIResource(resource);
		return true;
	}

}
