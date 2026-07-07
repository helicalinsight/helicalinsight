package com.helicalinsight.validation;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.framework.FrameworkObject;


/**
 * This class is used to validate formData from user request which extends <p>FrameworkObject</p>
 * @author Somen  on 03/07/2015
 */
public interface IValidation extends FrameworkObject {
	/**
	 * isValid(JsonObject httpRequestJson, JsonObject jsonObject)
	 * @param httpRequestJson   formData
	 * @param jsonObject        xml data in JsonObject
	 * {@return True if validation is successful}{@code false} if data is not correct.
	 */
    boolean isValid(JsonObject httpRequestJson, JsonObject jsonObject);

}