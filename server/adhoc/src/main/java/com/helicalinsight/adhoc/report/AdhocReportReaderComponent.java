package com.helicalinsight.adhoc.report;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.serviceframework.IComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * AdhocReportReaderComponent is responsible for reading an adhoc report from the file system
 * and returning its content as JSON.
 *
 * It implements the IComponent interface.
 *
 * Created by author on 10-06-2015.
 * Modified by Rajasekhar
 */
@SuppressWarnings("unused")
public class AdhocReportReaderComponent implements IComponent {
	/**
     * Executes the component to read an adhoc report.
     *
     * @param jsonFormData 		 JSON string containing form data with directory and file name.
     * @return A string representing the content of the ad hoc report in JSON format.
     * @throws IllegalArgumentException If the specified file doesn't exist.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String directory = formJson.get("dir").getAsString();
        String fileName = formJson.get("file").getAsString();

        //Validate parameters
        Map<String, String> parameters = new HashMap<>();

        parameters.put("dir", directory);
        parameters.put("file", fileName);

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        final AdhocReport adhocReport = (AdhocReport) ReportOpenHelper.getAdhocReport(directory, fileName);
        if (adhocReport == null) {
            throw new IllegalArgumentException("The file " + fileName + " doesn't exists. " +
                    "Aborting operation.");
        }

        return ReportOpenHelper.reportContentAsJson(adhocReport).toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
