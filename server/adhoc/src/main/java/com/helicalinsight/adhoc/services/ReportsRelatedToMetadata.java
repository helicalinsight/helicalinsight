package com.helicalinsight.adhoc.services;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.report.AdhocReport;
import com.helicalinsight.adhoc.report.ReportOpenHelper;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;


/**
 * A component for extracting adhoc reports related to a specific metadata file.
 */
public class ReportsRelatedToMetadata implements IComponent {
    private static ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    private static String solutionDirectory = applicationProperties.getSolutionDirectory();

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Executes the component to extract ad hoc reports related to a specific metadata file.
     *
     * @param jsonFormData 			form data provide metadataFileName, location.
     * @return A JSON string containing the extracted adhoc reports.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formDataJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String metadataFileName = formDataJson.get("metadataFileName").getAsString();
        String location = formDataJson.get("location").getAsString();

        IComponent reportStatisticsProviderComponent = FactoryMethodWrapper
                .getTypedInstance("com.helicalinsight.admin.management.ReportStatisticsProvider", IComponent.class);
        JsonObject formdataObject = AdhocServiceUtils.prepareFormData();
        String result = reportStatisticsProviderComponent.executeComponent(formdataObject.toString());
        JsonObject resultAsJson = JsonParser.parseString(result).getAsJsonObject();
        JsonArray allFilesAvailableToLoggedInUser = GsonUtility.optJsonArray(resultAsJson,"latestReports");


        JsonObject requiredResult = new JsonObject();
        JsonArray requiredReportsArray = extractFilesBasedOnMetadataFileName(location,metadataFileName, allFilesAvailableToLoggedInUser);

        requiredResult.add("adhocReports", requiredReportsArray);

        return requiredResult.toString();

    }
    /**
     * Extracts adhoc reports based on the specified metadata file name and location.
     *
     * @param location            		 			 location of the metadata file.
     * @param metadataFileName    					 name of the metadata file.
     * @param allFilesAvailableToLoggedInUser 		 A JSON array containing information about all available files.
     * @return A JSON array containing the extracted adhoc reports.
     */
    public static JsonArray  extractFilesBasedOnMetadataFileName(String location,String metadataFileName,
                                                                 JsonArray allFilesAvailableToLoggedInUser) {

        JsonArray requiredReportJsonArray = new JsonArray();
        JsonArray reportJsonArray = AdhocServiceUtils.getSpecificExtension(allFilesAvailableToLoggedInUser,
                JsonUtils.getReportExtension());

        for (int index = 0; index < reportJsonArray.size(); index++) {
            JsonObject jsonItem = reportJsonArray.get(index).getAsJsonObject();
            String filePath = solutionDirectory + File.separator + jsonItem.get("reportPath").getAsString();
            File file = new File(filePath);
            String reportFileName = file.getName();
            String dir = file.getAbsolutePath().replaceAll(file.getName(), "");
            String solutionDir = applicationProperties.getSolutionDirectory();
            dir = dir.replace(solutionDir, "");
            dir = dir.replaceFirst("\\\\", "");
            dir =removeLastSlash(dir);
            String adhocReportName =jsonItem.get("file").getAsString();
            AdhocReport adhocReport = (AdhocReport)ReportOpenHelper.getAdhocReport(dir, reportFileName);

            String reportMetadataFileName = adhocReport.getMetadataReference().getMetadataFileName();
            String reportMetadataFileLocation = adhocReport.getMetadataReference().getLocation();
            reportMetadataFileLocation=	reportMetadataFileLocation.replaceAll("\\\\", "/");
            if (metadataFileName.equals(reportMetadataFileName)&&location.equals(reportMetadataFileLocation)) {
                prepareAdhocReport(adhocReportName ,allFilesAvailableToLoggedInUser,metadataFileName, requiredReportJsonArray, reportFileName, adhocReport,
                        reportMetadataFileName, dir);
            }
        }
        return requiredReportJsonArray;
    }
    /**
     * Prepares adhoc reports for inclusion in the result JSON array.
     *
     * @param adhocReportName       		 name of the adhoc report.
     * @param solutionDirectoryFiles 		 A JSON array containing information about all files in the solution directory.
     * @param metadataFileName      		 name of the metadata file.
     * @param requiredReportJsonArray 		 JSON array to store the extracted adhoc reports.
     * @param reportFileName        		 name of the report file.
     * @param adhocReport           		 An object representing the adhoc report.
     * @param reportMetadataFileName 		 name of the metadata file associated with the ad hoc report.
     * @param dir                   		 directory path of the report file.
     */
    private static void prepareAdhocReport(String adhocReportName,JsonArray solutionDirectoryFiles,String metadataFileName, JsonArray requiredReportJsonArray, String reportFileName,
                                           AdhocReport adhocReport, String reportMetadataFileName,String dir) {

        JsonObject reportJsonObject = new JsonObject();
        GsonUtility.accumulate(reportJsonObject,"reportFileName", reportFileName);
        GsonUtility.accumulate(reportJsonObject,"reportName", adhocReport.getReportName());
        GsonUtility.accumulate(reportJsonObject,"metadataFileName", adhocReport.getMetadataReference().getMetadataFileName());
        GsonUtility.accumulate(reportJsonObject,"location", dir);
        JsonArray extractFilesBasedOnReportFileName = SheduledReportsRelatedToReports.extractFilesBasedOnReportFileName(adhocReportName, solutionDirectoryFiles);
        reportJsonObject.add("savedReports", extractFilesBasedOnReportFileName);
        JsonArray extractDesignerFilesBasedOnReportFileName = DesignerReportsRelatedToReport.extractFilesBasedOnReportFileName(adhocReportName, solutionDirectoryFiles);
        reportJsonObject.add("designerReports", extractDesignerFilesBasedOnReportFileName);
        requiredReportJsonArray.add(reportJsonObject);
    }
    /**
     * Removes the last slash from a URL.
     *
     * @param url 		 	URL string.
     * @return URL string with the last slash removed.
     */
    private static String removeLastSlash(String url) {
        if(url.endsWith("\\")) {
            return url.substring(0, url.lastIndexOf("\\"));
        } else {
            return url;
        }
    }

}
