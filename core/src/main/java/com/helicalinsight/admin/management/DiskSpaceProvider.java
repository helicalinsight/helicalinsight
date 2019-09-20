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

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;

import java.io.File;

/**
 * @author Somen
 *         Created  on 17/4/2017.
 */
@SuppressWarnings("unused")
public class DiskSpaceProvider implements IComponent {


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        String efwExtension = JsonUtils.getEfwExtension();
        JSONObject responseJson = new JSONObject();
        File file = new File(ApplicationProperties.getInstance().getSolutionDirectory());
        long totalSpace = file.getTotalSpace();
        long freeSpace = file.getFreeSpace();

        responseJson.put("totalDiskSize", totalSpace / 1024 / 1024);
        responseJson.put("usedSpace", (totalSpace- freeSpace)/ 1024 / 1024);
        responseJson.put("freeSpace", freeSpace / 1024 / 1024);

        return responseJson.toString();
    }


}
