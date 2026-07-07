package com.helicalinsight.datasource.managed.jaxb;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.resourcesecurity.jaxb.Security;
import com.helicalinsight.resourcesecurity.jaxb.Security.Share;

public class JdbcConnectionTest {

	@Test
	public void testsetDatabaseName() {
		JdbcConnection con= new JdbcConnection();
		con.setDatabaseName("temp");
		Assert.assertEquals("temp",con.getDatabaseName());
	}
	@Test
	public void testsetDatabaseDialect() {
		JdbcConnection con= new JdbcConnection();
		con.setDatabaseDialect("temp");
		Assert.assertEquals("temp",con.getDatabaseDialect());
	}
	@Test
	public void testsetType() {
		JdbcConnection con= new JdbcConnection();
		con.setType("temp");
		Assert.assertEquals("temp",con.getType());
	}
	
	@Test
	public void testsetId() {
		JdbcConnection con= new JdbcConnection();
		con.setId(1);
		Assert.assertEquals(1,con.getId());
	}
	@Test
	public void testsetName() {
		JdbcConnection con= new JdbcConnection();
		con.setName("temp");
		Assert.assertEquals("temp",con.getName());
	}
	
	@Test
	public void testsetBaseType() {
		JdbcConnection con= new JdbcConnection();
		con.setBaseType("temp");
		Assert.assertEquals("temp",con.getBaseType());
	}
	@Test
	public void testsetVisible() {
		JdbcConnection con= new JdbcConnection();
		con.setVisible("temp");
		Assert.assertEquals("temp",con.getVisible());
	}
	@Test
	public void testsetUserName() {
		JdbcConnection con= new JdbcConnection();
		con.setUserName("temp");
		Assert.assertEquals("temp",con.getUserName());
	}
	
	@Test
	public void testsetPassword() {
		JdbcConnection con= new JdbcConnection();
		con.setPassword("temp");
		Assert.assertEquals("temp",con.getPassword());
	}
	@Test
	public void testsetJdbcUrl() {
		JdbcConnection con= new JdbcConnection();
		con.setJdbcUrl("temp");
		Assert.assertEquals("temp",con.getJdbcUrl());
	}
	@Test
	public void testsetDriverName() {
		JdbcConnection con= new JdbcConnection();
		con.setDriverName("temp");
		Assert.assertEquals("temp",con.getDriverName());
	}
	@Test
	public void testsetDataSourceProvider() {
		JdbcConnection con= new JdbcConnection();
		con.setDataSourceProvider("temp");
		Assert.assertEquals("temp",con.getDataSourceProvider());
	}
	
	@Test
	public void testsetShare() {
		JdbcConnection con= new JdbcConnection();
		Security.Share share = null;
		con.setShare(share);
		Assert.assertEquals(null,con.getShare());
	}
	@Test
	public void testsetSecurity() {
		JdbcConnection con= new JdbcConnection();
		Security share = null;
		con.setSecurity(share);
		Assert.assertEquals(null,con.getSecurity());
	}
	@Test
	public void test() {
		Security security = new Security();
		security.setCreatedBy(new Date().toString());
		Security.Share share = new Share();
		share.setRoles(null);
		JdbcConnection con= new JdbcConnection(1,"temp", "temp", "temp", "temp", "temp", "temp", "temp", "temp", security, share, "temp", "temp");
		con.hashCode();
		con.toString();
		con.equals(con);
		
	}
	
	
	
	
}
