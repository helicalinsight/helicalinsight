package com.helicalinsight.datasource.managed.jaxb;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.resourcesecurity.jaxb.Security;



public class TomcatPoolPropertiesTest {

	@Test
	public void testsetDatabaseName() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setDatabaseName("temp");
		Assert.assertEquals("temp", properties.getDatabaseName());
	}
	@Test
	public void testsetId() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setId(1);
		Assert.assertEquals(1, properties.getId());
	}
	@Test
	public void testsetVisible() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setVisible("temp");
		Assert.assertEquals("temp", properties.getVisible());
	}
	@Test
	public void testsetName() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setName("temp");
		Assert.assertEquals("temp", properties.getName());
	}
	@Test
	public void testsetType() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setType("temp");
		Assert.assertEquals("temp", properties.getType());
	}
	@Test
	public void testsetBaseType() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setBaseType("temp");
		Assert.assertEquals("temp", properties.getBaseType());
	}
	@Test
	public void testsetDataSourcePoolId() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setDataSourcePoolId("temp");
		Assert.assertEquals("temp", properties.getDataSourcePoolId());
	}
	@Test
	public void testsetDataSourceProvider() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setDataSourceProvider("temp");
		Assert.assertEquals("temp", properties.getDataSourceProvider());
	}
	@Test
	public void testsetForceAlternateUsername() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setForceAlternateUsername(true);
		Assert.assertEquals(true, properties.isForceAlternateUsername());
	}
	@Test
	public void testsetUsername() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setUsername("temp");
		Assert.assertEquals("temp", properties.getUsername());
	}
	@Test
	public void testsetDatabaseDialect() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setDatabaseDialect("temp");
		Assert.assertEquals("temp", properties.getDatabaseDialect());
	}
	@Test
	public void testsetPassword() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setPassword("temp");
		Assert.assertEquals("temp", properties.getPassword());
	}
	@Test
	public void testsetDriverClassName() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setDriverClassName("temp");
		Assert.assertEquals("temp", properties.getDriverClassName());
	}
	
	@Test
	public void testsetUrl() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setUrl("temp");
		Assert.assertEquals("temp", properties.getUrl());
	}
	
	@Test
	public void testsetValidationQuery() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setValidationQuery("temp");
		Assert.assertEquals("temp", properties.getValidationQuery());
	}
	@Test
	public void testsetMaxActive() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		properties.setMaxActive(122);
		Assert.assertEquals(122, properties.getMaxActive());
	}
	@Test
	public void testsetSecurity() {
		TomcatPoolProperties properties = new TomcatPoolProperties();
		Security security = new Security();
		properties.setSecurity(security);
		Assert.assertNotNull(properties.getSecurity());
	}
	@Test
	public void test() {
		
		TomcatPoolProperties properties = new TomcatPoolProperties(1,"12", "temp", "temp", "temp", false, "temp", "temp", "temp", "temp", false, false, false, "temp", 1, 2, 03, 04, 5, 6, 7, false, false, 8, false, "temp", "temp", "temp");
		properties.toString();
		properties.equals(properties);
		properties.hashCode();
	}
}
