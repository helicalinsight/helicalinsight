package com.helicalinsight.adhoc.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Utility class for Adhoc Service operations.
 * This class provides methods for handling JSON data and performing various utility operations.
 * Created by author on 04-02-2015.
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
 * @author Prashansa
 */
@SuppressWarnings("unused")
public class AdhocServiceUtils {

    private static final Logger logger = LoggerFactory.getLogger(AdhocServiceUtils.class);
    /**
     * Retrieves a JSON array from a JSON object based on the specified key.
     *
     * @param json    		 JSON object required array .
     * @param keyName 		 key name.
     * @return The JSON array associated with the specified key.
     * @throws IllegalArgumentException    If the JSON object or key name is null.
     * @throws MalformedJsonException      If the JSON object does not contain a valid JSON array with the specified key.
     * @throws IncompleteFormDataException If the JSON array associated with the key is empty or null.
     */
    public static JsonArray getArray(@Nullable JsonObject json, @Nullable String keyName) {
        if (json == null || keyName == null) {
            throw new IllegalArgumentException("The key can not be null");
        }
        JsonArray jsonArray;
        try {
            jsonArray = json.getAsJsonArray(keyName);
        } catch (Exception e) {
            throw new MalformedJsonException("The json has no array with key " + keyName);
        }

        if (jsonArray == null || jsonArray.isEmpty()) {
            throw new IncompleteFormDataException("The array with key name " + keyName + " is " +
                    "empty or null");
        }
        return jsonArray;
    }
    /**
     * Retrieves a JSON object from a JSON object based on the specified key.
     *
     * @param json    		 JSON object  provides required.
     * @param keyName 		 key name.
     * @return The JSON object associated with the specified key.
     * @throws IllegalArgumentException    If the JSON object or key name is null.
     * @throws MalformedJsonException      If the JSON object does not contain a valid JSON object with the specified key.
     * @throws IncompleteFormDataException If the JSON object associated with the key is empty or null.
     */
    public static JsonObject getObject(@Nullable JsonObject json, @Nullable String keyName) {
        if (json == null || keyName == null) {
            throw new IllegalArgumentException("The key can not be null");
        }
        JsonObject jsonObject;
        try {
            jsonObject = json.getAsJsonObject(keyName);
        } catch (Exception e) {
            throw new MalformedJsonException("The json has no object with key " + keyName);
        }

        if (jsonObject == null || jsonObject.entrySet().isEmpty()) {
            throw new IncompleteFormDataException("The object with key name " + keyName + " is " +
                    "empty or null");
        }
        return jsonObject;
    }

    /**
     * Adds extra data for the normal process.
     *
     * @param formJson      		 JSON object containing form data.
     * @param dataSourceType 		 type of the data source.
     * @throws IllegalArgumentException If the dataSourceType is null.
     */
    public static void addExtraDataForNormalProcess(@NotNull JsonObject formJson, String dataSourceType) {
        if (dataSourceType == null) {
            throw new IllegalArgumentException("The argument dataSourceType is null");
        }

        JsonObject settingsJson = com.helicalinsight.efw.utility.JsonUtils.newGetSettingsJson();
        JsonArray metadataImplementations = settingsJson.getAsJsonObject("metadataImplementations").getAsJsonArray("metadata");
        DataSourceUtils.accumulateConfiguration(formJson, dataSourceType, metadataImplementations);
    }
    /**
     * Adds extra data for the normal process using a database map.
     *
     * @param formMap       		 map containing form data.
     * @param dataSourceType 		 type of the data source.
     * @throws IllegalArgumentException If the dataSourceType is null.
     */
    public static void addExtraDataForNormalProcessDB(@NotNull Map<String,Object> formMap, String dataSourceType) {
        if (dataSourceType == null) {
            throw new IllegalArgumentException("The argument dataSourceType is null");
        }

        JsonObject settingsJson = com.helicalinsight.efw.utility.JsonUtils.newGetSettingsJson();
        JsonArray metadataImplementations = settingsJson.getAsJsonObject("metadataImplementations").getAsJsonArray("metadata");
        DataSourceUtils.accumulateConfigurationDB(formMap, dataSourceType, metadataImplementations);
    }

    /**
     * Prepares an array of extensions for different file types.
     * @return A JSON array containing the extensions.
     */
    public static JsonArray prepareExtensions() {
        JsonArray extensionArray = new JsonArray();
        extensionArray.add(JsonUtils.getEfwdExtension());
        extensionArray.add(JsonUtils.getMetadataExtension());
        extensionArray.add(JsonUtils.getReportExtension());
        extensionArray.add(JsonUtils.getEFWSRExtension());
        extensionArray.add(JsonUtils.getDesignerExtension());
        extensionArray.add("efwresult");
        return extensionArray;
    }
    /**
     * Prepares form data for adhoc service operations.
     * @return A JSON object representing the prepared form data.
     */
    public static JsonObject prepareFormData() {
        JsonObject formdataObject = new JsonObject();
        JsonArray extensionArray = prepareExtensions();
        formdataObject.add("extension", extensionArray);
        GsonUtility.accumulateInt(formdataObject,"depth", Integer.MAX_VALUE);
        return formdataObject;
    }
    /**
     * Retrieves a specific extension from a JSON array of file paths.
     *
     * @param fatFilesArray 		 JSON array containing file paths.
     * @param extension     		 extension to retrieve.
     * @return A JSON array containing file paths with the specified extension.
     */
    public static JsonArray getSpecificExtension(JsonArray fatFilesArray, String extension) {
        JsonArray specificExtensionArray = new JsonArray();
        if(fatFilesArray==null) {
            return specificExtensionArray;
        }
        for (int index = 0; index < fatFilesArray.size(); index++) {
            JsonObject singleObject = fatFilesArray.get(index).getAsJsonObject();
            if (extension.equals(FileUtils.getExtension(singleObject.get("reportPath").getAsString()))) {
                specificExtensionArray.add(singleObject);
            }
        }
        return specificExtensionArray;
    }
    /**
     * Tests the availability of a URL.
     *
     * @param strUrl 		 URL to test.
     * @return {@code true} if the URL is accessible, otherwise {@code false}.
     * @throws IOException If an I/O exception occurs.
     */
    public static boolean testURL(String strUrl) throws IOException {

        URL url = new URL(strUrl);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.connect();

        return urlConn.getResponseCode() == HttpURLConnection.HTTP_OK;

    }
    /**
     * Prepares a URL for drill operations based on the provided JSON object.
     *
     * @param drillJson 		 JSON object containing drill details.
     * @return The prepared URL for drill operations.
     */
    public static String prepareUrlForDrill(JsonObject drillJson) {
        String host = drillJson.get("host").getAsString();
        String port = drillJson.get("port").getAsString();
        String resourceUrl = "http://" + host + ":" + port;
        return resourceUrl;
    }

}
