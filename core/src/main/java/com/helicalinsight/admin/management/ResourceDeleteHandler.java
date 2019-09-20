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

package com.helicalinsight.admin.management;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.resourcecache.IResourceManager;
import com.helicalinsight.efw.resourcecache.ResourceManager;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Author on 13/05/2015
 *
 * @author Somen
 */
@SuppressWarnings("unused")
public class ResourceDeleteHandler implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        String resourcePath = formJson.getString("resourcePath");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("resourcePath", resourcePath);
        JSONObject jsonObject = new JSONObject();
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
        IResourceManager resourceManager = ResourceManager.getInstance();
        if (resourceManager.deleteResource(resourcePath + ", " + false)) {
            jsonObject.put("message", "Resource deleted successfully.");

        } else {
            jsonObject.put("message", "Resource cannot be deleted. It may not exists");
        }
        return jsonObject.toString();
    }
}