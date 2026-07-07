package com.helicalinsight.adhoc;

import static com.helicalinsight.efw.utility.ApplicationUtilities.getDefaultsMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;



/**
 * The `FormDataDefaultValuesRuleHandler` class provides methods to set default values in the form data based on
 * certain rules and configurations.
 * 
 * Created by the author on 23/07/2020.
 * @author Somen
 */
@SuppressWarnings("unused")
public class FormDataDefaultValuesRuleHandler {

	/**
     * Sets default values in the provided form data based on certain rules and configurations. The default values are
     * applied to filters and having clauses in the ad-hoc query.
     *
     * @param formData 		JsonObject provides custom filter expression
     */
    public static void setDefaultValuesInFormData(JsonObject formData) {

        String useDefaults = getDefaultsMap().get("adhoc.query.useDefaults");
        if (!"true".equalsIgnoreCase(useDefaults)) {
            return;
        }

        String iterateArray[] = {"filters","having"};
        for(String filterItem:iterateArray)
        if (formData.has(filterItem)) {
            JsonArray filters = formData.getAsJsonArray(filterItem);
            Boolean removeQuery = false;
            String customFilterExpression = GsonUtility.optStringValue(formData,"customFilterExpression",GsonUtility.optStringValue(formData,"customHavingExpression",""));
            String value = getDefaultsMap().get("adhoc.query.filter.emptyInClauseValue");
            JsonArray valueArray = JsonParser.parseString(value).getAsJsonArray();
            String replacement = getDefaultsMap().get("adhoc.query.filter.customExpressionReplaceValue");
            for (int index = 0; index < filters.size(); index++) {
                JsonObject item = filters.get(index).getAsJsonObject();
                if (item.has("values"))
                    if (item.getAsJsonArray("values").size() == 0) {
                        item.add("values", valueArray);
                        String toReplace = "\\$\\{\\s*" + index + "\\s*\\}";
                        customFilterExpression = customFilterExpression.replaceAll(toReplace, replacement);

                        removeQuery = true;
                    } else {
                        String values = item.getAsJsonArray("values").get(0).getAsString();
                        if (values.equals(")")) {
                            item.add("values", valueArray);
                            String toReplace = "\\$\\{\\s*" + index + "\\s*\\}";
                            customFilterExpression = customFilterExpression.replaceAll(toReplace, replacement);

                            removeQuery = true;
                        }
                    }
            }
            if (removeQuery) {
                // formData.put("customFilterExpression", customFilterExpression);
                formData.remove("query");
            }
        }
    }
    /**
     *  It checks each filter, and if a filter is marked as "ignore," it is excluded from the form data. After processing, 
     *  if the "filters" array is empty, the entire property is removed. 
     *
     * @param formData 				form data.
     */
    public static void ignoreFilter(JsonObject formData) {


        if (formData.has("filters")) {
            JsonArray filters = formData.getAsJsonArray("filters");
            JsonArray requiredFilter = new JsonArray();
            Boolean removeQuery = false;
            String replacement = getDefaultsMap().get("adhoc.query.filter.customExpressionReplaceValue");
            for (int index = 0; index < filters.size(); index++) {
                JsonObject item = filters.get(index).getAsJsonObject();
                if (!GsonUtility.optBooleanValue(item,"ignore", false)) {
                    requiredFilter.add(item);
                }
            }
            // formData.put("customFilterExpression", customFilterExpression);

            if (requiredFilter.isEmpty()) {
                formData.remove("filters");
            } else {
                formData.add("filters", requiredFilter);
            }
            if (filters.size() != requiredFilter.size()) {
                formData.remove("customFilterExpression");
                formData.remove("query");
            }

        }
    }


}