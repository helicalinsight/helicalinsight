//package com.helicalinsight.efw.serviceframework;
//
//import org.junit.Test;
//
//import com.helicalinsight.datasource.managed.jaxb.TomcatPoolProperties;
//import com.helicalinsight.efw.components.ExecutorUtils;
//import com.helicalinsight.efw.components.SqlJdbcHandler;
//import com.helicalinsight.efw.components.TomcatJdbcDataSourceProperties;
//
//import net.sf.json.JSONObject;
//
//public class SqlJdbcHandlerTest {
//
//	
//	@Test
//	public void testTomcatJdbcDataSourceProperties () {
//		TomcatJdbcDataSourceProperties dataSourceProperties = new TomcatJdbcDataSourceProperties();
//		TomcatPoolProperties connection = new TomcatPoolProperties();
//		JSONObject formData = new JSONObject();
//		formData.put("name", "user123");
//		formData.put("dataSourceProvider", "dataSource");
//		formData.put("userName", "user123");
//		formData.put("password", "123user");
//		formData.put("jdbcUrl", "jdbc:mysql://myhost1:3306/db_name");
//		formData.put("driverName", "com.mysql.jdbc.Driver");
//		formData.put("poolSize", 12);
//		formData.put("databaseDialect", "org.hibernate.dialect.MySQLDialect");
//		formData.put("database", "db_name");
//		TomcatJdbcDataSourceProperties.writeTomcatJdbcDataSourceType(connection, 10, formData, "edit");
//	}
//	@Test
//	public void testExecutorUtils () {
//		ExecutorUtils executorUtils = new ExecutorUtils();
//		
//	}
//}
