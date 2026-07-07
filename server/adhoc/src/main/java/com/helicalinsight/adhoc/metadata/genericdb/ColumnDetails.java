package com.helicalinsight.adhoc.metadata.genericdb;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.SQLTypeMap;

/**
 * Provides methods to retrieve column information for a table from DatabaseMetaData.
 *
 * Created by author on 27-02-2015.
 * @author Rajasekhar
 */
public class ColumnDetails {

    private static final Logger logger = LoggerFactory.getLogger(ColumnDetails.class);

    /**
     * There is an inconsistent in the response. If there is a single column the response will
     * be an object. Instead if there are multiple the response will be an array. The Json is
     * converted to string and sent.
     *
     * @param databaseMetaData DatabaseMetadata Object provides resultSet
     * @param table            A table
     * @param catalog          Catalog of the table
     * @param schema           Schema of the table
     * @return Json Array or Json Object in string form
     */
    public static String getColumnInfoForTable(@NotNull DatabaseMetaData databaseMetaData, String table,
                                               String catalog, @Nullable String schema) {
        Map<String, String> dataTypesMapping = AdhocUtils.getDataTypeMapping();

        JsonObject columns;
        columns = new JsonObject();
        try {
            ResultSet resultSetOfColumns;
            long now = System.currentTimeMillis();
            long later;

            if (schema == null) {
                schema = "%";
            }

            table = escape(table);

            resultSetOfColumns = databaseMetaData.getColumns(catalog, schema, table, "%");
            Map<String, String> columnInfo;

            while (resultSetOfColumns.next()) {
                int dataType = resultSetOfColumns.getInt("DATA_TYPE");
                String columnName = resultSetOfColumns.getString("COLUMN_NAME");
                int size = resultSetOfColumns.getInt("COLUMN_SIZE");
                int nullable = resultSetOfColumns.getInt("NULLABLE");
                int position = resultSetOfColumns.getInt("ORDINAL_POSITION");
                columnInfo = new HashMap<>();

                columnInfo.put("name", columnName);
                String type = ApplicationUtilities.removeClass(SQLTypeMap.toClass(dataType));
                columnInfo.put("type", type);
                String jsType = AdhocUtils.getType(type, dataTypesMapping);

                columnInfo.put("dataType", jsType);
                columnInfo.put("size", String.valueOf(size));
                columnInfo.put("nullable", (DatabaseMetaData.columnNullable == nullable) ? "TRUE" : "FALSE");
                columnInfo.put("position", String.valueOf(position));
                Gson gson = new GsonBuilder().serializeNulls().create();
                JsonObject column = gson.toJsonTree(columnInfo).getAsJsonObject();
                GsonUtility.accumulate(columns,"columns", column);
            }

            later = System.currentTimeMillis();

            if (logger.isDebugEnabled()) {
                logger.debug("The time taken to retrieve the column metadata for the table " +
                        table + " is " + (later - now));
            }
        } catch (SQLException ex) {
            throw new MetadataRetrievalException(ex);
        }
        return columns.toString();
    }
    /**
     * Escapes the given string to handle special characters.
     *
     * @param string 		 string to be escaped.
     * @return escaped string.
     */
    public static String escape(String string) {
        if (string.contains("/")) {
            string = string.replaceAll("/", "//");
        }

        if (string.contains("\\")) {
            string = string.replaceAll("\\\\", "\\\\\\\\");
        }
        return string;
    }
}
