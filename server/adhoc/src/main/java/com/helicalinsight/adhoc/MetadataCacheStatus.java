package com.helicalinsight.adhoc;

import java.util.Date;

public class MetadataCacheStatus {
	private Boolean cached;
	private Integer metadataId;
	private Date lastUpdateTime; 

	public MetadataCacheStatus(){
		
	}
	
	public MetadataCacheStatus(Boolean cached,Date lastUpdateTime,Integer id){
		this.cached=cached;
		this.lastUpdateTime=lastUpdateTime;
		this.metadataId=id;
	}
	
	public Boolean getCached() {
		return cached;
	}
	public void setCached(Boolean cached) {
		this.cached = cached;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	public Integer getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(Integer metadataId) {
		this.metadataId = metadataId;
	}
	
}
