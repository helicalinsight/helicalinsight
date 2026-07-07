package com.helicalinsight.efw.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.FileBrowserCacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.resourceloader.BaseLoader;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;

/**
 * Contains utility method related to IO operations. Consists of method that
 * read the configuration settings for different file operations from
 * setting.xml.
 * <p>
 * Created by author on 16-10-2014.
 *
 * @author Rajasekhar
 * @version 1.0
 * @since 1.1
 */
public class IOOperationsUtility {
    private static final Logger logger = LoggerFactory.getLogger(IOOperationsUtility.class);

    /**
     * Simply deletes a file with appropriate logs.
     *
     * @param file a <code>File</code> which has to be deleted.
     */
    public static void deleteWithLogs(File file) {
        if (file.delete()) {
            logger.debug("Successfully deleted the file " + file);
        } else {
            logger.debug("Couldn't delete the file " + file);
        }
    }

    public static void safeDeleteWithLogs(File file) {
        String absolutePath = file.getAbsolutePath();
        File efwdFile = new File(absolutePath.replace("." + com.helicalinsight.efw.utility.JsonUtils.getHCRExtension(), "." + com.helicalinsight.efw.utility.JsonUtils.getEfwdExtension()));
        boolean flag = efwdFile.exists() && efwdFile.delete();
        if (file.exists() && file.delete()) {
            logger.debug("Successfully deleted the file " + file);
        } else {
            logger.debug("Couldn't delete the file " + file);
        }
    }

    /**
     * Simply deletes a directory with appropriate logs.
     *
     * @param directory Name of the directory which has to be deleted.
     */
    public static void deleteEmptyDirectoryWithLogs(File directory) {
        String relativeSolutionPath = ApplicationUtilities.getRelativeSolutionPath(directory.getAbsolutePath());
        if (relativeSolutionPath != null)
            FileBrowserCacheUtils.deleteFromDb(directory.getAbsolutePath());
        if (directory.delete()) {
            logger.info(directory + " is empty. No conditions for deletion. Deleted");
        } else {
            if (relativeSolutionPath != null)
                FileBrowserCacheUtils.madeChangesDb(directory.getAbsolutePath());
            logger.info(directory + " couldn't be deleted");
            throw new OperationFailedException(directory + " couldn't be deleted");
        }
    }

    /**
     * <p>This method is used to check that a file is binary or not.
     * </p>
     *
     * @param f
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static boolean isBinaryFile(File f) throws FileNotFoundException, IOException {
        FileInputStream in = new FileInputStream(f);
        int size = in.available();
        if (size > 1024) size = 1024;
        byte[] data = new byte[size];
        in.read(data);
        in.close();

        int ascii = 0;
        int other = 0;

        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            if (b < 0x09) return true;

            if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D) ascii++;
            else if (b >= 0x20 && b <= 0x7E) ascii++;
            else other++;
        }

        if (other == 0) return false;

        return 100 * other / (ascii + other) > 95;
    }

    /**
     * <p>
     * Converts setting.xml operations node into
     * <code>Map<String, Map<String, String>></code>
     * </p>
     *
     * @return A <code>Map<String, Map<String, String>></code> of configuration
     * settings
     */
    public Map<String, Map<String, String>> getMapOfOperationSettings() {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        JsonObject xmlContent = processor.getJsonObject(applicationProperties.getSettingPath(), false);
        JsonObject jsonObject = null;
        try {
            jsonObject = xmlContent.getAsJsonObject("operations");
        } catch (JsonIOException ex) {
            logger.error("JSONException ", ex);
        }

        logger.debug("operations : " + jsonObject);

        Iterator<?> iterator;
        if (jsonObject != null) {
            iterator = jsonObject.keySet().iterator();
        } else {
            throw new RuntimeException("The operations node is absent");
        }

        Map<String, Map<String, String>> mapOfKeys = new HashMap<>();

        if (iterator != null) {
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                try {
                    JsonObject json = jsonObject.getAsJsonObject(key);
                    if (json != null) {
                        mapOfKeys.put(key, getMapForKey(key, json));
                    }
                } catch (JsonIOException ex) {
                    logger.error(key + " is not a JSONObject");
                }
            }
        }
        return mapOfKeys;
    }

    /**
     * The configuration related settings for the file operations such as
     * import, export, rename, delete etc are read from setting.xml and a map of
     * such configuration will be prepared.
     *
     * @param key  Either efwsr or efwFavourite or efwResult etc.
     * @param json The json of setting.xml operations tag
     * @return <code>Map<String, String></code> which contains key - any file
     * operation and value - corresponding class name
     */
    private Map<String, String> getMapForKey(String key, JsonObject json) {
        Map<String, String> mapForKey = new HashMap<>();
        try {
            mapForKey.put("create", json.getAsJsonObject("create").get("class").getAsString());
        } catch (JsonIOException ex) {
            logger.info("create tag is not provided or class attribute is not provided for the " +
                    "key " + key + ". Using null");
            mapForKey.put("create", null);
        }
        try {
            mapForKey.put("delete", json.getAsJsonObject("delete").get("class").getAsString());
        } catch (JsonIOException ex) {
            logger.info("delete tag is not provided or class attribute is not provided for the " +
                    "key " + key + ". Using null");
            mapForKey.put("delete", null);
        }
        try {
            mapForKey.put("move", json.getAsJsonObject("move").get("class").getAsString());
        } catch (JsonIOException ex) {
            logger.info("move tag is not provided or class attribute is not provided for the key " +
                    "" + key + ". Using null");
            mapForKey.put("move", null);
        }
        try {
            mapForKey.put("rename", json.getAsJsonObject("rename").get("class").getAsString());
        } catch (JsonIOException ex) {
            logger.info("rename tag is not provided or class attribute is not provided for the " +
                    "key " + key + ". Using null");
            mapForKey.put("rename", null);
        }
        try {
            mapForKey.put("import", json.getAsJsonObject("import").get("class").getAsString());
        } catch (JsonIOException ex) {
            logger.info("import tag is not provided or class attribute is not provided for the " +
                    "key " + key + ". Using null");
            mapForKey.put("import", null);
        }
        try {
            mapForKey.put("export", json.getAsJsonObject("export").get("class").getAsString());
        } catch (JsonIOException ex) {
            logger.info("export tag is not provided or class attribute is not provided for the " +
                    "key " + key + ". Using null");
            mapForKey.put("export", null);
        }
        return mapForKey;
    }

    /**
     * Obtains the file types key and value as a <code>Map</code> from the
     * setting.xml (Only the nodes for which visible attribute value true are
     * considered).
     *
     * @return A <code>Map</code> of key value pairs
     */
    public Map<String, String> getVisibleExtensionsKeyValuePairs() {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        JsonObject xmlContent = processor.getJsonObject(applicationProperties.getSettingPath(), false);
        BaseLoader baseLoader = new BaseLoader(applicationProperties);
        JsonObject jsonObject = null;
        try {
            jsonObject = ControllerUtils.getJSONOfVisibleExtensionTags(xmlContent.getAsJsonObject("Extentions"));
        } catch (JsonIOException ex) {
            logger.error("JSONException ", ex);
        }
        return populateMap(jsonObject);
    }

    /**
     * Iterates over the given jsonObject and prepares a key value paris map
     *
     * @param jsonObject The json of the extensions tag from setting.xml
     * @return A <code>Map<String, String></code> which is created from
     * JSONObject.
     */
    private Map<String, String> populateMap(JsonObject jsonObject) {
        Map<String, String> map = new HashMap<>();
        Iterator<?> iterator;
        if (jsonObject != null) {
            iterator = jsonObject.keySet().iterator();
        } else {
            throw new RuntimeException("The jsonObject is null.");
        }

        if (iterator != null) {
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                try {
                	JsonObject asJsonObject = jsonObject.getAsJsonObject(key);
					if (asJsonObject.get("") != null) {
						map.put(jsonObject.getAsJsonObject(key).get("").getAsString(), key);
					}
                } catch (JsonIOException ex) {
                    logger.info("No rule or text value for the key " + key + " is provided.");
                }
            }
        }
        return map;
    }
}
