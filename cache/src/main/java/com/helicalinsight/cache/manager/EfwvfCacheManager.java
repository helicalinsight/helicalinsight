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

package com.helicalinsight.cache.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.validator.ResourceValidator;
import com.helicalinsight.efw.vf.ChartResource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Somen
 *         Created by Somen on 6/1/2015.
 */
@Component
@Scope("prototype")
@SuppressWarnings("unused")
public class EfwvfCacheManager extends EfwdCacheManager {
    private static final Logger logger = LoggerFactory.getLogger(EfwvfCacheManager.class);

    private String vfFile;
    private int vfId;
    private JSONObject chartData;
    private ChartResource chartResource;

    public String getVfFile() {
        return vfFile;
    }

    @Override
    public void setRequestData(String requestData) {
        ApplicationProperties applicationProperties;
        applicationProperties = ApplicationProperties.getInstance();
        JSONObject parameterJsonObject = (JSONObject) JSONSerializer.toJSON(requestData);
        String rootPath = parameterJsonObject.getString("dir");
        String absolutePath = applicationProperties.getSolutionDirectory();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        String vfFile = parameterJsonObject.getString("vf_file");

        this.vfId = Integer.parseInt(parameterJsonObject.getString("vf_id"));

        JSONObject vfJsonObject;
        int dataSourceId = 0;
        this.chartData = new JSONObject();

        String completeVfFile = absolutePath + File.separator + rootPath + File.separator + vfFile;
        this.vfFile = completeVfFile;
        vfJsonObject = processor.getJSONObject(completeVfFile, true);

        ResourceValidator resourceValidator = new ResourceValidator(vfJsonObject);
        boolean exists = resourceValidator.validateVf();

        if (!exists) {
            logger.error("Duplicate chart id in vf file");
            return;
        } else {
            JSONArray chartsArray = vfJsonObject.getJSONArray("Charts");
            for (int chartNumber = 0; chartNumber < chartsArray.size(); chartNumber++) {
                JSONObject chart = chartsArray.getJSONObject(chartNumber);
                int id = chart.getInt("@id");
                if (this.vfId == id) {
                    this.chartData = chart.getJSONObject("prop");
                    Object dataSource = this.chartData.get("DataSource");
                    logger.debug("The dataSource id is " + dataSource.toString());
                    if (dataSource instanceof JSONArray) {
                        break;
                    } else {
                        dataSourceId = Integer.parseInt(chartData.getString("DataSource"));
                    }
                    break;
                }
            }
        }
        parameterJsonObject.accumulate("map_id", dataSourceId);
        logger.info("Calling the efwd manager class with the map_id" + dataSourceId +
                parameterJsonObject.toString());
        super.setRequestData(parameterJsonObject.toString());

    }

    @Override
    public boolean serveCachedContent(HttpServletRequest request, HttpServletResponse response,
                                      JsonObject fileContent) {
        response.setContentType(ControllerUtils.defaultContentType());
        response.setCharacterEncoding(ApplicationUtilities.getEncoding());
        PrintWriter out = null;
        logger.info("Serving cache file for visualizeData......");
        JsonArray jsonArray = fileContent.getAsJsonArray("data");
        JSONObject resultJsonObject = new JSONObject();
        resultJsonObject.accumulate("id", "chart_" + getVfId());
        setChartData(jsonArray);
        resultJsonObject.accumulate("script", getScript());

        if (fileContent.has("lastModified")) {
            resultJsonObject.accumulate("lastModified", fileContent.get("lastModified").getAsString());
        }

        try {
            out = response.getWriter();
            out.print(resultJsonObject);
        } catch (IOException ioe) {
            logger.error("Exception occurred during io", ioe);
            return false;
        } finally {
            ApplicationUtilities.closeResource(out);
        }
        return true;
    }

    public int getVfId() {
        return this.vfId;
    }

    public void setChartData(JsonArray data) {
        String settingPath = ApplicationProperties.getInstance().getSettingPath();
        chartResource = new ChartResource(settingPath, data, this.chartData, this.vfId);
    }

    public String getScript() {
        if (chartResource != null) {
            return this.chartResource.getScript();
        } else {
            return null;
        }
    }
}
