package com.helicalinsight.adhoc.copypaste;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.dto.HIEfwdDTO;

public abstract class HiResourceCopyHandler {
	
	private String urlPrefix;
	private String sourcePath;	
	private HIResource destResource;
	private HIResource resource;
	private HIEfwdDTO hiefwdDto;
	private Boolean onConflictSkip;

	public String getPrefix() {
		return urlPrefix;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public HIResource getSource() {
		return resource;
	}
	
	public Boolean getOnConflictSkip() {
		return onConflictSkip;
	}

	public HIResource getDestinationResourceId() {
		return destResource;
	}
	
	public void setEfwdDto(HIEfwdDTO hiefwdDto) {
		this.hiefwdDto = hiefwdDto;
	}
	
	public HIEfwdDTO getEfwdDto() {
		return hiefwdDto;
	}
	
	public void setHiResource(HIResource resource) {
		this.resource=resource;
	}
	
	public void setData(String prefix,String sourcePath,HIResource resource,HIResource destinationResource,Boolean onConflictUpdate) {
		this.urlPrefix=prefix;
		this.sourcePath=sourcePath;
		this.resource=resource;
		this.destResource=destinationResource;
		this.onConflictSkip=onConflictUpdate;
	}
	
	public abstract void copyResource();
}
