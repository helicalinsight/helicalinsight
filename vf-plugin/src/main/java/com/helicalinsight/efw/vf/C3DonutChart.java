package com.helicalinsight.efw.vf;

import net.sf.json.JSONObject;

public class C3DonutChart extends EFWC3Chart {

    public String getChartConfig(JSONObject chartData) {

        String dataConf = dataConfiguration(chartData);
        String tooltipConf = tooltipConfiguration(chartData);
        String legendConf = legendConfiguration(chartData);
        String colorPatternConf = colorPattern(chartData);
        String donutConf = donutProperties(chartData);
        StringBuilder chartConfiguration = new StringBuilder();
        chartConfiguration.append("{\r\n").append("	bindto:chartElement, \r\n").append(dataConf + tooltipConf);
        chartConfiguration.append(colorPatternConf).append(tooltipConf).append(legendConf).append(donutConf)
                .append("}");

        String dynamicJson = chartConfiguration.toString().replace("\"chartElement\"", "chartElement")
                .replace("\"data\"", "data");
        StringBuilder c3conf = new StringBuilder();
        c3conf.append("var chart = c3.generate(").append(dynamicJson).append(");");
        return c3conf.toString();
    }

    public String donutProperties(JSONObject chartData) {
        StringBuilder donutConfiguration = new StringBuilder();
        donutConfiguration.append("donut:{\r\n");
        if (chartData.has("DonutChartLabelShow") && (chartData.get("DonutChartLabelShow") instanceof String)) {
            String isDonutLabel = chartData.getString("DonutLabelShow").trim();
            if (isDonutLabel.equalsIgnoreCase("false")) {
                donutConfiguration.append("label:{\r\n").append("show:false,\r\n").append("},\r\n");
            }
        }
        if (chartData.has("DonutChartTitle") && (chartData.get("DonutChartTitle") instanceof String)) {
            String DonutChartTitle = chartData.getString("DonutChartTitle").trim();
            donutConfiguration.append("title:").append(DonutChartTitle).append(",\r\n");
        }
        donutConfiguration.append("},\r\n");
        return donutConfiguration.toString();
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
            xyAxis.append("keys:{\r\n");
            xyAxis.append("value: [").append(measures).append("],\r\n},");
        } else {
            xyAxis.setLength(0);
            ;
        }

        dataConfiguration.append("data: {\r\n").append("        json: data,").append(xyAxis);
        dataConfiguration.append("        type: 'donut',\r\n").append("    },\r\n");

        return dataConfiguration.toString();
    }
}