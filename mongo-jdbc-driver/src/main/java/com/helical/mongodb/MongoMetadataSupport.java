package com.helical.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetProvider;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

final class MongoMetadataSupport {

    private MongoMetadataSupport() {
    }

    static ResultSet getCatalogs(
            MongoClient mongoClient,
            String currentDatabase) throws SQLException {

        List<String> databases = new ArrayList<>();

        try {
            for (String database : mongoClient.listDatabaseNames()) {
                databases.add(database);
            }
        } catch (Exception e) {
            // A restricted MongoDB user may not have list-databases permission.
            databases.add(currentDatabase);
        }

        CachedRowSet result = createResultSet(
                new String[]{"TABLE_CAT"},
                new int[]{Types.VARCHAR});

        for (String database : databases) {
            addRow(result, database);
        }

        return result;
    }

    static ResultSet getSchemas() throws SQLException {
        // MongoDB has databases and collections, but no JDBC-style schemas.
        return createResultSet(
                new String[]{"TABLE_SCHEM", "TABLE_CATALOG"},
                new int[]{Types.VARCHAR, Types.VARCHAR});
    }

    static ResultSet getTables(
            MongoDatabase database,
            String catalog,
            String schema,
            String tablePattern,
            String[] types) throws SQLException {

        CachedRowSet result = createResultSet(
                new String[]{
                        "TABLE_CAT",
                        "TABLE_SCHEM",
                        "TABLE_NAME",
                        "TABLE_TYPE",
                        "REMARKS",
                        "TYPE_CAT",
                        "TYPE_SCHEM",
                        "TYPE_NAME",
                        "SELF_REFERENCING_COL_NAME",
                        "REF_GENERATION"
                },
                new int[]{
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR
                });

        if (!matchesCatalog(catalog, database.getName())
                || !matchesSchema(schema)) {
            return result;
        }

        if (!supportsTableType(types)) {
            return result;
        }

        for (String collectionName : database.listCollectionNames()) {
            // Internal MongoDB collections are not useful as application tables.
            if (collectionName.startsWith("system.")) {
                continue;
            }

            if (!matchesPattern(collectionName, tablePattern)) {
                continue;
            }

            addRow(
                    result,
                    database.getName(),
                    null,
                    collectionName,
                    "TABLE",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
        }

        return result;
    }

    static ResultSet getColumns(
            MongoDatabase database,
            String catalog,
            String schema,
            String tablePattern,
            String columnPattern) throws SQLException {

        CachedRowSet result = createResultSet(
                new String[]{
                        "TABLE_CAT",
                        "TABLE_SCHEM",
                        "TABLE_NAME",
                        "COLUMN_NAME",
                        "DATA_TYPE",
                        "TYPE_NAME",
                        "COLUMN_SIZE",
                        "BUFFER_LENGTH",
                        "DECIMAL_DIGITS",
                        "NUM_PREC_RADIX",
                        "NULLABLE",
                        "REMARKS",
                        "COLUMN_DEF",
                        "SQL_DATA_TYPE",
                        "SQL_DATETIME_SUB",
                        "CHAR_OCTET_LENGTH",
                        "ORDINAL_POSITION",
                        "IS_NULLABLE",
                        "SCOPE_CATALOG",
                        "SCOPE_SCHEMA",
                        "SCOPE_TABLE",
                        "SOURCE_DATA_TYPE",
                        "IS_AUTOINCREMENT",
                        "IS_GENERATEDCOLUMN"
                },
                new int[]{
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.INTEGER,
                        Types.VARCHAR,
                        Types.INTEGER,
                        Types.INTEGER,
                        Types.INTEGER,
                        Types.INTEGER,
                        Types.INTEGER,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.INTEGER,
                        Types.INTEGER,
                        Types.INTEGER,
                        Types.INTEGER,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.SMALLINT,
                        Types.VARCHAR,
                        Types.VARCHAR
                });

        if (!matchesCatalog(catalog, database.getName())
                || !matchesSchema(schema)) {
            return result;
        }

        int ordinalPosition = 1;

        for (String collectionName : database.listCollectionNames()) {
            if (collectionName.startsWith("system.")
                    || !matchesPattern(collectionName, tablePattern)) {
                continue;
            }

            MongoCollection<Document> collection =
                    database.getCollection(collectionName);

            Map<String, MongoColumnInfo> columns =
                    inferColumns(collection);

            for (Map.Entry<String, MongoColumnInfo> entry : columns.entrySet()) {
                String columnName = entry.getKey();

                if (!matchesPattern(columnName, columnPattern)) {
                    continue;
                }

                MongoColumnInfo info = entry.getValue();

                addRow(
                        result,
                        database.getName(),
                        null,
                        collectionName,
                        columnName,
                        info.jdbcType,
                        info.typeName,
                        info.columnSize,
                        null,
                        info.decimalDigits,
                        10,
                        info.nullable
                                ? ResultSetMetaData.columnNullable
                                : ResultSetMetaData.columnNoNulls,
                        null,
                        null,
                        null,
                        null,
                        null,
                        ordinalPosition++,
                        info.nullable ? "YES" : "NO",
                        null,
                        null,
                        null,
                        null,
                        "NO",
                        "NO");
            }
        }

        return result;
    }

    static ResultSet getPrimaryKeys(
            MongoDatabase database,
            String catalog,
            String schema,
            String tableName) throws SQLException {

        CachedRowSet result = createResultSet(
                new String[]{
                        "TABLE_CAT",
                        "TABLE_SCHEM",
                        "TABLE_NAME",
                        "COLUMN_NAME",
                        "KEY_SEQ",
                        "PK_NAME"
                },
                new int[]{
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.SMALLINT,
                        Types.VARCHAR
                });

        if (!matchesCatalog(catalog, database.getName())
                || !matchesSchema(schema)
                || tableName == null) {
            return result;
        }

        boolean exists = false;

        for (String collectionName : database.listCollectionNames()) {
            if (collectionName.equals(tableName)) {
                exists = true;
                break;
            }
        }

        if (exists) {
            addRow(
                    result,
                    database.getName(),
                    null,
                    tableName,
                    "_id",
                    (short) 1,
                    "_id");
        }

        return result;
    }

    static ResultSet getEmptyImportedKeys() throws SQLException {
        return createResultSet(
                new String[]{
                        "PKTABLE_CAT",
                        "PKTABLE_SCHEM",
                        "PKTABLE_NAME",
                        "PKCOLUMN_NAME",
                        "FKTABLE_CAT",
                        "FKTABLE_SCHEM",
                        "FKTABLE_NAME",
                        "FKCOLUMN_NAME",
                        "KEY_SEQ",
                        "UPDATE_RULE",
                        "DELETE_RULE",
                        "FK_NAME",
                        "PK_NAME",
                        "DEFERRABILITY"
                },
                new int[]{
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.SMALLINT,
                        Types.SMALLINT,
                        Types.SMALLINT,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.SMALLINT
                });
    }

    private static Map<String, MongoColumnInfo> inferColumns(
            MongoCollection<Document> collection) {

        Map<String, MongoColumnInfo> columns = new LinkedHashMap<>();
        int documentCount = 0;

        // Inspect a bounded sample rather than scanning an entire collection.
        for (Document document : collection.find().limit(100)) {
            documentCount++;

            for (Map.Entry<String, Object> entry : document.entrySet()) {
                MongoColumnInfo info =
                        columns.computeIfAbsent(
                                entry.getKey(),
                                ignored -> new MongoColumnInfo());

                info.observe(entry.getValue());
            }
        }

        for (MongoColumnInfo info : columns.values()) {
            if (info.seenCount < documentCount) {
                info.nullable = true;
            }
        }

        return columns;
    }

    private static CachedRowSet createResultSet(
            String[] columnNames,
            int[] columnTypes) throws SQLException {

        CachedRowSet result =
                RowSetProvider.newFactory().createCachedRowSet();

        RowSetMetaDataImpl metadata = new RowSetMetaDataImpl();
        metadata.setColumnCount(columnNames.length);

        for (int i = 0; i < columnNames.length; i++) {
            int column = i + 1;

            metadata.setColumnName(column, columnNames[i]);
            metadata.setColumnLabel(column, columnNames[i]);
            metadata.setColumnType(column, columnTypes[i]);
            metadata.setNullable(
                    column,
                    ResultSetMetaData.columnNullable);
            metadata.setColumnDisplaySize(column, 255);
            metadata.setPrecision(column, 255);
            metadata.setScale(column, 0);
        }

        result.setMetaData(metadata);
        return result;
    }

    private static void addRow(
            CachedRowSet result,
            Object... values) throws SQLException {

        result.moveToInsertRow();

        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                result.updateNull(i + 1);
            } else {
                result.updateObject(i + 1, values[i]);
            }
        }

        result.insertRow();
        result.moveToCurrentRow();
        result.beforeFirst();
    }

    private static boolean matchesCatalog(
            String catalog,
            String databaseName) {

        return catalog == null
                || catalog.isBlank()
                || "%".equals(catalog)
                || databaseName.equals(catalog);
    }

    private static boolean matchesSchema(String schema) {
        // MongoDB has no separate schema layer.
        return schema == null
                || schema.isBlank()
                || "%".equals(schema);
    }

    private static boolean supportsTableType(String[] types) {
        if (types == null || types.length == 0) {
            return true;
        }

        for (String type : types) {
            if ("TABLE".equalsIgnoreCase(type)) {
                return true;
            }
        }

        return false;
    }

    private static boolean matchesPattern(
            String value,
            String pattern) {

        if (pattern == null
                || pattern.isBlank()
                || "%".equals(pattern)) {
            return true;
        }

        StringBuilder regex = new StringBuilder("^");

        for (int i = 0; i < pattern.length(); i++) {
            char character = pattern.charAt(i);

            if (character == '%') {
                regex.append(".*");
            } else if (character == '_') {
                regex.append('.');
            } else {
                regex.append(
                        Pattern.quote(
                                String.valueOf(character)));
            }
        }

        regex.append('$');

        return Pattern.compile(regex.toString())
                .matcher(value)
                .matches();
    }

    private static final class MongoColumnInfo {

        private int jdbcType = Types.OTHER;
        private String typeName = "JSON";
        private int columnSize = 0;
        private int decimalDigits = 0;
        private int seenCount = 0;
        private boolean nullable = false;

        private void observe(Object value) {
            seenCount++;

            if (value == null) {
                nullable = true;
                return;
            }

            int detectedType = detectJdbcType(value);
            String detectedTypeName = detectTypeName(value);

            if (seenCount == 1 || jdbcType == Types.OTHER) {
                jdbcType = detectedType;
                typeName = detectedTypeName;
                columnSize = detectColumnSize(value);
                decimalDigits = detectDecimalDigits(value);
                return;
            }

            if (jdbcType != detectedType) {
                // MongoDB fields can contain different BSON types.
                // JSON/OTHER is the safest JDBC representation.
                jdbcType = Types.OTHER;
                typeName = "JSON";
                columnSize = 0;
                decimalDigits = 0;
            }
        }

        private static int detectJdbcType(Object value) {
            if (value instanceof String
                    || value instanceof ObjectId) {
                return Types.VARCHAR;
            }

            if (value instanceof Integer
                    || value instanceof Short
                    || value instanceof Byte) {
                return Types.INTEGER;
            }

            if (value instanceof Long) {
                return Types.BIGINT;
            }

            if (value instanceof Float) {
                return Types.REAL;
            }

            if (value instanceof Double) {
                return Types.DOUBLE;
            }

            if (value instanceof Decimal128
                    || value instanceof BigDecimal) {
                return Types.DECIMAL;
            }

            if (value instanceof Boolean) {
                return Types.BOOLEAN;
            }

            if (value instanceof Date) {
                return Types.TIMESTAMP;
            }

            if (value instanceof Binary) {
                return Types.VARBINARY;
            }

            return Types.OTHER;
        }

        private static String detectTypeName(Object value) {
            if (value instanceof ObjectId) {
                return "VARCHAR";
            }

            if (value instanceof Document
                    || value instanceof List
                    || value instanceof Map) {
                return "JSON";
            }

            return switch (detectJdbcType(value)) {
                case Types.VARCHAR -> "VARCHAR";
                case Types.INTEGER -> "INTEGER";
                case Types.BIGINT -> "BIGINT";
                case Types.REAL -> "REAL";
                case Types.DOUBLE -> "DOUBLE";
                case Types.DECIMAL -> "DECIMAL";
                case Types.BOOLEAN -> "BOOLEAN";
                case Types.TIMESTAMP -> "TIMESTAMP";
                case Types.VARBINARY -> "VARBINARY";
                default -> "JSON";
            };
        }

        private static int detectColumnSize(Object value) {
            if (value instanceof String
                    || value instanceof ObjectId) {
                return 255;
            }

            if (value instanceof Integer) {
                return 10;
            }

            if (value instanceof Long) {
                return 19;
            }

            if (value instanceof Float
                    || value instanceof Double) {
                return 17;
            }

            if (value instanceof Decimal128
                    || value instanceof BigDecimal) {
                return 38;
            }

            return 0;
        }

        private static int detectDecimalDigits(Object value) {
            if (value instanceof Decimal128
                    || value instanceof BigDecimal) {
                return 10;
            }

            return 0;
        }
    }
}