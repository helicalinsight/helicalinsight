package com.helicalinsight.efw.vf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ClassNotConfiguredException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Muqtar
 * @author Rajasekhar
 * @see ChartService
 */
public class ChartResource {

    private static final Logger logger = LoggerFactory.getLogger(ChartResource.class);
    private final String settingPath;
    private final JsonArray data;
    private  JSONObject chartData;
    private  JsonObject newChartData;
    private final String chartId;

    /**
     * <p>
     * Constructor that sets the instance variables
     * path
     * </p>
     *
     * @param settingPath The path to setting.xml
     * @param data        The query execution result
     * @param chartData   The properties of chart(The prop tag in Chart)
     */
    @Deprecated
    public ChartResource(String settingPath, JsonArray data, JSONObject chartData, String chartId) {
        this.settingPath = settingPath;
        this.data = data;
        this.chartData = chartData;
        this.chartId = chartId;
    }
    
    public ChartResource(String settingPath, JsonArray data, JsonObject newChartData, String chartId) {
        this.settingPath = settingPath;
        this.data = data;
        this.newChartData = newChartData;
        this.chartId = chartId;
    }
    /**
     * <p>
     * Responsibility of this method is create instance of a class which is
     * available in setting.xml file within Charts tag. Before that it checks
     * type of Charts node in setting.xml whether it is same as the type
     * coming from front end.
     * </p>
     *
     * @return Returns the data along with the chart script
     */
    public String getScript() {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject settingXMLJsonObject = processor.getJSONObject(settingPath, false);
        JSONArray settingXMLDataMap = settingXMLJsonObject.getJSONArray("Charts");
        String chartScript;
        IChart chartObject = null;
        String type = chartData.getString("type");
        if (logger.isDebugEnabled()) {
            logger.debug("The type of chart being rendered is " + type);
        }
        for (int count = 0; count < settingXMLDataMap.size(); count++) {
            if (type.equalsIgnoreCase(settingXMLDataMap.getJSONObject(count).getString("@type"))) {
                String clazz = settingXMLDataMap.getJSONObject(count).getString("@class");
                chartObject = (IChart) FactoryMethodWrapper.getUntypedInstance(clazz);
                break;
            }
        }

        if (chartObject == null) {
            throw new ClassNotConfiguredException("IChart implementation is not found");
        }

        chartScript = chartObject.getChartDetails(chartData, data, String.valueOf(chartId));
        return chartScript;
    }
    
    public String newGetScript() {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JsonObject settingXMLJsonObject = processor.getJsonObject(settingPath, false);
        JsonArray settingXMLDataMap = settingXMLJsonObject.getAsJsonArray("Charts");
        String chartScript;
        IChart chartObject = null;
        String type = newChartData.get("type").getAsString();
        if (logger.isDebugEnabled()) {
            logger.debug("The type of chart being rendered is " + type);
        }
        for (int count = 0; count < settingXMLDataMap.size(); count++) {
            if (type.equalsIgnoreCase(settingXMLDataMap.get(count).getAsJsonObject().get("@type").getAsString())) {
                String clazz = settingXMLDataMap.get(count).getAsJsonObject().get("@class").getAsString();
                chartObject = (IChart) FactoryMethodWrapper.getUntypedInstance(clazz);
                break;
            }
        }

        if (chartObject == null) {
            throw new ClassNotConfiguredException("IChart implementation is not found");
        }

        chartScript = chartObject.getChartDetails(chartData, data, String.valueOf(chartId));
        return chartScript;
    }

}