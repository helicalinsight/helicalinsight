package com.helicalinsight.adhoc.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.serviceframework.IComponent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to handle file deletion operations.
 * This class implements the {@link IComponent} interface to execute the deletion operation as a component.
 * The component receives JSON form data containing file details and deletes the specified file(s) from the directory.
 * The file details include the report file name, location, and file name.
 *
 * @author Somen
 * @since 01/04/2015
 */
public class FileDeleteHandler implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Executes the component to delete files from the directory.
     *
     * @param jsonFormData 			 JSON string containing form data.
     * @return A JSON string indicating the success or failure of the deletion operation.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        // JSONArray listOfReport = formJson.getJSONArray("reportArray");
        String reportName = GsonUtility.optString(formJson,"reportFileName");
        String location = GsonUtility.optString(formJson,"location");
        String file = GsonUtility.optString(formJson,"file");
        // parameters.put("reportArray", listOfReport.toString());

        String fileName = "";
        if (file.length() > 0) {
            fileName = file;
        } else if (reportName.length() > 0) {
            fileName = reportName;
        }
        List<String> reports = new ArrayList<>();
        reports.add(location + File.separator + fileName);

        JsonObject jsonObject = new JsonObject();
        int deletedSuccessfully = deleteReportFromDirectory(reports);
        if (deletedSuccessfully > 0) {
            jsonObject.addProperty("message", "File deleted successfully.");
            //Requested " + listOfReport .size() + " deleted " + deletedSuccessfully
        } else {
            jsonObject.addProperty("message", "File cannot be deleted");
        }
        return jsonObject.toString();
    }

    /**
     * Deletes the specified reports from the directory.
     *
     * @param reportList 		 list of reports to be deleted.
     * @return The number of reports deleted successfully.
     */
    private int deleteReportFromDirectory(@NotNull List<String> reportList) {
        String directoryLocation = ApplicationProperties.getInstance().getSolutionDirectory() + File.separator;
        File fileObj;
        int count = 0;
        for (Object aReportList : reportList) {
            String filePath = directoryLocation + File.separator + aReportList;
            fileObj = new File(filePath);
            if (fileObj.exists()) {
                //noinspection ResultOfMethodCallIgnored
                fileObj.delete();
                count++;
            }
        }
        return count;
    }
}