package com.helicalinsight.export.dto;

import java.io.Serializable;

public class ImportRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	
	private String onConflict;
	private Boolean upload;
	private ResourceOptions options;
	private String destination;
	private String key;

	public ResourceOptions getOptions() {
		return options;
	}

	public String getOnConflict() {
		return onConflict;
	}

	public void setOnConflict(String onConflict) {
		this.onConflict = onConflict;
	}

	public void setOptions(ResourceOptions options) {
		this.options = options;
	}

	public Boolean getUpload() {
		return upload;
	}

	public void setUpload(Boolean upload) {
		this.upload = upload;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}
