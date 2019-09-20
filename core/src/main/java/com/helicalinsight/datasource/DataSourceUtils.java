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

import com.helicalinsight.datasource.managed.IGlobalXmlReader;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;

import java.io.File;

/**
 * Created by author on 08-09-2015.
 *
 * @author Rajasekhar
 */
public class DataSourceUtils {

    public static String globalIdJson(int id) {
        IGlobalXmlReader iGlobalXmlReader = ApplicationContextAccessor.getBean(IGlobalXmlReader.class);
        return iGlobalXmlReader.getDataSourceJson(id, new File(JsonUtils.getGlobalConnectionsPath()));
    }
}
