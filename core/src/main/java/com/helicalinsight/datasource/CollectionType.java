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

/**
 * Created by user on 3/29/2017.
 *
 * @author Rajasekhar
 */
public class CollectionType {

    private JSONObject requestParameters;
    private String parameter;
    private String defaultValue;
    private String parameterFromRequest;
    private String openQuote;
    private String closeQuote;
    private boolean areQuotesConfigured;

    public CollectionType(JSONObject requestParameters, String parameter, String defaultValue,
                          String parameterFromRequest, String openQuote, String closeQuote,
                          boolean areQuotesConfigured) {
        this.requestParameters = requestParameters;
        this.parameter = parameter;
        this.defaultValue = defaultValue;
        this.parameterFromRequest = parameterFromRequest;
        this.openQuote = openQuote;
        this.closeQuote = closeQuote;
        this.areQuotesConfigured = areQuotesConfigured;
    }

    public String collectionType() {
        if (parameterFromRequest != null) {
            //Check if it is an array
            if (parameterFromRequest.matches("^\\[.*\\]$")) {
                JSONArray array;
                try {
                    array = requestParameters.getJSONArray(parameter);
                } catch (JSONException ex) {
                    throw new IllegalStateException("Parameter is not an array. Expecting array.", ex);
                }

                if (!array.isEmpty()) {
                    defaultValue = "";
                    for (int index = 0; index < array.size(); index++) {
                        if (areQuotesConfigured) {
                            defaultValue = defaultValue + openQuote + array.getString(index) + closeQuote + ",";
                        } else {
                            //For compatibility with old configuration
                            defaultValue = defaultValue + "'" + array.getString(index) + "'" + ",";
                        }
                    }
                    //Remove last , (comma)
                    defaultValue = defaultValue.substring(0, defaultValue.length() - 1);
                }
            } else {
                throw new EfwdServiceException(parameter + " is not well formed. Expected as a JSONArray as it is of " +
                        "type Collection.");
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
