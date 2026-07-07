package com.helicalinsight.adhoc;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;


/**
 * The `OwnershipChangeComponent` class is responsible for handling ownership changes of resources.
 * It allows the change of ownership for specified resources based on the provided type and new owner ID.
 */
public class OwnershipChangeComponent implements IComponent {
	/**
     * Executes the ownership change operation based on the provided JSON-formatted form data.
     *
     * @param jsonFormData 			JSON-formatted data containing information about the ownership change.
     * @return JSON-formatted response indicating the success of the ownership change operation.
     */
	@Override
	public String executeComponent(String jsonFormData) {
		ObjectNode response = JacksonUtility.emptyNode();
		ObjectNode formData = JacksonUtility.fromObject(jsonFormData);
		String type = formData.get("type").asText();
		ArrayNode resources = formData.withArray("resources");
		Integer newOwnerId = formData.get("newOwnerId").asInt();
		Map<String,String> parameters = new HashMap<>();
		
		parameters.put("type", type);
		parameters.put("resources", ""+resources);
		parameters.put("newOwnerId", ""+newOwnerId);
		
		ControllerUtils.checkForNullsAndEmptyParameters(parameters);
		if(resources.isEmpty()) {
			throw new EfwServiceException("Please provide resource(s) to change owner!");
		}
		AbstractOwnershipChangeHandler handler = OwnershipChangeHandlerFactory.getInstance(type);
		for(Object resource : resources) {
			handler.change(Integer.valueOf(resource.toString()), newOwnerId);
		}
		return response.put("message", "Successfully changed ownership of resource(s)").toString();
	}
	/**
     * Checks if the class is thread-safe to be cached. Always returns true for this class.
     * @return Always returns {@code true} as this class is designed to be thread-safe for caching.
     */
	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}
}