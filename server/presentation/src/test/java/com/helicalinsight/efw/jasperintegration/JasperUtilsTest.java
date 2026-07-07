package com.helicalinsight.efw.jasperintegration;

import org.junit.Test;

public class JasperUtilsTest {

	@Test
	public void testGetEquivavlentClass() {
		JasperUtils.getEquivalentClass("text");
		JasperUtils.getEquivalentClass("numeric");
		JasperUtils.getEquivalentClass("boolean");
		JasperUtils.getEquivalentClass("date");
		JasperUtils.getEquivalentClass("dateTime");
	}
	
}
