package com.helicalinsight.adhoc;

import java.util.Map;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

/**
 * The `OwnershipChangeHandlerFactory` class is a factory class responsible for mapping different resource types to their
 * corresponding ownership change handler implementations.
 */
public class OwnershipChangeHandlerFactory {
	
	private OwnershipChangeHandlerFactory() {
		
	}
	// Map associating resource types with their corresponding database identifiers
	private static final Map<String,String> map = Map.of(
			"folders","HI_RESOURCE_DB",
			"files", "HI_RESOURCE_DB",
			"datasources[managed]","DS_GLOBAL_CONNECTIONS",
			"datasources[efwd]","HI_EFWD_CONNECTION"
			);
	/**
     * Gets an instance of the {@link AbstractOwnershipChangeHandler} based on the specified type.
     *
     * @param type 			 type of the resource for which ownership change is requested.
     * @return An instance of the corresponding ownership change handler, or {@code null} if not found.
     */
	public static AbstractOwnershipChangeHandler getInstance(String type) {
		if(map.containsKey(type.toLowerCase())) {
			return (AbstractOwnershipChangeHandler) ApplicationContextAccessor.getBean(map.get(type.toLowerCase()).toLowerCase()+"_ownershipChangeHandler");
		}
		else {
			return null;
		}
	}

}
