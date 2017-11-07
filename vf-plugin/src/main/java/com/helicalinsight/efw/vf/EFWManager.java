package com.helicalinsight.efw.vf;

import com.helicalinsight.efw.utility.ConfigurationFileReader;

import java.util.Map;

public class EFWManager {

    private Map<String, String> c3ChartMap = ConfigurationFileReader
            .mapFromClasspathPropertiesFile("efwc3chart.properties");

    public EFWC3Chart getC3Chart(String subtype) {
        EFWC3Chart efwc3ChartObject = null;
        String c3ChartSubtypeKey = "";
        String c3ChartClassName;
        if (subtype.length() > 0) {
            for (Map.Entry<String, String> entry : c3ChartMap.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(subtype)) {
                    c3ChartSubtypeKey = entry.getKey();
                    break;
                }
            }
            c3ChartClassName = c3ChartMap.get(c3ChartSubtypeKey);
        } else {
            c3ChartClassName = c3ChartMap.get("line");
        }
        try {
            efwc3ChartObject = (EFWC3Chart) Class.forName(c3ChartClassName).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return efwc3ChartObject;
    }
}
