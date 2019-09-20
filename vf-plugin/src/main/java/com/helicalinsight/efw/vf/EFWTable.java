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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the IChart interface
 *
 * @author Abhishek
 * @since 1.0
 */

public class EFWTable implements IChart {

    private static final Logger logger = LoggerFactory.getLogger(EFWChart.class);

    @Override
    public String getChartDetails(JSONObject chartData, JsonArray data, String chartId) {
        StringBuilder tableScript = new StringBuilder();
        StringBuilder table = new StringBuilder();

        table.append("var table = document.createElement('table');\r\n" + "table.id = chartElement + \"_table\";\r\n"
                + "table.className = \"table table-striped table-bordered display\";\r\n"
                + "$(\"#\"+chartElement).append(table);\r\n" + "var chartElem = \"#\"+chartElement+\"_table\";");
        String tableconf = getChartConfig(chartData, data);

        tableScript.append("(function(dataset, chartElement){try{").append(table).append(tableconf).append("window[chartElement]=tableConfig;")
                .append("}catch(err){console.error(\"Error: \",err.message);}})(").append(data).append(",'chart_")
                .append(chartId).append("')");

        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                    "The %s has prepared the JavaScript to be " + "rendered " + "by" + " the browser successfully.",
                    this.getClass()));
        }
        return tableScript.toString();
    }

    private String getChartConfig(JSONObject chartData, JsonArray data) {
        StringBuilder tableScript = new StringBuilder();
        JSONObject tableConfiguration = new JSONObject();
        tableConfiguration.put("data", "dataset");
        dataConfigurationException(chartData, data, tableConfiguration);
        tableConfiguration.put("order", new JSONArray());
        tableScript.append("var tableConfig = $(chartElem).DataTable(").append(tableConfiguration.toString())
                .append(");");
        String dynamicJson = tableScript.toString().replace("\"dataset\"", "dataset");
        return dynamicJson;
    }

    private void dataConfigurationException(JSONObject chartData, JsonArray data, JSONObject tableConfiguration) {
        // TODO Auto-generated method stub
        JSONArray columnConfiguration = new JSONArray();
        JSONObject measureKeys = new JSONObject();
        JSONObject dimensionKeys = new JSONObject();

        if (chartData.has("Dimensions") && (chartData.get("Dimensions") instanceof String)) {
            String[] chartTagDimensionsArray = chartData.getString("Dimensions").trim().split(",");
            int chartTagDimensionsArrayLength = chartTagDimensionsArray.length;
            for (int i = 0; i < chartTagDimensionsArrayLength; i++) {
                chartTagDimensionsArray[i] = chartTagDimensionsArray[i].trim();
                if (!chartTagDimensionsArray[i].isEmpty()) {
                    dimensionKeys.put("title", chartTagDimensionsArray[i]);
                    dimensionKeys.put("data", chartTagDimensionsArray[i]);
                    columnConfiguration.add(dimensionKeys);
                    dimensionKeys.clear();
                }
            }
        }

		/* check for Measures */
        if (chartData.has("Measures") && (chartData.get("Measures") instanceof String)) {
            String[] chartTagMeasuresArray = chartData.getString("Measures").trim().split(",");
            int chartTagMeasuresArrayLength = chartTagMeasuresArray.length;
            for (int i = 0; i < chartTagMeasuresArrayLength; i++) {
                chartTagMeasuresArray[i] = chartTagMeasuresArray[i].trim();
                if (!chartTagMeasuresArray[i].isEmpty()) {
                    String formatter = "";
                    StringBuilder formaterFunction = new StringBuilder();
                    if (chartData.has(chartTagMeasuresArray[i])
                            && (chartData.get(chartTagMeasuresArray[i]) instanceof String)) {
                        formatter = chartData.getString(chartTagMeasuresArray[i]).trim();
                        formaterFunction.append("$.fn.dataTable.render.number( ',', '.', 2, ").append(formatter)
                                .append("),");
                        measureKeys.put("render", formaterFunction.toString());
                    }
                    measureKeys.put("title", chartTagMeasuresArray[i]);
                    measureKeys.put("data", chartTagMeasuresArray[i]);
                    measureKeys.put("className", "dt-right");
                    columnConfiguration.add(measureKeys);
                    measureKeys.clear();
                }
            }
        }

        if (columnConfiguration.isEmpty()) {
            measureKeys.put("title", "Empty Table");
            measureKeys.put("data", "null");
            measureKeys.put("defaultContent", "<i>Not set</i>");
            columnConfiguration.add(measureKeys);
        }
        tableConfiguration.put("columns", columnConfiguration);
    }
}
