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

package com.helicalinsight.hwf.component.input;

import com.helicalinsight.hwf.core.IRequest;
import com.helicalinsight.hwf.exception.HwfException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class InputProcess implements IRequest {
    private static final Logger logger = LoggerFactory.getLogger(InputProcess.class);

    @Override
    public JSONObject getRequestValue(HttpServletRequest request, JSONObject globalInput, JSONArray commonInputArray,
                                      JSONObject commonInputJson) {

        JSONObject inputJsonObjectWithParameterValue = new JSONObject();
        Enumeration<String> parameterNames = request.getParameterNames();
        ArrayList<String> parameterList = Collections.list(parameterNames);

        for (int count = 0; count < commonInputArray.size(); count++) {

            JSONObject commonInputArrayItem = commonInputArray.getJSONObject(count);
            String commInputArrayItemKey = commonInputArrayItem.getString("inputKey");

            String urlKeyName = commonInputArrayItem.getString("#text");
            if (parameterList.contains(urlKeyName)) {
                logger.debug("If condition checks parameterList contains" + urlKeyName);
                String[] parameterValues = request.getParameterValues(urlKeyName);
                inputJsonObjectWithParameterValue.accumulate(commInputArrayItemKey,
                        parameterValues.length == 1 ? parameterValues[0] : parameterValues);
            } else {
                boolean defaultData = commonInputArrayItem.has("@default");
                if (defaultData) {
                    inputJsonObjectWithParameterValue.accumulate(commInputArrayItemKey,
                            commonInputArrayItem.getString("@default"));
                } else {
                    throw new HwfException("Input value " + commInputArrayItemKey + " is not available in " +
                            "request/Input tag does not have default value ");
                }
            }
        }

        if (!(commonInputArray.size() > 0)) {
            String urlKey = commonInputJson.getString("#text");
            if (parameterList.contains(urlKey)) {
                inputJsonObjectWithParameterValue.accumulate(commonInputJson.getString("inputKey"),
                        request.getParameter(urlKey));
            } else {
                boolean defaultData = commonInputJson.has("@default");
                if (defaultData) {
                    inputJsonObjectWithParameterValue.accumulate(commonInputJson.getString("inputKey"),
                            commonInputJson.getString("@default"));
                } else {
                    throw new HwfException("Input value is not available in request/Input tag does not have default "
                            + "value ");
                }
            }
        } else {
            logger.info("if not called " + commonInputArray.size());
        }
        logger.debug("inputJsonObjectWithParameterValue:  " + inputJsonObjectWithParameterValue);
        return inputJsonObjectWithParameterValue;
    }
}
