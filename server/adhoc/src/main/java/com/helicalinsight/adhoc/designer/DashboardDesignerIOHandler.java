package com.helicalinsight.adhoc.designer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Efw;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class implements the {@link IComponent} interface and is responsible for handling input-output operations related to the dashboard designer.
 * It provides methods for saving HTML and designer files, as well as creating and saving EFW objects.
 * 
 * Created by author on 30-01-2015.
 * @author Rajasekhar
 * @author Somen
 */
public class DashboardDesignerIOHandler implements IComponent {

    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(DashboardDesignerIOHandler.class);
    /**
     * Saves the HTML content to a file.
     *
     * @param dir        				 directory where the file will be saved
     * @param htmlString 				 HTML content to be saved
     * @param uuid       				 UUID to be used for the file name
     * @return The File object representing the saved HTML file
     * @throws EfwServiceException if there is an error in saving the HTML file
     */
    @NotNull
    static File saveHtml(String dir, String htmlString, String uuid) {
        File htmlFile = new File(applicationProperties.getSolutionDirectory() + File.separator +
                dir + File.separator + uuid + ".html");

        if (ApplicationUtilities.createAFile(htmlFile, htmlString)) {
            logger.debug(htmlFile + " is saved successfully.");
        } else {
            throw new EfwServiceException("Error in saving file. The html couldn't be saved.");
        }

        return htmlFile;
    }
    /**
     * Indicates whether the method is thread-safe for caching purposes.
     * @return {@code true}
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Executes the component to handle input-output operations related to the dashboard designer.
     *
     * @param jsonFormData        FormData providing parameters like directory, file name, HTML content, etc.
     * @return A JSON string containing UUID and message indicating the success of the operation
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        if (formJson.has("uuid")) {
            JsonElement uuid = formJson.get("uuid");
            if (uuid.isJsonNull() || "".equals(uuid.getAsString()) || "".equals(uuid.getAsString().trim())) {
                throw new IllegalArgumentException("The parameter uuid is null or empty");
            }

            IComponent editComponent = new DashboardDesignerUpdateHandler();
            return editComponent.executeComponent(jsonFormData);
        }

        String state = formJson.get("state").getAsString();

        String name = formJson.get("fileName").getAsString();
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
        parameters.put("name", name);
        parameters.put("dir", dir);

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        String uuid = UUID.randomUUID().toString();

        saveHtml(dir, htmlString, uuid);

        String efwFileWithUuid = uuid + "." + JsonUtils.getEfwExtension();
        EfwDashboardDesigner designer = designerObject(state, name, description, efwFileWithUuid);
        saveDesignerFile(JsonUtils.getDesignerExtension(), designer, uuid, dir);

        efwObject(icon, style, name, description, dir, efwFileWithUuid, uuid);

        JsonObject responseJson;
        responseJson = new JsonObject();
        GsonUtility.accumulate(responseJson,"uuid", uuid + "." + JsonUtils.getDesignerExtension());
        GsonUtility.accumulate(responseJson,"message", "Design is saved successfully");
        return responseJson.toString();
    }
    /**
     * Creates and returns a new instance of EfwDashboardDesigner based on the provided parameters.
     *
     * @param state         		 state of the designer
     * @param name          		 name of the designer
     * @param description   		 description of the designer (can be null)
     * @param efwLocation   		 location of the EFW file associated with the designer
     * @return A new instance of EfwDashboardDesigner
     */
    private EfwDashboardDesigner designerObject(String state, String name, @Nullable String description,
                                                String efwLocation) {
        EfwDashboardDesigner designer = ApplicationContextAccessor.getBean(EfwDashboardDesigner.class);

        if (description != null) {
            designer.setDescription(description);
        } else {
            designer.setDescription("Efw Dashboard Designer");
        }

        designer.setName(name);
        designer.setEfw(efwLocation);
        designer.setState(state);
        designer.setVisible("true");
        designer.setSecurity(SecurityUtils.securityObject());
        return designer;
    }
    /**
     * Saves the given EfwDashboardDesigner object to a file with the provided UUID and directory.
     *
     * @param ddExtension   	 file extension for the designer file
     * @param designer      	 EfwDashboardDesigner object to be saved
     * @param uuid          	 UUID for the file name
     * @param dir           	 directory where the file should be saved
     */
    private void saveDesignerFile(String ddExtension, EfwDashboardDesigner designer, String uuid, String dir) {
        File designerFile = new File(applicationProperties.getSolutionDirectory() + File.separator + dir + File
                .separator + uuid + "." + ddExtension);

        try {
            synchronized (this) {
                JaxbUtils.marshal(designer, designerFile);
            }
        } catch (Exception e) {
            throw new EfwServiceException("Error in saving file. The designer couldn't be saved. " +
                    "" + "" + "Reason is ", e);
        }
    }
    /**
     * Creates and saves an Efw object based on the provided parameters.
     *
     * @param icon              		 icon associated with the EFW file (can be null)
     * @param style             		 style associated with the EFW file (can be null)
     * @param name              		 name of the EFW file
     * @param description       		 description of the EFW file (can be null)
     * @param dir               		 directory where the EFW file should be saved
     * @param efwFileWithUuid   		 name of the EFW file including UUID
     * @param uuid              		 UUID for the file name
     */
    private void efwObject(@Nullable String icon, @Nullable String style, String name, @Nullable String description,
                           String dir, String efwFileWithUuid, String uuid) {
        File efwFile = new File(applicationProperties.getSolutionDirectory() + File.separator +
                dir + File.separator + efwFileWithUuid);

        Efw efw = ApplicationContextAccessor.getBean(Efw.class);

        efw.setTitle(name);
        efw.setAuthor(AuthenticationUtils.getUserName());
        if (description != null) {
            efw.setDescription(description);
        } else {
            efw.setDescription("Efw File");
        }

        if (icon != null) {
            efw.setIcon(icon);
        }

        efw.setTemplate(uuid + ".html");

        efw.setVisible("true");

        if (style != null) {
            efw.setStyle(style);
        }
        efw.setSecurity(SecurityUtils.securityObject());
        try {
            synchronized (this) {
                JaxbUtils.marshal(efw, efwFile);
            }
        } catch (Exception e) {
            throw new EfwServiceException("Error in saving file. The efw file couldn't be saved. " +
                    "" + "" + "Reason is ", e);
        }
    }
}
