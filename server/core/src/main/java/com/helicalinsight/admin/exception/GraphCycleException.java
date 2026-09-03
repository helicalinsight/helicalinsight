package com.helicalinsight.admin.exception;

public class GraphCycleException extends RuntimeException {
    
	private static final long serialVersionUID = 1L;
	
	public GraphCycleException(String message) {
        super(message);
    }
}