package com.helicalinsight.admin.utils;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.JsonUtils;

public class DialectSupport {

	private static boolean shouldConvert;
	private static volatile boolean initialized;

	private static Set<Class<? extends Dialect>> emptyStringAsNullDialects = Set.of();

	private DialectSupport() {

	}

	public static synchronized void initialize(SessionFactory sessionFactory) {

		if (initialized) {
			return;
		}

		Set<Class<? extends Dialect>> configured = new HashSet<>();

		JsonObject settingsJson = JsonUtils.newGetSettingsJson();
		
		JsonObject dialectObject = GsonUtility.optJsonObject(settingsJson, "Dialects");
		JsonArray configuredDialects = GsonUtility.optJsonArray(dialectObject, "Dialect");

		shouldConvert = false;

		if (configuredDialects != null && !configuredDialects.isEmpty()) {
			for (JsonElement element : configuredDialects) {
				String eachDialect = element.getAsString();
				Class<? extends Dialect> dialectClass = loadDialectClass(eachDialect);
				configured.add(dialectClass);
			}
		}

		emptyStringAsNullDialects = Set.copyOf(configured);
		
		Dialect dialect = sessionFactory
		        .unwrap(SessionFactoryImplementor.class)
		        .getJdbcServices()
		        .getDialect();
		
		shouldConvert = emptyStringAsNullDialects.stream()
				.anyMatch(clazz -> clazz.isAssignableFrom(dialect.getClass()));
		initialized = true;
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends Dialect> loadDialectClass(String className) {
		try {
			Class<?> clazz = Class.forName(className);

			if (!Dialect.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException(className + " is not a Hibernate Dialect");
			}

			return (Class<? extends Dialect>) clazz;
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Unknown Hibernate Dialect: " + className, e);
		}
	}

	public static boolean shouldConvert() {
		return shouldConvert;
	}
}
