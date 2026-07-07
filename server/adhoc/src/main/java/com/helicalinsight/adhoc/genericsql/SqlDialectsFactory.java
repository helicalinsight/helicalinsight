package com.helicalinsight.adhoc.genericsql;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.JsonUtils;

/**
 * Factory class for producing objects of type SqlQuery. Implementations represent variations in SQL query dialects.
 * @author Rajasekhar
 * Created by user on 5/11/2016.
 */
class SqlDialectsFactory {
	/**
     * Gets an instance of SqlQuery based on the specified type.
     *
     * @param type 							 type of SQL dialect
     * @return an instance of SqlQuery corresponding to the specified type
     * @throws IllegalArgumentException 	if the type is null or empty
     * @throws ConfigurationException   	if there is an unknown configuration of sqlImplementations or if it is not configured
     */
    public static SqlQuery getDialectOfType(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type is null or empty");
        }

        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        if (!settingsJson.has("sqlImplementations")) {
            return new SqlQueryImplementation();
        }

        JsonElement sqlImplementations = settingsJson.getAsJsonObject("sqlImplementations");
        if (sqlImplementations.isJsonObject()) {
            JsonElement implementations = ((JsonObject) sqlImplementations).get("implementation");
            if (implementations.isJsonObject()) {
                JsonObject jsonObject = implementations.getAsJsonObject();
                String string = jsonObject.get("type").getAsString();
                if (type.equalsIgnoreCase(string)) {
                    return FactoryMethodWrapper.getTypedInstance(jsonObject.get("class").getAsString(), SqlQuery.class);
                }
            } else if (implementations.isJsonArray()) {
                //Many implementations are there
                JsonArray array = implementations.getAsJsonArray();
                for (int index = 0; index < array.size(); index++) {
                    JsonObject jsonObject = array.get(index).getAsJsonObject();
                    if (type.equalsIgnoreCase(jsonObject.get("type").getAsString())) {
                        return FactoryMethodWrapper.getTypedInstance(jsonObject.get("class").getAsString(), SqlQuery.class);
                    }
                }
            } else {
                throw new ConfigurationException("Unknown configuration of sqlImplementations");
            }
        } else {
            throw new ConfigurationException("Configure the sqlImplementations with mandatory attribute");
        }
        throw new ConfigurationException("No relevant type is configured for the Sql Query Implementation");
    }
}
