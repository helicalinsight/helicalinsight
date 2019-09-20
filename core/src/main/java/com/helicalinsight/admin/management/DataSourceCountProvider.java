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

import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.components.EfwdReaderUtility;
import com.helicalinsight.efw.components.GlobalXmlReaderUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * @author Somen
 *         Created  on 17/4/2017.
 */
@SuppressWarnings("unused")
public class DataSourceCountProvider implements IComponent {


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        String efwExtension = JsonUtils.getEfwExtension();
        JSONObject responseJson = new JSONObject();
        responseJson.put("dataSourceCount", getDataSourceCount());
        return responseJson.toString();
    }


    private int getDataSourceCount() {
        List<JSONObject> dataSources;
        JSONArray extensions = new JSONArray();
        String efwdExtension = JsonUtils.getEfwdExtension();
        extensions.add(efwdExtension);
        EfwdReaderUtility efwdReaderUtility = new EfwdReaderUtility(extensions);
        dataSources = efwdReaderUtility.getAllEfwdConnections("sql.jdbc");

        GlobalXmlReaderUtility globalXmlReaderUtility = ApplicationContextAccessor.getBean
                (GlobalXmlReaderUtility.class);
        globalXmlReaderUtility.addDataSources(dataSources, DataSourceSecurityUtility.READ);
        return dataSources.size();
    }


}
