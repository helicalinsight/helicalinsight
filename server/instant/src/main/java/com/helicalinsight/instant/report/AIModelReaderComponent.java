package com.helicalinsight.instant.report;

import com.helicalinsight.adhoc.report.AdhocReport;
import com.helicalinsight.adhoc.report.ReportOpenHelper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.serviceframework.IComponent;


/**
 * InstantReportReaderComponent is responsible for reading an instant report from the file system
 * and returning its content as JSON.
 * 
 * It implements the IComponent interface.
 * 
 * @author Somen
 */
@SuppressWarnings("unused")
public class AIModelReaderComponent implements IComponent {
	/**
     * Executes the component to read an model.
     *
     * @param jsonFormData 		 JSON string containing form data with directory and file name.
     * @return A string representing the content of the model report in JSON format.
     * @throws IllegalArgumentException If the specified file doesn't exist.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String directory = formJson.get("dir").getAsString();
        String fileName = formJson.get("file").getAsString();


        final AdhocReport adhocReport = (AdhocReport) ReportOpenHelper.getModelStateDb(directory, fileName);
        if (adhocReport == null) {
            throw new IllegalArgumentException("The file " + fileName + " doesn't exists. " +
                    "Aborting operation.");
        }

        return ReportOpenHelper.getModelContentAsJson(adhocReport).toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
