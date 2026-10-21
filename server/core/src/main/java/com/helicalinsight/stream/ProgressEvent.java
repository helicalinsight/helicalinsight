package com.helicalinsight.stream;

import java.io.Serializable;

public final class ProgressEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String CHANNEL_LOG = "log";
	public static final String CHANNEL_WARNING = "warning";
	public static final String CHANNEL_ERROR = "error";
	public static final String CHANNEL_INFO = "info";

	private final String channel;
	private final String path;
	private final String message;
	private final long ts;

	public ProgressEvent(String channel, String path, String message, long ts) {
		this.channel = channel;
		this.path = path;
		this.message = message;
		this.ts = ts;
	}

	public static ProgressEvent of(String channel, String path, String message) {
		return new ProgressEvent(channel, path, message, System.currentTimeMillis());
	}

	public String getChannel() {
		return channel;
	}

	public String getPath() {
		return path;
	}

	public String getMessage() {
		return message;
	}

	public long getTs() {
		return ts;
	}
}
