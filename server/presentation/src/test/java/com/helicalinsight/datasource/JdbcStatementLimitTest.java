package com.helicalinsight.datasource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.helicalinsight.efw.ApplicationProperties;

public class JdbcStatementLimitTest {

	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

	private String savedSystemDirectory;
	private File systemDirectory;
	private File propertiesFile;

	@Before
	public void setUp() throws Exception {
		savedSystemDirectory = ApplicationProperties.getInstance().getSystemDirectory();
		systemDirectory = Files.createTempDirectory("jdbc-statement-limit").toFile();
		ApplicationProperties.getInstance().setSystemDirectory(systemDirectory.getAbsolutePath());
		new File(systemDirectory, "Admin").mkdirs();
		propertiesFile = new File(systemDirectory, "Admin/databaseLimits.properties");
		PreviewLimitTest.resetStatementLimitCache();
	}

	@After
	public void tearDown() throws Exception {
		ApplicationProperties.getInstance().setSystemDirectory(savedSystemDirectory);
		PreviewLimitTest.resetStatementLimitCache();
		delete(systemDirectory);
	}

	@Test
	public void missingFileUsesSqlWrap() {
		assertFalse(JdbcStatementLimit.useStatementLimit(DRIVER));
	}

	@Test
	public void fileCreatedLaterIsPickedUpWithoutRestart() throws Exception {
		assertFalse(JdbcStatementLimit.useStatementLimit(DRIVER));
		write("true", 2_000L);
		assertTrue(JdbcStatementLimit.useStatementLimit(DRIVER));
	}

	@Test
	public void unchangedTimestampKeepsThePreviousDecision() throws Exception {
		write("true", 5_000L);
		assertTrue(JdbcStatementLimit.useStatementLimit(DRIVER));

		write("false", 5_000L);
		assertTrue(JdbcStatementLimit.useStatementLimit(DRIVER));

		write("false", 9_000L);
		assertFalse(JdbcStatementLimit.useStatementLimit(DRIVER));
	}

	private void write(String value, long modifiedAt) throws Exception {
		Properties properties = new Properties();
		properties.setProperty(DRIVER, value);
		properties.setProperty("default", "false");
		try (FileOutputStream output = new FileOutputStream(propertiesFile)) {
			properties.store(output, null);
		}
		propertiesFile.setLastModified(modifiedAt);
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
