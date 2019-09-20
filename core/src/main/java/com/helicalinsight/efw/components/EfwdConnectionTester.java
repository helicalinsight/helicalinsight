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

package com.helicalinsight.efw.components;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by author on 30-01-2015.
 *
 * @author Rajasekhar
 * @author Muqtar
 */
@Component
public class EfwdConnectionTester implements IComponent {

    public String executeComponent(String formData) {
        JSONObject formDataJson = JSONObject.fromObject(formData);
        String type;
        try {
            type = formDataJson.getString("type");
        } catch (Exception ex) {
            throw new IncompleteFormDataException(ExceptionUtils.getRootCauseMessage(ex));
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("type", type);

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
        if (formDataJson.has("directory")) {
            String directory = formDataJson.getString("directory");
            formDataJson.accumulate("dir", directory);
        }
        if (formDataJson.has("id")) {
            DataSourceSecurityUtility.isDataSourceAuthenticated(formDataJson);
        }

        EfwdDataSourceHandler handler = DsTypeHandlerFactory.handler(type);

        return handler.testDS(formDataJson).toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
