package com.helicalinsight.adhoc.security;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.AbstractSecurityRules;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

/**
 * Implements strict security rules to prevent SQL injection vulnerabilities in form data.
 * Extends AbstractSecurityRules for security enforcement.
 * Created by Helical on 3/9/2021.
 */
@Deprecated
public class Strict extends AbstractSecurityRules {

	
	/**
     * Checks whether it's safe to cache the security rules.
     * @return {@code false} since strict security rules may change dynamically.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return false;
    }
    /**
     * Checks for SQL injection vulnerabilities in the form data.
     * 
     * @param formData 				 JSON object containing form data.
     * @param key 					 key to access the form data within the JSON object.
     * @param obj 					 object representing the form data.
     * @return The original form data JSON object if it passes security checks.
     * @throws Exception If a security violation is detected.
     */
	@Override
	public JsonObject checkSqlInjection(JsonObject formData, String key, Object obj) throws Exception {
		DocumentContext jsonDocumentContext = JsonPath.parse(formData, Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS));
        Object formDataObject = jsonDocumentContext.read(key);
        if (null != formDataObject) {
            if (formDataObject instanceof JsonArray) {
                if (( (JsonArray) formDataObject).size() > 0) {
                    throw new Exception("Cannot Execute when security is enabled");
                }
            }   
            else if (formDataObject instanceof JsonObject) {
                throw new Exception("Cannot Execute when security is enabled");
            }
            
        }
        return formData;
	}
}
