package com.helicalinsight.adhoc.designer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.serviceframework.IComponent;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * DashboardDesignerDeleteHandler class extends {@link IComponent}.
 * It handles the deleting the designer file.
 * Created by author on 16-07-2015.
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class DashboardDesignerDeleteHandler implements IComponent {

    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    /**
     * Deletes the designer file based on the provided JSON-formatted data.
     *
     * @param jsonFromData       FormData providing parameters like directory and file.
     * @return A response message indicating successful deletion.
     * @throws IncompleteFormDataException if the directory or file parameters are missing
     * @throws EfwServiceException if an error occurs while deleting the resource
     * @throws IllegalArgumentException if specified file is not present
     */
    @Nullable
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();

        if (!formData.has("dir") || !formData.has("file")) {
            throw new IncompleteFormDataException("The request parameters dir and/or file are " + "missing");
        }

        String directory = formData.get("dir").getAsString();
        String fileName = formData.get("file").getAsString();

        //Validate parameters
        Map<String, String> parameters = new HashMap<>();

        parameters.put("dir", directory);
        parameters.put("file", fileName);

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        File file = new File(this.applicationProperties.getSolutionDirectory() + File.separator +
                directory + File.separator + fileName);

        if (!file.exists()) {
            throw new IllegalArgumentException("The file " + file + " doesn't exists. Aborting " +
                    "operation");
        }

        boolean deleted = file.delete();

        JsonObject response;
        response = new JsonObject();

        if (deleted) {
            GsonUtility.accumulate(response,"message", "The requested file is deleted successfully");
        } else {
            throw new EfwServiceException("The requested file couldn't be deleted. IO Error.");
        }
        return response.toString();
    }
    /**
     * Indicates whether the method is thread-safe for caching purposes.
     * @return {@code true}
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
