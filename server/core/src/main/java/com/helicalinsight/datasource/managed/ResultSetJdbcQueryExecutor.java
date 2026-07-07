package com.helicalinsight.datasource.managed;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.SQLTypeMap;
import org.apache.commons.dbutils.DbUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by author on 5/4/2020.
 *
 * @author Rajesh
 */
public class ResultSetJdbcQueryExecutor implements Callable<ResultSet> {
    private static final Logger logger = LoggerFactory.getLogger(ResultSetJdbcQueryExecutor.class);
    private final Statement statement;
    private final ApplicationProperties properties;
    private final String sql;

    public ResultSetJdbcQueryExecutor(Statement statement, String sql) {
        this.statement = statement;
        this.sql = sql;
        this.properties = ApplicationProperties.getInstance();

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
            object.addProperty("type", aClass.getName());

            columnNameAndType.add(Integer.toString(counter), object);
        }
        metaDataArray.add(columnNameAndType);
    }

    public ResultSet executeSql() throws SQLException {
        ResultSet resultSet = null;
        try {
            resultSet = this.statement.executeQuery(this.sql);
            RowSetFactory factory = RowSetProvider.newFactory();
            CachedRowSet cachedResultSet = factory.createCachedRowSet();
            cachedResultSet.populate(resultSet);
            return cachedResultSet;
        } catch (SQLException ex) {
        	System.out.println(ex.getCause());
            throw new EfwdServiceException("Couldn't query the database", ex);
        } finally {
            DbUtils.closeQuietly(resultSet);
        }

    }

    @Override
    public ResultSet call() throws Exception {
        return executeSql();
    }

}
