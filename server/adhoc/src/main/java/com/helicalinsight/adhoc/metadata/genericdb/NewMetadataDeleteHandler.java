package com.helicalinsight.adhoc.metadata.genericdb;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;


/**
 * Provides functionality to handle deletion of metadata and associated reports.
 * This component deletes metadata files and their associated reports based on the provided parameters.
 */
public class NewMetadataDeleteHandler implements IComponent {

	/**
     * Indicates whether this component is thread-safe to cache or not.
     * @return true thread-safe to cache.
     */
	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}
	/**
     * Executes the component logic to handle deletion of metadata and associated reports.
     * method retrieves associated reports for a given metadata file using the ReportsRelatedToMetadata component.
     * a metadata file names, associated report file names, and the deletion type, occurs within the MetadataDeleteHandlerWithType component,
     * @param jsonFormData 		 JSON data containing location, metadata, 
     * @return a JSON string containing the deletion status message
     */
	@Override
	public String executeComponent(String jsonFormData) {
		JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String location = formJson.get("location").getAsString();
        String metadataFileName = formJson.get("metadataFileName").getAsString();
        String type = formJson.get("type").getAsString();
        
        
        IComponent dataSourceRelatedComponent = FactoryMethodWrapper
				.getTypedInstance("com.helicalinsight.adhoc.services.ReportsRelatedToMetadata", IComponent.class);
		JsonObject requestFormData = new JsonObject();
		requestFormData.addProperty("metadataFileName", metadataFileName);
		requestFormData.addProperty("classifier", "metadata");
		requestFormData.addProperty("location",location);
		
		String dataSourceRelatedFiles = dataSourceRelatedComponent.executeComponent(requestFormData.toString());
		JsonObject dataSourceRelatedFilesJson = JsonParser.parseString(dataSourceRelatedFiles).getAsJsonObject();
		JsonArray adhocReportsArray = GsonUtility.optJsonArray(dataSourceRelatedFilesJson,"adhocReports");
		
		JsonArray requestedMeatadataNamesArray = new JsonArray();
		requestedMeatadataNamesArray.add(location +File.separator + metadataFileName);
		JsonArray requestedReportsNamesArray = new JsonArray();
		JsonObject metadataDeleteComponentRequest = new JsonObject();
		for (int index = 0; index < adhocReportsArray.size(); index++) {
			JsonObject singlereport =adhocReportsArray.get(index).getAsJsonObject();
			String reportFileName =singlereport.get("reportFileName").getAsString();
			String reportFileLocation =singlereport.get("location").getAsString();
			String reportFileWithLocation =reportFileLocation+File.separator+reportFileName;
			requestedReportsNamesArray.add(reportFileWithLocation);
			if(singlereport.has("savedReports")){
				JsonArray savedReportsJson =singlereport.getAsJsonArray("savedReports");
				for(int indexOfValue =0; indexOfValue<savedReportsJson.size();indexOfValue++){
					JsonObject singleSavedValue =savedReportsJson.get(indexOfValue).getAsJsonObject();
					requestedReportsNamesArray.add(singleSavedValue.get("sheduledReportName").getAsString());
				}
			}
		}
		metadataDeleteComponentRequest.add("metadataFileName", requestedMeatadataNamesArray);
		metadataDeleteComponentRequest.addProperty("type", type);
		metadataDeleteComponentRequest.add("reportFileNames", requestedReportsNamesArray);
		IComponent metadataDeleteHandlerComponent = FactoryMethodWrapper.getTypedInstance(
				"com.helicalinsight.adhoc.metadata.genericdb.MetadataDeleteHandlerWithType", IComponent.class);
        
		
		String metadataDeleteHandlerMessage = metadataDeleteHandlerComponent
				.executeComponent(metadataDeleteComponentRequest.toString());
      

        return metadataDeleteHandlerMessage.toString();
	}




}
