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
import com.helicalinsight.efw.exceptions.ClassNotConfiguredException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Muqtar
 * @author Rajasekhar
 * @see ChartService
 */
public class ChartResource {

    private static final Logger logger = LoggerFactory.getLogger(ChartResource.class);
    private final String settingPath;
    private final JsonArray data;
    private final JSONObject chartData;
    private final Integer chartId;

    /**
     * <p>
     * Constructor that sets the instance variables
     * path
     * </p>
     *
     * @param settingPath The path to setting.xml
     * @param data        The query execution result
     * @param chartData   The properties of chart(The prop tag in Chart)
     */
    public ChartResource(String settingPath, JsonArray data, JSONObject chartData, Integer chartId) {
        this.settingPath = settingPath;
        this.data = data;
        this.chartData = chartData;
        this.chartId = chartId;
    }

    /**
     * <p>
     * Responsibility of this method is create instance of a class which is
     * available in setting.xml file within Charts tag. Before that it checks
     * type of Charts node in setting.xml whether it is same as the type
     * coming from front end.
     * </p>
     *
     * @return Returns the data along with the chart script
     */
    public String getScript() {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject settingXMLJsonObject = processor.getJSONObject(settingPath, false);
        JSONArray settingXMLDataMap = settingXMLJsonObject.getJSONArray("Charts");
        String chartScript;
        IChart chartObject = null;
        String type = chartData.getString("type");
        if (logger.isDebugEnabled()) {
            logger.debug("The type of chart being rendered is " + type);
        }
        for (int count = 0; count < settingXMLDataMap.size(); count++) {
            if (type.equalsIgnoreCase(settingXMLDataMap.getJSONObject(count).getString("@type"))) {
                String clazz = settingXMLDataMap.getJSONObject(count).getString("@class");
                chartObject = (IChart) FactoryMethodWrapper.getUntypedInstance(clazz);
                break;
            }
        }

        if (chartObject == null) {
            throw new ClassNotConfiguredException("IChart implementation is not found");
        }

        chartScript = chartObject.getChartDetails(chartData, data, String.valueOf(chartId));
        return chartScript;
    }
}