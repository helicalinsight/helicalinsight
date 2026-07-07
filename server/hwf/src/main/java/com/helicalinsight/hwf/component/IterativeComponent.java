package com.helicalinsight.hwf.component;

import com.helicalinsight.hwf.core.IIterator;
import com.helicalinsight.hwf.core.api.ExecutionStatus;
import com.helicalinsight.hwf.util.ComponentUtils;
import com.helicalinsight.hwf.util.IterationUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Somen
 *         Created on 5/7/2016.
 */
public class IterativeComponent implements IIterator {
    private static final Logger logger = LoggerFactory.getLogger(IterativeComponent.class);

    private Integer executionState = ExecutionStatus.NOT_SET;
    private Boolean hasNext = ExecutionStatus.HAS_NEXT_NOT_SET;
    private String expression;
    private String initialCondition;
    private String iterator;

    @Override
    public JSONObject jobProcess(JSONObject input, JSONObject jobProcess) {
        logger.info("The input in iterative component is " + input);
        JSONObject steps = jobProcess.getJSONObject("steps");
        steps.put("requiredInput", input); //should be handled by proxy class
        JSONObject evaluateResult = evaluateIteration(steps);
        logger.info("evaluated result in iteration component is " + evaluateResult);
        this.hasNext = evaluateResult.getBoolean("evalCondition");
        logger.info("hasNextSet to " + hasNext);

        executionState = ExecutionStatus.SUCCESS;
        JSONObject afterReturn = ComponentUtils.handleSuccess(evaluateResult);
        logger.info("afterRetrun " + afterReturn);
        return afterReturn;
    }

    @Override
    public JSONObject evaluateIteration(JSONObject steps) {
        return IterationUtils.evaluateCondition(steps);
    }

    @Override
    public JSONObject executionStatus() {
        if (hasNext) {
            executionState = ExecutionStatus.RUNNING;
        }
        return ComponentUtils.setExecutionStatus(executionState);
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public int getCountState() {
        return 0;
    }
}
