package com.helical.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JDBC adapter for MongoDB used by the Helical Insight datasource layer.
 *
 * <p>The project already exposes MongoDB as a JDBC datasource through the
 * {@code com.helical.mongodb.MongoJdbcDriver} configuration. The official
 * MongoDB Java driver is not itself a JDBC driver, so this class bridges the
 * existing JDBC datasource infrastructure to the MongoDB Java driver.</p>
 *
 * <p>The adapter intentionally keeps the JDBC surface small. It supports
 * connection validation and the SELECT form used by metadata/reporting
 * flows, while unsupported JDBC operations are reported through standard
 * JDBC exceptions.</p>
 */
public final class MongoJdbcDriver implements Driver {

    public static final String DRIVER_NAME = "com.helical.mongodb.MongoJdbcDriver";
    private static final String URL_PREFIX = "jdbc:mongodb:";
    private static final String MONGO_PREFIX = "mongodb://";
    private static final String MONGO_SRV_PREFIX = "mongodb+srv://";

    private static final Pattern SELECT_PATTERN = Pattern.compile(
            "(?is)^\\s*select\\s+(.+?)\\s+from\\s+([A-Za-z0-9_.$-]+)(?:\\s+limit\\s+(\\d+))?\\s*;?\\s*$");

    static {
        try {
            DriverManager.registerDriver(new MongoJdbcDriver());
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public MongoJdbcDriver() {
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }

        Properties properties = info == null ? new Properties() : info;
        String mongoUrl = normalizeUrl(url);
        MongoClient client = createClient(mongoUrl, properties);
        MongoClientURI mongoUri = new MongoClientURI(mongoUrl);
        String databaseName = mongoUri.getDatabase();

        if (databaseName == null || databaseName.trim().isEmpty()) {
            client.close();
            throw new SQLException("MongoDB database name is required in the JDBC URL: " + url);
        }

        MongoDatabase database = client.getDatabase(databaseName);
        return connectionProxy(client, database, mongoUrl);
    }

    @Override
    public boolean acceptsURL(String url) {
        if (url == null) {
            return false;
        }
        return url.startsWith(URL_PREFIX)
                || url.startsWith(MONGO_PREFIX)
                || url.startsWith(MONGO_SRV_PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
        return new DriverPropertyInfo[]{
                new DriverPropertyInfo("user", "", false, "MongoDB username", null),
                new DriverPropertyInfo("password", "", false, "MongoDB password", null)
        };
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() {
        return Logger.getLogger(DRIVER_NAME);
    }

    private static String normalizeUrl(String url) throws SQLException {
        String value = url.trim();
        if (value.startsWith(URL_PREFIX)) {
            value = value.substring("jdbc:".length());
        }
        if (!value.startsWith(MONGO_PREFIX) && !value.startsWith(MONGO_SRV_PREFIX)) {
            throw new SQLException("Unsupported MongoDB JDBC URL: " + url);
        }
        return value;
    }

    private static MongoClient createClient(String mongoUrl, Properties properties) throws SQLException {
        try {
            MongoClientURI uri = new MongoClientURI(mongoUrl);
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");

            if (isBlank(user) || uri.getCredentials() != null) {
                return new MongoClient(uri);
            }

            String database = uri.getDatabase();
            MongoCredential credential = MongoCredential.createCredential(
                    user,
                    isBlank(database) ? "admin" : database,
                    password == null ? new char[0] : password.toCharArray());

            String host = uri.getHosts().isEmpty() ? "localhost" : uri.getHosts().get(0);
            return new MongoClient(new ServerAddress(host), credential, uri.getOptions());
        } catch (RuntimeException e) {
            throw new SQLException("Unable to create MongoDB client: " + e.getMessage(), e);
        }
    }

    private static Connection connectionProxy(MongoClient client, MongoDatabase database, String url) {
        AtomicBoolean closed = new AtomicBoolean(false);

        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if ("close".equals(name)) {
                if (closed.compareAndSet(false, true)) {
                    client.close();
                }
                return null;
            }
            if ("isClosed".equals(name)) return closed.get();
            if ("isValid".equals(name)) return !closed.get();
            if ("createStatement".equals(name)) {
                ensureOpen(closed);
                return statementProxy((Connection) proxy, database, null);
            }
            if ("prepareStatement".equals(name)) {
                ensureOpen(closed);
                return statementProxy((Connection) proxy, database,
                        args != null && args.length > 0 ? String.valueOf(args[0]) : null);
            }
            if ("getMetaData".equals(name)) {
                ensureOpen(closed);
                return metadataProxy(database, url);
            }
            if ("getCatalog".equals(name) || "getSchema".equals(name)) return database.getName();
            if ("setSchema".equals(name) || "setCatalog".equals(name)
                    || "setAutoCommit".equals(name) || "commit".equals(name)
                    || "rollback".equals(name) || "clearWarnings".equals(name)) {
                ensureOpen(closed);
                return null;
            }
            if ("getAutoCommit".equals(name)) return true;
            if ("getWarnings".equals(name)) return null;
            if ("isReadOnly".equals(name)) return false;
            if ("setReadOnly".equals(name)) return null;
            if ("getTransactionIsolation".equals(name)) return Connection.TRANSACTION_NONE;
            if ("setTransactionIsolation".equals(name)) return null;
            if ("unwrap".equals(name)) {
                Class<?> type = (Class<?>) args[0];
                if (type.isInstance(proxy)) return proxy;
                throw new SQLException("Not a wrapper for " + type.getName());
            }
            if ("isWrapperFor".equals(name)) return ((Class<?>) args[0]).isInstance(proxy);
            if ("toString".equals(name)) return "MongoJdbcConnection{" + url + "}";
            return defaultValue(method.getReturnType());
        };

        return (Connection) Proxy.newProxyInstance(
                MongoJdbcDriver.class.getClassLoader(), new Class[]{Connection.class}, handler);
    }

    private static Statement statementProxy(Connection connection, MongoDatabase database, String preparedSql) {
        AtomicBoolean closed = new AtomicBoolean(false);
        final ResultSet[] currentResult = new ResultSet[1];
        final int[] updateCount = new int[]{-1};

        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if ("close".equals(name)) {
                closed.set(true);
                closeQuietly(currentResult[0]);
                return null;
            }
            if ("isClosed".equals(name)) return closed.get();
            if ("getConnection".equals(name)) return connection;
            if ("executeQuery".equals(name)) {
                ensureOpen(closed);
                String sql = preparedSql != null ? preparedSql : String.valueOf(args[0]);
                currentResult[0] = executeSelect(database, sql);
                updateCount[0] = -1;
                return currentResult[0];
            }
            if ("execute".equals(name)) {
                ensureOpen(closed);
                String sql = preparedSql != null ? preparedSql : String.valueOf(args[0]);
                currentResult[0] = executeSelect(database, sql);
                updateCount[0] = -1;
                return true;
            }
            if ("executeUpdate".equals(name)) {
                ensureOpen(closed);
                throw new SQLFeatureNotSupportedException("MongoDB JDBC driver is read-only");
            }
            if ("getResultSet".equals(name)) return currentResult[0];
            if ("getUpdateCount".equals(name)) return updateCount[0];
            if ("getMoreResults".equals(name)) return false;
            if ("setMaxRows".equals(name) || "setFetchSize".equals(name)
                    || "setQueryTimeout".equals(name) || "setMaxFieldSize".equals(name)
                    || "setFetchDirection".equals(name) || "clearWarnings".equals(name)) return null;
            if ("getMaxRows".equals(name) || "getFetchSize".equals(name)
                    || "getQueryTimeout".equals(name) || "getMaxFieldSize".equals(name)) return 0;
            if ("getFetchDirection".equals(name)) return ResultSet.FETCH_FORWARD;
            if ("getWarnings".equals(name)) return null;
            if ("unwrap".equals(name)) {
                Class<?> type = (Class<?>) args[0];
                if (type.isInstance(proxy)) return proxy;
                throw new SQLException("Not a wrapper for " + type.getName());
            }
            if ("isWrapperFor".equals(name)) return ((Class<?>) args[0]).isInstance(proxy);
            if ("toString".equals(name)) return "MongoJdbcStatement";
            return defaultValue(method.getReturnType());
        };

        return (Statement) Proxy.newProxyInstance(
                MongoJdbcDriver.class.getClassLoader(), new Class[]{Statement.class}, handler);
    }

    private static ResultSet executeSelect(MongoDatabase database, String sql) throws SQLException {
        Matcher matcher = SELECT_PATTERN.matcher(sql);
        if (!matcher.matches()) {
            throw new SQLException("Unsupported MongoDB SQL. Supported form: SELECT <fields> FROM <collection> [LIMIT n]");
        }

        String projection = matcher.group(1).trim();
        String collectionName = matcher.group(2);
        int limit = matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3));

        int dot = collectionName.lastIndexOf('.');
        if (dot >= 0 && dot < collectionName.length() - 1) {
            collectionName = collectionName.substring(dot + 1);
        }

        MongoCollection<Document> collection = database.getCollection(collectionName);
        FindIterable<Document> iterable = collection.find();
        if (limit > 0) iterable = iterable.limit(limit);

        if (!"*".equals(projection)) {
            Document projectionDocument = new Document();
            for (String field : projection.split(",")) {
                String clean = field.trim();
                if (!clean.isEmpty()) projectionDocument.append(clean, 1);
            }
            iterable = iterable.projection(projectionDocument);
        }

        List<Document> rows = new ArrayList<>();
        try (MongoCursor<Document> cursor = iterable.iterator()) {
            while (cursor.hasNext()) rows.add(cursor.next());
        }
        return resultSetProxy(rows);
    }

    private static ResultSet resultSetProxy(List<Document> documents) {
        AtomicBoolean closed = new AtomicBoolean(false);
        final int[] index = new int[]{-1};
        final boolean[] wasNull = new boolean[]{false};
        List<String> columns = columns(documents);

        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if ("next".equals(name)) {
                if (closed.get()) throw new SQLException("ResultSet is closed");
                if (index[0] + 1 < documents.size()) {
                    index[0]++;
                    return true;
                }
                index[0] = documents.size();
                return false;
            }
            if ("close".equals(name)) { closed.set(true); return null; }
            if ("isClosed".equals(name)) return closed.get();
            if ("wasNull".equals(name)) return wasNull[0];
            if ("getMetaData".equals(name)) return resultSetMetaDataProxy(columns);
            if ("findColumn".equals(name)) return columnIndex(columns, String.valueOf(args[0]));
            if ("getObject".equals(name) || "getString".equals(name)
                    || "getInt".equals(name) || "getLong".equals(name)
                    || "getDouble".equals(name) || "getBoolean".equals(name)) {
                Object value = valueAt(documents, columns, index[0], args[0]);
                wasNull[0] = value == null;
                if ("getString".equals(name)) return value == null ? null : String.valueOf(value);
                if ("getInt".equals(name)) return value == null ? 0 : ((Number) convertNumber(value, Integer.class)).intValue();
                if ("getLong".equals(name)) return value == null ? 0L : ((Number) convertNumber(value, Long.class)).longValue();
                if ("getDouble".equals(name)) return value == null ? 0D : ((Number) convertNumber(value, Double.class)).doubleValue();
                if ("getBoolean".equals(name)) return value != null && (value instanceof Boolean ? value : Boolean.parseBoolean(String.valueOf(value)));
                return value;
            }
            if ("getRow".equals(name)) return index[0] + 1;
            if ("beforeFirst".equals(name)) { index[0] = -1; return null; }
            if ("getType".equals(name)) return ResultSet.TYPE_FORWARD_ONLY;
            if ("getConcurrency".equals(name)) return ResultSet.CONCUR_READ_ONLY;
            if ("unwrap".equals(name)) {
                Class<?> type = (Class<?>) args[0];
                if (type.isInstance(proxy)) return proxy;
                throw new SQLException("Not a wrapper for " + type.getName());
            }
            if ("isWrapperFor".equals(name)) return ((Class<?>) args[0]).isInstance(proxy);
            return defaultValue(method.getReturnType());
        };

        return (ResultSet) Proxy.newProxyInstance(
                MongoJdbcDriver.class.getClassLoader(), new Class[]{ResultSet.class}, handler);
    }

    private static DatabaseMetaData metadataProxy(MongoDatabase database, String url) {
        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if ("getDatabaseProductName".equals(name)) return "MongoDB";
            if ("getDatabaseProductVersion".equals(name)) return "unknown";
            if ("getDriverName".equals(name)) return DRIVER_NAME;
            if ("getDriverVersion".equals(name)) return "1.0";
            if ("getURL".equals(name)) return "jdbc:mongodb:" + url;
            if ("getUserName".equals(name)) return "";
            if ("supportsTransactions".equals(name)) return false;
            if ("supportsResultSetType".equals(name)) return (Integer) args[0] == ResultSet.TYPE_FORWARD_ONLY;
            if ("supportsResultSetConcurrency".equals(name)) return (Integer) args[1] == ResultSet.CONCUR_READ_ONLY;
            if ("getTables".equals(name)) return emptyResultSet();
            if ("getColumns".equals(name)) return emptyResultSet();
            if ("unwrap".equals(name)) {
                Class<?> type = (Class<?>) args[0];
                if (type.isInstance(proxy)) return proxy;
                throw new SQLException("Not a wrapper for " + type.getName());
            }
            if ("isWrapperFor".equals(name)) return ((Class<?>) args[0]).isInstance(proxy);
            return defaultValue(method.getReturnType());
        };
        return (DatabaseMetaData) Proxy.newProxyInstance(
                MongoJdbcDriver.class.getClassLoader(), new Class[]{DatabaseMetaData.class}, handler);
    }

    private static ResultSet emptyResultSet() {
        return resultSetProxy(Collections.emptyList());
    }

    private static ResultSetMetaData resultSetMetaDataProxy(List<String> columns) {
        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if ("getColumnCount".equals(name)) return columns.size();
            if ("getColumnName".equals(name) || "getColumnLabel".equals(name)) return columns.get((Integer) args[0] - 1);
            if ("getColumnType".equals(name)) return java.sql.Types.VARCHAR;
            if ("getColumnTypeName".equals(name)) return "VARCHAR";
            if ("isNullable".equals(name)) return ResultSetMetaData.columnNullable;
            if ("isReadOnly".equals(name)) return true;
            if ("isWritable".equals(name) || "isDefinitelyWritable".equals(name)) return false;
            if ("getSchemaName".equals(name)) return "";
            if ("getTableName".equals(name)) return "";
            if ("getCatalogName".equals(name)) return "";
            if ("getPrecision".equals(name) || "getScale".equals(name)) return 0;
            if ("getColumnClassName".equals(name)) return Object.class.getName();
            return defaultValue(method.getReturnType());
        };
        return (ResultSetMetaData) Proxy.newProxyInstance(
                MongoJdbcDriver.class.getClassLoader(), new Class[]{ResultSetMetaData.class}, handler);
    }

    private static List<String> columns(List<Document> documents) {
        if (documents.isEmpty()) return Collections.emptyList();
        LinkedHashMap<String, Boolean> names = new LinkedHashMap<>();
        for (Document document : documents) {
            for (String key : document.keySet()) names.put(key, Boolean.TRUE);
        }
        return new ArrayList<>(names.keySet());
    }

    private static Object valueAt(List<Document> documents, List<String> columns, int row, Object key) throws SQLException {
        if (row < 0 || row >= documents.size()) throw new SQLException("Cursor is not positioned on a row");
        String column;
        if (key instanceof Number) {
            int number = ((Number) key).intValue();
            if (number < 1 || number > columns.size()) throw new SQLException("Invalid column index: " + number);
            column = columns.get(number - 1);
        } else {
            column = String.valueOf(key);
        }
        return documents.get(row).get(column);
    }

    private static int columnIndex(List<String> columns, String column) throws SQLException {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).equalsIgnoreCase(column)) return i + 1;
        }
        throw new SQLException("Unknown column: " + column);
    }

    private static Object convertNumber(Object value, Class<?> target) {
        if (value instanceof Number) return value;
        String text = String.valueOf(value);
        if (target == Integer.class) return Integer.parseInt(text);
        if (target == Long.class) return Long.parseLong(text);
        return Double.parseDouble(text);
    }

    private static void ensureOpen(AtomicBoolean closed) throws SQLException {
        if (closed.get()) throw new SQLException("JDBC object is closed");
    }

    private static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try { closeable.close(); } catch (Exception ignored) { }
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0F;
        if (type == double.class) return 0D;
        if (type == char.class) return '\0';
        return null;
    }
}
