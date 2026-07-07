package com.helicalinsight.efw.resourceloader;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.ApplicationException;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.resourcereader.AbstractResourceReader;
import com.helicalinsight.efw.resourcereader.IResourceReader;
import com.helicalinsight.efw.resourcereader.ResourceReaderFactoryProducer;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.validator.FileValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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
            JsonObject jsonFromXml = processor.getJsonObject(fileValidator.getFile(), false);
            if (!jsonFromXml.has("Extentions")) {
                throw new ImproperXMLConfigurationException("Extentions key is not present in " + "setting.xml");
            } else {
                String path = this.properties.getSolutionDirectory();
                JsonObject jsonOfExtensions = jsonFromXml.getAsJsonObject("Extentions");

                JsonObject visibleExtensionTags = ControllerUtils.getJSONOfVisibleExtensionTags(jsonOfExtensions);

                JsonObject resourcereader = jsonFromXml.getAsJsonObject("resourcereader");
                String fileType = resourcereader.get("type").getAsString();
                String objClass = resourcereader.get("class").getAsString();
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
     * Loads the resources from the solution directory
     *
     * @return A string which represents a json of all the content of the
     * solution directory
     */
    public String loadResources(String specificResource, Boolean isRequestedRecursive) throws
            UnSupportedRuleImplementationException, ImproperXMLConfigurationException {
        String resources = null;
        FileValidator fileValidator = new FileValidator();
        if (JsonUtils.isFileBrowserCacheEnabled())
            if (specificResource != null) specificResource = specificResource.replaceAll("\\\\", "/");

        fileValidator.setFile(settingPath);
        // If the setting.xml is present read it
        if (fileValidator.isFilePresent()) {
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            JsonObject jsonFromXml = processor.getJsonObject(fileValidator.getFile(), false);
            if (!jsonFromXml.has("Extentions")) {
                throw new ImproperXMLConfigurationException("Extentions key is not present in " + "setting.xml");
            } else {

                JsonObject jsonOfExtensions = jsonFromXml.getAsJsonObject("Extentions");

                JsonObject visibleExtensionTags = ControllerUtils.getJSONOfVisibleExtensionTags(jsonOfExtensions);

                String fileType = jsonFromXml.getAsJsonObject("resourcereader").get("type").getAsString();
                String objClass = jsonFromXml.getAsJsonObject("resourcereader").get("class").getAsString();
                AbstractResourceReader abstractResourceReader = ResourceReaderFactoryProducer.getFactory(type,
                        fileType);
                String path;
                if (checkSlashForSolutionDir(specificResource)) {
                    path = this.properties.getSolutionDirectory();
                } else {
                    path = this.properties.getSolutionDirectory() + File.separator + specificResource;
                }
                //path = this.properties.getSolutionDirectory() + File.separator + specificResource;
                visibleExtensionTags.addProperty("isRequestedRecursive", isRequestedRecursive);

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

    private boolean checkSlashForSolutionDir(String specificResource) {
        return JsonUtils.isFileBrowserCacheEnabled() ? ("/".equals(specificResource) || "//".equals(specificResource) || "".equals(specificResource)) : false;
    }


}
