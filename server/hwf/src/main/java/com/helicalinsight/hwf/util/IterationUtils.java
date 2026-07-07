package com.helicalinsight.hwf.util;

import com.helicalinsight.hwf.exception.HwfException;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Somen
 *         Created on 5/14/2016.
 */
public class IterationUtils {
    private static final Logger logger = LoggerFactory.getLogger(IterationUtils.class);

    private static final String groovyHeader = "import net.sf.json.JSONObject;\n" + "public JSONObject evaluate" +
            "(JSONObject input,JSONObject result){";
    private static final String groovyFooter = "}";

    public static JSONObject evaluateCondition(JSONObject steps) {
        JSONObject requiredInput = steps.getJSONObject("requiredInput");

        JSONObject expression = JSONObject.fromObject(steps.getJSONObject("expression"));//created a copy
        String type = expression.getString("@type");

        if (requiredInput.has("nextState")) {
            JSONObject nextState = requiredInput.getJSONObject("nextState");
            String afterIteration = nextState.getString("afterIteration");
            String myExpression = nextState.getString("initialCondition");
            String lhsExpression = myExpression.substring(0, myExpression.lastIndexOf("=") + 1);
            String newInitialization = lhsExpression + afterIteration;
            expression.put("initialization", newInitialization);

        }
        String initCondition = expression.getString("initialization");
        String expressionText = getExpression(expression);

        JSONObject response = new JSONObject();

        if ("groovy".equalsIgnoreCase(type)) {
            JSONObject groovyResult = handleGroovy(expressionText, requiredInput, response);
            groovyResult.accumulate("initialCondition", initCondition);
            return groovyResult;
        }
        return new JSONObject();

    }

    private static String getExpression(JSONObject expression) {
        String initialization = expression.getString("initialization") + "\n";
        String condition = "evalCondition=(" + expression.getString("condition") + " )" + "\n";
        String process = "", returnProcess = "";
        if (expression.has("passOutput")) {
            process = "pass = " + expression.getString("passOutput") + "\n";
            returnProcess = "result.put('process',pass);\n";
        }
        String iteration = "afterIteration=(" + expression.getString("iteration") + " ) " + "\n";
        String returnValue = "result.put('evalCondition',evalCondition); \n" +
                "result.put('afterIteration',afterIteration);\n " +
                returnProcess + "return result;";

        return groovyHeader + initialization + condition + process + iteration + returnValue + groovyFooter;
    }

    private static JSONObject handleGroovy(String expressionText, JSONObject inputs, JSONObject response) {
        final GroovyClassLoader classLoader = new GroovyClassLoader();
        Class groovy = classLoader.parseClass(expressionText);
        GroovyObject groovyObj;
        try {
            groovyObj = (GroovyObject) groovy.newInstance();
        } catch (Exception ex) {
            logger.error("Exception occurred", ex);
            throw new HwfException("Groovy code execution failed " + ex.getMessage());
        }
        return (JSONObject) groovyObj.invokeMethod("evaluate", new Object[]{inputs, response});

    }
}
