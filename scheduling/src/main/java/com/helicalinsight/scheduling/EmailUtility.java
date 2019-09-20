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

package com.helicalinsight.scheduling;

import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.CSVUtility;
import com.helicalinsight.export.ReportsProcessor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * An utility class for the Emailing component of the application, which takes
 * care of getting the attachments generated in the Temp directory of the System
 * directory
 *
 * @author Rajasekhar
 */
public class EmailUtility {

    private static final Logger logger = LoggerFactory.getLogger(EmailUtility.class);

    /**
     * Returns an array of the locations of the attachments. reportsSourceType
     * 'adhoc' means that the report is to be processed expecting that the
     * reportSource is html data. Otherwise the reportSource is url. The class
     * uses the phantom js related api to get the attachments.
     * <p/>
     * Note: Request parameter 'data' as in '/exportData' controller method has
     * to be provided for csv to be processed
     *
     * @param formats          The email attachment formats
     * @param reportSource     The html source of the report or the url
     * @param reportSourceType 'adhoc' or not 'adhoc'
     * @param reportName       The name of the report from the request
     * @param parameterData    The data related to csv
     * @return An array of the locations of the attachments
     */
    public static String[] getAttachmentsArray(String[] formats, String reportSource, String reportSourceType,
                                               String reportName, String parameterData, JSONObject printOptions) {
        String[] attachments = new String[formats.length];

        int counter = 0;
        List<String> locationsList;
        List<String> formatList = Arrays.asList(formats);
        List<String> locations = new ArrayList<>();

        // To pass the HTML source file location for the rest of the reports
        // formats
        String uri = null;

        // Generate pdf, png, jpeg or csv exactly once and send only one
        // attachment for each format type
        Set<String> formatsSet = new HashSet<>(Arrays.asList(formats));
        ReportsProcessor reportsProcessor = new ReportsProcessor();
        if ("adhoc".equals(reportSourceType) || reportSourceType == null) {
            // htmlString is provided to get the report
            for (String format : formatsSet) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Preparing attachment for the format: " + format);
                }
                // The temporary HTML file needs to be generated only once.
                // For the next format the URI can be used
                if ((counter == 0) && (!"csv".equalsIgnoreCase(format))) {
                    locationsList = reportsProcessor.generateReportUsingHTMLSource(reportSource, format, reportName);

                    if (logger.isDebugEnabled()) {
                        logger.debug("The attachments files location list: " + locationsList);
                    }
                    attachments[counter] = locationsList.get(0);
                    uri = locationsList.get(1);

                    // No need to proceed with the rest of the logic. Continue
                    // with the next formats; update the counter.;
                    counter++;
                    continue;
                }

                /**
                 * Request parameter 'data' as in '/exportData' controller
                 * method has to be provided
                 */
                if ("csv".equalsIgnoreCase(format)) {
                    if (!(parameterData == null)) {
                        counter = insertCsvAttachment(reportName, parameterData, attachments, counter);
                    } else {
                        logger.error("Couldn't get csv attachment as the request parameter is null.");
                    }
                    continue;
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("HTML file already created. Now, requesting with uri: " + uri);
                }

                locationsList = reportsProcessor.generateReportFromURI(uri, format, reportName, printOptions);
                attachments[counter] = locationsList.get(0);
                counter++;
            }
        } else {
            // This block is being used by scheduling
            // URI with parameters is provided to get the report
            if (formatList.contains("csv")) {
                if (!(parameterData == null)) {
                    insertCsvAttachment(reportName, parameterData, attachments, counter);
                    locations.add(TempDirectoryCleaner.getTempDirectory() + File.separator +
                            reportName + ".csv");
                } else {
                    logger.error("Couldn't get csv attachment as the request parameter is null.");
                }
            }
            //Here reportSource is a url with username, password, path and parameters
            printOptions.put("format", JSONArray.fromObject(formats));
            locations.addAll(reportsProcessor.generateReportFromURI(reportSource, null, reportName, printOptions));
            attachments = locations.toArray(attachments);
        }
        return attachments;
    }

    // Return the incremented counter after placing the attachment location in
    // the attachments
    public static int insertCsvAttachment(String reportName, String parameterData, String[] attachments,
                                          int arrayIndex) {
        CSVUtility csvWriter = new CSVUtility();
        String result = csvWriter.getCSVData(parameterData);
        File tempCSVFile = new File(TempDirectoryCleaner.getTempDirectory() + File.separator +
                reportName + ".csv");
        ApplicationUtilities.createAFile(tempCSVFile, result);
        if (logger.isDebugEnabled()) {
            logger.debug("CSV file " + (tempCSVFile.exists() ? "created exists." : "is not " + "created."));
        }
        attachments[arrayIndex] = tempCSVFile.toString();
        return ++arrayIndex;
    }
}
