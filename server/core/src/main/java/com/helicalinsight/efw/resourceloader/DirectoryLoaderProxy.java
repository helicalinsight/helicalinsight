package com.helicalinsight.efw.resourceloader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.resourcereader.AbstractResourceReader;
import com.helicalinsight.efw.resourcereader.IResourceReader;
import com.helicalinsight.efw.resourcereader.ResourceReaderFactoryProducer;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.validator.FileValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DirectoryLoaderProxy {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryLoaderProxy.class);

    private final ApplicationProperties applicationProperties;

    private final String settingPath;

    private final String type;

    @SuppressWarnings("rawtypes")
    private final List listOfKeys;

    private final JsonObject settings;

    private JsonObject extensions;

    private List<String> keys;

    /**
     * The list of keys parameter can be null
     *
     * @param listOfKeys Keys representing the extentions of the Extensions tag in setting.xml
     */
    public DirectoryLoaderProxy(@SuppressWarnings("rawtypes") List listOfKeys) {
        this.listOfKeys = listOfKeys;
        this.applicationProperties = ApplicationProperties.getInstance();
        this.settingPath = this.applicationProperties.getSettingPath();
        this.type = this.applicationProperties.getType();
        this.settings = JsonUtils.newGetSettingsJson();
        this.extensions = this.settings.getAsJsonObject("Extentions");
        this.keys = JsonUtils.getKeys(this.extensions);
    }

    @NotNull
    public List<String> getAccessibleListOfDirectories(Boolean isRequestedRecursive) {
        this.extensions = removeOtherKeys();
        log();
        List<String> listOfDirectories = new ArrayList<>();
        JsonArray resourcesJsonArray = new Gson().fromJson(loadResources(isRequestedRecursive),JsonArray.class);
        ControllerUtils.replaceFilePath(resourcesJsonArray);
        return getDirectoriesList(resourcesJsonArray,
                listOfDirectories);
    }

    @Nullable
    public String getResources(Boolean isRequestedRecursive) {
        RulesInjector rulesInjector = new RulesInjector(this.listOfKeys, this.extensions);
        rulesInjector.injectRules();
        this.extensions = getKeysToBeProcessed();
        log();
        return loadResources(isRequestedRecursive);
    }

    @Nullable
    public String getResources(String resource, Boolean isRequestedRecursive) {
        RulesInjector rulesInjector = new RulesInjector(this.listOfKeys, this.extensions);
        rulesInjector.injectRules();
        this.extensions = getKeysToBeProcessed();
        log();
        return loadResources(isRequestedRecursive, resource);
    }

    private JsonObject getKeysToBeProcessed() {
        if (this.listOfKeys != null) {
            for (String key : this.keys) {
                if (!"folder".equals(key) && !this.listOfKeys.contains(key)) {
                    this.extensions.remove(key);
                }
            }
        }
        return this.extensions;
    }

    private void log() {
        if (logger.isDebugEnabled()) {
            logger.debug("Keys being processed are " + this.extensions);
        }
    }

    //Proxies the base loader functionality
    @Nullable
    private String loadResources(Boolean isRequestedRecursive) {
        String resources = null;
        FileValidator fileValidator = new FileValidator();
        fileValidator.setFile(this.settingPath);
        // If the setting.xml is present read it
        if (fileValidator.isFilePresent()) {
            if (!this.settings.has("Extentions")) {
                throw new ConfigurationException("The Extensions tag is not present in settings.");
            } else {
                JsonObject visibleExtensionTags = getVisibleExtensions();

                String fileType = this.settings.getAsJsonObject("resourcereader").get("type").getAsString();
                String resourceReaderClass = this.settings.getAsJsonObject("resourcereader").get("class").getAsString();

                String path = this.applicationProperties.getSolutionDirectory();
                AbstractResourceReader abstractResourceReader = ResourceReaderFactoryProducer.getFactory(this.type,
                        fileType);
                IResourceReader resourceType = null;
                if (abstractResourceReader != null) {
                    visibleExtensionTags.addProperty("isRequestedRecursive", isRequestedRecursive);
                    resourceType = abstractResourceReader.getIResource(this.type, fileType, resourceReaderClass,
                            path, visibleExtensionTags);
                }
                try {
                    if (resourceType != null) {
                        resources = resourceType.getResources();
                    }
                } catch (Exception ex) {
                    throw new EfwServiceException("Could not load resources", ex);
                }
            }
        } else {
            throw new ConfigurationException("The setting.xml is not present.");
        }
        return resources;
    }


    @Nullable
    private String loadResources(Boolean isRequestedRecursive, String path) {
        String resources = null;
        FileValidator fileValidator = new FileValidator();
        fileValidator.setFile(this.settingPath);
        // If the setting.xml is present read it
        if (fileValidator.isFilePresent()) {
            if (!this.settings.has("Extentions")) {
                throw new ConfigurationException("The Extensions tag is not present in settings.");
            } else {
                JsonObject visibleExtensionTags = getVisibleExtensions();

                String fileType = this.settings.getAsJsonObject("resourcereader").get("type").getAsString();
                String resourceReaderClass = this.settings.getAsJsonObject("resourcereader").get("class").getAsString();

                //String path = this.applicationProperties.getSolutionDirectory();
                AbstractResourceReader abstractResourceReader = ResourceReaderFactoryProducer.getFactory(this.type,
                        fileType);
                IResourceReader resourceType = null;
                if (abstractResourceReader != null) {
                    visibleExtensionTags.addProperty("isRequestedRecursive", isRequestedRecursive);
                    resourceType = abstractResourceReader.getIResource(this.type, fileType, resourceReaderClass,
                            path, visibleExtensionTags);
                }
                try {
                    if (resourceType != null) {
                        resourceType.setDiscardEmptyFolders(true);
                        resources = resourceType.getResources();
                    }
                } catch (Exception ex) {
                    throw new EfwServiceException("Could not load resources", ex);
                }
            }
        } else {
            throw new ConfigurationException("The setting.xml is not present.");
        }
        return resources;
    }

    private JsonObject getVisibleExtensions() {
        return
                ControllerUtils.getJSONOfVisibleExtensionTags(this.extensions);
    }

    @NotNull
    private List<String> getDirectoriesList(@NotNull JsonArray solutionDirectoryJsonArray,
                                            @NotNull List<String> listOfDirectories) {
        int size = solutionDirectoryJsonArray.size();
        for (int index = 0; index < size; index++) {
            JsonObject file = solutionDirectoryJsonArray.get(index).getAsJsonObject();
            if (file.has("type")) {
                if (file.get("type").equals("folder")) {
                    listOfDirectories.add(file.get("path").getAsString());
                    if (file.has("children")) {
                        getDirectoriesList(file.getAsJsonArray("children"), listOfDirectories);
                    }
                }
            }
        }
        return listOfDirectories;
    }

    private JsonObject removeOtherKeys() {
        if (this.listOfKeys != null) {
            for (String key : this.keys) {
                if (!"folder".equals(key)) {
                    this.extensions.remove(key);
                }
            }
        }
        return this.extensions;
    }
}
