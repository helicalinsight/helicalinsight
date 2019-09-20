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

public class C3GaugeChart extends EFWC3Chart {

    public String getChartConfig(JSONObject chartData) {
        String dataConf = dataConfiguration(chartData);
        String tooltipConf = tooltipConfiguration(chartData);
        String legendConf = legendConfiguration(chartData);
        String colorPatternConf = colorPattern(chartData);
        String gaugeConf = gaugeProperties(chartData);
        StringBuilder chartConfiguration = new StringBuilder();
        chartConfiguration.append("{\r\n").append("	bindto:chartElement, \r\n").append(dataConf);
        chartConfiguration.append(colorPatternConf).append(tooltipConf).append(legendConf).append(gaugeConf)
                .append("}");

        String dynamicJson = chartConfiguration.toString().replace("\"chartElement\"", "chartElement")
                .replace("\"data\"", "data");
        StringBuilder c3conf = new StringBuilder();
        c3conf.append("var chart = c3.generate(").append(dynamicJson).append(");");

        return c3conf.toString();
    }

    public String gaugeProperties(JSONObject chartData) {
        StringBuilder gaugeConfiguration = new StringBuilder();
        gaugeConfiguration.append("gauge:{\r\n");
        if (chartData.has("GaugeChartLabel") && (chartData.get("GaugeChartLabel") instanceof String)) {
            String isGaugeLabel = chartData.getString("GaugeLabel").trim();
            if (isGaugeLabel.equalsIgnoreCase("false")) {
                gaugeConfiguration.append("label:{\r\n").append("show:false,\r\n").append("},\r\n");
            }
        }
        gaugeConfiguration.append("},\r\n");
        return gaugeConfiguration.toString();
    }

    @Override
    String dataConfiguration(JSONObject chartData) {
        // TODO Auto-generated method stub
        StringBuilder measures = new StringBuilder();
        StringBuilder xyAxis = new StringBuilder();
        StringBuilder dataConfiguration = new StringBuilder();

        ChartUtils chartUtilsObject = new ChartUtils();
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

        // adding measures to configuration
        if (!(measures.length() == 0)) {
            xyAxis.append("keys:{ \r\n");
            xyAxis.append("value: [").append(measures).append("],\r\n},");
        } else {
            xyAxis.setLength(0);
        }

        dataConfiguration.append("data: {\r\n").append("        json: data,").append(xyAxis);
        dataConfiguration.append("        type: 'gauge',\r\n").append("    },\r\n");

        return dataConfiguration.toString();
    }

}