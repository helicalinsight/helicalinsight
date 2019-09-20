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

import com.helicalinsight.efw.exceptions.EfwdServiceException;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by author on 17-Jan-15.
 *
 * @author Rajasekhar
 */
@Component
class EfwdServiceUtility {

    public Efwd getEfwd(String connectionDetails) {
        int globalId;
        String serviceType;
        try {
            JSONObject connection = JSONObject.fromObject(connectionDetails);
            globalId = Integer.valueOf(connection.getString("globalId"));
            serviceType = connection.getString("@type");
        } catch (JSONException ex) {
            throw new EfwdServiceException("The json from the efwd file is malformed.", ex);
        } catch (NumberFormatException ex) {
            throw new EfwdServiceException("The global id from the efwd file is not a number.");
        }
        return new Efwd(globalId, serviceType);
    }
}
