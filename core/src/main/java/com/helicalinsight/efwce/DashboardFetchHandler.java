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

package com.helicalinsight.efwce;

import com.helicalinsight.datasource.managed.jaxb.EFWCE;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import net.sf.json.JSONObject;

import java.io.File;

/**
 * @author Rajesh
 *         Created by author on 4/22/2019.
 */
public class DashboardFetchHandler implements IComponent {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    @Override
    public String executeComponent(String jsonFormData) {

        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        JSONObject responseJson;
        responseJson = new JSONObject();
        String file = formJson.getString("file");

        if (file == null) {
            return null;
        }
        //file=file.substring(0, file.lastIndexOf('.'));
        String dir = formJson.getString("dir");
        //String ddExtension = JsonUtils.getDesignerExtension();

        String solutionDirectory = applicationProperties.getSolutionDirectory() + File.separator + dir;

        File designerFile = new File(solutionDirectory + File.separator + file);

        if (!designerFile.exists()) {
            throw new IllegalArgumentException("Aborting operation. There is no designer " + "resource with the " +
                    "specified name.");
        }

        EFWCE efwce = JaxbUtils.unMarshal(EFWCE.class, designerFile);

        responseJson.accumulate("state", efwce.getState());
        responseJson.accumulate("reportName", efwce.getName());
        return responseJson.toString();

    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
