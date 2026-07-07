package com.helicalinsight.export.components;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.export.ExportUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;


import java.io.File;
import java.util.Map;

/**
 * The `AnySettingsGroovyProcessor` class implements the `IComponent` interface to handle the execution of Groovy scripts
 * based on specified content IDs. It is responsible for processing various settings using Groovy scripts and providing the
 * corresponding content.
 * Created by Author on 08/12/2016
 * @author Somen
 */
@SuppressWarnings("unused")
public class AnySettingsGroovyProcessor implements IComponent {

	/**
     * Indicates whether the component is thread-safe to cache. 
     * @return {@code true} if the component is thread-safe to cache, otherwise false.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }


    /**
     * Executes the Groovy processor based on the provided JSON form data.
     *
     * @param jsonFormData 		JSON form data containing the content IDs to be processed.
     * @return A JSON string containing the processed content based on the specified content IDs.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();
        //String contentId = formData.getString("contentId");
        JsonElement rawObj = formData.get("contentId");
        if (rawObj instanceof JsonArray) {
            return provideMultiContent(formData, (JsonArray) rawObj);
        } else if ("hcrConfigurations".equals(rawObj.getAsString())) {
            return provideMultiContent(formData, JsonUtils.getHCRConfigurations());
        } else
            return provideContent(formData, rawObj.getAsString());
    }
    /**
     * Provides content for multiple content IDs.
     *
     * @param formData  form data.
     * @param key       Array of content IDs to be processed.
     * @return A JSON string containing the processed content for each content ID.
     */
    private String provideMultiContent(JsonObject formData, JsonArray key) {
        JsonObject responses = new JsonObject();
        for (int index = 0; index < key.size(); index++) {
            String eachKey = key.get(index).getAsString();
            String content;
            if ("allTypes".equalsIgnoreCase(eachKey)) {
                content = JsonUtils.getAllTypesFromSetting(null);
            } else {
                try {
                    content = provideContent(formData, eachKey);
                } catch (ResourceNotFoundException e) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("__hcrStaticFileAvailability__", false);
                    obj.addProperty("message", "The resource " + eachKey + " not found");
                    content = obj.toString();
                }
            }
            JsonParser jsonParser = new JsonParser();
            JsonObject jo = (JsonObject)jsonParser.parse(content);
            if(eachKey.equals("Static/HCR")){
                responses.addProperty(eachKey.replace("Static/", ""), content);
            }else {
                responses.add(eachKey.replace("Static/", ""), jo);
            }
            //responses.addProperty(eachKey.replace("Static/", ""), content);
        }
        return responses.toString();
    }
    /**
     * this method evaluates Groovy scripts.
     *
     * @param formData  		JSON form data.
     * @param contentId 		Content ID to be processed.
     * @return A JSON string containing the processed content for the specified content ID.
     * @throws ResourceNotFoundException If the resource for the specified content ID is not found.
     */
    private String provideContent(JsonObject formData, String contentId) {
        String contentPath = ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin" +
                File.separator + contentId;

        String pathname = contentPath + ExportUtils.JSON_EXTENSION;
        File file = new File(pathname);
        if (file.exists()) {
            return ExportUtils.getFileAsString(pathname);
        }

        pathname = contentPath + ExportUtils.GROOVY_EXTENSION;
        file = new File(pathname);

        if (file.exists()) {
            Binding binding = new Binding();
            binding.setVariable("formData", formData);
            GroovyShell shell = new GroovyShell(binding);
            String groovyScript = ExportUtils.getFileAsString(pathname);
            return shell.evaluate(groovyScript).toString();
            
        } else {
            throw new ResourceNotFoundException("The resource " + contentId + " not found");
        }
    }

}