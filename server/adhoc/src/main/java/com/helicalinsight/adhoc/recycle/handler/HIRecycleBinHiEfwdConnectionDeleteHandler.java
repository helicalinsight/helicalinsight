package com.helicalinsight.adhoc.recycle.handler;

import java.util.HashMap;
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
 * HIRecycleBinHiEfwdConnectionDeleteHandler
 * 
 * This class handles the deletion of HIEFWD connections from the recycle bin.
 * It implements the RecycleBinHandler interface.
 */
@Component("hi_efwd_connection_deleteHandler")
public class HIRecycleBinHiEfwdConnectionDeleteHandler implements RecycleBinHandler {

	
	@Autowired
	private  EFWDConnectionService  connectionService;
	
	@Autowired
	private HIRecycleBinService recycleBinService;
	/**
     * Handles the deletion of a single HIRecycleBin entry.
     * Deletes the HIRecycleBin entry and the associated HI EFWD connection.
     * 
     * @param bin 			 HIRecycleBin entry provides id
     * @return {@code true} if the deletion is successful, {@code false} otherwise
     */
	@Transactional
	@Override
	public boolean handle(RecycleBinDTO bin) {
		handle(bin,new HashMap<Long,Boolean>());
		return true;
	}
	/**
     * Handles the deletion of a single HIRecycleBin entry and updates a map.
     * Deletes the HIRecycleBin entry and the associated HI EFWD connection.
     * 
     * @param bin 			    HIRecycleBin entry to be handled
     * @param deleteStatusMap   map to be updated after handling the entry
     * @return {@code true} if the deletion is successful, {@code false} otherwise
     */
	public boolean handle(RecycleBinDTO bin,Map<Long,Boolean> deleteStatusMap) {
		if(!deleteStatusMap.isEmpty() && deleteStatusMap.containsKey(bin.getRecycleBinId()))
			deleteStatusMap.replace(bin.getRecycleBinId(), true);
		recycleBinService.delete(bin.getRecycleBinId());
		connectionService.deleteEFConnectionById(bin.getResourceId());
		return true;
	}

}
