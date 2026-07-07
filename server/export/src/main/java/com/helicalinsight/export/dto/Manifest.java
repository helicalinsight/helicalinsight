package com.helicalinsight.export.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.helicalinsight.efw.ApplicationProperties;

public class Manifest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String version;
	private List<String> resourcePaths = new ArrayList<>();
	private Map<String, String> shares = new HashMap<>();
	private Map<String,String> schedules = new HashMap<>();
	private Map<String, String> dataSources = new HashMap<>();;
	private Map<String,List<String>> dependencies = new HashMap<>();
	private Map<String,List<String>> images = new HashMap<>();
	private ResourceOptions options;

	public Map<String, List<String>> getImages() {
		return images;
	}

	public void setImages(Map<String, List<String>> images) {
		this.images = images;
	}

	public List<String> getResourcePaths() {
		return resourcePaths;
	}


	public Map<String, String> getShares() {
		return shares;
	}

	
	public ResourceOptions getOptions() {
		return options;
	}

	public void setResourcePaths(List<String> resourcePaths) {
		this.resourcePaths = resourcePaths;
	}

	public void setShares(Map<String, String> shares) {
		this.shares = shares;
	}

	

	public Map<String, String> getSchedules() {
		return schedules;
	}


	public void setSchedules(Map<String, String> schedules) {
		this.schedules = schedules;
	}


	public void setOptions(ResourceOptions options) {
		this.options = options;
	}

	public Map<String, String> getDataSources() {
		return dataSources;
	}

	public void setDataSources(Map<String, String> dataSources) {
		this.dataSources = dataSources;
	}


	public Map<String, List<String>> getDependencies() {
		return dependencies;
	}


	public void setDependencies(Map<String, List<String>> dependencies) {
		this.dependencies = dependencies;
	}


	public String getVersion() {
		if(version == null) {
			return "0";
		}
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
}
