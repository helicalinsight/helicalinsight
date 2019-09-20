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

import net.sf.json.JSONObject;

/**
 * @author Somen
 *         Created on 8/28/2016.
 */
@SuppressWarnings("UnusedDeclaration")
public class GroovyUsersSession {

    public static boolean evaluateCondition(String condition) {
        String expression = SecurityExpressionEvaluator.replaceExpression(condition);
        JSONObject checkValidity = validateCondition(expression);
        GroovyUtils.processResult(checkValidity, "condition");
        return GroovyUtils.evaluateExpression(expression);
    }


    public static String getValue(String expression) {
        String evaluatedExpression = GroovyUtils.replaceExpression(expression);
        return evaluatedExpression;
    }


    public static JSONObject validateCondition(String expression) {
        String evaluatedExpression = GroovyUtils.replaceExpression(expression);
        return GroovyUtils.validateCondition(evaluatedExpression);
    }
}