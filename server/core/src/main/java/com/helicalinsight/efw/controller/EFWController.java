package com.helicalinsight.efw.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.helicalinsight.admin.model.HIResourceImages;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceEfwContentsService;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.QueryExecutor;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.controllerutils.StatusValidator;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.externalresources.ExternalResourceAbstractFactory;
import com.helicalinsight.efw.externalresources.ExternalResourceFactoryProducer;
import com.helicalinsight.efw.externalresources.IExternalResource;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.resourceloader.BaseLoader;
import com.helicalinsight.efw.resourceloader.DirectoryLoaderProxy;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JRXMLUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.SettingXmlUtility;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.efw.utility.TemplateFileEditor;
import com.helicalinsight.efw.utility.TemplateReader;
import com.helicalinsight.efw.vf.ChartService;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The main controller class of the EFW-Project. Has methods that have request
 * mappings for /hdi, /getSolutionResources, /getEFWSolution,
 * /executeDatasource, /visualizeData, /exportData, /downloadReport,
 * /getExternalResource.
 * <p>
 * <p>
 * A typical setting.xml referenced through out the application will be present
 * in the location System/Admin directory. The System directory is part of the
 * solution directory which consists of the framework related files.
 * <p>
 * The configuration path of setting.xml can be found in project.properties file
 * in the class path.
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
 * @author Prashansa
 * @version 1.0
 * @since 1.0
 */
@Controller
@Component("EFWController")
public class EFWController {

    private static final Logger logger = LoggerFactory.getLogger(EFWController.class);
    private final static Map<String, String> propertyMap = ControllerUtils.getPropertyMap();
    /**
     * The singleton instance of the class
     * <code>ApplicationProperties<code/> which consists
     * of all the settings of the application read from project.properties
     */
    private final ApplicationProperties applicationProperties;
    @Autowired
    private StatusValidator statusValidator;

    @Autowired
    private HIResourceServiceDB hiResourceServiceDB;


    /**
     * The constructor loads the application settings from project.properties
     * and initialises the singleton class for use by the rest of the
     * application.
     */
    public EFWController() {
        this.applicationProperties = ApplicationProperties.getInstance();
    }

    /**
     * Provides the default landing page view and model of the application.
     * Mapped to hdi.html
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
            logger.debug("Request type is :  {}. HI controller is called.", request.getMethod());
        }

        if (this.statusValidator.isStatusNotOkay()) {
            throw new EfwServiceException("Unexpected error occured!");
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

                JsonObject viewHandlerJson = SettingXmlUtility.getViewHandler();
                JsonObject handlerJson = GsonUtility.optJsonObject(viewHandlerJson,extension.toLowerCase());
                if (handlerJson.entrySet().isEmpty()) {
                    throw new EfwServiceException("The file type " + extension + "is not supported.");
                }

                String dirAlias = handlerJson.get("dir").getAsString();
                String fileAlias = handlerJson.get("file").getAsString();
                String redirectLink = handlerJson.get("redirectLink").getAsString();

                if (!"dir".equals(dirAlias)) {
                    request.setAttribute(dirAlias, dir);
                }
                if (!"file".equals(fileAlias)) {
                    request.setAttribute(fileAlias, file);
                }
                //Commenting as not required on 29-06-2015
                //getSolutionResources();
                try {
                    Map<String, String> parameters = ControllerUtils.parameters(request);
                    JsonObject jsonObject = new Gson().toJsonTree(parameters).getAsJsonObject();
                    request.setAttribute("urlParameters", jsonObject);
                    request.setAttribute("reportMode", "open");
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

    @RequestMapping(value = "/helical-report", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView openReport(HttpServletRequest request) throws IOException {
        ModelAndView openReport = getHelicalReportModel(request);
        ControllerUtils.addUrlParameters(request, openReport);
        openReport.setViewName("helical-report");
        return openReport;
    }

    @RequestMapping(value = "/helical-report-edit", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView editReport(HttpServletRequest request) throws IOException {
        ModelAndView openReport = getHelicalReportModel(request);
        openReport.setViewName("helical-report-edit");
        return openReport;
    }

    private ModelAndView getHelicalReportModel(HttpServletRequest request) {
        ModelAndView openReport = new ModelAndView();
        String dir = request.getParameter("dir");
        String fileName = request.getParameter("file");

        if (dir == null && fileName == null) {
            request.setAttribute("urlParameters", "{}");
            //return openReport;
        }
        Map<String, String> parameters = ControllerUtils.parameters(request);
        JsonObject jsonObject = new Gson().toJsonTree(parameters).getAsJsonObject();
        request.setAttribute("urlParameters", jsonObject);
        return openReport;
    }


    /**
     * The folder structure displayed on the main page is obtained by calling
     * this service
     *
     * @return Writes a string of all the folders and files in the EFW directory
     * to the response body as a json
     */
    @RequestMapping("/d/getSolutionResources")
    public
    @ResponseBody
    String getSolutionResources(@RequestParam(value = "resource", required = false) String resource,
                                @RequestParam(value = "recursive", required = false) Boolean recursive,
                                @RequestParam(value = "extensions", required = false) String extensions) throws
            UnSupportedRuleImplementationException, ImproperXMLConfigurationException {
        if (JsonUtils.isFileBrowserCacheEnabled())
            return getSolutionCache(recursive, resource, extensions);
        else
            return getSolutionPlain(recursive, resource);
    }

    @RequestMapping(value="/getSolutionResources",method = {RequestMethod.POST,RequestMethod.GET},produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSolutionResourcesDB(@RequestParam(value = "resource", required = false) String resource,
                                                    @RequestParam(value = "recursive", required = false) Boolean recursive,
                                                    @RequestParam(value = "extensions", required = false) String extensions) throws
            UnSupportedRuleImplementationException, ImproperXMLConfigurationException {

        try{
            HIResourceOfActiveUser hiResourceOfActiveUser = hiResourceServiceDB.getResourceOfActiveUser();
            List<HIResourceDTO> resourceDTOList = hiResourceOfActiveUser.getResourceDTOList();
            return ResponseEntity.ok().body(resourceDTOList);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value="/getSolutionResourcesTest",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String getSolutionResourcesDBTest(@RequestParam(value = "resource", required = false) String resource,
                                                    @RequestParam(value = "recursive", required = false) Boolean recursive,
                                                    @RequestParam(value = "extensions", required = false) String extensions) throws
            UnSupportedRuleImplementationException, ImproperXMLConfigurationException {
    	    HttpHeaders responseHeaders = new HttpHeaders();
    	    responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try{
            HIResourceOfActiveUser hiResourceOfActiveUser = hiResourceServiceDB.getResourceOfActiveUser();
            ArrayList<HIResourceDTO> resourceDTOList = (ArrayList<HIResourceDTO>) hiResourceOfActiveUser.getResourceDTOList();
            return new Gson().toJson(resourceDTOList);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
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

        if (this.statusValidator.isStatusNotOkay()) {
           throw new EfwServiceException("Unexpected error occured!");
        }

        ModelAndView serviceLoadView = new ModelAndView();
        response.setContentType("text/html");
        String absolutePath = applicationProperties.getSolutionDirectory();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        String templateData;
        String efwTemplate;
        JsonObject efwFileJsonObject;

        String efwFilePath = absolutePath + File.separator + dirPath + File.separator + efwFile;
        logger.debug("The path of efw file is " + efwFilePath);
        efwFileJsonObject = processor.getJsonObject(efwFilePath, false);
        if (efwFileJsonObject == null) {
            serviceLoadView.addObject("message", "The respective file was not found");
            logger.error("The file or the folder requested is not found");
            serviceLoadView.setViewName("serviceLoadView");
            return serviceLoadView;
        }

        efwTemplate = efwFileJsonObject.get("template").getAsString();

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
                logger.debug("Service condition is invoked '" + request.getAttribute("service") + "'");

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


    @RequestMapping(value = "/viewReport", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String executeJRXML(@RequestParam("data") String data,
                               HttpServletRequest request, HttpServletResponse response) {
        JRXMLUtils.setData(data);
        JsonObject jsonData = JsonParser.parseString(new Gson().toJson(data)).getAsJsonObject();
        byte[] bytes = JRXMLUtils.executeReport();
        ControllerUtils.addCookie(request, response, "reportDownloadStatus", "1");
        String format = jsonData.get("format").getAsString();
        JsonObject printOptionsJson = new JsonObject();
        printOptionsJson.add("format",  JsonParser.parseString("['" + format + "']").getAsJsonArray());
        JsonArray cookiesArray = ControllerUtils.newGetCookieArray(request);
        printOptionsJson.add("cookie", cookiesArray);
        response.setContentType(propertyMap.get(format));
        String name = "sample." + format;

        // Set the response headers
        String dispositionType = "inline; ";

        response.setHeader("Content-Disposition", String.format(dispositionType + "filename=\"%s\"", name));
        writeFileToStream(format, response, bytes);

        return null;
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

        if (this.statusValidator.isStatusNotOkay()) {
            throw new EfwServiceException("Unexpected error occured!");
        }

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

        if (this.statusValidator.isStatusNotOkay()) {
            throw new EfwServiceException("Unexpected error occured!");
        }

        response.setContentType(ControllerUtils.defaultContentType());
        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            JsonObject jsonObject;
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
            JsonObject jsonObject;

            String efwFilePath = absolutePath + File.separator + dir + File.separator + fileName;
            logger.debug("The efw file path is " + efwFilePath);
            jsonObject = processor.getJsonObject(efwFilePath, false);
            template = jsonObject.get("template").getAsString();
            if (template.isEmpty() || template.length() == 0) {
                logger.error("EFW file has no template element. HTML file not found.");
                throw new ResourceNotFoundException("EFW file has no template element. HTML file not found.");
            }
            String templatePath = absolutePath + File.separator + dir + File.separator + template;
            TemplateFileEditor file = new TemplateFileEditor();
            file.createFileAccordingToCondition(htmlString, templatePath);

            JsonObject model = new JsonObject();
            model.addProperty("status", 1);

            JsonObject successResponse = new JsonObject();
            successResponse.addProperty("message", "Successfully updated the template");
            model.add("response", successResponse);
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
     * @throws IOException 
     */
    @RequestMapping(value = "/getExternalResource", method = RequestMethod.GET)
    public void getExternalResource(@RequestParam("path") String path, HttpServletResponse response) throws IOException {
        String absolutePath;
        Boolean isResourceExists=Boolean.TRUE;

        if (path.contains("_TEMP_")) {
            absolutePath = TempDirectoryCleaner.getTempDirectory().getAbsolutePath() + File.separator;
            path = path.replaceAll("_TEMP_", "");
        } else
            absolutePath = applicationProperties.getSolutionDirectory() + File.separator;
        File file = new File(absolutePath + path);
        String title= file.getName();
        String[] tokens = path.split(("\\.(?=[^\\.]+$)"));
        String extension = tokens[1];
        if(!file.exists()) {

        		HIResource hiResource=hiResourceServiceDB.getResourceByUrl(path);

            if (hiResource != null) {
                HIResourceImages hiResourceImages = hiResource.getHiResourceImages();
                title = hiResource.getTitle();
                String contentType = hiResourceImages.getContentType();
              String tempImageFile =  TempDirectoryCleaner.getTempDirectory().getAbsolutePath() + File.separator+hiResource.getTitle()+"."+ contentType;
                file = new File(tempImageFile);
                extension=contentType;
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(hiResourceImages.getContent());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
        if (tokens.length < 2 || !isResourceExists) {
            logger.warn("The requested file type is not recognized. " + file);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("The requested file type is " + file);
            }
        }


        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        boolean foundType = false;

        if (!settingsJson.has("contents")) {
            throw new EfwServiceException("The application configuration is incorrect. Configure " +
                    "" + "contents in setting.xml.");
        } else {
            Object contents = settingsJson.get("contents");
            if (contents instanceof JsonObject) {
                JsonArray contentsJson = settingsJson.getAsJsonObject("contents").getAsJsonArray("type");
                for (JsonElement object : contentsJson) {
                    JsonObject typeJson = object.getAsJsonObject();
                    String clazz = typeJson.get("class").getAsString();
                    try {
                        JsonArray array = typeJson.getAsJsonArray("content");
                        for (JsonElement item : array) {
                            JsonObject contentJson = item.getAsJsonObject();
                            contentJson.addProperty("fileTitle",title);
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
                        JsonObject contentJson = typeJson.getAsJsonObject("content");
                        contentJson.addProperty("fileTitle",title);
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

    private boolean execute(HttpServletResponse response, File file, String extension, String clazz,
                            JsonObject contentJson) {
        String pattern = contentJson.get("pattern").getAsString();
        String patternExtension = getPatternExtension(pattern);
        if (extension.equalsIgnoreCase(patternExtension)) {
            response.reset();
            if (contentJson.has("responseContent")) {
                response.setContentType(contentJson.get("responseContent").getAsString());
            }
            if(contentJson.has("fileTitle")){
                response.setHeader("File-Title",contentJson.get("fileTitle").getAsString());
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

    private String getSolutionPlain(Boolean recursive, String resource) throws UnSupportedRuleImplementationException {
        if (this.statusValidator.isStatusNotOkay()) {
            throw new EfwServiceException("Unexpected error occured!");
        }
        BaseLoader baseLoader = new BaseLoader(applicationProperties);
        if (recursive == null) {
            recursive = applicationProperties.isRecursiveResourceLoad();
        }
        if (resource == null) {
            resource = "";
        }
        String resourceString = baseLoader.loadResources(resource, recursive);
        JsonArray resourceArr = JsonParser.parseString(resourceString).getAsJsonArray();
        ControllerUtils.replaceFilePath(resourceArr);
        return resourceArr.toString();
    }

    private Map<String,Object> getSolutionPlainDB(Boolean recursive, String resource) throws UnSupportedRuleImplementationException {
        if (this.statusValidator.isStatusNotOkay()) {
            throw new EfwServiceException("Unexpected error occured!");
        }
        BaseLoader baseLoader = new BaseLoader(applicationProperties);
        if (recursive == null) {
            recursive = applicationProperties.isRecursiveResourceLoad();
        }
        if (resource == null) {
            resource = "";
        }
        String resourceString = baseLoader.loadResources(resource, recursive);
        JsonArray resourceArr = JsonParser.parseString(resourceString).getAsJsonArray();
        ControllerUtils.replaceFilePath(resourceArr);
        //return resourceArr.toString();
        return new HashMap<>();
    }

    private String getSolutionCache(Boolean recursive, String resource, String extensions) throws UnSupportedRuleImplementationException {
        extensions = "[\"all\"]".equals(extensions) ? null : extensions;
        if (extensions != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Received a request to get the resources information from the Solution " + "directory.");
            }
            boolean flag = false;
            String updatedResource = null;
            if ((resource == null || "/".equals(resource) || "//".equals(resource) || "".equals(resource))) {
                updatedResource = applicationProperties.getSolutionDirectory();
                flag = true;

            } else {
                updatedResource = applicationProperties.getSolutionDirectory() + File.separator + resource;
            }
            Boolean requestedRecursive = null;
            if (recursive == null) {
                recursive = true;
                requestedRecursive = recursive;
            } else {
                requestedRecursive = recursive;
                recursive = true;
            }
            long now = System.currentTimeMillis();
            long later;
            List listOfKeys = null;
            if (extensions != null) {
                if ("[]".equals(extensions) || "".equals(extensions) || extensions.trim().length() == 0) {
                    throw new IllegalArgumentException("When provided, the parameter 'extensions'  " + "should not be " +
                            "empty.");
                }
                JsonArray jsonArray;
                try {
                    jsonArray = JsonParser.parseString(extensions).getAsJsonArray();
                } catch (Exception e) {
                    throw new EfwServiceException("The parameter extensions is expected to be of " + "type json array.");
                }
                
                for(JsonElement element : jsonArray) {
                	listOfKeys.add(element);
                }
            }

            //boolean isAjax = ControllerUtils.isAjax(request);
            try {
                String resources = new DirectoryLoaderProxy(listOfKeys).getResources(updatedResource, recursive);
                JsonArray resourcesJsonArray = JsonParser.parseString(resources).getAsJsonArray();
                ControllerUtils.replaceFilePath(resourcesJsonArray);
                later = System.currentTimeMillis();

                if (logger.isDebugEnabled()) {
                    logger.debug("It took nearly " + ((later - now) / 1000) + " seconds to complete " +
                            "the IO operations to get the resources information.");
                }
                if (!requestedRecursive)
                    ControllerUtils.prepareLevel(resourcesJsonArray, flag);
                return resourcesJsonArray.toString();
                //ControllerUtils.handleSuccess(response, isAjax, resourcesJsonArray.toString());
            } catch (Exception exception) {
                later = System.currentTimeMillis();
                if (logger.isDebugEnabled()) {
                    logger.debug("There was some problem. Could not read the resources. It took " +
                            "nearly " + ((later - now) / 1000) + " seconds to complete the IO " +
                            "operations to get the resources information.");
                }
                //ControllerUtils.handleFailure(response, isAjax, exception);
            }
        } else {
            if (this.statusValidator.isStatusNotOkay()) {
                throw new EfwServiceException("Unexpected error occured!");
            }

            BaseLoader baseLoader = new BaseLoader(applicationProperties);

            if (recursive == null) {
                recursive = applicationProperties.isRecursiveResourceLoad();
            }
            if (resource == null) {
                resource = "";
            }
            String resourceString = baseLoader.loadResources(resource, recursive);
            JsonArray resourceArr = JsonParser.parseString(resourceString).getAsJsonArray();
            ControllerUtils.replaceFilePath(resourceArr);
            return resourceArr.toString();
        }
        return "Resource not found.";
    }

    @RequestMapping(value = "/{page}", method = {RequestMethod.GET, RequestMethod.POST})
    public String tilesView(@PathVariable("page") String page) {
        return page;
    }


    @RequestMapping(value = "/efw-template", method = {RequestMethod.GET, RequestMethod.POST})
    public String efwTemplate() {
        return "efw-template";
    }


    @RequestMapping(value = "/mock/switchUser", method = {RequestMethod.GET, RequestMethod.POST})
    public String switchUser() {
        return "switchUser";

    }

    private void writeFileToStream(String format, HttpServletResponse response, byte[] byteArray) {
        OutputStream outputStream = null;

        try {
            // Write to outputStream
            outputStream = response.getOutputStream();
            outputStream.write(byteArray, 0, byteArray.length);
            outputStream.flush();

        } catch (FileNotFoundException ex) {
            logger.error("FileNotFoundException occurred as the " + format + " file is not " +
                    "generated.", ex);
        } catch (IOException ex) {
            logger.error("IOException occurred", ex);
        } finally {
            ApplicationUtilities.closeResource(outputStream);
        }
    }

    @RequestMapping(value = "/applicationSettings", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String getApplicationSettings(HttpServletRequest request) {
        UserSession userSession = new UserSession();
        JsonObject userData = new JsonObject();
        userSession.addSessionDetails("getContents",request,userData);
        userData.addProperty("contentId", "Static/DashboardGlobals");
        IComponent anySettingsGroovyProcessor = FactoryMethodWrapper
                .getTypedInstance("com.helicalinsight.export.components.AnySettingsGroovyProcessor", IComponent.class);
        String fileData = anySettingsGroovyProcessor.executeComponent(userData.toString());
        Object requestRemaining = request.getServletContext().getAttribute("remainingDays");
        String remainingDays=null;
        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        if(settingsJson.has("showExperimentalFeatures")){
            userData.addProperty("showExperimentalFeatures",settingsJson.get("showExperimentalFeatures").getAsBoolean());
        }
        userData.addProperty("streamResponse", GsonUtility.optBooleanValue(settingsJson, "streamResponse", false));
		 if(settingsJson.has("hideDefaultLoginButtons")){
            userData.addProperty("hideDefaultLoginButtons",settingsJson.get("hideDefaultLoginButtons").getAsBoolean());
        }
         if(settingsJson.has("serviceSpeedLimitAlert")){
            userData.addProperty("serviceSpeedLimitAlert",settingsJson.get("serviceSpeedLimitAlert").getAsInt());
        }
        if(requestRemaining!=null)
            remainingDays= ""+ requestRemaining;
        if(StringUtils.isNotEmpty(remainingDays)) {
            JsonObject license = new JsonObject();
            String remainingDayMessage = "";
            if (remainingDays.equals("0")) {
                remainingDayMessage = "Your license is about to expire today";
            } else if (Integer.parseInt(remainingDays) > 0) {
                remainingDayMessage = "Your license is about to expire. You have " + remainingDays + " day(s) remaining.";
            }
            license.addProperty("remainingDays", remainingDays);
            license.addProperty("remainingDayMessage", remainingDayMessage);
            userData.add("license", license);
        }
        userData.add("settings",new Gson().fromJson(fileData,JsonObject.class));
        return userData.toString();

    }
}