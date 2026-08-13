package com.helicalinsight.adhoc.genericsql;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jsqlparser.expression.Expression;

/**
 * Applies ROLLUP/CUBE rewrites on the original SQL string so adhoc formatting
 * ({@code \ngroup by\n\t}, {@code \norder by\n\t}, etc.) is preserved.
 * JSQLParser {@code toString()} is avoided for the final query because it flattens newlines.
 */
final class OlapSqlRewriteHelper {

    private static final Pattern GROUP_BY_CLAUSE = Pattern.compile(
            "(?is)(?:\\n\\s*)?group\\s+by\\b.+?(?=\\s*(?:\\n\\s*)?(?:having\\b|order\\s+by\\b|limit\\b|offset\\b|$))");
    private static final Pattern FROM_CLAUSE = Pattern.compile("(?is)(?:\\nfrom\\b|\\s+from\\b)");
    private static final Pattern ORDER_BY = Pattern.compile("(?is)\\border\\s+by\\b");
    private static final Pattern LIMIT_OR_OFFSET = Pattern.compile("(?is)(?:\\n\\s*)?(?:limit\\b|offset\\b)");

    private OlapSqlRewriteHelper() {
    }

    /**
     * @param originalSql         query before OLAP rewrite (keeps existing newlines)
     * @param groupByBody         e.g. {@code ROLLUP(a, b)} or {@code a, b} when using WITH modifier
     * @param withModifier         if true, append {@code WITH ROLLUP}/{@code WITH CUBE}
     * @param withKeyword         {@code WITH ROLLUP} or {@code WITH CUBE}
     * @param selectItemsFragment optional fragment starting with comma, e.g. {@code ,\n\tGROUPING(a) AS grouping_a}
     * @param orderByBody         optional order list without the ORDER BY keyword; null/empty skips insert
     */
    static String rewrite(String originalSql, String groupByBody, boolean withModifier, String withKeyword,
                          String selectItemsFragment, String orderByBody) {
        String sql = originalSql;
        sql = replaceGroupBy(sql, groupByBody, withModifier, withKeyword);
        if (selectItemsFragment != null && !selectItemsFragment.isEmpty()) {
            sql = insertBeforeFrom(sql, selectItemsFragment);
        }
        if (orderByBody != null && !orderByBody.isEmpty() && !ORDER_BY.matcher(sql).find()) {
            sql = insertOrderBy(sql, orderByBody);
        }
        return sql;
    }

    static String joinExpressions(List<Expression> expressions) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expressions.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(expressions.get(i).toString());
        }
        return sb.toString();
    }

    static String ansiFunctionCall(String functionName, List<Expression> expressions) {
        return functionName + "(" + joinExpressions(expressions) + ")";
    }

    private static String replaceGroupBy(String sql, String groupByBody, boolean withModifier, String withKeyword) {
        String replacement = "\ngroup by\n\t" + groupByBody;
        if (withModifier && withKeyword != null && !withKeyword.isEmpty()) {
            replacement = replacement + " " + withKeyword;
        }
        Matcher matcher = GROUP_BY_CLAUSE.matcher(sql);
        if (!matcher.find()) {
            throw new IllegalStateException("GROUP BY clause not found for string rewrite.");
        }
        return sql.substring(0, matcher.start()) + replacement + sql.substring(matcher.end());
    }

    private static String insertBeforeFrom(String sql, String fragment) {
        Matcher matcher = FROM_CLAUSE.matcher(sql);
        if (!matcher.find()) {
            return sql;
        }
        return sql.substring(0, matcher.start()) + fragment + sql.substring(matcher.start());
    }

    private static String insertOrderBy(String sql, String orderByBody) {
        String clause = "\norder by\n\t" + orderByBody;
        Matcher matcher = LIMIT_OR_OFFSET.matcher(sql);
        if (matcher.find()) {
            return sql.substring(0, matcher.start()) + clause + "\n" + sql.substring(matcher.start()).replaceFirst("^\\s+", "");
        }
        return sql + clause;
    }
}
