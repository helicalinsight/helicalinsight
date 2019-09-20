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

import com.helicalinsight.efw.resourceloader.DirectoryLoaderProxy;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Somen
 * Created  on 17/4/2017.
 */
@SuppressWarnings("unused")
public class ReportStatasticsProvider implements IComponent {


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        String efwExtension = JsonUtils.getEfwExtension();
        JSONObject responseJson = new JSONObject();
        List<String> extensionList = new ArrayList<>();
        extensionList.add(efwExtension);
        String resources = new DirectoryLoaderProxy(extensionList).getResources(true);

        List<EFWReportVO> requiredList = new ArrayList<>();

        processEFW(resources, requiredList, "");
        responseJson.put("reportsCount", requiredList.size());
        if (requiredList.size() > 0) {

            Collections.sort(requiredList);
            List<EFWReportVO> efwReportVOs = requiredList.subList(0, Math.min(requiredList.size(), 4));
            responseJson.put("latestReports", efwReportVOs);
        }


        return responseJson.toString();
    }

    private void processEFW(String resources, List<EFWReportVO> requiredList, String logicalPathOfFile) {


        JSONArray resourceArray = JSONArray.fromObject(resources);
        for (Object jsonObject : resourceArray) {
            JSONObject resourceJson = (JSONObject) jsonObject;
            if (resourceJson.has("children")) {
                String children = resourceJson.getString("children");
                if (!"[]".equals(children)) {
                    logicalPathOfFile = logicalPathOfFile + "/" + resourceJson.getString("name");
                    processEFW(children, requiredList, logicalPathOfFile);
                    logicalPathOfFile = "";
                }
            } else {
                String type = resourceJson.getString("type");
                String path = resourceJson.getString("path");
                String extension = resourceJson.getString("extension");
                if ("file".equalsIgnoreCase(type) && "efw".equalsIgnoreCase(extension)) {
                    long lastModified = resourceJson.getLong("lastModified");
                    String title = resourceJson.getString("title");
                    String linuxStylePath = path.replaceAll("\\\\", "/");
                    int endIndexOfFileSeparator = linuxStylePath.lastIndexOf("/");
                    if (endIndexOfFileSeparator > 0) {
                        String dir = linuxStylePath.substring(0, endIndexOfFileSeparator);
                        String file = linuxStylePath.substring(endIndexOfFileSeparator + 1, linuxStylePath.length());
                        requiredList.add(new EFWReportVO(path, lastModified, title, dir, file, logicalPathOfFile));
                    } else {
                        requiredList.add(new EFWReportVO(path, lastModified, title, "/", path, logicalPathOfFile));
                    }
                }
            }

        }
    }
}

