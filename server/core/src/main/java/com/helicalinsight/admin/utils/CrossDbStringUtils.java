package com.helicalinsight.admin.utils;

import org.apache.commons.lang3.StringUtils;

import com.helicalinsight.efw.ApplicationProperties;

public final class CrossDbStringUtils {

	private CrossDbStringUtils() {
	}

	public static String toDatabaseValue(String value) {
		
		if (!DialectSupport.shouldConvert()) {
			return value;
		}
		
		if (value == null) {
			return null;
		}
		if (StringUtils.isBlank(value)) {
			return blankMarker();
		}
		return value;
	}

	public static String toEntityValue(String value) {
		if (!DialectSupport.shouldConvert()) {
			return value;
		}
		if (value == null) {
			return null;
		}
		if (blankMarker().equals(value)) {
			return "";
		}
		return value;
	}

	private static String blankMarker() {
		return ApplicationProperties.getInstance().getBlankValue();
	}
}