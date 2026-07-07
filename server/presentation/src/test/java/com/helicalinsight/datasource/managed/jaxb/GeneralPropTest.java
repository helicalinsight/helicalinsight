package com.helicalinsight.datasource.managed.jaxb;

import org.junit.Test;

public class GeneralPropTest {

	@Test
	public void testEquals() {
		GeneralProp generalProp = new GeneralProp();
		generalProp.equals(generalProp);
		generalProp.equals(null);
		generalProp.hashCode();
	}
	
	
}
