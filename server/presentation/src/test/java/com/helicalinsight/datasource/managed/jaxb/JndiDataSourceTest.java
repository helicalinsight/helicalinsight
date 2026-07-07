package com.helicalinsight.datasource.managed.jaxb;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.resourcesecurity.jaxb.Security;

public class JndiDataSourceTest {

	@Test
	public void testsetDatabaseName() {
		JndiDataSource dataSource =new JndiDataSource();
		dataSource.setDatabaseName("temp");
		Assert.assertEquals("temp",dataSource.getDatabaseName());
	}
	@Test
	public void testsetDatabaseDialect() {
		JndiDataSource dataSource =new JndiDataSource();
		dataSource.setDatabaseDialect("temp");
		Assert.assertEquals("temp",dataSource.getDatabaseDialect());
	}
	@Test
	public void testsetId() {
		JndiDataSource dataSource =new JndiDataSource();
		dataSource.setId(1);
		Assert.assertEquals(1,dataSource.getId());
	}
	@Test
	public void testsetName() {
		JndiDataSource dataSource =new JndiDataSource();
		dataSource.setName("temp");
		Assert.assertEquals("temp",dataSource.getName());
	}
	@Test
	public void testsetType() {
		JndiDataSource dataSource =new JndiDataSource();
		dataSource.setType("temp");
		Assert.assertEquals("temp",dataSource.getType());
	}
	@Test
	public void testsetBaseType() {
		JndiDataSource dataSource =new JndiDataSource();
		dataSource.setBaseType("temp");
		Assert.assertEquals("temp",dataSource.getBaseType());
	}
	@Test
	public void testsetVisible() {
		JndiDataSource dataSource =new JndiDataSource();
		dataSource.setVisible("temp");
		Assert.assertEquals("temp",dataSource.getVisible());
	}
	@Test
	public void testsetDataSourceProvider() {
		JndiDataSource dataSource =new JndiDataSource();
		dataSource.setDataSourceProvider("temp");
		Assert.assertEquals("temp",dataSource.getDataSourceProvider());
	}
	@Test
	public void testsetLookUpName() {
		JndiDataSource dataSource =new JndiDataSource();
		dataSource.setLookUpName("temp");
		Assert.assertEquals("temp",dataSource.getLookUpName());
	}
	@Test
	public void testsetDriverClassName() {
		JndiDataSource dataSource =new JndiDataSource();
		dataSource.setDriverClassName("temp");
		Assert.assertEquals("temp",dataSource.getDriverClassName());
	}
	@Test
	public void testsetSecurity() {
		JndiDataSource dataSource =new JndiDataSource();
		Security sec =null;
		dataSource.setSecurity(sec);
		Assert.assertEquals(null,dataSource.getSecurity());
	}
	@Test
	public void testsetShare() {
		JndiDataSource dataSource =new JndiDataSource();
		Security.Share sec =null;
		dataSource.setShare(sec);
		Assert.assertEquals(null,dataSource.getShare());
	}
	
	
	@Test
	public void testss() {
		JndiDataSource dataSource =new JndiDataSource(1,"temp", "temp", "temp", "temp", "temp", "temp");
		dataSource.equals(dataSource);
		dataSource.hashCode();
		dataSource.toString();
	}
	

	
}
