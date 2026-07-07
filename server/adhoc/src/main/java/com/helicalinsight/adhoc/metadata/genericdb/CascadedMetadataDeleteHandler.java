package com.helicalinsight.adhoc.metadata.genericdb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.utils.FileDeleteUtils;

/**
 * The CascadedMetadataDeleteHandler class implements the {@link IMetadataDeleteRule} interface to handle the cascaded 
 * deletion of metadata and related files. It deletes metadata files and their associated reports based on the 
 * provided metadata file names and report file names.
 */
@Component
public class CascadedMetadataDeleteHandler implements IMetadataDeleteRule {
	
	/**
     * Deletes metadata files and their associated reports based on the provided metadata file names and report 
     * file names. It then returns a response JSON string indicating the status of the deletion operation.
     *
     * @param metadataFileName 			array containing the names of the metadata files to be deleted.
     * @param responseJson     			JsonObject to which the response message will be added.
     * @param reportFileName   			array containing the names of the report files .
     * @return A response JSON string indicating the status of the deletion operation.
     */
	@Override
	public String deleteMetadata(JsonArray metadataFileName, JsonObject responseJson, JsonArray reportFileName) {
		return deleteMetadataCascade(  metadataFileName,  responseJson,reportFileName);
	}
	private String deleteMetadataCascade( JsonArray metadataFileName, JsonObject responseJson,JsonArray reportFileName) {
		List<String> metadataList = new ArrayList<>();
        List<String> metadataFilePath = new ArrayList<>();
        List<String> reportFileList = new ArrayList<>();
       
        if(!reportFileName.isEmpty()){
        for(int index1 =0 ;index1<reportFileName.size();index1++){
        	reportFileList.add(reportFileName.get(index1).getAsString());
        }
        int count =0;
        FileDeleteUtils.deleteReport(reportFileList,count);}
        
        List<String> deletedMetadataList =new ArrayList<>();
        if(!metadataFileName.isEmpty()){
            for(int index =0 ;index<metadataFileName.size();index++){
            metadataList.add(metadataFileName.get(index).getAsString());
            metadataFilePath.add(metadataFileName.get(index).getAsString());}
        
           deletedMetadataList = FileDeleteUtils.deleteRequestedMetadata(metadataFilePath);}
            if (!deletedMetadataList.isEmpty()) {
                metadataList.removeAll(deletedMetadataList);
                responseJson.addProperty("message", "Metadata and related files deleted successfully.");
            } else {
                responseJson.addProperty("message", "Metadata and related files can't be deleted.");
            }
     

        return responseJson.toString();
	}

}
