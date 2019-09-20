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

package com.helicalinsight.efw.vf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.QueryExecutor;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.ApplicationException;
import com.helicalinsight.efw.exceptions.DuplicateChartIdException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.validator.ResourceValidator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible to create JSONObject to plot chart.
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 */
public class ChartService {

    private static final Logger logger = LoggerFactory.getLogger(ChartService.class);
    private final String data;
    private final String settingPath;
    private final ApplicationProperties applicationProperties;

    /**
     * @param data                  The http request data
     * @param applicationProperties Instance of the singleton class of the application
     */
    public ChartService(String data, ApplicationProperties applicationProperties) {
        if (data != null) {
            this.data = data;
        } else {
            throw new RequiredParameterIsNullException("The required parameter data is missing");
        }
        this.settingPath = applicationProperties.getSettingPath();
        this.applicationProperties = applicationProperties;
    }

    /**
     * This method is responsible to read efwvf File and create JSONObject which
     * contains chart data and script to be rendered. Before that it validate efwvf file.
     *
     * @return The data along with the script to be rendered by the browser
     * @throws ApplicationException
     */
    public JSONObject getChartData() throws ApplicationException {
        JSONObject parameterJsonObject = (JSONObject) JSONSerializer.toJSON(data);
        String rootPath = parameterJsonObject.getString("dir");
        String vfFile = parameterJsonObject.getString("vf_file");
        String vf_id = parameterJsonObject.getString("vf_id");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("dir", rootPath);
        parameters.put("vf_file", vfFile);
        parameters.put("vf_id", vf_id);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        String absolutePath = applicationProperties.getSolutionDirectory();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();

        int vfId = Integer.parseInt(vf_id);

        JSONObject vfJsonObject;

        JSONObject resultJsonObject = new JSONObject();
        int dataSourceId;

        JSONObject chartData = new JSONObject();

        String completeVfFile = absolutePath + File.separator + rootPath + File.separator + vfFile;
        if (logger.isDebugEnabled()) {
            logger.debug("The complete path of efwvf file is " + completeVfFile);
        }
        vfJsonObject = processor.getJSONObject(completeVfFile, true);

        ResourceValidator resourceValidator = new ResourceValidator(vfJsonObject);
        boolean exists = resourceValidator.validateVf();

        JsonObject dataSetJson;
        JsonArray data = new JsonArray();
        if (!exists) {
            logger.error("Duplicate chart id in vf file");
            throw new DuplicateChartIdException("Duplicate Chart ID in VF File");
        } else {
            JSONArray chartsArray = vfJsonObject.getJSONArray("Charts");
            logger.debug("The size of charts array is " + chartsArray.size());
            for (int chartNumber = 0; chartNumber < chartsArray.size(); chartNumber++) {
                JSONObject chart = chartsArray.getJSONObject(chartNumber);
                int id = chart.getInt("@id");
                if (vfId == id) {
                    chartData = chart.getJSONObject("prop");
                    Object dataSource = chartData.get("DataSource");
                    logger.debug("The dataSource id is " + dataSource.toString());
                    if (dataSource instanceof JSONArray) {
                        dataSetJson = new JsonObject();
                        dataSetJson.addProperty("data", "No Data is available");
                        data.add(dataSetJson);
                        break;
                    } else {
                        dataSourceId = Integer.parseInt(chartData.getString("DataSource"));
                        JSONObject paramMapId = parameterJsonObject.accumulate("map_id", dataSourceId);
                        String dataMapId = paramMapId.toString();
                        QueryExecutor queryExecutor = new QueryExecutor(dataMapId, applicationProperties);
                        dataSetJson = queryExecutor.getResultSet();

                        if (dataSetJson.get("data") == null) {
                            logger.error("Please Provide the Parameters");
                            break;
                        } else {
                            data = dataSetJson.getAsJsonArray("data");
                        }
                    }
                }
            }

            ChartResource chartResource = new ChartResource(settingPath, data, chartData, vfId);
            resultJsonObject.accumulate("id", "chart_" + vfId);
            resultJsonObject.accumulate("script", chartResource.getScript());
            return resultJsonObject;
        }
    }

    /**
     * This method is responsible to read efwvf file and create JSONObject which
     * contains chart data to be rendered and script. Before that it validate efwvf file.
     *
     * @return The data along with the script to be rendered by the browser
     * @throws ApplicationException
     */
    public JsonObject getData() throws ApplicationException {
        JSONObject parameterJsonObject = (JSONObject) JSONSerializer.toJSON(data);
        String rootPath = parameterJsonObject.getString("dir");
        String absolutePath = applicationProperties.getSolutionDirectory();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        String vfFile = parameterJsonObject.getString("vf_file");

        int vfId = Integer.parseInt(parameterJsonObject.getString("vf_id"));

        JSONObject vfJsonObject;

        int dataSourceId;
        JSONObject chartData;

        String completeVfFile = absolutePath + File.separator + rootPath + File.separator + vfFile;
        vfJsonObject = processor.getJSONObject(completeVfFile, true);

        ResourceValidator resourceValidator = new ResourceValidator(vfJsonObject);
        boolean exists = resourceValidator.validateVf();

        JsonObject resultData = null;
        if (!exists) {
            logger.error("Duplicate chart id in vf file");
            throw new ApplicationException("Duplicate Chart ID in VF FIle");
        } else {
            JSONArray charts = vfJsonObject.getJSONArray("Charts");
            for (int chartNumber = 0; chartNumber < charts.size(); chartNumber++) {
                JSONObject chart = charts.getJSONObject(chartNumber);
                int id = chart.getInt("@id");
                if (vfId == id) {
                    chartData = chart.getJSONObject("prop");
                    Object dataSource = chartData.get("DataSource");
                    if (dataSource instanceof JSONArray) {
                        resultData = new JsonObject();
                        resultData.addProperty("data", "No Data is available");
                        break;
                    } else {
                        dataSourceId = Integer.parseInt(chartData.getString("DataSource"));
                        JSONObject mapIdParameter = parameterJsonObject.accumulate("map_id", dataSourceId);
                        String dataMapId = mapIdParameter.toString();
                        QueryExecutor queryExecutor = new QueryExecutor(dataMapId, applicationProperties);
                        resultData = queryExecutor.getResultSet();
                    }
                }
            }
            return resultData;
        }
    }
}