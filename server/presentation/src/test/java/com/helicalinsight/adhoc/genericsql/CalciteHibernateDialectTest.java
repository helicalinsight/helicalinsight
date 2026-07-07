package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CalciteHibernateDialectTest {

	 @Test
	    public void testOpenQuote() {
	        CalciteHibernateDialect dialect = new CalciteHibernateDialect();
	        assertEquals('`', dialect.openQuote());
	    }

	    @Test
	    public void testCloseQuote() {
	        CalciteHibernateDialect dialect = new CalciteHibernateDialect();
	        assertEquals('`', dialect.closeQuote());
	    }

	    @Test
	    public void testSupportsLimit() {
	        CalciteHibernateDialect dialect = new CalciteHibernateDialect();
	        assertTrue(dialect.supportsLimit());
	    }

	    @Test
	    public void testGetLimitString() {
	        CalciteHibernateDialect dialect = new CalciteHibernateDialect();
	        String query = "SELECT * FROM table";
	        int offset = 5;
	        int limit = 10;
	        String expectedLimitString = "SELECT * FROM table limit 10";
	        assertEquals(expectedLimitString, dialect.getLimitString(query, offset, limit));
	    }
}
