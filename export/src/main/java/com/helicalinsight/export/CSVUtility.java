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

package com.helicalinsight.export;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.ApplicationException;
import com.helicalinsight.efw.vf.ChartService;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is responsible for converting chart data in to json array and
 * iterate the JSONArray and return the string for download CSV file of chart
 * data
 *
 * @author Muqtar Ahmed
 */

public class CSVUtility {

    private static final Logger logger = LoggerFactory.getLogger(CSVUtility.class);

    /**
     * The singleton instance which holds the application settings. Only one
     * instance per application
     */
    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    /**
     * This method is used to convert chart data to json array, iterate the
     * json array and return the data
     *
     * @param data chart data
     * @return String
     */
    public String getCSVData(String data) {
        JsonObject jsonObject;
        try {
            ChartService chartService = new ChartService(data, this.applicationProperties);
            jsonObject = chartService.getData();
            String jsonString = jsonObject.get("data").getAsString();
            JSONObject jsonObj = new JSONObject();
            JSONArray dataNJsonArray = JSONArray.fromObject(jsonString);

            for (Object object : dataNJsonArray) {
                jsonObj = (JSONObject) JSONSerializer.toJSON(object);
            }

            Iterator<?> keys = jsonObj.keys();
            List<String> listOfKeys = new ArrayList<>();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                listOfKeys.add(key);
            }

            String header = "";
            String stringOfHeader;
            for (String hValue : listOfKeys) {
                stringOfHeader = '"' + hValue + '"';
                header = header + stringOfHeader + ",";
            }

            String result;
            result = header.substring(0, header.length() - 1).trim() + "\n";
            for (int j = 0; j < dataNJsonArray.size(); j++) {
                String stringOfValues = "";
                logger.debug("Size of Json Array" + dataNJsonArray.size());
                for (String listOfKey : listOfKeys) {
                    String value = dataNJsonArray.getJSONObject(j).getString(listOfKey);
                    String valuesInQuotes = '"' + value + '"';
                    stringOfValues = stringOfValues + valuesInQuotes + ",";
                    logger.debug("Values == " + stringOfValues);
                }
                result = result + stringOfValues.substring(0, stringOfValues.length() - 1).trim() + "\n";
            }
            return result;
        } catch (JSONException e) {
            logger.error("JSONException ", e);
            //handle error
        } catch (ApplicationException e) {
            logger.error("ApplicationException occurred. " + e);
            //handle error
        }
        return null;
    }
}
