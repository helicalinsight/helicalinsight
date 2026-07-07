package com.helicalinsight.admin.management;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by author on 1/30/2020.
 *
 * @author Rajesh
 */
public class ImageListComponent implements IComponent {
    private ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    @Override
    public String executeComponent(String formData) {
        JSONObject jsonFormData = JSONObject.fromObject(formData);
        String action = jsonFormData.getString("action");
        String dir = jsonFormData.getString("dir");

        String targetDir = applicationProperties.getSolutionDirectory() + File.separator + dir;
        JSONObject response = new JSONObject();
        File file = new File(targetDir);
        switch (action) {
            case "list":
                if (!file.exists()) {
                    throw new EfwServiceException("The target directory: " + dir + " does not exists");
                }
                List<String> fileList = Arrays.asList(file.list());
                List<String> collectedFilterList = fileList.stream().filter(r -> JsonUtils.supportedImageExtensions.contains(FileUtils.getExtension(r))).collect(Collectors.toList());
                response.put("imageList", collectedFilterList);
                break;

            case "display":
                if (!file.exists()) {
                    throw new EfwServiceException("The target directory: " + dir + " does not exists");
                }
                response.put("files", Arrays.asList(file.list()));
                break;

            case "delete":
                String fileToDelete = jsonFormData.getString("file");
                boolean isDeleted = FileUtils.safeDeleteFile(new File(targetDir + File.separator + fileToDelete));
                response.put("message", "File deletion " + (isDeleted ? "success" : "failed"));
                break;
        }
        return response.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
