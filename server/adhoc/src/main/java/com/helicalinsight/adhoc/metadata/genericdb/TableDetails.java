package com.helicalinsight.adhoc.metadata.genericdb;

import com.helicalinsight.efw.utility.ApplicationUtilities;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Utility class for retrieving table details from a database metadata.
 * 
 * Created by author on 27-02-2015.
 * @author Rajasekhar
 */
public class TableDetails {

    private static final Logger logger = LoggerFactory.getLogger(TableDetails.class);
    /**
     * Retrieves a list of tables from the specified database metadata.
     *
     * @param databaseMetaData 		 metadata object for the database
     * @param catalogName      		 catalog name
     * @param schemaName       		 schema name
     * @return A list of table names
     * @throws MetadataRetrievalException If an error occurs while retrieving table information
     */
    public static List<String> getListOfTables(@NotNull DatabaseMetaData databaseMetaData, String catalogName,
                                               String schemaName) {
        Map<String, String> defaultsMap = ApplicationUtilities.getDefaultsMap();
        String typesString = defaultsMap.get("adhoc.metadata.fetch");
        String[] types;
        if (typesString != null) {
            types = typesString.split(",");
        } else {
            types = new String[]{"TABLE"};
        }

        long now = System.currentTimeMillis();
        long later;
        Set<String> tableNameList;
        List<String> tableNameListAll;
        ResultSet result = null;
        try {
            result = databaseMetaData.getTables(catalogName, schemaName, "%", types);
            tableNameList = new HashSet<>();
            tableNameListAll = new ArrayList<>();
            String url = databaseMetaData.getURL();
            String query = DatabaseDetails.getQuery(url);
            List<String> databaseList = DatabaseDetails.getComaToList(DatabaseDetails.getList(query, DatabaseDetails.HI_TABLE));
            String getContainsTable = DatabaseDetails.getList(query, DatabaseDetails.HI_TABLE_CONTAINS);

            while (result.next()) {
                String tableName = result.getString(3);
                if (databaseList.contains(tableName)) {
                    tableNameList.add(tableName);
                }
                if (StringUtils.isNoneEmpty(getContainsTable) && tableName.contains(getContainsTable)) {
                    tableNameList.add(tableName);
                }
                tableNameListAll.add(tableName);

            }
            if (tableNameList.isEmpty()) {
                tableNameList.addAll(tableNameListAll);
            }
            later = System.currentTimeMillis();

            if (logger.isDebugEnabled()) {
                logger.debug("Fetched the tables information from the database. Time taken is " + (later - now));
            }
        } catch (SQLException e) {
            throw new MetadataRetrievalException("Could not get database tables information", e);
        } finally {
            DbUtils.closeQuietly(result);
        }
        List<String> mainList = new ArrayList<String>();
        mainList.addAll(tableNameList);
        return mainList;
    }
}
