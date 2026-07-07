package com.helicalinsight.adhoc.recycle.handler;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import java.io.File;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.adhoc.utils.FlatFileDeleteUtils;
import com.helicalinsight.admin.dao.HIRecycleBinDao;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.datasource.service.GlobalConnectionService;

/**
 * HIRecycleBinGlobalConnectionDeleteHandler
 * 
 * This class handles the deletion of global connections from the recycle bin.
 * It implements the {@link RecycleBinHandler} interface.
 */
@Component("ds_global_connections_deleteHandler")
public class HIRecycleBinGlobalConnectionDeleteHandler implements RecycleBinHandler {

	
	@Autowired
	private GlobalConnectionService gConnectionService;
	
	@Autowired
	private HIRecycleBinService recycleBinService;
	/**
     * Handles the deletion of a single HIRecycleBin entry.
     * Deletes the HIRecycleBin entry and the associated global connection.
     * 
     * @param bin          HIRecycleBin entry to be handled
     * @return true if the deletion is successful, false otherwise
     */
	@Override
	public boolean handle(RecycleBinDTO bin) {
		Integer globalId = bin.getResourceId();
		if(gConnectionService.deleteGlobalConnections(globalId)){
			recycleBinService.delete(bin.getRecycleBinId());
		}else{
			throw new EfwServiceException("The resource could not be deleted, because some of the files linked to it are not in deleted state.");
		}
		//deleting file under the flatfile folder with corrospond to that connectionID
		FlatFileDeleteUtils.deleteRequestedFolders(String.valueOf(globalId));
		return true;
	}
	/**
     * Handles the deletion of a single HIRecycleBin entry and updates a map.
     * Deletes the HIRecycleBin entry and the associated global connection.
     * 
     * @param bin        HIRecycleBin entry to be handled
     * @param map        map to be updated after handling the entry
     * @return {@code true} if the deletion is successful, {@code false} otherwise
     */
	public boolean handle(RecycleBinDTO bin,Map<Long,Boolean> map) {
		handle(bin);
		return true;
	}

}
