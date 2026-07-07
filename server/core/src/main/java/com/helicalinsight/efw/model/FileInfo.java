package com.helicalinsight.efw.model;

import java.io.Serializable;

import com.google.gson.JsonObject;

public class FileInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String title;
	private String type;
	private String path;
	private String extension;
	private String permissionLevel;
	private JsonObject options;
	private Long lastModified;
	private  Integer resourceId;

	public Integer getResourceId() {
		return resourceId;
	}

	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}

	public FileInfo() {
		// No Args Constructor.
	}
	
	
	public String getName() {
		return name;
	}
	public String getTitle() {
		return title;
	}
	public String getType() {
		return type;
	}
	public String getPath() {
		return path;
	}
	public String getExtension() {
		return extension;
	}
	public String getPermissionLevel() {
		return permissionLevel;
	}
	public JsonObject getOptions() {
		return options;
	}
	public Long getLastModified() {
		return lastModified;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public void setPermissionLevel(String permissionLevel) {
		this.permissionLevel = permissionLevel;
	}
	public void setOptions(JsonObject options) {
		this.options = options;
	}
	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}
	
	

	@Override
	public String toString() {
		return "FileInfo [name=" + name + ", title=" + title + ", type=" + type + ", path=" + path + ", extension="
				+ extension + ", permissionLevel=" + permissionLevel + ", options=" + options + ", lastModified="
				+ lastModified + "]";
	}
	


}
