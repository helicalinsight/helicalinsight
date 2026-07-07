package com.helicalinsight.cache;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.cache.model.ConnectionCatalog;
import com.helicalinsight.cache.model.ConnectionDetails;
import com.helicalinsight.cache.model.ConnectionSchema;

public class ConnectionCatalogTest {

	@Test
	public void testsetCatalog() {
		ConnectionCatalog catalog = new ConnectionCatalog();
		catalog.setCatalog("catalog");
		Assert.assertNotNull(catalog.getCatalog());
	}
	@Test
	public void testsetCatalogId() {
		ConnectionCatalog catalog = new ConnectionCatalog();
		catalog.setCatalogId(11l);
		Assert.assertNotNull(catalog.getCatalogId());
	}
	@Test
	public void testsetConnectionDetails() {
		ConnectionCatalog catalog = new ConnectionCatalog();
		ConnectionDetails connectionDetails = new ConnectionDetails();
		catalog.setConnectionDetails(connectionDetails);
		Assert.assertNotNull(catalog.getConnectionDetails());
	}
	@Test
	public void testsetConnectionSchema() {
		ConnectionCatalog catalog = new ConnectionCatalog();
		List<ConnectionSchema> connectionSchema = new ArrayList<>();
		catalog.setConnectionSchema(connectionSchema);
		Assert.assertNotNull(catalog.getConnectionSchema());
	}
}
