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

package com.helicalinsight.hwf.component;

import com.helicalinsight.hwf.core.ICondition;
import com.helicalinsight.hwf.core.api.ExecutionStatus;
import com.helicalinsight.hwf.util.ComponentUtils;
import com.helicalinsight.hwf.util.ConditionUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Somen
 *         Created  on 5/7/2016.
 */
public class ConditionalComponent implements ICondition {
    private static final Logger logger = LoggerFactory.getLogger(ConditionalComponent.class);

    private Integer executionState = ExecutionStatus.NOT_SET;


    @Override
    public JSONObject jobProcess(JSONObject input, JSONObject jobProcess) {
        JSONObject steps = jobProcess.getJSONObject("steps");
        steps.put("requiredInput", input); //should be handled by proxy class
        Boolean evaluationResult = evaluateCondition(steps);
        executionState = ExecutionStatus.SUCCESS;
        return ComponentUtils.handleSuccess(evaluationResult);
    }

    @Override
    public boolean evaluateCondition(JSONObject steps) {
        return ConditionUtils.evaluateCondition(steps);
    }

    @Override
    public JSONObject executionStatus() {
        return ComponentUtils.setExecutionStatus(executionState);
    }


}
