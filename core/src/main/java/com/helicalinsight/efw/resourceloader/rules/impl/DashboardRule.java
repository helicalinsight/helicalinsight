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

package com.helicalinsight.efw.resourceloader.rules.impl;

import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceloader.rules.AbstractResourceRule;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;
import com.sun.istack.NotNull;
import net.sf.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Rajesh
 * Created by author on 4/9/2019.
 */
public class DashboardRule extends AbstractResourceRule implements IResourceRule {
    private DashboardRule() {
    }

    @NotNull
    public static IResourceRule getInstance() {
        return DashboardRuleHolder.INSTANCE;
    }

    @NotNull
    public String toString() {
        return "DashboardRule";
    }

    @Override
    protected void addContentOfJson(Map<String, String> foldersMap, JSONObject fileAsJson, Iterator<?> keys) {
        foldersMap.put("title", fileAsJson.getString("fileName"));
    }

    @Override
    public boolean validateFile(JSONObject fileAsJson) throws ImproperXMLConfigurationException, UnSupportedRuleImplementationException {
        return validationResult(fileAsJson);
    }

    @Override
    public Map<String, String> getResourceMap(JSONObject fileAsJson, String extensionKey, String path, String name, String lastModified) {
        return include(fileAsJson, extensionKey, path, name, lastModified);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    private static class DashboardRuleHolder {
        public static final IResourceRule INSTANCE = new DashboardRule();
    }
}
