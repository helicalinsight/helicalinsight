package com.helicalinsight.datasource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.framework.AvailablePluginClassesRepository;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.DetectDriverClass;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 * CustomWatcherComponent
 * <p> This Component is used to Update the PluginRegistry. </p>
 *  @author Rajesh     
 */
public class CustomWatcherComponent implements IComponent {

    static final AvailablePluginClassesRepository REPOSITORY = AvailablePluginClassesRepository.getInstance();
    /**
     * executeComponent(String jsonFormData)
     * This method helps in loading, update, scan, and removing pluginRegistry.
     * @param jsonFormData          formData in string format
     * @return jsonObject in string format with plugin status message.
     * @throws EfwServiceException for invalid action 
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = new Gson().fromJson(jsonFormData, JsonObject.class);
        JsonObject result;
        result = new JsonObject();
        String action = null;
        if (formJson.has("action")) {
            action = GsonUtility.optString(formJson,"action");
            if ("".equalsIgnoreCase(action) || action == null) {
                throw new EfwServiceException("Please provide a valid action");
            }

        }

        if ("scan".equalsIgnoreCase(action)) {
            JsonArray classes = scanForJars();
            result.add("data", classes);
            result.addProperty("message", "Plugin Refresh Successfully");
            if (classes.isEmpty()) {
                result.addProperty("message", "There is no available plugins");
            }
        }
        if ("remove".equalsIgnoreCase(action)) {
            if (formJson.has("removeJar") && !("".equals(GsonUtility.optString(formJson,"removeJar")))) {

                String removeJar = GsonUtility.optString(formJson,"removeJar");

                String message = removeJarFromRegistry(removeJar);
                result.addProperty("message", message);

            }

        }
        if ("update".equalsIgnoreCase(action)) {
            updateJars();
            result.addProperty("message", "Update success");
        }
        if ("load".equalsIgnoreCase(action) && formJson.has("driverName")) {
            result.addProperty("message", DetectDriverClass.loadGivenClass(formJson.get("driverName").getAsString()));
        }

        return result.toString();
    }

    /**
     * scanForJars()
     * <p>
     * This method scans the pluginRegistry then shows the result in
     * JSONArry.
     * </p>
     * @return JSONArray
     */
    private JsonArray scanForJars() {
        return CustomWatcherUtils.scan();
    }

    /**
     * removeJarFromRegistry(String jarName)
     * <p>
     * This method removes a given jar from the registry.
     * </p>
     * @param jarName         jar name to remove
     * @return String message "De-Registered successfully"
     */
    private String removeJarFromRegistry(String jarName) {
        return CustomWatcherUtils.deRegister(jarName);
    }

    /**
     * updateJars()
     * <p>This method will update plugin jar</p>
     */
    private void updateJars() {
        try {
            CustomWatcherUtils.update();
        } catch (MalformedURLException e) {
            throw new EfwdServiceException("The given url is not valid");
        } catch (UnsupportedEncodingException e) {
            throw new EfwdServiceException("The given url is not valid");
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
