package com.helicalinsight.scheduling;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.utils.ApplicationDefaultUserAndRoleNamesConfigurer;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.ReportsUtility;
import com.helicalinsight.efw.utility.SendMail;
import com.helicalinsight.export.ExportUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static com.helicalinsight.efw.utility.JsonUtils.safeGetJsonObject;

/**
 * HttpScheduleJob class implements {@link Job} interface and custom {@link ISchedule} interface.
 * This class is responsible to execute the scheduled job process for http based mocking
 * 
 * @author Somen
 * @see ScheduleProcessCall
 */
public class HttpScheduleJob implements Job, ISchedule {

    private static final Logger logger = LoggerFactory.getLogger(HttpScheduleJob.class);

    /**
     * execute(JobExecutionContext context)
     * Executes the scheduled job process and fetches data from context.
     * @param context       provides information about schedule
     */
    @Override
    public void execute(JobExecutionContext context) {


        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
       // JSONObject json = JSONObject.fromObject(dataMap.get("jsonobject"));
        JsonObject json = safeGetJsonObject(dataMap.get("jsonobject"));
        //new Gson().fromJson((JsonObject)dataMap.get("jsonobject"),JsonObject.class);
        final JsonObject schedulingJob = json.getAsJsonObject("SchedulingJob");
        String reportEfwFile = schedulingJob.get("reportFile").getAsString();
        String reportDirectory = schedulingJob.get("reportDirectory").getAsString();

        JsonObject parametersJSON = schedulingJob.getAsJsonObject("reportParameters");
        JsonObject printOptions = optJsonObject(parametersJSON,"printOptions");
        if (printOptions == null) {
            printOptions = new JsonObject();
        }

        ExportUtils.setPrintOptionsAndDiscardFromReportParameters(parametersJSON.toString(), printOptions);

        parametersJSON.remove("printOptions");
        String reportCsvParameter = null;


        /*
         * check reportParameter contains csv data or not
		 */
        int jobId = dataMap.getInt("jobinput");
        String path = dataMap.getString("path");


        XmlOperation xmlOperation = new XmlOperation();
        JsonObject jsonObjectCsvData = xmlOperation.getParticularObject(path, String.valueOf(jobId));
        if (jsonObjectCsvData.getAsJsonObject("SchedulingJob").getAsJsonObject("reportParameters").has("csvdata")) {
            reportCsvParameter = jsonObjectCsvData.getAsJsonObject("SchedulingJob").getAsJsonObject("reportParameters")
                    .get("csvdata").getAsString();
        }

        JsonObject emailSettings = schedulingJob.getAsJsonObject("EmailSettings");
        String recipients = emailSettings.get("Recipients").getAsString();


        String[] totalRecipients = recipients.substring(1, recipients.length() - 1).replace("\"", "").split(",");

        String subject = getSubjectLine(json, emailSettings);


        // Read the properties file in the EFW/System/Mail directory
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> propertiesMap = propertiesFileReader.read("Mail", "mailConfiguration" + ".properties");
        Assert.notNull(propertiesMap, "The mailConfiguration.properties map is null!!");

        String body = getBodyLine(emailSettings, propertiesMap);

        String csvData = reportCsvParameter;

        String baseUrl = dataMap.getString("baseUrl");
        Map<String, String> realNames = AuthenticationUtils.getRealNames(json);


        //FIXED a bug for phantom js login. Getting the user name, organization based on the
        //user id from the xml.

        String parameters = ControllerUtils.concatenateParameters(parametersJSON);
        /*Fixed a bug - index out of bounds*/
        if (parameters.length() > 0) {
            parameters = parameters.substring(0, parameters.length() - 1);
        }


        String reportUrl = getReportUrl(reportEfwFile, reportDirectory, baseUrl, parameters, realNames);

        String encodedUrl = "";
        try {
            encodedUrl = URLEncoder.encode(reportUrl, ApplicationUtilities.getEncoding());
        } catch (UnsupportedEncodingException ignore) {

        }
        String reportName = ReportsUtility.getReportName(json.get("JobName".trim()).getAsString());
        String reportSourceType = "url";
        String formats = emailSettings.get("Formats").getAsString();
        String[] theTotalFormats = formats.substring(1, formats.length() - 1).replace("\"", "").split(",");

        sessionStart(realNames, printOptions, csvData, encodedUrl, reportName, reportSourceType, theTotalFormats, parametersJSON, totalRecipients, subject, propertiesMap, body);


        updateXmlFile(context, json, jobId, path, xmlOperation);
    }
    /**
     * optJsonObject(JsonObject jsonObject, String key)
     * This method checks key present in the jsonObject or not.
     * if it is present it returns JsonObject ,otherwise null.
     * @param jsonObject 		
     * @param key 				key of the JsonObject.
     * @return jsonObject		
     */
     
    private static JsonObject optJsonObject(JsonObject jsonObject, String key) {
		if (jsonObject != null) {
			JsonElement jsonElement = jsonObject.get(key);
			if (jsonElement != null && jsonElement.isJsonObject()) {
				return jsonElement.getAsJsonObject();
			}
		}
		return null;
	}
    /**
     * sessionStart(Map<String, String> realNames, JsonObject printOptions, String csvData, String encodedUrl, 
           String reportName, String reportSourceType, String[] theTotalFormats, JsonObject parametersJSON,
           String[] totalRecipients, String subject, Map<String, String> propertiesMap, String body) 
     * Initiates the HTTP session for executing the scheduled job process.    
     * @param realNames                provides username, password, organization
     * @param printOptions			   to add cookies
     * @param csvData				   data related to csv
     * @param encodedUrl			   html resource url or url
     * @param reportName			   report name
     * @param reportSourceType		   'adhoc' or not 'adhoc'
     * @param theTotalFormats		   email attachment formats
     * @param parametersJSON           json object to add printOption properties
     * @param totalRecipients		   array which specifies recipients
     * @param subject				   email subject
     * @param propertiesMap			   provides data required for Java Mail API.
     * @param body					   body of mail
     */
	protected void sessionStart(Map<String, String> realNames, JsonObject printOptions, String csvData, String encodedUrl, String reportName, String reportSourceType, String[] theTotalFormats, JsonObject parametersJSON, String[] totalRecipients, String subject, Map<String, String> propertiesMap, String body) {
        String baseUrl = ApplicationProperties.getInstance().getDomain();
        baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/") + 1);


        InputStream response = null;
        try {

            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cookieManager);

            String firstLogin = getFirstLogin(baseUrl);
            String finalUrl = getFinalUrl(realNames, baseUrl);
            String logoutUrl = getLogOutUrl(baseUrl);

            URL url = new URL(firstLogin);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                logger.error("Cannot login to the system as download manager. returning");
                return;
            }


            url = new URL(finalUrl);
            url.openConnection();

            connection = (HttpURLConnection) url.openConnection();
            response = connection.getInputStream();

            CookieStore cookieJar = cookieManager.getCookieStore();
            List<HttpCookie> cookies = cookieJar.getCookies();

            JsonArray cookieArray = new JsonArray();
            for (HttpCookie hc : cookies) {
                JsonObject cookieData = new JsonObject();
                cookieData.addProperty(hc.getName(), hc.getValue());
                cookieArray.add(cookieData);
            }
            try (Scanner scanner = new Scanner(response)) {
            	 String responseBody = scanner.useDelimiter("\\A").next();
                logger.info(responseBody);
            }

            //handle export


            printOptions.add("cookie", cookieArray);
            String[] attachments = getAttachements(printOptions, csvData, realNames, encodedUrl, reportName, reportSourceType, theTotalFormats);


            sendMail(parametersJSON, printOptions, totalRecipients, subject, propertiesMap, body, attachments);

            connection = (HttpURLConnection) new URL(logoutUrl).openConnection();
            response = connection.getInputStream();

            try (Scanner scanner = new Scanner(response)) {
                String responseBody = scanner.useDelimiter("\\A").next();
                logger.info(responseBody);
            }
        } catch (IOException ex) {
            logger.error("The exception occurred ", ex);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ex) {
                    logger.error("Error Occurred ", ex);
                }
            }
        }

    }
	/**
	 * getLogOutUrl(String baseUrl)
     * creates the logout URL based on the provided base URL.
     *
     * @param baseUrl      URL of the application.
     * @return The URL to log out the user.
     */
    protected String getLogOutUrl(String baseUrl) {
        return baseUrl + "j_spring_security_exit_user";
    }
    /**
     * getFirstLogin(String baseUrl)
     * creates the URL for the initial login attempt using a predefined user role.
     *
     * @param baseUrl         base URL of the application.
     * @return URL for the initial login attempt.
     */
    protected String getFirstLogin(String baseUrl) {
        ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = ApplicationContextAccessor.getBean(ApplicationDefaultUserAndRoleNamesConfigurer.class);
        String downloadManager = namesConfigurer.getRolePhantomName();

        return baseUrl + "#?username=" + downloadManager + "&j+password=" + downloadManager;
    }
    /**
     * getFinalUrl(Map<String, String> realNames, String baseUrl)
     * Constructs the final URL for impersonation, based on the provided user information.
     *
     * @param realNames         map containing user information such as username and organization.
     * @param baseUrl           base URL of the application.
     * @return URL for impersonation with appropriate user details.
     */
    protected String getFinalUrl(Map<String, String> realNames, String baseUrl) {
        String finalUrl;
        String hwfUrl = baseUrl + "mock/impersonate?username=";
        String organization = realNames.get("organization");

        String username = realNames.get("username");
        if (organization == null) {
            finalUrl = hwfUrl + username;
        } else {
            finalUrl = hwfUrl + username + ":" + organization;
        }
        return finalUrl;
    }
    /**
     * sendMail(JsonObject parametersJSON, JsonObject printOptions, String[] totalRecipients, String subject, Map<String, String> propertiesMap, String body, String[] attachments)
     * Sends an email with attachments to specified recipients using the provided mail properties.
     *
     * @param parametersJSON          object used to add printOption properties.
     * @param printOptions            object containing username,domain,orgnization.
     * @param totalRecipients         An array of email addresses of the recipients.
     * @param subject                 subject line of the email.
     * @param propertiesMap           map containing mail configuration properties.
     * @param body                    content or body of the email.
     * @param attachments An array of file paths to be attached to the email.
     */
    protected void sendMail(JsonObject parametersJSON, JsonObject printOptions, String[] totalRecipients, String subject, Map<String, String> propertiesMap, String body, String[] attachments) {
        String hostName = propertiesMap.get("hostName");
        String port = propertiesMap.get("port");
        String from = propertiesMap.get("from");
        String isAuthenticated = propertiesMap.get("isAuthenticated");
        String isSSLEnabled = propertiesMap.get("isSSLEnabled");
        String user = propertiesMap.get("user");
        String password = propertiesMap.get("password");


        SendMail mailClient = new SendMail();
        // Send mail to all the recipients with all the attachments
        try {
            mailClient.sendMessage(hostName, port, totalRecipients, from, isAuthenticated, isSSLEnabled, user,
                    password, subject, body, attachments);
            cleanUp(parametersJSON, printOptions);
        } catch (AddressException ex) {
            logger.error("AddressException", ex);
        } catch (MessagingException ex) {
            logger.error("MessagingException", ex);
        } catch (IOException ex) {
            logger.error("IOException", ex);
        }
    }
    /**
     * getAttachements(JsonObject printOptions, String csvData, Map<String, String> realNames, String encodedUrl, String reportName, String reportSourceType, String[] theTotalFormats)
     * Returns an array of file paths for attachments based on the provided parameters.
     *
     * @param printOptions              object containing username,domain,orgnization.
     * @param csvData          			CSV data related to the report.
     * @param realNames 				A map containing username, organization, and password.
     * @param encodedUrl 				encoded URL of the report.
     * @param reportName 	 			name of the report.
     * @param reportSourceType 			source type of the report ('adhoc' or not 'adhoc').
     * @param theTotalFormats 			An array of email attachment formats.
     * @return An array of file paths to be used as attachments in the email.
     */
    protected String[] getAttachements(JsonObject printOptions, String csvData, Map<String, String> realNames, String encodedUrl, String reportName, String reportSourceType, String[] theTotalFormats) {
        String username = realNames.get("username");
        String organization = realNames.get("organization");
        String appPassword = realNames.get("password");

        printOptions.addProperty("domain", ApplicationProperties.getInstance().getDomain());
        printOptions.addProperty("username", username);
        printOptions.addProperty("passCode", appPassword);
        printOptions.addProperty("organization", organization);
        return EmailUtility.getAttachmentsArray(theTotalFormats, encodedUrl, reportSourceType,
                reportName, csvData, printOptions);
    }
    /**
     * getBodyLine(JsonObject emailSettings, Map<String, String> propertiesMap)
     * Retrieves the body content for the email message based on the provided email settings and properties map.
     *
     * @param emailSettings        object containing email content.
     * @param propertiesMap        A map containing data required for the Java Mail API.
     * @return body content message.
     */
    protected String getBodyLine(JsonObject emailSettings, Map<String, String> propertiesMap) {
        String body = "";
        if (emailSettings.has("Body")) {
            body = emailSettings.get("Body").getAsString();
        }

        if ((body == null) || "".equals(body) || "[]".equals(body)) {
            body = propertiesMap.get("body");
        }
        return body;
    }
    /**
     * getSubjectLine(JsonObject json, JsonObject emailSettings) 
     * Retrieves the subject line for the email message based on the provided JSON and email settings.
     *
     * @param json         			object containing job-related information.
     * @param emailSettings 	    object containing email content.
     * @return subject line to be used in the email message.
     */
    protected String getSubjectLine(JsonObject json, JsonObject emailSettings) {
        String subject = "";
        if (emailSettings.has("Subject")) {
            subject = emailSettings.get("Subject").getAsString();
            subject = subject + ":  " + json.get("JobName").getAsString();
        }
        return subject;
    }
    /**
     * getReportUrl(String reportEfwFile, String reportDirectory, String baseUrl, String parameters, Map<String, String> realName)
     * Returns URL for accessing a report.
     * @param reportEfwFile				report file
     * @param reportDirectory			directory of file
     * @param baseUrl					url to access report file
     * @param parameters				parameters for the URL
     * @param realName					map containing information about organization, username, password.
     * @return report url created using specified parameters
     */
    protected String getReportUrl(String reportEfwFile, String reportDirectory, String baseUrl, String parameters, Map<String, String> realName) {
        String reportUrl;
        String organization = realName.get("organization");
        String username = realName.get("username");
        String appPassword = realName.get("password");
        if (organization == null) {
            reportUrl = baseUrl + "#?username=" + username + "&password=" + appPassword +
                    "&dir=" + reportDirectory + "&file=" +
                    reportEfwFile + "&" + parameters;
        } else {
            reportUrl = baseUrl + "?j_organization=" + organization + "&username=" + username +
                    "&password=" + appPassword + "&dir=" + reportDirectory + "&file=" +
                    reportEfwFile + "&" + parameters;
        }
        return reportUrl;
    }
    /**
     * cleanUp(JsonObject parametersJSON, JsonObject printOptions)
     * Removes information from the provided JSON objects.
     *
     * This method removes specific key-value pairs from the "printOptions" JsonObject,
     * including "domain", "username", "passCode", and "organization" fields.
     * The modified "printOptions" JsonObject is then added back to the "parametersJSON".
     *
     * @param parametersJSON Object containing parameters for processing.
     * @param printOptions   Object containing information will be removed like username, domain, passcode.
     */
    protected void cleanUp(JsonObject parametersJSON, JsonObject printOptions) {
        printOptions.remove("domain");
        printOptions.remove("username");
        printOptions.remove("passCode");
        printOptions.remove("organization");
        parametersJSON.add("printOptions", printOptions);
    }
    /**
     * updateXmlFile(JobExecutionContext context, JsonObject json, int jobId, String path, XmlOperation xmlOperation)
     * method is responsible for update EndAfterExecutions,NoOfExecutions node which is child node of Schedule
     * node in scheduling.xml
     * @param context           context provides timing to trigger schedule process
     * @param json				scheduling information
     * @param jobId             id to get schedule object 
     * @param path              path of an scheduling.xml file
     * @param xmlOperation		for getting xml object/file.
     */
    protected void updateXmlFile(JobExecutionContext context, JsonObject json, int jobId, String path, XmlOperation xmlOperation) {
        String id = String.valueOf(jobId);
        JsonObject newData = new JsonObject();
        if (json.getAsJsonObject("ScheduleOptions").get("endsRadio").getAsString().equalsIgnoreCase("After")) {
            JsonObject object = xmlOperation.getParticularObject(path, id);
            newData.addProperty("NoOfExecutions", Integer.parseInt(object.get("NoOfExecutions").getAsString()) + 1);
        }
        newData.addProperty("NextExecutionOn", context.getNextFireTime().toString());
        newData.addProperty("LastExecutedOn", context.getFireTime().toString());

        XmlOperationWithParser xmlOperationWithParser = new XmlOperationWithParser();

        xmlOperationWithParser.updateExistingXml(newData, path, id);
    }

    /**
     * getCookiesArray(URLConnection conn)
     * Get the list of cookies from the provided URL connection.
     * @param conn           url for getting cookies
     * @return list of HttpCookie objects
     */
    public List<HttpCookie> getCookiesArray(URLConnection conn) {


        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);

        try {
            Object obj = conn.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CookieStore cookieJar = manager.getCookieStore();
        List<HttpCookie> cookies = cookieJar.getCookies();
        JsonArray cookieArray = new JsonArray();
        for (HttpCookie hc : cookies) {
            JsonObject cookieData = new JsonObject();
            cookieData.addProperty(hc.getName(), hc.getValue());
        }

        return cookies;

    }
}
