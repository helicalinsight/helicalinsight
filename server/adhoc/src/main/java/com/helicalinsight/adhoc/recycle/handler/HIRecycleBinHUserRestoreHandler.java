package com.helicalinsight.adhoc.recycle.handler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.UserService;

/**
 * HIRecycleBinHUserRestoreHandler
 * 
 * This class handles the restoration of users from the recycle bin.
 * It implements the RecycleBinHandler interface.
 */
@Component("h_users_restoreHandler")
public class HIRecycleBinHUserRestoreHandler implements RecycleBinHandler {

	@Autowired
	@Qualifier("userDetailsService")
	private UserService userService;
	
	@Autowired
	private HIRecycleBinService recycleBinService;
	/**
	 * @deprecated this due to performance issue. Use {@link #handle(RecycleBinDTO, Map)} instead.
     * Handles the restoration of a single HIRecycleBin entry.
     * Restores the HIRecycleBin entry and marks the associated user as not deleted.
     * 
     * @param bin 				 HIRecycleBin entry to be handled
     * @param completed 		 map to be updated after handling the entry
     * @return {@code true} if the restoration is successful, {@code false} otherwise
     */
	@Deprecated
	@Override
	public boolean handle(HIRecycleBin bin, Map<Long,Boolean> completed) {
		User user = bin.getHiRecycleBinHUsers().getUser();
		user.setDeleted(false);
		userService.editUser(user);
		completed.put(bin.getId(),true);
		return recycleBinService.delete(bin.getId());
	}
	
	@Override
	public boolean handle(RecycleBinDTO bin, Map<Long,Boolean> completed) {
		userService.restoreUser(bin.getResourceId());
		completed.put(bin.getRecycleBinId(),true);
		return recycleBinService.delete(bin.getRecycleBinId());
	}

}
