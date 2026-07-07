package com.helicalinsight.scheduling;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.HCRException;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.CSVUtility;
import com.helicalinsight.export.ReportsProcessor;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.CloseableHttpResponse;
import org.apache.http.client.config.RequestConfig;

import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * EmailUtility
 * An utility class for the Emailing component of the application, which takes
 * care of getting the attachments generated in the Temp directory of the System
 * directory
 *
 * @author Rajasekhar
 * @since 1.1
 */
public class EmailUtility {

    private static final Logger logger = LoggerFactory.getLogger(EmailUtility.class);

    /**
     * getAttachmentsArray(String[] formats, String reportSource, String reportSourceType,
     String reportName, String parameterData, JsonObject printOptions)
     * Returns an array of the locations of the attachments. reportsSourceType
     * 'adhoc' means that the report is to be processed expecting that the
     * reportSource is html data. Otherwise the reportSource is url. The class
     * uses the phantom js related api to get the attachments.
     * <p>
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
                                               String reportName, String parameterData, JsonObject printOptions) {
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
            printOptions.add("format", new Gson().fromJson(new Gson().toJson(formats), JsonArray.class));
            locations.addAll(reportsProcessor.generateReportFromURI(reportSource, null, reportName, printOptions));
            attachments = locations.toArray(attachments);
        }
        return attachments;
    }
    /**
     * insertCsvAttachment(String reportName, String parameterData, String[] attachments,
     int arrayIndex)
     * Return the incremented counter after placing the attachment location in the attachments
     * @param reportName				name of the report from the request
     * @param parameterData				data related to csv
     * @param attachments				The email attachment formats
     * @param arrayIndex                index of string array
     * @return count of email attachment formats
     */
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
    /**
     * makeHttpCall(JsonObject formData, List<String> resourceUrl)
     * Makes an HTTP call to retrieve data and perform actions related to email processing.
     * @param formData           JSON object containing the form data
     * @param resourceUrl        urls to retrieve information form an Email related.like mock url and url for downloading a report
     * @return HTTP response data in string format.
     * @throws HCRException If an error occurs during the HTTP call.
     */
    public static String makeHttpCall(JsonObject formData, List<String> resourceUrl) {
        try {
            return OkHttpSessionExample.makeCall(resourceUrl, formData);
        }catch(Exception e){
            logger.error("Exception occured in http call ",e);
            return  null;
        }
    }
    public static String makeHttpCallOld(JsonObject formData, List<String> resourceUrl) {


        //For login
        HttpGet httpGets = new HttpGet(resourceUrl.get(0));//mock url
        HttpGet httpGet = new HttpGet(resourceUrl.get(1).replace("//mock","/mock"));//mock url
        String response = "";
        int connectionTimeout = 5000;
        int readTimeout = 10000;       // 10 seconds

        // Create RequestConfig with timeouts
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setSocketTimeout(readTimeout)
                .build();

        // CookieStore to store session cookies
        CookieStore cookieStore = new BasicCookieStore();

        // Create HttpClient with CookieStore to manage sessions
        try (CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
                .build()) {


            HttpResponse execute2 = client.execute(httpGets);
            HttpResponse execute = client.execute(httpGet);


            HttpPost postRequest = new HttpPost(resourceUrl.get(2).replace("//export","/downloadReport"));

            List<NameValuePair> postParameters = new ArrayList<>();

            postParameters.add(new BasicNameValuePair("type", "hcr"));
            postParameters.add(new BasicNameValuePair("serviceType", "report"));
            postParameters.add(new BasicNameValuePair("service", "generateReport"));
            postParameters.add(new BasicNameValuePair("formData", formData.toString()));

            postRequest.setEntity(new UrlEncodedFormEntity(postParameters));
            HttpResponse httpResponse = client.execute(postRequest);
            HttpEntity responseEntity = httpResponse.getEntity();
            response = EntityUtils.toString(responseEntity);
        } catch (IOException e) {
            throw new HCRException("error occurred during scheduling the hcr report..." + e.getMessage());
        }
        return response;
    }
    /**
     * doLogout(String baseUrl)
     * Performs a logout action using the provided base URL.
     * @param baseUrl    url for logout
     */
    public static void doLogout(String baseUrl){
        try {
            String logoutUrl = baseUrl.replace("hi.html", "logout");
            HttpURLConnection connection = (HttpURLConnection) new URL(logoutUrl).openConnection();
        } catch (IOException e) {
            logger.error("Error while logging out ",e);
        }
    }
    /**
     * makeBinaryHttpCall(JsonObject formData, List<String> resourceUrl)
     * Makes an HTTP call to retrieve binary data and perform actions using the provided form data.
     * @param formData					 JSON object containing the form data
     * @param resourceUrl				 urls to retrieve information form an Email related.
     * @return HTTP response data in string format
     * @throws OperationFailedException If an error occurs during the HTTP call.
     */
    public static String makeBinaryHttpCall(JsonObject formData, List<String> resourceUrl) {
        try {
            return OkHttpSessionExample.makeBinaryHttpCall(formData, resourceUrl);
        }catch(Exception e){
            logger.error("Exception occured in http call ", e);
            return  null;
        }
    }
    public static String makeBinaryHttpCallOld(JsonObject formData, List<String> resourceUrl) {



        HttpGet httpGet1 = new HttpGet(resourceUrl.get(0));//mock url
        HttpGet httpGet = new HttpGet(resourceUrl.get(1).replace("//mock","/mock"));//mock url

        String response = "";
        int connectionTimeout = 5000;
        int readTimeout = 10000;       // 10 seconds

        // Create RequestConfig with timeouts
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setSocketTimeout(readTimeout)
                .build();

        // CookieStore to store session cookies
        CookieStore cookieStore = new BasicCookieStore();

        // Create HttpClient with CookieStore to manage sessions
        try (CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
                .build()) {

            HttpResponse execute1 = client.execute(httpGet1);
            HttpResponse execute = client.execute(httpGet);
            HttpPost postRequest = new HttpPost(resourceUrl.get(2).replace("//export","/downloadReport"));
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("data", formData.toString()));
            postParameters.add(new BasicNameValuePair("sendFileName", "true"));
            postRequest.setEntity(new UrlEncodedFormEntity(postParameters));
            HttpResponse httpResponse = client.execute(postRequest);
            HttpEntity responseEntity = httpResponse.getEntity();
            response = EntityUtils.toString(responseEntity);
        } catch (IOException e) {
            throw new OperationFailedException("error occurred during scheduling the hcr report..." + e.getMessage());
        }
        return response;
    }
}
