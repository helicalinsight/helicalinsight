package com.helicalinsight.core.datasource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileWriter;
import java.util.NoSuchElementException;

import javax.sql.rowset.CachedRowSet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.helicalinsight.datasource.ChunkIterator;
import com.helicalinsight.datasource.ChunkReader;

public class ChunkIteratorTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@SuppressWarnings("unchecked")
	@Test
	public void testHasNext_WhenChunkExists() throws Exception {

		File dir = folder.newFolder();

		File chunk = new File(dir, "chunk_0.cache");

		chunk.createNewFile();

		ChunkReader<String> reader = mock(ChunkReader.class);

		ChunkIterator<String> iterator = new ChunkIterator<String>(dir, reader);

		assertTrue(iterator.hasNext());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testHasNext_WhenCompleteFileExists() throws Exception {

		File dir = folder.newFolder();

		File complete = new File(dir, ".cache_complete");

		complete.createNewFile();

		ChunkReader<String> reader = mock(ChunkReader.class);

		ChunkIterator<String> iterator = new ChunkIterator<String>(dir, reader);

		assertFalse(iterator.hasNext());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNext_WhenChunkExists() throws Exception {

		File dir = folder.newFolder();

		File chunk = new File(dir, "chunk_0.cache");

		try (FileWriter fw = new FileWriter(chunk)) {
			fw.write("data");
		}

		ChunkReader<String> reader = mock(ChunkReader.class);

		when(reader.read(chunk)).thenReturn("SUCCESS");

		ChunkIterator<String> iterator = new ChunkIterator<String>(dir, reader);

		String result = iterator.next();

		assertEquals("SUCCESS", result);

		verify(reader).read(chunk);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNext_WhenReaderThrowsException() throws Exception {

		File dir = folder.newFolder();

		File chunk = new File(dir, "chunk_0.cache");

		try (FileWriter fw = new FileWriter(chunk)) {
			fw.write("data");
		}

		ChunkReader<String> reader = mock(ChunkReader.class);

		when(reader.read(chunk)).thenThrow(new RuntimeException("read error"));

		ChunkIterator<String> iterator = new ChunkIterator<String>(dir, reader);

		try {

			iterator.next();

			fail();

		} catch (RuntimeException e) {

			assertTrue(e.getMessage().contains("Failed reading chunk"));
		}
	}

	@SuppressWarnings("unchecked")
	@Test(expected = NoSuchElementException.class)
	
	public void testNext_WhenCompleteFileExists_ReturnsEmptyRowSet() throws Exception {

		File dir = folder.newFolder();

		File complete = new File(dir, ".cache_complete");

		complete.createNewFile();

		ChunkReader<Object> reader = mock(ChunkReader.class);

		ChunkIterator<Object> iterator = new ChunkIterator<Object>(dir, reader);

		Object result = iterator.next();

		assertNotNull(result);

		assertTrue(result instanceof CachedRowSet);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = NoSuchElementException.class)
	public void testNext_WhenNoMoreElements() throws Exception {

		File dir = folder.newFolder();

		File complete = new File(dir, ".cache_complete");

		complete.createNewFile();

		ChunkReader<String> reader = mock(ChunkReader.class);

		ChunkIterator<String> iterator = new ChunkIterator<String>(dir, reader);

		iterator.hasNext();

		iterator.next();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSingleFileMode() throws Exception {

		File singleFile = folder.newFile("single.cache");

		try (FileWriter fw = new FileWriter(singleFile)) {
			fw.write("single-data");
		}

		ChunkReader<String> reader = mock(ChunkReader.class);

		when(reader.read(singleFile)).thenReturn("FILE_DATA");

		ChunkIterator<String> iterator = new ChunkIterator<String>(singleFile, reader);

		assertTrue(iterator.hasNext());

		String result = iterator.next();

		assertEquals("FILE_DATA", result);

		assertFalse(iterator.hasNext());

		verify(reader).read(singleFile);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_WithNullDirectory() {

		ChunkReader<String> reader = mock(ChunkReader.class);

		new ChunkIterator<String>(null, reader);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_WithInvalidRange() {

		File dir = new File("dummy");

		ChunkReader<String> reader = mock(ChunkReader.class);

		new ChunkIterator<String>(dir, reader, 5, 1);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidChunkFileName() throws Exception {

		File dir = folder.newFolder();

		File invalid = new File(dir, "abc.cache");

		invalid.createNewFile();

		ChunkReader<String> reader = mock(ChunkReader.class);

		new ChunkIterator<String>(null, reader);
	}
}