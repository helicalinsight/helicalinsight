package com.helicalinsight.cache;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.cache.model.ConnectionDetails;

public class ConnectionDetailsTest {

	@Test
	public void testsetConnectionId() {
		ConnectionDetails connectionDetails = new ConnectionDetails();
		Long id = 11l;
		connectionDetails.setConnectionId(id);
		Assert.assertNotNull(connectionDetails.getConnectionId());
	}
	@Test
	public void testsetDatasourceId() {
		ConnectionDetails connectionDetails = new ConnectionDetails();
		Long id = 11l;
		connectionDetails.setDatasourceId(id);
		Assert.assertNotNull(connectionDetails.getDatasourceId());
	}
	@Test
	public void testsetConnectionCatalog() {
		ConnectionDetails connectionDetails = new ConnectionDetails();
		connectionDetails.setConnectionCatalog(null);
		Assert.assertNull(connectionDetails.getConnectionCatalog());
	}
	@Test
	public void testsetDir() {
		ConnectionDetails connectionDetails = new ConnectionDetails();
		connectionDetails.setDir(31l);
		Assert.assertNotNull(connectionDetails.getDir());
	}
	@Test
	public void testsetFile() {
		ConnectionDetails connectionDetails = new ConnectionDetails();
		connectionDetails.setFile("temp");
		Assert.assertNotNull(connectionDetails.getFile());
	}
	@Test
	public void testsetConnectionType() {
		ConnectionDetails connectionDetails = new ConnectionDetails();
		connectionDetails.setConnectionType("temp");
		Assert.assertNotNull(connectionDetails.getConnectionType());
	}
}
