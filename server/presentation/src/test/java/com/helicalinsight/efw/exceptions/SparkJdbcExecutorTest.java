package com.helicalinsight.efw.exceptions;

import org.junit.Test;

import com.helicalinsight.datasource.managed.GlobalXmlReader;
import com.helicalinsight.datasource.managed.HCRJdbcQueryExecutor;
import com.helicalinsight.datasource.managed.JdbcServiceImpl;
import com.helicalinsight.datasource.managed.JsonUtils;
import com.helicalinsight.datasource.managed.SparkJdbcExecutor;

public class SparkJdbcExecutorTest {

	@Test
	public void testSparkJdbcExecutor() {
		String sql = "select * from people";
		SparkJdbcExecutor executor = new SparkJdbcExecutor(null, sql);
	}
	
	
	@Test
	public void testHCRJdbcQueryExecutor () {
		String sql = "select * from people";
		HCRJdbcQueryExecutor  executor = new HCRJdbcQueryExecutor(null, sql);
	}
	
	@Test
	public void testJdbcServiceImpl() {
		JdbcServiceImpl imp = new JdbcServiceImpl();
		imp.setLimit(11);
	}
	//@Test
	public void testGlobalXmlReader() {
		GlobalXmlReader reader = new GlobalXmlReader ();
		reader.getDataSourceJson(1);
	}
	
}
