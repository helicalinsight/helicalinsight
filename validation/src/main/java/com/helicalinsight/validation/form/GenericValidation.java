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

package com.helicalinsight.validation.form;

import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.validation.IValidation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.math.NumberUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

@Component
public class GenericValidation implements IValidation {

    private static final Logger logger = LoggerFactory.getLogger(GenericValidation.class);

    private static Map<String, String> regexMap = new PropertiesFileReader().read("Admin", "regex.properties");

    public static String jsonNavigator(JSONObject json, String expression) {
        String[] keyList = expression.split("\\.");

        Object object = null;
        for (String key : keyList) {
            int index;
            if (key.endsWith("]")) {
                index = getIndex(key);
                key = key.substring(0, key.indexOf("["));
                object = json.get(key);
                if (object instanceof JSONArray) {
                    JSONArray jsonArray = JSONArray.fromObject(object);
                    object = jsonArray.get(index);
                }
            } else object = json.get(key);

            if (object instanceof JSONObject) {
                json = JSONObject.fromObject(object);
            }
        }

        return object != null ? object.toString() : null;
    }

    public static int getIndex(String array) {
        int begin = array.indexOf("[");
        int end = array.indexOf("]");
        String index = array.substring(begin + 1, end);
        return Integer.parseInt(index);
    }

    public static void init() {
        regexMap = new PropertiesFileReader().read("Admin", "regex.properties");
    }

    @Override
    public boolean isThreadSafeToCache() {
        return false;
    }

    public boolean isValid(JSONObject formData, JSONObject xmlRuleJson) {
        //Made a copy
        JSONObject jsonCopy = JSONObject.fromObject(formData);
        String definitionFolder = xmlRuleJson.getString("definitionFolder");
        String componentFile = xmlRuleJson.getString("@definition-file");
        JSONObject validationRulesJson = ResourceProcessorFactory.getIProcessor().getJSONObject(definitionFolder +
                File.separator + componentFile, true);
        JSONObject errorMessages = new JSONObject();
        boolean overallResult = true;
        if (validationRulesJson == null) {
            return false;
        }
        JSONObject formValidation = validationRulesJson.getJSONObject("formData");

        JSONObject requestJsonObject = JSONObject.fromObject(jsonCopy);

        if (formValidation.has("jsFunction")) {
            JSONObject jsFunction = formValidation.getJSONObject("jsFunction");
            formValidation.discard("jsFunction");
            formValidation.discard("@mandatory");
            overallResult = validateJson(jsFunction, requestJsonObject, errorMessages);
            JSONObject result = errorMessages.getJSONObject("result");
            xmlRuleJson.accumulate("message", result);
        }

        if (errorMessages.isEmpty()) {
            overallResult = recursiveValidation(jsonCopy, formValidation, errorMessages, requestJsonObject);
            xmlRuleJson.accumulate("message", errorMessages);
        }

        return overallResult;
    }

    public boolean isRequired(String value) {
        return !(value == null || value.trim().length() == 0 || "[]".equals(value));
    }

    public boolean minLength(String value, int length) {
        return value != null && value.length() >= length;

    }

    public boolean maxLength(String value, int length) {
        return value != null && value.length() <= length;

    }

    public boolean isOfType(String value, String type) {
        String typeRegex = typeResolver(type);
        return typeRegex == null || value.matches(typeRegex);
    }

    public String typeResolver(String type) {
        return regexMap.get(type);
    }

    @SuppressWarnings("ConstantConditions")
    public boolean recursiveValidation(JSONObject jsonCopy, JSONObject formValidation, JSONObject errorMessages,
                                       JSONObject requestJsonObject) {
        boolean overallResult = true;
        List<String> validationKeys = JsonUtils.getKeys(formValidation);
        for (String validationKey : validationKeys) {
            Object sampleObject = jsonCopy.get(validationKey);

            if (sampleObject instanceof JSONObject) {
                JSONObject errorJson = new JSONObject();
                overallResult = overallResult & recursiveValidation(jsonCopy.getJSONObject(validationKey),
                        formValidation.getJSONObject(validationKey), errorJson, requestJsonObject);
                errorMessages.accumulate(validationKey, errorJson);
            } else {
                boolean result = true;
                String sampleString = jsonCopy.optString(validationKey);
                boolean sampleNotEmpty = false;
                if (sampleString.length() > 0) {
                    sampleNotEmpty = true;
                }

                JSONObject record = formValidation.getJSONObject(validationKey);
                String required = record.optString("@required");
                String requiredIf = record.optString("@requiredIf");
                boolean reqIf = false;
                if (requiredIf.length() > 0) {
                    String condition = record.getString("@condition");
                    String rhsValue = record.getString("@value");

                    String val = jsonNavigator(requestJsonObject, requiredIf);
                    String completeExpression;
                    if (!(NumberUtils.isNumber(val) && NumberUtils.isNumber(rhsValue))) {
                        //if the requiredIf parameter is not in the request than required if has
                        // to be false
                        reqIf = val != null && val.equalsIgnoreCase(rhsValue);
                    } else {
                        ExpressionParser parser = new SpelExpressionParser();
                        completeExpression = val + condition + rhsValue;
                        Expression exp = parser.parseExpression(completeExpression);
                        reqIf = exp.getValue(Boolean.class);

                    }

                }

                String type = record.optString("@type");
                Integer maxLength = record.optInt("@maxLength");
                Integer minLength = record.optInt("@minLength");
                Integer length = record.optInt("@length");

                if (required.length() > 0 || reqIf) {
                    if (!isRequired(sampleString)) {
                        errorMessages.accumulate(validationKey, "Please enter the mandatory " +
                                "field " + validationKey);
                        result = false;
                    }
                }

                if (sampleNotEmpty) {
                    if (type.length() > 0) {
                        if ("custom".equals(type)) {
                            if (!sampleString.matches(record.optString("@expression"))) {
                                errorMessages.accumulate(validationKey, "The custom type is " + "invalid ");
                                result = false;
                            }
                        } else {
                            String typeArray[] = type.split(",");
                            for (String typeEntries : typeArray) {
                                if (!isOfType(sampleString, typeEntries)) {
                                    errorMessages.accumulate(validationKey, " is invalid ");
                                    result = false;

                                }
                            }
                        }
                    }

                    if (maxLength > 0) {
                        if (!maxLength(sampleString, maxLength)) {
                            errorMessages.accumulate(validationKey, "max-length of this field is " +
                                    "" + maxLength);
                            result = false;
                        }
                    }

                    if (minLength > 0) {
                        if (!minLength(sampleString, minLength)) {
                            errorMessages.accumulate(validationKey, "minLength of this field is " +
                                    "" + minLength);
                            result = false;
                        }
                    }

                    if (length > 0) {
                        if (length != sampleString.length()) {
                            errorMessages.accumulate(validationKey, "length of this field is " +
                                    "" + length);
                            result = false;
                        }
                    }
                }
                overallResult = overallResult && result;
            }
        }
        return overallResult;
    }

    private boolean validateJson(JSONObject jsFunction, JSONObject requestJsonObject, JSONObject errorMessage) {
        String code = jsFunction.getString("#text");
        String functionName = jsFunction.getString("@name");
        if (jsFunction.has("@language")) {
            String language = jsFunction.getString("@language");
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
            JSONObject actualMessage = JSONObject.fromObject(returnErrorMessage);
            if (!actualMessage.isEmpty()) {
                errorMessage.accumulate("result", actualMessage);
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