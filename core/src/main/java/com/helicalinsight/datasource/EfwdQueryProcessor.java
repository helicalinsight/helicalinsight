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

package com.helicalinsight.datasource;

import com.helicalinsight.efw.exceptions.EfwdServiceException;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The class is part of the efwd layer. Initially the class was part of
 * JDBCDriver class. Later refactored as a separate class.
 *
 * @author Rajasekhar
 */
@Component
public class EfwdQueryProcessor {

    private static final Logger logger = LoggerFactory.getLogger(EfwdQueryProcessor.class);

    /**
     * Returns the query to be executed by the efwd layer after formatting the variable content in
     * the query. The parameters in the query are replaced with the default parameters from the
     * efwd file DataMap's Parameters. If the http request consists of parameters then they will
     * be used.
     *
     * @param dataMapFromEfwd   The content of the data map tag of efwd
     * @param requestParameters The request parameter json
     * @return The query to be executed by the efwd engine
     */
    public String getQuery(JSONObject dataMapFromEfwd, JSONObject requestParameters) {
        String query;
        if (dataMapFromEfwd.has("Query")) {
            query = dataMapFromEfwd.getString("Query");
        } else {
            throw new EfwdServiceException("Query is missing in the requested DataMap.");
        }
        if (dataMapFromEfwd.has("Parameters")) {
            try {
                JSONArray parametersArray = dataMapFromEfwd.getJSONArray("Parameters");

                int parameterCount = parametersArray.size();
                for (int count = 0; count < parameterCount; count++) {
                    JSONObject parameterJson = parametersArray.getJSONObject(count);
                    String name = parameterJson.getString("@name");

                    String expression = "${" + name + "}";

                    if (StringUtils.contains(query, expression)) {
                        query = replace(requestParameters, query, parameterJson, name, expression);
                    }
                }
            } catch (JSONException ex) {
                throw new EfwdServiceException("Malformed Efwd file. Error details - ", ex);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("The processed query is " + query);
        }
        return query;
    }

    private String replace(JSONObject requestParameters, String query, JSONObject parameterJson, String parameter,
                           String queryDollarPart) {
        String defaultValue = null;
        if (parameterJson.has("@default")) {
            defaultValue = parameterJson.getString("@default");
        }

        String parameterValueFromRequest = null;
        if (requestParameters.has(parameter)) {
            parameterValueFromRequest = requestParameters.getString(parameter);
        }

        String type = parameterJson.getString("@type");

        boolean areQuotesConfigured = parameterJson.has("@openQuote") && parameterJson.has("@closeQuote");
        String openQuote = "";
        if (parameterJson.has("@openQuote")) {
            openQuote = parameterJson.getString("@openQuote");
        }

        String closeQuote = "";
        if (parameterJson.has("@closeQuote")) {
            closeQuote = parameterJson.getString("@closeQuote");
        }

        //Replace the default value with the new value
        if ("Collection".equalsIgnoreCase(type)) {
            defaultValue = new CollectionType(requestParameters, parameter, defaultValue, parameterValueFromRequest,
                    openQuote, closeQuote, areQuotesConfigured).collectionType();
        } else {
            defaultValue = new OtherTypes(defaultValue, parameterValueFromRequest, openQuote, closeQuote,
                    areQuotesConfigured).otherTypes();
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

        private OtherTypes(String defaultValue, String parameterValueFromRequest, String openQuote,
                           String closeQuote, boolean areQuotesConfigured) {
            this.defaultValue = defaultValue;
            this.parameterValueFromRequest = parameterValueFromRequest;
            this.openQuote = openQuote;
            this.closeQuote = closeQuote;
            this.areQuotesConfigured = areQuotesConfigured;
        }

        public String otherTypes() {
            // If it is none of the above types then
            if (parameterValueFromRequest != null) {
                if (!parameterValueFromRequest.isEmpty()) {
                    if (areQuotesConfigured) {
                        defaultValue = openQuote + parameterValueFromRequest + closeQuote;
                    } else {
                        //For compatibility sake. To maintain the consistency in case of String
                        defaultValue = "'" + parameterValueFromRequest + "'";
                    }
                } else {
                    if (defaultValue == null) {
                        throw new EfwdServiceException("For one of the parameters, parameter value is present in the " +
                                "" + "request but is empty. Default value is also null. Can't process query.");
                    }
                }
            } else if (defaultValue == null) {
                throw new QueryParameterMissingException("Default parameter value is missing. Can't process query. "
                        + "Please provide default value or parameter value from request.");
            }
            return defaultValue;
        }
    }
}