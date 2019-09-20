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
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.resourcereader.AbstractResourceReader;
import com.helicalinsight.efw.resourcereader.IResourceReader;
import com.helicalinsight.efw.resourcereader.ResourceReaderFactoryProducer;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.validator.FileValidator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
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

    private final JSONObject settings;

    private JSONObject extensions;

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
        this.settings = JsonUtils.getSettingsJson();
        this.extensions = this.settings.getJSONObject("Extentions");
        this.keys = JsonUtils.getKeys(this.extensions);
    }


    public List<String> getAccessibleListOfDirectories(Boolean isRequestedRecursive) {
        this.extensions = removeOtherKeys();
        log();
        List<String> listOfDirectories = new ArrayList<>();
        return getDirectoriesList((JSONArray) JSONSerializer.toJSON(loadResources(isRequestedRecursive)),
                listOfDirectories);
    }

    public String getResources(Boolean isRequestedRecursive) {
        RulesInjector rulesInjector = new RulesInjector(this.listOfKeys, this.extensions);
        rulesInjector.injectRules();
        this.extensions = getKeysToBeProcessed();
        log();
        return loadResources(isRequestedRecursive);
    }

    private JSONObject getKeysToBeProcessed() {
        if (this.listOfKeys != null) {
            for (String key : this.keys) {
                if (!"folder".equals(key) && !this.listOfKeys.contains(key)) {
                    this.extensions.discard(key);
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
    private String loadResources(Boolean isRequestedRecursive) {
        String resources = null;
        FileValidator fileValidator = new FileValidator();
        fileValidator.setFile(this.settingPath);
        // If the setting.xml is present read it
        if (fileValidator.isFilePresent()) {
            if (!this.settings.has("Extentions")) {
                throw new ConfigurationException("The Extensions tag is not present in settings.");
            } else {
                JSONObject visibleExtensionTags = getVisibleExtensions();

                JSONObject resourcereader = this.settings.getJSONObject("resourcereader");
                String fileType = resourcereader.getString("@type");
                String resourceReaderClass = resourcereader.getString("@class");

                String path = this.applicationProperties.getSolutionDirectory();
                AbstractResourceReader abstractResourceReader = ResourceReaderFactoryProducer.getFactory(this.type,
                        fileType);
                IResourceReader resourceType = null;
                if (abstractResourceReader != null) {
                    visibleExtensionTags.accumulate("isRequestedRecursive", isRequestedRecursive);
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

    private JSONObject getVisibleExtensions() {
        return new BaseLoader(this.applicationProperties).getJSONOfVisibleExtensionTags(this.extensions);
    }


    private List<String> getDirectoriesList(JSONArray solutionDirectoryJsonArray,
                                            List<String> listOfDirectories) {
        int size = solutionDirectoryJsonArray.size();
        for (int index = 0; index < size; index++) {
            JSONObject file = solutionDirectoryJsonArray.getJSONObject(index);
            if (file.containsKey("type")) {
                if (file.get("type").equals("folder")) {
                    listOfDirectories.add(file.getString("path"));
                    if (file.has("children")) {
                        getDirectoriesList(file.getJSONArray("children"), listOfDirectories);
                    }
                }
            }
        }
        return listOfDirectories;
    }

    private JSONObject removeOtherKeys() {
        if (this.listOfKeys != null) {
            for (String key : this.keys) {
                if (!"folder".equals(key)) {
                    this.extensions.discard(key);
                }
            }
        }
        return this.extensions;
    }
}
