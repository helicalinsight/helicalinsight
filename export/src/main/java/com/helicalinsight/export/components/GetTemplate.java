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

package com.helicalinsight.export.components;

import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.export.ExportUtils;
import net.sf.json.JSONObject;

import java.io.File;

/**
 * Created by Author on 08/12/2016
 *
 * @author Somen
 */
@SuppressWarnings("unused")
public class GetTemplate implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }


    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);
        JSONObject response = new JSONObject();
        String templateId = formData.getString("templateId");
        String jsonFileName = ExportUtils.getTemplatesDirectory() + File.separator + templateId + ExportUtils
                .JSON_EXTENSION;
        String fileAsString = ExportUtils.getFileAsString(jsonFileName);
        JSONObject body = JSONObject.fromObject(fileAsString).getJSONObject("body");
        response.accumulate("template", body);

        return response.toString();
    }


}