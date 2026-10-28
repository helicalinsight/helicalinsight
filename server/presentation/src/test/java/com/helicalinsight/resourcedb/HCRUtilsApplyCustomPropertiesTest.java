package com.helicalinsight.resourcedb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.HCRUtils;

import net.sf.jasperreports.engine.design.JRDesignTextField;

public class HCRUtilsApplyCustomPropertiesTest {

	private static final String BLANK = "net.sf.jasperreports.";

	private Map<String, String> strategies;
	private Map<String, String> originalStrategies;

	@Before
	public void setUp() throws Exception {
		Field field = HCRUtils.class.getDeclaredField("propertiesMapReplace");
		field.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<String, String> loaded = (Map<String, String>) field.get(null);
		strategies = loaded;
		originalStrategies = new HashMap<>(loaded);
		strategies.clear();
		strategies.put("replace.strategy.blank", BLANK);
	}

	@After
	public void tearDown() {
		strategies.clear();
		strategies.putAll(originalStrategies);
	}

	@Test
	public void nullArgumentsDoNothing() {
		JRDesignTextField field = new JRDesignTextField();
		JsonObject settings = new JsonObject();
		settings.addProperty("export.xls.remove.empty.space.between.rows", "true");

		HCRUtils.applyCustomProperties(null, settings);
		HCRUtils.applyCustomProperties(field, null);

		assertEquals(0, field.getPropertiesMap().getPropertyNames().length);
	}

	@Test
	public void appliesBlankPrefixWhenNoOtherStrategyMatches() {
		JRDesignTextField field = new JRDesignTextField();

		JsonObject settings = new JsonObject();
		settings.addProperty("export.xls.exclude.origin.keep.first.band.1", "pageHeader");
		settings.addProperty("export.xls.exclude.origin.band.1", "null");
		settings.addProperty("export.xls.exclude.origin.band.2", "pageFooter");
		settings.addProperty("export.xls.exclude.origin.keep.first.band.2", "columnHeader");
		settings.addProperty("export.xls.exclude.origin.keep.first.band.3", "columnHeader");
		settings.addProperty("export.xls.remove.empty.space.between.rows", "true");
		settings.addProperty("export.xls.remove.empty.space.between.columns", "true");
		settings.addProperty("export.xlsx.exclude.origin.keep.first.band.1", "pageHeader");
		settings.addProperty("export.xlsx.exclude.origin.band.1", "null");
		settings.addProperty("export.xlsx.exclude.origin.band.2", "pageFooter");
		settings.addProperty("export.xlsx.exclude.origin.keep.first.band.2", "columnHeader");
		settings.addProperty("export.xlsx.exclude.origin.keep.first.band.3", "columnHeader");
		settings.addProperty("export.xlsx.remove.empty.space.between.rows", "true");
		settings.addProperty("export.xlsx.remove.empty.space.between.columns", "true");

		HCRUtils.applyCustomProperties(field, settings);

		assertEquals(14, field.getPropertiesMap().getPropertyNames().length);
		for (String key : settings.keySet()) {
			assertNull(field.getPropertiesMap().getProperty(key));
			assertEquals(settings.get(key).getAsString(), field.getPropertiesMap().getProperty(BLANK + key));
		}
	}

	@Test
	public void leavesNameUnchangedWhenItAlreadyHasBlankPrefix() {
		JRDesignTextField field = new JRDesignTextField();
		JsonObject settings = new JsonObject();
		settings.addProperty("net.sf.jasperreports.print.keep.full.text", "true");

		HCRUtils.applyCustomProperties(field, settings);

		assertEquals("true", field.getPropertiesMap().getProperty("net.sf.jasperreports.print.keep.full.text"));
		assertEquals(1, field.getPropertiesMap().getPropertyNames().length);
	}

	@Test
	public void matchingStrategyPrefixWinsOverBlankPrefix() {
		strategies.put("replace.strategy.export.xls.", "com.example.");

		JRDesignTextField field = new JRDesignTextField();
		JsonObject settings = new JsonObject();
		settings.addProperty("export.xls.remove.empty.space.between.rows", "true");
		settings.addProperty("export.xlsx.remove.empty.space.between.rows", "true");

		HCRUtils.applyCustomProperties(field, settings);

		assertEquals("true",
				field.getPropertiesMap().getProperty("com.example.export.xls.remove.empty.space.between.rows"));
		assertEquals("true", field.getPropertiesMap()
				.getProperty("net.sf.jasperreports.export.xlsx.remove.empty.space.between.rows"));
	}
}