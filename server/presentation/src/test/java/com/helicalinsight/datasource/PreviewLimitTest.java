package com.helicalinsight.datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.helicalinsight.efw.ApplicationProperties;

public class PreviewLimitTest {

	private static final String MYSQL = "com.mysql.cj.jdbc.Driver";
	private static final String DERBY = "org.apache.derby.jdbc.AutoloadedDriver";

	private String savedSystemDirectory;
	private File systemDirectory;

	@Before
	public void setUp() throws Exception {
		savedSystemDirectory = ApplicationProperties.getInstance().getSystemDirectory();
		systemDirectory = Files.createTempDirectory("preview-limit").toFile();
		ApplicationProperties.getInstance().setSystemDirectory(systemDirectory.getAbsolutePath());
		resetStatementLimitCache();
		writeLimits("default=true", MYSQL + "=true", DERBY + "=false");
	}

	@After
	public void tearDown() throws Exception {
		ApplicationProperties.getInstance().setSystemDirectory(savedSystemDirectory);
		resetStatementLimitCache();
		delete(systemDirectory);
	}

	@Test
	public void statementDriverWithoutLimitUsesDefaultRows() {
		PreviewLimit limit = PreviewLimit.resolve(MYSQL, null, 10);
		assertEquals(PreviewLimit.Kind.STATEMENT, limit.kind());
		assertEquals(10, limit.rows());
	}

	@Test
	public void statementDriverUsesRequestedRows() {
		PreviewLimit limit = PreviewLimit.resolve(MYSQL, "25", 10);
		assertEquals(PreviewLimit.Kind.STATEMENT, limit.kind());
		assertEquals(25, limit.rows());
	}

	@Test
	public void wrapDriverUsesSqlWrap() {
		PreviewLimit limit = PreviewLimit.resolve(DERBY, null, 10);
		assertEquals(PreviewLimit.Kind.SQL_WRAP, limit.kind());
		assertEquals(10, limit.rows());
	}

	@Test
	public void missingDriverUsesDefault() {
		PreviewLimit limit = PreviewLimit.resolve("org.example.UnknownDriver", null, 10);
		assertEquals(PreviewLimit.Kind.STATEMENT, limit.kind());
		assertEquals(10, limit.rows());
	}

	@Test
	public void nullDriverUsesDefault() {
		PreviewLimit limit = PreviewLimit.resolve(null, null, 10);
		assertEquals(PreviewLimit.Kind.STATEMENT, limit.kind());
		assertEquals(10, limit.rows());
	}

	@Test
	public void missingDefaultAndMissingDriverWrapsSql() throws Exception {
		writeLimits(DERBY + "=false");
		PreviewLimit limit = PreviewLimit.resolve("org.example.UnknownDriver", null, 10);
		assertEquals(PreviewLimit.Kind.SQL_WRAP, limit.kind());
		assertEquals(10, limit.rows());
	}

	@Test
	public void fullLimitIsUnlimitedForEitherDriver() {
		PreviewLimit statement = PreviewLimit.resolve(MYSQL, "full", 10);
		PreviewLimit wrap = PreviewLimit.resolve(DERBY, "FULL", 10);
		assertEquals(PreviewLimit.Kind.UNLIMITED, statement.kind());
		assertEquals(0, statement.rows());
		assertEquals(PreviewLimit.Kind.UNLIMITED, wrap.kind());
		assertEquals(0, wrap.rows());
	}

	@Test
	public void nonNumericLimitThrows() {
		try {
			PreviewLimit.resolve(MYSQL, "abc", 10);
			fail("Expected NumberFormatException");
		} catch (NumberFormatException expected) {
			assertEquals("For input string: \"abc\"", expected.getMessage());
		}
	}

	private void writeLimits(String... lines) throws Exception {
		File admin = new File(systemDirectory, "Admin");
		admin.mkdirs();
		File file = new File(admin, "databaseLimits.properties");
		Properties properties = new Properties();
		for (String line : lines) {
			int split = line.indexOf('=');
			properties.setProperty(line.substring(0, split), line.substring(split + 1));
		}
		try (FileOutputStream output = new FileOutputStream(file)) {
			properties.store(output, null);
		}
		file.setLastModified(file.lastModified() + 1000L);
		resetStatementLimitCache();
	}

	static void resetStatementLimitCache() throws Exception {
		Field flags = JdbcStatementLimit.class.getDeclaredField("flags");
		flags.setAccessible(true);
		flags.set(null, Collections.emptyMap());
		Field loadedAt = JdbcStatementLimit.class.getDeclaredField("loadedAt");
		loadedAt.setAccessible(true);
		loadedAt.setLong(null, Long.MIN_VALUE);
	}

	private static void delete(File file) {
		if (file == null || !file.exists()) {
			return;
		}
		File[] children = file.listFiles();
		if (children != null) {
			for (File child : children) {
				delete(child);
			}
		}
		file.delete();
	}
}
