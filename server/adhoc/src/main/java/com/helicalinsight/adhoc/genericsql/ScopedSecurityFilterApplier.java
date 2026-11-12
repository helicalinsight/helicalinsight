package com.helicalinsight.adhoc.genericsql;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.ParenthesisFromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.WithItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Applies metadata row-level security filters in the query scope where each secured table is
 * actually referenced.
 * <p>
 * Security filters are written against a table name, for example {@code "dim_customer"."tenant_id" = 1}.
 * Appending such a filter to the outermost WHERE clause is only valid when the table is visible at
 * the outermost level. When the table is used inside a sub-query (typically the SQL of a metadata view
 * inlined as a derived table) the condition must be placed inside that sub-query, otherwise the
 * database rejects the statement because the table cannot be resolved from the outer scope.
 * <p>
 * This class works on the parsed statement:
 * <ul>
 *     <li>{@link #findOuterReferences(String, Collection)} reports the secured names (tables, or the
 *     aliases of derived tables) visible at the outermost level, so that the caller can keep applying
 *     their filters to the outer WHERE clause.</li>
 *     <li>{@link #pushDownNestedFilters(String, Map)} adds the filters of tables referenced inside
 *     sub-queries, CTEs and expression sub-queries to the WHERE clause of the enclosing SELECT,
 *     rewriting the table qualifier to the alias used in that scope.</li>
 * </ul>
 * Table names are compared ignoring identifier quotes and case, so that a different spelling of the
 * same table (for example {@code "DIM_CUSTOMER"}) is still matched.
 * <p>
 * Both methods throw {@link JSQLParserException} when the query or a filter cannot be parsed; callers
 * are expected to fall back to their legacy behaviour in that case.
 */
public final class ScopedSecurityFilterApplier {

    private ScopedSecurityFilterApplier() {
    }

    /**
     * Returns the normalized names from {@code securedNames} referenced in the outermost scope of the query
     * (each branch of a top-level UNION counts as outermost).
     *
     * @param sql          query to analyse
     * @param securedNames names to look for, compared using {@link #normalize(String)}
     * @return the matching normalized names, in order of appearance
     * @throws JSQLParserException when the query cannot be parsed or is not a SELECT
     */
    public static Set<String> findOuterReferences(String sql, Collection<String> securedNames)
            throws JSQLParserException {
        Set<String> wanted = new LinkedHashSet<>();
        for (String name : securedNames) {
            wanted.add(normalize(name));
        }
        Set<String> found = new LinkedHashSet<>();
        collectOuterReferences(parseSelect(sql).getSelectBody(), wanted, found);
        return found;
    }

    /**
     * Adds the filters of secured tables referenced inside nested scopes to those scopes.
     *
     * @param sql              query to rewrite
     * @param filtersByTable   filters per secured table, keyed by normalized table name
     *                         (see {@link #normalize(String)})
     * @return the rewritten query, or {@code sql} unchanged when nothing had to be added
     * @throws JSQLParserException when the query or one of the filters cannot be parsed
     */
    public static String pushDownNestedFilters(String sql, Map<String, List<String>> filtersByTable)
            throws JSQLParserException {
        if (filtersByTable == null || filtersByTable.isEmpty()) {
            return sql;
        }
        Select select = parseSelect(sql);
        Rewriter rewriter = new Rewriter(filtersByTable);
        rewriter.walkSelect(select, 0);
        if (rewriter.failure != null) {
            throw rewriter.failure;
        }
        return rewriter.changed ? select.toString() : sql;
    }

    /**
     * Strips identifier quotes ({@code "}, back-tick, square brackets) and lower-cases the identifier.
     */
    public static String normalize(String identifier) {
        if (identifier == null) {
            return "";
        }
        return identifier.replaceAll("[\"`\\[\\]]", "").trim().toLowerCase(Locale.ROOT);
    }

    private static Select parseSelect(String sql) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        if (!(statement instanceof Select)) {
            throw new JSQLParserException("Only SELECT statements are supported");
        }
        return (Select) statement;
    }

    private static void collectOuterReferences(SelectBody body, Set<String> wanted, Set<String> found) {
        if (body instanceof SetOperationList) {
            for (SelectBody branch : ((SetOperationList) body).getSelects()) {
                collectOuterReferences(branch, wanted, found);
            }
            return;
        }
        if (!(body instanceof PlainSelect)) {
            return;
        }
        PlainSelect plainSelect = (PlainSelect) body;
        for (FromItem fromItem : fromItems(plainSelect)) {
            String name = referenceName(fromItem);
            if (name != null && wanted.contains(name)) {
                found.add(name);
            }
        }
    }

    /**
     * The name by which a FROM item can be matched to a metadata table: the table name for a table, the
     * alias for a derived table (an inlined metadata view is aliased with the view name).
     */
    private static String referenceName(FromItem fromItem) {
        if (fromItem instanceof Table) {
            return normalize(((Table) fromItem).getName());
        }
        if (fromItem instanceof SubSelect && fromItem.getAlias() != null) {
            return normalize(fromItem.getAlias().getName());
        }
        return null;
    }

    private static List<FromItem> fromItems(PlainSelect plainSelect) {
        List<FromItem> items = new ArrayList<>();
        if (plainSelect.getFromItem() != null) {
            items.add(plainSelect.getFromItem());
        }
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                if (join.getRightItem() != null) {
                    items.add(join.getRightItem());
                }
            }
        }
        return items;
    }

    private static final class Rewriter {
        private final Map<String, List<String>> filtersByTable;
        private boolean changed;
        private JSQLParserException failure;

        Rewriter(Map<String, List<String>> filtersByTable) {
            this.filtersByTable = filtersByTable;
        }

        void walkSelect(Select select, int depth) {
            walkWithItems(select.getWithItemsList(), depth);
            walkBody(select.getSelectBody(), depth);
        }

        void walkWithItems(List<WithItem> withItems, int depth) {
            if (withItems == null) {
                return;
            }
            for (WithItem withItem : withItems) {
                // A CTE is its own scope, never the outermost one.
                walkBody(withItem.getSelectBody(), depth + 1);
            }
        }

        void walkSubSelect(SubSelect subSelect, int depth) {
            walkWithItems(subSelect.getWithItemsList(), depth);
            walkBody(subSelect.getSelectBody(), depth);
        }

        void walkBody(SelectBody body, int depth) {
            if (body instanceof PlainSelect) {
                walkPlainSelect((PlainSelect) body, depth);
            } else if (body instanceof SetOperationList) {
                for (SelectBody branch : ((SetOperationList) body).getSelects()) {
                    walkBody(branch, depth);
                }
            } else if (body instanceof WithItem) {
                walkBody(((WithItem) body).getSelectBody(), depth);
            }
        }

        void walkPlainSelect(PlainSelect plainSelect, int depth) {
            List<FromItem> fromItems = fromItems(plainSelect);

            // Recurse first so nested scopes are handled independently of this one.
            for (FromItem fromItem : fromItems) {
                walkFromItem(fromItem, depth);
            }
            walkExpressionSubQueries(plainSelect, depth);

            // The outermost scope is handled by the caller (outer WHERE clause).
            if (depth == 0) {
                return;
            }
            for (FromItem fromItem : fromItems) {
                String name = referenceName(fromItem);
                if (name == null) {
                    continue;
                }
                List<String> filters = filtersByTable.get(name);
                if (filters == null || filters.isEmpty()) {
                    continue;
                }
                addFilters(plainSelect, fromItem, name, filters);
            }
        }

        void walkFromItem(FromItem fromItem, int depth) {
            if (fromItem instanceof SubSelect) {
                walkSubSelect((SubSelect) fromItem, depth + 1);
            } else if (fromItem instanceof LateralSubSelect) {
                SubSelect subSelect = ((LateralSubSelect) fromItem).getSubSelect();
                if (subSelect != null) {
                    walkSubSelect(subSelect, depth + 1);
                }
            } else if (fromItem instanceof ParenthesisFromItem) {
                walkFromItem(((ParenthesisFromItem) fromItem).getFromItem(), depth);
            }
        }

        void walkExpressionSubQueries(PlainSelect plainSelect, int depth) {
            ExpressionVisitorAdapter visitor = new ExpressionVisitorAdapter() {
                @Override
                public void visit(SubSelect subSelect) {
                    walkSubSelect(subSelect, depth + 1);
                }
            };
            if (plainSelect.getSelectItems() != null) {
                for (SelectItem item : plainSelect.getSelectItems()) {
                    if (item instanceof SelectExpressionItem) {
                        ((SelectExpressionItem) item).getExpression().accept(visitor);
                    }
                }
            }
            if (plainSelect.getJoins() != null) {
                for (Join join : plainSelect.getJoins()) {
                    if (join.getOnExpression() != null) {
                        join.getOnExpression().accept(visitor);
                    }
                }
            }
            if (plainSelect.getWhere() != null) {
                plainSelect.getWhere().accept(visitor);
            }
            if (plainSelect.getHaving() != null) {
                plainSelect.getHaving().accept(visitor);
            }
        }

        void addFilters(PlainSelect plainSelect, FromItem fromItem, String securedName, List<String> filters) {
            String qualifier = qualifierFor(fromItem);
            for (String filter : filters) {
                Expression condition;
                try {
                    // No partial parse: silently dropping part of a security filter would weaken it.
                    condition = CCJSqlParserUtil.parseCondExpression(filter, false);
                } catch (JSQLParserException exception) {
                    failure = exception;
                    return;
                }
                requalify(condition, securedName, qualifier);
                Expression where = plainSelect.getWhere();
                String conditionText = condition.toString();
                if (where != null && where.toString().contains(conditionText)) {
                    continue;
                }
                Expression wrapped = new Parenthesis(condition);
                plainSelect.setWhere(where == null ? wrapped : new AndExpression(where, wrapped));
                changed = true;
            }
        }

        /** The name the secured table is reachable by inside its own scope. */
        private String qualifierFor(FromItem fromItem) {
            if (fromItem.getAlias() != null && fromItem.getAlias().getName() != null) {
                return fromItem.getAlias().getName();
            }
            return ((Table) fromItem).getName();
        }

        /** Rewrites {@code securedName.column} references in the filter to {@code qualifier.column}. */
        private void requalify(Expression condition, String securedName, String qualifier) {
            condition.accept(new ExpressionVisitorAdapter() {
                @Override
                public void visit(Column column) {
                    Table table = column.getTable();
                    if (table != null && table.getName() != null
                            && securedName.equals(normalize(table.getName()))) {
                        column.setTable(new Table(qualifier));
                    }
                }
            });
        }
    }
}
