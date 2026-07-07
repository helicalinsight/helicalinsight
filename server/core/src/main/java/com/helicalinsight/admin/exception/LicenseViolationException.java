package com.helicalinsight.admin.exception;


public class LicenseViolationException extends RuntimeException  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	public LicenseViolationException(String message) {
		super(message);
	}
	
	public LicenseViolationException(Throwable throwable) {
		super(throwable);
	}
	
	public LicenseViolationException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
