package com.helicalinsight.datasource;

import com.google.gson.JsonObject;

public abstract class AbstractFileOperationsOverNetwork {


	public abstract String addFile(JsonObject parameter);

	public abstract String getFile(JsonObject parameter);

	public abstract String updateFiles(JsonObject parameter);

	public abstract String deleteFile(JsonObject parameter);

	public abstract String createFolder(JsonObject parameter);

	public abstract String deleteFolder(JsonObject parameter);

	public abstract String renameFolder(JsonObject parameter);

    public abstract void setDrillConfigConnection(JsonObject parameter);

    public abstract boolean isFileExists(String fileName);
}
