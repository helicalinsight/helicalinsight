package com.helicalinsight.efw.vf;

public class ChartUtils {

    public String array2csv(String[] chartTagMeasuresArray) throws NullPointerException {
        // TODO Auto-generated method stub
        if (chartTagMeasuresArray[0] == null) {
            return "";
        } else {
            int numberOfMeasures = chartTagMeasuresArray.length;
            StringBuilder Measures = new StringBuilder();
            if (numberOfMeasures == 1) {
                Measures.append("'").append(chartTagMeasuresArray[0]).append("'");
            } else {
                for (int i = 0; i < numberOfMeasures; i++) {
                    Measures.append("'").append(chartTagMeasuresArray[i]).append("'");
                    if (i != (numberOfMeasures - 1)) {
                        Measures.append(",");
                    }
                }
            }
            return Measures.toString();
        }
    }
}
