package com.helicalinsight.adhoc.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;

import java.io.File;

/**
 * @author helical019
 * <p>This Component will provide all *.metadata, *.report, *.efwsr files detailes based on the datasourceId. </p>
 *  DataSourceRelatedFiles class implements {@link IComponent} . 
 */
@SuppressWarnings("unused")
public class DataSourceRelatedFiles implements IComponent {
    private static ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    private static String solutionDirectory = applicationProperties.getSolutionDirectory();

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * This method helps in extracting file based on datasourceId and efwdId and also extracts metadata object.
     * and prepares metadata using connectionId, file name and directory.
     * @param jsonFormData      formData provides dataSourceId, classifier, location of file etc.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formDataJson = JsonParser.parseString(jsonFormData).getAsJsonObject();

        IComponent reportStatsProvider = FactoryMethodWrapper
                .getTypedInstance("com.helicalinsight.admin.management.ReportStatisticsProvider", IComponent.class);
        JsonObject formdataObject = AdhocServiceUtils.prepareFormData();
        String result = reportStatsProvider.executeComponent(formdataObject.toString());
        JsonObject resultAsJson = JsonParser.parseString(result).getAsJsonObject();
        JsonArray allFilesAvailableToLoggedInUser = GsonUtility.optJsonArray(resultAsJson,"latestReports");

        String dataSourceId = GsonUtility.optString(formDataJson,"dataSourceId");
        String classifier = formDataJson.get("classifier").getAsString();

        String location = classifier.equals("efwd") ? GsonUtility.optString(formDataJson,"location") : null;

        return extractFilesBasedOnDataSourceId(dataSourceId, allFilesAvailableToLoggedInUser, classifier, location);
    }

    private String extractFilesBasedOnDataSourceId(String dataSourceId, JsonArray solutionDirectoryFiles,
                                                   String classifier, String location) {
        JsonObject requiredResult = new JsonObject();
        JsonArray metadataJsonArray = AdhocServiceUtils.getSpecificExtension(solutionDirectoryFiles, JsonUtils.getMetadataExtension());

        JsonArray metadataFiles = extractFilesBasedOnEfwdId(dataSourceId, location, metadataJsonArray, solutionDirectoryFiles);

        if (metadataFiles.size() != 0) {
            requiredResult.add("metadataFiles", metadataFiles);
        } else {
            requiredResult.addProperty("message", "No metadata files found for the " + "dataSourceId:" + dataSourceId);
        }
        return requiredResult.toString();
    }

    private JsonArray extractFilesBasedOnEfwdId(String requestedId, String location, JsonArray metadataFiles, JsonArray solutionDirectoryFiles) {
        JsonArray filteredMetadatas = new JsonArray();

        for (int index = 0; index < metadataFiles.size(); index++) {
            JsonObject jsonItem = metadataFiles.get(index).getAsJsonObject();
            Metadata metadata = extractMetadata(jsonItem.get("reportPath").getAsString());
            if (metadata != null) {
                String metadataConnectionId = metadata.getConnectionDetails().getConnectionId();
                String reportLocation = jsonItem.get("dir").getAsString();
                String fileName = jsonItem.get("file").getAsString();
                if (location != null) {
                    String metadataDirectory = metadata.getConnectionDetails().getDirectory();

                    if (requestedId.equals(metadataConnectionId) && location.equals(metadataDirectory)) {

                        prepareMetadataJson(reportLocation, fileName, solutionDirectoryFiles, filteredMetadatas, jsonItem, metadata);


                    }
                } else {
                    if (requestedId.equals(metadataConnectionId) && GlobalJdbcTypeUtils.isTypeGlobal(metadata.getConnectionType())) {
                        prepareMetadataJson(reportLocation, fileName, solutionDirectoryFiles, filteredMetadatas, jsonItem, metadata);
                    }
                }


            }
        }
        return filteredMetadatas;
    }

    private void prepareMetadataJson(String reportLocation, String fileName, JsonArray solutionDirectoryFiles, JsonArray dataSourcesArray, JsonObject childrenObject, Metadata metadata) {
        JsonObject datasourceDetails = new JsonObject();
        GsonUtility.accumulate(datasourceDetails,"metadataName", metadata.getFileName());
        GsonUtility.accumulate(datasourceDetails,"databaseType", metadata.getDatabaseType());
        GsonUtility.accumulate(datasourceDetails,"connectionId", metadata.getConnectionDetails().getConnectionId());
        GsonUtility.accumulate(datasourceDetails,"lastModified", childrenObject.get("lastModified").getAsString());
        GsonUtility.accumulate(datasourceDetails,"name", childrenObject.get("title").getAsString());
        GsonUtility.accumulate(datasourceDetails,"path", childrenObject.get("reportPath").getAsString());
        GsonUtility.accumulate(datasourceDetails,"folderName", childrenObject.get("logicalPath"));
        JsonArray extractFilesBasedOnMetadataFileName = ReportsRelatedToMetadata.extractFilesBasedOnMetadataFileName(reportLocation, fileName, solutionDirectoryFiles);
        datasourceDetails.add("reportDetails", extractFilesBasedOnMetadataFileName);
        dataSourcesArray.add(datasourceDetails);
    }

    private Metadata extractMetadata(String filePath) {
        File metadataFile = new File(solutionDirectory + File.separator + filePath);
        Metadata metadata = null;
        try {
        metadata = JaxbUtils.unMarshal(Metadata.class, metadataFile);
        }
        catch (Exception e) {
        	
		}
        return metadata;
    }


}
