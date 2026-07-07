package com.helicalinsight.core.request;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

public class RequestContext {
	
	private static final ThreadLocal<String> requestIdHolder = new ThreadLocal<String>();
	
	
	public static void set(String requestId) {
		requestIdHolder.set(requestId);
	}
	
	public static String get() {
		String requestId =  requestIdHolder.get();
		if (StringUtils.isBlank(requestId)) {
			requestId = UUID.randomUUID().toString();
		}
		return requestId;
	}
	
	public static void clear() {
		requestIdHolder.remove();
	}

}
