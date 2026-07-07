package com.helicalinsight.adhoc.recycle.handler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;

/**
 * HIRecycleBinGlobalConnectionRestoreHandler
 * 
 * This class handles the restoration of global connections from the recycle
 * bin. It implements the RecycleBinHandler interface.
 */
@Component("ds_global_connections_restoreHandler")
public class HIRecycleBinGlobalConnectionRestoreHandler implements RecycleBinHandler {

	@Autowired
	private GlobalConnectionService gConnectionService;

	@Autowired
	private HIRecycleBinService recycleBinService;

	/**
	 @deprecated this due to performance issue. Use {@link #handle(RecycleBinDTO, Map)} instead.
	 * Handles the restoration of a single HIRecycleBin entry. Restores the
	 * HIRecycleBin entry and marks the associated global connection as not deleted.
	 * 
	 * @param bin       	 HIRecycleBin entry to give id
	 * @param completed 	 map to be updated after handling the entry
	 * @return {@code true} if the restoration is successful, {@code false} otherwise
	 */
	@Deprecated(forRemoval = true)
	@Override
	public boolean handle(HIRecycleBin bin, Map<Long, Boolean> completed) {
		GlobalConnections connection = bin.getHiRecycleBinDsGlobalConnections().getGlobalConnection();
		connection.setDeleted(false);
		gConnectionService.editGlobalConnections(connection);
		completed.put(bin.getId(), true);
		return recycleBinService.delete(bin.getId());
	}
	
	/**
	 * Handles the restoration of a single HIRecycleBin entry. Restores the
	 * HIRecycleBin entry and marks the associated global connection as not deleted.
	 * 
	 * @param bin       	 HIRecycleBin entry to give id
	 * @param completed 	 map to be updated after handling the entry
	 * @return {@code true} if the restoration is successful, {@code false} otherwise
	 */
	@Override
	public  boolean handle(RecycleBinDTO bin, Map<Long,Boolean> completed) {
		GlobalConnections connection = gConnectionService.findGlobalConnectionById(bin.getResourceId(), false);
		connection.setDeleted(false);
		gConnectionService.editGlobalConnections(connection);
		completed.put(bin.getRecycleBinId(), true);
		return recycleBinService.delete(bin.getRecycleBinId());
	}

}
