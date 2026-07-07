package com.helicalinsight.adhoc.metadata;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.GroovyUtils;

/**
 * MetadataSecurityExpressionValidator
 * This class implements the IComponent interface and provides a method to execute security expression validation.
 * 
 * @author Somen
 * Created on 8/8/2016.
 */
@SuppressWarnings("ALL")
public class MetadataSecurityExpressionValidator implements IComponent {
	/**
     * Executes the component to validate security expressions.
     *
     * @param jsonFormData 		 JSON string containing the form data.
     * @return A JSON string representing the validation result.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject request = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String executionType = request.get("executionType").getAsString();
        JsonObject data = request.getAsJsonObject("data");
        String conditionString = data.get("condition").getAsString();
        String filterString = GsonUtility.optString(data,"filter");

        JsonObject responseCondition;
        JsonObject responseFilter;
        JsonObject responseComponent = new JsonObject();
        String message = "";
        try {
            String clazz = MetadataSecurityObjectFactory.getClass(executionType);
            Class aClass = FactoryMethodWrapper.getClass(clazz);
            Object object = aClass.newInstance();
            if (data.has("filter")) {
                data.get("filter").getAsString();
                Method method = aClass.getDeclaredMethod("validateFilter", String.class);
                responseFilter = (JsonObject) method.invoke(object, filterString);

                message = GroovyUtils.processResult(responseFilter, "Filter");
            }
            Method method = aClass.getDeclaredMethod("validateCondition", String.class);
            responseCondition = (JsonObject) method.invoke(object, conditionString);
        } catch (Exception exception) {
            throw new FormValidationException("There was some problem. Message: " + ExceptionUtils
                    .getRootCauseMessage(exception));
        }
        message = StringUtils.isNotEmpty(message) ? message + ". " : "";
        message =message+ GroovyUtils.processResult(responseCondition, "Condition");
        responseComponent.addProperty("message", message);
        return responseComponent.toString();
    }
    /**
     * Indicates whether the component is thread-safe to cache.
     * @return {@code true} if the component is thread-safe to cache.
     */
    public boolean isThreadSafeToCache() {
        return true;
    }
}