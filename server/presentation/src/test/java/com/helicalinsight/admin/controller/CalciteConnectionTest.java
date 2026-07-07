package com.helicalinsight.admin.controller;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.datasource.calcite.CalciteConnection;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import com.helicalinsight.resourcesecurity.jaxb.Security.Share;

public class CalciteConnectionTest {

	@Test
	public void testsetId() {
		CalciteConnection calciteConnection = new CalciteConnection();
		calciteConnection.setId(0);
		Assert.assertEquals(0, calciteConnection.getId());
	}
	@Test
	public void testsetType() {
		CalciteConnection calciteConnection = new CalciteConnection();
		calciteConnection.setType("type");
		Assert.assertEquals("type", calciteConnection.getType());
	}
	@Test
	public void testsetName() {
		CalciteConnection calciteConnection = new CalciteConnection();
		calciteConnection.setName("type");
		Assert.assertEquals("type", calciteConnection.getName());
	}
	@Test
	public void testsetBaseType() {
		CalciteConnection calciteConnection = new CalciteConnection();
		calciteConnection.setName("type");
		Assert.assertNotNull(calciteConnection.getBaseType());
	}
	@Test
	public void testsetDriverName() {
		CalciteConnection calciteConnection = new CalciteConnection();
		calciteConnection.setDriverName("type");
		Assert.assertEquals("type", calciteConnection.getDriverName());
	}
	@Test
	public void testsetDataSourceProvider() {
		CalciteConnection calciteConnection = new CalciteConnection();
		calciteConnection.setDataSourceProvider("type");
		Assert.assertEquals("type", calciteConnection.getDataSourceProvider());
	}
	@Test
	public void testsetModel() {
		CalciteConnection calciteConnection = new CalciteConnection();
		calciteConnection.setModel("type");
		Assert.assertEquals("type", calciteConnection.getModel());
	}
	@Test
	public void testsetSecurity() {
		CalciteConnection calciteConnection = new CalciteConnection();
		Security security = new Security();
		calciteConnection.setSecurity(security);
		Assert.assertNotNull(calciteConnection.getSecurity());
		
	}
	@Test
	public void testsetShare() {
		CalciteConnection calciteConnection = new CalciteConnection();
		Security.Share share = new Share();
		calciteConnection.setShare(share);
		Assert.assertNotNull(calciteConnection.getShare());
		
	}
}
