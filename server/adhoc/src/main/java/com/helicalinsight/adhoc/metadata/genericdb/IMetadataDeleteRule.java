package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
/**
 * The IMetadataDeleteRule interface defines a contract for deleting metadata files and their associated reports.
 */
public interface IMetadataDeleteRule {
	/**
     * Deletes metadata files and their associated reports based on the provided metadata file names and report 
     * file names. It then returns a response JSON string indicating the status of the deletion operation.
     *
     * @param metadataFileName 			array containing the names of the metadata files to be deleted.
     * @param responseJson     			JsonObject to which the response message will be added.
     * @param reportFileName   			array containing the names of the report files .
     * @return A response JSON string indicating the status of the deletion operation.
     */
	String deleteMetadata(JsonArray metadataFileName, JsonObject responseJson, JsonArray reportFileName);
}
