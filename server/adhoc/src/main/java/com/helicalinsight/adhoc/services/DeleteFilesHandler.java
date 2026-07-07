package com.helicalinsight.adhoc.services;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.FlatFilesDeleteUtils;
/**
 * A handler class responsible for deleting files or folders.
 */
public class DeleteFilesHandler implements IComponent {
	/**
     * Deletes the files or folders specified in the JSON form data.
     *
     * @param jsonFormData 	 JSON string containing the list of files or folders to be deleted.
     * @return A JSON string containing a message indicating the status of the deletion operation.
     */
	@Override
	public String executeComponent(String jsonFormData) {
		JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();
		JsonArray listOfFilesPath = formData.getAsJsonArray("listOfFilesToBeDeleted");
		String message ="";
		try {
			message = FlatFilesDeleteUtils.deleteFolderOrFile(listOfFilesPath);
		} catch (IOException e) {
			message = "Cannot able to perform the operation.";
		}

		JsonObject jsonResult = new JsonObject();
		jsonResult.addProperty("message", message);

		return jsonResult.toString();
	}

	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}

}
