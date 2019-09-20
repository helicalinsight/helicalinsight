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

import net.sf.json.JSONObject;

public class C3SplineChart extends EFWC3Chart {

    public String getChartConfig(JSONObject chartData) {

        StringBuilder axisConfiguration = null;
        String colorPatternConfiguration = null;
        String legendConfiguration = null;

        axisConfiguration = axisProperties(chartData);
        String dataConf = dataConfiguration(chartData);

        colorPatternConfiguration = colorPattern(chartData);

        legendConfiguration = legendConfiguration(chartData);
        String tooltipConf = tooltipConfiguration(chartData);

        String gridConf = gridLines(chartData);

        StringBuilder chartConfiguration = new StringBuilder();
        chartConfiguration.append("{\r\n").append("	bindto:chartElement, \r\n").append(dataConf);
        chartConfiguration.append(axisConfiguration).append(colorPatternConfiguration).append(tooltipConf)
                .append(gridConf).append(legendConfiguration).append("}");

        String dynamicJson = chartConfiguration.toString().replace("\"chartElement\"", "chartElement")
                .replace("\"data\"", "data");
        StringBuilder c3conf = new StringBuilder();
        c3conf.append("var chart = c3.generate(").append(dynamicJson).append(");");
        return c3conf.toString();
    }

    @Override
    public String dataConfiguration(JSONObject chartData) {
        // TODO Auto-generated method stub

        StringBuilder measuresStack = new StringBuilder();
        StringBuilder dimensions = new StringBuilder();
        StringBuilder measures = new StringBuilder();
        StringBuilder xyAxis = new StringBuilder();
        StringBuilder dataConfiguration = new StringBuilder();

        ChartUtils chartUtilsObject = new ChartUtils();
        if (chartData.has("StackMeasures")) {
            measuresStack.append(chartData.getString("StackMeasures").trim());
            measuresStack.append("groups:[");
            measuresStack.append(measuresStack);
            measuresStack.append("],");
        } else {
            measuresStack.append("");
        }

        // check for dimensions
        if (chartData.has("Dimensions") && (chartData.get("Dimensions") instanceof String)) {
            dimensions.setLength(0);
            dimensions.append(chartData.getString("Dimensions").trim());
            String[] chartTagDimensionsArray = dimensions.toString().split(",");

            int chartTagDimensionsArrayLength = chartTagDimensionsArray.length;
            for (int i = 0; i < chartTagDimensionsArrayLength; i++) {
                chartTagDimensionsArray[i] = chartTagDimensionsArray[i].trim();
            }
            dimensions.setLength(0);
            // empty tags and tag with only whitespace in dimensions
            if (chartTagDimensionsArray[0].length() > 0) {
                dimensions.append("'" + chartTagDimensionsArray[0] + "'");
            } else {
                dimensions.setLength(0);
            }
        } else {
            dimensions.setLength(0);
        }

        // check for Measures
        if (chartData.has("Measures") && (chartData.get("Measures") instanceof String)) {
            measures.setLength(0);
            measures.append(chartData.getString("Measures").trim());
            String[] chartTagMeasuresArray = measures.toString().split(",");
            int chartTagMeasuresArrayLength = chartTagMeasuresArray.length;
            for (int i = 0; i < chartTagMeasuresArrayLength; i++) {
                chartTagMeasuresArray[i] = chartTagMeasuresArray[i].trim();
            }
            measures.setLength(0);
            // check for empty tag and tag with whitespace in measures
            if (chartTagMeasuresArray[0].length() > 0) {
                measures.append(chartUtilsObject.array2csv(chartTagMeasuresArray));
            } else {
                measures.setLength(0);
            }
        } else {
            measures.setLength(0);
        }

        if ((dimensions.length() != 0) && (measures.length() != 0)) {
            xyAxis.append("keys:{\r\n x:").append(dimensions).append(",\r\n");
            xyAxis.append("value: [").append(measures).append("],\r\n},");
        } else if ((dimensions.length() == 0) && (measures.length() != 0)) {
            xyAxis.append("keys:{\r\n");
            xyAxis.append("value: [").append(measures).append("],\r\n},");
        } else if ((dimensions.length() != 0) && (measures.length() == 0)) {
            xyAxis.append("keys:{\r\n x:").append(dimensions).append(",\r\n");
            xyAxis.append(",\r\n},");
        } else {
            xyAxis.setLength(0);
        }

        dataConfiguration.append("data: {\r\n").append("        json: data,").append(xyAxis).append(measuresStack);
        dataConfiguration.append("        type: 'spline',\r\n").append("    },\r\n");
        return dataConfiguration.toString();
    }

}
