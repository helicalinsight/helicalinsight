package com.helicalinsight.stream;

import com.helicalinsight.admin.utils.JacksonUtility;

public class SessionProgressEventSink implements ProgressEventSink {

	private final StreamSession session;

	public SessionProgressEventSink(StreamSession session) {
		this.session = session;
	}

	@Override
	public void emit(ProgressEvent event) {
		if (session == null || event == null) {
			return;
		}
		String data = JacksonUtility.mapToJson(event).toString();
		session.send(new StreamEvent(event.getChannel(), data));
	}

	@Override
	public void complete(Object result) {
		if (session == null) {
			return;
		}
		if (result != null) {
			String data = result instanceof String s ? s : JacksonUtility.mapToJson(result).toString();
			session.send(new StreamEvent("ready", data));
		}
		session.complete();
	}

	@Override
	public void abort(String message) {
		if (session == null) {
			return;
		}
		session.error(new RuntimeException(message != null ? message : "aborted"));
	}
}
