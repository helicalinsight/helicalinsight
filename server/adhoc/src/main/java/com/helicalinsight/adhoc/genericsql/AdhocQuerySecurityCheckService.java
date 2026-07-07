package com.helicalinsight.adhoc.genericsql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;


/**
 * The AdhocQuerySecurityCheckService class is responsible for performing security checks
 * on custom columns in the ad-hoc query metadata.
 * Created by Helical on 2/17/2021.
 */
public class AdhocQuerySecurityCheckService {
    private Map<String, String> propsMap = ApplicationUtilities.getDefaultsMap();
    /**
     * Checks for custom columns in the formDataJson and metadataJson.
     * @param formDataJson 			 JsonObject containing form data.
     * @param metadataJson 			 Metadata object provides database, tables and columns info.
     * @return JsonObject after security checks.
     * @throws Exception If an error occurs during the security checks.
     */
    //TODO : Convert this to Gson
    public JsonObject check_for_custom_columns(JsonObject formDataJson, Metadata metadataJson) throws Exception {
        DocumentContext jsonDocumentContext = JsonPath.parse(formDataJson, Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS));
        for (Map.Entry<String, String> entryMap : propsMap.entrySet()) {
            if (entryMap.getKey().startsWith("$.")) {
                String key = entryMap.getKey();
                String value = entryMap.getValue();
                Object formDataObject = jsonDocumentContext.read(key);
                if (null != formDataObject) {
                    if (value.equalsIgnoreCase("strict")) {
                        if (formDataObject instanceof JsonArray) {
                            if (((JsonArray) formDataObject).size() > 0) {
                                throw new Exception("Cannot Execute when security is enabled");
                            } else if (formDataObject instanceof JsonObject) {
                                throw new Exception("Cannot Execute when security is enabled");
                            }
                        }
                    } else if (value.equalsIgnoreCase("stripped")) {
                        jsonDocumentContext.delete(key);
                    } else if (value.equalsIgnoreCase("strict-metadata")) {
                        if (formDataObject instanceof JsonArray) {
                            List<String> metadataColumnList = new ArrayList<String>();
                            JsonArray jsonArrayColumns = JsonParser.parseString(new Gson().toJson(formDataObject)).getAsJsonArray();
                            if (key.contains("$.filters")) {
                                validateFilters(jsonArrayColumns);
                            } else {
                                List<String> userColumnList = userRequestedColumns(jsonArrayColumns);
                                Database database = metadataJson.getDatabase();
                                List<Table> tablesList = database.getTables().getTableList();
                                if (tablesList.size() == 1) {
                                    Table table = tablesList.get(0);
                                    Columns columns = table.getColumns();
                                    List<Column> column = columns.getColumn();
                                    for (Column col : column) {
                                        metadataColumnList.add(col.getName());
                                    }
                                } else {
                                    for (int i = 0; i < tablesList.size(); i++) {
                                        Table table = tablesList.get(i);
                                        Columns columns = table.getColumns();
                                        List<Column> column = columns.getColumn();
                                        for (Column col : column) {
                                            metadataColumnList.add(col.getName());
                                        }
                                    }
                                }
                                for (String s : userColumnList) {
                                    boolean isContainsColumn = metadataColumnList.contains(s);
                                    if (!isContainsColumn) {
                                        throw new Exception("The column you are trying to access is not found in the metadata");
                                    }
                                }
                            }

                        }
                    }
                } else {
                    throw new Exception("key does not exists");
                }
            }
        }
        return jsonDocumentContext.json();
    }

    /**
     * Validates the filters in the JsonArray.
     * @param jsonArray 	 JsonArray containing filters.
     * @return Null 		 if validation is successful.
     * @throws Exception     If Statement is not null.
     */
    private String validateFilters(JsonArray jsonArray) throws Exception {
        for (int i = 0; i < jsonArray.size(); i++) {
            if (jsonArray.get(i) instanceof JsonPrimitive) {
                Statement stmt = null;
                try {
                    stmt = CCJSqlParserUtil.parse(jsonArray.get(i).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (stmt != null) {
                    throw new Exception("invalid filter value");
                }
            }
        }
        return null;
    }

    /**
     * Retrieves user-requested columns from the JsonArray.
     * @param columns 			 JsonArray containing column information.
     * @return The list of user-requested columns.
     */
    private List<String> userRequestedColumns(JsonArray columns) {
        List<String> userInput = new ArrayList<>();
        Statement stmt = null;
        for (JsonElement object : columns) {
            JsonObject json = object.getAsJsonObject();
            String columnName = json.get("column").getAsString();
            String fqdn = "";
            String[] columnNameArray = columnName.split("\\.");
            if (columnNameArray.length > 0) {
                columnName = StringUtils.strip(columnNameArray[columnNameArray.length - 1], "\"");
                userInput.add(columnName.trim());
            } else {
                userInput.add(columnName.trim());
            }
        }
        return userInput;
    }
}
