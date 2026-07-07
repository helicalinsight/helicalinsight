package com.helicalinsight.cache;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.cache.model.ConnectionColumn;
import com.helicalinsight.cache.model.ConnectionTable;

public class ConnectionColumnTest {

	@Test
	public void testsetColumnId() {
		ConnectionColumn column = new ConnectionColumn();
		column.setColumnId(23l);
		Assert.assertNotNull(column.getColumnId());
	}
	@Test
	public void testsetConnectionTable() {
		ConnectionColumn column = new ConnectionColumn();
		ConnectionTable connectionTable = new ConnectionTable();
		column.setConnectionTable(connectionTable);
		Assert.assertNotNull(column.getConnectionTable());
	}
	@Test
	public void testsetColumns() {
		ConnectionColumn column = new ConnectionColumn();
		column.setColumns("temp");
		Assert.assertNotNull(column.getColumns());
	}
	@Test
	public void testsetType() {
		ConnectionColumn column = new ConnectionColumn();
		column.setType("temp");
		Assert.assertNotNull(column.getType());
	}
	@Test
	public void testsetDataType() {
		ConnectionColumn column = new ConnectionColumn();
		column.setDataType("temp");
		Assert.assertNotNull(column.getDataType());
	}
	@Test
	public void testsetSize() {
		ConnectionColumn column = new ConnectionColumn();
		column.setSize("temp");
		Assert.assertNotNull(column.getSize());
	}
	@Test
	public void testsetNullable() {
		ConnectionColumn column = new ConnectionColumn();
		column.setNullable("temp");
		Assert.assertNotNull(column.getNullable());
	}
	
	@Test
	public void testsetPosition() {
		ConnectionColumn column = new ConnectionColumn();
		column.setPosition("temp");
		Assert.assertNotNull(column.getPosition());
	}
}
