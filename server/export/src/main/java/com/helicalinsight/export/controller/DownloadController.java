package com.helicalinsight.export.controller;

import java.io.IOException;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.HttpRangeFileStreamer;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.export.ticket.TransferTicket;
import com.helicalinsight.export.ticket.TransferTicketService;
import com.helicalinsight.export.exception.InvalidTicketException;
import com.helicalinsight.export.exception.TicketInProgressException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class DownloadController {

	@Autowired
	private TransferTicketService transferTicketService;

	/**
	 * SSE: push READY / ABORTED when prepare finishes — replaces HEAD polling.
	 * Mapped outside /fileDownload so it never collides with the Range download handler.
	 */
	@RequestMapping(value = "/downloadEvents", method = RequestMethod.GET,
			produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter events(@RequestParam("ticket") String ticketId, @RequestParam(value="requestId", required=false) String requestId,   HttpServletResponse response) {
		if (!JsonUtils.isStreamResponseEnabled()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SSE disabled (streamResponse=false)");
		}
		String loggedInUserId = AuthenticationUtils.getUserId();
		try {
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("X-Accel-Buffering", "no");
			return transferTicketService.openEvents(ticketId, loggedInUserId);
		} catch (SecurityException e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
		} catch (InvalidTicketException e) {
			SseEmitter emitter = new SseEmitter(0L);
			try {
				Map<String, Object> data = new HashMap<>();
				data.put("status", "ABORTED");
				data.put("message", e.getMessage());
				emitter.send(SseEmitter.event().name("aborted").data(data, MediaType.APPLICATION_JSON));
				emitter.complete();
			} catch (Exception ignore) {
				emitter.completeWithError(e);
			}
			return emitter;
		}
	}

	@RequestMapping(value = "/fileDownload", method = { RequestMethod.HEAD, RequestMethod.GET })
	public void download(@RequestParam("ticket") String ticketId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String loggedInUserId = AuthenticationUtils.getUserId();

		TransferTicket ticket;

		try {
			ticket = transferTicketService.resolveReady(ticketId, loggedInUserId);
		} catch (SecurityException e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		} catch (TicketInProgressException ex) {
			// Not ready yet — 202 so clients/proxies do not treat as permanently gone
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			response.setHeader("Cache-Control", "no-store");
			return;
		} catch (InvalidTicketException ex) {
			response.sendError(HttpServletResponse.SC_GONE);
			return;
		} catch (IllegalArgumentException e) {
			response.sendError(HttpServletResponse.SC_GONE);
			return;
		}

		String disposition = "attachment; filename=\"" + ticket.fileName + "\"";
		if ("HEAD".equalsIgnoreCase(request.getMethod())) {
			long length = ticket.finalFile.toFile().length();
			response.setStatus(HttpServletResponse.SC_OK);
			applyTicketHeaders(response, ticket, disposition, length, false);
			return;
		}
		applyTicketHeaders(response, ticket, disposition, -1, true);
		HttpRangeFileStreamer.stream(
				ticket.finalFile.toFile(),
				request,
				response,
				ticket.contentType,
				disposition);
	}

	private static void applyTicketHeaders(HttpServletResponse response, TransferTicket ticket,
			String disposition, long length, boolean includeContentRange) {
		response.setHeader("Accept-Ranges", "bytes");
		response.setContentType(ticket.contentType);
		if (length >= 0) {
			response.setContentLengthLong(length);
		}
		response.setHeader("Content-Disposition", disposition);
		if (ticket.checksumSha256 != null) {
			response.setHeader(TransferTicketService.CHECKSUM_HEADER, ticket.checksumSha256);
		}
		String expose = "Content-Length, Content-Disposition, Accept-Ranges, "
				+ TransferTicketService.CHECKSUM_HEADER;
		if (includeContentRange) {
			expose += ", Content-Range";
		}
		response.setHeader("Access-Control-Expose-Headers", expose);
	}
}
