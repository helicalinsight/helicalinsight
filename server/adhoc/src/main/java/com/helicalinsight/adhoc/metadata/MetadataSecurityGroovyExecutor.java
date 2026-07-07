package com.helicalinsight.adhoc.metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

/**
 * This class implements the {@link ISecureMetadata} interface and provides methods for evaluating conditions and filters using Groovy scripts.
 * Created on 08/08/2016.
 * @author Somen
 */
@SuppressWarnings("UnusedDeclaration")
public class MetadataSecurityGroovyExecutor implements ISecureMetadata {
    private static final Logger logger = LoggerFactory.getLogger(MetadataSecurityGroovyExecutor.class);
    /**
     * Evaluates a condition using Groovy script.
     *
     * @param condition The condition to be evaluated.
     * @return {@code true} if the condition is met, otherwise {@code false}
     */
    @Override
    public boolean evaluateCondition(String condition) {
        logger.info("conditionString " + condition);
        return executeGroovy(condition, "evalCondition", Boolean.class);
    }
    /**
     * Executes a Groovy script and retrieves the filter string.
     *
     * @param filter 		 Groovy script representing the filter.
     * @return The evaluated filter string.
     */
    public <T> T executeGroovy(String groovyCodeAsString, String methodName, Class<T> clazz) {
        final GroovyClassLoader classLoader = new GroovyClassLoader();
        Class groovy = classLoader.parseClass(groovyCodeAsString);
        GroovyObject groovyObj;
        try {
            groovyObj = (GroovyObject) groovy.newInstance();
        } catch (Exception ex) {
            logger.error("An exception occurred while executing custom groovy code. Skipping validation.", ex);
            return null;
        }

        Object[] args = {};
        Object object = groovyObj.invokeMethod(methodName, args);
        return clazz.cast(object);
    }

    @Override
    public String getFilters(String filter) {
        return executeGroovy(filter, "evalFilter", String.class);
    }
    /**
     * Indicates whether the component is thread-safe to cache.
     * @return {@code true} if the component is thread-safe to cache.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Validates a filter using Groovy script and returns the result as a JsonObject.
     * @param filterString 		 Groovy script representing the filter.
     * @return A JsonObject containing the validation result.
     */
    public JsonObject validateFilter(String filterString) {
        JsonObject response = new JsonObject();
        final GroovyClassLoader classLoader = new GroovyClassLoader();
        Class groovy = classLoader.parseClass(filterString);
        GroovyObject groovyObj;
        try {
            groovyObj = (GroovyObject) groovy.newInstance();
            Object[] args = {};
            String filter = (String) groovyObj.invokeMethod("evalFilter", args);
            MetadataSecurityConditionIf.setFilterSuccessMessage(response, filter);
        } catch (Exception exception) {
            MetadataSecurityConditionIf.setFilterErrorMessage(response, exception);
        }

        return response;
    }
    /**
     * Validates a condition using Groovy script and returns the result as a JsonObject.
     *
     * @param condition 		 Groovy script representing the condition.
     * @return A JsonObject containing the validation result.
     */
    public JsonObject validateCondition(String condition) {
        final GroovyClassLoader classLoader = new GroovyClassLoader();
        Class groovy = classLoader.parseClass(condition);
        GroovyObject groovyObj;
        JsonObject response = new JsonObject();
        try {
            groovyObj = (GroovyObject) groovy.newInstance();
            Object[] args = {};
            groovyObj.invokeMethod("evalCondition", args);
            response.addProperty("condition", "Test Success");
            response.addProperty("result", true);
        } catch (Exception exception) {
            response.addProperty("result", false);
            response.addProperty("errorMessage", exception.getMessage());
        }
        return response;
    }
}
