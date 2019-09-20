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

package com.helicalinsight.efw.resourceloader.rules;

import net.sf.json.JSONObject;

/**
 * Created by author on 03-12-2014.
 * <p/>
 * Implementations validate a file based on various conditions or rules.
 *
 * @author Rajasekhar
 */
public interface IResourceSecurityRule extends IRule {

    /**
     * Validates a file based on rules
     *
     * @param fileAsJson The file under concern
     * @return <code>true</code> if the file is validated
     */
    boolean validate(JSONObject fileAsJson);

}
