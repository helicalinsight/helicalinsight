package com.helicalinsight.export.unit;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.ticket.TransferTicket;
import com.helicalinsight.export.ticket.TransferTicketService;
import com.helicalinsight.export.ticket.Status;
import com.helicalinsight.export.exception.InvalidTicketException;
import com.helicalinsight.stream.SseEmitterRegistry;

public class UploadTicketServiceLifecycleTest {

	@Test
	public void putChunksAssembleAndVerifyChecksum() throws Exception {
		Path base = Files.createTempDirectory("hi-upload-test-");
		try (MockedStatic<TempDirectoryCleaner> tempMock = Mockito.mockStatic(TempDirectoryCleaner.class)) {
			tempMock.when(TempDirectoryCleaner::getTempDirectory).thenReturn(base.toFile());

			TransferTicketService service = newService();
			byte[] payload = "hello-resumable-upload-world".getBytes(StandardCharsets.UTF_8);
			String checksum = sha256(payload);
			String requestId = "req-upload-1";
			String ticketId = TransferTicketService.computeUploadTicketId("user1", "a.txt", payload.length, checksum,
					requestId);

			TransferTicket ticket = service.beginUpload(ticketId, requestId, "user1", "a.txt", "text/plain",
					payload.length, checksum, 8, "/importFile", null, 60);
			Assert.assertEquals(Status.PREPARING, ticket.status);
			Assert.assertTrue(ticket.upload);

			int chunkSize = ticket.chunkSize;
			for (int i = 0; i < ticket.totalChunks; i++) {
				int start = i * chunkSize;
				int end = Math.min(start + chunkSize, payload.length);
				byte[] part = new byte[end - start];
				System.arraycopy(payload, start, part, 0, part.length);
				service.putChunk(ticketId, "user1", i, new ByteArrayInputStream(part), part.length);
			}

			byte[] first = new byte[Math.min(chunkSize, payload.length)];
			System.arraycopy(payload, 0, first, 0, first.length);
			service.putChunk(ticketId, "user1", 0, new ByteArrayInputStream(first), first.length);

			List<Integer> received = service.receivedChunkIndices(ticket);
			Assert.assertEquals(ticket.totalChunks, received.size());

			Path assembled = service.assembleAndVerify(ticketId, "user1");
			Assert.assertTrue(Files.exists(assembled));
			Assert.assertArrayEquals(payload, Files.readAllBytes(assembled));
			service.completeUpload(ticketId, "{\"status\":1}");
			Assert.assertEquals(Status.READY, ticket.status);

			Path tempDir = ticket.tempDir;
			service.abortByRequestId(requestId);
			Assert.assertFalse(Files.exists(tempDir));
		}
	}

	@Test
	public void checksumMismatchFails() throws Exception {
		Path base = Files.createTempDirectory("hi-upload-cs-");
		try (MockedStatic<TempDirectoryCleaner> tempMock = Mockito.mockStatic(TempDirectoryCleaner.class)) {
			tempMock.when(TempDirectoryCleaner::getTempDirectory).thenReturn(base.toFile());

			TransferTicketService service = newService();
			byte[] payload = "abc".getBytes(StandardCharsets.UTF_8);
			String requestId = "req-upload-bad-cs";
			String ticketId = TransferTicketService.computeUploadTicketId("user1", "b.txt", payload.length, "deadbeef",
					requestId);
			TransferTicket ticket = service.beginUpload(ticketId, requestId, "user1", "b.txt", "text/plain",
					payload.length, "deadbeef", 1024, "/importFile", null, 60);
			service.putChunk(ticketId, "user1", 0, new ByteArrayInputStream(payload), payload.length);
			try {
				service.assembleAndVerify(ticketId, "user1");
				Assert.fail("expected checksum mismatch");
			} catch (IllegalStateException e) {
				Assert.assertTrue(e.getMessage().toLowerCase().contains("checksum"));
			}
			Assert.assertEquals(1, ticket.totalChunks);
			service.abortByRequestId(requestId);
		}
	}

	@Test
	public void abortThenChunkFailsAndTempDeleted() throws Exception {
		Path base = Files.createTempDirectory("hi-upload-abort-");
		try (MockedStatic<TempDirectoryCleaner> tempMock = Mockito.mockStatic(TempDirectoryCleaner.class)) {
			tempMock.when(TempDirectoryCleaner::getTempDirectory).thenReturn(base.toFile());

			TransferTicketService service = newService();
			byte[] payload = "xy".getBytes(StandardCharsets.UTF_8);
			String checksum = sha256(payload);
			String requestId = "req-upload-abort";
			String ticketId = TransferTicketService.computeUploadTicketId("user1", "c.txt", payload.length, checksum,
					requestId);
			TransferTicket ticket = service.beginUpload(ticketId, requestId, "user1", "c.txt", "text/plain",
					payload.length, checksum, 1024, "/importFile", null, 60);
			Path tempDir = ticket.tempDir;
			Assert.assertTrue(Files.isDirectory(tempDir));

			service.abortByRequestId(requestId);
			Assert.assertFalse(Files.exists(tempDir));

			try {
				service.putChunk(ticketId, "user1", 0, new ByteArrayInputStream(payload), payload.length);
				Assert.fail("expected aborted");
			} catch (InvalidTicketException expected) {
				Assert.assertNotNull(expected.getMessage());
			}
		}
	}

	@Test
	public void resumeReportsReceivedChunksViaIdempotentBegin() throws Exception {
		Path base = Files.createTempDirectory("hi-upload-resume-");
		try (MockedStatic<TempDirectoryCleaner> tempMock = Mockito.mockStatic(TempDirectoryCleaner.class);
				MockedStatic<JsonUtils> jsonMock = Mockito.mockStatic(JsonUtils.class, Mockito.CALLS_REAL_METHODS)) {
			tempMock.when(TempDirectoryCleaner::getTempDirectory).thenReturn(base.toFile());
			jsonMock.when(JsonUtils::isStreamResponseEnabled).thenReturn(true);

			TransferTicketService service = newService();
			byte[] payload = "0123456789abcdef".getBytes(StandardCharsets.UTF_8);
			String checksum = sha256(payload);
			String requestId = "req-upload-resume";
			String ticketId = TransferTicketService.computeUploadTicketId("user1", "d.bin", payload.length, checksum,
					requestId);
			TransferTicket ticket = service.beginUpload(ticketId, requestId, "user1", "d.bin",
					"application/octet-stream", payload.length, checksum, 4, "/importFile", null, 60);

			byte[] part0 = new byte[4];
			System.arraycopy(payload, 0, part0, 0, 4);
			service.putChunk(ticketId, "user1", 0, new ByteArrayInputStream(part0), 4);

			TransferTicket resumed = service.beginUpload(ticketId, requestId, "user1", "d.bin",
					"application/octet-stream", payload.length, checksum, 4, "/importFile", null, 60);
			Assert.assertSame(ticket, resumed);
			List<Integer> received = service.receivedChunkIndices(resumed);
			Assert.assertEquals(1, received.size());
			Assert.assertEquals(Integer.valueOf(0), received.get(0));
			Assert.assertTrue(service.uploadSessionPayload(resumed).containsKey("eventsUrl"));

			service.abortByRequestId(requestId);
		}
	}

	private static TransferTicketService newService() throws Exception {
		TransferTicketService service = new TransferTicketService();
		SseEmitterRegistry registry = Mockito.mock(SseEmitterRegistry.class);
		java.lang.reflect.Field f = TransferTicketService.class.getDeclaredField("sseEmitterRegistry");
		f.setAccessible(true);
		f.set(service, registry);
		return service;
	}

	private static String sha256(byte[] data) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return HexFormat.of().formatHex(md.digest(data));
	}
}
