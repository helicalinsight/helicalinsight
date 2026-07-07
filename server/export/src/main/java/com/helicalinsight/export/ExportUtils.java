package com.helicalinsight.export;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import org.apache.velocity.exception.ResourceNotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The ExportUtils class provides utility methods for handling export-related operations in Helical Insight.
 * It includes methods for retrieving directories, reading files, and manipulating report parameters.
 * This class is designed to offer various static methods for export-related functionalities.
 * Created on 12/7/2016.
 * @author Somen
 */
public class ExportUtils {
	public static final String JSON_EXTENSION = ".json";
	public static final String GROOVY_EXTENSION = ".groovy";
	public static final String JS_EXTENSION = ".js";
	public static final String SCRIPT_HEADER = "(function doExecute() { \n ";
	public static final String SCRIPT_FOOTER = "\n})()";
	public static final String SCRIPT_BEIGN_INDEX = "\n//BEGINS_HERE\n";
	public static final String SCRIPT_END_INDEX = "\n//ENDS_HERE\n";

	/**
     * Returns the directory path for export templates.
     * @return A String representing the directory path for export templates.
     */
	public static String getTemplatesDirectory() {
		return getReportDirectory() + File.separator + "ExportTemplates";
	}
	/**
     * Returns the temporary directory path for export templates.
     * @return A String representing the temporary directory path for export templates.
     */
	public static String getTemplatesTempDirectory() {
		String tempDirectory = getTemplatesDirectory() + File.separator + "Temp";
		File tempFolder = new File(tempDirectory);
		if (!tempFolder.exists()) {
			tempFolder.mkdir();
		}
		return tempDirectory;
	}
	/**
     * Returns the directory path for reports.
     * @return A String representing the directory path for reports.
     */
	public static String getReportDirectory() {
		return ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Reports";
	}
	/**
     * Reads the content of a file and returns it as a String.
     *
     * @param settingFilePath 		path of the file to be read.
     * @return A String representing the content of the file.
     * @throws RuntimeException If there is an issue in reading the file.
     */
	public static String getFileAsString(String settingFilePath) {
		try {
			return new String(Files.readAllBytes(Paths.get(settingFilePath)), ControllerUtils.defaultCharSet());
		} catch (IOException ioe) {
			throw new RuntimeException("There was a problem in the operation" + " " + ioe.getMessage());
		}
	}	/**
     * Reads the content of a file and returns it as a String.
     *
     * @param jsonFilePath 		path of the file to be read.
     * @return A String representing the content of the file.
     */
	public static JsonObject getFileAsJsonObject(String jsonFilePath) {
		JsonElement root = null;
		try {
			root = JsonParser.parseReader(new FileReader(jsonFilePath));
		} catch (FileNotFoundException e) {
			throw new ResourceNotFoundException(e);
		}

		if (!root.isJsonObject()) {
			throw new IllegalStateException("JSON is not an object");
		}

		return root.getAsJsonObject();

	}
	
	/**
     * This method used to Set the print options and discards them from report parameters .
     *
     * @param reportParameters 		report parameters in JSON format.
     * @param printOptionsJson   	JsonObject containing print options or cookies.
     * @return A String representing modified report parameters.
     */
	public static String setPrintOptionsAndDiscardFromReportParameters(String reportParameters,
			JsonObject printOptionsJson) {
		if (reportParameters != null && !reportParameters.isEmpty()) {
			JsonObject reportParameterAsJson = new Gson().fromJson(reportParameters,JsonObject.class);
			
			if (reportParameterAsJson.has("printOptions")) {
				JsonObject printOptionsObject = reportParameterAsJson.getAsJsonObject("printOptions");
				for(String key : printOptionsObject.keySet()) {
					printOptionsJson.add(key,printOptionsObject.get(key));
				}
				reportParameterAsJson.remove("printOptions");
				reportParameters = reportParameterAsJson.toString();
			}
		}
		return reportParameters;
	}
	/**
     * Checks if the given process is active.
     *
     * @param process The Process object to be checked.
     * @return {@code true} if the process is active, {@code false} otherwise.
     */
	public static boolean isProcessActive(Process process) {

		try {
			if (process != null) {
				process.exitValue();
			}

			return false;
		} catch (IllegalThreadStateException e) {
			return true;
		}
	}
}
