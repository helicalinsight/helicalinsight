package com.helicalinsight.adhoc.security;

import com.google.gson.JsonObject;
import com.helicalinsight.AbstractSecurityRules;

/**
 * Default security rules implementation for adhoc reporting.
 * This class extends the {@link AbstractSecurityRules} class and provides default implementations for security checks.
 * Created by Helical on 3/9/2021.
 */
@Deprecated
public class Default extends AbstractSecurityRules {
	
	/**
     * Determines whether this security rules implementation is thread-safe to cache.
     * @return {@code false}, indicating that this implementation is not thread-safe to cache.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return false;
    }
    /**
     * Checks for SQL injection in the provided form data.
     *
     * @param formData 			 JSON object containing the form data to be checked.
     * @param key      			 key corresponding to the form data.
     * @param obj      			 An object associated with the form data.
     * @return original form data JSON object, as no SQL injection check is performed.
     * @throws Exception If any error occurs during the SQL injection check process.
     */
	@Override
	public JsonObject checkSqlInjection(JsonObject formData, String key, Object obj) throws Exception {
		return formData;
	}
}
