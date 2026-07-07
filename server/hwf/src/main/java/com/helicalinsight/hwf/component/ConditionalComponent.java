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
