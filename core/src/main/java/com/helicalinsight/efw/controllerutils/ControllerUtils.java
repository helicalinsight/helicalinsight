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

package com.helicalinsight.efw.controllerutils;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.BaseLoader;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServicesXmlReader;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ServerSideExportComponent;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by author on 01-Jan-15.
 *
 * @author Rajasekhar
 */
public class ControllerUtils {

    private static final Logger logger = LoggerFactory.getLogger(ControllerUtils.class);
    private final static Map<String, String> propertyMap = ConfigurationFileReader.mapFromClasspathPropertiesFile
            ("reports.properties");

    public static Map<String, String> getPropertyMap() {
        return propertyMap;
    }

    public static void addUrlParameters(HttpServletRequest request, ModelAndView modelAndView) {
        JSONObject urlParameters = new JSONObject();
        urlParameters.accumulateAll(parameters(request));
        modelAndView.addObject("urlParameters", urlParameters);
    }

    public static String getSessionId(HttpServletRequest request) {
        String requestedSessionId = request.getRequestedSessionId();
        boolean requestedSessionIdValid = request.isRequestedSessionIdValid();
        if (logger.isDebugEnabled()) {
            logger.debug("The requested session id of the user is " + requestedSessionId + " and the session is valid");
        }
        if (requestedSessionId != null && requestedSessionIdValid) {
            return requestedSessionId;
        }
        return null;
    }

    public static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    /**
     * Checks whether the parameters are null or empty strings. If an empty or null parameter is
     * found then RequiredParameterIsNullException is thrown with the the message including the
     * faulty parameter.
     *
     * @param parameters A map of request parameters and their values
     */
    public static void checkForNullsAndEmptyParameters(Map<String, String> parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("The map is null");
        }
        Set<Map.Entry<String, String>> entries = parameters.entrySet();

        for (Map.Entry<String, String> entry : entries) {
            String value = entry.getValue();
            if ((value == null) || ("".equals(value)) || (value.trim().length() < 0)) {
                throw new RequiredParameterIsNullException(String.format("The parameter %s is null or empty. " +
                        "Invalid request.", entry.getKey()));
            }
        }
    }

    public static void handleFailure(HttpServletResponse response, boolean isAjax,
                                     Exception exception) throws IOException {
        if (isAjax) {
            String rootCauseMessage = ExceptionUtils.getRootCauseMessage(exception);
            JSONObject theResponse = statusZeroJson(rootCauseMessage);
            logger.error("There was a problem in serving the request. The cause is " + rootCauseMessage, exception);
            handleAjaxRuntimeException(response, theResponse.toString());
        } else {
            //Now ErrorInterceptorFilter and Tomcat deal with the request
            throw new RuntimeException("Unable to process the request. Something went terribly " + "wrong.", exception);
        }
    }

    public static JSONObject statusZeroJson(String rootCauseMessage) {
        JSONObject theResponse;
        theResponse = new JSONObject();

        theResponse.accumulate("status", 0);

        JSONObject messageJson = new JSONObject();
        messageJson.accumulate("message", rootCauseMessage);
        theResponse.accumulate("response", messageJson);
        return theResponse;
    }

    private static void handleAjaxRuntimeException(HttpServletResponse response,
                                                   String message) throws IOException {
        //Commented as 500 status code is not required
        //response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        setContentType(response);
        response.getWriter().write(message);
        response.flushBuffer();
    }

    public static void setContentType(HttpServletResponse response) {
        response.setContentType(defaultContentType());
        response.setCharacterEncoding(ApplicationUtilities.getEncoding());
    }

    public static void handleSuccess(HttpServletResponse response, boolean isAjax,
                                     String executionResult) throws IOException {
        if (isAjax) {
            handleAjaxSuccess(response, executionResult);
        } else {
            handleNormalRequestSuccess(response, executionResult);
        }
    }

    private static void handleAjaxSuccess(HttpServletResponse response, String json) throws IOException {
        setContentType(response);
        write(response, json);
    }

    private static void handleNormalRequestSuccess(HttpServletResponse response,
                                                   String json) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding(ApplicationUtilities.getEncoding());
        write(response, json);
    }

    private static void write(HttpServletResponse response, String json) throws IOException {
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
        out.close();
    }

    /**
     * Saves the file being downloaded in a specific location as efwresult file
     *
     * @param request        Web request
     * @param resultNameTag  Value of the resultNameTag
     * @param fileToDownload The file being downloaded and is being saved.
     */
    //Extracted from two controller methods to modularize on 21-07-2015. Earlier code duplication
    //is reduced.
    public static void saveFile(HttpServletRequest request, String resultNameTag, File fileToDownload) {
        String enableSaveResult = ApplicationProperties.getInstance().getEnableSavedResult();
        if ("TRUE".equalsIgnoreCase(enableSaveResult)) {
            String fileReportPath = request.getParameter("filename");
            String requestParameter = request.getParameter("reportNameParam");
            String reportNameParam = (requestParameter == null) ? fileReportPath : requestParameter;
            String dirReportPath = request.getParameter("dir");
            String reportType = request.getParameter("reportType");
            String resultDirectory = request.getParameter("resultDirectory");

            ServerSideExportComponent serverSideExportComponent = new ServerSideExportComponent(reportNameParam,
                    reportType, resultNameTag, resultDirectory, fileToDownload, dirReportPath, fileReportPath);

            boolean validate = serverSideExportComponent.validateRequestParameters();

            if (validate) {
                serverSideExportComponent.copyReportFromTemp();
                serverSideExportComponent.saveEfwResultFile();
            }
        } else {
            logger.info("The JSONObject corresponding to setting.xml does not have " +
                    "enableSavedResult tag. Please set it enableSavedResult to true to save " +
                    "downloaded report.");
        }
    }

    @SuppressWarnings("unused")
    public static JSONArray loadResource(String location, Boolean recursive) {
        BaseLoader baseLoader = new BaseLoader(ApplicationProperties.getInstance());
        String result;
        JSONArray resultArray = new JSONArray();
        try {
            result = baseLoader.loadResources(location, recursive);
            if (result != null) {
                return JSONArray.fromObject(result);
            }
        } catch (Exception ignore) {
            logger.error("Error occurred ", ignore);
        }
        return resultArray;
    }


    public static void addCookie(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String cookieName,
                                 String cookieValue) {
        String cookiePath = httpRequest.getContextPath();
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath(cookiePath);
        httpResponse.addCookie(cookie);
    }


    public static void accessDenied(HttpServletRequest request, HttpServletResponse response) {
        JSONObject model = new JSONObject();
        model.put("status", 0);

        JSONObject data = new JSONObject();
        data.put("message", "Access Denied. You don't have sufficient privileges to access " + "the requested " +
                "resource.");
        try {
            model.put("response", data);
            if (isAjax(request)) {
                handleSuccess(response, true, model.toString());
            } else {
                request.getRequestDispatcher("WEB-INF/pages/accessdenied.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String defaultContentType() {
        return propertyMap.get("default-content-type");
    }

    public static String defaultCharSet() {
        return propertyMap.get("default-charset");
    }

    public static String getComponentResult(String type, String serviceType, String service, JSONObject formJson,
                                            String serviceClass) {
        IService iService = FactoryMethodWrapper.getTypedInstance(serviceClass, IService.class);
        String result;
        if (iService != null) {
            result = iService.doService(type, serviceType, service, formJson.toString());
        } else {
            throw new EfwServiceException(String.format("The instance of the class %s could not be obtained." + "" +
                    " Check for typos.", serviceClass));
        }
        return result;
    }

    public static String getServiceClass(String type, String serviceType, String service) {
        JSONObject servicesXmlJson = JsonUtils.getJsonOfImportableXml("services");

        String serviceClass = ApplicationContextAccessor.getBean(ServicesXmlReader.class).getServiceClass
                (servicesXmlJson, type, serviceType, service);

        if (serviceClass == null) {
            throw new EfwServiceException("The expected service %s is not " + "available. Check for ");
        }
        return serviceClass;
    }

    @SuppressWarnings("rawtypes")

    public static Map<String, String> parameters(HttpServletRequest request) {
        Enumeration enumeration = request.getParameterNames();

        Map<String, String> parameters = new HashMap<>();
        while (enumeration.hasMoreElements()) {
            String name = (String) enumeration.nextElement();
            String[] value = request.getParameterValues(name);
            if (value.length > 1) {
                String multiSelection = "[";
                for (int count = 0; count < value.length; count++) {
                    String separator = (count == 0 ? "" : ",");
                    multiSelection = multiSelection.trim() + separator + "'" + value[count] + "'";
                }
                parameters.put(name, multiSelection + "]");
            } else {
                parameters.put(name, value[0]);
            }
        }
        return parameters;
    }

    public static void validate(JSONObject formData) {
        JSONObject formDataJson = JSONObject.fromObject(formData);
        String userName;
        String password;
        String jdbcUrl;
        String driverName;
        String name;
        try {
            userName = formDataJson.getString("userName");
            password = formDataJson.getString("password");
            jdbcUrl = formDataJson.getString("jdbcUrl");
            driverName = formDataJson.getString("driverName");
            name = formDataJson.getString("name");
        } catch (Exception e) {
            throw new IncompleteFormDataException("The form data json is incomplete and lacks " +
                    "required parameters. " + ExceptionUtils.getRootCauseMessage(e));
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("userName", userName);
        parameters.put("password", password);
        parameters.put("jdbcUrl", jdbcUrl);
        parameters.put("driverName", driverName);
        parameters.put("name", name);
        checkForNullsAndEmptyParameters(parameters);
    }

    public static JSONObject getDataFromResponse(JSONObject jsonServiceResult) {
        if (jsonServiceResult != null) {
            JSONObject response = jsonServiceResult.optJSONObject("response");
            if (response != null && !response.isEmpty()) {
                return response;
            }
        }
        return new JSONObject();
    }

    public static String concatenateParameters(JSONObject reportParameters) {
        List<String> listOfKeys = JsonUtils.listKeys(reportParameters.discard("csvdata"));

        String parameter = "";

        for (String keyName : listOfKeys) {
            String KeyValue = reportParameters.getString(keyName);

            if (KeyValue.contains("[") || KeyValue.contains("]")) {
                String modifiedKey = KeyValue.substring(1, KeyValue.length() - 1).replace("\"", "");
                String[] array = modifiedKey.split(",");
                for (String parameterValue : array) {
                    parameter = parameter + keyName + "=" + parameterValue + "&";
                }
            } else {
                parameter = parameter + keyName + "=" + KeyValue + "&";
            }
        }
        return parameter;
    }
    public static void replaceFilePath(JSONArray resourceArray) {


        for (Object jsonObject : resourceArray) {
            JSONObject resourceJson = (JSONObject) jsonObject;
            if (resourceJson.has("children")) {
                JSONArray children = resourceJson.getJSONArray("children");
                if (!children.isEmpty()) {
                    replaceFilePath(children);
                }
            } else {
                String type = resourceJson.getString("type");
                String path = resourceJson.getString("path");
                if ("file".equalsIgnoreCase(type)) {
                    resourceJson.put("path", path.replaceAll("\\\\", "/"));
                    resourceJson.discard("absolutepath");


                }
            }

        }
    }

}