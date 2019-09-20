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

package com.helicalinsight.efw.utility;

import com.helicalinsight.resourcesecurity.jaxb.Efwsr;
import net.sf.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user on 3/14/2017.
 *
 * @author Rajasekhar
 */
public class ResponseUtils {
    public static String createJsonResponse(String message) {
        JSONObject responseMessage = new JSONObject();
        responseMessage.put("message", message);
        return handleSuccessMassage(responseMessage).toString();
    }

    /**
     * This method is responsible for handling the messages for success or failure and prepare the
     * json
     * object for message. for success message status will be 1 and for failure message status is 0
     *
     * @param message message
     * @return json as string
     */
    public static JSONObject handleSuccessMassage(JSONObject message) {
        JSONObject response = new JSONObject();
        response.accumulate("status", 1);
        response.accumulate("response", message);
        return response;
    }

    /**
     * This method check the element in the list. If element present in the list it return true
     * else false
     *
     * @param element To be checked
     * @param list    List which contains elements to be check
     * @return return boolean value true/false
     */
    public static boolean contains(Integer element, ArrayList<Integer> list) {
        return list.contains(element);
    }

    public static JSONObject reportContentAsJson(Efwsr efwsr) {
        JSONObject data = new JSONObject();
        String reportParameters = efwsr.getReportParameters();
        data.accumulate("reportParameters", reportParameters);
        String reportDirectory = efwsr.getReportDirectory();
        String reportFile = efwsr.getReportFile();
        String reportName = efwsr.getReportName();
        data.accumulate("reportDirectory", reportDirectory);
        data.accumulate("reportFile", reportFile);
        data.accumulate("reportName", reportName);
        return data;
    }
}
