package com.helicalinsight.export;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.exceptions.ExportException;
import com.helicalinsight.efw.utility.*;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The reports that are downloaded in the HDI are processed by this object.
 * It provides methods to generate reports using HTML source or URI, manage PhantomJS
 * locations, and handle report-related functionalities.
 * @author Rajasekhar
 * @since 1.0
 */
public class ReportsProcessor {

    private final static Logger logger = LoggerFactory.getLogger(ReportsProcessor.class);

    /**
     * The location of the screen shot js file
     */
    private String scriptLocation;

    /**
     * The location of the phantom js is different for different os types. The
     * method returns the appropriate binary corresponding to the os. Currently
     * only Windows, Mac OS and Linux are supported.
     *
     * @return The location of the phantom js
     */
    public static String getPhantomLocation() {
        String phantomLocation;
        String reportDirectory = ExportUtils.getReportDirectory() + File.separator;
        if (SystemUtils.IS_OS_WINDOWS) {
            phantomLocation = reportDirectory + "windows_phantomjs.exe";
        } else if (SystemUtils.IS_OS_MAC) {
            phantomLocation = reportDirectory + "macosx_phantomjs";
        } else if (SystemUtils.IS_OS_LINUX) {
            phantomLocation = reportDirectory + "linux_phantomjs";
        } else {
            logger.error("phantomLocation is null. Check phantomjs binary is present or not");
            throw new EfwException("PhantomJs Location is null. Can't process request.");
        }

        File file = new File(phantomLocation);
        phantomLocation = file.getAbsolutePath();
        return phantomLocation;
    }

    /**
     * This method returns the list of file paths when provided with the
     * htmlSource. This method will create a temporary html file on the file
     * system and use it as a source to generate report. The list consists of
     * the report path as the first index and the second index is the html from
     * which that report screen shot is taken
     *
     * @param htmlSource The HTML source as string
     * @param format     The type of the report to be generated
     * @param reportName The name of the report to be generated
     * @return The location of the report on the file system
     */
    public List<String> generateReportUsingHTMLSource(String htmlSource, String format, String reportName) {
        PropertiesFileReader reader = new PropertiesFileReader();
        Map<String, String> messagesMap = reader.read("message.properties");

        String encoding = messagesMap.get("encoding");

        logger.debug("Actual string htmlString = " + htmlSource + ", format = " + format + ", " + "reportName = "
                + reportName);

        ReportsUtility reportsUtility = new ReportsUtility();

        htmlSource = reportsUtility.decodeURLEncoding(htmlSource, encoding);

        htmlSource = reportsUtility.decodeBase64Encoding(htmlSource, encoding);

        // htmlSource = addFunction(htmlSource, request);

        File temporaryDirectory = TempDirectoryCleaner.getTempDirectory();

        // Create Temp directory if it doesn't exists
        FileUtils.createDirectory(temporaryDirectory);

        JsonObject credentials = phantomCredentials();

        try {
            File temporaryHTMLFile = File.createTempFile(reportName, ".html", temporaryDirectory);

            logger.debug("The temporary HTML file location is " + temporaryHTMLFile);

            FileStatusInspectionUtility fileStatusInspectionUtility = new FileStatusInspectionUtility();
            if (fileStatusInspectionUtility.isCompletelyWritten(temporaryHTMLFile, htmlSource, encoding)) {
                logger.info("The htmlString is written successfully with encoding " + encoding);
                return generateReportFromURI(temporaryHTMLFile.toString(), format, reportName, credentials);
            }
        } catch (IOException ex) {
            logger.error("IOException occurred while writing file", ex);
            // handle error
        }
        return null;
    }
    /**
     * phantomCredentials() Generates and returns a JsonObject containing PhantomJS credentials.
     * The credentials include domain, username,password, and organization (if available).
     * The information is obtained from the current user's details.
     *
     * @return JsonObject containing PhantomJS credentials.
     */
    public JsonObject phantomCredentials() {
        JsonObject credentials = new JsonObject();
        Principal principal = AuthenticationUtils.getUserDetails();

        credentials.addProperty("domain", ApplicationProperties.getInstance().getDomain());
        credentials.addProperty("username", principal.getUsername());
        credentials.addProperty("passCode", principal.getPassword());
        String organization = principal.getOrg_name();
        if (organization != null) {
            credentials.addProperty("organization", organization);
        }
        return credentials;
    }

    /**
     * This method returns a list of file locations on the file system when
     * provided with the URI. The list consists of the report path as the first
     * index and the second index is the html from which that report screen shot
     * is taken.
     *
     * @param reportSourceURI The URI of the input html file
     * @param format          The type of the report to be generated
     * @param reportName      The name of the report to be generated
     * @return The location of the report on the file system
     */
	public List<String> generateReportFromURI(String reportSourceURI, String format, String reportName,
			JsonObject settings) {

		this.scriptLocation = getScriptLocation(false);

		String destinationFile = TempDirectoryCleaner.getTempDirectory() + File.separator + reportName;
		List<String> locationsList = new ArrayList<>();

// PhantomExportService exportServiceType = new PhantomServiceManager();
		settings.addProperty("scriptLocation", getScriptLocation(false));
		settings.addProperty("phantomLocation", getPhantomLocation());
		settings.addProperty("chromeDriverLocation", getChromeDriverLocation());
		settings.addProperty("reportSourceUri", reportSourceURI);
		settings.addProperty("destinationFile", destinationFile);

//settings.put("format", Arrays.asList(format.split(",")));
		List<String> stringList = ExportThreadFactory.handleThreads(settings);
// List<String> stringList =
// exportServiceType.handlePhantomjs(settings);
// PhantomJS phantomJS = new PhantomJS(phantomLocation, scriptLocation,
// reportSourceURI, destinationFile,settings);

// Thread phantomThread = new Thread(phantomJS);
// phantomThread.setName("phantom-thread "+System.currentTimeMillis());
		logger.info("CurrentThread = " + Thread.currentThread() + ". Starting phantom-thread "
				+ "to generate the report format.");
// phantomThread.start();
// phantomThread.join();
		logger.info("phantomThread execution is completed. Resuming application thread "
				+ Thread.currentThread().getName());
        if(stringList==null || stringList.size()<=1) {
            throw new RuntimeException("Error occurred while headless chrome. "+stringList);
        }
		String inputString = stringList.get(0);
		String errorString = stringList.get(1);
		if (inputString != null && inputString.length() > 0) {

			if (!(settings.get("format").isJsonArray())) {
				String formatA = settings.get("format").getAsString();
                formatA=formatA.replace("[","").replace("]","");
				String[] split = formatA.split(",");
                JsonArray jsonArray = new JsonArray();

                for (int i = 0; i < split.length; i++) {
                    split[i] = split[i].trim(); // Trim spaces from each element
                    jsonArray.add(split[i]);
                }
				settings.add("format",  jsonArray);
			}
			JsonArray formatArray = GsonUtility.optJsonArray(settings, "format");

			List<String> destinationArray = getFileLocation(inputString);
			if (destinationArray.size() > 0) {
				destinationFile = destinationArray.get(0);
			}
			if (formatArray != null && !formatArray.isEmpty()) {
				for (Object formats : formatArray) {
                    String file = destinationFile + "." + (formats.toString().replaceAll("\"",""));
					File requiredFile = new File(file);
					if (requiredFile.exists()) {
						locationsList.add(file);
					}
				}
			} else {
                String file = destinationFile + "." + (format.toString().replaceAll("\"",""));
				File requiredFile = new File(file);
				if (requiredFile.exists()) {
					locationsList.add(file);
				}

			}

			if (!(locationsList.size() > 0)) {
				cannotExport(errorString, inputString);
			}
		} else {
			cannotExport(errorString, inputString);
		}

		return locationsList;
	}
	/**
     * it handles the case when exporting is not possible.
     *
     * @param errorString 		error message.
     * @param inputString 	    string from the export module.
     */
    public void cannotExport(String errorString, String inputString) {
        logger.error("The input String from the export module is " + inputString);
        String message = "";
        if (inputString != null && inputString.contains("EXPORT ERROR:")) {
            message = inputString.substring(inputString.lastIndexOf("EXPORT ERROR:"));
        }
        throw new ExportException("Error Occurred while exporting. Cause: " + errorString + " " + message);
    }

    /**
     * The corresponding screen shot js file location is returned based on the
     * condition whether requested with url or not.
     *
     * @param isRequestedWithURL if true the js file should screenshot_url
     * @return Returns the screenshot java script file location
     */
    public String getScriptLocation(boolean isRequestedWithURL) {
        URL resource;
        if (isRequestedWithURL) {
            resource = getClass().getClassLoader().getResource("/HDIPhantomjs/screenshot_url" + ".js");

        } else {
            resource = getClass().getClassLoader().getResource("/HDIPhantomjs/screenshot.js");
        }

        if (resource != null) {
            this.scriptLocation = resource.getFile();
        } else {
            throw new EfwException("Phantom JS script location is not found. Can't process  " + "request.");
        }
        File screenShotFile = new File(scriptLocation);
        scriptLocation = screenShotFile.getAbsolutePath();
        return scriptLocation;
    }
    /**
     * Extracts file locations from the input string.
     *
     * @param inputString 		input string.
     * @return List of file locations.
     */
    private List<String> getFileLocation(String inputString) {
        List<String> matchList = new ArrayList<String>();
        Pattern regex = Pattern.compile("##\\((.*?)\\)##");
        Matcher regexMatcher = regex.matcher(inputString);

        while (regexMatcher.find()) {
            matchList.add(regexMatcher.group(1));
        }
        return matchList;

    }

    /**
     * Gets the location of the ChromeDriver.
     * @return ChromeDriver location.
     */
    public static String getChromeDriverLocation() {
        String phantomLocation;
        String chromeLocation;
        String reportDirectory = ExportUtils.getReportDirectory() + File.separator;
        if (SystemUtils.IS_OS_WINDOWS) {
            phantomLocation = reportDirectory + "windows_chromedriver.exe";
            chromeLocation =  String.join(File.separator, reportDirectory,"chrome","chrome.exe");
        } else if (SystemUtils.IS_OS_MAC) {
            phantomLocation = reportDirectory + "macosx_chromedriver";
            chromeLocation = String.join(File.separator, reportDirectory,"chrome","chrome");
        } else if (SystemUtils.IS_OS_LINUX) {
            phantomLocation = reportDirectory + "linux_chromedriver";
            chromeLocation = String.join(File.separator, reportDirectory,"chrome","chrome");
        } else {
            logger.error("phantomLocation is null. Check phantomjs binary is present or not");
            throw new EfwException("PhantomJs Location is null. Can't process request.");
        }
        ObjectNode chromeProps = JacksonUtility.emptyNode();
        chromeProps.put("chromeDriverLocation", new File(phantomLocation).getAbsolutePath());
        chromeProps.put("chromeLocation", new File(chromeLocation).getAbsolutePath());
        return chromeProps.toString();
    }
}