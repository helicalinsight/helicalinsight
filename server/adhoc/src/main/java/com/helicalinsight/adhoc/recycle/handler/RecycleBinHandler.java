package com.helicalinsight.adhoc.recycle.handler;

import java.util.List;
import java.util.Map;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;

/**
 * RecycleBinHandler
 * 
 * This interface defines methods to handle operations on the recycle bin entries.
 * Classes implementing this interface can define their own logic for handling recycle bin entries.
 */
public interface RecycleBinHandler {
	/**
	 * Handles the operation for a HIRecycleBin entry.
	 * 
	 * @param bin       HIRecycleBin entry to be handled
	 * @return {@code true} if the operation is successful, {@code false} otherwise
	 */
	default boolean handle(HIRecycleBin bin) {
		return false;
	}

	default boolean handle(RecycleBinDTO bin) {
		return false;
	}
	
	/**
	 * Handles the operation for a single HIRecycleBin entry with a list of
	 * completed entries.
	 * 
	 * @param bin       	 HIRecycleBin entry to be handled
	 * @param completed	     list to store the IDs of completed entries
	 * @return {@code true} if the operation is successful, {@code false} otherwise
	 */
	default boolean handle(HIRecycleBin bin, List<Long> completed) {
		return false;
	}

	/**
	 * Handles the operation for a HIRecycleBin entry with a map of completed
	 * entries.
	 * 
	 * @param bin 		 HIRecycleBin entry to be handled
	 * @param map 		 map to store the status of completed entries
	 * @return {@code true} if the operation is successful, {@code false} otherwise
	 */
	default boolean handle(HIRecycleBin bin, Map<Long, Boolean> map) {
		return true;
	}
	
	default boolean handle(RecycleBinDTO bin, Map<Long,Boolean> map) {
		return true;
	}
}
