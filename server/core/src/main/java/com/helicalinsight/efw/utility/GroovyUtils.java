package com.helicalinsight.efw.utility;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.FormValidationException;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import net.sf.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.helicalinsight.admin.model.User;
/**
 * @author Somen
 *         Created by helical021 on 7/20/2017.
 */
public class GroovyUtils {
    private static final Logger logger = LoggerFactory.getLogger(GroovyUtils.class);

    public static <T> T executeGroovy(String groovyCodeAsString, String methodName, Class<T> clazz) {
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
        if(object instanceof JSONObject) {
        	String json = new Gson().toJson(object);
        	JsonObject object2 = new Gson().fromJson(json, JsonObject.class);
        	return clazz.cast(object2);
        }
        return clazz.cast(object);
    }

    public static String singleQuotes(String value) {
        return "'" + value + "'";
    }

    public static String processResult(JsonObject response, String type) {
        String message;
        boolean result = response.get("result").getAsBoolean();
        if (result) {
            message = type + " Test Success";
        } else {
            String errorMessage = response.get("errorMessage").getAsString();
            throw new FormValidationException("The " + type + " Expression Test failed.  Error Message is " +
                    errorMessage);
        }
        return message;
    }

    public static String replaceExpression(String condition) {
        String replaceExp = SecurityExpressionEvaluator.replaceExpression(condition);
        return replaceExp;
    }
    public static String replaceExpressionUser(String condition, User user) {
        String replaceExp = SecurityExpressionEvaluator.replaceExpression2(condition,user);
        return replaceExp;
    }

    public static boolean evaluateExpression(String expression) {
        return SecurityExpressionEvaluator.evaluateExpression(expression);
    }

    public static JsonObject validateCondition(String expression) {
        return SecurityExpressionEvaluator.validateCondition(expression);
    }


    public static boolean validateJson(JsonObject jsFunction, JsonObject requestJsonObject, JsonObject errorMessage) {
        String code = jsFunction.get("#text").getAsString();
        String functionName = jsFunction.get("@name").getAsString();
        if (jsFunction.has("@language")) {
            String language = jsFunction.get("@language").getAsString();
            if ("groovy".equalsIgnoreCase(language)) {
                GroovyCodeExecutionManager executionManager = new GroovyCodeExecutionManager(code, functionName,
                        requestJsonObject, errorMessage);
                return executionManager.executeGroovy();
            }
        }

        try {
            Context context = Context.enter();
            ScriptableObject scope = context.initStandardObjects();

            context.evaluateString(scope, code, functionName, 1, null);

            Function function = (Function) scope.get(functionName, scope);
            Object result = function.call(context, scope, scope, new Object[]{requestJsonObject.toString(),
                    errorMessage.toString()});
            String returnErrorMessage = (String) Context.jsToJava(result, String.class);
            JsonObject actualMessage = JsonParser.parseString(returnErrorMessage).getAsJsonObject();
            if (!actualMessage.entrySet().isEmpty()) {
                GsonUtility.accumulate(errorMessage,"result", actualMessage);
                return false;
            }
        } catch (Exception unknownException) {
            logger.error("Error occurred", unknownException);
        } finally {
            Context.exit();
        }
        return true;
    }


}
