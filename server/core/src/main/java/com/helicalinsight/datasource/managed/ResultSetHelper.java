package com.helicalinsight.datasource.managed;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.utils.DataTypeUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.SQLTypeMap;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by author on 5/4/2020.
 *
 * @author Rajesh
 * class changed to gson
 */
public class ResultSetHelper {

    private static final Logger logger = LoggerFactory.getLogger(ResultSetHelper.class);

    private final ApplicationProperties properties = ApplicationProperties.getInstance();
    /**
     * using gson
     * getHiddenList(JsonObject formData)
     * @param formData
     * @return
     */
    public static ArrayList<String> getHiddenList(JsonObject formData) {
        if (formData.entrySet().isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<String> hiddenArrayList = new ArrayList<>();
        if(formData.has("columns")) {
            JsonArray columns = formData.getAsJsonArray("columns");
            for (int index = 0; index < columns.size(); index++) {
                JsonObject jsonObject = columns.get(index).getAsJsonObject();
                boolean hidden = GsonUtility.optBoolean(jsonObject, "hidden");
                if (hidden) {
                    hiddenArrayList.add(jsonObject.get("alias").getAsString());
                }
            }
        }
        return hiddenArrayList;
    }
    protected void addMetadata(@NotNull ResultSetMetaData resultSetMetaData, @NotNull JsonArray metaDataArray,
                               int columnCount, List<String> hiddenList) throws SQLException {
        Map<String, String> dataTypesMapping = new PropertiesFileReader().read("Admin", "dataTypesMapping.properties");

        JsonObject columnNameAndType = new JsonObject();

        for (int counter = 1; counter <= columnCount; counter++) {
            JsonObject object = new JsonObject();
            String columnLabel = resultSetMetaData.getColumnLabel(counter);
            if (hiddenList.contains(columnLabel)) {
                continue;
            }
            object.addProperty("name", columnLabel);

            final int columnType = resultSetMetaData.getColumnType(counter);
            final Class<?> aClass = SQLTypeMap.toClass(columnType);
            object.addProperty("type", dataTypesMapping.get(ApplicationUtilities.removeClass(aClass)));

            if ( aClass.equals(Object.class)) {
            	String newType = DataTypeUtils.updateColumnDataType(resultSetMetaData,counter,dataTypesMapping);
            	if(StringUtils.isNotBlank(newType)) {
            		object.addProperty("type", newType);
            	}
            }
            columnNameAndType.add(Integer.toString(counter), object);
        }
        metaDataArray.add(columnNameAndType);
    }

    protected void addARow(@NotNull ResultSet resultSet, @NotNull ResultSetMetaData resultSetMetaData, int columnCount,
                           @NotNull JsonArray dataArray, @NotNull JsonObject row, ArrayList<String> hiddenListArray) throws SQLException {
        String nullValue = this.properties.getNullValue();
        for (int index = 1; index <= columnCount; index++) {
            int columnType = resultSetMetaData.getColumnType(index);
            columnType = columnType == -101 ? 2014 : columnType;
            Object object = resultSet.getObject(index);
            String columnLabel = resultSetMetaData.getColumnLabel(index);
            if (hiddenListArray.contains(columnLabel)) {
                continue;
            }
            if ((columnType == Types.DATE) || (columnType == Types.TIMESTAMP) || (columnType == Types.TIME) || (columnType == Types.TIMESTAMP_WITH_TIMEZONE)) {
                if (object == null) {
                    row.addProperty(columnLabel, nullValue);
                } else {
                    String result = SQLTypeMap.getSpecificFromResultset(object.toString(), resultSet, index);
                    if (result != null) {
                        row.addProperty(columnLabel, result);
                    } else {
                        row.addProperty(columnLabel, object.toString());
                    }
                }
            } else {
                if (object instanceof Number) {
                    row.addProperty(columnLabel, (Number) (object));
                } else if (object instanceof Character) {
                    row.addProperty(columnLabel, (Character) (object));
                } else if (object instanceof Boolean) {
                    //UI Needs as string
                    row.addProperty(columnLabel, "" + object);
                } else {

                    row.addProperty(columnLabel, (object == null ? nullValue : object.toString()));
                }
            }
        }
        dataArray.add(row);
    }

    public JsonObject resultSetToJsonConverter(ResultSet resultSet, JsonObject formData) {
        JsonObject queryResult = null;
        try {
//            logger.info("size", ((CachedRowSet) resultSet).size());
            Long now = System.currentTimeMillis();
            ResultSetMetaData resultSetMetaData = null;
            resultSetMetaData = resultSet.getMetaData();

            int rowCount = 0; //To count the number of rows

            queryResult = new JsonObject();

            JsonArray metaDataArray = new JsonArray();
            int columnCount = resultSetMetaData.getColumnCount();

            //Adding metadata of the result. This is a fix for SQLite. Earlier the method was
            // called late.
            ArrayList<String> hiddenArrayList = getHiddenList(formData);
            addMetadata(resultSetMetaData, metaDataArray, columnCount, hiddenArrayList);

            JsonArray dataArray = new JsonArray();
            while (resultSet.next()) {
                JsonObject row = new JsonObject();
                ++rowCount;
                addARow(resultSet, resultSetMetaData, columnCount, dataArray, row, hiddenArrayList);
            }

            queryResult.add("data", dataArray);

            JsonObject rowsJson = new JsonObject();
            rowsJson.addProperty("rows", rowCount);
            //logger.error("The table obtained is "+dataArray);
            logger.debug("Time taken " + (now - System.currentTimeMillis()));
            metaDataArray.add(rowsJson);

            queryResult.add("metadata", metaDataArray);
            return queryResult;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queryResult;
    }
}
