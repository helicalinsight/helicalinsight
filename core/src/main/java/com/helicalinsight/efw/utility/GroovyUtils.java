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

package com.helicalinsight.efw.utility;

import com.helicalinsight.efw.exceptions.FormValidationException;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Somen
 * Created by helical021 on 7/20/2017.
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
        return clazz.cast(object);
    }
    public static String singleQuotes(String value) {
        return "'" + value + "'";
    }

    public static String processResult(JSONObject response, String type) {
        String message;
        boolean result = response.getBoolean("result");
        if (result) {
            message = type + " Test success";
        } else {
            String errorMessage = response.getString("errorMessage");
            throw new FormValidationException("The " + type + " Expression Test failed.  Error Message is " +
                    errorMessage);
        }
        return message;
    }

    public static String replaceExpression(String condition) {
        return SecurityExpressionEvaluator.replaceExpression(condition);
    }

    public static boolean evaluateExpression(String expression) {
        return SecurityExpressionEvaluator.evaluateExpression(expression);
    }

    public static JSONObject validateCondition(String expression) {
        return SecurityExpressionEvaluator.validateCondition(expression);
    }
}
