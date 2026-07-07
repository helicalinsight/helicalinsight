package com.helicalinsight.core.datasource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.helicalinsight.datasource.RowCacheSetChunkReader;
import com.helicalinsight.efw.exceptions.EfwServiceException;

public class RowCacheSetChunkReaderTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testRead_Success() throws Exception {

		File file = folder.newFile("chunk.cache");

		CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {

			oos.writeObject(rowSet);
		}

		RowCacheSetChunkReader reader = new RowCacheSetChunkReader();

		ResultSet result = reader.read(file);

		assertNotNull(result);

		assertTrue(result instanceof CachedRowSet);
	}

	@Test
	public void testRead_WhenFileDoesNotExist() {

		File missingFile = new File("invalid.cache");

		RowCacheSetChunkReader reader = new RowCacheSetChunkReader();

		try {

			reader.read(missingFile);

			fail();

		} catch (EfwServiceException e) {

			assertTrue(e.getMessage().contains("Failed to read chunk"));
		}
	}

	@Test
	public void testRead_WhenInvalidObjectInFile() throws Exception {

		File invalidFile = folder.newFile("invalid.cache");

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(invalidFile))) {

			oos.writeObject("INVALID_OBJECT");
		}

		RowCacheSetChunkReader reader = new RowCacheSetChunkReader();

		try {

			reader.read(invalidFile);

			fail();

		} catch (EfwServiceException e) {

			assertTrue(e.getMessage().contains("Failed to read chunk"));
		}
	}

	@Test
	public void testConstructor() {

		RowCacheSetChunkReader reader = new RowCacheSetChunkReader();

		assertNotNull(reader);
	}
}