/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.hwf.util;

import com.helicalinsight.hwf.exception.HwfException;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Somen 
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
