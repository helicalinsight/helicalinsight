package com.helicalinsight.efwce;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rajesh
 *         Created by author on 4/22/2019.
 */
public class DashboardDeleteHandler implements IComponent {
    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    @Override
    public String executeComponent(String jsonFormData) {

        JSONObject formData = JSONObject.fromObject(jsonFormData);

        if (!formData.has("dir") || !formData.has("file")) {
            throw new IncompleteFormDataException("The request parameters dir and/or file are " + "missing");
        }

        String directory = formData.getString("dir");
        String fileName = formData.getString("file");

        //Validate parameters
        Map<String, String> parameters = new HashMap<>();

        parameters.put("dir", directory);
        parameters.put("file", fileName);

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        String ceExtension = JsonUtils.getEFWCEExtension();
        if (fileName.endsWith(ceExtension)) {
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        }

        int count = deleteRelatedFiles(directory, fileName, ceExtension);

        JSONObject response = new JSONObject();

        response.accumulate("message", count + " File(s) deleted successfully");

        return response.toString();
    }

    private int deleteRelatedFiles(String directory, String fileName, String ceExtension) {
        String filePath = this.applicationProperties.getSolutionDirectory() + File.separator + directory + File.separator + fileName + ".";
        List<File> listOfFiles = new ArrayList<>();
        listOfFiles.add(new File(filePath + ceExtension));
        listOfFiles.add(new File(filePath + JsonUtils.getEfwExtension()));
        listOfFiles.add(new File(filePath + JsonUtils.getEfwdExtension()));
        listOfFiles.add(new File(filePath + JsonUtils.getEfwvfExtension()));
        listOfFiles.add(new File(filePath + "html"));
        int count = 0;
        for (File file : listOfFiles) {
            if (FileUtils.safeDeleteFile(file))
                count++;
        }
        return count;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
