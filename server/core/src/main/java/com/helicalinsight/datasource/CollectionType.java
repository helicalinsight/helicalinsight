package com.helicalinsight.datasource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.efw.exceptions.EfwdServiceException;

/**
 * CollectionType
 * This class handles the conversion of values from JSON parameters into a string representation for collection types.
 *
 * Created by user on 3/29/2017.
 * @author Rajasekhar
 */
public class CollectionType {

	private JsonObject requestParameters;
	private String parameter;
	private String defaultValue;
	private String parameterFromRequest;
	private String openQuote;
	private String closeQuote;
	private boolean areQuotesConfigured;

	
	public CollectionType(JsonObject requestParameters, String parameter, String defaultValue,
			String parameterFromRequest, String openQuote, String closeQuote, boolean areQuotesConfigured) {
		this.requestParameters = requestParameters;
		this.parameter = parameter;
		this.defaultValue = defaultValue;
		this.parameterFromRequest = parameterFromRequest;
		this.openQuote = openQuote;
		this.closeQuote = closeQuote;
		this.areQuotesConfigured = areQuotesConfigured;
	}

	/**
	 * newCollectionType()  using gson
	 * This method is responsible for fetching values
	 * from formData parameters and converting them into a string representation.
	 *
	 * @return The string representation of values fetched from the JSON object.
	 * @throws IllegalStateException          if the parameter is not in array form.
	 * @throws EfwdServiceException           if the JsonObject does not match the
	 *                                        expected format "^\\[.*\\]$" for a
	 *                                        collection.
	 * @throws QueryParameterMissingException if the default parameter value is
	 *                                        missing, and no parameter value is
	 *                                        available from the request.
	 */
	public String collectionType() {
		if (parameterFromRequest != null) {
			// Check if it is an array
			if (parameterFromRequest.matches("^\\[.*\\]$")) {
				JsonArray array;
				try {
					JsonElement elem = requestParameters.get(parameter);
					String val=null;
					if (elem != null && elem.isJsonPrimitive()) {
						val=elem.getAsString();
						array  = JsonParser.parseString(val).getAsJsonArray();
					}else{
					 array = elem.getAsJsonArray();
					 }
					//array = requestParameters.getAsJsonArray(parameter);
					
				} catch (JsonSyntaxException ex) {
					throw new IllegalStateException("Parameter is not an array. Expecting array.", ex);
				}

				if (!array.isEmpty()) {
					defaultValue = "";
					for (int index = 0; index < array.size(); index++) {
						if (areQuotesConfigured) {
							defaultValue = defaultValue + openQuote + array.get(index).getAsString() + closeQuote + ",";
						} else {
							// For compatibility with old configuration
							defaultValue = defaultValue + "'" + array.get(index).getAsString() + "'" + ",";
						}
					}
					// Remove last , (comma)
					defaultValue = defaultValue.substring(0, defaultValue.length() - 1);
				}
			} else {
				throw new EfwdServiceException(
						parameter + " is not well formed. Expected as a JSONArray as it is of " + "type Collection.");
			}
		} else {
			if (defaultValue == null) {
				throw new QueryParameterMissingException("Default parameter value is missing. Can't process query. "
						+ "Please provide default value or parameter value from request.");
			}
		}
		return defaultValue;
	}

}
	