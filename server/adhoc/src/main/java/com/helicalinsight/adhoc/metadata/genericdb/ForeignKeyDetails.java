package com.helicalinsight.adhoc.metadata.genericdb;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;

/**
 * The ForeignKeyDetails class retrieves foreign key information for a specified table from the database metadata.
 * It provides a method to get foreign keys as a JSON object containing details such as key name, reference table,
 * and reference column.
 */
public class ForeignKeyDetails {

    private final static Logger logger = LoggerFactory.getLogger(ForeignKeyDetails.class);

    /**
     * Retrieves foreign key details for the specified table from the database metadata.
     *
     * @param databaseMetaData 		 DatabaseMetaData object to retrieve metadata from.
     * @param tableName        		 name of the table to retrieve foreign key details for.
     * @param schema           		 schema of the table.
     * @param catalog          		 catalog of the table.
     * @return 						 A JSON string containing foreign key details.
     * @throws SQLException          If an SQL exception occurs.
     */
    public static String getForeignKeys(@NotNull DatabaseMetaData databaseMetaData, String tableName, String schema,
                                        String catalog) throws SQLException {
        long startTime = System.currentTimeMillis();
        JsonObject foreignKeysJson = new JsonObject();

        try (ResultSet foreignKeys = databaseMetaData.getImportedKeys(catalog, schema, tableName)) {
            while (foreignKeys.next()) {
                Map<String, String> keysMap = new HashMap<>();
                keysMap.put("name", foreignKeys.getString("FKCOLUMN_NAME"));
                keysMap.put("referenceTable", foreignKeys.getString("PKTABLE_NAME"));
                keysMap.put("referenceColumn", foreignKeys.getString("PKCOLUMN_NAME"));
                GsonUtility.accumulate(foreignKeysJson, "foreignKeys", keysMap);
            }
        } catch (SQLFeatureNotSupportedException e) {
            // Log the specific feature not supported exception
            logger.warn("Foreign key retrieval is not supported for table '{}' in this database .", tableName);
              
        } catch (SQLException e) {
            logger.error("SQL Exception while retrieving foreign keys for table '{}': ", tableName);
             
        }

        long endTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("The table " + tableName + "'s foreign key information is retrieved in " + (endTime - startTime) + "" +
                    " milli seconds");
        }

        return foreignKeysJson.toString();
    }
}