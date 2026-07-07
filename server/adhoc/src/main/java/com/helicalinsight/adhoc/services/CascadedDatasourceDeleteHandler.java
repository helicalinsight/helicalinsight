package com.helicalinsight.adhoc.services;

import java.io.File;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.DataSourceDeleteUtilsDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.components.EfwdDeleteUtility;
import com.helicalinsight.efw.exceptions.DuplicateDatasourceConnectionException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
/**
 * Handles the cascaded deletion of data sources and associated metadata.
 * Implements the {@link IDataSourceDeleteRule} interface.
 * 
 */
@Component
public class CascadedDatasourceDeleteHandler implements IDataSourceDeleteRule {

    private static final Logger logger = LoggerFactory.getLogger(CascadedDatasourceDeleteHandler.class);
    /**
     * Deletes the specified data source and associated metadata.
     * 
     * @param formDataJson 			 JSON object containing form data.
     * @param dataSourceProvider 	 data source provider.
     * @param id 					 ID of the data source.
     * @return A status message indicating the outcome of the deletion process.
     */
    @Override
    public String deleteDataSource(JsonObject formDataJson, String dataSourceProvider, String id) {
        JsonObject dataSourceRelatedFilesJson = providerDatasourceRelatedFiles(id, null, null);
        boolean isDataSourceDeleted;
        boolean isMetadataDeleted = false;
        //isMetadataDeleted = deleteMetadata(dataSourceRelatedFilesJson, isMetadataDeleted);

        String response = processDatasourceDelete(formDataJson, dataSourceProvider, id);
        //JSONObject response = prepareStatusMessage(isDataSourceDeleted, isMetadataDeleted, id);

        return response;

    }
    /**
     * Deletes the specified data source and associated metadata.
     * 
     * @param id 				 ID of the data source.
     * @param classifier 		 classifier.
     * @param type 				 type.
     * @param directory 		 directory.
     * @return A status message indicating the outcome of the deletion process.
     */
    public String deleteDataSource(String id, String classifier, String type, String directory) {
        JsonObject dataSourceRelatedFilesJson = providerDatasourceRelatedFiles(id, classifier, directory);
        boolean isDataSourceDeleted;
        boolean isMetadataDeleted = false;
        isMetadataDeleted = deleteMetadata(dataSourceRelatedFilesJson, isMetadataDeleted);

        try {
            EfwdDeleteUtility utility = new EfwdDeleteUtility(id, directory);
            utility.delete();
            isDataSourceDeleted = true;

        } catch (DuplicateDatasourceConnectionException e) {
            throw new RuntimeException("The given data source id is not exists in the directory " + directory, e);
        }

        JsonObject response = prepareStatusMessage(isDataSourceDeleted, isMetadataDeleted, id);

        return response.toString();
    }
    /**
     * Prepares the status message indicating the outcome of the deletion process.
     * 
     * @param isDatasourceDeleted 			 boolean indicating whether the data source was deleted successfully.
     * @param isMetadataDeleted 			 boolean indicating whether the metadata was deleted successfully.
     * @param dataSourceId 					 ID of the data source.
     * @return The status message JSON object.
     */
    private JsonObject prepareStatusMessage(boolean isDatasourceDeleted, boolean isMetadataDeleted, String dataSourceId) {
        String messageMetadata = isMetadataDeleted ? "Metadata deleted successfully " : " Could not delete metadata";
        String messageDatasource = isDatasourceDeleted ? "Datasource deleted successfully"
                : "Could not delete datasource";

        JsonObject response = new JsonObject();

        response.addProperty("message", messageMetadata + " " + messageDatasource);
        if (isDatasourceDeleted) {
            JsonObject data = new JsonObject();
            data.addProperty("id", dataSourceId);
            response.add("data", data);
        }
        return response;
    }
    /**
     * Deletes the associated metadata files.
     * 
     * @param dataSourceRelatedFilesJson 			 JSON object containing information about the associated files.
     * @param isMetadataDeleted 					 boolean indicating whether the metadata was deleted successfully.
     * @return A boolean indicating whether the metadata was deleted successfully.
     */
    private boolean deleteMetadata(JsonObject dataSourceRelatedFilesJson, boolean isMetadataDeleted) {
        if (dataSourceRelatedFilesJson.has("metadataFiles") && GsonUtility.optJsonArray(dataSourceRelatedFilesJson,"metadataFiles").size() != 0) {
            isMetadataDeleted = processMetadataDelete(dataSourceRelatedFilesJson);
        }
        return isMetadataDeleted;
    }
    /**
     * Retrieves information about the associated files of a data source.
     * 
     * @param id 				 ID of the data source.
     * @param classifier 		 classifier.
     * @param directory 		 directory.
     * @return A JSON object containing information about the associated files.
     */
    private JsonObject providerDatasourceRelatedFiles(String id, String classifier, String directory) {
        IComponent dataSourceRelatedComponent = FactoryMethodWrapper
                .getTypedInstance("com.helicalinsight.adhoc.services.DataSourceRelatedFiles", IComponent.class);

        JsonObject requestFormData = prepareFormDataForDatasourceRelatedFiles(id, classifier, directory);

        String dataSourceRelatedFiles = dataSourceRelatedComponent.executeComponent(requestFormData.toString());
        return JsonParser.parseString(dataSourceRelatedFiles).getAsJsonObject();
    }
    /**
     * Prepares form data for retrieving information about the associated files of a data source.
     * 
     * @param id 						 ID of the data source.
     * @param classifier 				 classifier.
     * @param directory 				 directory.
     * @return A JSON object containing the prepared form data.
     */
    private JsonObject prepareFormDataForDatasourceRelatedFiles(String id, String classifier, String directory) {
        JsonObject requestFormData = new JsonObject();
        requestFormData.addProperty("dataSourceId", id);
        requestFormData.addProperty("classifier", classifier != null ? "efwd" : "global");
        if (directory != null) {
            requestFormData.addProperty("location", directory);
        }
        return requestFormData;
    }

    /**
     * Processes the deletion of associated metadata files.
     * 
     * @param dataSourceRelatedFilesJson 			 JSON object containing information about the associated files.
     * @return A boolean indicating whether the metadata was deleted successfully.
     */
    private boolean processMetadataDelete(JsonObject dataSourceRelatedFilesJson) {
        boolean flag = false;
        String metadataDeleteHandlerMessage;
        IComponent metadataDeleteHandlerComponent = FactoryMethodWrapper.getTypedInstance(
                "com.helicalinsight.adhoc.metadata.genericdb.MetadataDeleteHandlerWithType", IComponent.class);
        JsonObject metadataDeleteComponentRequest = new JsonObject();
        JsonArray requestdMetadataToBeDelete = dataSourceRelatedFilesJson.getAsJsonArray("metadataFiles");
        JsonArray requestedMeatadataNamesArray = new JsonArray();
        JsonArray requestedReportsNamesArray = new JsonArray();

        for (int indexValue = 0; indexValue < requestdMetadataToBeDelete.size(); indexValue++) {
            JsonObject singleMetadata = requestdMetadataToBeDelete.get(indexValue).getAsJsonObject();
            String path = singleMetadata.get("path").getAsString();
            // String metadataName = singleMetadata.getString("Name");
            requestedMeatadataNamesArray.add(path);
            // location =path.replaceAll(metadataName, "");
            if (singleMetadata.has("reportDetails")) {
                JsonArray reportJsonArray = singleMetadata.getAsJsonArray("reportDetails");
                for (int index = 0; index < reportJsonArray.size(); index++) {
                    JsonObject singlereport = reportJsonArray.get(index).getAsJsonObject();
                    String reportFileName = singlereport.get("reportFileName").getAsString();
                    String reportFileLocation = singlereport.get("location").getAsString();
                    String reportFileWithLocation = reportFileLocation + File.separator + reportFileName;
                    requestedReportsNamesArray.add(reportFileWithLocation);
                    if (singlereport.has("savedReports")) {
                        JsonArray savedReportsJson = singlereport.getAsJsonArray("savedReports");
                        for (int indexOfValue = 0; indexOfValue < savedReportsJson.size(); indexOfValue++) {
                            JsonObject singleSavedValue = savedReportsJson.get(indexOfValue).getAsJsonObject();
                            requestedReportsNamesArray.add(singleSavedValue.get("sheduledReportName").getAsString());
                        }
                    }
                }
            }
        }
        // metadataDeleteComponentRequest.put("location", location);
        metadataDeleteComponentRequest.add("metadataFileName", requestedMeatadataNamesArray);
        metadataDeleteComponentRequest.addProperty("type", "cascade");
        metadataDeleteComponentRequest.add("reportFileNames", requestedReportsNamesArray);

        metadataDeleteHandlerMessage = metadataDeleteHandlerComponent
                .executeComponent(metadataDeleteComponentRequest.toString());
        JsonObject messageObj = JsonParser.parseString(metadataDeleteHandlerMessage).getAsJsonObject();
        if ("Metadata and related files deleted successfully.".equals(GsonUtility.optString(messageObj,"message"))) {
            flag = true;
        }

        return flag;
    }
    /**
     * Processes the deletion of the specified data source and associated metadata.
     * 
     * @param formDataJson 			 JSON object containing form data.
     * @param dataSourceProvider 	 data source provider.
     * @param id 					 ID of the data source.
     * @return A status message indicating the outcome of the deletion process.
     */
    private String processDatasourceDelete(JsonObject formDataJson, String dataSourceProvider, String id) {
        boolean flag = false;
        String dataSourceDeleteMessage = null;
        try {
            
//          dataSourceDeleteMessage = DataSourceDeleteUtils.marshal(dataSourceProvider, id, formDataJson, "delete");
            DataSourceDeleteUtilsDB dataSourceDeleteUtilsDB1 = new DataSourceDeleteUtilsDB();
            dataSourceDeleteMessage = dataSourceDeleteUtilsDB1.marshalDelete(dataSourceProvider, id, formDataJson, "delete");

            if (logger.isDebugEnabled()) {
                logger.debug("The deleted status of the global xml is " + dataSourceDeleteMessage);
            }

            if (dataSourceDeleteMessage != null) {
                flag = true;
            }
        } catch (Exception ex) {
            logger.error("The error is ", ex);
            throw new EfwServiceException("The data source could not be deleted with the. Cause " + ExceptionUtils.getRootCauseMessage(ex));
        }
        return dataSourceDeleteMessage;
    }

}
