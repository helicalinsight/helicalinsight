package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TeradataHIDialectTest {

	@Test
	public void ut_a1_test_getLimitString() {
		TeradataHIDialect dialect = new TeradataHIDialect();
		String limitString = dialect.getLimitString("query", 12, 10);
		assertEquals(limitString, "query");
	}
}
