package com.helicalinsight.adhoc;

public class MetadataProperties {
	
	private Integer metadataId;
	private String connectionType;
	private String fileName;
	private String dataBaseName;
	
	public String getDataBaseName() {
		return dataBaseName;
	}
	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}
	public Integer getMetadataId() {
		return metadataId;
	}
	public void setMetadataId(Integer metadataId) {
		this.metadataId = metadataId;
	}
	public String getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}