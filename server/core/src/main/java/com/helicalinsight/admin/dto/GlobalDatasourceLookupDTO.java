package com.helicalinsight.admin.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalDatasourceLookupDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String dsType;
	private String jdbcUrl;
	private String name;
	private String userName;
	private String password;
	private String type;
	private String dbName;
	
	
	public GlobalDatasourceLookupDTO() {
		
	}
	
	public GlobalDatasourceLookupDTO(String dsType, String jdbcUrl, String name, String userName, String password,
			String type, String dbName) {
		this.dsType = dsType;
		this.jdbcUrl = jdbcUrl;
		this.name = name;
		this.userName = userName;
		this.password = password;
		this.type = type;
		this.dbName = dbName;
	}
	
	
	public String getDsType() {
		return dsType;
	}
	public void setDsType(String dsType) {
		this.dsType = dsType;
	}
	public String getJdbcUrl() {
		return jdbcUrl;
	}
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
}
