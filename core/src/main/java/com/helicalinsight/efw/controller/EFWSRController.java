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

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.io.FileOperationsUtility;
import com.helicalinsight.efw.utility.ExecuteReport;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.ResponseUtils;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;

/**
 * <p>
 * This class handles the execution of the efwsr files. Has methods with request
 * mappings /executeSavedReport.
 * </p>
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
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

        JSONObject parametersJSON = (JSONObject) JSONSerializer.toJSON(efwsr.getReportParameters());
        String dirPath;
        int index = efwFile.lastIndexOf(".");
        List<String> list = executeReport.execute(efwDirectory, efwFile, parametersJSON);
        String templateData = list.get(0);
        dirPath = list.get(1);
        serviceLoadView.addObject("templateData", templateData);
        serviceLoadView.setViewName("serviceLoadView");

        JSONObject attributeValue = ResponseUtils.reportContentAsJson(efwsr);

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
}