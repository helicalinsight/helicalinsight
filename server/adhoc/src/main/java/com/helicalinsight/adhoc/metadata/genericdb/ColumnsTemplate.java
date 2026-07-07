package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Provides methods to retrieve column information from a JSON object.
 * 
 * Created by author on 27-06-2015.
 * @author Rajasekhar
 */
@Component
public class ColumnsTemplate {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ColumnsTemplate.class);
   
    /**
     * Retrieves a list of columns from the given JSON object.
     *
     * @param columnsJson 		 JSON object containing column information.
     * @param properties  		 properties to be used for column processing.
     * @return A list of Column objects.
     */
    @NotNull
    public List<Column> getColumnsList(@NotNull JsonObject columnsJson, Properties properties) {
        List<Column> columnList = new ArrayList<>();
        try {
            JsonArray columns = columnsJson.getAsJsonArray("columns");
            for (JsonElement object : columns) {
                JsonObject column = object.getAsJsonObject();
                addColumn(columnList, column, properties);
            }
        } catch (Exception ex) {
            JsonObject column;
            try {
                column = columnsJson.getAsJsonObject("columns");
            } catch (Exception e) {
                if (logger.isInfoEnabled()) {
                    logger.info("Couldn't find columns in a table. Returning empty columns list.");
                }
                return columnList;
            }
            addColumn(columnList, column, properties);
        }
        return columnList;
    }
    
    /**
     * Adds a column to the given list.
     *
     * @param columnList 		 list of columns to which the column will be added.
     * @param column     		 JSON object provides column name and type.
     * @param properties 		 properties to be used for column processing.
     */
    private void addColumn(@NotNull List<Column> columnList, @Nullable JsonObject column, Properties properties) {
        if (column == null || "null".equals(column.toString())) {
            return;
        }
        Column actualColumn = ApplicationContextAccessor.getBean(Column.class);
        String name = column.get("name").getAsString();
        actualColumn.setName(name);
        actualColumn.setAliasName(name);
        actualColumn.setId(AdhocUtils.getUuid());
        String type = column.get("type").getAsString();
        String property = properties.getProperty(type);
        if (property != null) {
            actualColumn.setDefaultFunction(property);
        }
        actualColumn.setType(type);
        columnList.add(actualColumn);
    }
}
