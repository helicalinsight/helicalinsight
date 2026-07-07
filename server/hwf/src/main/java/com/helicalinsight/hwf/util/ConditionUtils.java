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
public class ConditionUtils {
    private static final Logger logger = LoggerFactory.getLogger(ConditionUtils.class);

    public static boolean evaluateCondition(JSONObject steps) {


        JSONObject expression = steps.getJSONObject("expression");
        String type = expression.getString("@type");
        String expressionText = getGroovyScript(expression);
        JSONObject inputs = steps.getJSONObject("requiredInput");
        return "groovy".equalsIgnoreCase(type) && handleGroovy(expressionText, inputs);

    }

    private static String getGroovyScript(JSONObject expression) {
        String groovyHeader = "import net.sf.json.JSONObject;\n" + "public boolean evaluate(JSONObject input){ \n";
        String expressionText = expression.getString("#text") + "\n";
        String groovyFooter = "}";

        expressionText = groovyHeader + expressionText + groovyFooter;
        return expressionText;

    }


    private static boolean handleGroovy(String expressionText, JSONObject inputs) {
        final GroovyClassLoader classLoader = new GroovyClassLoader();
        Class groovy = classLoader.parseClass(expressionText);
        GroovyObject groovyObj;
        try {
            groovyObj = (GroovyObject) groovy.newInstance();
        } catch (Exception ex) {
            logger.error("Exception occurred", ex);
            throw new HwfException("Groovy code execution failed " + ex.getMessage());
        }
        return (boolean) groovyObj.invokeMethod("evaluate", new Object[]{inputs});

    }
}
