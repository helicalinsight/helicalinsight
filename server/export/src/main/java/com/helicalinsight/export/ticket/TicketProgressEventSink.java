package com.helicalinsight.export.ticket;

import java.util.List;

import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.stream.BatchAwareProgressEventSink;
import com.helicalinsight.stream.BatchingProgressEventSink;
import com.helicalinsight.stream.ProgressEvent;
import com.helicalinsight.stream.ProgressEventSink;

public class TicketProgressEventSink implements BatchAwareProgressEventSink {

	private final TransferTicketService ticketService;
	private final String ticketId;
	private final String requestId;

	public TicketProgressEventSink(TransferTicketService ticketService, String ticketId, String requestId) {
		this.ticketService = ticketService;
		this.ticketId = ticketId;
		this.requestId = requestId;
	}

	/** Live milestone logs (analyze). */
	public static ProgressEventSink of(TransferTicketService ticketService, String ticketId, String requestId) {
		return new TicketProgressEventSink(ticketService, ticketId, requestId);
	}

	/** Batched by eventBatchSize (commit / streaming import). */
	public static ProgressEventSink batched(TransferTicketService ticketService, String ticketId, String requestId) {
		return new BatchingProgressEventSink(of(ticketService, ticketId, requestId));
	}

	@Override
	public void emit(ProgressEvent event) {
		ticketService.emitProgress(ticketId, event);
	}

	@Override
	public void emitBatch(List<ProgressEvent> events) {
		ticketService.emitProgressBatch(ticketId, events);
	}

	@Override
	public void complete(Object result) {
		String json;
		if (result == null) {
			json = "{}";
		} else if (result instanceof String s) {
			json = s;
		} else {
			json = JacksonUtility.mapToJson(result).toString();
		}
		ticketService.completeUpload(ticketId, json);
	}

	@Override
	public void abort(String message) {
		ticketService.abortByRequestId(requestId, message);
	}
}
