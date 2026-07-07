package com.helicalinsight.adhoc.report;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.serviceframework.IComponent;


/**
 * The AdhocReportReaderComponentDb class reads an adhoc report from the database.
 * @author Somen
 */
@SuppressWarnings("unused")
public class AdhocReportReaderComponentDb implements IComponent {
	/**
     * Executes the component to read an ad hoc report from the database.
     *
     * @param jsonFormData 				 JSON string containing form data provides directory and file name.
     * @return A string representing the content of the ad hoc report in JSON format.
     * @throws IllegalArgumentException If the specified report file does not exist.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String directory = formJson.get("dir").getAsString();
        String fileName = formJson.get("file").getAsString();
        boolean isEdit = GsonUtility.optBooleanValue(formJson,"isEdit",false);
        final AdhocReport adhocReport = (AdhocReport) ReportOpenHelper.getAdhocReportDb(directory, fileName,isEdit);
        if (adhocReport == null) {
            throw new IllegalArgumentException("The file " + fileName + " doesn't exists. " +
                    "Aborting operation.");
        }
        if(isEdit && adhocReport.getMetadataReference().getCube() == null) {
        	adhocReport.setMetadataReference(null);
        }
        return ReportOpenHelper.reportContentAsJson(adhocReport).toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
