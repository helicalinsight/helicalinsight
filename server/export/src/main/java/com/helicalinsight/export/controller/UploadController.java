package com.helicalinsight.export.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.Gson;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.export.exception.InvalidTicketException;
import com.helicalinsight.export.ticket.TransferTicket;
import com.helicalinsight.export.ticket.TransferTicketService;
import com.helicalinsight.export.upload.ResumableUploadService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Byte transfer for resumable upload. Session prepare uses existing
 * {@code /importFile} / {@code /importResource} with {@code resumable=true};
 * finalize runs automatically when the last chunk arrives (SSE via {@code /downloadEvents}).
 */
@RestController
public class UploadController {

	private static final Gson GSON = new Gson();

	@Autowired
	private ResumableUploadService resumableUploadService;

	@Autowired
	private TransferTicketService ticketService;

	@RequestMapping(value = "/uploadChunk", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void chunk(@RequestParam("ticket") String ticketId, @RequestParam("index") int index,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean isAjax = ControllerUtils.isAjax(request);
		try {
			String userId = AuthenticationUtils.getUserId();
			long contentLength = request.getContentLengthLong();
			boolean allReceived;
			try (InputStream in = request.getInputStream()) {
				allReceived = ticketService.putChunk(ticketId, userId, index, in, contentLength);
			}
			if (allReceived) {
				resumableUploadService.completeAsyncIfNeeded(ticketId, userId);
			}
			TransferTicket ticket = ticketService.requireOwned(ticketId, userId);
			Map<String, Object> body = new HashMap<>();
			body.put("ticket", ticketId);
			body.put("index", index);
			body.put("received", true);
			body.put("receivedChunks", ticketService.receivedChunkIndices(ticket));
			body.put("complete", allReceived);
			body.put("eventsUrl", TransferTicketService.eventsUrl(ticketId, ticket.requestId));
			writeJson(response, isAjax, GSON.toJson(body));
		} catch (SecurityException e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
		} catch (InvalidTicketException e) {
			response.sendError(HttpServletResponse.SC_GONE, e.getMessage());
		} catch (IllegalArgumentException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			ControllerUtils.handleFailure(response, isAjax, e);
		}
	}

	private static void writeJson(HttpServletResponse response, boolean isAjax, String json) throws IOException {
		ControllerUtils.handleSuccess(response, isAjax, json);
	}
}
