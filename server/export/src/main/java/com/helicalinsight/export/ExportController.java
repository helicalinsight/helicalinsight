package com.helicalinsight.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.ActiveQueryRegistry;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.ReportsUtility;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.components.DownloadCacheManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * ExportController class handling report exports and downloads.
 * Created by user on 3/14/2017.
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
    @RequestMapping(value = "/downloadReport", method = {RequestMethod.GET, RequestMethod.POST})
    public String exportData(@RequestParam(value = "data", required = false) String data,
                             @RequestParam(value = "xml", required = false) String htmlString,
                             @RequestParam(value = "format", required = false) String format,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        if(data==null){
            downloadReport(htmlString,format,request,response);
        }else{
            downloadCsvExcel(data,request,response);
        }
        return null;
    }
    /**
     * Handles the export of CSV or Excel format based on the request parameters.
     * 
     * @param data       Parameter data in JSON format containing export details.
     * @param request    Request object containing request parameters.
     * @param response   Response object for setting response headers and streaming the file.
     * @throws IOException If an I/O error occurs during the export process.
     */
    private void downloadCsvExcel(String data, HttpServletRequest request, HttpServletResponse response) throws IOException{
        JsonObject requestData = JsonParser.parseString(data).getAsJsonObject();
		DownloadCacheManager downloadCacheManager = ApplicationContextAccessor.getBean(DownloadCacheManager.class);
        ControllerUtils.addCookie(request, response, "hi_report_downloadStatus", "1");
        if (requestData.has("isAdhoc") ||
                GsonUtility.optString(requestData,"type").toLowerCase().contains("xls")) {
            downloadCacheManager.setRequestData(data);
            Object adhocData = downloadCacheManager.getDataFromDatabase(downloadCacheManager.getQuery
                    (downloadCacheManager.getConnectionType(downloadCacheManager.getConnectionId())));

            boolean result = downloadCacheManager.serveCachedContent(request, response, adhocData);
            boolean isAjax = ControllerUtils.isAjax(request);
            if (!result) {
                ControllerUtils.handleFailure(response, isAjax, new EfwServiceException("No Data found"));
            }
            return;
        }

        // Get the attachment name if provided. If not use time stamp
        String reportName = request.getParameter("reportName") == null ? request.getParameter("reportNameParam") : null;
        String resultNameTag = reportName == null ? ReportsUtility.getReportName(null) : reportName;

        String attachmentName = ReportsUtility.getReportName(reportName);
        CSVUtility csvWriter = new CSVUtility();
        String csvData = csvWriter.getCSVData(data);
        final String downloadFormat = ".csv";
        String tempDir = TempDirectoryCleaner.getTempDirectory() + File.separator + attachmentName + downloadFormat;
        File fileToDownload = new File(tempDir);
        ApplicationUtilities.createAFile(fileToDownload, csvData);
        String dispositionType = request.getParameter("print") != null ? "inline; " : "attachment; ";
        response.setHeader("Content-Disposition", String.format(dispositionType + "filename=\"%s\"", attachmentName + downloadFormat));
        response.setContentType("application/octet-stream");
        if(fileToDownload!=null)
        	response.setHeader("Content-Length", Long.toString(fileToDownload.length()));
        writeFileToStream(downloadFormat, response, tempDir,request.getParameter("requestId"));
        ControllerUtils.saveFile(request, resultNameTag, fileToDownload);

    }

    /**
     * Handles the download of a report in a specific format.
     *
     * @param htmlString   the report will be generated from this HTML source.
     * @param format       The export format (e.g., "pdf", "csv", "xlsx").
     * @param request      Request object containing request parameters.
     * @param response     Response object for setting response headers and streaming the file.
     * @return             Null, as the response is directly handled for file download.
     */
    private String downloadReport(String htmlString,
                          String format,
                          HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("reportNameParam");

        String reportName = ReportsUtility.getReportName(name);

        String reportParameters = request.getParameter("reportParameters");

        String reportType = request.getParameter("reportType");
        String dir = request.getParameter("dir");
        String reportFileName = request.getParameter("reportFile");

        ControllerUtils.addCookie(request, response, "hi_report_downloadStatus", "1");

        ReportsProcessor reportsProcessor = new ReportsProcessor();
        JsonObject printOptionsJson = new JsonObject();
        printOptionsJson.add("format", JsonParser.parseString("['" + format + "']").getAsJsonArray());
        JsonArray cookiesArray = ControllerUtils.newGetCookieArray(request);
        printOptionsJson.add("cookie", cookiesArray);
        //As reportParameter may contain the Print options so in order to get print option the below method is necessary
        reportParameters = ExportUtils.setPrintOptionsAndDiscardFromReportParameters(reportParameters,
                printOptionsJson);

        List<String> locationsList;
        if (htmlString == null || htmlString.isEmpty()) {

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
        File fileToDownload = new File(destinationFile);

        response.setHeader("Content-Disposition", String.format(dispositionType + "filename=\"%s\"", attachmentName));
        if(fileToDownload!=null)
        	response.setHeader("Content-Length", Long.toString(fileToDownload.length()));
        writeFileToStream(format, response, destinationFile,request.getParameter("requestId"));
        String resultNameTag = (name == null) ? ReportsUtility.getReportName(null) : name;

        // Below code copies the downloaded file and create the file in user
        // specified location if enableSavedResult is enabled in setting.xml
        // file
        ControllerUtils.saveFile(request, resultNameTag, fileToDownload);
        return null;
    }

    /**
     * Writes the file to the output stream.
     * Method responsible for the efficient and real-time transmission of a file's content to the client's browser through the HTTP response 
     * @param format           export format.
     * @param response         HttpServletResponse object for writing binary data.
     * @param destinationFile  destination file path.
     * @param requestId        The request ID.
     */
    private void writeFileToStream(String format, HttpServletResponse response, String destinationFile,String requestId) {
    	OutputStream outputStream=null;
    	FileInputStream fileInputStream = null;
        ActiveQueryRegistry registry=ActiveQueryRegistry.getRegistry();
        try {
            // Write to outputStream
            outputStream = response.getOutputStream();
        	File fileToDownload=new File(destinationFile);
            fileInputStream=new FileInputStream(fileToDownload);
            DownloadCacheManager.writeFileToStream(fileToDownload.length(),outputStream,fileInputStream,requestId,registry);

        } catch (FileNotFoundException ex) {
            logger.error("FileNotFoundException occurred as the " + format + " file is not " +
                    "generated.", ex);
        } catch (IOException ex) {
            logger.error("IOException occurred", ex);
        } finally {
            ApplicationUtilities.closeResource(fileInputStream);
            ApplicationUtilities.closeResource(outputStream);
            if(requestId!=null)
            	registry.deregisterFileProcessThread(requestId);
        }
    }
    
}