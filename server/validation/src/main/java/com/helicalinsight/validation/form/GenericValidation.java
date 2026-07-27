package com.helicalinsight.validation.form;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.validation.IValidation;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

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
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to valid formData which implements<p>IValidation</p>
 *
 */

@Component
public class GenericValidation implements IValidation {

    private static final Logger logger = LoggerFactory.getLogger(GenericValidation.class);

    private static final Configuration JSON_PATH_CONF = Configuration.defaultConfiguration()
            .addOptions(Option.SUPPRESS_EXCEPTIONS);

    private static Map<String, String> regexMap;

    private static Map<String, String> regexMap() {
        if (regexMap == null) {
            synchronized (GenericValidation.class) {
                if (regexMap == null) {
                    try {
                        regexMap = new PropertiesFileReader().read("Admin", "regex.properties");
                    } catch (Throwable ex) {
                        logger.warn("Could not load regex.properties; type checks will be skipped until init()", ex);
                        regexMap = new java.util.HashMap<>();
                    }
                    if (regexMap == null) {
                        regexMap = new java.util.HashMap<>();
                    }
                }
            }
        }
        return regexMap;
    }

    /**
     * Returns an unmodifiable view of the validation regex map loaded from
     * {@code System/Admin/regex.properties}. Available to Groovy validators as {@code regexMap}.
     */
    public static Map<String, String> getRegexMap() {
        return java.util.Collections.unmodifiableMap(new LinkedHashMap<>(regexMap()));
    }

    /**
     * Resolves a value from {@code json} using Jayway JsonPath.
     * Accepts dotted paths ({@code a.b[0].c}) or full JsonPath ({@code $.a.b[0].c}).
     *
     * @param json       form data
     * @param expression path to the target key
     * @return value as string, or {@code null} if missing
     */
    public static String jsonNavigator(JsonObject json, String expression) {
        if (json == null || StringUtils.isBlank(expression)) {
            return null;
        }
        String path = toJsonPath(expression);
        Object value = JsonPath.using(JSON_PATH_CONF).parse(json.toString()).read(path);
        if (value == null) {
            return null;
        }
        if (value instanceof Map || value instanceof List) {
            return new Gson().toJson(value);
        }
        return String.valueOf(value);
    }

    /**
     * Normalizes a dotted / bracket path into a JsonPath expression.
     */
    static String toJsonPath(String expression) {
        String trimmed = expression.trim();
        if (trimmed.startsWith("$")) {
            return trimmed;
        }
        return "$." + trimmed;
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
	 * {@return True if validation is successful} {@code false} if data is not correct.
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
        // XmlMapper typically unwraps <formData>; keep a safety unwrap if the root remains.
        JsonObject formValidation = unwrapFormData(validationRulesJson);

        JsonObject requestJsonObject = jsonCopy.getAsJsonObject();

        JsonObject evaluator = extractEvaluator(formValidation);
        if (evaluator != null) {
            overallResult = validateJson(evaluator, requestJsonObject, errorMessages);
            if (errorMessages.has("result") && errorMessages.get("result").isJsonObject()) {
                xmlRuleJson.add("message", errorMessages.getAsJsonObject("result"));
                return false;
            }
        }

        JsonObject fieldErrors = new JsonObject();
        boolean fieldResult = validateWithJsonPath(formValidation, fieldErrors, requestJsonObject);
        overallResult = overallResult && fieldResult;
        xmlRuleJson.add("message", fieldErrors);
        return overallResult;
    }

    /**
     * Prefer the inner {@code formData} object when the XML root was preserved.
     */
    private JsonObject unwrapFormData(JsonObject validationRulesJson) {
        if (validationRulesJson.has("formData") && validationRulesJson.get("formData").isJsonObject()
                && validationRulesJson.entrySet().size() == 1) {
            return validationRulesJson.getAsJsonObject("formData");
        }
        return validationRulesJson;
    }

    /**
     * Supports both {@code jsFunction} and {@code jsValidator} complex evaluators.
     * Removes the evaluator element so it is not treated as a field rule.
     */
    private JsonObject extractEvaluator(JsonObject formValidation) {
        String key = null;
        if (formValidation.has("jsValidator")) {
            key = "jsValidator";
        } else if (formValidation.has("jsFunction")) {
            key = "jsFunction";
        }
        if (key == null) {
            return null;
        }
        JsonObject evaluator = formValidation.getAsJsonObject(key);
        formValidation.remove(key);
        formValidation.remove("mandatory");
        return evaluator;
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
        return regexMap().get(type);
    }

    /**
     * Validates form data by resolving each leaf rule through JsonPath.
     * Nested rule containers are flattened to dotted paths (e.g. {@code EmailSettings.Subject}).
     *
     * @param formValidation    validation rules (flat or nested)
     * @param errorMessages     accumulates validation errors
     * @param requestJsonObject root form data for JsonPath lookups
     * @return true if all rules pass
     */
    @SuppressWarnings("ConstantConditions")
    public boolean validateWithJsonPath(JsonObject formValidation, JsonObject errorMessages,
                                        JsonObject requestJsonObject) {
        boolean overallResult = true;
        if (formValidation == null) {
            return true;
        }

        Map<String, JsonObject> leafRules = flattenLeafRules(formValidation);
        for (Map.Entry<String, JsonObject> entry : leafRules.entrySet()) {
            String fieldPath = entry.getKey();
            JsonObject record = entry.getValue();
            String validationKey = fieldPath.contains(".")
                    ? fieldPath.substring(fieldPath.lastIndexOf('.') + 1)
                    : fieldPath;

            boolean result = true;
            String sampleString = jsonNavigator(requestJsonObject, fieldPath);
            if (sampleString == null) {
                sampleString = "";
            }
            boolean sampleNotEmpty = sampleString.length() > 0;

            String required = GsonUtility.optString(record, "required");
            String requiredIf = GsonUtility.optString(record, "requiredIf");
            boolean reqIf = false;
            if (requiredIf.length() > 0) {
                String condition = GsonUtility.optString(record, "condition");
                String rhsValue = GsonUtility.optString(record, "value");

                String val = jsonNavigator(requestJsonObject, requiredIf);
                if (!(NumberUtils.isNumber(val) && NumberUtils.isNumber(rhsValue))) {
                    // if the requiredIf parameter is not in the request then requiredIf is false
                    reqIf = val != null && (condition.isEmpty() || "=".equals(condition) || "==".equals(condition))
                            && val.equalsIgnoreCase(rhsValue);
                    if ("!=".equals(condition) || "<>".equals(condition)) {
                        reqIf = val != null && !val.equalsIgnoreCase(rhsValue);
                    }
                } else if (!condition.isEmpty()) {
                    ExpressionParser parser = new SpelExpressionParser();
                    Expression exp = parser.parseExpression(val + condition + rhsValue);
                    Boolean evaluated = exp.getValue(Boolean.class);
                    reqIf = evaluated != null && evaluated;
                }
            }

            String type = GsonUtility.optString(record, "type");
            Integer maxLength = GsonUtility.optInt(record, "maxLength");
            Integer minLength = GsonUtility.optInt(record, "minLength");
            Integer length = GsonUtility.optInt(record, "length");

            if (isTruthy(required) || reqIf) {
                if (!isRequired(sampleString)) {
                    String message = GsonUtility.optStringValue(record, "requiredMessage",
                            "Please enter the mandatory field " + validationKey);
                    errorBucketForPath(errorMessages, fieldPath).addProperty(validationKey, message);
                    overallResult = overallResult && false;
                    continue;
                }
            }

            if (sampleNotEmpty) {
                if (type.length() > 0) {
                    if ("custom".equals(type)) {
                        if (!sampleString.matches(GsonUtility.optString(record, "expression"))) {
                            String errorMessage = GsonUtility.optStringValue(record, "errorMessage",
                                    "The custom type is invalid");
                            errorBucketForPath(errorMessages, fieldPath).addProperty(validationKey, errorMessage);
                            result = false;
                        }
                    } else {
                        String[] typeArray = type.split(",");
                        for (String typeEntries : typeArray) {
                            if (!isOfType(sampleString, typeEntries)) {
                                String errorMessage = GsonUtility.optStringValue(record, "errorMessage", " is invalid ");
                                GsonUtility.accumulate(errorBucketForPath(errorMessages, fieldPath),
                                        validationKey, errorMessage);
                                result = false;
                            }
                        }
                    }
                }

                if (maxLength > 0) {
                    if (!maxLength(sampleString, maxLength)) {
                        errorBucketForPath(errorMessages, fieldPath).addProperty(validationKey,
                                "max-length of this field is " + maxLength);
                        result = false;
                    }
                }

                if (minLength > 0) {
                    if (!minLength(sampleString, minLength)) {
                        errorBucketForPath(errorMessages, fieldPath).addProperty(validationKey,
                                "minLength of this field is " + minLength);
                        result = false;
                    }
                }

                if (length > 0) {
                    if (length != sampleString.length()) {
                        errorBucketForPath(errorMessages, fieldPath).addProperty(validationKey,
                                "length of this field is " + length);
                        result = false;
                    }
                }
            }
            overallResult = overallResult && result;
        }
        return overallResult;
    }

    /**
     * Flattens nested validation rule containers into dotted JsonPath keys mapped to leaf rules.
     */
    private Map<String, JsonObject> flattenLeafRules(JsonObject formValidation) {
        Map<String, JsonObject> leafRules = new LinkedHashMap<>();
        Deque<Map.Entry<String, JsonObject>> stack = new ArrayDeque<>();
        stack.push(new AbstractMap.SimpleEntry<>("", formValidation));

        while (!stack.isEmpty()) {
            Map.Entry<String, JsonObject> current = stack.pop();
            String parentPath = current.getKey();
            JsonObject rules = current.getValue();
            if (rules == null) {
                continue;
            }

            for (Map.Entry<String, JsonElement> entry : rules.entrySet()) {
                String ruleKey = entry.getKey();
                if ("jsFunction".equals(ruleKey) || "jsValidator".equals(ruleKey) || "mandatory".equals(ruleKey)) {
                    continue;
                }
                JsonElement value = entry.getValue();
                if (value == null || !value.isJsonObject()) {
                    continue;
                }
                JsonObject record = value.getAsJsonObject();
                String fieldPath = StringUtils.isBlank(parentPath) ? entry.getKey() : parentPath + "." + entry.getKey();

                if (isNestedRuleContainer(record)) {
                    stack.push(new AbstractMap.SimpleEntry<>(fieldPath, record));
                } else {
                    leafRules.put(fieldPath, record);
                }
            }
        }
        return leafRules;
    }

    /**
     * Returns the error object that should hold messages for the leaf of {@code fieldPath},
     * creating nested objects for parent path segments when needed.
     */
    private JsonObject errorBucketForPath(JsonObject errorMessages, String fieldPath) {
        if (!fieldPath.contains(".")) {
            return errorMessages;
        }
        String[] parts = fieldPath.split("\\.");
        JsonObject current = errorMessages;
        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i]) || !current.get(parts[i]).isJsonObject()) {
                current.add(parts[i], new JsonObject());
            }
            current = current.getAsJsonObject(parts[i]);
        }
        return current;
    }

    /**
     * Treats common truthy attribute values as required ({@code true}, {@code yes}, {@code required}, {@code 1}).
     */
    private boolean isTruthy(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        String normalized = value.trim().toLowerCase();
        return "true".equals(normalized) || "yes".equals(normalized)
                || "required".equals(normalized) || "1".equals(normalized);
    }

    /**
     * A nested rule container has child objects; a leaf rule only has primitive attributes
     * (required, type, maxLength, etc.).
     */
    private boolean isNestedRuleContainer(JsonObject record) {
        for (Map.Entry<String, JsonElement> entry : record.entrySet()) {
            JsonElement value = entry.getValue();
            if (value != null && value.isJsonObject()) {
                return true;
            }
        }
        return false;
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
        String code = extractEvaluatorCode(jsFunction);
        if (StringUtils.isBlank(code)) {
            logger.error("Complex validator has no script body");
            return true;
        }
        String functionName = GsonUtility.optStringValue(jsFunction, "name", "validate");
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

    /**
     * Xml→JSON converters place CDATA / text under {@code ""}, {@code #cdata}, or {@code #text}.
     */
    private String extractEvaluatorCode(JsonObject jsFunction) {
        for (String key : new String[]{"", "#cdata", "#text"}) {
            if (jsFunction.has(key) && jsFunction.get(key).isJsonPrimitive()) {
                return jsFunction.get(key).getAsString();
            }
        }
        return "";
    }
}
