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
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implements the IChart interface
 *
 * @author Abhishek
 * @since 1.0
 */

public class EFWCrossTab implements IChart {

    private static final Logger logger = LoggerFactory.getLogger(EFWChart.class);

    /**
     * Returns the script to be rendered by the browser along with the data
     *
     * @param chartData The chart data that consists of script tag
     * @param data      The chart data
     * @return Returns The script to be rendered by the browser
     */
    @Override
    public String getChartDetails(JSONObject chartData, JsonArray data, String chartId) {
        StringBuilder crossTabScript = new StringBuilder();
        String crossTabconf = getChartConfig(chartData);
        StringBuilder crosstabIdentifier = new StringBuilder();
        crosstabIdentifier.append("var chartElement = \"#\"+chartElementId;");
        crossTabScript.append("(function(data, chartElementId){try{").append(crosstabIdentifier).append(crossTabconf).append("window[chartElementId] = pgridwidget;")
                .append("}catch(err){console.error(\"Error: \",err.message);}})(").append(data).append(",'chart_")
                .append(chartId).append("')");

        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                    "The %s has prepared the JavaScript to be " + "rendered " + "by" + " the browser successfully.",
                    this.getClass()));
        }
        return crossTabScript.toString();
    }

    private String getChartConfig(JSONObject chartData) {
        JSONObject crossTabConfiguration = new JSONObject();
        StringBuilder crossTabConf = new StringBuilder();
        JSONArray fieldConfiguration = fieldConfiguration(chartData);

        crossTabConfiguration.put("dataSource", "data");
        crossTabConfiguration.put("canMoveFields", "false");
        crossTabConfiguration.put("dataHeadersLocation", "columns");
        crossTabConfiguration.put("theme", "gray");
        crossTabSizeConfiguration(chartData, crossTabConfiguration);
        crossTabConfiguration.put("fields", fieldConfiguration);
        dataConfiguration(chartData, crossTabConfiguration);

        crossTabConf.append("var pgridwidget = new orb.pgridwidget(").append(crossTabConfiguration.toString())
                .append(");");
        crossTabConf.append("pgridwidget.render(document.querySelector(chartElement));");
        String dynamicJson = crossTabConf.toString().replace("\"data\"", "data").replace("\"false\"", "false")
                .replace("\"'white'\"", "'white'");
        return dynamicJson;
    }

    private JSONArray fieldConfiguration(JSONObject chartData) {
        JSONArray aggregateArray = new JSONArray();
        if (chartData.has("Aggregator") && chartData.get("Aggregator") instanceof Object) {
            JSONObject aggregators = chartData.getJSONObject("Aggregator");
            List<String> aggregatorKeyList = JsonUtils.getKeys(aggregators);

            for (String aggregateFunctionName : aggregatorKeyList) {
                String keys = aggregators.getString(aggregateFunctionName);
                prepareAgregatedConfiguration(aggregateArray, aggregateFunctionName, keys);
            }
        }
        return aggregateArray;
    }

    private void prepareAgregatedConfiguration(JSONArray aggregateArray, String aggregateFunctionName, String keys) {

        String[] keysArray = keys.split(",");
        for (int i = 0; i < keysArray.length; i++) {
            JSONObject filedConf = new JSONObject();
            filedConf.put("name", keysArray[i]);
            filedConf.put("aggregateFunc", aggregateFunctionName.toLowerCase());
            aggregateArray.add(filedConf);
        }
    }

    private void crossTabSizeConfiguration(JSONObject chartData, JSONObject crossTabConfiguration) {
        String chartWidth = null;
        String chartHeight = null;
        if (chartData.has("Width") && chartData.get("Width") instanceof String) {
            chartWidth = chartData.getString("Width").trim();
        }
        if (chartData.has("Height") && chartData.get("Height") instanceof String) {
            chartHeight = chartData.getString("Height").trim();
        }
        crossTabConfiguration.put("width", chartWidth);
        crossTabConfiguration.put("height", chartHeight);
    }

    public void dataConfiguration(JSONObject chartData, JSONObject crossTabConfiguration) {
        // TODO Auto-generated method stub
        JSONArray measureKeys = new JSONArray();

		/* check for Measures */
        if (chartData.has("Measures") && (chartData.get("Measures") instanceof String)) {
            String[] chartTagMeasuresArray = chartData.getString("Measures").trim().split(",");
            int chartTagMeasuresArrayLength = chartTagMeasuresArray.length;
            for (int i = 0; i < chartTagMeasuresArrayLength; i++) {
                chartTagMeasuresArray[i] = chartTagMeasuresArray[i].trim();
                measureKeys.add(chartTagMeasuresArray[i]);
            }
        }
        crossTabConfiguration.put("data", measureKeys);
        rowConfiguration(chartData, crossTabConfiguration);
        colConfiguration(chartData, crossTabConfiguration);
    }

    private void rowConfiguration(JSONObject chartData, JSONObject crossTabConfiguration) {
        JSONArray rowKeys = new JSONArray();
        JSONObject subTotal = new JSONObject();
        subTotal.put("visible", "true");
        subTotal.put("collapsible", "true");
        subTotal.put("collapsed", "true");
        if (chartData.has("Rows") && chartData.get("Rows") instanceof String) {
            String[] rowsArray = chartData.getString("Rows").trim().split(",");
            int numberOfRows = rowsArray.length;
            for (int i = 0; i < numberOfRows; i++) {
                rowsArray[i] = rowsArray[i].trim();
                rowKeys.add(rowsArray[i]);
            }
        }
        crossTabConfiguration.put("subTotal", subTotal);
        crossTabConfiguration.put("rows", rowKeys);
    }

    private void colConfiguration(JSONObject chartData, JSONObject crossTabConfiguration) {
        JSONArray columnKeys = new JSONArray();
        if (chartData.has("Columns") && chartData.get("Columns") instanceof String) {
            String[] columnsArray = chartData.getString("Columns").trim().split(",");
            int numberOfColumns = columnsArray.length;
            for (int i = 0; i < numberOfColumns; i++) {
                columnsArray[i] = columnsArray[i].trim();
                columnKeys.add(columnsArray[i]);
            }
        }
        crossTabConfiguration.put("columns", columnKeys);
    }

}
