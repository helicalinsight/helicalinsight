package com.helicalinsight.adhoc.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * A utility class to handle file deletion operations from the database.
 * This class implements the {@link IComponent} interface to execute the deletion operation as a component.
 * The component receives JSON form data containing file details and deletes the specified file(s) from the database.
 * The file details include the file name, report file name, and location.
 *
 * @author Somen
 */
public class FileDeleteHandlerDb implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Executes the component to delete files from the database.
     *
     * @param jsonFormData 		 JSON string containing form data.
     * @return A JSON string indicating the success or failure of the deletion operation.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson =  JsonParser.parseString(jsonFormData).getAsJsonObject();
        String file = GsonUtility.optString(formJson,"file");
        String fileName = GsonUtility.optStringValue(formJson,"reportFileName", file);
        String location = GsonUtility.optString(formJson,"location");

        List<String> reports = Arrays.asList(location + "/" + fileName);


        JsonObject jsonObject = new JsonObject();
        int deletedSuccessfully = deleteReportFromDatabase(reports);
        if (deletedSuccessfully > 0) {
            jsonObject.addProperty("message", "File deleted successfully.");
        } else {
            jsonObject.addProperty("message", "File cannot be deleted");
        }
        return jsonObject.toString();
    }

    /**
     * Deletes the specified reports from the database.
     *
     * @param reportList 		 list of reports to be deleted.
     * @return The number of reports deleted successfully.
     */
    private int deleteReportFromDatabase(@NotNull List<String> reportList) {
        int count = 0;
        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        for (String url : reportList) {
            HIResource resourceByUrl = serviceDB.getResourceByUrl(url);
            serviceDB.deleteHIResource(resourceByUrl.getResourceId(),null,null);
            count++;
        }
        return count;
    }
}