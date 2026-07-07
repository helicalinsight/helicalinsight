package com.helicalinsight.validation.form;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.validation.IValidation;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to valid formData which implements<p>IValidation</p>
 *
 */

@Component
public class GenericValidation implements IValidation {

    private static final Logger logger = LoggerFactory.getLogger(GenericValidation.class);

    private static Map<String, String> regexMap = new PropertiesFileReader().read("Admin", "regex.properties");
   
    /**
     * jsonNavigator(JsonObject json, String expression)
     * @param json				fromData
     * @param expression		array of key-List 
     * {@return  object in string format }{@code null} if object is not instance of json data.
     */
    public static String jsonNavigator(JsonObject json, String expression) {
        String[] keyList = expression.split("\\.");

        Object object = null;
        for (String key : keyList) {
            int index;
            if (key.endsWith("]")) {
                index = getIndex(key);
                key = key.substring(0, key.indexOf("["));
                object = json.get(key);
                if (object instanceof JsonArray) {
                    JsonArray jsonArray = ((JsonElement) object).getAsJsonArray();
                    object = jsonArray.get(index);
                }
            } else object = json.get(key);

            if (object instanceof JsonObject) {
                json = (JsonObject) new Gson().toJsonTree(object);
            }
        }

        return object != null ? object.toString() : null;
    }
    /**
     * getIndex(String array)
     * @param array       it is key in string format
     * @return key index
     */
    public static int getIndex(String array) {
        int begin = array.indexOf("[");
        int end = array.indexOf("]");
        String index = array.substring(begin + 1, end);
        return Integer.parseInt(index);
    }
    /**
     * init()
     * This method is used to read a property file with in the EFW solution
     * directory.
     */
    public static void init() {
        regexMap = new PropertiesFileReader().read("Admin", "regex.properties");
    }

    @Override
    public boolean isThreadSafeToCache() {
        return false;
    }
    /**
     * isValid(JsonObject formData, JsonObject xmlRuleJson)
     * @param formData           formData
	 * @param xmlRuleJson        xml data in JsonObject
	 * {@return True if validation is successful}{@code false} if data is not correct.
	 */
    public boolean isValid(JsonObject formData, JsonObject xmlRuleJson) {
        //Made a copy
        JsonObject jsonCopy = formData.getAsJsonObject();
        String definitionFolder = xmlRuleJson.get("definitionFolder").getAsString();
        String componentFile = xmlRuleJson.get("definition-file").getAsString();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JsonObject validationRulesJson = processor.getJsonObject(definitionFolder + File.separator + componentFile,
                true);
        JsonObject errorMessages = new JsonObject();
        boolean overallResult = true;
        if (validationRulesJson == null) {
            return false;
        }
        JsonObject formValidation = validationRulesJson;

        JsonObject requestJsonObject = jsonCopy.getAsJsonObject();

        if (formValidation.has("jsFunction")) {
            JsonObject jsFunction = formValidation.getAsJsonObject("jsFunction");
            formValidation.remove("jsFunction");
            formValidation.remove("mandatory");
            overallResult = validateJson(jsFunction, requestJsonObject, errorMessages);
            JsonObject result = errorMessages.getAsJsonObject("result");
            xmlRuleJson.add("message", result);
        }

        if (errorMessages.entrySet().isEmpty()) {
            overallResult = recursiveValidation(jsonCopy, formValidation, errorMessages, requestJsonObject);
            xmlRuleJson.add("message", errorMessages);
        }

        return overallResult;
    }
    /**
     * isRequired(String value)
     * @param value      it is validationKeys value
     * {@return true if value is present in formdata} {@code false}  if value is null or empty.     
     */
    public boolean isRequired(String value) {
        return !(value == null || value.trim().length() == 0 || "[]".equals(value));
    }
    /**
     * minLength(String value, int length)
     * @param value        it is validationKeys value
     * @param length	   required length for validation of value
     * {@return True if length of value is valid},{@code false} if value is null and length is greater then minimum length
     */
    public boolean minLength(String value, int length) {
        return value != null && value.length() >= length;

    }
    /**
     * maxLength(String value, int length)
     * @param value        it is validationKeys value
     * @param length	   required length for validation of value
     * {@return True if length of value is valid},{@code false} if value is null and length smaller than max length
     */
    public boolean maxLength(String value, int length) {
        return value != null && value.length() <= length;

    }
    /**
     * isOfType(String value, String type)
     * @param value       		validation value
     * @param type				type of validation value
     * {@return true if type matches} {@code false} if not match and null. 
     */
    public boolean isOfType(String value, String type) {
        String typeRegex = typeResolver(type);
        if (typeRegex == null ) return true;
        if(value.matches(typeRegex)) return true;
        else {
        	logger.error("Regex match failed. Regex : {}, value : {}", typeRegex, value);
        	return false;
        }
    }
    /**
     * typeResolver(String type)
     * @param type      type of validation value
     * @return the value to which the specified key is mapped, or
     *         {@code null} if this map contains no mapping for the key
     */
    public String typeResolver(String type) {
        return regexMap.get(type);
    }
    /**
     * recursiveValidation(JsonObject jsonCopy, JsonObject formValidation, JsonObject errorMessages,
                                       JsonObject requestJsonObject)
     * @param jsonCopy               validation details in json format
     * @param formValidation         List of keys
     * @param errorMessages          stores the validation-keys and object
     * @param requestJsonObject      formData
     *{@return True if key value is correct}{@code false} if value length, value is invalid ,value type is wrong.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean recursiveValidation(JsonObject jsonCopy, JsonObject formValidation, JsonObject errorMessages,
                                       JsonObject requestJsonObject) {
        boolean overallResult = true;
        List<String> validationKeys = JsonUtils.getKeys(formValidation);
        for (String validationKey : validationKeys) {
             JsonElement sampleObject = jsonCopy.get(validationKey);

            if (sampleObject instanceof JsonObject) {
                JsonObject errorJson = new JsonObject();
                overallResult = overallResult & recursiveValidation(jsonCopy.getAsJsonObject(validationKey),
                        formValidation.getAsJsonObject(validationKey), errorJson, requestJsonObject);
                errorMessages.add(validationKey, errorJson);
            } else {
                boolean result = true;
                String sampleString = GsonUtility.optString(jsonCopy,validationKey);
                boolean sampleNotEmpty = false;
                if (sampleString.length() > 0) {
                    sampleNotEmpty = true;
                }

                JsonObject record = formValidation.getAsJsonObject(validationKey);
                String required = GsonUtility.optString(record,"required");
                String requiredIf = GsonUtility.optString(record,"requiredIf");
                boolean reqIf = false;
                if (requiredIf.length() > 0) {
                    String condition = record.get("condition").getAsString();
                    String rhsValue = record.get("value").getAsString();

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

                String type = GsonUtility.optString(record,"type");
                Integer maxLength = GsonUtility.optInt(record,"maxLength");
                Integer minLength = GsonUtility.optInt(record,"minLength");
                Integer length = GsonUtility.optInt(record,"length");

                if (required.length() > 0 || reqIf) {
                    if (!isRequired(sampleString)) {
                        errorMessages.addProperty(validationKey, "Please enter the mandatory " +
                                "field " + validationKey);
                        result = false;
                    }
                }

                if (sampleNotEmpty) {
                    if (type.length() > 0) {
                        if ("custom".equals(type)) {
                            if (!sampleString.matches(GsonUtility.optString(record,"expression"))) {
                                errorMessages.addProperty(validationKey, "The custom type is " + "invalid ");
                                result = false;
                            }
                        } else {
                            String typeArray[] = type.split(",");
                            for (String typeEntries : typeArray) {
                                if (!isOfType(sampleString, typeEntries)) {
                                	String errorMessage = GsonUtility.optStringValue(record,"errorMessage"," is invalid ");
                                    GsonUtility.accumulate(errorMessages,validationKey, errorMessage);
                                    result = false;

                                }
                            }
                        }
                    }

                    if (maxLength > 0) {
                        if (!maxLength(sampleString, maxLength)) {
                            errorMessages.addProperty(validationKey, "max-length of this field is " +
                                    "" + maxLength);
                            result = false;
                        }
                    }

                    if (minLength > 0) {
                        if (!minLength(sampleString, minLength)) {
                            errorMessages.addProperty(validationKey, "minLength of this field is " +
                                    "" + minLength);
                            result = false;
                        }
                    }

                    if (length > 0) {
                        if (length != sampleString.length()) {
                            errorMessages.addProperty(validationKey, "length of this field is " +
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
   
    /**
     * validateJson(JsonObject jsFunction, JsonObject requestJsonObject, JsonObject errorMessage)
     * it validate the form data with groovy
     * @param jsFunction    				formData
     * @param requestJsonObject				copy of formData
     * @param errorMessage					messages for which type of value and value length  
     * {@return true if passed parameter is saved in groovy object} {@code false} otherwise,
     * {@code false} if error message already present in object,
     */
	private boolean validateJson(JsonObject jsFunction, JsonObject requestJsonObject, JsonObject errorMessage) {
        String code = jsFunction.get("").getAsString();
        String functionName = jsFunction.get("name").getAsString();
        if (jsFunction.has("language")) {
            String language = jsFunction.get("language").getAsString();
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
            JsonObject actualMessage = new Gson().fromJson(returnErrorMessage,JsonObject.class);
            if (!actualMessage.entrySet().isEmpty()) {
                errorMessage.add("result", actualMessage);
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