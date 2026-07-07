package com.helicalinsight.adhoc.metadata;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.GroovyUtils;

/**
 * MetadataSecurityConditionIf
 *
 * This class implements the ISecureMetadata interface and provides methods to evaluate security conditions and filters
 * using Groovy expressions. It also includes methods to validate conditions and filters.
 * @author Somen
 * Created on 8/2/2016.
 */
@SuppressWarnings("UnusedDeclaration")
public class MetadataSecurityConditionIf implements ISecureMetadata {
	/**
     * Sets a success message for filter evaluation.
     *
     * @param response     		 JSON response object.
     * @param filterString 		 evaluated filter string.
     */
    public static void setFilterSuccessMessage(JsonObject response, String filterString) {
        response.addProperty("result", true);
        response.addProperty("evaluated", filterString);
    }
    /**
     * Sets an error message for filter evaluation.
     *
     * @param response  		 JSON response object.
     * @param exception 		 exception raised during evaluation.
     */
    public static void setFilterErrorMessage(JsonObject response, Exception exception) {
        response.addProperty("result", false);
        response.addProperty("errorMessage", exception.getMessage());
    }
    /**
     * Evaluates a security condition.
     *
     * @param condition 	 			security condition to evaluate.
     * @return True if the condition evaluates to true, otherwise false.
     */
    @Override
    public boolean evaluateCondition(String condition) {
        String expression = GroovyUtils.replaceExpression(condition);
        return GroovyUtils.evaluateExpression(expression);
    }
    /**
     * Gets the evaluated filter string.
     *
     * @param filter 	 filter expression to evaluate.
     * @return The evaluated filter string.
     */
    @Override
    public String getFilters(String filter) {
        return GroovyUtils.replaceExpression(filter);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Validates a security condition.
     *
     * @param expression 		 security condition to validate.
     * @return A JSON object containing the validation result.
     */
    public JsonObject validateCondition(String expression) {
        String evaluatedExpression = GroovyUtils.replaceExpression(expression);
        return GroovyUtils.validateCondition(evaluatedExpression);
    }
    /**
     * Validates a filter expression.
     *
     * @param filter 		   filter expression to validate.
     * @return A JSON object containing the validation result.
     */
    public JsonObject validateFilter(String filter) {
        JsonObject response = new JsonObject();
        try {
            String filterString = GroovyUtils.replaceExpression(filter);
            setFilterSuccessMessage(response, filterString);
        } catch (Exception exception) {
            setFilterErrorMessage(response, exception);
        }
        return response;
    }

}