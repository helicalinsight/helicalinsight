package com.helicalinsight.adhoc.security;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
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

/**
 * Validates raw SQL queries against the tables and columns exposed by metadata service response.
 */
public final class SqlQueryMetadataValidator {

    private SqlQueryMetadataValidator() {
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
            };
        }
    }
}
