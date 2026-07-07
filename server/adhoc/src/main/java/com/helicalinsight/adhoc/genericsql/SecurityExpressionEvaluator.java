package com.helicalinsight.adhoc.genericsql;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * A utility class for evaluating security expressions.
 * This class provides methods to evaluate, validate, and replace security expressions.
 * @author Somen
 * Created on 9/10/2015.
 */
@Component
public final class SecurityExpressionEvaluator {

	/**
     * Evaluates the given security expression.
     *
     * @param expression 		 security expression to evaluate.
     * @return {@code true} if the expression evaluates to true, {@code false} otherwise.
     */
    public static boolean evaluateExpression(String expression) {
        return com.helicalinsight.efw.utility.SecurityExpressionEvaluator.evaluateExpression(expression);
    }

    /**
     * Validates the syntax of the given security expression.
     *
     * @param expression 		 security expression to validate.
     * @return A JsonObject containing the validation result.
     */
    public static JsonObject validateCondition(String expression) {
    	JsonObject validateCondition = com.helicalinsight.efw.utility.SecurityExpressionEvaluator.validateCondition(expression);
        return validateCondition;
    }
    /**
     * Checks if a multi-value expression contains the specified argument.
     *
     * @param multiValue 		 multi-value expression to check.
     * @param argument   		 argument to check for in the multi-value expression.
     * @return {@code true} if the argument is present in the multi-value expression, {@code false} otherwise.
     */
    @SuppressWarnings("unused")
    //Method is used by using reflection. In Spring Expression Language(SpEL)
    public static boolean check(String multiValue, String argument) {
        return com.helicalinsight.efw.utility.SecurityExpressionEvaluator.check(multiValue, argument);
    }
    /**
     * Replaces the placeholders in the security expression with their actual values.
     *
     * @param expression 		 security expression containing placeholders.
     * @return The security expression with placeholders replaced by actual values.
     */
    public static String replaceExpression(String expression) {
        return com.helicalinsight.efw.utility.SecurityExpressionEvaluator.replaceExpression(expression);
    }


}
