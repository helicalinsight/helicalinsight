package com.helicalinsight.adhoc.genericsql;

import net.sf.jsqlparser.JSQLParserException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ScopedSecurityFilterApplierTest {

    private static final String CUSTOMER_FILTER = "\"dim_customer\".\"tenant_id\" = 1";

    /** Filter-values query on a metadata view whose SQL reads the secured table dim_customer. */
    private static final String VIEW_QUERY = "SELECT DISTINCT \"v_customer_orders\".\"segment\" FROM ("
            + "SELECT o.tenant_id AS tenant_id, o.order_id, dim_customer.segment "
            + "FROM \"sales_catalog\".\"sales\".\"fact_orders\" o "
            + "JOIN \"sales_catalog\".\"sales\".\"dim_customer\" dim_customer "
            + "ON dim_customer.customer_id = o.customer_id AND dim_customer.tenant_id = o.tenant_id "
            + "WHERE COALESCE(o.deleted, false) = false"
            + ") \"v_customer_orders\" WHERE (\"v_customer_orders\".\"tenant_id\" = 1)";

    private static Map<String, List<String>> customerFilters() {
        Map<String, List<String>> filters = new HashMap<>();
        filters.put("dim_customer", Collections.singletonList(CUSTOMER_FILTER));
        return filters;
    }

    @Test
    public void tableInsideInlinedViewIsNotAnOuterReference() throws JSQLParserException {
        Set<String> outer = ScopedSecurityFilterApplier.findOuterReferences(
                VIEW_QUERY, Arrays.asList("v_customer_orders", "dim_customer"));
        assertEquals(Collections.singleton("v_customer_orders"), outer);
    }

    @Test
    public void filterOfTableInsideViewIsPushedIntoTheView() throws JSQLParserException {
        String result = ScopedSecurityFilterApplier.pushDownNestedFilters(VIEW_QUERY, customerFilters());

        String inner = result.substring(result.indexOf('(', result.indexOf("FROM")),
                result.lastIndexOf(") \"v_customer_orders\""));
        assertTrue(result, inner.contains("(dim_customer.\"tenant_id\" = 1)"));
        String outer = result.substring(result.lastIndexOf(") \"v_customer_orders\""));
        assertFalse(result, outer.contains("dim_customer"));
        assertTrue(result, outer.contains("\"v_customer_orders\".\"tenant_id\" = 1"));
    }

    @Test
    public void filterIsRewrittenToTheAliasUsedInTheView() throws JSQLParserException {
        String query = VIEW_QUERY.replace("\"dim_customer\" dim_customer", "\"dim_customer\" c")
                .replace("dim_customer.", "c.");
        String result = ScopedSecurityFilterApplier.pushDownNestedFilters(query, customerFilters());
        assertTrue(result, result.contains("(c.\"tenant_id\" = 1)"));
        assertFalse(result, result.contains("\"dim_customer\".\"tenant_id\""));
    }

    @Test
    public void tableNameIsMatchedIgnoringCase() throws JSQLParserException {
        String query = VIEW_QUERY.replace("\"dim_customer\" dim_customer", "\"DIM_CUSTOMER\" c")
                .replace("dim_customer.", "c.");
        String result = ScopedSecurityFilterApplier.pushDownNestedFilters(query, customerFilters());
        assertTrue(result, result.contains("(c.\"tenant_id\" = 1)"));
    }

    @Test
    public void tableWithoutAliasKeepsItsWrittenName() throws JSQLParserException {
        String query = "SELECT x.a FROM (SELECT \"dim_customer\".a FROM \"sales\".\"dim_customer\") x";
        String result = ScopedSecurityFilterApplier.pushDownNestedFilters(query, customerFilters());
        assertTrue(result, result.contains("WHERE (\"dim_customer\".\"tenant_id\" = 1)) x"));
    }

    @Test
    public void filterIsAndedWithExistingWhereOfTheSubQuery() throws JSQLParserException {
        String query = "SELECT x.a FROM (SELECT t.a FROM dim_customer t WHERE t.deleted = false) x";
        String result = ScopedSecurityFilterApplier.pushDownNestedFilters(query, customerFilters());
        assertTrue(result, result.contains("WHERE t.deleted = false AND (t.\"tenant_id\" = 1)"));
    }

    @Test
    public void filterAlreadyPresentInSubQueryIsNotDuplicated() throws JSQLParserException {
        String query = "SELECT x.a FROM (SELECT t.a FROM dim_customer t WHERE (t.\"tenant_id\" = 1)) x";
        assertSame(query, ScopedSecurityFilterApplier.pushDownNestedFilters(query, customerFilters()));
    }

    @Test
    public void tablesInCteAndExpressionSubQueriesGetTheFilter() throws JSQLParserException {
        String query = "WITH c AS (SELECT t.customer_id FROM dim_customer t) "
                + "SELECT f.a FROM fact_orders f WHERE f.customer_id IN (SELECT d.customer_id FROM dim_customer d) "
                + "AND f.customer_id IN (SELECT c.customer_id FROM c)";
        String result = ScopedSecurityFilterApplier.pushDownNestedFilters(query, customerFilters());
        assertTrue(result, result.contains("FROM dim_customer t WHERE (t.\"tenant_id\" = 1)"));
        assertTrue(result, result.contains("FROM dim_customer d WHERE (d.\"tenant_id\" = 1)"));
    }

    @Test
    public void outerTableIsLeftToTheCaller() throws JSQLParserException {
        String query = "SELECT t.a FROM \"dim_customer\" t";
        assertSame(query, ScopedSecurityFilterApplier.pushDownNestedFilters(query, customerFilters()));
        assertEquals(Collections.singleton("dim_customer"),
                ScopedSecurityFilterApplier.findOuterReferences(query, Collections.singletonList("dim_customer")));
    }

    @Test
    public void everyUnionBranchIsAnOuterScope() throws JSQLParserException {
        String query = "SELECT a FROM dim_customer UNION ALL SELECT a FROM \"V_Other\"";
        assertEquals(2, ScopedSecurityFilterApplier.findOuterReferences(
                query, Arrays.asList("dim_customer", "v_other")).size());
    }

    @Test
    public void queryWithoutSecuredNestedTablesIsReturnedUnchanged() throws JSQLParserException {
        String query = "SELECT x.a FROM (SELECT f.a FROM fact_orders f) x";
        assertSame(query, ScopedSecurityFilterApplier.pushDownNestedFilters(query, customerFilters()));
    }

    @Test(expected = JSQLParserException.class)
    public void unparsableQueryThrows() throws JSQLParserException {
        ScopedSecurityFilterApplier.findOuterReferences("SELECT FROM WHERE (", Collections.singletonList("t"));
    }

    @Test(expected = JSQLParserException.class)
    public void unparsableFilterThrows() throws JSQLParserException {
        Map<String, List<String>> filters = new HashMap<>();
        filters.put("dim_customer", Collections.singletonList("tenant_id = = ("));
        ScopedSecurityFilterApplier.pushDownNestedFilters("SELECT x.a FROM (SELECT a FROM dim_customer) x", filters);
    }
}
