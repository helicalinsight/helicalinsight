package com.helicalinsight.adhoc.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.QueryExecutor;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
/**
 * Utility class for extracting data from EFWD files.
 * This class provides methods to read EFWD files and retrieve data from them.
 * It is used to process EFWD files and obtain JSON data based on specified parameters.
 */
public class EFWDExtractorUtil {
	private static final Logger logger = LoggerFactory.getLogger(EFWDExtractorUtil.class);
	static ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
	/**
     * Reads an EFWD file and extracts data based on the specified parameters.
     *
     * @param directory 		 directory containing the EFWD file.
     * @param mapId     		 ID of the map.
     * @param type      		 type of the file.
     * @return A JSON string representing the extracted data.
     * @throws EfwServiceException If the EFWD file is not found or cannot be processed.
     */
	public static String readEFWDFile(String directory, int mapId, String type) {

		/*
		 * String efwdFile; String efwd = null;
		 */

		Map<String, String> parameters = new HashMap<>();
		parameters.put("dir", directory);
		parameters.put("map_id", "" + mapId);
		ControllerUtils.checkForNullsAndEmptyParameters(parameters);

		IProcessor processor = ResourceProcessorFactory.getIProcessor();

		String settingPath = applicationProperties.getSettingPath();
		JsonObject settings = processor.getJsonObject(settingPath, false);
		String extension = settings.getAsJsonObject("Extentions").get("efwd").getAsString();
		String solutionDirectory = applicationProperties.getSolutionDirectory();
		// removing the '/setting.xml' from the settingPath
		/*
		 * if (null != settingPath && settingPath.length() > 0) { int endIndex =
		 * settingPath.lastIndexOf("/"); if (endIndex != -1) { adminPath =
		 * settingPath.substring(0, endIndex); } }
		 * 
		 * Sysout("settingPath   :" + settingPath +
		 * " absolutePath   :" + adminPath);
		 */
		// getting all the files from DataSourceMetadata folder
		File folder = new File(solutionDirectory + File.separator + directory);

		File[] listOfFiles = folder.listFiles();
		Assert.notNull(listOfFiles, "List of files is null. Directory has no content.");

		boolean flag = false;
		if (listOfFiles != null) {
			for (File listOfFile : listOfFiles) {

				if (listOfFile.isFile()) {
					String fileName = listOfFile.getName();

					String[] tokens = fileName.split("\\.(?=[^\\.]+$)");

					// picking the efwd file by checking name and extension
					if ((tokens.length > 1) && (tokens[0].equalsIgnoreCase(type))
							&& (tokens[1].equalsIgnoreCase(extension))) {
						// efwd = fileName;
						flag = true;
					}
				}
			}
		} else {
			throw new EfwServiceException("The list of files is empty. Couldn't find any efwd.");
		}

		/*
		 * // converting efwd file into jsonObject efwdFile = folder +
		 * File.separator + efwd; Sysout("efwdFile  :" + efwdFile);
		 * JSONObject fileAsJson = processor.getJSONObject(efwdFile, false);
		 * 
		 * // validating the efwdJsonObject ResourceValidator resourceValidator
		 * = new ResourceValidator(fileAsJson); boolean isValidEFWD =
		 * resourceValidator.validateEfwd();
		 */

		//String data = "{" + "'dir':" + "'" + directory + "'," + "'map_id':" + mapId + "}";
		JsonObject dataJson= new JsonObject();
		dataJson.addProperty("dir", directory);
		dataJson.addProperty("map_id",mapId);
		//need to find out the file name in sqlfunctionxmlmapping.properties
		dataJson.addProperty("file","postgres.efwd");

		if (flag) {
			QueryExecutor queryExecutor = new QueryExecutor(dataJson.toString(), applicationProperties);
			JsonObject jsonObject = queryExecutor.getResultSet();
			if (jsonObject != null) {

				return jsonObject.toString();

			} else {
				throw new RequiredParameterIsNullException("Request parameter data is null");
			}

		}

		return null;
	}
}
