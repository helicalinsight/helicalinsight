package com.helicalinsight.admin.dto;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlainConnDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private int efwdId;
	private String driver;
	private String url;
	private String userName;
	private String pass;
	private String condition;
	private String database;
	private String name;
	private String directory;
	private String type;
	private Boolean isPublic;
	private HIEfwdDTO efwdDto;

	public PlainConnDTO() {
		
	}
	
	public PlainConnDTO(int id, String database, String driver, int efwdId, String name, String pass, String url, String userName) {
		 this.id = id;
		 this.database = database;
		 this.driver = driver;
		 this.efwdId = efwdId;
		 this.name = name;
		 this.pass = pass;
		 this.url = url;
		 this.userName = userName;
	}
	
	public PlainConnDTO(int id, String database, String driver, int efwdId, String name, String pass, String url, String userName, HIEfwdDTO efwdDto) {
		 this.id = id;
		 this.database = database;
		 this.driver = driver;
		 this.efwdId = efwdId;
		 this.name = name;
		 this.pass = pass;
		 this.url = url;
		 this.userName = userName;
		 this.efwdDto = efwdDto;
	}
	
	
	
	public PlainConnDTO(int id, String database, String driver, int efwdId,
            String name, String pass, String url,
            String userName, HIEfwdDTO efwdDto,  String condition) {
		
		this(id, database, driver, efwdId, name, pass, url, userName, efwdDto); // reuse
		this.condition = condition;
	}
	
	
	
	
	
	
	

	public int getId() {
		return id;
	}

	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public String getUserName() {
		return userName;
	}

	public String getPass() {
		return pass;
	}

	public String getCondition() {
		return condition;
	}

	public String getDatabase() {
		return database;
	}

	public String getName() {
		return name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getEfwdId() {
		return efwdId;
	}

	public String getDirectory() {
		return directory;
	}

	public String getType() {
		return type;
	}

	public void setEfwdId(int efwdId) {
		this.efwdId = efwdId;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	public HIEfwdDTO getEfwd() {
		return efwdDto;
	}
	
	public void setEfwd(HIEfwdDTO efwdDto) {
		this.efwdDto = efwdDto;
	}

	@Override
	public int hashCode() {
		return Objects.hash(efwdId, id, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlainConnDTO other = (PlainConnDTO) obj;
		return efwdId == other.efwdId && id == other.id && Objects.equals(type, other.type);
	}

	@Override
	public String toString() {
		return "PlainConnDTO [id=" + id + ", efwdId=" + efwdId + ", driver=" + driver + ", url=" + url + ", userName="
				+ userName + ", pass=" + pass + ", condition=" + condition + ", database=" + database + ", name=" + name
				+ ", directory=" + directory + ", type=" + type + "]";
	}

}
