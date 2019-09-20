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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import net.sf.json.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by author on 24-02-2015.
 *
 * @author Rajasekhar
 */
public class ServiceUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServiceUtils.class);

    //Requires classifier
    public static String executeService(String type, String serviceType, String service, String formData) {
        JSONObject formDataJson = JSONObject.fromObject(formData);

        String classifier;
        try {
            classifier = formDataJson.getString("classifier");
        } catch (net.sf.json.JSONException ex) {
            throw new RequiredParameterIsNullException("Required parameter classifier not found.");
        }

        String componentJson = componentJson(type, serviceType, service, classifier);

        String componentClass = componentClass(componentJson);
        return executeService(formDataJson, componentJson, componentClass);
    }

    /**
     * Returns the json of the component from the xml
     *
     * @param type        The feature type
     * @param serviceType The service type
     * @param service     The actual service
     * @param classifier  Differentiates components configured. Can be null
     * @return The json of the component
     */

    public static String componentJson(String type, String serviceType, String service, String classifier) {
        ComponentsXmlReader componentsXmlReader = ApplicationContextAccessor.getBean(ComponentsXmlReader.class);
        String componentJson = componentsXmlReader.findComponentNode(type, serviceType, service, classifier);

        if (componentJson == null || "".equals(componentJson) || "".equals(componentJson.trim())) {
            throw new ConfigurationException("The component is not defined for the service " + service);
        }
        return componentJson;
    }

    public static String componentClass(String componentJson) {
        String componentClass;
        try {
            componentClass = JSONObject.fromObject(componentJson).getString("@class");
        } catch (Exception ex) {
            throw new EfwServiceException("The component doesn't exist. Check configuration.");
        }
        return componentClass;
    }

    //Does not require classifier
    public static String executeService(JSONObject formDataJson, String componentJson,
                                        String componentClass) {
        if (componentClass == null || "".equals(componentClass) || "".equals(componentClass.trim())) {
            throw new EfwServiceException("The component doesn't exist. Check configuration.");
        }

        JsonObject model;
        model = new JsonObject();
        try {
            IComponent iComponent = FactoryMethodWrapper.getTypedInstance(componentClass, IComponent.class);
            if (iComponent != null) {
                String result = getComponentResult(formDataJson, componentJson, iComponent);

                String emptySelection = "No Columns Selected";
                if (emptySelection.equals(result)) {
                    model.addProperty("status", 0);
                    JsonObject message = new JsonObject();
                    message.addProperty("message", emptySelection);
                    model.add("response", message);
                } else {
                    model.addProperty("status", 1);
                    JsonElement element = new Gson().fromJson(result, JsonElement.class);
                    JsonObject jsonObject = element.getAsJsonObject();
                    model.add("response", jsonObject);
                }
            } else {
                throw new ConfigurationException("Could not produce the component object. Check " + "the " +
                        "configuration files.");
            }
        } catch (Exception exception) {
            model.addProperty("status", 0);
            logger.error("An exception has taken place. The stackTrace is ", exception);
            JsonObject response = new JsonObject();
            response.addProperty("message", "Error: " + ExceptionUtils.getRootCauseMessage(exception));
            model.add("response", response);
        }
        return model.toString();
    }

    private static String getComponentResult(JSONObject formDataJson, String componentJson, IComponent iComponent) {
        formDataJson.accumulate("componentJson", componentJson);
        return iComponent.executeComponent(formDataJson.toString());
    }

    public static String execute(String type, String serviceType, String service, String formData) {
        JSONObject formDataJson = JSONObject.fromObject(formData);

        String componentJson = componentJson(type, serviceType, service);

        String componentClass = componentClass(componentJson);
        return executeService(formDataJson, componentJson, componentClass);
    }

    public static String componentJson(String type, String serviceType, String service) {
        ComponentsXmlReader componentsXmlReader = ApplicationContextAccessor.getBean(ComponentsXmlReader.class);
        String componentJson = componentsXmlReader.findComponentNode(type, serviceType, service, null);

        if (componentJson == null || "".equals(componentJson) || "".equals(componentJson.trim())) {
            throw new ConfigurationException("The component is not defined for the service " + service);
        }
        return componentJson;
    }
}