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

package com.helicalinsight.efw.resourceloader;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.ApplicationException;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.resourcereader.AbstractResourceReader;
import com.helicalinsight.efw.resourcereader.IResourceReader;
import com.helicalinsight.efw.resourcereader.ResourceReaderFactoryProducer;
import com.helicalinsight.efw.validator.FileValidator;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;

/**
 * An instance of this class is typically used once in the web app starting. The
 * object reads the project.properties and takes part in initializing the
 * singleton class ApplicationProperties
 *
 * @author Rajasekhar
 * @since 1.1
 */
public class BaseLoader {

    private static final Logger logger = LoggerFactory.getLogger(BaseLoader.class);

    /**
     * Property obtained from project.properties
     */
    private final String settingPath;
    /**
     * Here type means the type from the project.properties
     */
    private final String type;

    private final ApplicationProperties properties;

    /**
     * Constructs an instance of this class
     *
     * @param properties An instance of ApplicationProperties, which is a singleton
     */
    public BaseLoader(ApplicationProperties properties) {
        this.properties = properties;
        this.settingPath = properties.getSettingPath();
        this.type = properties.getType();
    }

    /**
     * Loads the resources from the solution directory
     *
     * @return A string which represents a json of all the content of the
     * solution directory
     */
    public String loadResources() throws UnSupportedRuleImplementationException, ImproperXMLConfigurationException {
        String resources = null;
        FileValidator fileValidator = new FileValidator();
        fileValidator.setFile(settingPath);
        // If the setting.xml is present read it
        if (fileValidator.isFilePresent()) {
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            JSONObject jsonFromXml = processor.getJSONObject(fileValidator.getFile(), false);
            if (!jsonFromXml.has("Extentions")) {
                throw new ImproperXMLConfigurationException("Extentions key is not present in " + "setting.xml");
            } else {
                String path = this.properties.getSolutionDirectory();
                JSONObject jsonOfExtensions = jsonFromXml.getJSONObject("Extentions");

                JSONObject visibleExtensionTags = getJSONOfVisibleExtensionTags(jsonOfExtensions);

                JSONObject resourcereader = jsonFromXml.getJSONObject("resourcereader");
                String fileType = resourcereader.getString("@type");
                String objClass = resourcereader.getString("@class");
                AbstractResourceReader abstractResourceReader = ResourceReaderFactoryProducer.getFactory(type,
                        fileType);
                IResourceReader resourceType = abstractResourceReader.getIResource(type, fileType, objClass, path,
                        visibleExtensionTags);
                if (logger.isDebugEnabled()) {
                    logger.info("Obtaining repository resources using the process type " + resourceType.getClass());
                }

                try {
                    resources = resourceType.getResources();
                } catch (ApplicationException e) {
                    logger.error("An ApplicationException occurred", e);
                }
            }
        } else {
            logger.error("The application configuration file setting.xml file in not present");
        }
        return resources;
    }

    /**
     * Returns the json of the tags for which visibility is true in the
     * setting.xml
     *
     * @param jsonOfExtensions The Extensions tag of the setting.xml
     * @return The json of the tags for which visibility is true
     */
    public JSONObject getJSONOfVisibleExtensionTags(JSONObject jsonOfExtensions) {
        Iterator<?> iterator = jsonOfExtensions.keys();
        JSONObject visibleExtensionsJSON = new JSONObject();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            try {
                // Check whether the visible attribute is provided
                // or not. If not, control moves to the catch block as there
                // will be an exception
                JSONObject jsonObject = jsonOfExtensions.getJSONObject(key);
                if (jsonObject != null) {
                    try {
                        if ("true".equalsIgnoreCase(jsonObject.getString("@visible"))) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("The key " + key + " is set to be visible in the repository.");
                            }
                            visibleExtensionsJSON.accumulate(key, jsonObject);
                        }
                    } catch (JSONException ex) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("The key " + key + " is set not to be visible in the " +
                                    "repository.");
                        }
                    }
                }
            } catch (JSONException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("The key " + key + " is not a json object.");
                }
            }
        }
        return visibleExtensionsJSON;
    }


    /**
     * Loads the resources from the solution directory
     *
     * @return A string which represents a json of all the content of the
     * solution directory
     */
    public String loadResources(String specificResource, Boolean isRequestedRecursive) throws
            UnSupportedRuleImplementationException, ImproperXMLConfigurationException {
        String resources = null;
        FileValidator fileValidator = new FileValidator();
        if(specificResource!=null) specificResource=specificResource.replaceAll("\\\\","/");
        fileValidator.setFile(settingPath);
        // If the setting.xml is present read it
        if (fileValidator.isFilePresent()) {
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            JSONObject jsonFromXml = processor.getJSONObject(fileValidator.getFile(), false);
            if (!jsonFromXml.has("Extentions")) {
                throw new ImproperXMLConfigurationException("Extentions key is not present in " + "setting.xml");
            } else {

                JSONObject jsonOfExtensions = jsonFromXml.getJSONObject("Extentions");

                JSONObject visibleExtensionTags = getJSONOfVisibleExtensionTags(jsonOfExtensions);

                String fileType = jsonFromXml.getJSONObject("resourcereader").getString("@type");
                String objClass = jsonFromXml.getJSONObject("resourcereader").getString("@class");
                AbstractResourceReader abstractResourceReader = ResourceReaderFactoryProducer.getFactory(type,
                        fileType);
                String path = this.properties.getSolutionDirectory() + File.separator + specificResource;

                visibleExtensionTags.accumulate("isRequestedRecursive", isRequestedRecursive);

                IResourceReader resourceType = abstractResourceReader.getIResource(type, fileType, objClass, path,
                        visibleExtensionTags);
                if (logger.isDebugEnabled()) {
                    logger.info("Obtaining repository resources using the process type " + resourceType.getClass());
                }

                try {
                    resources = resourceType.getResources();
                } catch (ApplicationException e) {
                    logger.error("An ApplicationException occurred", e);
                }
            }
        } else {
            logger.error("The application configuration file setting.xml file in not present");
        }
        return resources;
    }


}
