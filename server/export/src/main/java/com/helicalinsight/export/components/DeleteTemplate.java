package com.helicalinsight.export.components;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.export.ExportUtils;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The `DeleteTemplate` class implements the `IComponent` interface to delete a template based on the provided
 * template ID. It reads the template ID from the JSON form data, constructs the path to the template file, and
 * deletes the template file.
 * Created by Author on 08/12/2016
 * @author Somen
 */
@SuppressWarnings("unused")
public class DeleteTemplate implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Method performs the component to delete a template based on the provided JSON form data.
     *
     * @param jsonFormData 		JSON form data containing the template ID to be deleted.
     * @return A JSON string containing the result of the template deletion operation.
     * @throws OperationFailedException If the template deletion operation fails.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String templateId = formData.get("templateId").getAsString();
        String fileToDelete = ExportUtils.getTemplatesDirectory() + File.separator + templateId + ExportUtils
                .JSON_EXTENSION;
        JsonObject response = new JsonObject();
        try {
            Files.delete(Paths.get(fileToDelete));
        } catch (IOException ioe) {
            throw new OperationFailedException("The operation was not successful");
        }
        response.addProperty("message", "Template deleted successfully");
        return response.toString();
    }


}