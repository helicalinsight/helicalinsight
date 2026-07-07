package com.helicalinsight.efw.exceptions;

public class InconsistentStateException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public InconsistentStateException(String msg) {
		super(msg);
	}
}
