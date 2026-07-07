package com.helicalinsight.adhoc.metadata.genericdb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.utils.FileDeleteUtils;

/**
 * The SimpleMetadataDeleteHandler class implements the {@link IMetadataDeleteRule} interface
 * and provides a simple implementation for deleting metadata and related files.
 */
@Component
public class SimpleMetadataDeleteHandler implements IMetadataDeleteRule {

	/**
     * Deletes metadata files based on the provided metadata file names.
     *
     * @param metadataFileName  	JSON array containing the names of the metadata files to be deleted.
     * @param responseJson     	    JSON object to which the response message will be added.
     * @param reportFileName    	JSON array not used in this method
     * @return A string representing the response JSON after the deletion operation.
     */
    @Override
    public String deleteMetadata(JsonArray metadataFileName, JsonObject responseJson, JsonArray reportFileName) {


        List<String> metadataList = new ArrayList<>();
        List<String> metadataFilePath = new ArrayList<>();
        List<String> deletedMetadataList = null;
        if (metadataFileName.size() != 0) {
            for (int index = 0; index < metadataFileName.size(); index++) {
                metadataList.add(metadataFileName.get(index).getAsString());
                metadataFilePath.add(metadataFileName.get(index).getAsString());
            }
            deletedMetadataList = FileDeleteUtils.deleteRequestedMetadata(metadataFilePath);
        }

        if (deletedMetadataList.size() > 0) {
            metadataList.removeAll(deletedMetadataList);
            responseJson.addProperty("message", "Metadata deleted successfully");
        } else {
            responseJson.addProperty("message", "Metadata cannot be deleted");
        }


        return responseJson.toString();
    }


}
