/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.export;

import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.utility.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The reports that are downloaded in the HI are processed by this object.
 *
 * @author Rajasekhar
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
    private static String getPhantomLocation() {
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

        logger.debug("Actual string htmlString = " + htmlSource + ", format = " + format + ", " +
                "reportName = " + reportName);

        ReportsUtility reportsUtility = new ReportsUtility();

        htmlSource = reportsUtility.decodeURLEncoding(htmlSource, encoding);

        htmlSource = reportsUtility.decodeBase64Encoding(htmlSource, encoding);

        //htmlSource = addFunction(htmlSource, request);

        File temporaryDirectory = TempDirectoryCleaner.getTempDirectory();

        // Create Temp directory if it doesn't exists
        FileUtils.createDirectory(temporaryDirectory);

        JSONObject credentials = phantomCredentials();

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
            //handle error
        }
        return null;
    }

    public JSONObject phantomCredentials() {
        JSONObject credentials = new JSONObject();
        Principal principal = AuthenticationUtils.getUserDetails();

        credentials.put("domain", ApplicationProperties.getInstance().getDomain());
        credentials.put("username", principal.getUsername());
        credentials.put("passCode", principal.getPassword());
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
                                              JSONObject settings) {

        this.scriptLocation = getScriptLocation(false);

        String destinationFile = TempDirectoryCleaner.getTempDirectory() + File.separator +
                reportName;
        List<String> locationsList = new ArrayList<>();
        try {
            String phantomLocation = getPhantomLocation();

            PhantomJS phantomJS = new PhantomJS(phantomLocation, scriptLocation, reportSourceURI, destinationFile,
                    settings);

            Thread phantomThread = new Thread(phantomJS);
            phantomThread.setName("phantom-thread");
            logger.info("CurrentThread = " + Thread.currentThread() + ". Starting phantom-thread " +
                    "to generate the report format.");
            phantomThread.start();
            phantomThread.join();
            logger.info("phantomThread execution is completed. Resuming application thread " + Thread.currentThread()
                    .getName());

            JSONArray formatArray = settings.getJSONArray("format");
            for (Object formats : formatArray) {
                locationsList.add(destinationFile + "." + formats.toString());
            }

            // locationsList.add(1, reportSourceURI);
        } catch (InterruptedException ex) {
            logger.error("InterruptedException occurred", ex);
            throw new RuntimeException(ex);
        }
        return locationsList;
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
}