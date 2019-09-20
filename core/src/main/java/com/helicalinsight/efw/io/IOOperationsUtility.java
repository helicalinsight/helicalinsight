/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.io;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.resourceloader.BaseLoader;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Contains utility method related to IO operations. Consists of method that
 * read the configuration settings for different file operations from
 * setting.xml.
 * <p>
 * Created by author on 16-10-2014.
 *
 * @author Rajasekhar
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
        if (directory.delete()) {
            logger.info(directory + " is empty. No conditions for deletion. Deleted");
        } else {
            logger.info(directory + " couldn't be deleted");
        }
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
        JSONObject xmlContent = processor.getJSONObject(applicationProperties.getSettingPath(), false);
        JSONObject jsonObject = null;
        try {
            jsonObject = xmlContent.getJSONObject("operations");
        } catch (JSONException ex) {
            logger.error("JSONException ", ex);
        }

        logger.debug("operations : " + jsonObject);

        Iterator<?> iterator;
        if (jsonObject != null) {
            iterator = jsonObject.keys();
        } else {
            throw new RuntimeException("The operations node is absent");
        }

        Map<String, Map<String, String>> mapOfKeys = new HashMap<>();

        if (iterator != null) {
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                try {
                    JSONObject json = jsonObject.getJSONObject(key);
                    if (json != null) {
                        mapOfKeys.put(key, getMapForKey(key, json));
                    }
                } catch (JSONException ex) {
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
    private Map<String, String> getMapForKey(String key, JSONObject json) {
        Map<String, String> mapForKey = new HashMap<>();
        try {
            mapForKey.put("create", json.getJSONObject("create").getString("@class"));
        } catch (JSONException ex) {
            logger.info("create tag is not provided or class attribute is not provided for the " +
                    "key " + key + ". Using null");
            mapForKey.put("create", null);
        }
        try {
            mapForKey.put("delete", json.getJSONObject("delete").getString("@class"));
        } catch (JSONException ex) {
            logger.info("delete tag is not provided or class attribute is not provided for the " +
                    "key " + key + ". Using null");
            mapForKey.put("delete", null);
        }
        try {
            mapForKey.put("move", json.getJSONObject("move").getString("@class"));
        } catch (JSONException ex) {
            logger.info("move tag is not provided or class attribute is not provided for the key " +
                    "" + key + ". Using null");
            mapForKey.put("move", null);
        }
        try {
            mapForKey.put("rename", json.getJSONObject("rename").getString("@class"));
        } catch (JSONException ex) {
            logger.info("rename tag is not provided or class attribute is not provided for the " +
                    "key " + key + ". Using null");
            mapForKey.put("rename", null);
        }
        try {
            mapForKey.put("import", json.getJSONObject("import").getString("@class"));
        } catch (JSONException ex) {
            logger.info("import tag is not provided or class attribute is not provided for the " +
                    "key " + key + ". Using null");
            mapForKey.put("import", null);
        }
        try {
            mapForKey.put("export", json.getJSONObject("export").getString("@class"));
        } catch (JSONException ex) {
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
        JSONObject xmlContent = processor.getJSONObject(applicationProperties.getSettingPath(), false);
        BaseLoader baseLoader = new BaseLoader(applicationProperties);
        JSONObject jsonObject = null;
        try {
            jsonObject = baseLoader.getJSONOfVisibleExtensionTags(xmlContent.getJSONObject("Extentions"));
        } catch (JSONException ex) {
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
    private Map<String, String> populateMap(JSONObject jsonObject) {
        Map<String, String> map = new HashMap<>();
        Iterator<?> iterator;
        if (jsonObject != null) {
            iterator = jsonObject.keys();
        } else {
            throw new RuntimeException("The jsonObject is null.");
        }

        if (iterator != null) {
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                try {
                    map.put(jsonObject.getJSONObject(key).getString("#text"), key);
                } catch (JSONException ex) {
                    logger.info("No rule or text value for the key " + key + " is provided.");
                }
            }
        }
        return map;
    }
}
