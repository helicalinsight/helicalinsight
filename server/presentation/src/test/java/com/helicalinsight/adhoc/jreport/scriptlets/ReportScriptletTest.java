package com.helicalinsight.adhoc.jreport.scriptlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ReportScriptletTest {
	
	@Test
	public void ut_a1_test_convertIntToWords() {
		ReportScriptlet reportScriptlet = new ReportScriptlet();
		String convertIntToWords = ReportScriptlet.convertIntToWords(100);
		assertEquals("one hundred",convertIntToWords);
	}

	@Test
	public void ut_a2_test_functionName() {
		 String functionName = ReportScriptlet.functionName();
		 assertNull(functionName);
	}
}
