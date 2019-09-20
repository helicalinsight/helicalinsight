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

import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.export.components.DownloadCacheManager;
import com.helicalinsight.validation.filter.ValidationFilter;
import com.helicalinsight.validation.form.GenericValidation;
import net.sf.json.JSONObject;


/**
 * Created by Author on 8/07/2015
 *
 * @author Somen
 */
public class ResourceRefreshHandler implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        String refreshKey = formJson.optString("refresh");
        JSONObject jsonObject = new JSONObject();
        switch (refreshKey) {
            case "validation":
                new ValidationFilter().initialize();
                GenericValidation.init();
                break;
            case "cache":
                CacheUtils.init();
                new DownloadCacheManager().init();
                break;
            default:
                new ValidationFilter().initialize();
                GenericValidation.init();
                CacheUtils.init();
                new DownloadCacheManager().init();
                break;
        }
        jsonObject.put("message", "Successfully refreshed " + refreshKey + " settings");
        return jsonObject.toString();
    }
}