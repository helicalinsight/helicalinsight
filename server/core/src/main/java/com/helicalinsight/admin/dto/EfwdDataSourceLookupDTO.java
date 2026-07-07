package com.helicalinsight.admin.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class EfwdDataSourceLookupDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String jdbcUrl;
	private String name;
	private String userName;
	private String password;
	private String type;
	private String condition;
	private String directory;

	public EfwdDataSourceLookupDTO() {

	}

	public EfwdDataSourceLookupDTO(String jdbcUrl, String name, String userName, String password, String type, String condition, String directory) {
		this.jdbcUrl = jdbcUrl;
		this.name = name;
		this.userName = userName;
		this.password = password;
		this.type = type;
		this.condition = condition;
		this.directory = directory;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
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
	
	public String getDirectory() {
		return directory;
	}
	
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
}
