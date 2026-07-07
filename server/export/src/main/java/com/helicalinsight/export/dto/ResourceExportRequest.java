package com.helicalinsight.export.dto;


import com.helicalinsight.core.request.PayLoad;

public class ResourceExportRequest  implements PayLoad{
	
	private String dir;
	private String file;
	private ResourceOptions options;
	public String getDir() {
		return dir;
	}
	public String getFile() {
		return file;
	}
	public ResourceOptions getOptions() {
		return options;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public void setOptions(ResourceOptions options) {
		this.options = options;
	}

	
	
}
