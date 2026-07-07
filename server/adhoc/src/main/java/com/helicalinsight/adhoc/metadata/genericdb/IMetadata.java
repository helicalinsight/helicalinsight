package com.helicalinsight.adhoc.metadata.genericdb;

import com.helicalinsight.efw.framework.FrameworkObject;

import java.util.Map;

/**
 * Interface for accessing metadata related to generic databases.
 * Implementations of this interface provide methods to retrieve metadata in various formats.
 * 
 * Created by author on 27-02-2015.
 * @author Rajasekhar
 */
public interface IMetadata extends FrameworkObject {
	/**
     * Retrieves metadata in JSON format based on the provided information.
     *
     * @param jsonInformation 		 JSON string containing information required for metadata retrieval.
     * @return a JSON string representing the retrieved metadata
     */
    String getMetadata(String jsonInformation);
    /**
     * Retrieves metadata related to the database based on the provided information.
     * This method provides a default implementation that returns null.
     *
     * @param jsonInformation		a map containing information required for metadata retrieval
     * @return a string representing the retrieved metadata related to the database
     */
    default String getMetadataDB(Map<String,Object> jsonInformation){
        return null;
    }

}
