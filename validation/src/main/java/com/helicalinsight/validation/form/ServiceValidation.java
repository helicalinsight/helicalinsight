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

package com.helicalinsight.validation.form;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by Author on 30/07/2015
 *
 * @author Somen
 */

@Component
public class ServiceValidation extends GenericValidation {

    private static final Logger logger = LoggerFactory.getLogger(ServiceValidation.class);

    public boolean isValid(JSONObject formData, JSONObject xmlRuleJson) {

        //pickup the definition file and pass it to generic validation
        // from the formData get the service, serviceType and type..(the type can be an array)
        //loop the xmlRuleJson serviceParameters jsonArray
        //accumulate the definition-file in the xmlRuleJson
        //discard the service parameters
        //return generic validation
        JSONObject serviceJson = formData.getJSONObject("serviceJson");
        String service = serviceJson.getString("service");
        String serviceType = serviceJson.getString("serviceType");
        String type = serviceJson.getString("type");
        formData.discard("serviceJson");

        String definitionFile = "";
        JSONArray serviceParameter = xmlRuleJson.getJSONArray("serviceParameters");
        for (int index = 0; index < serviceParameter.size(); index++) {
            JSONObject record = serviceParameter.getJSONObject(index);
            if (type.equals(record.getString("@type")) && service.equals(record.getString("@service")) && serviceType
                    .equals(record.getString("@serviceType"))) {
                definitionFile = record.getString("@definition-file");
                break;
            }
        }

        if ("".equals(definitionFile)) {
            return true;
        }

        xmlRuleJson.discard("serviceParameters");
        xmlRuleJson.put("@definition-file", definitionFile);

        return super.isValid(formData.getJSONObject("formData"), xmlRuleJson);
    }
}