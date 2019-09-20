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

package com.helicalinsight.efw.controller;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.QueryExecutor;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.*;
import com.helicalinsight.efw.externalresources.ExternalResourceAbstractFactory;
import com.helicalinsight.efw.externalresources.ExternalResourceFactoryProducer;
import com.helicalinsight.efw.externalresources.IExternalResource;
import com.helicalinsight.efw.resourceloader.BaseLoader;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.*;
import com.helicalinsight.efw.vf.ChartService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * The main controller class of the EFW-Project. Has methods that have request
 * mappings for /hi, /getSolutionResources, /getEFWSolution,
 * /executeDatasource, /visualizeData, /exportData, /downloadReport,
 * /getExternalResource.
 * <p/>
 * <p/>
 * A typical setting.xml referenced through out the application will be present
 * in the location System/Admin directory. The System directory is part of the
 * solution directory which consists of the framework related files.
 * <p/>
 * The configuration path of setting.xml can be found in project.properties file
 * in the class path.
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
 * @author Prashansa
 */
@Controller
@Component("EFWController")
public class EFWController {

    private static final Logger logger = LoggerFactory.getLogger(EFWController.class);

    /**
     * The singleton instance of the class
     * <code>ApplicationProperties<code/> which consists
     * of all the settings of the application read from project.properties
     */
    private final ApplicationProperties applicationProperties;

    /**
     * The constructor loads the application settings from project.properties
     * used by the rest of the application.
     */
    public EFWController() {
        this.applicationProperties = ApplicationProperties.getInstance();
    }

    /**
     * Provides the default landing page view and model of the application.
     * Mapped to hi.html
     *
     * @param request  HttpServletRequest object
     * @param response HttpServletResponse object
     * @return ModelAndView object
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/hi", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView hdi(HttpServletRequest request, HttpServletResponse response) throws
            UnSupportedRuleImplementationException, ImproperXMLConfigurationException {
        if (logger.isDebugEnabled()) {
            logger.debug("Request type is :  {}. hi controller is called.", request.getMethod());
        }

        Enumeration<String> enumeration = request.getParameterNames();
        if (enumeration.hasMoreElements()) {
            if (request.getParameter("dir") != null && request.getParameter("file") != null) {
                /*
                 * Adding request parameter "service = run" to understand
				 * whether the user requested to execute the report using GET
				 * parameters
				 */
                request.setAttribute("service", "run");
                String dir = request.getParameter("dir");
                String file = request.getParameter("file");
                logger.debug("The parameter dir is " + dir + ", and file is " + file);

                String extension = FilenameUtils.getExtension(file);

                JSONObject viewHandlerJson = SettingXmlUtility.getViewHandler();
                JSONObject handlerJson = viewHandlerJson.optJSONObject(extension.toLowerCase());
                if (handlerJson.isEmpty()) {
                    throw new EfwServiceException("The file type " + extension + "is not supported.");
                }

                String dirAlias = handlerJson.getString("dir");
                String fileAlias = handlerJson.getString("file");
                String redirectLink = handlerJson.getString("redirectLink");

                if (!"dir".equals(dirAlias)) {
                    request.setAttribute(dirAlias, dir);
                }
                if (!"file".equals(fileAlias)) {
                    request.setAttribute(fileAlias, file);
                }
                //Commenting as not required on 29-06-2015
                //getSolutionResources();
                try {
                    request.getRequestDispatcher(redirectLink).forward(request, response);
                    logger.debug("Returning null from /hi mapping");
                    return null;
                } catch (ServletException e) {
                    logger.error("ServletException occurred {}", e);
                    throw new RuntimeException("Couldn't forward to the getEFWSolution");
                } catch (IOException e) {
                    logger.error("IOException occurred", e);
                    throw new RuntimeException("Couldn't forward to the getEFWSolution");
                }
            }
        } else {
            logger.debug("No parameter supplied.");
        }

        return new ModelAndView();
    }

    /**
     * The folder structure displayed on the main page is obtained by calling
     * this service
     *
     * @return Writes a string of all the folders and files in the EFW directory
     * to the response body as a json
     */
    @RequestMapping("/getSolutionResources")
    public
    @ResponseBody
    String getSolutionResources(@RequestParam(value = "resource", required = false) String resource,
                                @RequestParam(value = "recursive", required = false) Boolean recursive) throws
            UnSupportedRuleImplementationException, ImproperXMLConfigurationException {
        BaseLoader baseLoader = new BaseLoader(applicationProperties);

        if (recursive == null) {
            recursive = applicationProperties.isRecursiveResourceLoad();
        }
        if (resource == null) {
            resource = "";

        }
        String resourceString = baseLoader.loadResources(resource, recursive);
        JSONArray resourceArr = JSONArray.fromObject(resourceString);
        ControllerUtils.replaceFilePath(resourceArr);
        return resourceArr.toString();

    }

    /**
     * Returns the EFW template file content
     *
     * @param dirPath  The request parameter solution directory path
     * @param efwFile  The request parameter efw file
     * @param response HttpServletResponse object
     * @param request  HttpServletRequest object
     * @return Returns the EFW template file content
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getEFWSolution", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView getEFWSolution(@RequestParam("dir") String dirPath, @RequestParam("file") String efwFile,
                                       HttpServletResponse response, HttpServletRequest request) {
        logger.debug("The /getEFWSolution controller mapping is called.");
        logger.debug("The http parameters are dir: " + dirPath + ", efwFile: " + efwFile);

        ModelAndView serviceLoadView = new ModelAndView();
        response.setContentType("text/html");
        String absolutePath = applicationProperties.getSolutionDirectory();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        String templateData;
        String efwTemplate;
        JSONObject efwFileJsonObject;

        String efwFilePath = absolutePath + File.separator + dirPath + File.separator + efwFile;
        logger.debug("The path of efw file is " + efwFilePath);
        efwFileJsonObject = processor.getJSONObject(efwFilePath, false);
        if (efwFileJsonObject == null) {
            serviceLoadView.addObject("message", "The respective file was not found");
            logger.error("The file or the folder requested is not found");
            serviceLoadView.setViewName("serviceLoadView");
            return serviceLoadView;
        }

        efwTemplate = efwFileJsonObject.getString("template");

        String encoding = ApplicationUtilities.getEncoding();

        if (efwTemplate.isEmpty() || efwTemplate.length() == 0) {
            logger.error("EFW file has no template element. HTML file not found.");
        } else {
            String templateFile;
            if (efwTemplate.contains("solution:")) {
                templateFile = efwTemplate.replaceFirst("solution:", absolutePath + File.separator);
            } else {
                templateFile = absolutePath + File.separator + dirPath + File.separator +
                        efwTemplate;
            }

            TemplateReader templateReader = new TemplateReader(new File(templateFile));
            templateData = templateReader.readTemplate();
            Enumeration<String> enumeration = request.getParameterNames();

            Map<String, String> parameterValues = new HashMap<>();
            while (enumeration.hasMoreElements()) {
                String name = enumeration.nextElement();
                logger.debug("The http parameter name: " + name);
                String[] value = request.getParameterValues(name);
                if (value.length > 1) {
                    String temp = "";
                    for (int i = 0; i < value.length; i++) {
                        temp = temp.trim() + (i == 0 ? "" : ",") + "'" + value[i] + "'";
                    }
                    parameterValues.put(name, temp);
                } else {
                    parameterValues.put(name, "'" + value[0] + "'");
                }
                logger.debug("The parameter's value length is " + value.length);
            }

            for (Map.Entry<String, String> entry : parameterValues.entrySet()) {
                String key = entry.getKey();
                logger.debug("The templateData contains ${} " + templateData.contains("${" +
                        key + "}"));
                if (templateData.contains("${" + key + "}")) {
                    templateData = templateData.replace("${" + key + "}", entry.getValue());
                }
            }

            if (request.getAttribute("service") != null) {
                logger.debug("Service condition '" + request.getAttribute("service") + "'");

                ControllerUtils.addUrlParameters(request, serviceLoadView);

                serviceLoadView.addObject("dir", dirPath.replace("\\", "\\\\"));
                serviceLoadView.addObject("templateData", templateData);
                serviceLoadView.setViewName("serviceLoadView");
            } else {
                OutputStream outputStream;
                try {
                    outputStream = response.getOutputStream();
                    outputStream.write(templateData.getBytes(encoding));
                    String parameters = request.getParameter("parameters");
                    if (parameters != null) {
                        outputStream.write(("<script> var parameters=" + parameters + ";</script>").getBytes(encoding));
                    }

                    outputStream.flush();
                    ApplicationUtilities.closeResource(outputStream);
                    return null;
                } catch (IOException e) {
                    logger.error("IOException occurred ", e);
                }
            }
        }
        return serviceLoadView;
    }

    /**
     * Returns the data from the database by executing the query
     *
     * @param data The request parameter data
     * @return Returns the data by executing the query
     */
    @RequestMapping(value = "/executeDatasource", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    String executeDatasource(@RequestParam("data") String data, HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        response.setContentType(ControllerUtils.defaultContentType());
        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            if (data != null && data.length() > 0) {
                QueryExecutor dataSource = new QueryExecutor(data, applicationProperties);
                final JsonObject resultSet = dataSource.getResultSet();
                if (resultSet != null) {
                    return resultSet.toString();
                }
            } else {
                throw new RequiredParameterIsNullException("Request parameter data is null");
            }
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
        return null;
    }

    /**
     * Returns the visualization data from the ChartService
     *
     * @param chartData Request parameter data
     *                  Writes the visualization data from the ChartService to output stream
     */
    @RequestMapping(value = "/visualizeData", method = RequestMethod.POST)
    public void visualizeData(@RequestParam("data") String chartData, HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The /visualizeData controller mapping is called with http data %s.",
                    chartData));
        }
        response.setContentType(ControllerUtils.defaultContentType());
        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            JSONObject jsonObject;
            if (chartData != null && chartData.length() > 0) {
                ChartService chartService = new ChartService(chartData, applicationProperties);
                jsonObject = chartService.getChartData();
                if (jsonObject != null) {
                    String responseData = jsonObject.toString();
                    ControllerUtils.handleSuccess(response, isAjax, responseData);
                } else {
                    throw new EfwServiceException("The data returned from ChartService is null.");
                }
            } else {
                throw new RequiredParameterIsNullException("Parameter data is not valid.");
            }
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

	/*
     * Currently only CSV, XLS are supported.
	 */

    /**
     * This service is used for updating the template file with the ad-hoc
     * changes made by the user in the dashboard
     *
     * @param dir        The request parameter - directory of the template file
     * @param htmlString The request parameter - source of the html page
     * @param fileName   The request parameter - the name of the template file
     */
    @RequestMapping(value = "/updateEFWTemplate", method = RequestMethod.POST)
    public void updateEFWTemplate(@RequestParam("dir") String dir, @RequestParam("xml") String htmlString,
                                  @RequestParam("file") String fileName, HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            String absolutePath = applicationProperties.getSolutionDirectory();
            String template;
            JSONObject jsonObject;

            String efwFilePath = absolutePath + File.separator + dir + File.separator + fileName;
            logger.debug("The efw file path is " + efwFilePath);
            jsonObject = processor.getJSONObject(efwFilePath, false);
            template = jsonObject.getString("template");
            if (template.isEmpty() || template.length() == 0) {
                logger.error("EFW file has no template element. HTML file not found.");
                throw new ResourceNotFoundException("EFW file has no template element. HTML file not found.");
            }
            String templatePath = absolutePath + File.separator + dir + File.separator + template;
            TemplateFileEditor file = new TemplateFileEditor();
            file.createFileAccordingToCondition(htmlString, templatePath);

            JSONObject model = new JSONObject();
            model.put("status", 1);

            JSONObject successResponse = new JSONObject();
            successResponse.put("message", "Successfully updated the template");
            model.put("response", successResponse);
            ControllerUtils.handleSuccess(response, isAjax, model.toString());
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    /**
     * Returns the external resources like images, css and js requested by the
     * browser
     *
     * @param path     The request parameter - path of the external resource
     *                 requested
     * @param response HttpServletResponse object
     */
    @RequestMapping(value = "/getExternalResource", method = RequestMethod.GET)
    public void getExternalResource(@RequestParam("path") String path, HttpServletResponse response) {
        File file = new File(applicationProperties.getSolutionDirectory() + File.separator + path);
        String[] tokens = path.split(("\\.(?=[^\\.]+$)"));
        if (tokens.length < 2 || !file.exists()) {
            logger.warn("The requested file type is not recognized. " + file);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("The requested file type is " + file);
            }
        }
        String extension = tokens[1];

        JSONObject settingsJson = JsonUtils.getSettingsJson();
        boolean foundType = false;

        if (!settingsJson.has("contents")) {
            throw new EfwServiceException("The application configuration is incorrect. Configure " +
                    "" + "contents in setting.xml.");
        } else {
            String contents = settingsJson.getString("contents");
            if (contents.matches("^\\[.*\\]$")) {
                JSONArray contentsJson = settingsJson.getJSONArray("contents");
                for (Object object : contentsJson) {
                    JSONObject typeJson = JSONObject.fromObject(object);
                    String clazz = typeJson.getString("@class");
                    try {
                        JSONArray array = typeJson.getJSONArray("content");
                        for (Object item : array) {
                            JSONObject contentJson = JSONObject.fromObject(item);
                            if (execute(response, file, extension, clazz, contentJson)) {
                                foundType = true;
                                break;
                            }
                        }
                        if (foundType) {
                            return;
                        }
                    } catch (Exception ignore) {
                        logger.error("Exception", ignore);
                        JSONObject contentJson = typeJson.getJSONObject("content");
                        if (execute(response, file, extension, clazz, contentJson)) {
                            break;
                        }
                    }
                }
            } else {
                throw new EfwServiceException("The application configuration is incorrect. " + "Configure contents as" +
                        " array in setting.xml.");
            }
        }
    }

    @RequestMapping(value = "/{page}", method = {RequestMethod.GET, RequestMethod.POST})
    public String tilesView(@PathVariable("page") String page) {
        return page;
    }

    @RequestMapping(value = "/mock/switchUser", method = {RequestMethod.GET, RequestMethod.POST})
    public String switchUser(String page) {
        return "switchUser";

    }


    private boolean execute(HttpServletResponse response, File file, String extension, String clazz,
                            JSONObject contentJson) {
        String pattern = contentJson.getString("@pattern");
        String patternExtension = getPatternExtension(pattern);
        if (extension.equalsIgnoreCase(patternExtension)) {
            if (contentJson.has("@responseContent")) {
                response.setContentType(contentJson.getString("@responseContent"));
            }
            ExternalResourceAbstractFactory processFactory = ExternalResourceFactoryProducer.getFactory(file, clazz,
                    response);
            IExternalResource externalResource = processFactory.getExternalResource();
            externalResource.getFileType();
            return true;
        } else {
            return false;
        }
    }



    private String getPatternExtension(String pattern) {
        String[] strings = pattern.split(("\\.(?=[^\\.]+$)"));
        if (strings.length < 2) {
            throw new EfwServiceException("The configuration of the contents tag is incorrect.");
        }
        return strings[1];
    }
}