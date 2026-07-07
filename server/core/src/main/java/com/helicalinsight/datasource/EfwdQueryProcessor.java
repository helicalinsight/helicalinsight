package com.helicalinsight.datasource;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.AbstractSecurityRules;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

import com.google.gson.JsonElement;
/**
 * EfwdQueryProcessor
 * The class is part of the efwd layer. Initially the class was part of
 * JDBCDriver class. Later refactored as a separate class.
 *
 * @author Rajasekhar
 * @since 1.1
 */
@Component
public class EfwdQueryProcessor {

    private static final Logger logger = LoggerFactory.getLogger(EfwdQueryProcessor.class);

	/**
	 * Returns the query to be executed by the efwd layer after formatting the
	 * variable content in the query. The parameters in the query are replaced with
	 * the default parameters from the efwd file DataMap's Parameters. If the http
	 * request consists of parameters then they will be used.
	 *
	 * @param dataMapFromEfwd   The content of the data map tag of efwd
	 * @param requestParameters The request parameter json
	 * @return The query to be executed by the efwd engine
	 */

	/**
	 * getQuery(JsonObject dataMapFromEfwd, JsonObject requestParameters) using gson
	 * 
	 * @param JsonObject dataMapTagContent          content of the data map tag of efwd
	 * @param JsonObject requestParameterJson	     request parameters
	 * @return String query
	 */
	public String getQuery(JsonObject dataMapFromEfwd, JsonObject requestParameters) {
		String query;
		if (dataMapFromEfwd.has("Query")) {
			query = dataMapFromEfwd.get("Query").getAsString();
		} else {
			throw new EfwdServiceException("Query is missing in the requested DataMap.");
		}
		if (dataMapFromEfwd.has("Parameters")) {
			try {

				JsonObject parametersObject = dataMapFromEfwd.getAsJsonObject("Parameters");
				JsonElement parameterElement = parametersObject.get("Parameter");
				JsonArray parametersArray = new JsonArray();
				if (parameterElement.isJsonArray()) {
					// If it's already an array, assign it directly
					parametersArray = parameterElement.getAsJsonArray();
				} else if (parameterElement.isJsonObject()) {
					// If it's an object, wrap it in a new JsonArray
					parametersArray.add(parameterElement.getAsJsonObject());
				}
				int parameterCount = parametersArray.size();
				for (int count = 0; count < parameterCount; count++) {
					JsonObject parameterJson = parametersArray.get(count).getAsJsonObject();
					String name = parameterJson.get("name").getAsString();
                    String expression = "${" + name + "}";
                    if (StringUtils.contains(query, expression)) {
                        //sql injection check
                        try{
                            checkForSqlInjection(parameterJson,requestParameters);
                        }catch (SecurityException e){
                            throw new SecurityException(e.getMessage());
                        }
						query = replace(requestParameters, query, parameterJson, name, expression);
					}
				}
			} catch (JsonSyntaxException ex) {
				throw new EfwdServiceException("Malformed Efwd file. Error details - ", ex);
			}
		
		}
		if (logger.isDebugEnabled()) {
			logger.debug("The processed query is " + query);
		}
		return query;
	}
		/**
	 * getQuery2(JsonObject dataMapFromEfwd, JsonObject requestParameters) using gson
	 * @param JsonObject dataMapFromEfwd           content of data Map
	 * @param JsonObject requestParameters         request Parameters
	 * @return query
	 */
	public String getQuery2(JsonObject dataMapFromEfwd, JsonObject requestParameters) {
		String query;
		if (dataMapFromEfwd.has("query")) {
			query = dataMapFromEfwd.get("query").getAsString();
		} else {
			throw new EfwdServiceException("Query is missing in the requested DataMap.");
		}
		if (dataMapFromEfwd.has("parameters")) {
			try {
				JsonArray parametersArray = dataMapFromEfwd.getAsJsonArray("parameters");

				int parameterCount = parametersArray.size();
				for (int count = 0; count < parameterCount; count++) {
					JsonObject parameterJson = parametersArray.get(count).getAsJsonObject();
					String name = parameterJson.get("name").getAsString();

					String expression = "${" + name + "}";
					if (StringUtils.contains(query, expression)) {
						// sql injection check
						try {
							checkForSqlInjection(parameterJson, requestParameters);
						} catch (SecurityException e) {
							throw new SecurityException(e.getMessage());
						}

						// query = replace(requestParameters, query, parameterJson, name, expression);
					}
				}
			} catch (JsonSyntaxException ex) {
				throw new EfwdServiceException("Malformed Efwd file. Error details - ", ex);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("The processed query is " + query);
		}
		return query;
	}


	/**
	 * replace using gson
	 * replace(JsonObject requestParameters, String query, JsonObject parameterJson, String parameter,
			String queryDollarPart)
	 * Replaces placeholders in the given query with corresponding parameter values.
	 *
	 * @param requestParameters   object containing request parameters.
	 * @param query               original query with placeholders to be replaced.
	 * @param parameterJson       object containing parameter configuration.
	 * @param parameter           parameter name used as a placeholder in the query.
	 * @param queryDollarPart     part of the query to be replaced (e.g., "$$parameter$$").
	 * @return The modified query with placeholders replaced by values.
	 */
	private String replace(JsonObject requestParameters, String query, JsonObject parameterJson, String parameter,
			String queryDollarPart) {
        //TODO check this method
        String defaultValue = null;
        if (parameterJson.has("default")) {
            defaultValue = parameterJson.get("default").getAsString();
        }

        String parameterValueFromRequest = null;
        if (requestParameters.has(parameter)) {
			JsonElement jsonElement =requestParameters.get(parameter);
			if (jsonElement != null && jsonElement.isJsonPrimitive()) {
				parameterValueFromRequest = requestParameters.get(parameter).getAsString();
			}else{
				parameterValueFromRequest= requestParameters.get(parameter).toString();
			}
        }

        String type = parameterJson.get("type").getAsString();

        boolean areQuotesConfigured = parameterJson.has("openQuote") && parameterJson.has("closeQuote");
        String openQuote = "";
        if (parameterJson.has("openQuote")) {
            openQuote = parameterJson.get("openQuote").getAsString();
        }

        String closeQuote = "";
        if (parameterJson.has("closeQuote")) {
            closeQuote = parameterJson.get("closeQuote").getAsString();
        }

        //Replace the default value with the new value
        if ("Collection".equalsIgnoreCase(type)) {
            defaultValue = new CollectionType(requestParameters, parameter, defaultValue, parameterValueFromRequest,
                    openQuote, closeQuote, areQuotesConfigured).collectionType();
        } else {
            defaultValue = new OtherTypes(defaultValue, parameterValueFromRequest, openQuote, closeQuote,
                    areQuotesConfigured, type).otherTypes();
        }

        if (defaultValue != null) {
            query = query.replace(queryDollarPart, defaultValue);
        }
        return query;
    }

    private static class OtherTypes {

        private String defaultValue;
        private String parameterValueFromRequest;
        private String openQuote;
        private String closeQuote;
        private boolean areQuotesConfigured;
        private String type;

        private OtherTypes(String defaultValue, String parameterValueFromRequest, String openQuote,
                           String closeQuote, boolean areQuotesConfigured, String type) {
            this.defaultValue = defaultValue;
            this.parameterValueFromRequest = parameterValueFromRequest;
            this.openQuote = openQuote;
            this.closeQuote = closeQuote;
            this.areQuotesConfigured = areQuotesConfigured;
            this.type = type;
        }

        public String otherTypes() {
            // If it is none of the above types then
            if (parameterValueFromRequest != null) {
                if (!parameterValueFromRequest.isEmpty()) {
                    if (areQuotesConfigured) {
                        defaultValue = openQuote + parameterValueFromRequest + closeQuote;
                    } else if ("Numeric".equalsIgnoreCase(type)) {
                        defaultValue = parameterValueFromRequest;
                    } else {
                        //For compatibility sake. To maintain the consistency in case of String
                        defaultValue = "'" + parameterValueFromRequest + "'";
                    }
                } else {
                    if (defaultValue == null) {
                        throw new EfwdServiceException("For one of the parameters, parameter value is present in the " +
                                "request but is empty. Default value is also null. Can't process query.");
                    }
                }
            } else if (defaultValue == null) {
                throw new QueryParameterMissingException("Default parameter value is missing. Can't process query. "
                        + "Please provide default value or parameter value from request.");
            }
            return defaultValue;
        }
    }


	/**
	 * checkForSqlInjection(JsonObject formData, JsonObject requestParams) using gson
	 * Checks for SQL injection vulnerabilities in form data.
	 * @param JsonObject formData          formData
	 * @param JsonObject requestParams     request parameters
	 * @return Object of formData
	 */
	public Object checkForSqlInjection(JsonObject formData, JsonObject requestParams) {
		Map<String, String> propsMap = ApplicationUtilities.getDefaultsMap();
		for (Map.Entry<String, String> entryMap : propsMap.entrySet()) {
			if (entryMap.getKey().startsWith("$.")) {
				String key = entryMap.getKey();
				String value = entryMap.getValue();
				String className = AbstractSecurityRules.getAdhocSecurityRulesClass(value);
				try {
					if (ApplicationUtilities.isClass(className)) {
						Object aClass = FactoryMethodWrapper.getClass(className);
						AbstractSecurityRules rules = (AbstractSecurityRules) FactoryMethodWrapper
								.getUntypedInstance(className);
						try {
							rules.checkSqlInjection(formData, key, requestParams);
						} catch (Exception e) {
							throw new SecurityException(e.getMessage());
						}
					}

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return formData;
	}
}    

              