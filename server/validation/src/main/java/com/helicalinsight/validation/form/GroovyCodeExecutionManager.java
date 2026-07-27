package com.helicalinsight.validation.form;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Invokes custom Groovy validators. Request/response are passed as Groovy-friendly Maps
 * ({@code [:]}). The engine {@code regexMap} (from {@code regex.properties}) is injected into
 * the script binding so validators can use patterns such as {@code regexMap.email}.
 *
 * @author Rajasekhar
 */
public class GroovyCodeExecutionManager {

    private static final Logger logger = LoggerFactory.getLogger(GroovyCodeExecutionManager.class);
    private static final Gson GSON = new Gson();

    private String groovyCodeAsString;
    private String methodName;
    private JsonObject requestJsonObject;
    private JsonObject errorMessage;

    public GroovyCodeExecutionManager(String groovyCodeAsString, String methodName, JsonObject requestJsonObject,
                                      JsonObject errorMessage) {
        this.groovyCodeAsString = groovyCodeAsString;
        this.methodName = methodName;
        this.requestJsonObject = requestJsonObject;
        this.errorMessage = errorMessage;
    }

    /**
     * Executes the configured Groovy validate method.
     *
     * @return true when validation passes (empty error map), false when errors were reported
     */
    @SuppressWarnings("unchecked")
    public boolean executeGroovy() {
        final GroovyClassLoader classLoader = new GroovyClassLoader();
        Class groovy = classLoader.parseClass(this.groovyCodeAsString);
        GroovyObject groovyObj;
        try {
            groovyObj = (GroovyObject) groovy.newInstance();
        } catch (Exception ex) {
            logger.error("An exception occurred while executing custom groovy code. Skipping validation.", ex);
            return true;
        }

        Map<String, String> regexMap = GenericValidation.getRegexMap();
        injectRegexMap(groovyObj, regexMap);

        Map<String, Object> requestMap = toMap(this.requestJsonObject);
        Map<String, Object> responseMap = toMap(this.errorMessage);

        if (logger.isDebugEnabled()) {
            logger.debug("The input being sent to the groovy code is {}", requestMap);
        }

        Object output = invokeValidate(groovyObj, requestMap, responseMap, regexMap);
        JsonObject result = toJsonObject(output);

        if (result != null && !result.entrySet().isEmpty()) {
            this.errorMessage.add("result", result);
            return false;
        }
        return true;
    }

    /**
     * Makes {@code regexMap} available as a script binding / property, and prefers a 3-arg
     * {@code validate(request, response, regexMap)} when defined.
     */
    private void injectRegexMap(GroovyObject groovyObj, Map<String, String> regexMap) {
        Map<String, String> injectable = regexMap != null ? regexMap : Collections.emptyMap();
        groovyObj.setProperty("regexMap", injectable);
        if (groovyObj instanceof Script) {
            Binding binding = ((Script) groovyObj).getBinding();
            if (binding == null) {
                binding = new Binding();
                ((Script) groovyObj).setBinding(binding);
            }
            binding.setVariable("regexMap", injectable);
        }
    }

    private Object invokeValidate(GroovyObject groovyObj, Map<String, Object> requestMap,
                                  Map<String, Object> responseMap, Map<String, String> regexMap) {
        try {
            return groovyObj.invokeMethod(this.methodName,
                    new Object[]{requestMap, responseMap, regexMap});
        } catch (groovy.lang.MissingMethodException threeArgMissing) {
            return groovyObj.invokeMethod(this.methodName, new Object[]{requestMap, responseMap});
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(JsonObject json) {
        if (json == null || json.entrySet().isEmpty()) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> map = GSON.fromJson(json, Map.class);
        return map != null ? map : new LinkedHashMap<>();
    }

    private JsonObject toJsonObject(Object output) {
        if (output == null) {
            return new JsonObject();
        }
        if (output instanceof JsonObject) {
            return (JsonObject) output;
        }
        if (output instanceof Map) {
            return GSON.toJsonTree(output).getAsJsonObject();
        }
        logger.warn("Groovy validator returned unexpected type {}; treating as empty", output.getClass().getName());
        return new JsonObject();
    }
}
