package com.helicalinsight.adhoc.security;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.AbstractSecurityRules;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

/**
 * Implements security rules to prevent SQL injection vulnerabilities in form data
 * and sanitize column names and expressions.
 * Extends AbstractSecurityRules for security enforcement.
 * 
 * Created by Helical on 3/9/2021.
 */
@Deprecated
public class Stripped extends AbstractSecurityRules {
    private static final Logger logger = LoggerFactory.getLogger(Stripped.class);
    /**
     * Checks for SQL injection vulnerabilities in form data and sanitizes column names and expressions.
     * 
     * @param formData 			 JSON object containing form data.
     * @param key 				 key to access the form data within the JSON object.
     * @param object 			 object representing the form data.
     * @return sanitized form data JSON object.
     * @throws Exception If a security violation is detected.
     */
	@Override
	public JsonObject checkSqlInjection(JsonObject formData, String key, Object object) throws Exception {
		DocumentContext jsonDocumentContext = JsonPath.parse(formData, Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS));
        Object jsonObject = null;
        Object havingObject = null;
        JsonObject updatedJSON = new Gson().fromJson(jsonDocumentContext.jsonString(),JsonObject.class);
        if(jsonDocumentContext.read(key)==null){
            throw new Exception("Invalid Property Passed");
        }
        if (key.startsWith("$.filters")) {
            Object filterObject=jsonDocumentContext.read(key);
            if(filterObject instanceof JsonArray){
                JsonArray jsonArrayColumns =  new Gson().fromJson(new Gson().toJson(filterObject),JsonArray.class);
                return validateFilterExpression(jsonArrayColumns,jsonDocumentContext,key);
            }else{
                JsonObject obj=jsonDocumentContext.json();
                JsonArray jsonArray=obj.getAsJsonArray("values");
                return validateFilterExpression(jsonArray,jsonDocumentContext,key);
            }
        } else {
            String columnName = "";
            if (key.contains("$.columns")) {
                jsonObject = jsonDocumentContext.read(key);
                if (jsonObject instanceof JsonArray) {
                    JsonArray jsonArrayColumns = new Gson().fromJson(new Gson().toJson(jsonObject),JsonArray.class);
                    if (jsonArrayColumns.size()!=0) {
                        for (int i = 0; i < jsonArrayColumns.size(); i++) {
                            columnName=jsonArrayColumns.get(i).getAsJsonObject().get("alias").getAsString();
                            columnName=StringUtils.strip(columnName, "\"");
                            jsonDocumentContext.delete(key);
                            updatedJSON = jsonDocumentContext.json();
                        }
                    }else{
                        updatedJSON = jsonDocumentContext.json();
                    }
                }
            } else {
                jsonDocumentContext.delete(key);
                updatedJSON = jsonDocumentContext.json();
            }
            if (updatedJSON.has("functions")) {
                JsonObject functionObject = updatedJSON.getAsJsonObject("functions");
                if (functionObject.has("aggregate")) {
                    updatedJSON = jsonDocumentContext.delete("$.functions.aggregate[?(@['custom'])]").json();
                    int size = 0;
                    if (null != updatedJSON) {
                        size = updatedJSON.getAsJsonObject("functions").getAsJsonArray("aggregate").size();
                        if (size == 0) {
                            updatedJSON.getAsJsonObject("functions").remove("aggregate");
                        }
                    }
                }
                if (functionObject.has("groupBy")) {
                    JsonArray groupByArray = functionObject.getAsJsonArray("groupBy");
                    if (groupByArray.size() > 0) {
                        for (int i = 0; i < groupByArray.size(); i++) {
                            String groupByColumnName = groupByArray.get(i).getAsJsonObject().get("column").getAsString();
                            if (!StringUtils.isEmpty(columnName)) {
                                if (columnName.equals(groupByColumnName)) {
                                    updatedJSON.getAsJsonObject("functions").getAsJsonArray("groupBy").remove(i);
                                    int size = updatedJSON.getAsJsonObject("functions").getAsJsonArray("groupBy").size();
                                    if (size == 0) {
                                        updatedJSON.getAsJsonObject("functions").remove("groupBy");
                                    }
                                }
                            }
                        }
                    }
                }
                if (functionObject.has("orderBy")) {
                    JsonArray groupByArray = functionObject.getAsJsonArray("orderBy");
                    if (groupByArray.size() > 0) {
                        for (int i = 0; i < groupByArray.size(); i++) {
                            String orderByColumnName = groupByArray.get(i).getAsJsonObject().get("alias").getAsString();
                            if (!StringUtils.isEmpty(columnName)) {
                                if (columnName.equals(orderByColumnName)) {
                                    updatedJSON.getAsJsonObject("functions").getAsJsonArray("orderBy").remove(i);
                                    int size = updatedJSON.getAsJsonObject("functions").getAsJsonArray("orderBy").size();
                                    if (size == 0) {
                                        updatedJSON.getAsJsonObject("functions").remove("orderBy");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return updatedJSON;

	}
	/**
     * Checks whether it's safe to cache the security rules.
     * @return {@code false} since the security rules may change dynamically.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return false;
    }
    
    /**
     * Validates filter expressions to prevent SQL injection vulnerabilities.
     * 
     * @param filterValueArray 			 array containing filter values.
     * @param jsonDocumentContext 		 document context for JSON manipulation.
     * @param key 						 key to access the form data within the JSON object.
     * @return The sanitized form data JSON object.
     */
	private JsonObject validateFilterExpression(JsonArray filterValueArray,DocumentContext jsonDocumentContext,String key){
        JsonObject jsonObject=jsonDocumentContext.json();
        String customExpression="";
        String havingExpression="";
        for(int i=0;i<filterValueArray.size();i++) {
            Object value = filterValueArray.get(i);
            if (!(value instanceof JsonObject)) {
                Statement stmt = null;
                try {
                    String filterValue = filterValueArray.get(i).getAsString();
                    stmt = CCJSqlParserUtil.parse(filterValueArray.get(i).getAsString());
                } catch (Exception e) {
                    logger.error("sql not found");
                }
                if (stmt != null) {
                    jsonObject.getAsJsonArray("filters").remove(i);
                }
            }
        }
                    if(jsonObject.has("filters")){
                        JsonArray finalFilterArray=jsonObject.getAsJsonArray("filters");
                        if(finalFilterArray.size()==0){
                            jsonObject.remove("filters");
                            jsonObject.remove("customFilterExpression");
                            jsonObject.remove("customHavingExpression");
                        }else{
                            if(filterValueArray.size()!=finalFilterArray.size()){
                                for(int j=0;j<finalFilterArray.size();j++){
                                    if(jsonObject.has("customFilterExpression")){
                                        customExpression+="${"+j+"} AND ";
                                    }

                                    if(jsonObject.has("customHavingExpression")){
                                        havingExpression+="${"+j+"} AND ";
                                    }
                                }
                                if(jsonObject.has("customFilterExpression")){
                                    jsonObject.addProperty("customFilterExpression", customExpression.substring(0, customExpression.lastIndexOf("AND")));
                                }

                                if(jsonObject.has("customHavingExpression")){
                                    jsonObject.addProperty("customHavingExpression", havingExpression.substring(0, havingExpression.lastIndexOf("AND")));
                                }
                            }
                        }
                    }

            return jsonObject;
        }
	/**
     * Checks for custom expressions in the JSON object and updates them.
     * 
     * @param json 				 JSON string.
     * @param clauseName 		 name of the clause to check for custom expressions.
     * @return The updated JSON object.
     */
    private JsonObject check_for_custom_expression(String json,String clauseName){
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        if(!jsonObject.has(clauseName)){
            if(clauseName.equalsIgnoreCase("filters")){
                jsonObject.remove("customFilterExpression");
            }else if(clauseName.equalsIgnoreCase("having")){
                jsonObject.remove("customHavingExpression");
            }
        }else{
            JsonElement filterObject = jsonObject.get(clauseName);
            String customExpression="";
            if(filterObject!=null){
                if(filterObject.isJsonArray()){
                    JsonArray jsonArrayFilter = filterObject.getAsJsonArray();
                    if(jsonArrayFilter.size()==0){
                        jsonObject.remove(clauseName);
                        if(clauseName.equalsIgnoreCase("filters")){
                            jsonObject.remove("customFilterExpression");
                        }else if(clauseName.equalsIgnoreCase("having")){
                            jsonObject.remove("customHavingExpression");
                        }
                    }
                    for(int j=0;j<jsonArrayFilter.size();j++){
                        customExpression+="${"+j+"} AND ";
                    }
                    if(!StringUtils.isEmpty(customExpression)){
                        if(clauseName.equalsIgnoreCase("filters")){
                            jsonObject.addProperty("customFilterExpression", customExpression.substring(0, customExpression.lastIndexOf("AND")));
                        }else if(clauseName.equalsIgnoreCase("having")){
                            jsonObject.addProperty("customHavingExpression", customExpression.substring(0, customExpression.lastIndexOf("AND")));
                        }
                    }
                }
            }
        }
        return jsonObject;
    }

    /**
     * Extracts user-requested columns from a JSON array of columns.
     * 
     * @param columns 		 JSON array containing column definitions.
     * @return list of user-requested column names.
     */
    private List<String> userRequestedColumns(JsonArray columns) {
        List<String> userInput = new ArrayList<>();
        Statement stmt = null;
        for (JsonElement object : columns) {
            JsonObject json = object.getAsJsonObject();
            String columnName = json.get("column").getAsString();
            String fqdn = "";
            String[] columnNameArray = columnName.split("\\.");
            if(columnNameArray.length>0){
                columnName= StringUtils.strip(columnNameArray[columnNameArray.length - 1], "\"");
                userInput.add(columnName.trim());
            }else{
                userInput.add(columnName.trim());
            }
        }
        return userInput;
    }
}
