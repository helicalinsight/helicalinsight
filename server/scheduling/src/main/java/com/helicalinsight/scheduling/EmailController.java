package com.helicalinsight.scheduling;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.adhoc.jreport.HCRHelper;
import com.helicalinsight.adhoc.jreport.IHCRGenerator;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EmailException;
import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationSettings;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.ReportsUtility;
import com.helicalinsight.efw.utility.ResponseUtils;
import com.helicalinsight.efw.utility.SendMail;
import com.helicalinsight.export.ExportUtils;
import com.helicalinsight.export.PrepareFormDataForAdhocDownload;
import com.helicalinsight.export.ServerSideExport;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;
import com.helicalinsight.scheduling.utils.TemplateReplacer;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

/**
 * EmailController
 * The controller responsible for the email service of the application. Has a
 * method with request mapping /sendMail.
 *
 * @author Rajasekhar
 * @since 1.1
 */
@Controller
@Component
public class EmailController {

    private final static Logger logger = LoggerFactory.getLogger(EmailController.class);

    private static final String RESOURCE_TYPE_ADHOC = "adhoc";
    private static final String RESOURCE_TYPE_URL = "url";
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    @Autowired
    PrepareFormDataForAdhocDownload formDataHelper;

    /**
     * sendMail(@RequestParam("formats") String formats, @RequestParam("recipients") String recipients,
     HttpServletRequest request, HttpServletResponse response)
     * Sends email with the attachments in the requested formats to the desired
     * recipients. The request parameters 'body' of the email, 'subject' of the
     * email and the 'data' for csv attachment are optional; If provided they
     * will be used.
     *
     * @param formats    The request parameter - formats of the attachments of the
     *                   dashboard
     * @param recipients The request parameter - recipients of the email
     * @param request    provides report name , cookies and value of the specified request header
     * @param response   sets content type
     */
    @RequestMapping(value = "/sendMail", method = RequestMethod.POST)
    public void sendMail(@RequestParam("formats") String formats, @RequestParam("recipients") String recipients,
                         HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);
        JsonObject printOptions = new JsonObject();
        String reportParam = request.getParameter("reportParameters");
        JsonArray cookiesArray = ControllerUtils.newGetCookieArray(request);
        printOptions.add("cookie", cookiesArray);
        reportParam = ExportUtils.setPrintOptionsAndDiscardFromReportParameters(reportParam, printOptions);
        JSONObject nJs = JSONObject.fromObject(reportParam);
        String rf=request.getParameter("reportFile");
        String dr=request.getParameter("dir");
        String rn=request.getParameter("reportName");
        int lastSpaceIndex = rn.lastIndexOf(" ");
        if (lastSpaceIndex != -1) {
            rn = rn.substring(0, lastSpaceIndex);
        }
        String rUrl=dr+"/"+rf;
        String rPath=dr+"/"+rn;
        nJs.put("reportPath", rPath);
        nJs.put("reportUrl", rUrl);
        nJs.put("reportFile", rf);
        nJs.put("reportFileName", rf);
        nJs.put("reportNameWithExtension", rf);

        ApplicationSettings applicationSettings = ApplicationContextAccessor.getBean(ApplicationSettings.class);
        String defaultEmailResourceType = applicationSettings.getDefaultEmailResourceType();

        //If the email resource type is neither adhoc or url it is unknown request
        if (!RESOURCE_TYPE_ADHOC.equalsIgnoreCase(defaultEmailResourceType) && !RESOURCE_TYPE_URL.equalsIgnoreCase
                (defaultEmailResourceType)) {
            throw new EmailException("The email resource type is unknown. Expecting is either adhoc or url");
        }

        String reportName = getReportName(request);
        String reportNames = getReportName(request);
         lastSpaceIndex = reportNames.lastIndexOf(" ");
        if (lastSpaceIndex != -1) {
            reportNames = reportNames.substring(0, lastSpaceIndex);
        }
        nJs.put("reportName",reportNames);

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

            List<String> formatList = new ArrayList<String>(Arrays.asList(totalFormats));
            if (logger.isInfoEnabled()) {
                logger.debug("The email formats are " + formatList);
                logger.info((((totalFormats.length == 0) || ("[\"\"]".equals(formats))) ? "Not preparing " :
                        "Preparing") + " attachments for the mail.");
            }

            // Get subject from the request parameter if present
            String subject = getSubject(request, propertiesMap, reportName);

            SendMail mailClient = new SendMail();
            String[] attachments;
            String hcrReportFile = null;
            hcrReportFile = request.getParameter("reportFile");
            String hcrDirectory = request.getParameter("dir");
            String extension = FileUtils.getExtension(hcrReportFile);
            String hcrExtension = JsonUtils.getHCRExtension();


            //efwsr with hcr
            if (extension.equals(JsonUtils.getEFWSRExtension())) {
                try {
                    File theFile = new File(ApplicationProperties.getInstance().getSolutionDirectory() + File.separator +
                            hcrDirectory + File.separator + hcrReportFile);
                    Efwsr efwsr = JaxbUtils.unMarshal(Efwsr.class, theFile);
                    String reportFile = efwsr.getReportFile();
                    String efwsrActualExtension = FileUtils.getExtension(reportFile);
                    if (efwsrActualExtension.equals(hcrExtension)) {
                        extension = hcrExtension;
                        String reportParameters = request.getParameter("reportParameters");

                        if (StringUtils.isBlank(reportParameters)|| reportParameters.equals("{}")) {
                            reportParam = efwsr.getReportParameters();
                        } else {
                            reportParam = reportParameters;
                        }
                        hcrReportFile = reportFile;
                        hcrDirectory = efwsr.getReportDirectory();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //efwsr with hcr

            if (extension.equals(hcrExtension)) {
                //String json = new Gson().toJson(reportParam);
                nJs.put("reportName",rn);
                nJs.put("reportDir",hcrDirectory);
                JsonObject reportJson = GsonUtility.parseString(reportParam, JsonObject.class);
                JsonObject hcrJsonData = HCRHelper.prepareFormDataForHCRExportReport(hcrReportFile, hcrDirectory, totalFormats);
                JsonObject formData = HCRHelper.prepareFormDataForHCRParameters(reportJson, hcrJsonData);
                formData.addProperty("emailExportName", reportName);
                IHCRGenerator generator = (IHCRGenerator) ApplicationContextAccessor.getBean(JsonUtils.getHCRGeneratorType());
                JsonObject responseJson = generator.generateHCReport(formData);
                JsonObject jrxmlData = responseJson.getAsJsonObject("jrxmlData");
                JsonArray exportedFiles = jrxmlData.getAsJsonArray("exportedFiles");
                List<String> listOfFiles = new ArrayList<>();
                for (int index = 0; index < exportedFiles.size(); index++) {
                    listOfFiles.add(exportedFiles.get(index).getAsString());
                }
                String[] attachmentsArray = listOfFiles.toArray(new String[0]);

                body = TemplateReplacer.replaceEmailComponents(nJs.toString(),body,true);
                subject=TemplateReplacer.replaceEmailComponents(nJs.toString(),subject,true);
                String [] newTotalRec = new String[totalRecipients.length];
                for(int i=0; i<totalRecipients.length; i++){
                    String s = totalRecipients[i];
                    s=TemplateReplacer.replaceEmailComponents(nJs.toString(),s,true);
                    newTotalRec[i]=s.replaceAll("'","");
                }


                send(hostName, port, from, isAuthenticated, isSSLEnabled, user, password, body, newTotalRec,
                        subject, mailClient, attachmentsArray);
                String result = ResponseUtils.createJsonResponse("Email sent successfully.");
                ControllerUtils.handleSuccess(response, isAjax, result);
                return;
            } else if ((totalFormats.length == 0) || ("[\"\"]".equals(formats))) {
                //Without attachments
                try {
                    mailClient.sendMessage(hostName, reportName, totalRecipients, from, isAuthenticated,
                            isSSLEnabled, user, password, subject, body);
                } catch (MessagingException ex) {
                    throw new EmailException("Couldn't send email to the recipient");
                }
            } else {


                String reportParameters = null;
                String reportType = null;
                String dir = null;
                String reportFile = null;
                List<String> locations = new ArrayList<>();
                attachments = new String[totalFormats.length];


                reportParameters = request.getParameter("reportParameters");
                reportType = request.getParameter("reportType");
                dir = request.getParameter("dir");
                reportFile = request.getParameter("reportFile");
                nJs=JSONObject.fromObject(reportParameters);
                nJs.put("reportName",reportName);
                nJs.put("reportType",reportType);
                nJs.put("reportDir",dir);
                nJs.put("reportFile",reportFile);
                nJs.put("reportFileName",reportFile);
                nJs.put("reportNameWithExtension",reportFile);
                nJs.put("reportPath",dir+"/"+reportName);
                nJs.put("reportUrl",dir+"/"+reportFile);

                validate(formats, recipients, reportParameters, reportType, dir, reportFile);

                String adhocFormData = request.getParameter("adhocFormData");
                if (adhocFormData != null) {
                    if (formatList.contains("csv")) {
                        formatList.remove("csv");
                        locations.add(formDataHelper.prepareFormData(new Gson().fromJson(adhocFormData, JsonObject.class), "csv", request.getParameter("reportName")));
                    }
                    if (formatList.contains("xls")||formatList.contains("xlsx")) {
                        formatList.remove("xls");
                        formatList.remove("xlsx");
                        locations.add(formDataHelper.prepareFormData(new Gson().fromJson(adhocFormData, JsonObject.class), "xlsx", request.getParameter("reportName")));
                    }
                }
                if (!formatList.isEmpty()) {
                    printOptions.addProperty("format", formatList.toString());
                    ServerSideExport serverSideExport = new ServerSideExport(null, reportName,
                            reportParameters, reportType, dir, reportFile, printOptions);
                    locations.addAll(serverSideExport.listOfLocations());
                }
                //Handle csv as well as xls

                attachments = locations.toArray(attachments);
body = TemplateReplacer.replaceEmailComponents(nJs.toString(),body,true);
subject=TemplateReplacer.replaceEmailComponents(nJs.toString(),subject,true);
String [] newTotalRec = new String[totalRecipients.length];
for(int i=0; i<totalRecipients.length; i++){
    String s = totalRecipients[i];
    s=TemplateReplacer.replaceEmailComponents(nJs.toString(),s,true);
    newTotalRec[i]=s.replaceAll("'","");
}

                //Since the attachments are prepared now, send the email
                send(hostName, port, from, isAuthenticated, isSSLEnabled, user, password, body, newTotalRec,
                        subject, mailClient, attachments);

            }

            String result = ResponseUtils.createJsonResponse("Email sent successfully.");
            ControllerUtils.handleSuccess(response, isAjax, result);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }
    /**
     * getReportName(HttpServletRequest request)
     * reportName from the request parameter if present; else use epoch time
     * @param request     reportName
     * @return reportName
     */
    private String getReportName(HttpServletRequest request) {
        // Get reportName from the request parameter if present; else use epoch time
        return ReportsUtility.getReportName(request.getParameter("reportName"));
    }
    /**
     * getBody(HttpServletRequest request, Map<String, String> propertiesMap)
     * @param request             provides email body
     * @param propertiesMap       if email body is not pressent usees default body from map
     * @return email body. if body present otherwise default body.
     */
    private String getBody(HttpServletRequest request, Map<String, String> propertiesMap) {
        String bodyParameter = request.getParameter("body");
        if (StringUtils.isBlank(bodyParameter)) {
            logger.debug("Email Body is not provided. Using default body for the email");
            return propertiesMap.get("body");
        } else {
            return bodyParameter;
        }
    }
    /**
     * convertToArray(String formats)
     * @param formats       formats of the attachments of the dashboard
     * @return string array
     */
    private String[] convertToArray(String formats) {
        try {
            //JSONArray formatsArray = JSONArray.fromObject(formats);
            JsonArray formatsArray = new Gson().fromJson(formats, JsonArray.class);
//            String totalFormats[] = new String[formatsArray.size()];
//            totalFormats = (String[]) formatsArray.toArray(totalFormats);
            String totalFormats[] = new Gson().fromJson(formatsArray, String[].class);
            return totalFormats;
        } catch (JsonSyntaxException ex) {
            logger.error("JsonException : " + ex);
            throw new EmailException("Attachment formats is in unsupported format. Failed to send mail.");
        }

    }
    /**
     * getSubject(HttpServletRequest request, Map<String, String> propertiesMap, String reportName)
     * @param request               to provide subject
     * @param propertiesMap         it provides subject in case request will not provides
     * @param reportName			HReport Name
     * @return subject of email.
     */
    private String getSubject(HttpServletRequest request, Map<String, String> propertiesMap, String reportName) {
        String subjectParameter = request.getParameter("subject");
        if (StringUtils.isBlank(subjectParameter)) {
            logger.debug("Subject is not provided. Using default subject from properties file.");
            return propertiesMap.get("subject") + reportName;
        } else {
            return subjectParameter;
        }
    }
    /**
     * send(String hostName, String port, String from, String isAuthenticated, String isSSLEnabled,
     String user, String password, String body, String[] totalRecipients, String subject,
     SendMail mailClient, String[] attachments)
     * This method can send emails with attachments.
     * @param hostName					  specifies SMTP host name
     * @param port						  specifies SMTP port number
     * @param from						  specifies the sender
     * @param isAuthenticated			  specifies value true or false
     * @param isSSLEnabled				  specifies value true or false
     * @param user						  user who is sending mail
     * @param password					  password of the user
     * @param body						  body of mail
     * @param totalRecipients			  array which specifies recipients
     * @param subject					  subject of mail
     * @param mailClient                  object of SendMail class
     * @param attachments				  attachments which has to be send with mail
     */
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
    /**
     *
     * @param formats                    formats of the attachments of the dashboard
     * @param recipients			     email id of recipients
     * @param request					 gives type adhoc or not adhoc and  html source of the report or the url
     * @param reportName				 report name
     * @param totalFormats				 email attachment formats 
     * @param csvData                    CSV (Comma-Separated Values) is a simple and widely used format for storing tabular data.
     *                                   Each line of a CSV file represents a record, and the values within a record are separated by commas. 
     * @param printOptions				 object
     * @return
     */
    private String[] getAttachments(String formats, String recipients, HttpServletRequest request, String reportName,
                                    String[] totalFormats, String csvData, JsonObject printOptions) {
        String[] attachments;
        String reportSource = request.getParameter("reportSource");
        String reportSourceType = request.getParameter("reportSourceType");
        validateFormData(formats, recipients, reportSource, reportSourceType);

        //For other types of attachments along with the csv
        attachments = EmailUtility.getAttachmentsArray(totalFormats, reportSource, reportSourceType, reportName,
                csvData, printOptions);
        return attachments;
    }
    /**
     * validate(String formats, String recipients, String reportParameters, String reportType, String dir,
     String reportFile)
     * adds all parameter into hashMap.                    
     * checks all parameters are null or empty strings.  If an empty or null parameter is
     * found then RequiredParameterIsNullException is thrown with the the message including the
     * faulty parameter.                  
     * @param formats						formats of the attachments of the dashboard
     * @param recipients					email id of recipients
     * @param reportParameters				report parametes 
     * @param reportType                    report type
     * @param dir							directory of report file
     * @param reportFile					report file 
     */
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
    /**
     * alidateFormData(String formats, String recipients, String reportSource, String reportSourceType) 
     * checks all parameters are null or empty strings. If an empty or null parameter is
     * found then RequiredParameterIsNullException is thrown with the the message including the
     * faulty parameter.
     * @param formats					formats of the attachments of the dashboard
     * @param recipients				email id of recipients
     * @param reportSource				html source of the report or the url
     * @param reportSourceType			type adhoc or not adhoc
     */
    private void validateFormData(String formats, String recipients, String reportSource, String reportSourceType) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("formats", formats);
        parameters.put("recipients", recipients);
        parameters.put("reportSource", reportSource);
        parameters.put("reportSourceType", reportSourceType);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
        check(formats, recipients);
    }
    /**
     * check(String formats, String recipients)
     * throws exception if parameters are empty
     * @param formats				formats of the attachments of the dashboard
     * @param recipients			email id of recipients
     * @throws FormValidationException
     */
    private void check(String formats, String recipients) {
        if ("[]".equals(formats)) {
            throw new FormValidationException("Formats is empty.Please select any one format.");
        }
        if ("[]".equals(recipients)) {
            throw new FormValidationException("Please type the recipients email id");
        }
    }
}