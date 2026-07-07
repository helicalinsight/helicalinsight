package com.helicalinsight.datasource.managed;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.SQLTypeMap;
import org.jetbrains.annotations.NotNull;

import java.sql.Blob;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;
import java.sql.Types;
import java.util.Map;

/**
 * Created by author on 4/8/2020.
 *
 * @author Rajesh
 */
public class HCRJdbcQueryExecutor extends JdbcQueryExecutor {
    public HCRJdbcQueryExecutor(Statement statement, String sql) {
        super(statement, sql);
    }

    protected void addMetadata(@NotNull ResultSetMetaData resultSetMetaData, @NotNull JsonArray metaDataArray,
                               int columnCount) throws SQLException {
        Map<String, String> dataTypesMapping = new PropertiesFileReader().read("Admin", "dataTypesMapping.properties");

        JsonObject columnNameAndType = new JsonObject();

        for (int counter = 1; counter <= columnCount; counter++) {
            JsonObject object = new JsonObject();
            object.addProperty("name", resultSetMetaData.getColumnLabel(counter));

            final int columnType = resultSetMetaData.getColumnType(counter);
            final Class<?> aClass = SQLTypeMap.toClass(columnType);
            String type = aClass.getName();
            
            if ( type.equalsIgnoreCase("java.sql.Blob")) {
            	type = "byte[]";
            }
            
            object.addProperty("type", type);
            
            columnNameAndType.add(Integer.toString(counter), object);
        }
        metaDataArray.add(columnNameAndType);
    }
}
