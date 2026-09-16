package com.helicalinsight.core.datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.helicalinsight.datasource.StreamingCacheMarkers;

public class StreamingCacheMarkersTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void writeAndReadErrorMessage() throws Exception {
		File dir = folder.newFolder();
		StreamingCacheMarkers.writeError(dir, new RuntimeException("column must appear in GROUP BY"));

		assertTrue(StreamingCacheMarkers.errorFile(dir).exists());
		assertFalse(StreamingCacheMarkers.completeFile(dir).exists());
		assertTrue(StreamingCacheMarkers.readErrorMessage(dir).contains("GROUP BY"));
	}

	@Test
	public void throwIfError_UsesFileContents() throws Exception {
		File dir = folder.newFolder();
		Files.writeString(StreamingCacheMarkers.errorFile(dir).toPath(),
				"Error: SQLException: invalid group by", StandardCharsets.UTF_8);

		try {
			StreamingCacheMarkers.throwIfError(dir);
			fail("Expected EfwServiceException");
		} catch (com.helicalinsight.efw.exceptions.EfwServiceException e) {
			assertEquals("Error: SQLException: invalid group by", e.getCause().getMessage());
		}
	}

	@Test
	public void throwIfError_NoOpWhenMissing() throws Exception {
		File dir = folder.newFolder();
		StreamingCacheMarkers.throwIfError(dir);
	}
}
