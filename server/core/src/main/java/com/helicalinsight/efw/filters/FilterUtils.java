package com.helicalinsight.efw.filters;

import java.io.File;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;

/**
 * @author Somen Created on 9/6/2015.
 */
public class FilterUtils {

	public static String getValidationConfigurationFilesDirectory() {
		return ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin" + File.separator
				+ "Validation";
	}
	

	/**
	 * newGetExcludeUlrPattern using gson
	 * @return JsonArray
	 */
	public static JsonArray newGetExcludeUlrPattern() {
		String validationXmlPath = getValidationFilePath();
		File validationFile = new File(validationXmlPath);
		JsonObject validationXmlJson;
		if (validationFile.exists()) {
			IProcessor processor = ResourceProcessorFactory.getIProcessor();
			validationXmlJson = processor.getJsonObject(validationXmlPath, false);
			return GsonUtility.optJsonArray(validationXmlJson.getAsJsonObject("excludePatterns"),"pattern");
		}
		return null;
	}

	public static String getValidationFilePath() {
		return ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin" + File.separator
				+ "validation.xml";
	}
	

	/**
	 * newIsExcludePattern using gson
	 * @param JsonArray excludeArray
	 * @param String requestedUrl
	 * @return boolean
	 */
	public static boolean newIsExcludePattern(JsonArray excludeArray, String requestedUrl) {
		if (excludeArray != null) {
			for (int index = 0; index < excludeArray.size(); index++) {
				String string = excludeArray.get(index).getAsString();
				if (requestedUrl.matches(string)) {
					return true;
				}
			}
		}
		return false;
	}
}
