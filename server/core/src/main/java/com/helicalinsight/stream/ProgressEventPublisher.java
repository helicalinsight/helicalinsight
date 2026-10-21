package com.helicalinsight.stream;

import com.helicalinsight.admin.dto.Notification;

public class ProgressEventPublisher {

	private final Notification notification;
	private final ProgressEventSink sink;

	public ProgressEventPublisher(Notification notification, ProgressEventSink sink) {
		this.notification = notification != null ? notification : new Notification();
		this.sink = sink;
	}

	public Notification getNotification() {
		return notification;
	}

	public void log(String path, String message) {
		ProgressEvent event = ProgressEvent.of(ProgressEvent.CHANNEL_LOG, path, message);
		notification.addLog(event);
		emit(event);
	}

	public void warning(String path, String message) {
		notification.addWarning(path, message);
		emit(ProgressEvent.of(ProgressEvent.CHANNEL_WARNING, path, message));
	}

	public void error(String path, String message) {
		notification.addError(path, message);
		emit(ProgressEvent.of(ProgressEvent.CHANNEL_ERROR, path, message));
	}

	public void info(String path, String message) {
		notification.addInfo(path, message);
		emit(ProgressEvent.of(ProgressEvent.CHANNEL_INFO, path, message));
	}

	public void complete(Object result) {
		if (sink != null) {
			sink.complete(result);
		}
	}

	public void abort(String message) {
		if (sink != null) {
			sink.abort(message);
		}
	}

	private void emit(ProgressEvent event) {
		if (sink != null) {
			sink.emit(event);
		}
	}
}
