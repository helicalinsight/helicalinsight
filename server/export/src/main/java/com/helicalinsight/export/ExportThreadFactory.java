//ExportThreadFactory.java
package com.helicalinsight.export;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.ExportException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.JsonUtils;

/**
 * This is Factory class for threads which is able to switch threads to download
 * reports as an attachments on the file system.
 * 
 * @author Rajasekhar M
 *
 */
public class ExportThreadFactory {
	private static Logger logger = LoggerFactory.getLogger(ExportThreadFactory.class);

		
	 /**
     * This method handles the creation and execution of export threads based on the provided settings.
     * 
     * @param settings 		JsonObject containing export settings.
     * @return A List of Strings representing the results of the export threads.
     * @throws ExportException If there is an issue with the export configuration or execution.
     */
	public static List<String> handleThreads(JsonObject settings) {
		PhantomExportService phantomExportService = null;
		List<String> stringList = null;
		JsonObject settingsJson = JsonUtils.newGetSettingsJson();
		if (settingsJson.has("export")) {
			//JsonArray printModes = settingsJson.optJSONObject("export").optJSONArray("printingModes");
			JsonObject exportObject = GsonUtility.optJsonObject(settingsJson,"export");
			JsonObject printModes = GsonUtility.optJsonObject(exportObject,"printingModes");
			JsonArray printMode =  GsonUtility.optJsonArray(printModes,"printingMode");
			if (printMode != null && printMode.isJsonArray() && printMode.size() > 1) {
				for (Object object : printMode) {
					JsonObject print = (JsonObject) object;
					if (print.has("default") && print.get("default").getAsString().equalsIgnoreCase("true")) {
						phantomExportService = (PhantomExportService) FactoryMethodWrapper
								.getUntypedInstance(print.get("class").getAsString());
						logger.info("Loaded one of printing service for exporting report");
						break;
					}
				}
			}
			if (phantomExportService == null) {
				phantomExportService = new PhantomServiceManager();
			}
			stringList = phantomExportService.handlePhantomjs(settings);

			return stringList;
		}

		throw new ExportException("The configuration 'export' is missing in setting.xml");
	}
	


	

}