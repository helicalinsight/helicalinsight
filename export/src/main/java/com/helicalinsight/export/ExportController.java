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

import com.google.gson.JsonObject;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.ReportsUtility;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.components.DownloadCacheManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 3/14/2017.
 *
 * @author Rajasekhar
 */
@Controller
public class ExportController {

    private static final Logger logger = LoggerFactory.getLogger(ExportController.class);

    private final static Map<String, String> propertyMap = ControllerUtils.getPropertyMap();

    /**
     * The CSV data of a particular chart is returned. File is written to the
     * response stream as an attachment.
     *
     * @param data     The request parameter data
     * @param request  HttpServletRequest object
     * @param response HttpServletResponse object
     */
    @RequestMapping(value = "/exportData", method = {RequestMethod.GET, RequestMethod.POST})
    public String exportData(@RequestParam("data") String data, HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        JSONObject requestData = JSONObject.fromObject(data);
        ControllerUtils.addCookie(request, response, "hi_report_downloadStatus", "1");
        if (requestData.optString("type").equalsIgnoreCase("xls")) {
            DownloadCacheManager downloadCacheManager = ApplicationContextAccessor.getBean(DownloadCacheManager.class);
            downloadCacheManager.setRequestData(data);
            JsonObject adhocData = downloadCacheManager.getDataFromDatabase(downloadCacheManager.getQuery
                    (downloadCacheManager.getConnectionType(downloadCacheManager.getConnectionId())));

            boolean result = downloadCacheManager.serveCachedContent(request, response, adhocData);
            boolean isAjax = ControllerUtils.isAjax(request);
            if (!result) {
                ControllerUtils.handleFailure(response, isAjax, new EfwServiceException("No Data found"));
            }
            return null;
        }

        // Get the attachment name if provided. If not use time stamp
        String reportName = request.getParameter("reportName") == null ? request.getParameter("reportNameParam") : null;
        String resultNameTag = reportName == null ? ReportsUtility.getReportName(null) : reportName;

        String attachmentName = ReportsUtility.getReportName(reportName);
        CSVUtility csvWriter = new CSVUtility();
        String csvData = csvWriter.getCSVData(data);
        final String format = ".csv";
        String tempDir = TempDirectoryCleaner.getTempDirectory() + File.separator +
                attachmentName + format;
        File fileToDownload = new File(tempDir);
        ApplicationUtilities.createAFile(fileToDownload, csvData);

        String dispositionType = request.getParameter("print") != null ? "inline; " : "attachment; ";

        response.setHeader("Content-Disposition", String.format(dispositionType + "filename=\"%s\"",
                attachmentName + format));
        response.setContentType("application/octet-stream");
        // Write to outputStream
        writeFileToStream(format, response, tempDir);
        ControllerUtils.saveFile(request, resultNameTag, fileToDownload);
        return null;
    }

    /**
     * <p>
     * The main service for printing of the dashboard view in various formats.
     * Accepts htmlString, which is the source of the dashboard view, the format
     * of the report to be generated i.e. pdf or png or jpeg. This service
     * handles only such requests for which the html source is provided. The
     * report file is written to the response stream as an attachment.
     * </p>
     *
     * @param htmlString The request parameter htmlString
     * @param format     The request parameter format of the report to be downloaded
     * @param request    HttpServletRequest object
     * @param response   HttpServletResponse object
     * @return Returns null as the required file is sent through the response as
     * an attachment just to avoid 404
     */
    @RequestMapping(value = "/downloadReport", method = {RequestMethod.POST, RequestMethod.GET})
    public
    @ResponseBody
    String downloadReport(@RequestParam("xml") String htmlString, @RequestParam("format") String format,
                          HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("reportNameParam");
        String reportName = ReportsUtility.getReportName(name);

        String reportParameters = request.getParameter("reportParameters");

        String reportType = request.getParameter("reportType");
        String dir = request.getParameter("dir");
        String reportFileName = request.getParameter("reportFile");

        ControllerUtils.addCookie(request, response, "hi_report_downloadStatus", "1");

        ReportsProcessor reportsProcessor = new ReportsProcessor();
        JSONObject printOptionsJson = new JSONObject();
        printOptionsJson.put("format", JSONArray.fromObject("['" + format + "']"));
        //As reportParameter may contain the Print options so in order to get print option the below method is necessary
        reportParameters = ExportUtils.setPrintOptionsAndDiscardFromReportParameters(reportParameters,
                printOptionsJson);

        List<String> locationsList;
        if (htmlString == null || htmlString.isEmpty()) {
            //The server is using the url to open the page in phantom js and saving the report on the file
            //system. The list of locations of the same are returned as a list.

            ServerSideExport serverSideExport = new ServerSideExport(format, reportName, reportParameters,
                    reportType, dir, reportFileName, printOptionsJson);
            locationsList = serverSideExport.listOfLocations();
        } else {
            locationsList = reportsProcessor.generateReportUsingHTMLSource(htmlString, format, reportName);
        }

        String destinationFile = locationsList.get(0);

        String attachmentName = reportName + "." + format;

        // Set the content type for the response from the properties file
        response.setContentType(propertyMap.get(format));


        // Set the response headers
        String dispositionType = request.getParameter("print") != null ? "inline; " : "attachment; ";

        response.setHeader("Content-Disposition", String.format(dispositionType + "filename=\"%s\"", attachmentName));
        writeFileToStream(format, response, destinationFile);
        File fileToDownload = new File(destinationFile);
        String resultNameTag = (name == null) ? ReportsUtility.getReportName(null) : name;

        // Below code copies the downloaded file and create the file in user
        // specified location if enableSavedResult is enabled in setting.xml
        // file
        ControllerUtils.saveFile(request, resultNameTag, fileToDownload);
        /**
         * Clean the Temporary directory. It was commented because of the new
         * changes in the requirements of email feature. Later on it should be
         * used
         */
        // TempDirectoryCleaner.clean(TempDirectoryCleaner.getTempDirectory());
        return null;
    }

    private void writeFileToStream(String format, HttpServletResponse response, String destinationFile) {
        OutputStream outputStream = null;
        FileInputStream fileInputStream = null;

        try {
            // Write to outputStream
            fileInputStream = new FileInputStream(destinationFile);
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();

        } catch (FileNotFoundException ex) {
            logger.error("FileNotFoundException occurred as the " + format + " file is not " +
                    "generated.", ex);
        } catch (IOException ex) {
            logger.error("IOException occurred", ex);
        } finally {
            ApplicationUtilities.closeResource(fileInputStream);
            ApplicationUtilities.closeResource(outputStream);
        }
    }
}
