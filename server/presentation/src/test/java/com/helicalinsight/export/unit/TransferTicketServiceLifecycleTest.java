package com.helicalinsight.export.unit;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.helicalinsight.export.ticket.TransferTicketService;
import com.helicalinsight.stream.SseEmitterRegistry;

public class TransferTicketServiceLifecycleTest {

	@Test
	public void abortThenLateComplete_deletesArtifactWithoutThrowing() throws Exception {
		TransferTicketService service = new TransferTicketService();
		SseEmitterRegistry registry = Mockito.mock(SseEmitterRegistry.class);
		setField(service, "sseEmitterRegistry", registry);

		Path temp = Files.createTempFile("hi-dl-abort-", ".bin");
		Files.write(temp, new byte[] { 1, 2, 3 });

		String requestId = "req-abort-1";
		String ticketId = TransferTicketService.computeTicketId(
				"user1", "dir", "file", "pdf", "", "", "", "", requestId);
		service.begin(ticketId, requestId, "user1", 60);

		service.abortByRequestId(requestId);

		// Late complete from producer must not throw; should clean file
		service.complete(ticketId, temp, "out.bin", "application/octet-stream");
		Assert.assertFalse(Files.exists(temp));
	}

	@Test
	public void trackAfterAbort_deletesPath() throws Exception {
		TransferTicketService service = new TransferTicketService();
		SseEmitterRegistry registry = Mockito.mock(SseEmitterRegistry.class);
		setField(service, "sseEmitterRegistry", registry);

		Path temp = Files.createTempFile("hi-dl-track-", ".bin");
		Files.write(temp, new byte[] { 9 });

		String requestId = "req-abort-2";
		String ticketId = TransferTicketService.computeTicketId(
				"user1", "d", "f", "csv", "", "", "", "", requestId);
		service.begin(ticketId, requestId, "user1", 60);
		service.abortByRequestId(requestId);
		service.track(ticketId, temp);
		Assert.assertFalse(Files.exists(temp));
	}

	private static void setField(Object target, String name, Object value) throws Exception {
		java.lang.reflect.Field f = target.getClass().getDeclaredField(name);
		f.setAccessible(true);
		f.set(target, value);
	}
}
