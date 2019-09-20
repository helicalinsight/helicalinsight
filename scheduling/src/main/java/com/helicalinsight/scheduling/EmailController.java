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

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EmailException;
import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.*;
import com.helicalinsight.export.ExportUtils;
import com.helicalinsight.export.ServerSideExport;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The controller responsible for the email service of the application. Has a
 * method with request mapping /sendMail.
 *
 * @author Rajasekhar
 */
@Controller
@Component
public class EmailController {

    private final static Logger logger = LoggerFactory.getLogger(EmailController.class);

    private static final String RESOURCE_TYPE_ADHOC = "adhoc";
    private static final String RESOURCE_TYPE_URL = "url";

    /**
     * Sends email with the attachments in the requested formats to the desired
     * recipients. The request parameters 'body' of the email, 'subject' of the
     * email and the 'data' for csv attachment are optional; If provided they
     * will be used.
     *
     * @param formats    The request parameter - formats of the attachments of the
     *                   dashboard
     * @param recipients The request parameter - recipients of the email
     * @param request    HttpServletRequest object
     */
    @RequestMapping(value = "/sendMail", method = RequestMethod.POST)
    public void sendMail(@RequestParam("formats") String formats, @RequestParam("recipients") String recipients,
                         HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);
        JSONObject printOptions = new JSONObject();
        String reportParam = request.getParameter("reportParameters");
        reportParam = ExportUtils.setPrintOptionsAndDiscardFromReportParameters(reportParam, printOptions);

        ApplicationSettings applicationSettings = ApplicationContextAccessor.getBean(ApplicationSettings.class);
        String defaultEmailResourceType = applicationSettings.getDefaultEmailResourceType();

        //If the email resource type is neither adhoc or url it is unknown request
        if (!RESOURCE_TYPE_ADHOC.equalsIgnoreCase(defaultEmailResourceType) && !RESOURCE_TYPE_URL.equalsIgnoreCase
                (defaultEmailResourceType)) {
            throw new EmailException("The email resource type is unknown. Expecting is either adhoc or url");
        }

        String reportName = getReportName(request);

        try {
            PropertiesFileReader propertiesFileReader = new PropertiesFileReader();

            Map<String, String> propertiesMap = propertiesFileReader.read("Mail", "mailConfiguration.properties");

            Assert.notNull(propertiesMap, "The mailConfiguration.properties file in System/Mail directory is " +
                    "empty!!");

            String hostName = propertiesMap.get("hostName");
            String port = propertiesMap.get("port");
            String from = propertiesMap.get("from");
            String isAuthenticated = propertiesMap.get("isAuthenticated");
            String isSSLEnabled = propertiesMap.get("isSSLEnabled");
            String user = propertiesMap.get("user");
            String password = propertiesMap.get("password");

            // Get body from the request parameter if present
            String body = getBody(request, propertiesMap);

            String[] totalFormats = convertToArray(formats);
            String[] totalRecipients = convertToArray(recipients);

            if (logger.isInfoEnabled()) {
                logger.debug("The email formats are " + Arrays.asList(totalFormats));
                logger.info((((totalFormats.length == 0) || ("[\"\"]".equals(formats))) ? "Not preparing " :
                        "Preparing") + " attachments for the mail.");
            }

            // Get subject from the request parameter if present
            String subject = getSubject(request, propertiesMap, reportName);

            SendMail mailClient = new SendMail();
            String[] attachments;

            if ((totalFormats.length == 0) || ("[\"\"]".equals(formats))) {
                //Without attachments
                try {
                    mailClient.sendMessage(hostName, reportName, totalRecipients, from, isAuthenticated,
                            isSSLEnabled, user, password, subject, body);
                } catch (MessagingException ex) {
                    throw new EmailException("Couldn't send email to the recipient");
                }
            } else {
                // Send mail to all the recipients with all the attachments
                String csvData = request.getParameter("data");
                //If only csv is the attachment to be attached
                if ((totalFormats.length == 1) && ("csv".equalsIgnoreCase(totalFormats[0]))) {
                    if (csvData == null) {
                        throw new EmailException("Couldn't process request for csv attachment. Failed to send " +
                                "email message.");
                    }

                    attachments = new String[1];
                    //Attachments array consists of the location of the csv file
                    EmailUtility.insertCsvAttachment(reportName, csvData, attachments, 0);
                    send(hostName, port, from, isAuthenticated, isSSLEnabled, user, password, body, totalRecipients,
                            subject, mailClient, attachments);
                } else {
                    String reportSourceType = request.getParameter("reportSourceType");
                    if (reportSourceType == null || "".equalsIgnoreCase(reportSourceType.trim())) {
                        throw new IncompleteFormDataException("Parameter reportSourceType is null or empty");
                    }

                    if (!reportSourceType.equalsIgnoreCase(RESOURCE_TYPE_URL)) {
                        //Do the old way. Handles csv and phantom supported formats
                        attachments = getAttachments(formats, recipients, request, reportName, totalFormats, csvData,
                                printOptions);
                    } else {
                        //Handle csv as well
                        String reportParameters = null;
                        String reportType = null;
                        String dir = null;
                        String reportFile = null;
                        List<String> formatList = Arrays.asList(totalFormats);
                        List<String> locations = new ArrayList<>();
                        attachments = new String[totalFormats.length];
                        int counter = 0;

                        if (formatList.contains("csv")) {
                            //Inserts the location of the csv file in attachments at index counter
                            EmailUtility.insertCsvAttachment(reportName, csvData, attachments, counter);
                            locations.add(TempDirectoryCleaner.getTempDirectory() + File.separator +
                                    reportName + ".csv");
                        } else {
                            reportParameters = request.getParameter("reportParameters");
                            reportType = request.getParameter("reportType");
                            dir = request.getParameter("dir");
                            reportFile = request.getParameter("reportFile");

                            validate(formats, recipients, reportParameters, reportType, dir, reportFile);

                            printOptions.put("format", JSONArray.fromObject(formats));
                            ServerSideExport serverSideExport = new ServerSideExport(null, reportName,
                                    reportParameters, reportType, dir, reportFile, printOptions);
                            locations.addAll(serverSideExport.listOfLocations());
                            attachments = locations.toArray(attachments);
                        }
                    }

                    //Since the attachments are prepared now, send the email
                    send(hostName, port, from, isAuthenticated, isSSLEnabled, user, password, body, totalRecipients,
                            subject, mailClient, attachments);
                }
            }

            String result = ResponseUtils.createJsonResponse("Email sent successfully.");
            ControllerUtils.handleSuccess(response, isAjax, result);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    private String getReportName(HttpServletRequest request) {
        // Get reportName from the request parameter if present; else use epoch time
        return ReportsUtility.getReportName(request.getParameter("reportName"));
    }

    private String getBody(HttpServletRequest request, Map<String, String> propertiesMap) {
        String bodyParameter = request.getParameter("body");
        if (bodyParameter == null) {
            logger.debug("Email Body is not provided. Using default body for the email");
            return propertiesMap.get("body");
        } else {
            return bodyParameter;
        }
    }

    private String[] convertToArray(String formats) {
        try {
            JSONArray formatsArray = JSONArray.fromObject(formats);
            String totalFormats[] = new String[formatsArray.size()];
            totalFormats = (String[]) formatsArray.toArray(totalFormats);
            return totalFormats;
        } catch (JSONException ex) {
            logger.error("JSONException : " + ex);
            throw new EmailException("Attachment formats is in unsupported format. Failed to send mail.");
        }

    }

    private String getSubject(HttpServletRequest request, Map<String, String> propertiesMap, String reportName) {
        String subjectParameter = request.getParameter("subject");
        if (subjectParameter == null) {
            logger.debug("Subject is not provided. Using default subject from properties file.");
            return propertiesMap.get("subject") + reportName;
        } else {
            return subjectParameter;
        }
    }

    private void send(String hostName, String port, String from, String isAuthenticated, String isSSLEnabled,
                      String user, String password, String body, String[] totalRecipients, String subject,
                      SendMail mailClient, String[] attachments) {
        try {
            mailClient.sendMessage(hostName, port, totalRecipients, from, isAuthenticated, isSSLEnabled, user,
                    password, subject, body, attachments);
        } catch (AddressException ex) {
            throw new EmailException("Please check recipients address. Couldn't send email to " + "the recipient.");
        } catch (MessagingException ex) {
            throw new EmailException("Couldn't send email to the recipient");
        } catch (IOException ex) {
            throw new EmailException("Attachment(s) not found. Couldn't send email to the recipient.");
        }
    }

    private String[] getAttachments(String formats, String recipients, HttpServletRequest request, String reportName,
                                    String[] totalFormats, String csvData, JSONObject printOptions) {
        String[] attachments;
        String reportSource = request.getParameter("reportSource");
        String reportSourceType = request.getParameter("reportSourceType");
        validateFormData(formats, recipients, reportSource, reportSourceType);

        //For other types of attachments along with the csv
        attachments = EmailUtility.getAttachmentsArray(totalFormats, reportSource, reportSourceType, reportName,
                csvData, printOptions);
        return attachments;
    }

    private void validate(String formats, String recipients, String reportParameters, String reportType, String dir,
                          String reportFile) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("formats", formats);
        parameters.put("recipients", recipients);
        parameters.put("reportParameters", reportParameters);
        parameters.put("dir", dir);
        parameters.put("reportFile", reportFile);
        parameters.put("reportType", reportType);

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
        check(formats, recipients);
    }

    private void validateFormData(String formats, String recipients, String reportSource, String reportSourceType) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("formats", formats);
        parameters.put("recipients", recipients);
        parameters.put("reportSource", reportSource);
        parameters.put("reportSourceType", reportSourceType);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
        check(formats, recipients);
    }

    private void check(String formats, String recipients) {
        if ("[]".equals(formats)) {
            throw new FormValidationException("Formats is empty.Please select any one format.");
        }
        if ("[]".equals(recipients)) {
            throw new FormValidationException("Please type the recipients email id");
        }
    }
}