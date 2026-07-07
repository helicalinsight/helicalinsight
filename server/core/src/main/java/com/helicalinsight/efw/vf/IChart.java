package com.helicalinsight.efw.vf;

import com.google.gson.JsonArray;
import net.sf.json.JSONObject;

/**
 * Implementation classes method returns the java script and data to be rendered as chart
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
 * @since 1.0
 */
public interface IChart {

    public String getChartDetails(JSONObject chartData, JsonArray data, String chartId);

}