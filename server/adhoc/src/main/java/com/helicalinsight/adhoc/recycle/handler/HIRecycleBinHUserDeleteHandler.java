package com.helicalinsight.adhoc.recycle.handler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.UserService;

/**
 * HIRecycleBinHUserDeleteHandler
 * 
 * This class handles the deletion of users from the recycle bin.
 * It implements the {@link RecycleBinHandler} interface.
 */
@Component("h_users_deleteHandler")
public class HIRecycleBinHUserDeleteHandler implements RecycleBinHandler {

	
	@Autowired
	@Qualifier("userDetailsService")
	private UserService userService;
	
	@Autowired
	private HIRecycleBinService recycleBinService;
	/**
     * Handles the deletion of a single HIRecycleBin entry.
     * Deletes the HIRecycleBin entry and the associated user.
     * 
     * @param bin 		 HIRecycleBin entry to be handled
     * @return {@code true} if the deletion is successful, {@code false} otherwise
     */
	@Override
	public boolean handle(RecycleBinDTO bin) {
		recycleBinService.delete(bin.getRecycleBinId());
		userService.deleteUser(bin.getResourceId());
		return true;
	}
	/**
     * Handles the deletion of a single HIRecycleBin entry and updates a map.
     * Deletes the HIRecycleBin entry and the associated user.
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
