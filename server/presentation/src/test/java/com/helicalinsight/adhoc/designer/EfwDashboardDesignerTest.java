package com.helicalinsight.adhoc.designer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.helicalinsight.resourcesecurity.jaxb.Security;
import com.helicalinsight.resourcesecurity.jaxb.Security.Share;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EfwDashboardDesignerTest {

	@Test
	public void ut_a1_test_setName() {
		EfwDashboardDesigner designer = new EfwDashboardDesigner();
		designer.setName("helical"); 
		String name = designer.getName();
		assertEquals("helical", name);
	}
	
	@Test
	public void ut_a2_test_setVisible() {
		EfwDashboardDesigner designer = new EfwDashboardDesigner();
		designer.setVisible("helical"); 
		String name = designer.getVisible();
		assertEquals("helical", name);
	}
	
	@Test
	public void ut_a3_test_setDescription() {
		EfwDashboardDesigner designer = new EfwDashboardDesigner();
		designer.setDescription("helical"); 
		String name = designer.getDescription();
		assertEquals("helical", name);
	}
	
	@Test
	public void ut_a4_test_setState() {
		EfwDashboardDesigner designer = new EfwDashboardDesigner();
		designer.setState("helical"); 
		String name = designer.getState();
		assertEquals("helical", name);
	}
	
	
	@Test
	public void ut_a5_test_setSecurity() {
		EfwDashboardDesigner designer = new EfwDashboardDesigner();
		Security security = mock(Security.class);
		designer.setSecurity(security); 
		Security name = designer.getSecurity();
		assertEquals(security, name);
	}
	
	@Test
	public void ut_a6_test_setShare() {
		EfwDashboardDesigner designer = new EfwDashboardDesigner();
		Security.Share share= mock(Security.Share.class);
		designer.setShare(share); 
		Share name = designer.getShare();
		assertEquals(share, name);
	}
	
	@Test
	public void ut_a7_test_setEfw() {
		EfwDashboardDesigner designer = new EfwDashboardDesigner();
		designer.setEfw("efw"); 
		String name = designer.getEfw();
		assertEquals("efw", name);
	}
	
	@Test
	public void ut_a8_test_toString() {
		EfwDashboardDesigner designer = new EfwDashboardDesigner();
		designer.toString(); 
		
	}
}
