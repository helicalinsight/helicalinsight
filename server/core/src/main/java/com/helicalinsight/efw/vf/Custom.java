package com.helicalinsight.efw.vf;

import com.google.gson.JsonArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the IChart interface
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
 * @since 1.0
 */
public class Custom implements IChart {

    private static final Logger logger = LoggerFactory.getLogger(Custom.class);

    /**
     * Returns the script to be rendered by the browser along with the data
     *
     * @param chartData The chart data that consists of script tag
     * @param data      The chart data
     * @return Returns The script to be rendered by the browser
     */
    public String getChartDetails(JSONObject chartData, JsonArray data, String chartId) {
        String chartTagScript = chartData.getString("script");
        chartTagScript = "(function(data, chartElement){" + chartTagScript + "})(" + data + ")";
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The %s has prepared the JavaScript to be rendered " +
                    "byw the browser successfully.", this.getClass()));
        }
        return chartTagScript;
    }
}