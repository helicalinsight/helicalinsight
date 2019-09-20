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

package com.helicalinsight.datasource;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Created by author on 25-08-2015.
 *
 * @author Somen
 */
@SuppressWarnings("unused")
public class QuickConnectionTest implements IService {

    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        JSONObject requestFormData = JSONObject.fromObject(formData);

        DataSourceReader dataSourceReader = new DataSourceReader();

        String result = dataSourceReader.doService(type, serviceType, "read", formData);
        JSONObject readServiceResult = JSONObject.fromObject(result);

        if (readServiceResult.getString("status").equals("0")) {
            return result;
        }

        JSONObject dataJson = ControllerUtils.getDataFromResponse(readServiceResult);
        JSONObject freshRequest = removeAtFromJsonKey(dataJson);
        String classifier = requestFormData.optString("classifier");
        String dir = requestFormData.optString("dir");
        freshRequest.accumulate("classifier", classifier);
        freshRequest.accumulate("dir", dir);
        return ServiceUtils.executeService(type, serviceType, "test", freshRequest.toString());
    }

    private JSONObject removeAtFromJsonKey(JSONObject dataJson) {
        List<String> jsonKeys = JsonUtils.getKeys(dataJson);
        JSONObject duplicateJson = new JSONObject();
        for (String key : jsonKeys) {
            String tempKey;
            String tempValue;
            if (key.startsWith("@")) {
                tempKey = key.replace("@", "");
            } else {
                tempKey = key;
            }
            tempValue = dataJson.getString(key);
            duplicateJson.accumulate(tempKey, tempValue);
        }
        return duplicateJson;
    }


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
