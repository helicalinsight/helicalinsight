package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.AbstractFileOperationsOverNetwork;
import com.helicalinsight.datasource.FileOperationOverNetworkFactory;
import com.helicalinsight.efw.serviceframework.IComponent;

public class FileCopierOverNetworkHandler implements IComponent {

	@Override
	public String executeComponent(String jsonFormData) {
		JsonObject formData = new Gson().fromJson(jsonFormData,JsonObject.class);
		JsonObject parameters = formData.getAsJsonObject("parameters");
		JsonObject result = new JsonObject();
		String transmissionType = formData.get("transmissionType").getAsString();
		String operationType = formData.get("operationType").getAsString();
        String message = handleFileTransfer(parameters, transmissionType, operationType);

		result.addProperty("message", message);
		return result.toString();

	}

    public String handleFileTransfer(JsonObject parameters, String transmissionType, String operationType) {
        String message = "Something went wrong..";

        AbstractFileOperationsOverNetwork fileOperationHandlerClass = FileOperationOverNetworkFactory
                .getFileOperationHandlerClass(transmissionType);
        message = switch (operationType) {
            case "addFile" -> fileOperationHandlerClass.addFile(parameters);
            case "getFile" -> fileOperationHandlerClass.getFile(parameters);
            case "updateFile" -> fileOperationHandlerClass.updateFiles(parameters);
            case "deleteFile" -> fileOperationHandlerClass.deleteFile(parameters);
            case "createFolder" -> fileOperationHandlerClass.createFolder(parameters);
            case "deleteFolder" -> fileOperationHandlerClass.deleteFolder(parameters);
            case "renameFolder" -> fileOperationHandlerClass.renameFolder(parameters);
            default -> message;
        };
        return message;
    }

    @Override
	public boolean isThreadSafeToCache() {

		return true;
	}

}
