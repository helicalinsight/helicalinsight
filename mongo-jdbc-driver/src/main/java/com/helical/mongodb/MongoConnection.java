package com.helical.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.lang.reflect.Proxy;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.ShardingKey;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class MongoConnection implements Connection {

    private final MongoClient mongoClient;
    private final String databaseName;
    private final String jdbcUrl;

    private boolean closed = false;

    public MongoConnection(
            MongoClient mongoClient,
            String databaseName,
            String jdbcUrl) {

        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
        this.jdbcUrl = jdbcUrl;
    }

    String getDatabaseName() {
        return databaseName;
    }

    MongoClient getMongoClient() {
        return mongoClient;
    }

    MongoDatabase getDatabase() {
        return mongoClient.getDatabase(databaseName);
    }

    @Override
    public Statement createStatement() throws SQLException {
        checkClosed();
        return new MongoStatement(this);
    }

    @Override
    public Statement createStatement(
            int resultSetType,
            int resultSetConcurrency) throws SQLException {

        checkClosed();
        return new MongoStatement(this);
    }

    @Override
    public Statement createStatement(
            int resultSetType,
            int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {

        checkClosed();
        return new MongoStatement(this);
    }

    @Override
    public void close() throws SQLException {
        if (!closed) {
            mongoClient.close();
            closed = true;
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        if (closed) {
            return false;
        }

        try {
            getDatabase().runCommand(
                    new org.bson.Document("ping", 1)
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
public DatabaseMetaData getMetaData() throws SQLException {
    checkClosed();

    return (DatabaseMetaData) Proxy.newProxyInstance(
            DatabaseMetaData.class.getClassLoader(),
            new Class<?>[]{DatabaseMetaData.class},
            (proxy, method, args) -> {

                String methodName = method.getName();

                switch (methodName) {
                    case "getDatabaseProductName":
                        return "MongoDB";

                    case "getDatabaseProductVersion":
                        return "7.0";

                    case "getDatabaseMajorVersion":
                        return 7;

                    case "getDatabaseMinorVersion":
                        return 0;

                    case "getDriverName":
                        return "Helical MongoDB JDBC Driver";

                    case "getDriverVersion":
                        return "1.0.0";

                    case "getDriverMajorVersion":
                        return 1;

                    case "getDriverMinorVersion":
                        return 0;

                    case "getURL":
                        return jdbcUrl;

                    case "getUserName":
                        return "";

                    case "getConnection":
                        return this;

                    case "getCatalogTerm":
                        return "database";

                    case "getSchemaTerm":
                        return "schema";

                    case "getTableTerm":
                        return "collection";

                    case "getCatalogSeparator":
                        return ".";

                    case "getCatalogs":
                        return MongoMetadataSupport.getCatalogs(
                                mongoClient,
                                databaseName);

                    case "getSchemas":
                        return MongoMetadataSupport.getSchemas();

                    case "getTables":
                        return MongoMetadataSupport.getTables(
                                getDatabase(),
                                (String) args[0],
                                (String) args[1],
                                (String) args[2],
                                (String[]) args[3]);

                    case "getColumns":
                        return MongoMetadataSupport.getColumns(
                                getDatabase(),
                                (String) args[0],
                                (String) args[1],
                                (String) args[2],
                                (String) args[3]);

                    case "getPrimaryKeys":
                        return MongoMetadataSupport.getPrimaryKeys(
                                getDatabase(),
                                (String) args[0],
                                (String) args[1],
                                (String) args[2]);

                    case "getImportedKeys":
                    case "getExportedKeys":
                        return MongoMetadataSupport.getEmptyImportedKeys();

                    case "supportsTransactions":
                        return false;

                    case "isWrapperFor":
                        return false;

                    case "unwrap":
                        throw new SQLException(
                                "MongoDB JDBC metadata does not support unwrap");

                    case "toString":
                        return "MongoDB DatabaseMetaData";

                    default:
                        Class<?> returnType = method.getReturnType();

                        if (returnType == boolean.class) {
                            return false;
                        }

                        if (returnType == int.class) {
                            return 0;
                        }

                        if (returnType == long.class) {
                            return 0L;
                        }

                        if (returnType == float.class) {
                            return 0.0f;
                        }

                        if (returnType == double.class) {
                            return 0.0d;
                        }

                        return null;
                }
            }
    );
}

    @Override
    public String getSchema() throws SQLException {
        checkClosed();
        return databaseName;
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                "MongoDB schema switching is not supported"
        );
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
    }

    @Override
    public String getCatalog() throws SQLException {
        return databaseName;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return Connection.TRANSACTION_NONE;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return true;
    }

    @Override
    public void commit() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                "MongoDB JDBC transactions are not supported"
        );
    }

    @Override
    public void rollback() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                "MongoDB JDBC transactions are not supported"
        );
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                "MongoDB JDBC transactions are not supported"
        );
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                "MongoDB JDBC savepoints are not supported"
        );
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                "MongoDB JDBC savepoints are not supported"
        );
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                "MongoDB JDBC savepoints are not supported"
        );
    }

    @Override
    public PreparedStatement prepareStatement(String sql)
            throws SQLException {

        throw new SQLFeatureNotSupportedException(
                "Prepared statements are not supported"
        );
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql,
            int resultSetType,
            int resultSetConcurrency) throws SQLException {

        throw new SQLFeatureNotSupportedException(
                "Prepared statements are not supported"
        );
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql,
            int resultSetType,
            int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {

        throw new SQLFeatureNotSupportedException(
                "Prepared statements are not supported"
        );
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql,
            int autoGeneratedKeys) throws SQLException {

        throw new SQLFeatureNotSupportedException(
                "Prepared statements are not supported"
        );
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql,
            int[] columnIndexes) throws SQLException {

        throw new SQLFeatureNotSupportedException(
                "Prepared statements are not supported"
        );
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql,
            String[] columnNames) throws SQLException {

        throw new SQLFeatureNotSupportedException(
                "Prepared statements are not supported"
        );
    }

    @Override
    public CallableStatement prepareCall(String sql)
            throws SQLException {

        throw new SQLFeatureNotSupportedException(
                "Callable statements are not supported"
        );
    }

    @Override
    public CallableStatement prepareCall(
            String sql,
            int resultSetType,
            int resultSetConcurrency) throws SQLException {

        throw new SQLFeatureNotSupportedException(
                "Callable statements are not supported"
        );
    }

    @Override
    public CallableStatement prepareCall(
            String sql,
            int resultSetType,
            int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {

        throw new SQLFeatureNotSupportedException(
                "Callable statements are not supported"
        );
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return sql;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map)
            throws SQLException {
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
    }

    @Override
    public int getHoldability() throws SQLException {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public void setClientInfo(String name, String value)
            throws SQLClientInfoException {
    }

    @Override
    public void setClientInfo(Properties properties)
            throws SQLClientInfoException {
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return new Properties();
    }

    @Override
    public Array createArrayOf(
            String typeName,
            Object[] elements) throws SQLException {

        throw new SQLFeatureNotSupportedException(
                "SQL arrays are not supported"
        );
    }

    @Override
    public Struct createStruct(
            String typeName,
            Object[] attributes) throws SQLException {

        throw new SQLFeatureNotSupportedException(
                "SQL structs are not supported"
        );
    }

    @Override
    public Blob createBlob() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                "SQL blobs are not supported"
        );
    }

    @Override
    public Clob createClob() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                "SQL clobs are not supported"
        );
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                "SQL NClobs are not supported"
        );
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                "SQL XML is not supported"
        );
    }

    @Override
        public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setNetworkTimeout(
            Executor executor,
            int milliseconds) throws SQLException {
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        close();
    }

    @Override
    public void beginRequest() throws SQLException {
    }

    @Override
    public void endRequest() throws SQLException {
    }

    @Override
    public boolean setShardingKeyIfValid(
            ShardingKey shardingKey,
            ShardingKey superShardingKey,
            int timeout) throws SQLException {

        return false;
    }

    @Override
    public void setShardingKey(
            ShardingKey shardingKey,
            ShardingKey superShardingKey) throws SQLException {

        throw new SQLFeatureNotSupportedException(
                "Sharding keys are not supported"
        );
    }

    @Override
    public void setShardingKey(ShardingKey shardingKey)
            throws SQLException {

        throw new SQLFeatureNotSupportedException(
                "Sharding keys are not supported"
        );
    }

    @Override
    public boolean setShardingKeyIfValid(
            ShardingKey shardingKey,
            int timeout) throws SQLException {

        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        }

        throw new SQLException(
                "MongoConnection is not a wrapper for " + iface.getName()
        );
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return iface.isInstance(this);
    }

    private void checkClosed() throws SQLException {
        if (closed) {
            throw new SQLException("MongoDB connection is closed");
        }
    }
}