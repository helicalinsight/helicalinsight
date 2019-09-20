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

package com.helicalinsight.hwf.component.output;

import com.helicalinsight.hwf.core.IResponse;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;


public class OutputProcess implements IResponse {
    private static final Logger logger = LoggerFactory.getLogger(OutputProcess.class);

    @Override
    public JSONObject setOutPutResponse(HttpServletResponse response, JSONObject outputTag, String outputFromInputTag) {
        logger.debug("outputTag:  " + outputTag);
        logger.debug("outputValue:  " + outputTag.size());
        logger.debug("outputFromInputTag:  " + outputFromInputTag.length());
        return outputTag;
    }
}
