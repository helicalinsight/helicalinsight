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

package com.helicalinsight.efw.resourceprocessor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * implementation class of this interface return the JSONObject
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 * @author Somen
 */
public interface IProcessor {

    /**
     * this method get the resource type and flag value
     *
     * @param resource type of resource
     * @param flag     flag value
     * @return JSONObject
     */

    JSONObject getJSONObject(String resource, boolean flag);

    JSONArray getJSONArray(String resource, boolean flag);
}
