package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by author on 10/17/2019.
 *
 * @author Rajesh
 */
public class DeleteHCReportComponent implements IComponent {
    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();

        String directory = formData.get("dir").getAsString();
        String fileName = formData.get("file").getAsString();

        //Validate parameters
        Map<String, String> parameters = new HashMap<>();

        parameters.put("dir", directory);
        parameters.put("file", fileName);

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
        String hcrExtension = JsonUtils.getHCRExtension();
        if (fileName.endsWith(hcrExtension)) {
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        }
        JsonObject response = new JsonObject();

        if (deleteRelatedFile(directory, fileName, hcrExtension))
            GsonUtility.accumulate(response,"message", " File :" + fileName + " deleted successfully");
        else
        	GsonUtility.accumulate(response,"message", " Could not able to delete the file :" + fileName);

        return response.toString();
    }

    private boolean deleteRelatedFile(String directory, String fileName, String hcrExtension) {
        String filePath = this.applicationProperties.getSolutionDirectory() + File.separator + directory + File.separator + fileName + ".";
        File deleteFile = new File(filePath + hcrExtension);
        return FileUtils.safeDeleteFile(deleteFile);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
