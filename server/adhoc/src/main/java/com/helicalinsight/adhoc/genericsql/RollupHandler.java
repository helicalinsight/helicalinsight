package com.helicalinsight.adhoc.genericsql;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * Adds ROLLUP to the GROUP BY clause and ORDER BY after limit/offset handling.
 * Optionally adds GROUPING() columns when analytics requests {@code grouping: true}.
 * Triggered when formData analytics requests subTotals.
 * Rewrites the original SQL string to preserve adhoc newline formatting.
 */
final class RollupHandler {
    private static final Logger logger = LoggerFactory.getLogger(RollupHandler.class);
    private static final String GROUPING_ALIAS_PREFIX = "grouping_";
    private static final Pattern ORDER_BY = Pattern.compile("(?is)\\border\\s+by\\b");

    private String query;
    private final SqlQueryContext context;

    public RollupHandler(String query, SqlQueryContext context) {
        this.context = context;
        this.query = query;
    }

    /**
     * Rewrites GROUP BY to ROLLUP / WITH ROLLUP, optionally adds GROUPING() select items, and ensures ORDER BY.
     *
     * @return The SQL query with rollup, optional grouping, and order by applied.
     */
    public String applyRollup() {
        if (this.query == null || this.query.isEmpty()) {
            return this.query;
        }
        if (!AdhocSqlDialectSettings.isRollupEnabled(this.context.getReferenceFile())) {
            logger.info("Rollup skipped: reference '{}' has rollup disabled in adhocRollupSettings.json.",
                    this.context.getReferenceFile());
            return this.query;
        }
        try {
            Statement statement = CCJSqlParserUtil.parse(this.query);
            if (!(statement instanceof Select)) {
                logger.warn("Rollup requested but statement is not a SELECT; leaving query unchanged.");
                return this.query;
            }
            Select select = (Select) statement;
            PlainSelect plainSelect = findPlainSelectWithGroupBy(select.getSelectBody());
            if (plainSelect == null || plainSelect.getGroupBy() == null) {
                logger.warn("GROUP BY clause not found; rollup was requested but cannot be applied.");
                return this.query;
            }

            GroupByElement groupBy = plainSelect.getGroupBy();
            List<Expression> groupByExpressions = new ArrayList<>(groupBy.getGroupByExpressions());
            if (groupByExpressions.isEmpty()) {
                return this.query;
            }

            boolean applyGrouping = isGroupingRequested();
            boolean ansi = AdhocSqlDialectSettings.usesAnsiRollupSyntax(this.context.getReferenceFile());
            String groupByBody = ansi
                    ? OlapSqlRewriteHelper.ansiFunctionCall("ROLLUP", groupByExpressions)
                    : OlapSqlRewriteHelper.joinExpressions(groupByExpressions);
            String selectFragment = applyGrouping ? buildGroupingSelectFragment(groupByExpressions) : null;
            String orderByBody = ORDER_BY.matcher(this.query).find()
                    ? null
                    : buildOrderByBody(groupByExpressions, applyGrouping);

            this.query = OlapSqlRewriteHelper.rewrite(
                    this.query,
                    groupByBody,
                    !ansi,
                    "WITH ROLLUP",
                    selectFragment,
                    orderByBody);
        } catch (Exception ex) {
            logger.error("Failed to apply rollup; leaving query unchanged.", ex);
        }
        return this.query;
    }

    private boolean isGroupingRequested() {
        JsonObject formData = this.context.getFormData();
        if (formData == null || !formData.has("analytics") || formData.get("analytics").isJsonNull()) {
            return false;
        }
        JsonArray instructions = formData.getAsJsonArray("analytics");
        for (JsonElement instruction : instructions) {
            if (instruction.isJsonObject()
                    && GsonUtility.optBooleanValue(instruction.getAsJsonObject(), "grouping", false)) {
                return true;
            }
        }
        return false;
    }

    private String buildGroupingSelectFragment(List<Expression> groupByExpressions) {
        StringBuilder fragment = new StringBuilder();
        List<String> groupByColumnNames = groupByColumnNames();
        for (int i = 0; i < groupByExpressions.size(); i++) {
            fragment.append(",\n\tGROUPING(")
                    .append(groupByExpressions.get(i).toString())
                    .append(") AS ")
                    .append(groupingAlias(groupByColumnNames, i));
        }
        return fragment.toString();
    }

    private List<String> groupByColumnNames() {
        List<String> names = new ArrayList<>();
        JsonObject formData = this.context.getFormData();
        if (formData == null || !formData.has("functions")) {
            return names;
        }
        JsonObject functions = formData.getAsJsonObject("functions");
        if (!functions.has("groupBy")) {
            return names;
        }
        for (JsonElement object : functions.getAsJsonArray("groupBy")) {
            JsonObject json = object.getAsJsonObject();
            String alias = json.get("column").getAsString();
            alias = AdhocUtils.sanitizeStringIfStartsWithDot(alias);
            names.add(alias);
        }
        return names;
    }

    private String groupingAlias(List<String> groupByColumnNames, int index) {
        String suffix;
        if (index < groupByColumnNames.size()) {
            suffix = groupByColumnNames.get(index).replace('.', '_').replace('"', ' ').replace('`', ' ').trim();
            suffix = suffix.replaceAll("\\s+", "_");
        } else {
            suffix = String.valueOf(index);
        }
        return GROUPING_ALIAS_PREFIX + suffix;
    }

    private String buildOrderByBody(List<Expression> groupByExpressions, boolean applyGrouping) {
        if (this.context.isApplyOrderBy()) {
            String ordered = new OrderByClause(this.context, "").order().trim();
            String clause = ordered.replaceFirst("(?i)^order\\s+by\\s+", "").trim();
            if (!clause.isEmpty()) {
                return clause;
            }
        }

        StringBuilder body = new StringBuilder();
        List<Expression> expressions = resolveOrderExpressions(groupByExpressions);
        for (int i = 0; i < expressions.size(); i++) {
            Expression expression = expressions.get(i);
            if (i > 0) {
                body.append(", ");
            }
            if (applyGrouping) {
                body.append("GROUPING(").append(expression.toString()).append("), ");
            }
            body.append(expression.toString());
        }
        return body.toString();
    }

    private List<Expression> resolveOrderExpressions(List<Expression> groupByExpressions) {
        JsonObject formData = this.context.getFormData();
        if (formData == null || !formData.has("functions")) {
            return groupByExpressions;
        }
        JsonObject functions = formData.getAsJsonObject("functions");
        if (!functions.has("groupBy")) {
            return groupByExpressions;
        }
        JsonArray groupByJson = functions.getAsJsonArray("groupBy");
        List<Expression> orderExpressions = new ArrayList<>();
        List<String> derivedTableColumns = this.context.getDerivedTableColumns();
        for (JsonElement object : groupByJson) {
            JsonObject json = object.getAsJsonObject();
            String alias = json.get("column").getAsString();
            alias = AdhocUtils.sanitizeStringIfStartsWithDot(alias);
            if (derivedTableColumns.contains(alias)) {
                alias = AdhocUtils.stripDatabaseName(alias);
            }
            String quoted;
            if (!this.context.hasParenthesis(alias)) {
                quoted = this.context.doApplyQuotes(alias);
            } else {
                quoted = this.context.quotes(alias);
            }
            try {
                orderExpressions.add(CCJSqlParserUtil.parseExpression(quoted));
            } catch (Exception ignore) {
                // skip unparsable alias
            }
        }
        return orderExpressions.isEmpty() ? groupByExpressions : orderExpressions;
    }

    private PlainSelect findPlainSelectWithGroupBy(SelectBody selectBody) {
        if (selectBody == null) {
            return null;
        }
        if (selectBody instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectBody;
            if (plainSelect.getGroupBy() != null) {
                return plainSelect;
            }
            PlainSelect nested = findInFromItem(plainSelect.getFromItem());
            if (nested != null) {
                return nested;
            }
            List<Join> joins = plainSelect.getJoins();
            if (joins != null) {
                for (Join join : joins) {
                    nested = findInFromItem(join.getRightItem());
                    if (nested != null) {
                        return nested;
                    }
                }
            }
            return null;
        }
        if (selectBody instanceof SetOperationList) {
            SetOperationList setOperationList = (SetOperationList) selectBody;
            for (SelectBody body : setOperationList.getSelects()) {
                PlainSelect nested = findPlainSelectWithGroupBy(body);
                if (nested != null) {
                    return nested;
                }
            }
        }
        return null;
    }

    private PlainSelect findInFromItem(FromItem fromItem) {
        if (fromItem instanceof SubSelect) {
            return findPlainSelectWithGroupBy(((SubSelect) fromItem).getSelectBody());
        }
        return null;
    }
}
