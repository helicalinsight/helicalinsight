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

package com.helicalinsight.efw.serviceframework;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.ClassNotConfiguredException;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by author on 24-02-2015.
 *
 * @author Rajasekhar
 */
@Component
public class ComponentsXmlReader {

    private static final Logger logger = LoggerFactory.getLogger(ComponentsXmlReader.class);

    public String findComponentNode(String type, String serviceType, String service, String classifier) {
        String operationType = ApplicationProperties.getInstance().getType();
        String processingMode;
        if ("xml".equalsIgnoreCase(operationType)) {
            processingMode = "xml";
        } else if ("json".equalsIgnoreCase(operationType)) {
            processingMode = "json";
        } else {
            throw new ConfigurationException("Application operation mode is wrongly configured. " +
                    "The type in project.properties must be either xml or json. But it is " +
                    operationType);
        }
        JSONObject componentsXmlJson = JsonUtils.getJsonOfImportableXml("components");
        return getComponentNodeJson(componentsXmlJson, type, serviceType, service, classifier, processingMode);
    }

    private String getComponentNodeJson(JSONObject componentsXmlJson, String type, String serviceType,
                                        String service, String classifier, String processingMode) {
        String componentClassJson;
        try {
            if (componentsXmlJson.has(processingMode)) {
                if (componentsXmlJson.getJSONObject(processingMode).has(type)) {
                    componentClassJson = getRequiredJson(componentsXmlJson, type, serviceType, service, classifier,
                            processingMode);
                } else {
                    componentClassJson = getJsonFromImports(componentsXmlJson, type, serviceType, service,
                            classifier, processingMode);
                }

                if (componentClassJson == null) {
                    componentClassJson = getJsonFromImports(componentsXmlJson, type, serviceType, service,
                            classifier, processingMode);
                }
            } else {
                componentClassJson = getJsonFromImports(componentsXmlJson, type, serviceType, service, classifier,
                        processingMode);
            }
        } catch (Exception ex) {
            throw new ConfigurationException("The components xml file is incorrectly configured. Check for typos" + "" +
                    ".", ex);
        }
        return componentClassJson;
    }

    private String getRequiredJson(JSONObject componentsXmlJson, String type, String serviceType,
                                   String service, String classifier, String processingMode) {
        JSONObject typeJson = componentsXmlJson.getJSONObject(processingMode).getJSONObject(type);

        if (!typeJson.has(serviceType)) {
            return getJsonFromImports(componentsXmlJson, type, serviceType, service, classifier, processingMode);
        }

        JSONObject serviceTypeJson = typeJson.getJSONObject(serviceType);
        try {
            if (!serviceTypeJson.has(service)) {
                return getJsonFromImports(componentsXmlJson, type, serviceType, service, classifier, processingMode);
            }
            JSONObject requiredJson = serviceTypeJson.getJSONObject(service);
            if (classifier != null) {
                String theClassifier = classifier(requiredJson);
                if (!theClassifier.contains(",")) {
                    if (classifier.equalsIgnoreCase(theClassifier)) {
                        return requiredJson.toString();
                    } else {
                        return getJsonFromImports(componentsXmlJson, type, serviceType, service, classifier,
                                processingMode);
                    }
                } else {
                    if (Arrays.asList(theClassifier.replaceAll("\\s*", "").split(",")).contains(classifier)) {
                        return requiredJson.toString();
                    } else {
                        return getJsonFromImports(componentsXmlJson, type, serviceType, service, classifier,
                                processingMode);
                    }
                }
            } else {
                return requiredJson.toString();
            }
        } catch (Exception ex) {
            if (ex instanceof ConfigurationException || ex instanceof ClassNotConfiguredException) {
                throw new ConfigurationException(ex.getMessage(), ex);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("The requested component is an array. Using classifier.");
            }

            if (classifier == null) {
                throw new RequiredParameterIsNullException("The parameter classifier is required to locate the exact " +
                        "" + "component type. But it is null.");
            }

            JSONArray componentsArray = serviceTypeJson.getJSONArray(service);
            for (Object aComponent : componentsArray) {
                JSONObject component = (JSONObject.fromObject(aComponent));
                if (classifier.equalsIgnoreCase(component.getString("@classifier"))) {
                    return component.toString();
                }
            }
        }
        return null;
    }

    private String classifier(JSONObject requiredJson) {
        String theClassifier;
        try {
            theClassifier = requiredJson.getString("@classifier");
        } catch (Exception ex) {
            throw new EfwServiceException(ex);
        }
        return theClassifier;
    }

    private String getJsonFromImports(JSONObject componentsXmlJson, String type, String serviceType,
                                      String service, String classifier, String processingMode) {
        JSONArray importsArray;
        try {
            importsArray = componentsXmlJson.getJSONArray("import");
        } catch (Exception ex) {
            throw new ClassNotConfiguredException("The components.xml has no imports and the required configuration "
                    + "is not found in the existing xml. Unable to find component class.");
        }
        try {
            for (Object importedXml : importsArray) {
                JSONObject eachXml = (JSONObject) importedXml;
                String name = eachXml.getString("@name");
                if (name == null || "".equals(name) || "".equals(name.trim())) {
                    throw new ConfigurationException(String.format("The import of services " +
                            "configuration file %s is not configured properly. The name is null " +
                            "of empty.", name));
                }
                JSONObject importedComponents = JsonUtils.getImportedXmlJson(name);
                String requiredJson = getComponentNodeJson(importedComponents, type, serviceType, service,
                        classifier, processingMode);
                if (requiredJson != null) {
                    return requiredJson;
                }
            }
        } catch (ConfigurationException ex) {
            throw new ConfigurationException("A configuration file couldn't be imported as it doesn't exist");
        } catch (Exception ex) {
            throw new EfwServiceException(String.format("The required type %s is not available " + "even after " +
                    "searching in importable components configuration files. Check " +
                    "xml configuration. Could not produce component object. Did the request " +
                    "include proper classifier to pick the component?", type), ex);
        }
        return null;
    }
}