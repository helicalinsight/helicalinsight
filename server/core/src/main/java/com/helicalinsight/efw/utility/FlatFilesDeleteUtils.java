package com.helicalinsight.efw.utility;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;

public class FlatFilesDeleteUtils {

	private static final Logger logger = LoggerFactory.getLogger(FlatFilesDeleteUtils.class);
	private static ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

	public static String deleteFolderOrFile(JsonArray listOfFilesWithConId) throws IOException {
		for (int index = 0; index < listOfFilesWithConId.size(); index++) {
			JsonObject arrayElement = listOfFilesWithConId.get(index).getAsJsonObject();
			String dataSourceId = GsonUtility.optString(arrayElement,"dataSourceId");
			String fileName = arrayElement.get("fileName").getAsString();
			deleteFileOrFolder(dataSourceId, fileName);
		}

		return "file(s) deleted successfully..";
	}

	private static void deleteFileOrFolder(String dataSourceId, String fileName) throws IOException {

		boolean flag;
		if (StringUtils.isNotEmpty(dataSourceId)) {
			String globalIdJson = DataSourceUtils.globalIdJson(Integer.parseInt(dataSourceId));
			JsonObject globalConObject = JsonParser.parseString(globalIdJson).getAsJsonObject();
			String fileNameFromGlobalConnection = globalConObject.get("url").getAsString();

			flag = fileNameFromGlobalConnection.equals(fileName);
			if(flag){
				delete(fileName);
			}else{
				throw new OperationFailedException("Please provide the valid dataSourceId or the fileName, dataSourceId or fileName is not valid");
			}
		}else
			delete(fileName);

	}

	private static void delete(String fileName) {
		String absolutePath = prepareAbsolutePathForFileName(fileName);
		File dir = new File(absolutePath);

		if (dir.exists()) {
			if (dir.isDirectory()) {

				FileUtils.deleteDirectory(dir);
				logger.debug("Deleting Directory");

			} else {
				FileUtils.deleteFile(dir);
				logger.debug("Deleting File");
			}

		} else {
			throw new OperationFailedException("file does not exist.");
		}
	}

	private static String prepareAbsolutePathForFileName(String fileName) {
		String directoryPath = null;
		directoryPath = DrillConfigUrlContext.provideDrillStorageDirectoryPath(directoryPath);
		String settingPath = applicationProperties.getSystemDirectory();
		return settingPath + directoryPath + File.separator + fileName;
	}


}
