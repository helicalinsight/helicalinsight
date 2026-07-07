package com.helicalinsight.efw.utility;

import com.google.gson.JsonObject;

/**
 * @author Somen
 *         Created on 8/28/2016.
 */
@SuppressWarnings("UnusedDeclaration")
public class GroovyUsersSession {

    public static boolean evaluateCondition(String condition) {
        String expression = SecurityExpressionEvaluator.replaceExpression(condition);
        JsonObject checkValidity = validateCondition(expression);
        GroovyUtils.processResult(checkValidity, "condition");
        return GroovyUtils.evaluateExpression(expression);
    }


    public static String getValue(String expression) {
        String evaluatedExpression = GroovyUtils.replaceExpression(expression);
        return evaluatedExpression;
    }


    public static JsonObject validateCondition(String expression) {
        String evaluatedExpression = GroovyUtils.replaceExpression(expression);
        return GroovyUtils.validateCondition(evaluatedExpression);
    }
}