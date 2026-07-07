package com.helicalinsight.cache;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.cache.model.ConnectionCatalog;
import com.helicalinsight.cache.model.ConnectionSchema;
import com.helicalinsight.cache.model.ConnectionTable;

public class ConnectionSchemaTest {

	@Test
	public void testsetSchema() {
		ConnectionSchema schema = new ConnectionSchema();
		schema.setSchema("temp");
		Assert.assertNotNull(schema.getSchema());
	}
	@Test
	public void testsetSchemaId() {
		ConnectionSchema schema = new ConnectionSchema();
		schema.setSchemaId(33l);
		Assert.assertNotNull(schema.getSchemaId());
	}
	@Test
	public void testsetConnectionCatalog() {
		ConnectionSchema schema = new ConnectionSchema();
		ConnectionCatalog connectionCatalog = new ConnectionCatalog();
		schema.setConnectionCatalog(connectionCatalog);
		Assert.assertNotNull(schema.getConnectionCatalog());
	}
	@Test
	public void testsetConnectionTable() {
		ConnectionSchema schema = new ConnectionSchema();
		List<ConnectionTable> connectionTable = new ArrayList<>();
		schema.setConnectionTable(connectionTable);
		Assert.assertNotNull(schema.getConnectionTable());
	}
}
