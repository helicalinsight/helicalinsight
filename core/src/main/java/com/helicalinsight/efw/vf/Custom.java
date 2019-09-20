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
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the IChart interface
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
 */
public class Custom implements IChart {

    private static final Logger logger = LoggerFactory.getLogger(Custom.class);

    /**
     * Returns the script to be rendered by the browser along with the data
     *
     * @param chartData The chart data that consists of script tag
     * @param data      The chart data
     * @return Returns The script to be rendered by the browser
     */
    public String getChartDetails(JSONObject chartData, JsonArray data, String chartId) {
        String chartTagScript = chartData.getString("script");
        chartTagScript = "(function(data, chartElement){" + chartTagScript + "})(" + data + ")";
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The %s has prepared the JavaScript to be " + "rendered " +
                    "by" + " the browser successfully.", this.getClass()));
        }
        return chartTagScript;
    }
}