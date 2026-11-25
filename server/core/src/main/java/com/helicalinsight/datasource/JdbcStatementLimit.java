package com.helicalinsight.datasource;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.PropertiesFileReader;

public class JdbcStatementLimit {

	private static final Logger logger = LoggerFactory.getLogger(JdbcStatementLimit.class);

	private static final String FILE_NAME = "databaseLimits.properties";
	private static volatile Map<String, String> flags = Collections.emptyMap();
	private static volatile long loadedAt = Long.MIN_VALUE;

	private JdbcStatementLimit() {
	}

	public static boolean useStatementLimit(String driverClass) {
		return Boolean.parseBoolean(current().getOrDefault(driverClass, current().get("default")));
	}

	private static Map<String, String> current() {
		File file = propertiesFile();
		long modified = file.lastModified();
		if (modified == loadedAt) {
			return flags;
		}
		synchronized (JdbcStatementLimit.class) {
			modified = file.lastModified();
			if (modified == loadedAt) {
				return flags;
			}
			logger.debug("File : {}  has been modified, reloading from the disk.");
			Map<String, String> fromFile = new PropertiesFileReader().read("Admin", FILE_NAME);
			flags = fromFile == null ? Collections.emptyMap() : Collections.unmodifiableMap(new HashMap<>(fromFile));
			loadedAt = modified;
			return flags;
		}
	}
	
	private static File propertiesFile() {
		logger.debug("Loading limit properties file : {}", FILE_NAME);
		return new File(ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin"
				+ File.separator + FILE_NAME);
	}
}
