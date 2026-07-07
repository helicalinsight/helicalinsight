package com.helicalinsight.adhoc.metadata.genericdb;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;

/**
 * Utility class for retrieving primary key details from a database table.
 *
 * Created by author on 27-02-2015.
 * @author Rajasekhar
 */
public class PrimaryKeyDetails {

    private final static Logger logger = LoggerFactory.getLogger(PrimaryKeyDetails.class);
    /**
     * Retrieves the primary key details for the specified table from the database metadata.
     *
     * @param databaseMetaData 			 metadata of the database connection
     * @param table            			 name of the table
     * @param schema           			 schema name of the table
     * @param catalog          			 catalog name of the table
     * @return A JSON representation of the primary keys for the table
     * @throws SQLException If an SQL exception occurs while retrieving the primary keys
     */
    public static String getPrimaryKeys(@NotNull DatabaseMetaData databaseMetaData, String table, String schema,
                                        String catalog) throws SQLException {
        long now = System.currentTimeMillis();
        long later;
        ResultSet primaryKeys = null;
        JsonObject primaryKeysJson;
        primaryKeysJson = new JsonObject();
        try {
            primaryKeys = databaseMetaData.getPrimaryKeys(catalog, schema, table);
            Map<String, String> keyMap;
            if(primaryKeys==null){
                return primaryKeysJson.toString();
            }
            while (primaryKeys.next()) {
                keyMap = new HashMap<>();
                keyMap.put("name", primaryKeys.getString(4));
                GsonUtility.accumulate(primaryKeysJson,"primaryKeys", keyMap);
            }
            later = System.currentTimeMillis();
        } finally {
            DbUtils.closeQuietly(primaryKeys);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("The table " + table + "'s primary key information is retrieved in " +
                    (later - now) + " milli seconds");
        }
        return primaryKeysJson.toString();
    }
}
