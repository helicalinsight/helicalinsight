package com.helicalinsight.hwf.component;

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
 * Created by author on 9/6/2019.
 *
 * @author Rajesh
 */
public class HWFDeleteComponent implements IComponent {
    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);

        if (!formData.has("dir") || !formData.has("file")) {
            throw new IncompleteFormDataException("The request parameters dir and file are " + "missing");
        }

        String directory = formData.getString("dir");
        String fileName = formData.getString("file");

        //Validate parameters
        Map<String, String> parameters = new HashMap<>();

        parameters.put("dir", directory);
        parameters.put("file", fileName);

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
        String hwfExtension = JsonUtils.getHWFExtension();
        String hwfdExtension = JsonUtils.getHWFDExtension();
        if (fileName.endsWith(hwfExtension)||fileName.endsWith(hwfdExtension)) {
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        }

        int count = deleteRelatedFiles(directory, fileName, hwfExtension,hwfdExtension);

        JSONObject response = new JSONObject();

        response.accumulate("message", count + " File(s) deleted successfully");

        return response.toString();
    }
    private int deleteRelatedFiles(String directory, String fileName, String hwfExtension,String hwfdExtension) {
        String filePath = this.applicationProperties.getSolutionDirectory() + File.separator + directory + File.separator + fileName + ".";
        List<File> listOfFiles = new ArrayList<>();
        listOfFiles.add(new File(filePath + hwfExtension));
        listOfFiles.add(new File(filePath + hwfdExtension));
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
    private void checkVisibility(){

    }
}
