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

package com.helicalinsight.validation.filter;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.filters.FilterUtils;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.validation.IValidation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component("validationFilter")
public class ValidationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ValidationFilter.class);

    private static boolean allowValidation = false;

    private static String configurationsDirectory;

    private static JSONObject validationXmlJson;

    private static JSONArray urlExcludePatterns;

    public ValidationFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //NOP
    }

    @PostConstruct
    public void initialize() {
        configurationsDirectory = FilterUtils.getValidationConfigurationFilesDirectory();
        logger.info(configurationsDirectory + " is updated");
        validationXmlJson = ResourceProcessorFactory.getIProcessor().getJSONObject(FilterUtils.getValidationFilePath(),
                false);
        urlExcludePatterns = FilterUtils.getExcludeUlrPattern();
        if (validationXmlJson.has("validationEnabled")) {
            allowValidation = validationXmlJson.getBoolean("validationEnabled");
            logger.info("Validation is set to " + allowValidation);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        try {
            if (allowValidation) {
                HttpServletRequest httpRequest = ((HttpServletRequest) request);
                HttpServletResponse httpResponse = ((HttpServletResponse) response);
                String requestedUrl = httpRequest.getServletPath();
                logger.info("The validation url is " + requestedUrl);
                //adding html to requestedUrl if not contains html
                if (!requestedUrl.toLowerCase().endsWith(".html")) {
                    requestedUrl = requestedUrl + ".html";
                }

                /********EXCLUDE THE PATTERN FOR .js, .css,.jpeg,.png etc********************/
                if (FilterUtils.isExcludePattern(urlExcludePatterns, requestedUrl)) {
                    chain.doFilter(request, response);
                    return;
                }

                /********EXCLUDE THE PATTERN FOR .js, .css,.jpeg,.png etc********************/

                /********MAP THE PATTERN FOR URL PATTERN*************************************/
                JSONObject mapping = validationXmlJson.optJSONObject("mapping");
                String defaultValidationClass = mapping.getString("@class");
                JSONArray urlPatterns = mapping.getJSONArray("url");
                IValidation iValidation;
                for (int i = 0; i < urlPatterns.size(); i++) {
                    JSONObject jsonObject = JSONObject.fromObject(urlPatterns.getJSONObject(i));
                    String urlPattern = jsonObject.getString("@pattern");
                    String validationClass = jsonObject.optString("@class");
                    if (urlPattern.equals(requestedUrl)) {
                        boolean isAjax = ControllerUtils.isAjax(httpRequest);
                        if (validationClass == null || validationClass.trim().length() == 0) {
                            validationClass = defaultValidationClass;
                        }
                        iValidation = FactoryMethodWrapper.getTypedInstance(validationClass, IValidation.class);
                        jsonObject.accumulate("definitionFolder", configurationsDirectory);
                        JSONObject httpRequestJson = JsonUtils.httpRequestToFormData(httpRequest);
                        if (("/services.html".equalsIgnoreCase(urlPattern))) {
                            httpRequestJson = JsonUtils.httpRequestWithServiceAndFormData(httpRequest);
                        }

                        if (httpRequestJson != null) {
                            if (iValidation != null && iValidation.isValid(httpRequestJson, jsonObject)) {
                                chain.doFilter(request, response);
                                logger.info("Returning true from validation");
                                return;
                            } else {
                                JSONObject model = new JSONObject();
                                model.put("status", 0);

                                JSONObject result = new JSONObject();
                                result.put("message", getValidationMessage(jsonObject.getJSONObject("message")));
                                model.put("response", result);
                                if (isAjax) {
                                    ControllerUtils.handleSuccess(httpResponse, true, model.toString());
                                } else {
                                    request.setAttribute("message", result.getString("message"));
                                    request.getRequestDispatcher("WEB-INF/jsp/errorPage.jsp").forward(request,
                                            response);
                                }

                                return;
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception exception) {
            logger.error("Exception occurred ", exception);

        }
        chain.doFilter(request, response);
    }

    public void destroy() {
    }

    public String getValidationMessage(JSONObject jsonObject) {
        StringBuilder message = new StringBuilder();

        List<String> validationKeys = JsonUtils.getKeys(jsonObject);
        for (String jsonKey : validationKeys) {
            Object sampleObject = jsonObject.get(jsonKey);

            if (sampleObject instanceof JSONObject) {
                JSONObject sampleJson = (JSONObject) sampleObject;
                message.append("\n").append(getValidationMessage(sampleJson));
            } else if (sampleObject instanceof JSONArray) {
                JSONArray sampleArray = (JSONArray) sampleObject;
                for (int index = 0; index < sampleArray.size(); index++) {
                    message.append(sampleArray.getString(index)).append("\n");
                }
            } else if (sampleObject instanceof String) {
                message.append(sampleObject.toString()).append("\n");
            }
        }
        return message.toString();
    }
}