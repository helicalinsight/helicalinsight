package com.helicalinsight.datasource.managed;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Map;
import java.util.concurrent.Callable;

import com.helicalinsight.efw.utility.SecurityExpressionEvaluator;
import org.apache.commons.dbutils.DbUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.SQLTypeMap;

/**
 * Created by author on 14-08-2015.
 *
 * @author Rajasekhar
 */
public class JdbcQueryExecutor implements Callable<JsonObject> {
	 private static final Logger logger = LoggerFactory.getLogger(JdbcQueryExecutor.class);
    private final Statement statement;
    private final ApplicationProperties properties;
    private final String sql;

    public JdbcQueryExecutor(Statement statement, String sql) {
        this.statement = statement;
        this.sql = sql;
        this.properties = ApplicationProperties.getInstance();

    }

    @Override
    public JsonObject call() throws Exception {
        return executeSql();
    }

    public JsonObject executeSql() throws SQLException {
        ResultSet resultSet = null;
        try {
            Long now= System.currentTimeMillis();
            resultSet = this.statement.executeQuery(this.sql);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int rowCount = 0; //To count the number of rows

            JsonObject queryResult = new JsonObject();

            JsonArray metaDataArray = new JsonArray();
            int columnCount = resultSetMetaData.getColumnCount();

            //Adding metadata of the result. This is a fix for SQLite. Earlier the method was
            // called late.
            addMetadata(resultSetMetaData, metaDataArray, columnCount);

            JsonArray dataArray = new JsonArray();
            while (resultSet.next()) {
                JsonObject row = new JsonObject();
                ++rowCount;
                addARow(resultSet, resultSetMetaData, columnCount, dataArray, row);
            }
            queryResult.add("data", dataArray);

            JsonObject rowsJson = new JsonObject();
            rowsJson.addProperty("rows", rowCount);
            //logger.error("The table obtained is "+dataArray);
            logger.debug("Time taken "+(now-System.currentTimeMillis()));
            metaDataArray.add(rowsJson);
            queryResult.add("metadata", metaDataArray);
            return queryResult;
        } catch (SQLException ex) {
            throw new EfwdServiceException("Couldn't query the database", ex);
        } finally {
            DbUtils.closeQuietly(resultSet);
        }
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
            object.addProperty("type", dataTypesMapping.get(ApplicationUtilities.removeClass(aClass)));

            columnNameAndType.add(Integer.toString(counter), object);
        }
        metaDataArray.add(columnNameAndType);
    }

    private void addARow(@NotNull ResultSet resultSet, @NotNull ResultSetMetaData resultSetMetaData, int columnCount,
                         @NotNull JsonArray dataArray, @NotNull JsonObject row) throws SQLException {
        String nullValue = this.properties.getNullValue();
        for (int index = 1; index <= columnCount; index++) {
            int columnType = resultSetMetaData.getColumnType(index);
            columnType = columnType == -101 ? 2014 :columnType;
            Object object = resultSet.getObject(index);
            String columnLabel = resultSetMetaData.getColumnLabel(index);
            if ((columnType == Types.DATE) || (columnType == Types.TIMESTAMP) || (columnType == Types.TIME) ||(columnType == Types.TIMESTAMP_WITH_TIMEZONE)) {
                if (object == null) {
                    row.addProperty(columnLabel, nullValue);
                } else {
                	String result = SQLTypeMap.getSpecificFromResultset(object.toString(), resultSet, index);
                	if(result != null){
                		 row.addProperty(columnLabel, result);
                	}else{
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
}