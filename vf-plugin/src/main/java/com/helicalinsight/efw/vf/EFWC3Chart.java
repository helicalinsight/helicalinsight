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

/**
 * Abstract Class used by C3 charts class
 * @author Abhishek
 * @since 1.0
 */

import net.sf.json.JSONObject;

public abstract class EFWC3Chart {

    abstract String getChartConfig(JSONObject chartData);

    abstract String dataConfiguration(JSONObject chartData);

    public String tooltipConfiguration(JSONObject chartData) {
        StringBuilder tooltipConfiguration = new StringBuilder();
        tooltipConfiguration.append("tooltip:{\r\n");

        if (chartData.has("TooltipShow")) {
            if (chartData.get("TooltipShow") instanceof String) {
                String isTooltipShow = chartData.getString("TooltipShow").trim();
                if (isTooltipShow.equalsIgnoreCase("false")) {
                    tooltipConfiguration.append("show: false,");
                }
            }
        }
        tooltipConfiguration.append("},\r\n");
        return tooltipConfiguration.toString();
    }

    /*
     * Returns the configuration related to grid lines for axis type charts
     *
     * @param charData: The data related to chart configurations
     *
     * @return Returns String containing configuration related to grid lines of
     * a chart
     *
     */
    public String gridLines(JSONObject chartData) {

        StringBuilder gridLines = new StringBuilder();
        StringBuilder gridLinesXAxis = new StringBuilder();
        StringBuilder gridLinesYAxis = new StringBuilder();

        gridLines.append("grid:{\r\n");
        if (chartData.has("gridLinesXAxisShow")) {
            if (chartData.get("gridLinesXAxisShow") instanceof String) {
                String isGridLineXAxisPresent = chartData.getString("gridLinesXAxisShow").trim();
                if (isGridLineXAxisPresent.equalsIgnoreCase("true")) {
                    gridLinesXAxis.append("x:{\r\n");
                    gridLinesXAxis.append("show:true,\r\n");
                    gridLinesXAxis.append("},\r\n");
                }
            }
        }
        if (chartData.has("gridLinesYAxisShow")) {
            if (chartData.get("gridLinesYAxisShow") instanceof String) {
                String isGridLineYAxisPresent = chartData.getString("gridLinesYAxisShow").trim();
                if (isGridLineYAxisPresent.equalsIgnoreCase("true")) {
                    gridLinesYAxis.append("y:{\r\n");
                    gridLinesYAxis.append("show:true,\r\n");
                    gridLinesYAxis.append("},\r\n");
                }
            }
        }
        gridLines.append(gridLinesXAxis).append(gridLinesYAxis).append("},\r\n");
        return gridLines.toString();
    }

    public String colorPattern(JSONObject chartData) {

        ChartUtils chartUtilsObject = new ChartUtils();
        StringBuilder colorPatterns = new StringBuilder();
        colorPatterns.append("color:{");

        // ColorPattern Tag and value inside tag
        if (chartData.has("ColorPattern")) {
            if (chartData.get("ColorPattern") instanceof String) {
                String[] chartTagColorPatternArray = chartData.getString("ColorPattern").trim().split(",");
                int chartTagColorPatternArrayLength = chartTagColorPatternArray.length;
                for (int i = 0; i < chartTagColorPatternArrayLength; i++) {
                    chartTagColorPatternArray[i] = chartTagColorPatternArray[i].trim();
                }
                if (chartTagColorPatternArray[0].length() > 0) {
                    colorPatterns.append("pattern:[");
                    colorPatterns.append(chartUtilsObject.array2csv(chartTagColorPatternArray)).append("],\r\n");
                }
            }
        }
        colorPatterns.append("},\r\n");
        return colorPatterns.toString();
    }

    public String legendConfiguration(JSONObject chartData) {
        ChartUtils chartUtilsObject = new ChartUtils();
        StringBuilder legendConf = new StringBuilder();
        legendConf.append("legend:{");
        if (chartData.has("LegendHide")) {
            if (chartData.get("LegendHide") instanceof String) {
                String isLegendHide = chartData.getString("LegendHide").trim();
                if (isLegendHide.equalsIgnoreCase("true")) {
                    legendConf.append("hide:");
                    legendConf.append("true,");
                } else {
                    String[] legendConfArray = isLegendHide.split(",");
                    int chartTagLegendArrayLength = legendConfArray.length;
                    for (int i = 0; i < chartTagLegendArrayLength; i++) {
                        legendConfArray[i] = legendConfArray[i].trim();
                    }
                    if (legendConfArray[0].length() > 0) {
                        legendConf.append("hide:");
                        legendConf.append("[");
                        legendConf.append(chartUtilsObject.array2csv(legendConfArray)).append("]");
                        legendConf.append(",\r\n");
                    }
                }
            }
        }
        if (chartData.has("LegendPosition")) {
            if (chartData.get("LegendPosition") instanceof String) {
                String legendPosition = chartData.getString("LegendPosition").trim();
                if (legendPosition.length() > 0) {
                    legendConf.append("position:");
                    legendConf.append("'");
                    legendConf.append(legendPosition);
                    legendConf.append("'");
                    legendConf.append(",\r\n");
                }
            }
        }
        legendConf.append("},\r\n");
        return legendConf.toString();
    }

    /*
     * Return configuration related axis for axis type charts
     *
     * @param charData: The data related to chart configurations
     *
     * @return Returns String containing configuration related to Axis
     * Properties of a chart
     *
     */
    public StringBuilder axisProperties(JSONObject chartdata) {
        StringBuilder axisConfiguration = new StringBuilder();

        if (chartdata.has("Dimensions") && (chartdata.get("Dimensions") instanceof String)) {
            if (chartdata.get("Dimensions").toString().trim().length() > 0) {
                axisConfiguration.append("axis:{\r\n").append("x:{\r\n").append("type:'category'\r\n").append("},\r\n")
                        .append("},\r\n");
            }
        } else {
            if (chartdata.has("TimeSeries") && chartdata.getString("TimeSeries").trim().equalsIgnoreCase("true")) {
                axisConfiguration.append("axis:{\r\n").append("x:{\r\n").append("type:'timeseries'\r\n")
                        .append("},\r\n").append("},\r\n");
            }
            axisConfiguration.append("axis:{\r\n").append("x:{\r\n").append("type:'indexed'\r\n").append("},\r\n")
                    .append("},\r\n");
        }
        return axisConfiguration;
    }
}
