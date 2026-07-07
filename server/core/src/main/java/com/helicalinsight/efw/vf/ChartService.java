package com.helicalinsight.efw.vf;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
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
 * @since 1.0
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
    public JsonObject getChartData() throws ApplicationException {
        JsonObject parameterJsonObject = JsonParser.parseString(new Gson().toJson(data)).getAsJsonObject();
        String rootPath = parameterJsonObject.get("dir").getAsString();
        String vfFile = parameterJsonObject.get("vf_file").getAsString();
        String vf_id = parameterJsonObject.get("vf_id").getAsString();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("dir", rootPath);
        parameters.put("vf_file", vfFile);
        parameters.put("vf_id", vf_id);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        String absolutePath = applicationProperties.getSolutionDirectory();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();

        int vfId = Integer.parseInt(vf_id);

        JsonObject vfJsonObject;

        JsonObject resultJsonObject = new JsonObject();
        int dataSourceId;

        JsonObject chartData = new JsonObject();

        String completeVfFile = absolutePath + File.separator + rootPath + File.separator + vfFile;
        if (logger.isDebugEnabled()) {
            logger.debug("The complete path of efwvf file is " + completeVfFile);
        }
        vfJsonObject = processor.getJsonObject(completeVfFile, true);

        ResourceValidator resourceValidator = new ResourceValidator(vfJsonObject);
        boolean exists = resourceValidator.newValidateVf();

        JsonObject dataSetJson;
        JsonArray data = new JsonArray();
        if (!exists) {
            logger.error("Duplicate chart id in vf file");
            throw new DuplicateChartIdException("Duplicate Chart ID in VF File");
        } else {
            JsonArray chartsArray = vfJsonObject.getAsJsonArray("Charts");
            logger.debug("The size of charts array is " + chartsArray.size());
            for (int chartNumber = 0; chartNumber < chartsArray.size(); chartNumber++) {
                JsonObject chart = chartsArray.get(chartNumber).getAsJsonObject();
                int id = chart.get("@id").getAsInt();
                if (vfId == id) {
                    chartData = chart.getAsJsonObject("prop");
                    Object dataSource = chartData.get("DataSource");
                    logger.debug("The dataSource id is " + dataSource.toString());
                    if (dataSource instanceof JsonArray) {
                        dataSetJson = new JsonObject();
                        dataSetJson.addProperty("data", "No Data is available");
                        data.add(dataSetJson);
                        break;
                    } else {
                        dataSourceId = Integer.parseInt(chartData.get("DataSource").getAsString());
                        JsonObject paramMapId = GsonUtility.accumulateInt(parameterJsonObject,"map_id", dataSourceId);
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

            ChartResource chartResource = new ChartResource(settingPath, data, chartData, String.valueOf(vfId));
            GsonUtility.accumulate(resultJsonObject,"id", "chart_" + vfId);
            GsonUtility.accumulate(resultJsonObject,"script", chartResource.getScript());
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