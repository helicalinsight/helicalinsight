package com.helicalinsight.adhoc;

import com.helicalinsight.adhoc.exception.OwnershipTransferException;
import com.helicalinsight.admin.dao.HIRecycleBinDao;
import com.helicalinsight.admin.model.HIEFWD;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.core.request.RecycleBinResourceItem;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The {@code EfwdConnectionOwnershipChangeHandler} class is an extends the {@link AbstractOwnershipChangeHandler}
 * and serves as the ownership change specifically for EFWD Connections. It provides functionality
 * for transferring ownership of an EFWD Connection to another user.
 */
@Component("hi_efwd_connection_ownershipChangeHandler")
public class EfwdConnectionOwnershipChangeHandler extends AbstractOwnershipChangeHandler {

    @Autowired
    private EFWDConnectionService efwdConnectionService;

    @Autowired
    private HIResourceServiceDB serviceDb;


    @Autowired
    private HIRecycleBinService recycleBinDao;

    /**
     * Changes the ownership of the specified EFWD Connection to the provided owner.
     *
     * @param efwdId  ID of the EFWD Connection to change ownership.
     * @param ownerId ID of the new owner to whom the ownership will be transferred.
     * @return {@code true} if the ownership change is successful, {@code false} otherwise.
     * @throws OwnershipTransferException If there is an issue with the ownership transfer.
     */
    @Override
    public boolean change(Integer efwdId, Integer ownerId) {
        HIEfwdConnection connection = requestedFromAdminRole() ?
                efwdConnectionService.findConnectionById("" + efwdId, false)
                : efwdConnectionService.findConnectionById("" + efwdId);

        Integer currentOwnerId = connection.getHiResourceEFWD().getCreatedBy();
        if (currentOwnerId.equals(ownerId)) {
            throw new OwnershipTransferException("The ownership of the resource(s) cannot be changed to the same user, as it is already assigned. The ownership will remain unchanged.");
        }
        User user = userService.findUser(ownerId);
        if (!isAdmin(user)) {
            throw new OwnershipTransferException("The user doesn't have admin rights. Please grant them admin privileges and try again.");
        }


        HIEFWD efwdResource = connection.getHiResourceEFWD();
        Map<String, List<Object>> associatedFiles = recycleBinDao.getEfwdConnectionResources(efwdId, efwdResource.getCreatedBy());
        if (associatedFiles == null) {
            throw new OwnershipTransferException("Failed to retrieve associated files for the EFWD connection.");
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
                ? resources.stream().map(obj -> ((RecycleBinResourceItem) obj).getResourceId()).toList()
                : new ArrayList<>();

// Validate ownership eligibility
        if (!listOfAssociatedResourceId.isEmpty() && !listOfPermittedResource.containsAll(listOfAssociatedResourceId)) {
            throw new OwnershipTransferException("Ownership transfer failed. Not all associated files are available for the new owner.");
        }


        connection.getHiResourceEFWD().setCreatedBy(user.getId());
        efwdConnectionService.edit(connection);
        return true;
    }

}
