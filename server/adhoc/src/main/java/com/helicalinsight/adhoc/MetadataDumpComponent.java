package com.helicalinsight.adhoc;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataDBUtility;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.resourcedb.MetadataDumpDTO;

/**
 * The {@code MetadataDumpComponent} class is an implementation of the {@link IComponent} interface, providing
 * functionality to execute a metadata dump operation. It retrieves metadata from the metadata cache
 * and returns the result as a JSON string.
 */
public class MetadataDumpComponent implements IComponent {
	
	private static final Logger logger = LoggerFactory.getLogger(MetadataDumpComponent.class);
	/**
     * Retrieves cached metadata and returning it as a JSON-formatted string with a key "data" in the result.
     * @param jsonFormData 			form data containing parameters for the metadata dump operation.
     * @return A JSON-formatted string containing the dumped metadata.
     */
	@Override
	public String executeComponent(String jsonFormData) {
		
		ObjectNode node;
		try {
			node = new ObjectMapper().readValue(jsonFormData, ObjectNode.class);
		}
		catch (JsonProcessingException e) {
			logger.error("Error occurred while parsing json");
		}
		MetadataDBUtility metadataDBUtility = new MetadataDBUtility();
		List<MetadataDumpDTO> metadata = metadataDBUtility.getCachedMetadata();
		ObjectNode result = JsonNodeFactory.instance.objectNode();
		result.putPOJO("data", metadata);
		return result.toString();
	}
	/**
     * Indicates whether this component is thread-safe to be cached.
     * @return {@code true} if the component is thread-safe to be cached, {@code false} otherwise.
     */
	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}


}
