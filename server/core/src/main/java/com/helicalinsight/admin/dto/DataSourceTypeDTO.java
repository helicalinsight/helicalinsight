package com.helicalinsight.admin.dto;

public interface DataSourceTypeDTO {
	
	
	static String TOMCAT = "TomcatJdbcDataSource";
	static String HIKARI = "HikariDataSource";
	static String NOSQL = "NoSQLDataSource";
	static String JNDI = "JndiDataSource";
}
