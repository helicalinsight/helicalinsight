package com.helicalinsight.export.dto;

public enum ResourceExtension {

	FOLDER(".efwfolder"), 
	METADATA(".metadata"), 
	HREPORT(".hr"), 
	EFWDD(".efwdd"), 
	EFW(".efw"),
	ZIP(".zip");
	

	private final String value;

	public String getValue() {
		return value;
	}

	private ResourceExtension(final String value) {
		this.value = value;
	}

}
