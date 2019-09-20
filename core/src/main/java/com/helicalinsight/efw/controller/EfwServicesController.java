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

import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.components.EfwdReaderUtility;
import com.helicalinsight.efw.components.GlobalXmlReaderUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.DirectoryLoaderProxy;
import com.helicalinsight.efw.utility.JdbcUrlFormatUtility;
import com.helicalinsight.efw.utility.SettingXmlUtility;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by author on 24-Jan-15.
 *
 * @author Rajasekhar
 * @author Muqtar
 * @author Somen
 */
@Controller
public class EfwServicesController {

    private static final Logger logger = LoggerFactory.getLogger(EfwServicesController.class);

    @RequestMapping(value = "/services", method = {RequestMethod.GET, RequestMethod.POST})
    public void service(@RequestParam("type") String type, @RequestParam("serviceType") String serviceType,
                        @RequestParam("service") String service, HttpServletRequest request,
                        HttpServletResponse response) throws IOException {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("type", type);
        parameters.put("serviceType", serviceType);
        parameters.put("service", service);

        String formData = request.getParameter("formData");

        if (formData != null) {
            parameters.put("formData", formData);
        } else {
            formData = new JSONObject().toString();
        }

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        JSONObject formJson = JSONObject.fromObject(formData);
        String requestedSessionId = ControllerUtils.getSessionId(request);

       /* if (requestedSessionId == null) {
            throw new RuntimeException("The session id is not available to the currently logged in user.");
        }
*/
        String serviceClass = ControllerUtils.getServiceClass(type, serviceType, service);

        boolean isAjax = ControllerUtils.isAjax(request);

        try {
            String result = ControllerUtils.getComponentResult(type, serviceType, service, formJson, serviceClass);
            ControllerUtils.handleSuccess(response, isAjax, result);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    @RequestMapping(value = "/createDataSource", method = {RequestMethod.POST, RequestMethod.GET})
    public void createDataSource(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> model = new HashMap<>();

        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            JSONObject dataSourcesList = SettingXmlUtility.getDataSourcesJson(true);

            JdbcUrlFormatUtility urlFormatUtility = ApplicationContextAccessor.getBean(JdbcUrlFormatUtility.class);

            JSONObject jsonOfDrivers = urlFormatUtility.getJsonOfDrivers();
            model.put("driversList", jsonOfDrivers.getJSONArray("drivers"));
            model.put("dataSourceTypes", dataSourcesList.getJSONArray("dataSources"));

            JSONObject result;
            result = new JSONObject();
            result.accumulateAll(model);

            ControllerUtils.handleSuccess(response, isAjax, result.toString());
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/getResources", method = {RequestMethod.POST, RequestMethod.GET})
    public void getDirectories(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Received a request to get the resources information from the Solution " + "directory.");
        }
        long now = System.currentTimeMillis();
        long later;
        String extensions = request.getParameter("extensions");
        List listOfKeys = null;
        if (extensions != null) {
            if ("[]".equals(extensions) || "".equals(extensions) || extensions.trim().length() == 0) {
                throw new IllegalArgumentException("When provided, the parameter 'extensions'  " + "should not be " +
                        "empty.");
            }
            JSONArray jsonArray;
            try {
                jsonArray = JSONArray.fromObject(extensions);
            } catch (Exception e) {
                throw new EfwServiceException("The parameter extensions is expected to be of " + "type json array.");
            }
            listOfKeys = jsonArray.subList(0, jsonArray.size());
        }

        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            String resources = new DirectoryLoaderProxy(listOfKeys).getResources(true);
            JSONArray resourcesJsonArray = JSONArray.fromObject(resources);
            ControllerUtils.replaceFilePath(resourcesJsonArray);
            later = System.currentTimeMillis();

            if (logger.isDebugEnabled()) {
                logger.debug("It took nearly " + ((later - now) / 1000) + " seconds to complete " +
                        "the IO operations to get the resources information.");
            }
            ControllerUtils.handleSuccess(response, isAjax, resourcesJsonArray.toString());
        } catch (Exception exception) {
            later = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug("There was some problem. Could not read the resources. It took " +
                        "nearly " + ((later - now) / 1000) + " seconds to complete the IO " +
                        "operations to get the resources information.");
            }
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    @RequestMapping(value = "/listDataSources", method = {RequestMethod.GET, RequestMethod.POST})
    public void listDataSources(@RequestParam("classifier") String classifier, HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        if ("".equals(classifier) || classifier.trim().length() == 0) {
            throw new RequiredParameterIsNullException("The parameter classifier is empty.");
        }

        boolean isAjax = ControllerUtils.isAjax(request);
        boolean onlyEfwds = false;
        boolean onlyGlobal = false;
        boolean both = false;
        String type = null;
        try {
            if ("efwd".equalsIgnoreCase(classifier)) {
                onlyEfwds = true;
                type = request.getParameter("type");
                if (type != null) {
                    if ("".equals(type) || type.trim().length() == 0) {
                        throw new RequiredParameterIsNullException("The parameter type is empty.");
                    }
                }
            } else if ("global".equalsIgnoreCase(classifier)) {
                onlyGlobal = true;
            } else if ("all".equalsIgnoreCase(classifier)) {
                both = true;
            } else {
                throw new IllegalArgumentException("The parameter classifier should be either " +
                        "'efwd' or 'global' or " + "'all'.");
            }

            JSONObject connections;
            connections = new JSONObject();
            List<JSONObject> dataSources = null;

            if (both || onlyEfwds) {
                JSONArray extensions = new JSONArray();
                extensions.add("efwd");
                EfwdReaderUtility efwdReaderUtility = new EfwdReaderUtility(extensions);
                dataSources = efwdReaderUtility.getAllEfwdConnections(type);
            }

            if (both || onlyGlobal) {
                if (dataSources == null) {
                    dataSources = new ArrayList<>();
                }
                GlobalXmlReaderUtility globalXmlReaderUtility = ApplicationContextAccessor.getBean
                        (GlobalXmlReaderUtility.class);
                globalXmlReaderUtility.addDataSources(dataSources, DataSourceSecurityUtility.READ);
            }

            connections.accumulate("dataSources", dataSources);
            ControllerUtils.handleSuccess(response, isAjax, connections.toString());
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    @RequestMapping(value = "/datasource-create", method = {RequestMethod.GET, RequestMethod.POST})
    public String dataSourceCreate() {
        return "datasource-create";
    }

    @RequestMapping(value = "/datasource-edit", method = {RequestMethod.GET, RequestMethod.POST})
    public String dataSourceEdit() {
        return "datasource-edit";
    }

    @RequestMapping(value = "/datasource-share", method = {RequestMethod.GET, RequestMethod.POST})
    public String dataSourceShare() {
        return "datasource-share";
    }

    @RequestMapping(value = "/efw-template", method = {RequestMethod.GET, RequestMethod.POST})
    public String efwTemplate() {
        return "efw-template";
    }

}
