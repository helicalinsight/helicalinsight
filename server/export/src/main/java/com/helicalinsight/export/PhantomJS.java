
package com.helicalinsight.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The instance of this class is a separate thread which generates the pdf or
 * png or jpeg formats on the file system.
 *
 * @author Rajasekhar
 * @since 1.0
 */

/**
 * this class is no longer used
 * <p> Use {@link HeadlessChromeManager}</p> 
 * @author HDEV016
 *
 */
@Deprecated
public class PhantomJS implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(PhantomJS.class);

	
	// created for gson and removed final keyword
	private JsonObject printOptions;

	private static Integer counter = 0;
	/**
	 * The location of the phantom js binary
	 */
	private String phantomLocation;

	/**
	 * The file used by phantom js to create a screenshot
	 */
	private String inputFileString;

	/**
	 * The output of the phantom js on the file system(Temp directory)
	 */
	private String destinationFileString;

	/**
	 * The location of the screenshot.js file
	 */
	private String scriptLocation;

	private String errorString;
	private String inputString;

	/**
	 * Constructs an object of this class by setting all the required fields
	 *
	 * @param phantomLocation       The location of the phantom js binary
	 * @param scriptLocation        The location of the screenshot.js file
	 * @param inputFileString       The file used by phantom js to create a
	 *                              screenshot
	 * @param destinationFileString The output of the phantom js on the file
	 *                              system(Temp
	 * @param printOptions          The session id of the logged in user
	 */



//created duplicte for gson
	public PhantomJS(String phantomLocation, String scriptLocation, String inputFileString,
			String destinationFileString, JsonObject printOptions) {
		this.inputFileString = inputFileString;
		this.destinationFileString = destinationFileString;
		this.scriptLocation = scriptLocation;
		this.phantomLocation = phantomLocation;
		this.printOptions = printOptions;
		this.errorString = null;
		this.inputString = null;
		logger.info("Print options is", printOptions);
	}

	/**
	 * Starts the phantom thread
	 */
	@Override
	public void run() {
		create();
	}

	/**
	 * Creates an operating system process by invoking the phantom js. The thread
	 * which invokes this thread will be temporarily resumed till this process
	 * completes. The phantom js creates the screen shot of the html from a file on
	 * the file system.
	 */
	private void create() {
		/*
		 * Fixed the bug 'parse error' in case the strings passed to the command line
		 * consist of url encoding.
		 */
		String encoding = this.checkEncoding();

		List<String> command = buildCommand();

		if (logger.isDebugEnabled()) {
			logger.debug("Phantom call : " + phantomLocation + " " + scriptLocation + " " + inputFileString + " "
					+ destinationFileString);
			// logger.debug("Phantom Command is " + command);
		}
		this.triggerPhantomProcess(encoding, command);

		logger.debug("Thread.currentThread() = " + Thread.currentThread() + " finished execution.");

	}

	public static Integer getCounter() {
		return counter;
	}

	public static void decrementCounter() {
		counter = counter - 1;
	}

	public String getErrorString() {
		return errorString;
	}

	public void setErrorString(String errorString) {
		this.errorString = errorString;
	}

	public String getInputString() {
		return inputString;
	}

	public void setInputString(String inputString) {
		this.inputString = inputString;
	}

	private String checkEncoding() {
		String encoding = ApplicationUtilities.getEncoding();

		if (isStringURLEncoded(phantomLocation)) {
			phantomLocation = urlDecode(phantomLocation, encoding);
		}

		if (isStringURLEncoded(scriptLocation)) {
			scriptLocation = urlDecode(scriptLocation, encoding);
		}

		if (isStringURLEncoded(inputFileString)) {
			inputFileString = urlDecode(inputFileString, encoding);
		}

		if (isStringURLEncoded(destinationFileString)) {
			destinationFileString = urlDecode(destinationFileString, encoding);
		}
		return encoding;
	}

	private List<String> buildCommand() {
		List<String> command = new ArrayList<>();
		command.add(0, phantomLocation);
		command.add(1, "--ssl-protocol=any");
		command.add(2, scriptLocation);
		try {
			command.add(3,
					new String(Base64.encodeBase64(inputFileString.getBytes(ApplicationUtilities.getEncoding()))));
		} catch (Exception ignore) {

		}
		// command.add(2, inputFileString);
		command.add(4, FilenameUtils.removeExtension(destinationFileString));
		command.add(5, printOptions.get("domain").getAsString());
		command.add(6, printOptions.get("username").getAsString());
		command.add(7, printOptions.get("passCode").getAsString());
		command.add(8, printOptions.has("organization") ? printOptions.get("organization").getAsString() : "\"\"");
		command.add(9, printSettingFileName());
		command.add(10, ExportUtils.getReportDirectory());
		if (printOptions.has("format")) {
			String format = "";
			for (Object formatItem : printOptions.getAsJsonArray("format")) {
				format += "," + formatItem;
			}
			format = format.substring(1);

			command.add(11, format);
		}
		return command;
	}

	private void triggerPhantomProcess(String encoding, List<String> command) {
		Process process;
		InputStream inputStream = null;

		InputStream errorStream = null;
		long timeOut = 0L;
		try {

			ProcessBuilder processBuilder = new ProcessBuilder(command);
			process = processBuilder.start();

			// Added the timeout for phantom-js process execution default being
			// 60000 ms
			JsonObject export = GsonUtility.optJsonObject(JsonUtils.newGetSettingsJson(),"export");
			if (export != null && !export.entrySet().isEmpty()) {
				Integer phantomTime = GsonUtility.optInt(export,"phantomTimeOut");
				timeOut = phantomTime != 0 ? phantomTime : 600000;
			}
			if (!waitFor(timeOut, TimeUnit.MILLISECONDS, process)) {
				process.destroy();

				this.setInputString("Process Timed out");
				this.setErrorString("Process Timed out. Please increase the export phantomTimeout");

			} else {
				inputStream = process.getInputStream();
				errorStream = process.getErrorStream();
				this.setInputString(IOUtils.toString(inputStream, encoding));
				this.setErrorString(IOUtils.toString(errorStream, encoding));
			}
			// Read the output of Phantom JS and also the error stream for
			// errors

			if (logger.isDebugEnabled()) {
				logger.debug("Phantom input message is " + inputString);
				logger.debug("Phantom error message is " + errorString);
			}

		} catch (IOException ex) {
			logger.error("IOException during phantom call", ex);
		} catch (InterruptedException ex) {
			logger.error("InterruptedException during phantom call", ex);
		} finally {
			ApplicationUtilities.closeResource(inputStream);
			ApplicationUtilities.closeResource(errorStream);
		}
	}

	private String printSettingFileName() {
		JsonObject printSettings = GsonUtility.optJsonObject(this.printOptions,"printOptions");
		String jsonFileName = extractSettingJsonFileName(printOptions);
		if (jsonFileName == null) {
			jsonFileName = ExportUtils.getTemplatesDirectory() + File.separator + "defaultTemplate"
					+ ExportUtils.JSON_EXTENSION;
		}

		return jsonFileName;
	}

	public static int incrementCounter() {
		counter = counter + 1;
		return counter;
	}

	
//created new method for gson
	public static String extractSettingJsonFileName(JsonObject printSettings) {
		String jsonFileName = null;
		if (printSettings != null && !printSettings.entrySet().isEmpty()) {
			if (!printSettings.has("body") && printSettings.has("templateId")) {
				String templateId = printSettings.get("templateId").getAsString();
				jsonFileName = ExportUtils.getTemplatesDirectory() + File.separator + templateId
						+ ExportUtils.JSON_EXTENSION;
			} else if (printSettings.has("body")) {
				long currentTime = System.currentTimeMillis();
				String commonFileName = ExportUtils.getTemplatesTempDirectory() + File.separator + currentTime;
				jsonFileName = commonFileName + ExportUtils.JSON_EXTENSION;
				File jsonFile = new File(jsonFileName);
				File jsFile = new File(commonFileName + ExportUtils.JS_EXTENSION);
				try {
					printSettings.remove("passCode");
					printSettings.remove("organization");
					printSettings.remove("username");
					printSettings.remove("domain");
					// printSettings.discard("format");
					// printSettings.toString(1) is converted to gson indentation first and then
					// added
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					String jsonString = gson.toJson(printSettings);
					FileUtils.writeStringToFile(jsonFile, jsonString, ApplicationUtilities.getEncoding());
					JsonObject body = printSettings.getAsJsonObject("body");
					if (body.has("hasScript")) {
						String scriptText = ExportUtils.SCRIPT_HEADER + GsonUtility.optString(body, "printScript")
								+ ExportUtils.SCRIPT_FOOTER;

						FileUtils.writeStringToFile(jsFile, scriptText, ApplicationUtilities.getEncoding());
					}
				} catch (IOException ioe) {
					logger.error("There was an error writing the file", ioe);
				}
			}
		}
		return jsonFileName;
	}

	
	/**
	 * true if the string is url encoded. Otherwise false
	 *
	 * @param string The string under concern which is supposed to be url encoded
	 * @return true if the string is url encoded
	 */
	private boolean isStringURLEncoded(String string) {
		Pattern pattern = Pattern.compile("[%]");
		Matcher matcher = pattern.matcher(string);
		return matcher.find();
	}

	/**
	 * The url decoded string
	 *
	 * @param urlEncodedString The string under concern which is supposed to be url
	 *                         encoded
	 * @param encoding         The encoding; usually utf-8
	 * @return the url decoded string
	 */
	private String urlDecode(String urlEncodedString, String encoding) {
		try {
			return URLDecoder.decode(urlEncodedString, encoding);
		} catch (UnsupportedEncodingException e) {
			logger.error("Character encoding is not supported " + encoding, e);
			// handle error
		}
		return null;
	}

	// Adopted from jdk 8 source code as we are using only jdk 7
	public boolean waitFor(long timeout, TimeUnit unit, Process process) throws InterruptedException {
		long startTime = System.nanoTime();
		long rem = unit.toNanos(timeout);

		do {
			try {
				process.exitValue();
				return true;
			} catch (IllegalThreadStateException ex) {
				if (rem > 0)
					Thread.sleep(Math.min(TimeUnit.NANOSECONDS.toMillis(rem) + 1, 100));
			}
			rem = unit.toNanos(timeout) - (System.nanoTime() - startTime);
		} while (rem > 0);
		return false;
	}
}