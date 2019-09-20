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

package com.helicalinsight.resourcesecurity.maxims;

import com.helicalinsight.efw.exceptions.EfwException;
import net.sf.json.JSONObject;


/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public final class MaximFactory extends AbstractMaxim {

    private final JSONObject shareJson;

    public MaximFactory(JSONObject shareJson) {
        this.shareJson = shareJson;
    }

    @Override
    public ISecurityMaxim maxim(String type) {
        if ("roles".equals(type)) {
            return new RoleMaxim(this.shareJson.getJSONObject(type));
        } else if ("users".equals(type)) {
            return new UserMaxim(this.shareJson.getJSONObject(type));
        } else {
            throw new EfwException(String.format("The maxim %s is undefined.", type));
        }
    }
}
