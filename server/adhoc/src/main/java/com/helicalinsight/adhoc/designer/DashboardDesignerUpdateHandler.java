package com.helicalinsight.adhoc.designer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.jaxb.Efw;

/**
 * 
 * This class is responsible for handling the update of dashboard designer files.
 * It implements the {@link IComponent} interface.
 *
 * Created by author on 30-01-2015.
 * @author Rajasekhar
 */
public class DashboardDesignerUpdateHandler implements IComponent {

    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    /**
     * Indicates whether the method is thread-safe for caching purposes.
     * @return {@code true} since the method is thread-safe for caching purposes.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * 
     * This method processes the JSON form data for updating an existing dashboard designer resource.
     * 
     * @param jsonFormData               a JSON string containing form data includes parameters like directory, file name, HTML content,
     *                                   state, description, icon, and style
     * @return a JSON string representing the response includes a UUID and a message indicating the success or failure
     *         of the operation.
     * @throws IllegalArgumentException if any required parameter in the JSON form data is missing or empty,
     *                                  or if the specified designer resource does not exist.
     * @throws EfwServiceException      if there is an error during the execution of the component logic,
     *                                  such as failure to save files or update resources.
     */
    @Nullable
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        
        if (formJson.get("uuid").isJsonNull()) {
            return null;
        }
        String uuid = formJson.get("uuid").getAsString();

        String state = formJson.get("state").getAsString();
        String dir = formJson.get("dir").getAsString();
        String htmlString = formJson.get("htmlString").getAsString();

        Map<String, String> parameters = new HashMap<>();

        String description = null;
        if (formJson.has("description")) {
            description = formJson.get("description").getAsString();
            parameters.put("description", description);
        }

        String icon = null;
        if (formJson.has("icon")) {
            icon = formJson.get("icon").getAsString();
            parameters.put("icon", icon);
        }

        String style = null;
        if (formJson.has("style")) {
            style = formJson.get("style").getAsString();
            parameters.put("style", style);
        }

        parameters.put("state", state);
        parameters.put("htmlString", htmlString);
        parameters.put("dir", dir);

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        // Update fileName, state and description if available

        String ddExtension = JsonUtils.getDesignerExtension();

        String solutionDirectory = applicationProperties.getSolutionDirectory() + File.separator + dir;

        if (uuid.endsWith(ddExtension)) {
            uuid = uuid.substring(0, uuid.lastIndexOf('.'));
        }
        File designerFile = new File(solutionDirectory + File.separator + uuid + "." + ddExtension);

        if (!designerFile.exists()) {
            throw new IllegalArgumentException("Aborting operation. There is no designer " + "resource with the " +
                    "specified name.");
        }

        File efw = new File(solutionDirectory + File.separator + uuid + "." + JsonUtils.getEfwExtension());

        if (!efw.exists()) {
            throw new EfwServiceException("The referenced resource's efw file is missing.");
        }

        EfwDashboardDesigner dashboardDesigner = JaxbUtils.unMarshal(EfwDashboardDesigner.class, designerFile);

        if (description != null) {
            dashboardDesigner.setDescription(description);
        }

        // Edit and Save the designer
        dashboardDesigner.setState(state);
        synchronized (this) {
            JaxbUtils.marshal(dashboardDesigner, designerFile);
        }

        // Update efw file if required
        Efw existingEfw = JaxbUtils.unMarshal(Efw.class, efw);

        if (style != null) {
            existingEfw.setStyle(style);
        }

        if (icon != null) {
            existingEfw.setIcon(icon);
        }

        if (description != null) {
            existingEfw.setDescription(description);
        }

        synchronized (this) {
            JaxbUtils.marshal(existingEfw, efw);
        }

        DashboardDesignerIOHandler.saveHtml(dir, htmlString, uuid);
        JsonObject responseJson;
        responseJson = new JsonObject();
        GsonUtility.accumulate(responseJson,"uuid", uuid);
        GsonUtility.accumulate(responseJson,"message", "Design is edited successfully");
        return responseJson.toString();
    }
}
