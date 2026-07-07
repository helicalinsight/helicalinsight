package com.helicalinsight.stream;

public enum EventType {

	BEGIN("begin"), 
	ERROR("error"), 
	COMPLETED("complete");

	private final String value;

	EventType(String value) {
		this.value = value;
	}
	
	public String value() {
		return value;
	}

}
