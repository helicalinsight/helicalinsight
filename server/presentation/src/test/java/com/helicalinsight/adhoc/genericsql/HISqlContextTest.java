package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HISqlContextTest {

	@Test
	public void ut_a1_test_HISqlContext() {
		HISqlContext hiSqlContext = new  HISqlContext();
		SqlQueryContext context = hiSqlContext.getContext();
		assertEquals(null, context);
	}
	@Test
	public void ut_a2_test_IllegalColumnNameException() {
	   new IllegalColumnNameException("message");
	} 
	
	@Test
	public void ut_a3_test_MetadataDesignException() {
		new MetadataDesignException("message");
	}
}
