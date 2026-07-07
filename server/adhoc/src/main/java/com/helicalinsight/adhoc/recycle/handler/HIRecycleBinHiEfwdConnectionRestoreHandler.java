package com.helicalinsight.adhoc.recycle.handler;

import java.util.Map;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.datasource.service.EFWDConnectionService;
/**
 * HIRecycleBinHiEfwdConnectionRestoreHandler
 * This class handles the restoration of HIEFWD connections from the recycle bin.
 * It implements the RecycleBinHandler interface.
 */
@Component("hi_efwd_connection_restoreHandler")
public class HIRecycleBinHiEfwdConnectionRestoreHandler implements RecycleBinHandler {

	@Autowired
	private EFWDConnectionService connectionService;
	
	@Autowired
	private HIRecycleBinService recycleBinService;
	
	/**
	 *  @deprecated this due to performance issue. Use {@link #handle(RecycleBinDTO, Map)} instead.
     * Handles the restoration of a single HIRecycleBin entry.
     * Restores the HIRecycleBin entry and marks the associated HI EFWD connection as not deleted.
     * 
     * @param bin             HIRecycleBin entry to be handled
     * @param completed       map to be updated after handling the entry
     * @return true if the restoration is successful, false otherwise
     */
	@Transactional
	@Override
	public boolean handle(HIRecycleBin bin,Map<Long,Boolean> completed) {
		HIEfwdConnection connection = bin.getHiRecycleBinHIEfwdConnection().getEfwdConnection();
		connection.setDeleted(false);
		connectionService.edit(connection);
		completed.put(bin.getId(),true);
		return recycleBinService.delete(bin.getId());
	}
	
	@Transactional
	@Override
	public boolean handle(RecycleBinDTO bin,Map<Long,Boolean> completed) {
		connectionService.restoreConnection(bin.getResourceId());
		completed.put(bin.getRecycleBinId(),true);
		return recycleBinService.delete(bin.getRecycleBinId());
	}

}
