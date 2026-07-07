package com.helicalinsight.cache;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.cache.model.ConnectionColumn;
import com.helicalinsight.cache.model.ConnectionSchema;
import com.helicalinsight.cache.model.ConnectionTable;

public class ConnectionTableTest {

	@Test
	public void testsetConnectionColumn() {
		ConnectionTable connectionTable=new ConnectionTable();
		List<ConnectionColumn> connectionColumn = new ArrayList<>();
		connectionTable.setConnectionColumn(connectionColumn);
		Assert.assertTrue(connectionTable.getConnectionColumn().isEmpty());
	}
	@Test
	public void testsetConnectionSchema() {
		ConnectionTable connectionTable=new ConnectionTable();
		ConnectionSchema connectionSchema = new ConnectionSchema();
		connectionTable.setConnectionSchema(connectionSchema);
		Assert.assertNotNull(connectionTable.getConnectionSchema());
	}
	@Test
	public void testsetTableId() {
		ConnectionTable connectionTable=new ConnectionTable();
		connectionTable.setTableId(11l);
		Assert.assertNotNull(connectionTable.getTableId());
	}
	@Test
	public void testsetTables() {
		ConnectionTable connectionTable=new ConnectionTable();
		connectionTable.setTables("temp");
		Assert.assertNotNull(connectionTable.getTables());
	}
}
