package com.helicalinsight.adhoc.metadata.genericdb;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.report.AdhocReport;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;


/**
 * Provides functionality to handle the deletion of metadata files and associated reports.
 * This component deletes the specified metadata files and their associated reports from the solution directory.
 */
public class MetadataDeleteHandler implements IComponent {
	/**
     * Indicates whether this component is thread-safe to cache or not.
     * @return true it is thread-safe to cache.
     */
	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}

	/**
     * Executes the component logic to delete metadata files and associated reports.
     *
     * @param jsonFormData 		 JSON data containing the form parameters
     * @return a JSON string containing the deletion status message
     */
	@Override
	public String executeComponent(String jsonFormData) {
		JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String location = formJson.get("location").getAsString();
        String metadataFileName = formJson.get("metadataFileName").getAsString();
        Map<String, String> parameters = new HashMap<>();
        //Handle Root directory
        if (!"".equals(location)) {
            parameters.put("location", location);
        }
        parameters.put("metadataFileName", metadataFileName);
        JsonObject responseJson = new JsonObject();

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
        List<String> metadataList = new ArrayList<>();
        List<String> metadataFilePath = new ArrayList<>();

        // This code needs to be looped in case of multiple metadata file list
        //for(int index =0 ;index<metadataFileName.size();index++){
        metadataList.add(metadataFileName);
        metadataFilePath.add(location + File.separator + metadataFileName);

        int deletedMetadataReportsFilesCount = deleteRelatedMetadataReport(metadataList);
        if (deletedMetadataReportsFilesCount >= 0) {
            List<String> deletedMetadataList = deleteRequestedMetadata(metadataFilePath);
            if (deletedMetadataList.size() > 0) {
                metadataList.removeAll(deletedMetadataList);
                responseJson.addProperty("message", "Metadata deleted successfully");
            } else {
                responseJson.addProperty("message", "Metadata cannot be deleted");
            }
        } else {
            responseJson.addProperty("message", "Metadata cannot be deleted");
        }

        return responseJson.toString();
	}

	 /**
     * Deletes metadata reports associated with the given metadata file names.
     *
     * @param metaDataFileList 		 list of metadata file names
     * @return the count of deleted metadata reports
     */
    public int deleteRelatedMetadataReport(@NotNull List<String> metaDataFileList) {
        String reportExtension = JsonUtils.getReportExtension();
        String solutionDirectoryPath = ApplicationProperties.getInstance().getSolutionDirectory() + File.separator;
        File solutionDirectory = new File(solutionDirectoryPath);
        List<String> filteredReportList = FileUtils.getFilteredFileList(solutionDirectory, reportExtension);
        int count = 0;
        for (String file : filteredReportList) {
            try {
                File reportFile = new File(file);
                AdhocReport report = JaxbUtils.unMarshal(AdhocReport.class, reportFile);
                String metadataReferenceName = null;
                if (report != null) {
                    metadataReferenceName = report.getMetadataReference().getMetadataFileName();
                }
                if (metaDataFileList.contains(metadataReferenceName)) {
                    if (reportFile.exists()) {
                        reportFile.delete();
                        count++;
                    }
                }
            } catch (ConfigurationException ignore) {

            }
        }
        return count;
    }
    /**
     * Deletes the requested metadata files.
     *
     * @param metadataList 	 list of metadata file paths
     * @return the list of deleted metadata file names
     */
    @NotNull
    public List<String> deleteRequestedMetadata(@NotNull List<String> metadataList) {
        String solutionDirectoryPath = ApplicationProperties.getInstance().getSolutionDirectory() + File.separator;
        File metadataFile;
        List<String> deletedList = new ArrayList<>();
        for (String file : metadataList) {
            metadataFile = new File(solutionDirectoryPath + file);
            if (metadataFile.exists()) {
                metadataFile.delete();
                deletedList.add(file);
            }
        }
        return deletedList;
    }

}
