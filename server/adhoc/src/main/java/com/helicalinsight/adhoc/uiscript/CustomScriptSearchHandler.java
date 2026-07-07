package com.helicalinsight.adhoc.uiscript;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.serviceframework.IComponent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.Collection;
import java.util.List;

/**
 * CustomScriptSearchHandler
 *
 * This class implements the functionality to search for custom scripts based on various parameters
 * such as group, script type, sub-group, and search phrase.
 *
 * Author: Somen
 * Created on: 23-09-2016
 */
@SuppressWarnings("unused")
public class CustomScriptSearchHandler implements IComponent {

	/**
     * Executes the search operation for custom scripts based on the provided JSON form data.
     *
     * @param jsonFormData    provides parameters such as group, scriptType, limit, offset,subGroup etc.
     * @return The JSON response containing the search results
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formDataJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        List<CustomScript> allCustomScripts = CustomScriptIndexer.getAllCustomScripts();
        List<CustomScript> requiredScripts = null;


        JsonArray groupJson = GsonUtility.optJsonArray(formDataJson,"group");

        String requestScript = GsonUtility.optString(formDataJson,"scriptType");
        final String scriptType = requestScript == null || requestScript.isEmpty() ? null : requestScript;
        String subGroupValue = formDataJson.get("subGroup").getAsString();
        final String subGroup = subGroupValue.isEmpty() ? null : subGroupValue;
        final String searchPhrase = formDataJson.get("searchPhrase").getAsString().toLowerCase();
        Integer offset = formDataJson.get("offset").getAsInt();
        Integer limit = formDataJson.get("limit").getAsInt();
        if (groupJson == null || groupJson.isEmpty()) {
            groupJson = new JsonArray();
            groupJson.add(formDataJson.get("group").getAsString());
        }

        groupJson.add("generic");

        final String firstGroup = groupJson.get(0).getAsString();
        Collection smallList = performSearch(allCustomScripts, firstGroup, scriptType, subGroup, searchPhrase);
        for (int index = 1; index < groupJson.size(); index++) {
            final String group = groupJson.get(index).getAsString();
            Collection collection = performSearch(allCustomScripts, group, scriptType, subGroup, searchPhrase);
            if (collection != null) {
                smallList.addAll(collection);
            }
        }
        JsonArray resultArray = new JsonArray();
        int size = 0;
        if (smallList != null && !smallList.isEmpty()) {
            size = prepareResponse(offset, limit, smallList, resultArray);
        }

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("total", size);
        responseJson.add("result", resultArray);
        return responseJson.toString();
    }

    private int prepareResponse(Integer offset, Integer limit, Collection smallList, JsonArray resultArray) {
        List<CustomScript> requiredScripts;
        int size = 0;
        if (smallList != null) {
            requiredScripts = (List<CustomScript>) smallList;
            size = requiredScripts.size();
            if (offset >= size) {
                throw new FormValidationException("The offset should  be less than the total records");
            }
            int endIndex = offset + limit;
            List<CustomScript> responseArray = requiredScripts.subList(offset, endIndex > size ? size : endIndex);
            int index = 0;
            for (CustomScript script : responseArray) {
                JsonObject response = new JsonObject();
                response.addProperty("name", script.getName());
                response.addProperty("scriptId", script.getScriptId());
                response.addProperty("scriptType", script.getScriptType());
                response.addProperty("group", script.getGroup());
                //response.put("snippet", script.getSnippet());

                CustomScriptUtils.addFunctionNameAndParameters(script, response);
                response.addProperty("isEditable", script.getUiSnippet() != null);
                response.addProperty("description", script.getDescription());
                response.addProperty("subGroup", script.getSubGroup());
                response.addProperty("icon", script.getIcon());
                resultArray.add(response);
            }
        }
        return size;
    }


    private Collection performSearch(List<CustomScript> requiredScripts, final String group, final String scriptType,
                                     final String subGroup, final String searchPhrase) {
        return CollectionUtils.select(requiredScripts, new Predicate() {
            public boolean evaluate(Object object) {

                CustomScript customScript = (CustomScript) object;

                return ("all".equalsIgnoreCase(group) || customScript.getGroup().equalsIgnoreCase(group)) && ((subGroup == null) || ("all".equalsIgnoreCase(subGroup))
                        || ((customScript.getSubGroup() != null) && customScript.getSubGroup().equalsIgnoreCase
                        (subGroup))) && (searchPhrase.isEmpty() || (customScript.getDescription() != null &&
                        customScript.getDescription().toLowerCase().contains(searchPhrase)) || customScript.getName()
                        .toLowerCase().contains(searchPhrase));
            }
        });
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }


}
