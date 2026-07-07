package com.helicalinsight.adhoc.security;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.AbstractSecurityRules;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

/**
 * Security rules implementation for detecting SQL strings in adhoc reporting.
 * This class extends the {@link AbstractSecurityRules} class and provides methods to detect SQL strings in form data.
 * Created by Helical on 3/13/2021.
 */
@Deprecated
public class SqlDetect extends AbstractSecurityRules {
    private Map<String,String> propsMap= ApplicationUtilities.getDefaultsMap();
    /**
     * Determines whether this security rules implementation is thread-safe to cache.
     * @return {@code false} indicating that this implementation is not thread-safe to cache.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return false;
    }
    /**
     * Checks for SQL patterns in the provided string value.
     * 
     * @param value 		 string value to be checked for SQL patterns.
     * @return {@code null} if a SQL pattern is found in the value, otherwise returns the original value.
     */
    public Object checkForSqlString(String value){
        Pattern sqlPattern=Pattern.compile(propsMap.get("sqlpattern"));
        Matcher m=sqlPattern.matcher(value);
        boolean matchFound = m.find();
        if(matchFound){
            return null;
        }
        return value;
    }
    /**
     * Checks for SQL injection in the provided form data.
     *
     * @param formData 		 JSON object containing the form data to be checked.
     * @param key      		 key corresponding to the form data.
     * @param obj      		 An object associated with the form data.
     * @return original form data JSON object, if no SQL injection is detected.
     * @throws SecurityException If SQL injection is detected in the form data.
     */
	@Override
	public JsonObject checkSqlInjection(JsonObject formData, String key, Object obj) throws Exception {
		DocumentContext jsonDocumentContext = JsonPath.parse(formData, Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS));
        Object formDataObject = jsonDocumentContext.read(key);
        //TODO EFW Report Response
        if(obj instanceof JsonObject){
            if(formDataObject instanceof String){
            	JsonElement jsonElement = ((JsonObject) obj).get((String) formDataObject);
                if(!jsonElement.isJsonNull()){
                    String value=((JsonObject) obj).get((String) formDataObject).getAsString();
                    Object result=checkForSqlString(value);
                    if(null==result){
                        throw new SecurityException("The filter value is unaccepted, please try with someother value");
                    }
                }else if(formDataObject instanceof String){
                    Object result=checkForSqlString((String) formDataObject);
                    if(null==result){
                        throw new SecurityException("The filter value is unaccepted, please try with someother value");
                    }
                }
            }
        }else if(formDataObject instanceof String){
            Object result=checkForSqlString(formDataObject.toString());
            if(null==result){
                throw new SecurityException("The filter value is unaccepted, please try with someother value");
            }
        }
        return formData;
	}
	
}
