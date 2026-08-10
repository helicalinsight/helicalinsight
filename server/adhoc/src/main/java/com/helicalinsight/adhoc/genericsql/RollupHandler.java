package com.helicalinsight.adhoc.genericsql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * Adds ROLLUP to the GROUP BY clause and applies ORDER BY after limit/offset handling.
 * Uses JSQLParser to rewrite the SQL AST. Triggered when formData analytics requests subTotals.
 */
final class RollupHandler {
    private static final Logger logger = LoggerFactory.getLogger(RollupHandler.class);

    private String query;
    private final SqlQueryContext context;

    public RollupHandler(String query, SqlQueryContext context) {
        this.context = context;
        this.query = query;
    }

    /**
     * Rewrites GROUP BY to ROLLUP / WITH ROLLUP and ensures ORDER BY via JSQLParser.
     *
     * @return The SQL query with rollup and order by applied.
     */
    public String applyRollup() {
        if (this.query == null || this.query.isEmpty()) {
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

            if (usesAnsiRollupSyntax()) {
                applyAnsiRollup(groupBy, groupByExpressions);
            } else {
                applyNonAnsiRollup(plainSelect, groupByExpressions);
            }

            if (plainSelect.getOrderByElements() == null || plainSelect.getOrderByElements().isEmpty()) {
                plainSelect.setOrderByElements(buildOrderByElements(groupByExpressions));
            }

            this.query = select.toString();
        } catch (Exception ex) {
            logger.error("Failed to apply rollup via JSQLParser; leaving query unchanged.", ex);
        }
        return this.query;
    }

    private void applyAnsiRollup(GroupByElement groupBy, List<Expression> groupByExpressions) {
        Function rollup = new Function();
        rollup.setName("ROLLUP");
        ExpressionList parameters = new ExpressionList(groupByExpressions);
        rollup.setParameters(parameters);
        groupBy.setGroupByExpressions(Collections.singletonList(rollup));
    }

    /**
     * JSQLParser 4.0 has no mysqlWithRollup flag; wrap GroupByElement.toString() to append WITH ROLLUP.
     */
    private void applyNonAnsiRollup(PlainSelect plainSelect, List<Expression> groupByExpressions) {
        GroupByElement withRollup = new GroupByElement() {
            @Override
            public String toString() {
                return super.toString() + " WITH ROLLUP";
            }
        };
        withRollup.setGroupByExpressions(groupByExpressions);
        plainSelect.setGroupByElement(withRollup);
    }

    private List<OrderByElement> buildOrderByElements(List<Expression> groupByExpressions) {
        List<OrderByElement> orderByElements = new ArrayList<>();
        if (this.context.isApplyOrderBy()) {
            String ordered = new OrderByClause(this.context, "").order().trim();
            String clause = ordered.replaceFirst("(?i)^order\\s+by\\s+", "").trim();
            for (String part : clause.split(",")) {
                String token = part.trim();
                if (token.isEmpty()) {
                    continue;
                }
                boolean desc = token.toLowerCase().endsWith(" desc");
                boolean asc = token.toLowerCase().endsWith(" asc");
                String expressionText = token;
                if (desc) {
                    expressionText = token.substring(0, token.length() - 4).trim();
                } else if (asc) {
                    expressionText = token.substring(0, token.length() - 3).trim();
                }
                try {
                    OrderByElement element = new OrderByElement();
                    element.setExpression(CCJSqlParserUtil.parseExpression(expressionText));
                    element.setAsc(!desc);
                    orderByElements.add(element);
                } catch (Exception ignore) {
                    // fall through to group-by based order
                }
            }
            if (!orderByElements.isEmpty()) {
                return orderByElements;
            }
        }

        for (Expression expression : resolveOrderExpressions(groupByExpressions)) {
            OrderByElement element = new OrderByElement();
            element.setExpression(expression);
            element.setAsc(true);
            orderByElements.add(element);
        }
        return orderByElements;
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

    /**
     * ANSI rollup ({@code GROUP BY ROLLUP(...)}) when dialect has rollup="true"
     * in adhocSqlSettings.xml; otherwise non-ANSI {@code WITH ROLLUP}.
     */
    private boolean usesAnsiRollupSyntax() {
        String dialect = null;
        if (this.context.getMetadata() != null && this.context.getMetadata().getConnectionDetails() != null) {
            dialect = this.context.getMetadata().getConnectionDetails().getDialect();
        }
        return AdhocSqlDialectSettings.supportsAnsiRollup(dialect);
    }
}
