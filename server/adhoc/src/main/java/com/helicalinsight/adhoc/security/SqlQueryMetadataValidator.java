package com.helicalinsight.adhoc.security;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubSelect;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validates raw SQL queries against the tables and columns exposed by metadata service response.
 */
public final class SqlQueryMetadataValidator {

    private static final String IDENTIFIER_REGEX_PREFIX_KEY = "sql.query.identifier.regex.prefix";
    private static final String IDENTIFIER_REGEX_SUFFIX_KEY = "sql.query.identifier.regex.suffix";
    private static final String DEFAULT_IDENTIFIER_REGEX_PREFIX = "(?<![A-Za-z0-9_])[\"`\\[]?";
    private static final String DEFAULT_IDENTIFIER_REGEX_SUFFIX = "[\"`\\]]?(?![A-Za-z0-9_])";

    private static final String IDENTIFIER_REGEX_PREFIX = resolveIdentifierRegexPart(
            IDENTIFIER_REGEX_PREFIX_KEY, DEFAULT_IDENTIFIER_REGEX_PREFIX);
    private static final String IDENTIFIER_REGEX_SUFFIX = resolveIdentifierRegexPart(
            IDENTIFIER_REGEX_SUFFIX_KEY, DEFAULT_IDENTIFIER_REGEX_SUFFIX);

    private SqlQueryMetadataValidator() {
    }

    private static String resolveIdentifierRegexPart(String propertyKey, String defaultValue) {
        Map<String, String> properties = new PropertiesFileReader().read("Admin", "defaults.properties");
        if (properties == null) {
            return defaultValue;
        }
        String configuredValue = properties.get(propertyKey);
        return StringUtils.isNotBlank(configuredValue) ? configuredValue : defaultValue;
    }

    /**
     * Builds a map of allowed table names to their column names from metadata service JSON.
     * The metadata JSON follows the structure produced by {@code AdhocViewJsonProvider}.
     */
    public static Map<String, Set<String>> buildAllowedTablesColumns(JsonObject metadataData) {
        Map<String, Set<String>> allowed = new LinkedHashMap<>();
        if (metadataData == null) {
            return allowed;
        }

        JsonObject tables = GsonUtility.optJsonObject(metadataData, "tables");
        if (tables == null) {
            return allowed;
        }

        for (Map.Entry<String, JsonElement> tableEntry : tables.entrySet()) {
            if (!tableEntry.getValue().isJsonObject()) {
                continue;
            }
            JsonObject tableJson = tableEntry.getValue().getAsJsonObject();
            String tableName = GsonUtility.optString(tableJson, "name");
            if (StringUtils.isBlank(tableName)) {
                tableName = tableEntry.getKey();
            }

            Set<String> columns = new HashSet<>();
            JsonObject columnsJson = GsonUtility.optJsonObject(tableJson, "columns");
            if (columnsJson != null) {
                for (Map.Entry<String, JsonElement> columnEntry : columnsJson.entrySet()) {
                    columns.add(columnEntry.getKey());
                }
            }

            registerTable(allowed, tableName, columns);
            registerTable(allowed, tableEntry.getKey(), columns);

            String alias = GsonUtility.optString(tableJson, "alias");
            if (StringUtils.isNotBlank(alias)) {
                registerTable(allowed, alias, columns);
            }
        }
        return allowed;
    }

    private static void registerTable(Map<String, Set<String>> allowed, String tableName, Set<String> columns) {
        if (StringUtils.isBlank(tableName)) {
            return;
        }
        String normalized = normalizeIdentifier(tableName);
        allowed.put(normalized, columns);
    }

    /**
     * Ensures referenced tables and SELECT-clause columns exist in the allowed metadata scope.
     */
    public static void validateQueryAgainstMetadata(String sql, Map<String, Set<String>> allowedTablesColumns) {
        if (allowedTablesColumns == null || allowedTablesColumns.isEmpty() || StringUtils.isBlank(sql)) {
            return;
        }

        SqlUtils sqlUtils = new SqlUtils();
        String normalizedSql = sqlUtils.modifedSql(sql);
        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(normalizedSql);
        } catch (Exception exception) {
            return;
        }

        if (!(statement instanceof Select)) {
            throw new SecurityException("Only SELECT queries are allowed");
        }

        QueryReferences references = new QueryReferences();
        collectSelectReferences((Select) statement, references);
        validateReferences(references, allowedTablesColumns, sqlUtils);
    }

    /**
     * Cross-checks the SELECT clause of a query against tables/columns that security has hidden.
     * <p>
     * {@code metadataFileJson} is the full metadata (all tables and columns) as stored in the metadata
     * file (structure {@code database.tables.tableList[].columns.column[]}), while
     * {@code metadataServiceData} is the security-filtered response that contains only the tables and
     * columns the current user is allowed to see. The set difference between the two yields the
     * <em>restricted</em> tables and columns.
     * <p>
     * The SELECT-clause expressions are then scanned (aliases are ignored) and, for every restricted
     * table/column name, quote- and qualifier-aware regexes are matched against them. This catches
     * references such as {@code "tablename"."columnname"}, {@code tablename.columnname},
     * {@code tablename.} (any column of a restricted table), or a bare {@code columnname}. If any
     * restricted identifier is present, a {@link SecurityException} is raised.
     *
     * @param sql                 the raw SQL query to inspect.
     * @param metadataFileJson    the full metadata JSON carrying every table and column.
     * @param metadataServiceData the security-filtered metadata JSON carrying only allowed items.
     */
    public static void validateSelectAgainstRestrictedMetadata(String sql, JsonObject metadataFileJson,
                                                               JsonObject metadataServiceData) {
        if (StringUtils.isBlank(sql) || metadataFileJson == null) {
            return;
        }

        Map<String, Set<String>> allTablesColumns = buildAllTablesColumnsFromMetadataFile(metadataFileJson);
        if (allTablesColumns.isEmpty()) {
            return;
        }

        Map<String, Set<String>> allowedTablesColumns = buildAllowedTablesColumns(metadataServiceData);
        if (allowedTablesColumns.isEmpty()) {
            // Nothing is marked as allowed; treat as "no security applied" to avoid rejecting every query.
            return;
        }

        Set<String> restrictedIdentifiers = collectRestrictedIdentifiers(allTablesColumns, allowedTablesColumns);
        if (restrictedIdentifiers.isEmpty()) {
            return;
        }

        String selectClause = extractSelectClause(sql);
        if (StringUtils.isBlank(selectClause)) {
            return;
        }

        for (String identifier : restrictedIdentifiers) {
            if (containsIdentifier(selectClause, identifier)) {
                throw new SecurityException(
                        "The table or column you are trying to access is out of scope or not found in the metadata");
            }
        }
    }

    /**
     * Builds a map of every table (name and originalName) to its column names from the full metadata file JSON.
     */
    static Map<String, Set<String>> buildAllTablesColumnsFromMetadataFile(JsonObject metadataFileJson) {
        Map<String, Set<String>> all = new LinkedHashMap<>();
        if (metadataFileJson == null) {
            return all;
        }

        JsonObject database = GsonUtility.optJsonObject(metadataFileJson, "database");
        if (database == null) {
            return all;
        }
        JsonObject tables = GsonUtility.optJsonObject(database, "tables");
        if (tables == null || !tables.has("tableList") || !tables.get("tableList").isJsonArray()) {
            return all;
        }

        JsonArray tableList = tables.getAsJsonArray("tableList");
        for (JsonElement tableElement : tableList) {
            if (!tableElement.isJsonObject()) {
                continue;
            }
            JsonObject tableJson = tableElement.getAsJsonObject();

            Set<String> columns = new HashSet<>();
            JsonObject columnsHolder = GsonUtility.optJsonObject(tableJson, "columns");
            if (columnsHolder != null && columnsHolder.has("column") && columnsHolder.get("column").isJsonArray()) {
                for (JsonElement columnElement : columnsHolder.getAsJsonArray("column")) {
                    if (!columnElement.isJsonObject()) {
                        continue;
                    }
                    JsonObject columnJson = columnElement.getAsJsonObject();
                    addIfNotBlank(columns, GsonUtility.optString(columnJson, "name"));
                    addIfNotBlank(columns, GsonUtility.optString(columnJson, "originalName"));
                }
            }

            registerTable(all, GsonUtility.optString(tableJson, "name"), columns);
            registerTable(all, GsonUtility.optString(tableJson, "originalName"), columns);
        }
        return all;
    }

    /**
     * Computes the difference between the full metadata and the allowed scope. The result contains the
     * restricted table names (including their simple, unqualified form) and the restricted column names.
     * A column is treated as restricted only when it is not allowed under any allowed table, so that a
     * column that is still exposed by another table does not cause a false positive.
     */
    private static Set<String> collectRestrictedIdentifiers(Map<String, Set<String>> allTablesColumns,
                                                            Map<String, Set<String>> allowedTablesColumns) {
        Set<String> restricted = new HashSet<>();

        for (String tableName : allTablesColumns.keySet()) {
            if (!isAllowedTable(tableName, allowedTablesColumns)) {
                restricted.add(tableName);
                restricted.add(simpleTableName(tableName));
            }
        }

        Set<String> allowedColumns = flattenColumns(allowedTablesColumns);
        for (Set<String> columns : allTablesColumns.values()) {
            for (String column : columns) {
                String normalizedColumn = normalizeIdentifier(column);
                if (!allowedColumns.contains(normalizedColumn)) {
                    restricted.add(normalizedColumn);
                }
            }
        }

        restricted.remove("");
        return restricted;
    }

    private static Set<String> flattenColumns(Map<String, Set<String>> tablesColumns) {
        Set<String> flattened = new HashSet<>();
        for (Set<String> columns : tablesColumns.values()) {
            for (String column : columns) {
                flattened.add(normalizeIdentifier(column));
            }
        }
        return flattened;
    }

    /**
     * Extracts the SELECT-clause expressions (aliases excluded) as a single searchable string. Projections
     * of sub-selects in the FROM clause are included so their exposed columns are covered too.
     */
    private static String extractSelectClause(String sql) {
        SqlUtils sqlUtils = new SqlUtils();
        String normalizedSql = sqlUtils.modifedSql(sql);
        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(normalizedSql);
        } catch (Exception exception) {
            return "";
        }
        if (!(statement instanceof Select)) {
            return "";
        }

        StringBuilder selectClause = new StringBuilder();
        appendSelectItems(((Select) statement).getSelectBody(), selectClause);
        return selectClause.toString();
    }

    private static void appendSelectItems(SelectBody selectBody, StringBuilder builder) {
        if (selectBody instanceof PlainSelect) {
            appendPlainSelectItems((PlainSelect) selectBody, builder);
        } else if (selectBody instanceof SetOperationList) {
            for (SelectBody body : ((SetOperationList) selectBody).getSelects()) {
                appendSelectItems(body, builder);
            }
        } else if (selectBody instanceof SubSelect) {
            appendSelectItems(((SubSelect) selectBody).getSelectBody(), builder);
        }
    }

    private static void appendPlainSelectItems(PlainSelect plainSelect, StringBuilder builder) {
        if (plainSelect.getSelectItems() != null) {
            for (SelectItem selectItem : plainSelect.getSelectItems()) {
                appendSelectItem(selectItem, builder);
            }
        }
        appendFromItemSelects(plainSelect.getFromItem(), builder);
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                appendFromItemSelects(join.getRightItem(), builder);
            }
        }
    }

    private static void appendSelectItem(SelectItem selectItem, StringBuilder builder) {
        if (selectItem instanceof SelectExpressionItem) {
            // getExpression().toString() intentionally excludes the AS alias, so aliases are ignored.
            Expression expression = ((SelectExpressionItem) selectItem).getExpression();
            if (expression != null) {
                builder.append(' ').append(expression).append(' ');
            }
        } else if (selectItem instanceof AllTableColumns) {
            Table table = ((AllTableColumns) selectItem).getTable();
            if (table != null) {
                builder.append(' ').append(table.getFullyQualifiedName()).append(' ');
            }
        }
        // AllColumns (*) exposes nothing identifiable by name and is skipped.
    }

    private static void appendFromItemSelects(FromItem fromItem, StringBuilder builder) {
        if (fromItem instanceof SubSelect) {
            appendSelectItems(((SubSelect) fromItem).getSelectBody(), builder);
        }
    }

    /**
     * Matches {@code identifier} as a standalone SQL identifier inside {@code text}. The identifier may be
     * wrapped in double quotes, back-ticks or square brackets and may be preceded by a qualifier dot
     * (e.g. {@code table.column}). Matching is case-insensitive and never matches a larger identifier.
     */
    private static boolean containsIdentifier(String text, String identifier) {
        if (StringUtils.isBlank(identifier)) {
            return false;
        }
        String regex = IDENTIFIER_REGEX_PREFIX + Pattern.quote(identifier) + IDENTIFIER_REGEX_SUFFIX;
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text).find();
    }

    private static void addIfNotBlank(Set<String> target, String value) {
        if (StringUtils.isNotBlank(value)) {
            target.add(normalizeIdentifier(value));
        }
    }

    private static void collectSelectReferences(Select select, QueryReferences references) {
        SelectBody body = select.getSelectBody();
        if (body instanceof PlainSelect) {
            collectPlainSelectReferences((PlainSelect) body, references);
        } else if (body instanceof SetOperationList) {
            for (SelectBody selectBody : ((SetOperationList) body).getSelects()) {
                if (selectBody instanceof PlainSelect) {
                    collectPlainSelectReferences((PlainSelect) selectBody, references);
                } else if (selectBody instanceof SubSelect) {
                    collectSubSelectReferences((SubSelect) selectBody, references);
                }
            }
        } else if (body instanceof SubSelect) {
            collectSubSelectReferences((SubSelect) body, references);
        }
    }

    private static void collectSubSelectReferences(SubSelect subSelect, QueryReferences references) {
        SelectBody selectBody = subSelect.getSelectBody();
        if (selectBody instanceof PlainSelect) {
            collectPlainSelectReferences((PlainSelect) selectBody, references);
        } else if (selectBody instanceof SetOperationList) {
            Select select = new Select();
            select.setSelectBody(selectBody);
            collectSelectReferences(select, references);
        }
        if (subSelect.getAlias() != null && StringUtils.isNotBlank(subSelect.getAlias().getName())) {
            String alias = normalizeIdentifier(subSelect.getAlias().getName());
            references.aliasToTable.put(alias, alias);
        }
    }

    private static void collectPlainSelectReferences(PlainSelect plainSelect, QueryReferences references) {
        collectFromItem(plainSelect.getFromItem(), references);
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                collectFromItem(join.getRightItem(), references);
            }
        }

        ExpressionVisitorAdapter expressionVisitor = references.newExpressionVisitor();
        if (plainSelect.getSelectItems() != null) {
            for (SelectItem selectItem : plainSelect.getSelectItems()) {
                if (selectItem instanceof SelectExpressionItem) {
                    Expression expression = ((SelectExpressionItem) selectItem).getExpression();
                    if (expression != null) {
                        expression.accept(expressionVisitor);
                    }
                }
            }
        }
    }

    private static void collectFromItem(FromItem fromItem, QueryReferences references) {
        if (fromItem == null) {
            return;
        }
        if (fromItem instanceof Table) {
            Table table = (Table) fromItem;
            String tableName = normalizeIdentifier(table.getName());
            references.queryTables.add(tableName);
            if (table.getAlias() != null && StringUtils.isNotBlank(table.getAlias().getName())) {
                String alias = normalizeIdentifier(table.getAlias().getName());
                references.aliasToTable.put(alias, tableName);
            }
        } else if (fromItem instanceof SubSelect) {
            collectSubSelectReferences((SubSelect) fromItem, references);
        }
    }

    private static void validateReferences(QueryReferences references, Map<String, Set<String>> allowedTablesColumns,
                                           SqlUtils sqlUtils) {
        for (String tableName : references.queryTables) {
            if (!isAllowedTable(tableName, allowedTablesColumns)) {
                throw new SecurityException(
                        "The Table you are trying to access is out of scope or not found in the metadata");
            }
        }

        for (ColumnReference columnReference : references.columnReferences) {
            if ("*".equals(columnReference.columnName) || sqlUtils.checkSqlKeyWords(columnReference.columnName)) {
                continue;
            }

            String tableName = columnReference.tableOrAlias;
            if (StringUtils.isNotBlank(tableName)) {
                tableName = resolveTableName(tableName, references.aliasToTable);
                if (!isAllowedTable(tableName, allowedTablesColumns)) {
                    throw new SecurityException(
                            "The Table you are trying to access is out of scope or not found in the metadata");
                }
                if (!isAllowedColumn(tableName, columnReference.columnName, allowedTablesColumns)) {
                    throw new SecurityException(
                            "The column you are trying to access is out of scope or not found in the metadata");
                }
            } else {
                if (!isAllowedColumnInQueryTables(columnReference.columnName, references, allowedTablesColumns)) {
                    throw new SecurityException(
                            "The column you are trying to access is out of scope or not found in the metadata");
                }
            }
        }
    }

    private static boolean isAllowedTable(String tableName, Map<String, Set<String>> allowedTablesColumns) {
        String normalized = normalizeIdentifier(tableName);
        if (allowedTablesColumns.containsKey(normalized)) {
            return true;
        }
        String simpleName = simpleTableName(normalized);
        for (String allowedTable : allowedTablesColumns.keySet()) {
            if (simpleTableName(allowedTable).equalsIgnoreCase(simpleName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAllowedColumn(String tableName, String columnName,
                                             Map<String, Set<String>> allowedTablesColumns) {
        Set<String> columns = findColumnsForTable(tableName, allowedTablesColumns);
        if (columns == null || columns.isEmpty()) {
            return false;
        }
        String normalizedColumn = normalizeIdentifier(columnName);
        for (String allowedColumn : columns) {
            if (normalizeIdentifier(allowedColumn).equalsIgnoreCase(normalizedColumn)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAllowedColumnInQueryTables(String columnName, QueryReferences references,
                                                        Map<String, Set<String>> allowedTablesColumns) {
        String normalizedColumn = normalizeIdentifier(columnName);
        for (String queryTable : references.queryTables) {
            String resolvedTable = resolveTableName(queryTable, references.aliasToTable);
            Set<String> columns = findColumnsForTable(resolvedTable, allowedTablesColumns);
            if (columns == null) {
                continue;
            }
            for (String allowedColumn : columns) {
                if (normalizeIdentifier(allowedColumn).equalsIgnoreCase(normalizedColumn)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Set<String> findColumnsForTable(String tableName, Map<String, Set<String>> allowedTablesColumns) {
        Set<String> columns = allowedTablesColumns.get(normalizeIdentifier(tableName));
        if (columns != null) {
            return columns;
        }
        String simpleName = simpleTableName(normalizeIdentifier(tableName));
        for (Map.Entry<String, Set<String>> entry : allowedTablesColumns.entrySet()) {
            if (simpleTableName(entry.getKey()).equalsIgnoreCase(simpleName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static String resolveTableName(String tableOrAlias, Map<String, String> aliasToTable) {
        String normalized = normalizeIdentifier(tableOrAlias);
        if (aliasToTable.containsKey(normalized)) {
            return aliasToTable.get(normalized);
        }
        return normalized;
    }

    private static String simpleTableName(String tableName) {
        if (tableName == null) {
            return "";
        }
        int index = tableName.lastIndexOf('.');
        if (index >= 0 && index < tableName.length() - 1) {
            return tableName.substring(index + 1);
        }
        return tableName;
    }

    private static String normalizeIdentifier(String identifier) {
        return SqlUtils.modifiedString(StringUtils.strip(identifier, "\"")).toLowerCase(Locale.ROOT);
    }

    private static final class ColumnReference {
        private final String tableOrAlias;
        private final String columnName;

        private ColumnReference(String tableOrAlias, String columnName) {
            this.tableOrAlias = tableOrAlias;
            this.columnName = columnName;
        }
    }

    private static final class QueryReferences {
        private final Set<String> queryTables = new HashSet<>();
        private final Map<String, String> aliasToTable = new HashMap<>();
        private final List<ColumnReference> columnReferences = new ArrayList<>();

        private ExpressionVisitorAdapter newExpressionVisitor() {
            return new ExpressionVisitorAdapter() {
                @Override
                public void visit(Column column) {
                    String columnName = normalizeIdentifier(column.getColumnName());
                    if (columnName.contains(".")) {
                        String[] parts = columnName.split("\\.");
                        columnName = parts[parts.length - 1];
                    }
                    String tableOrAlias = null;
                    Table table = column.getTable();
                    if (table != null && StringUtils.isNotBlank(table.getName())) {
                        tableOrAlias = normalizeIdentifier(table.getName());
                    }
                    columnReferences.add(new ColumnReference(tableOrAlias, columnName));
                }

                /**
                 * JSQLParser 4.0 ExpressionVisitorAdapter NPEs when ORDER BY inside OVER() is absent
                 * (getOrderByElements() returns null). Also visit PARTITION BY expressions, which the
                 * stock visitor skips.
                 */
                @Override
                public void visit(AnalyticExpression expr) {
                    if (expr.getExpression() != null) {
                        expr.getExpression().accept(this);
                    }
                    if (expr.getDefaultValue() != null) {
                        expr.getDefaultValue().accept(this);
                    }
                    if (expr.getOffset() != null) {
                        expr.getOffset().accept(this);
                    }
                    if (expr.getKeep() != null) {
                        expr.getKeep().accept(this);
                    }
                    if (expr.getFilterExpression() != null) {
                        expr.getFilterExpression().accept(this);
                    }
                    ExpressionList partitionExpressions = expr.getPartitionExpressionList();
                    if (partitionExpressions != null) {
                        partitionExpressions.accept(this);
                    }
                    List<OrderByElement> orderByElements = expr.getOrderByElements();
                    if (orderByElements != null) {
                        for (OrderByElement element : orderByElements) {
                            if (element != null && element.getExpression() != null) {
                                element.getExpression().accept(this);
                            }
                        }
                    }
                }
            };
        }
    }
}
