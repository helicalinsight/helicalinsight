package com.helicalinsight.efw.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.HCReport;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.io.FileOperationsUtility;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ExecuteReport;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResponseUtils;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This class handles the execution of the efwsr files. Has methods with request
 * mappings /executeSavedReport.
 * </p>
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
 * @version 1.0
 * @since 1.1
 */

@Controller
@Component
public class EFWSRController {

    private static final Logger logger = LoggerFactory.getLogger(EFWSRController.class);

    /**
     * The method handles the execution of the efwsr files. This method is
     * responsible to execute the saved report. Sets the request attribute
     * decorator in the request scope and forwards the request to the resource
     * serviceLodeView.jsp
     *
     * @param directoryName The dir request parameter
     * @param fileName      The file request parameter
     * @param request       The http request object
     * @return The ModelAndView object
     */
    @RequestMapping(value = "/executeSavedReport", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView executeSavedReport(@RequestParam("dir") String directoryName,
                                           @RequestParam("file") String fileName, HttpServletRequest request) {
        request.setAttribute("decorator", "empty");
        ApplicationProperties properties = ApplicationProperties.getInstance();
        ExecuteReport executeReport = new ExecuteReport();
        ModelAndView serviceLoadView = new ModelAndView();
        FileOperationsUtility fileOperationsUtility = new FileOperationsUtility();
        String solutionFolder = properties.getSolutionDirectory();
        File directory = new File(solutionFolder + File.separator + directoryName);

        String searchResult = fileOperationsUtility.search(solutionFolder, fileName);

        String path = searchResult == null ? "" : searchResult;
        if ((directoryName == null) || "".equalsIgnoreCase(directoryName)) {
            checkPath(path);
        } else if (!directory.isDirectory()) {
            checkPath(path);
        } else {
            File theFile = new File(properties.getSolutionDirectory() + File.separator +
                    directoryName + File.separator + fileName);
            if (!theFile.exists()) {
                checkPath(path);
            }
        }

        logger.debug("Trying to get JSON for the resource " + path);

        Efwsr efwsr = JaxbUtils.unMarshal(Efwsr.class, new File(path));
        /*
         * Get the saved JSON from the rdf/efwsr file. Convert string to JSONObject
        */
        String efwDirectory = efwsr.getReportDirectory();
        String efwFile = efwsr.getReportFile();

        /***************************************************/
        this.setUnsetRefresh(request, efwFile, efwDirectory);
        /***************************************************/

        ControllerUtils.addUrlParameters(request, serviceLoadView);

        JsonObject parametersJSON = JsonParser.parseString(new Gson().toJson(efwsr.getReportParameters())).getAsJsonObject();
        String dirPath;
        int index = efwFile.lastIndexOf(".");
        String extension = efwFile.substring(index + 1);
        if (extension.equalsIgnoreCase(JsonUtils.getReportExtension()) || extension.equalsIgnoreCase(JsonUtils.getHrReportExtension())) {
            dirPath = efwDirectory;
            String actualParameters = "";

            Map<String, Object> model = serviceLoadView.getModel();
            JsonObject urlParameters = (JsonObject) model.get("urlParameters");

            if (parametersJSON != null && !parametersJSON.entrySet().isEmpty()) {
                if (urlParameters.has("newParameters")) {
                    String dir = GsonUtility.optString(urlParameters,"dir");
                    String file = GsonUtility.optString(urlParameters,"file");
                    urlParameters.remove("dir");
                    urlParameters.remove("file");
                    urlParameters.remove("refresh");
                    urlParameters.remove("mode");
                    urlParameters.remove("newParameters");
                    actualParameters = "&" + ControllerUtils.concatenateParameters(urlParameters);
                    urlParameters.addProperty("dir", dir);
                    urlParameters.addProperty("file", file);
                } else {
                    actualParameters = "&" + ControllerUtils.concatenateParameters(parametersJSON);
                }
                actualParameters = actualParameters.substring(0, actualParameters.length() - 1);
            }

            String templateData = "/adhocReport.html?file=" + efwFile + "&dir=" + dirPath.replace("\\",
                    "\\\\") + actualParameters;
            if (extension.equalsIgnoreCase(JsonUtils.getHrReportExtension())) {
                templateData = "/hi.html?file=" + efwFile + "&mode=open&dir=" + dirPath.replace("\\",
                        "\\\\") + actualParameters;
            }

            serviceLoadView.addObject("templateData", templateData);
            serviceLoadView.addObject("isAdhoc", true);
        } else if (extension.equalsIgnoreCase(JsonUtils.getHCRExtension())) {
            HCReport hcrReport = (HCReport) ReportOpenHelper.getHCRReport(directoryName, fileName);

            ControllerUtils.addUrlParameters(request, serviceLoadView);
            Object urlParameters = serviceLoadView.getModel().get("urlParameters");
            if (urlParameters != null) {
                JsonObject json = (JsonObject) urlParameters;
                json.addProperty("dir", efwDirectory);
                json.addProperty("file", efwFile);
            }

            if (hcrReport != null) {
                JsonObject attributeValue = ReportOpenHelper.reportContentAsJson(hcrReport);
                attributeValue.addProperty("reportName", efwsr.getReportName());
                serviceLoadView.addObject("reportParameters", attributeValue);
                serviceLoadView.addObject("savedParameters",efwsr.getReportParameters());
            }
            String response = getExportFromStatic();
            serviceLoadView.addObject("exportTypes", response);
            serviceLoadView.setViewName("hcrReport");
            return serviceLoadView;

        } else {
            List<String> list = executeReport.execute(efwDirectory, efwFile, parametersJSON);
            String templateData = list.get(0);
            dirPath = list.get(1);
            serviceLoadView.addObject("templateData", templateData);
        }

        serviceLoadView.setViewName("serviceLoadView");


        JsonObject attributeValue = ResponseUtils.newReportContentAsJson(efwsr);

        logger.debug("Parameters of the report are " + attributeValue);
        serviceLoadView.addObject("reportParameters", attributeValue);
        logger.debug("The Efwsr controller. The view is " + serviceLoadView);

        serviceLoadView.addObject("dir", dirPath.replace("\\", "\\\\"));
        return serviceLoadView;
    }

    private void checkPath(String path) {
        if ("".equalsIgnoreCase(path) || path.trim().length() < 0) {
            throw new ResourceNotFoundException("File does not exist in file system");
        }
    }

    private void setUnsetRefresh(HttpServletRequest request, String efwFile, String efwDirectory) {
        String refreshParam = request.getParameter("refresh");
        HttpSession session = request.getSession(false);
        if (efwFile != null && efwDirectory != null) {
            session.setAttribute("requestedReport", efwDirectory + File.separator + efwFile);
        } else {
            session.removeAttribute("requestedReport");
        }

        if (refreshParam != null) {
            session.setAttribute("refresh", "true");
            logger.debug("Attribute 'refresh' is set");
        } else {
            session.removeAttribute("refresh");
            logger.debug("Attribute 'refresh' is unset");
        }
    }

    private String getExportFromStatic() {
        JsonObject formData = new JsonObject();
        formData.addProperty("contentId", "Static/exportType");
        IComponent anySettingsGroovyProcessor = FactoryMethodWrapper
                .getTypedInstance("com.helicalinsight.export.components.AnySettingsGroovyProcessor", IComponent.class);
        return anySettingsGroovyProcessor.executeComponent(formData.toString());
    }
}