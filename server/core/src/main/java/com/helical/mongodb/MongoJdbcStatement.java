package com.helical.mongodb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MongoJdbcStatement implements Statement {

    private final MongoJdbcConnection connection;
    private boolean closed = false;
    private ResultSet currentResultSet;
    private int updateCount = -1;
    private int maxRows = 0;
    private int maxFieldSize = 0;
    private int queryTimeout = 0;
    private int fetchSize = 0;
    private int fetchDirection = ResultSet.FETCH_FORWARD;
    private boolean poolable = false;
    private boolean closeOnCompletion = false;
    private final List<String> batch = new ArrayList<>();

    public MongoJdbcStatement(MongoJdbcConnection connection) {
        this.connection = connection;
    }

    private void checkClosed() throws SQLException {
        if (closed) {
            throw new SQLException("This statement has already been closed.");
        }
    }

    // @Override
    // public ResultSet executeQuery(String sql) throws SQLException {
    //     checkClosed();
    //     currentResultSet = MongoJdbcResultSet.singleValueResultSet(this, sql);
    //     updateCount = -1;
    //     return currentResultSet;
    // }

    @Override
public ResultSet executeQuery(String sql) throws SQLException {
    checkClosed();

    String normalized = sql.trim().replaceAll("\\s+", " ");
    String upper = normalized.toUpperCase();

    if (!upper.startsWith("SELECT")) {
        throw new SQLException("Only SELECT queries are supported.");
    }

    java.util.regex.Matcher matcher = java.util.regex.Pattern
            .compile("(?i)SELECT\\s+\\*\\s+FROM\\s+([a-zA-Z0-9_]+)")
            .matcher(normalized);

    if (!matcher.matches()) {
        throw new SQLException("Unsupported SQL: " + sql);
    }

    String collectionName = matcher.group(1);

    try {
        com.mongodb.DB db = connection.getMongoDatabase();
        com.mongodb.DBCollection collection = db.getCollection(collectionName);

        java.util.List<String> columns = new java.util.ArrayList<>();
        java.util.List<Object[]> rows = new java.util.ArrayList<>();

        com.mongodb.DBCursor cursor = collection.find();

        while (cursor.hasNext()) {
            com.mongodb.DBObject document = cursor.next();

            if (columns.isEmpty()) {
                for (String key : document.keySet()) {
                    if (!"_id".equals(key)) {
                        columns.add(key);
                    }
                }
            }

            Object[] row = new Object[columns.size()];

            for (int i = 0; i < columns.size(); i++) {
                row[i] = document.get(columns.get(i));
            }

            rows.add(row);
        }

        cursor.close();

        currentResultSet =
                MongoJdbcResultSet.create(this, columns, rows);

        updateCount = -1;
        return currentResultSet;

    } catch (Exception e) {
        throw new SQLException("MongoDB query failed: " + sql, e);
    }
}

    @Override
    public int executeUpdate(String sql) throws SQLException {
        checkClosed();
        currentResultSet = null;
        updateCount = 0;
        return updateCount;
    }

    @Override
    public void close() throws SQLException {
        closed = true;
        currentResultSet = null;
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return maxFieldSize;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        this.maxFieldSize = max;
    }

    @Override
    public int getMaxRows() throws SQLException {
        return maxRows;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        this.maxRows = max;
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        //No SQL escape processing is performed by this driver.
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return queryTimeout;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        this.queryTimeout = seconds;
    }

    @Override
    public void cancel() throws SQLException {
        //No long-running server-side statement to cancel.
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        //no-op
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        //Positioned updates are not supported.
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        checkClosed();
        currentResultSet = MongoJdbcResultSet.singleValueResultSet(this, sql);
        updateCount = -1;
        return true;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return currentResultSet;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return updateCount;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        currentResultSet = null;
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        this.fetchDirection = direction;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return fetchDirection;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        this.fetchSize = rows;
    }

    @Override
    public int getFetchSize() throws SQLException {
        return fetchSize;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        batch.add(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        batch.clear();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        int[] results = new int[batch.size()];
        Arrays.fill(results, 0);
        batch.clear();
        return results;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return getMoreResults();
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return MongoJdbcResultSet.empty(this, Arrays.asList("GENERATED_KEY"));
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return executeUpdate(sql);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return executeUpdate(sql);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return executeUpdate(sql);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return execute(sql);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return execute(sql);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return execute(sql);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        this.poolable = poolable;
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return poolable;
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        this.closeOnCompletion = true;
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return closeOnCompletion;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface != null && iface.isInstance(this)) {
            return iface.cast(this);
        }
        throw new SQLFeatureNotSupportedException("Not a wrapper for " + iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface != null && iface.isInstance(this);
    }
}
