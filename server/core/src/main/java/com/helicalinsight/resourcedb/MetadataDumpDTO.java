package com.helicalinsight.resourcedb;

import java.util.Date;

public class MetadataDumpDTO {
	
	private Long id;
	private String status;
	private String path;
	private String title;
	private String name;
	private String dumpType;
	private Date lastUpdatedTime;
	
	
	public MetadataDumpDTO() {
		
	}
	
	public MetadataDumpDTO(String title , String path) {
		this.title = title;
		this.path = path;
	}

	public Long getId() {
		return id;
	}


	public String getStatus() {
		return status;
	}


	public String getPath() {
		return path;
	}


	public String getTitle() {
		return title;
	}


	public String getName() {
		return name;
	}


	public Date getLastUpdatedTime() {
		return lastUpdatedTime;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setLastUpdatedTime(Date lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}
	

	public String getDumpType() {
		return dumpType;
	}

	public void setDumpType(String dumpType) {
		this.dumpType = dumpType;
	}

	@Override
	public String toString() {
		return "MetadataDumpDTO [id=" + id + ", status=" + status + ", path=" + path + ", title=" + title + ", name="
				+ name + ", lastUpdatedTime=" + lastUpdatedTime + "]";
	}
	
	
	
}
