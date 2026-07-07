package com.helicalinsight.datasource.managed;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.SQLTypeMap;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by author on 06-11-2017.
 *
 * @author Somen
 */
public class SparkJdbcExecutor extends JdbcQueryExecutor {


    public SparkJdbcExecutor(Statement statement, String sql) {
        super(statement, sql);
    }


    @Override
    protected void addMetadata(@NotNull ResultSetMetaData resultSetMetaData, @NotNull JsonArray metaDataArray,
                               int columnCount) throws SQLException {

        JsonObject columnNameAndType = new JsonObject();

        for (int counter = 1; counter <= columnCount; counter++) {
            JsonObject object = new JsonObject();
            object.addProperty("name", resultSetMetaData.getColumnLabel(counter));

            final int columnType = resultSetMetaData.getColumnType(counter);
            final Class<?> aClass = SQLTypeMap.toClass(columnType);
            object.addProperty("type", ApplicationUtilities.removeClass(aClass));

            columnNameAndType.add(Integer.toString(counter), object);
        }
        metaDataArray.add(columnNameAndType);
    }


}